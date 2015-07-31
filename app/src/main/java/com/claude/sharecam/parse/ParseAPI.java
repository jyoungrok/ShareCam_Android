package com.claude.sharecam.parse;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.claude.sharecam.Constants;
import com.claude.sharecam.Util;
import com.claude.sharecam.api.ErrorCode;
import com.claude.sharecam.orm.UploadingPicture;
import com.claude.sharecam.upload.UploadService;
import com.claude.sharecam.share.IndividualItem;
import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Claude on 15. 5. 31..
 */
public class ParseAPI {

    public static final String TAG="ParseAPI";
    //pin label name
    public static final String LABEL_CONTACT="contact";
    public static final String LABEL_GROUP="group";
    public static final String LABEL_FRIEND="friend";
    public static final String LABEL_SHARECONTACT="shareContact";


    public static final String SM_PHONE_VERIFY = "sm_phone_verify";
    public static final String SM_PHONE_CONFIRM = "sm_phone_confirm";
    public static final String SYNC_ALL_CONTACT ="sync_all_contact";
    public static final String DELETE_CONTACT="delete_contact";
    public static final String SIGN_UP_COMPLETED="sign_up_completed";
    //    public static final String SYNC_FRIEND_LIST="sync_friend_list";
    public static final String SYNC_DATA="sync_data";

    //parameter
    public static final int RETURN_ALL_FRIENDS_TYPE=0;
    public static final int RETURN_ADDED_FRIENDS_TYPE=1;

    /**
     * 회원 가입 시
     */

    //회원가입시 혹은 로그인시 초기화 진행
    //1. 연락처 서버에 올리기
    //2. 연락처로 친구 목록 동기화
    //3. 로컬로 친구들 불러오기

    public static void initialize(ParseUser user, final Context context, final Handler handler)
    {
        Log.d(TAG,"initialize");
        //연락처 초기화
        //모든 연락처 데이터 지우고 새로 업로드
        initContact(user, context, new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("type", String.valueOf(RETURN_ALL_FRIENDS_TYPE));

                //서버에서 연락처로 친구목록 동기화
                ParseCloud.callFunctionInBackground(ParseAPI.SYNC_ALL_CONTACT, params, new FunctionCallback<List<Friend>>() {
                    public void done(List<Friend> result, ParseException e) {
                        if(e==null) {
                            Log.d(TAG, "syncAllContact Done");
                            //존재하는 친구들 불러와서 local에 저장
                            initFriends(context, new Handler() {
                                @Override
                                public void handleMessage(Message msg) {
                                    super.handleMessage(msg);
                                    handler.sendEmptyMessage(0);
                                }
                            });
                        }
                        else
                        {
                            Log.d(TAG, "syncAllContact error");
                            ParseAPI.erroHandling(context,e);
                        }
                    }
                });
            }

        });
    }


    // 연락처를 모두 삭제 후 새로 업로드
    // 업로드한 연락처들 pinning
    public static void initContact(ParseUser user, final Context context, final Handler handler) {

        Log.d(TAG,"initContact");
        //read contact list from content prover
        final ArrayList<IndividualItem> arItem = Util.getContactList(context);


        //서버의 모든 연락처 데이터 삭제
        ParseCloud.callFunctionInBackground(ParseAPI.DELETE_CONTACT, new HashMap<String, Object>(), new FunctionCallback<JSONObject>() {
            public void done(JSONObject result, ParseException e) {

                Log.d(TAG,"deleteContact done");

                if (e == null) {
                    final List<Contact> contactList = new ArrayList<Contact>();

                    for (int i = 0; i < arItem.size(); i++) {
                        Contact contact = new Contact();
                        contact.setCreatedBy(ParseUser.getCurrentUser());
                        contact.setPhone(Util.convertToInternationalNumber(context, arItem.get(i).phoneNumber));
                        contact.setACL(ParseUser.getCurrentUser());
                        contactList.add(contact);
                    }


                    //기존에 저장되어 있던 로컬의 모든 연락처 데이터 삭제
                    ParseObject.unpinAllInBackground(LABEL_CONTACT, new SCDeleteCallback(context, new SCDeleteCallback.Callback() {
                        @Override
                        public void done() {
                            Log.d(TAG, "unpinAllContact done");
                            //모든 연락처 데이터 local 저장


                            //모든 연락처 데이터 서버에 저장
                            ParseObject.saveAllInBackground(contactList, new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            Log.d(TAG, "save all contact done");
                                            if (e == null) {
                                                ParseObject.pinAllInBackground(LABEL_CONTACT, contactList, new SCSaveCallback(context, new SCSaveCallback.Callback() {

                                                    @Override
                                                    public void done() {
                                                        Log.d(TAG, "pinAllContact done");
                                                        Util.setContactSyncTime(context);
                                                        handler.sendEmptyMessage(0);
                                                    }
                                                }));
                                            } else
                                                ParseAPI.erroHandling(context, e);
                                        }
                                    }

                            );
//                            ParseObject.pinAllInBackground(LABEL_CONTACT, contactList, new SCSaveCallback(context, new SCSaveCallback.Callback() {
//                                @Override
//                                public void done() {
//                                    Log.d(TAG,"pinAllContact done");
//                                    //모든 연락처 데이터 서버에 저장
//                                    ParseObject.saveAllInBackground(contactList, new SaveCallback() {
//                                        @Override
//                                        public void done(ParseException e) {
//                                            Log.d(TAG,"save all contact done");
//                                            if (e == null)
//                                                handler.sendEmptyMessage(0);
//                                            else
//                                                ParseAPI.erroHandling(context, e);
//                                        }
//                                    });
//                                }
//                            }));
                        }
                    }));


                } else
                    ParseAPI.erroHandling(context, e);
            }
        });
    }

    //기존 친구들 local에서 모두 삭제
    //기존 parse server에 등록되어 있는 친구들 불러와 local store에 추가
    public static void initFriends(final Context context, final Handler handler)
    {
        Log.d(TAG,"initFriends");
        //기존 친구 데이터 모두 삭제
        ParseObject.unpinAllInBackground(LABEL_FRIEND, new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                Log.d(TAG, "unpin all friend done");
                if (e == null) {

                    //서버로 부터 친구들 불러옴
                    ParseQuery<Friend> query2 = ParseQuery.getQuery(Friend.class);
                    query2.whereEqualTo("createdBy", ParseUser.getCurrentUser());
                    query2.include("friendUser");
                    query2.findInBackground(new FindCallback<Friend>() {
                        @Override
                        public void done(final List<Friend> list, ParseException e) {
                            Log.d(TAG, "find all friend done");
                            if (e == null) {
                                //불러온 데이터 저장
                                ParseObject.pinAllInBackground(LABEL_FRIEND, list, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        Log.d(TAG, "pin all friend done");
                                        if (e == null) {
                                            handler.sendEmptyMessage(0);
                                            //친구목록 동기화 시간 설정
                                            setLastUpdatedAt(context,list);
//                                            Util.setFriendLastUpdatedAt(context, list);
                                        } else
                                            ParseAPI.erroHandling(context, e);
                                    }
                                });
                            } else
                                ParseAPI.erroHandling(context, e);
                        }
                    });
                } else {
                    Log.d(TAG, "getExistingFriends1 error");
                    ParseAPI.erroHandling(context, e);
                }

            }
        });
    }

    /**
     * 연락처 동기화
     */








    //개인에게 받은 사진 목록 받아오기
    public static void getPicturesByMe(int page,int num,FindCallback<Picture> findCallback)  {
        ParseQuery<Picture> query=ParseQuery.getQuery(Picture.class);
        query.whereEqualTo("friendList", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("hasPhoto",true);
        query.whereEqualTo("photoSynched",true);
        query.orderByDescending("createdAt");
        query.setSkip((page-1)*num);
        query.setLimit(num);
        query.findInBackground(findCallback);
    }



    //나의 그룹들 불러오기
//    public static void getGroupsByMe(int page, final Context context, final Handler handler)
//    {
//        ParseQuery<Group> queryByCreatedBy=ParseQuery.getQuery(Group.class);
//        queryByCreatedBy.whereEqualTo("createdBy",ParseUser.getCurrentUser());
//        ParseQuery<Group> queryByFriend=ParseQuery.getQuery(Group.class);
//        queryByFriend.whereEqualTo("friendList",ParseUser.getCurrentUser().getObjectId());
//        ParseQuery<Group> queryByPhone=ParseQuery.getQuery(Group.class);
//        queryByPhone.whereEqualTo("phoneList",ParseUser.getCurrentUser().getObjectId());
//
//        List<ParseQuery> queryList=new ArrayList<ParseQuery>();
//        queryList.add(queryByCreatedBy);
//        queryList.add(queryByFriend);
//        queryList.add(queryByPhone);
//        ParseQuery<Group> mainQuery=ParseQuery.or(queryList);
//        mainQuery.setSkip((page-1)*Constants.NUM_LOAD_GROUP);
//        mainQuery.setLimit(Constants.NUM_LOAD_GROUP);
//        mainQuery.orderByDescending("updatedAt");
//        mainQuery.findInBackground(new FindCallback<Group>() {
//            @Override
//            public void done(final List<Group> list, ParseException e) {
//                if(e==null) {
//                    ParseObject.unpinAllInBackground(LABEL_GROUP, new DeleteCallback() {
//                        @Override
//                        public void done(ParseException e) {
//                            if(e==null)
//                            {
//                                ParseObject.pinAllInBackground(LABEL_GROUP, list,new SCSaveCallback(context, new SCSaveCallback.Callback() {
//                                    @Override
//                                    public void done() {
//                                        handler.sendEmptyMessage(0);
//                                    }
//                                }));
//                            }
//                            else
//                                ParseAPI.erroHandling(context,e);
//                        }
//                    });
//                }
//                else
//                    ParseAPI.erroHandling(context,e);
//            }
//        });
//    }

    //개인에게 받은 사진 목록 받아오기 (with thumbnail images)
    public static List<Picture> getPicturesByMe()  {
        ParseQuery<Picture> query=ParseQuery.getQuery(Picture.class);
        query.whereEqualTo("friendList", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("hasPhoto", true);
        query.whereEqualTo("photoSynched", true);
        query.setLimit(Constants.NUM_LOAD_PICTURE);
        try {
            return query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void getPicturesCountByMe(CountCallback countCallback)
    {
        ParseQuery<Picture> query=ParseQuery.getQuery(Picture.class);
        query.whereEqualTo("friendList", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("hasPhoto",true);
        query.whereEqualTo("photoSynched",true);
        query.countInBackground(countCallback);
    }

    //업로드된 주소록 기반 쉐어캠 친구 목록 동기화
    // 서버에서 새로운 친구 추가 하고
    //클라이언트에서 새로운 친구를 local store에 추가
    public static void syncFriendWithContact(final Context context,final Handler handler)
    {
        Log.d("jyr", "sync friend with contact");
        ParseCloud.callFunctionInBackground(ParseAPI.SYNC_ALL_CONTACT, new HashMap<String, Object>(), new FunctionCallback<List<Friend>>() {
            public void done(List<Friend> result, ParseException e) {
//                Log.d("jyr",result.toString());
                if (e == null) {
                    try {
                        //새로운 친구 local store에 추가
                        ParseObject.pinAll(result);
                    } catch (ParseException e1) {
                        e1.printStackTrace();
                    }
                    handler.sendEmptyMessage(0);
                } else
                    ParseAPI.erroHandling(context, e);
            }
        });
    }

    public static void signUpCompleted(final Context context, final Handler handler){
        ParseCloud.callFunctionInBackground(ParseAPI.SIGN_UP_COMPLETED, new HashMap<String, Object>(), new FunctionCallback<JSONObject>() {
            public void done(JSONObject result, ParseException e) {

                if(e==null) {
                    Log.d("jyr","sign up completed and fetch current user");
                    //refresh user data
                    ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                handler.sendEmptyMessage(0);
                                Log.d("jyr", "user completed=" + ParseUser.getCurrentUser().get("completed"));
                            } else
                                ParseAPI.erroHandling(context, e);
                        }
                    });
                }
                else
                    ParseAPI.erroHandling(context, e);
            }
        });
    }

    //local에서 친구 목록 불러옴
    public static List<Friend> getFriends_Local(final Context context) throws ParseException {
        ParseQuery<Friend> query=ParseQuery.getQuery(Friend.class);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.whereEqualTo("deleted",false);
        query.setLimit(1000);
        query.include("friendUser");
        query.fromPin(LABEL_FRIEND);

        return query.find();

    }


    public static List<Individual> getSharePerson_Local(final Context context)
    {
        ParseQuery<Individual> query=ParseQuery.getQuery(Individual.class);
        query.setLimit(1000);
        query.fromLocalDatastore();
        try {
            return query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    //그룹 생성
    public static void createGroup(final Context context,String groupName,ArrayList<IndividualItem> individualList,SaveCallback saveCallback)
    {
        Group group=new Group();
        group.setName(groupName);
        group.setCreatedBy(ParseUser.getCurrentUser());
        for(int i=0; i<individualList.size(); i++)
        {
            //쉐어캠 친구인 경우
            if(individualList.get(i).MODE==IndividualItem.FRIEND)
            {
                group.setFriendId(individualList.get(i).objectId);
            }
            //연락처 친구
            else
            {
                group.setPhoneId(individualList.get(i).phoneNumber);
            }
        }

        group.saveInBackground(saveCallback);
    }

    //전송 실패한 사진 다시 전송
    public static void reUploadFailedPicture(Context context,UploadingPicture uploadingPicture)
    {

        if(uploadingPicture.getState()==UploadingPicture.FAILED_UPLOADING_STATE)
        {
            Intent serviceIntent=new Intent(UploadService.RE_UPLOAD_ACTION);
            serviceIntent.setClass(context,UploadService.class);
            serviceIntent.putExtra(UploadService.UPLOADING_PICTURE,uploadingPicture);
//            serviceIntent.putExtra(UploadService.FILE_PATH,filePath);
            context.startService(serviceIntent);
        }

    }

    //공유 대상에게 이미지 업로드
    public static void uploadPicture(final Context context,List<Individual> spItems, final String filePath) throws SQLException {

        //공유 대상이 없는 경우 서버에 업로드 안함
        if(spItems.size()==0)
            return;

        Log.d(TAG,"uploadPicture"+spItems.size());

//        final Picture picture=new Picture();
//        picture.setCreatedBy(createdBy);
//        picture.init();

        ShareItem shareItem =new ShareItem();

        for(int i=0; i<spItems.size(); i++)
        {
            //쉐어캠 친구인 경우
            if(spItems.get(i).getIsFriend())
            {
                Log.d(TAG,"setFriend"+spItems.get(i).getFriendObjectId());
//                picture.setFriendId(spItems.get(i).getFriendObjectId());
                shareItem.friendList.add(spItems.get(i).getFriendObjectId());
            }
            //연락처 친구
            else
            {  Log.d(TAG,"setContact"+spItems.get(i).getInternationPhone(context));
//                picture.setPhone(spItems.get(i).getInternationPhone(context));
                shareItem.phoneList.add(spItems.get(i).getInternationPhone(context));
            }
        }

//        Log.d(TAG,"upload friend length="+picture.getFriendList().length());


        Intent serviceIntent=new Intent(UploadService.UPLOAD_ACTION);
        serviceIntent.setClass(context,UploadService.class);
        serviceIntent.putExtra(UploadService.PICTURE, shareItem);
        serviceIntent.putExtra(UploadService.FILE_PATH,filePath);
        context.startService(serviceIntent);
    }



    //공유 대상에게 이미지 업로드
    public static void uploadPicture(final Context context,List<Individual> spItems, final ArrayList<String> filePathList,ParseUser createdBy)
    {

        Log.d("jyr","uploadPicture");

        for(int i=0; i<filePathList.size(); i++) {
            Picture picture = new Picture();
            picture.setCreatedBy(createdBy);
            picture.init();


            for (int j = 0; j < spItems.size(); j++) {
                //쉐어캠 친구인 경우
                if (spItems.get(j).getIsFriend()) {
                    picture.setFriendId(spItems.get(j).getFriendObjectId());
                }

                //연락처 친구
                else {
                    picture.setPhone(spItems.get(j).getInternationPhone(context));

                }
            }


            Intent serviceIntent=new Intent(UploadService.UPLOAD_ACTION);
            serviceIntent.setClass(context,UploadService.class);
            serviceIntent.putExtra(UploadService.PICTURE,picture);
            serviceIntent.putExtra(UploadService.FILE_PATH,filePathList.get(i));
            context.startService(serviceIntent);


//            picture.saveEventually(new SaveCallback() {
//                @Override
//                public void done(ParseException e) {
//                    if (e == null) {
//                        Log.d("jyr", "picutre object upload done");
//                        picture.setPhotoSynched();
//                        picture.setImage(new ParseFile(picture.getObjectId() + ".JPEG", ImageManipulate.convertImageToByte(context, filePathList.get(index))));
//                        picture.saveInBackground(new SaveCallback() {
//                            @Override
//                            public void done(ParseException e) {
//                                if (e == null) {
//                                    Log.d("jyr", "image upload success");
//                                } else
//                                    ParseAPI.erroHandling(context, e);
//                            }
//                        });
//
//                    } else {
//                        ParseAPI.erroHandling(context, e);
//                    }
//                }
//            });
        }
    }

    public static void erroHandling(Context context,ParseException e)
    {
        Log.e(TAG, "Error: " + e.getMessage());
        Util.showToast(context, ErrorCode.getToastMessageId(e));
    }

    //친구 동기화 완료 시간 설정
    public static void setFriendLastUpdatedAt(Context context,long lastUpdatedAt)
    {
//        if(friendList.size()==0)
//            return;
//        Log.d(TAG,"setFriendLastUpdatedAt");
//        long lastUpdatedAt=0;
//        //friend list중 last updatedAt찾기
//        for(int i=0; i<friendList.size(); i++)
//        {
//            if(lastUpdatedAt<friendList.get(i).getFriendUser().getUpdatedAt().getTime()){
//                lastUpdatedAt=friendList.get(i).getFriendUser().getUpdatedAt().getTime();
//            }
//        }
        ((Util)context.getApplicationContext()).editor.putLong(Constants.PREF_FRIEND_LATST_UPDATED_AT, lastUpdatedAt).commit();

    }

    //친구 동기화 했던 마지막 시간 얻어오기
    public static Date getFriendLastUpdatedAt(Context context)
    {

        long lastUpdatedAt=((Util)context.getApplicationContext()).pref.getLong(Constants.PREF_FRIEND_LATST_UPDATED_AT, Constants.PREF_FRIEND_LAST_UPDATED_AT_DEFAULT);
//        Log.d(TAG,new )
        return new Date(lastUpdatedAt);
    }


    public static void setLastUpdatedAt(Context context,List<Friend> dataList)
    {
        if(dataList.size()==0)
            return;
        Log.d(TAG,"setFriendLastUpdatedAt");
        long lastUpdatedAt=0;
        //friend list중 last updatedAt찾기
        for(int i=0; i<dataList.size(); i++)
        {
            if(lastUpdatedAt<dataList.get(i).getUpdatedAt().getTime()){
                lastUpdatedAt=dataList.get(i).getUpdatedAt().getTime();
            }
        }

        setFriendLastUpdatedAt(context,lastUpdatedAt);

    }

    public static void setLastUpdatedAt(Context context,String className,List<ParseObject> dataList)
    {
        if(dataList.size()==0)
            return;
        Log.d(TAG,"setLastUpdatedAt "+className);
        long lastUpdatedAt=0;
        //friend list중 last updatedAt찾기
        for(int i=0; i<dataList.size(); i++)
        {
            if(lastUpdatedAt<dataList.get(i).getUpdatedAt().getTime()){
                lastUpdatedAt=dataList.get(i).getUpdatedAt().getTime();
            }
        }

        if(className.equals(Friend.CLASS_NAME))
        {
            setFriendLastUpdatedAt(context,lastUpdatedAt);
        }
    }
    public static Date getLastUpdatedAt(Context context,String className)
    {
        if(className.equals(Friend.CLASS_NAME))
        {
            return getFriendLastUpdatedAt(context);
        }
        return null;
    }


    public static void pinNewData(String className,List<ParseObject> newData) throws ParseException {
        Log.d(TAG,"pin new data "+className+" size="+newData.size());
        if(className.equals(Friend.CLASS_NAME))
        {
            ParseObject.pinAll(LABEL_FRIEND,newData);
        }

    }

    //parse와 data 동기화
    public static void syncData(final Context context,String className)
    {

        if(ParseUser.getCurrentUser()==null)
        {
            Log.d(TAG,"can not sync without sigining in");
            return;
        }
        Log.d(TAG,"sync data") ;
        HashMap<String, Object> params = new HashMap<String, Object>();
        Date syncTime=getLastUpdatedAt(context,className);
        Log.d(TAG,"syncTime = "+Util.dateToUTCStr(syncTime));
        params.put("className",className);
        params.put("syncDate", syncTime);
        List<ParseObject> newData= null;
        try {
            newData = ParseCloud.callFunction(ParseAPI.SYNC_DATA, params);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
//        Log.d(TAG,"find new friend "+newFriend.size()+"list done");


        if(newData.size()!=0)
        {
            try {
                pinNewData(className,newData);
                setLastUpdatedAt(context,className,newData);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }

    //친구 리스트 서버로 부터 동기화
    public static void syncFriendList(final Context context) {



        if(ParseUser.getCurrentUser()==null)
        {
            Log.d(TAG,"can not sync without sigining in");
            return;
        }
        Log.d(TAG,"syncFriendList") ;
        HashMap<String, Object> params = new HashMap<String, Object>();
        Date syncTime=getFriendLastUpdatedAt(context);
        Log.d(TAG,"syncTime = "+syncTime);
        params.put("className",Friend.CLASS_NAME);
        params.put("syncTime", syncTime);
        List<Friend> newFriend= null;
        try {
            newFriend = ParseCloud.callFunction(ParseAPI.SYNC_DATA, params);
        } catch (ParseException e) {
            e.printStackTrace();
            return;
        }
//        Log.d(TAG,"find new friend "+newFriend.size()+"list done");


        if(newFriend.size()!=0)
        {
            Log.d(TAG,"pin all new friends"+newFriend.size());
            try {
                ParseObject.pinAll(LABEL_FRIEND,newFriend);
                //last updatedAt 갱신
//                setFriendLastUpdatedAt(context, newFriend);
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

    }


    //performance 향상을 위해 백그라운드 호출 필요
    // local 데이터베이스에 연락처 데이터를 듕기화한 뒤 (추가된 것들 추가 , 삭제 된 것들 삭제)
    // 변경사항을 서버에 업로드
    // 추가된 연락처에 대해 친구가 있는지 검사 후 있을 시 친구 추가
    // onlyForNewContact - true - 새로운 연락처가 있을 때만 친구목록 동기화 실행 // 자동으로 동기화 수행시
    //                   - false - 새로운 연락처가 없더라도 서버로 부터 새로운 친구목록 동기화 // 사용자가 수동으로 동기화 요청 할 시
    public static void syncContact(final Context context, final boolean onlyForNewContact) throws ParseException {

        Log.d(TAG,"syncContact");

        if(ParseUser.getCurrentUser()==null)
        {
            Log.d(TAG,"can not sync without sigining in");
            return;
        }

        //연락처 데이터를 불러옴
        final ArrayList<IndividualItem> contactItems = Util.getContactList(context);

        Log.d(TAG,"get "+contactItems.size()+"contacts");

        final List<Contact> addedContactItems=new ArrayList<Contact>();
        final List<Contact> deletedContactItems=new ArrayList<Contact>();

        Util.logCurrentThread(TAG);


        //연락처 데이터를 local database에서 불러옴
        ParseQuery<Contact> query=ParseQuery.getQuery(Contact.class);
        query.fromLocalDatastore();
        List<Contact> dataItems=query.find();
        Log.d(TAG,"find "+dataItems.size()+"contact from local done");

        //삭제된 연락처 있는지 확인
        //추가된 연락처 찾기
        for(int i=0; i<dataItems.size(); i++)
        {
            for(int j=0; j<contactItems.size(); j++)
            {
                //데이터가 같은 것이 있는 경우
                if(dataItems.get(i).getPhone().equals(Util.convertToInternationalNumber(context,contactItems.get(j).phoneNumber)))
                {
                    contactItems.remove(j);
                    break;
                }
                //데이터가 같은 것이 없는 경우 -> 삭제된 연락처
                else if(j==contactItems.size()-1)
                {
                    deletedContactItems.add(dataItems.get(i));
                }
            }
        }

        //추가된 연락처들
        for(int i=0; i<contactItems.size(); i++)
        {
            Contact contact=new Contact();
            contact.setCreatedBy(ParseUser.getCurrentUser());
            contact.setPhoneNumber(context, contactItems.get(i).phoneNumber);
            contact.setACL(ParseUser.getCurrentUser());
            addedContactItems.add(contact);
        }

        // 삭제된 연락처 데이터 삭제 (서버, 로컬)
        if(deletedContactItems.size()>0) {
            // 추가된 연락처 데이터 추가 (서버, 로컬)
            ParseObject.deleteAll(deletedContactItems);
            Log.d(TAG, "delete " + deletedContactItems.size() + "contacts done");
            ParseObject.unpinAll(LABEL_CONTACT, deletedContactItems);
            Log.d(TAG, "delete " + deletedContactItems.size() + "contacts local done");
        }
        if(addedContactItems.size()>0) {
            ParseObject.saveAll(addedContactItems);
            Log.d(TAG, "save " + addedContactItems.size() + "contacts done");
            ParseObject.pinAll(LABEL_CONTACT, addedContactItems);
            Log.d(TAG, "save " + addedContactItems.size() + "contacts local done");
        }


        /**
         * 서버에서 연락처로 친구목록 동기화
         */
        //새로운 연락처가 있을 때만 동기화를 원하는 경우 -> 새로운 연락처 없을 시 동기화 수행 안함
        if(onlyForNewContact==true && addedContactItems.size()==0)
        {
//            handler.sendEmptyMessage(0);
            return;
        }

        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("type", RETURN_ADDED_FRIENDS_TYPE);
        List<Friend> newFrineds=ParseCloud.callFunction(ParseAPI.SYNC_ALL_CONTACT, params);
        if(newFrineds.size()>0) {
            Log.d(TAG, "syncAllContact Done");
            ParseObject.pinAll(LABEL_FRIEND, newFrineds);
            Log.d(TAG, "save new " + newFrineds.size() + " friend to local done");
        }

        Util.setContactSyncTime(context);


    }
}


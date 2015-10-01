package com.claude.sharecam.parse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.api.ErrorCode;
import com.claude.sharecam.orm.UploadingPicture;
import com.claude.sharecam.share.ShareItem;
import com.claude.sharecam.upload.UploadService;
import com.claude.sharecam.util.ImageManipulate;
import com.parse.CountCallback;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONArray;
import org.json.JSONException;
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

    public static final int NO_OBJECT_ERROR_CODE=101;

    public static final String TAG="ParseAPI";
    //pin label name
    public static final String LABEL_CONTACT="contact";// 쉐어캠에 없는 연락처를 저장
    public static final String LABEL_GROUP="group";
    //    public static final String LABEL_FRIEND="friend";// 쉐어캠에 있는 연락처들 저장
    public static final String LABEL_SHARECONTACT="shareContact";
    public static final String LABEL_PICTURE="PICTURE";
    public static final String LABEL_ALBUM ="sendAlbum";
    public static final String LABEL_NOTIFICATION="notification";

    public static final String SM_PHONE_VERIFY = "sm_phone_verify";
    public static final String SM_PHONE_CONFIRM = "sm_phone_confirm";
    public static final String FETCH_CONTACT="fetch_contact";
    public static final String SYNC_ALL_CONTACT ="sync_all_contact"; /** deprecated **/
    public static final String DELETE_CONTACT="delete_contact";
    public static final String INFORM_NEW_USER ="inform_new_user";
    //    public static final String SYNC_FRIEND_LIST="sync_friend_list";
    public static final String SYNC_DATA="sync_data";

    //parameter
    public static final int RETURN_ALL_FRIENDS_TYPE=0;
    public static final int RETURN_ADDED_FRIENDS_TYPE=1;

    /**
     * 회원 가입 시
     */


    /**
     * 회원 가입 시 호출
     * wrapper function
     *
     * 1. initContact
     * 2. fetchContact
     */

    public static void initialize(ParseUser user, final Context context, final Handler handler)
    {
        Log.d(TAG,"initialize");

        //연락처 초기화
        //모든 연락처 데이터  업로드
        initContact(user, context, new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                //수정된 연락처 불러오기
                fetchContactInBackground(context, new Handler() {

                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);
                        //Contact데이터 메모리에 로딩
                        Util.initContactItemList(context, handler);
                    }
                });
            }

        });
    }

    public static void fetchContactInBackground(final Context context, final Handler handler)
    {
        new Thread(){
            @Override
            public void run() {
                try {
                    fetchContact(context,handler);
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

        }.start();
    }

    /**
     * background 호출 필요
     * syncTime 이후 서버에서 갱신된 데이터들 받아와서 업데이트(phone, friendUser)
     */
    public static void fetchContact(final Context context, final Handler handler) throws ParseException {
        Log.d(TAG,"fetch Contact");


        HashMap<String, Object> params = new HashMap<String, Object>();
//                params.put("type", String.valueOf(RETURN_ALL_FRIENDS_TYPE));
        params.put("syncTime", Util.getContactSyncTime(context));

        //서버에서 연락처로 친구목록 동기화
        //
        final HashMap<String, Object> result=(HashMap<String, Object>) ParseCloud.callFunction(ParseAPI.FETCH_CONTACT, params);
        List<Contact> contactList = ((List<Contact>)(result).get("contact"));

        Log.d(TAG, "fetch contact done " + contactList.size());

        //가져온 데이터들이 있는 경우 갱신
        if (contactList.size() > 0) {
            List<ParseObject> contactPointList = new ArrayList<ParseObject>();
            for (int i = 0; i < contactList.size(); i++) {

                ParseObject contactPoint = ParseObject.createWithoutData("Point", contactList.get(i).getObjectId());
                Contact.copyServerData(contactPoint, contactList.get(i));
                contactPointList.add(contactPoint);

            }


            ParseObject.pinAll(LABEL_CONTACT, contactPointList);

            Util.setContactSyncTime(context, (Long) result.get("syncTime"));//sync time 갱신
            Log.d(TAG, "pin fetched contact done");
            handler.sendEmptyMessage(0);
        }

        else {
            Util.setContactSyncTime(context, (Long) result.get("syncTime"));//sync time 갱신
            handler.sendEmptyMessage(0);
        }
/*
        ParseCloud.callFunctionInBackground(ParseAPI.FETCH_CONTACT, params, new FunctionCallback<HashMap<String, Object>>() {
            public void done(final HashMap<String, Object> result, ParseException e) {
                if (e == null) {
                    final List<Contact> contactList = ((List<Contact>) result.get("contact"));

//
                    Log.d(TAG, "fetch contact done " + contactList.size());


                    //가져온 데이터들이 있는 경우 갱신
                    if (contactList.size() > 0) {
                        List<ParseObject> contactPointList = new ArrayList<ParseObject>();
                        for (int i = 0; i < contactList.size(); i++) {

                            ParseObject contactPoint = ParseObject.createWithoutData("Point", contactList.get(i).getObjectId());
                            Contact.copyServerData(contactPoint, contactList.get(i));
                            contactPointList.add(contactPoint);

                        }


                        ParseObject.pinAllInBackground(LABEL_CONTACT, contactPointList, new SCSaveCallback(context, new SCSaveCallback.Callback() {
                            @Override
                            public void done() {
                                Util.setContactSyncTime(context, (Long) result.get("syncTime"));//sync time 갱신
                                Log.d(TAG, "pin fetched contact done");
                                handler.sendEmptyMessage(0);
                            }
                        }));
                    } else {
                        Util.setContactSyncTime(context, (Long) result.get("syncTime"));//sync time 갱신
                        handler.sendEmptyMessage(0);
                    }

                } else {
                    ParseAPI.erroHandling(context, e);
                }
            }
        });*/
    }

    /**
     * 1. 모든 연락처들 server에 업로드
     * 2. local data(contactName, contactPhotoUri)와 함께 연락처 데이터 pin
     **/

    public static void initContact(ParseUser user, final Context context, final Handler handler) {



        Log.d(TAG,"initContact");
        final long newLocalSyncTime=new Date().getTime();
        //모든 연락처 불러오기
        final ArrayList<Contact> contactList = Util.getContactListFromCP(context, 0);


        Contact.saveAllInBackground(contactList, new SCSaveCallback(context, new SCSaveCallback.Callback() {
            @Override
            public void done() {
                Log.d(TAG, "save all contact done");

                //모든 데이터 삭제후 재 업로드
                ParseObject.unpinAllInBackground(LABEL_CONTACT,new SCDeleteCallback(context, new SCDeleteCallback.Callback() {
                    @Override
                    public void done() {
                        Log.d(TAG, "unpinAllContact done");
                        Contact.pinAllInBackgroundWithLocalData(LABEL_CONTACT, contactList, new SCSaveCallback(context, new SCSaveCallback.Callback() {

                            @Override
                            public void done() {
                                Log.d(TAG, "pinAllContact done");
                                //로컬 동기화 시간 갱신
                                Util.setLoalContactSyncTime(context, newLocalSyncTime);
                                handler.sendEmptyMessage(0);
                            }
                        }));
                    }
                }));
            }
        }));
    }



    /**
     * background 호출
     * 1. 로컬의 연락처 데이터들을 동기화
     * 2. 수정된 데이터 로컬(pin) 및 서버에 반영
     * 3. 수정된 연락처 있다면 서버에서 가져와서 업데이트 (fetchContact)
     */

    public static void syncContact(final Context context,Handler handler) throws ParseException {

        Log.d(TAG,"syncContact");
        long newLocalSyncTime=new Date().getTime();
        /**
         *  추가 / 수정 데이터 로컬 동기화 및 서버 전송
         */

//        List<Contact> modifiedContactList=Util.getContactListFromCP(context,Util.getLocalContactSyncTime(context));//최종 로컬 동기화 시간 이후에 추가/수정된 데이터들만 불러옴


        List<Contact> allPinnedContactList=ParseAPI.getPinnedContactList(false);
        Log.d(TAG,"getPinnedContactList done");
        List<Contact> allContactList=Util.getPinnedContactListFromCP(context, allPinnedContactList, 0);//android 기기에서 불러온 모든 연락처 데이터
        Log.d(TAG,"getPinnedContactListFromCP done");
        /**
         *  삭제된 데이터 로컬 동기화 및 서버 전송
         *  pin data와 CP 데이터의 갯수 비교 -> 삭제된 거 있는 경우 찾아서 삭제
         */
        int pinnedSize=allPinnedContactList.size();//저장되어는 로컬 연락처 데이터들

        if(pinnedSize==allContactList.size())
        {
            Log.d(TAG,"any contacts haven't deleted");
        }
        else{
            int deltedSize=pinnedSize-allContactList.size();
            Log.d(TAG,"contacts deleted "+(deltedSize)+" and find deleted contact");
//            ParseQuery<Contact> query2=ParseQuery.getQuery(Contact.class);
//            query2.fromPin(LABEL_CONTACT);
//            List<Contact> pinnedContactList=query2.find();

            ArrayList<Contact> deletedContactList=new ArrayList<Contact>();
            //삭제된 연락처 데이터 찾기
            for(int i=0; i<allPinnedContactList.size(); i++)
            {
                if(deltedSize==0)
                    break;

                for(int j=0; j<allContactList.size(); j++)
                {
                    //존재하는 연락처의 경우
                    if(allPinnedContactList.get(i).getPhone().equals(allContactList.get(j).getPhone()))
                    {
                        allContactList.remove(j);
                        break;

                    }
                    //삭제된 연락처인 경우
                    else if(j==allContactList.size()-1)
                    {
                        Log.d(TAG,"delete "+allPinnedContactList.get(i).getPinContactName()+" "+allPinnedContactList.get(i).getPhone());
                        deletedContactList.add(allPinnedContactList.get(i));
                        deltedSize--;
                    }
                }
            }

//            ParseObject.unpinAll(LABEL_CONTACT,deletedContactList);
//            Log.d(TAG,"unpin done "+deletedContactList.size());
            ParseObject.deleteAll(deletedContactList);
            Log.d(TAG,"delete done "+deletedContactList.size());

            allPinnedContactList=ParseAPI.getPinnedContactList(false);
        }

        List<Contact> modifiedContactList=Util.getPinnedContactListFromCP(context, allPinnedContactList, Util.getLocalContactSyncTime(context));

        /**
         * 수정 된 데이터가 있다면 save to server, pin to local
         * syncTime 이후에 수정된 데이터 CP data에서 가져와서 Pin data 에 적용 및 서버 전송
         */
        if(modifiedContactList.size()>0) {
            ArrayList<Contact> syncNeededContactcList = new ArrayList<Contact>();//phone이 수정된 경우
            ArrayList<Contact> pinNeededContactList=new ArrayList<Contact>();//local 갱신 필요 데이터 (contactName,PhotoUri,phone 중 하나가 수정된 경우)
            //추가 혹은 수정된 연락처 로컬 동기화 및 서버 전송
            Log.d(TAG, "contact "+modifiedContactList.size() + " modified");

            for (int i = 0; i < modifiedContactList.size(); i++) {

                ParseQuery<Contact> modifiedContact = ParseQuery.getQuery(Contact.class);
                modifiedContact.whereEqualTo("recordId", modifiedContactList.get(i).getRecordId());
//                modifiedContact.whereEqualTo("phone", modifiedContactList.get(i).getPhone());
                modifiedContact.fromPin(LABEL_CONTACT);
                List<Contact> origin = modifiedContact.find();
                Log.d(TAG, "modified recordId=" + origin.get(0).getRecordId() + " modified=" + origin.get(0).getContactName() + " " + origin.get(0).getPhone());

                //연락처 데이터가 수정된 경우
                if (origin.size() != 0) {

                    //전화번호가 수정 되지 않은 경우
                    if(modifiedContactList.get(i).getPhone().equals(origin.get(0).getPhone()))
                    {
                        //phone
                        if(! modifiedContactList.get(i).getContactName().equals(origin.get(0).contactName) ||
                                !(modifiedContactList.get(i).getContactPhotoUri()!=null && origin.get(0).contactPhotoUri!=null && modifiedContactList.get(i).getContactPhotoUri().equals(origin.get(0).contactPhotoUri)) ||
                                !(modifiedContactList.get(i).getContactPhotoUri()==null && origin.get(0).contactPhotoUri==null))
                        {
                            Log.d(TAG, "pin recordId=" + modifiedContactList.get(i).getRecordId() + " modified=" + modifiedContactList.get(i).getContactName() + " " + modifiedContactList.get(i).getPhone());

                            origin.get(0).setContactName(modifiedContactList.get(i).getContactName());
                            origin.get(0).setContactPhotoUri(modifiedContactList.get(i).getContactPhotoUri());
                            pinNeededContactList.add(origin.get(0));
                        }
                    }
                    //전화번호가 수정된 경우 -> 서버 반영
                    else {
                        Log.d(TAG, "save recordId=" + modifiedContactList.get(i).getRecordId() + " modified=" + modifiedContactList.get(i).getContactName() + " " + modifiedContactList.get(i).getPhone());

                        //원래 데이터에 받아온 데이터의 서버 데이터 내용 복사후 데이터 서버에 저장 하기 위한 리스트에 추가
                        origin.get(0).setPhoneNumber(context, modifiedContactList.get(i).getPhone());
                        syncNeededContactcList.add(origin.get(0));
                        origin.get(0).setContactName(modifiedContactList.get(i).getContactName());
                        origin.get(0).setContactPhotoUri(modifiedContactList.get(i).getContactPhotoUri());
                        pinNeededContactList.add(origin.get(0));
                    }
                }
            }


            //수정/ 추가된 연락처 로컬 저장
//            ParseObject.pinAll(LABEL_CONTACT,syncNeededContactcList);
            Contact.pinAllWithLocalData(LABEL_CONTACT,pinNeededContactList);
            Log.d(TAG, "modified contact pin done " + pinNeededContactList.size());
            ParseObject.saveAll(syncNeededContactcList);
            Log.d(TAG, "modified contact save done " + syncNeededContactcList.size());
        }
        else{
            Log.d(TAG,"any contacnts haven't modified");
        }

        /**
         * 추가된 데이터가 있다면 추가
         * pin data recordId 이외의 contact data 중 syncTime 이후에 수정된 데이터(새로운 전화번호) 저장
         */
        ArrayList<Contact> addedContactList=Util.getContactListWithoutPinFromCP(context,allPinnedContactList, Util.getLocalContactSyncTime(context));
        if(addedContactList.size()>0)
        {
            Log.d(TAG,"contact "+addedContactList.size()+" added");

            ParseObject.saveAll(addedContactList);
            Contact.pinAllWithLocalData(LABEL_CONTACT,addedContactList);
        }
        else{
            Log.d(TAG,"any contacnts haven't added");
        }


        //로컬 타임 동기화
        Util.setLoalContactSyncTime(context,newLocalSyncTime);

        /**
         * 서버에서 수정된 데이터 동기화 후 종료
         */
        fetchContact(context,handler);


    }




    public static List<Contact> getPinnedContactList(boolean sortByName) throws ParseException {
        ParseQuery<Contact> query=ParseQuery.getQuery(Contact.class);
        query.fromPin(LABEL_CONTACT);
        query.include("friendUser");
        if(sortByName)
            query.orderByAscending("contactName");
        List<Contact> result=query.find();
        if(result==null)
            result=new ArrayList<Contact>();
        return result;
    }

    /**
     * 전화번호로 연락처 이름 가져오기
     */
    public static String getContactNameWithPhone(String phone) throws ParseException {
        ParseQuery<Contact> query=ParseQuery.getQuery(Contact.class);
        query.whereEqualTo("phone", phone);
        query.fromPin(LABEL_CONTACT);
        Contact contact=query.getFirst();

        if(contact==null)
        {
            return phone;
        }
        else
        {
            return contact.getPinContactName();
        }
    }

    //쉐어캠에 있는 연락처 친구들 불러오기 (Contact의 friendUser field가 exist한 object들)
    public static List<Contact> getContactListWithFriendUser() throws ParseException {
        ParseQuery<Contact> query=ParseQuery.getQuery(Contact.class);
        query.whereExists("friendUser");
        query.fromPin(LABEL_CONTACT);
        query.include("friendUser");
        List<Contact> result=query.find();
        if(result==null)
            result=new ArrayList<Contact>();
        return result;
    }

    //공유 설정 친구들 불러오기
    //preference에 공유하고자 하는 전화번호 저장되어 있는데 이와 matching되는 것을 찾음
    public static List<Contact> getShareContactList(Context context,List<Contact> contactList,List<Contact> friendList){

        Log.d(TAG,"getShareContactsList");
        ArrayList<Contact> addedItems=new ArrayList<Contact>();//공유 대상
//        ArrayList<String> sharePhoneList=Util.getShareIndividual(context);

        /**
         * 1. 친구 리스트 중에 공유 설정한 사용자가 있는지 확인 -> 없는 경우 추가 안함
         * 2. 친구 아닌 연락처에서 해당 전화번호 있는지 확인 -> 없는 경우 추가 안함
         */

        ShareItem shareItem = null;

        try {
            shareItem=Util.getShareList(context);
        } catch (JSONException e) {
            Log.e(TAG,e.getMessage());
            e.printStackTrace();
        }
//        ArrayList<String> shareUserList= (ArrayList<String>) shareItem.shareUserList;
        ArrayList<String> sharePhoneList= (ArrayList<String>) shareItem.sharePhoneList;



        //1. 친구 리스트 중에 공유 설정한 사용자가 있는지 확인 -> 없는 경우 추가 안함
        for(int i=0; i<sharePhoneList.size(); i++)
        {
            for(int j=0; j<friendList.size(); j++)
            {
                if(sharePhoneList.get(i).equals(friendList.get(j).getFriendUser().getPhone()))
                {
                    addedItems.add(friendList.get(j));
                    sharePhoneList.remove(i);
                    i--;
                    break;
                }
            }
        }

        for(int i=0; i<sharePhoneList.size(); i++)
        {
            for(int j=0; j<contactList.size(); j++)
            {
                if(sharePhoneList.get(i).equals(contactList.get(j).getPhone()))
                {
                    addedItems.add(contactList.get(j));
                    sharePhoneList.remove(i);
                    i--;
                    break;
                }
            }
        }


        return  addedItems;
    }

    /**
     * 연락처 동기화
     */





    //select * from PICTURE where friendList = sdfgsdfg and dfgdsfg = dsfgdfg ;






    public static void getPicturesCountByMe(CountCallback countCallback)
    {
        ParseQuery<Picture> query=ParseQuery.getQuery(Picture.class);
        query.whereEqualTo("friendList", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("hasPhoto",true);
        query.whereEqualTo("photoSynched",true);
        query.countInBackground(countCallback);
    }


    //새로운 사용자의 등록을 타사용자에게 알림
    //타 사용자 중에 해당 사용자의 연락처를 가지고 있는 경우 친구 추가
    public static void informNewUser(final Context context, final Handler handler){
        ParseCloud.callFunctionInBackground(ParseAPI.INFORM_NEW_USER, new HashMap<String, Object>(), new FunctionCallback<JSONObject>() {
            public void done(JSONObject result, ParseException e) {

                if(e==null) {

                    handler.sendEmptyMessage(0);

                }
                else
                    ParseAPI.erroHandling(context, e);
            }
        });
    }


    //그룹 생성
    public static void createGroup(final Context context,String groupName,List<Contact> individualList,SaveCallback saveCallback)
    {
        Group group=new Group();
        group.setName(groupName);
        group.setCreatedBy(ParseUser.getCurrentUser());
        for(int i=0; i<individualList.size(); i++)
        {

            //쉐어캠 친구인 경우
            if(individualList.get(i).getFriendUser()!=null)
            {
                group.setUserList(individualList.get(i).getFriendUser());
            }
            //연락처 친구
            else
            {
                group.setPhoneList(individualList.get(i).getPhone());
            }

        }

        group.saveInBackground(saveCallback);
    }

    //전송 실패한 사진 다시 전송
    public static void reUploadFailedPicture(Context context,UploadingPicture uploadingPicture)
    {
        //네트워크 연결 불안정 시
        if(Util.checkNetwork(context))
        {
            Util.showToast(context, R.string.network_unavailable);
            return;
        }

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
    public static void uploadPicture(final Context context,ShareItem shareItem, final String filePath) throws SQLException {

        Log.d(TAG,"uploadPicture");

        //공유 대상이 없는 경우 서버에 업로드 안함
        if(shareItem.isEmpty())
            return;



        Intent serviceIntent=new Intent(UploadService.UPLOAD_ACTION);
        serviceIntent.setClass(context,UploadService.class);
        serviceIntent.putExtra(UploadService.PICTURE, shareItem);
        serviceIntent.putExtra(UploadService.FILE_PATH,filePath);
        context.startService(serviceIntent);
    }


    //
    //공유 대상에게 이미지 업로드
    public static void uploadPicture(final Context context,ShareItem shareItem, final ArrayList<String> filePathList,ParseUser createdBy)
    {

        if(shareItem.isEmpty())
            return;

        Log.d("jyr","uploadPicture");

        for(int i=0; i<filePathList.size(); i++) {


            Intent serviceIntent=new Intent(UploadService.UPLOAD_ACTION);
            serviceIntent.setClass(context,UploadService.class);
            serviceIntent.putExtra(UploadService.PICTURE,shareItem);
            serviceIntent.putExtra(UploadService.FILE_PATH,filePathList.get(i));
            context.startService(serviceIntent);


        }
    }

//    public static List<Picture> getAlbumCreatedByMe()
//    {
//        ParseQuery<Picture> query=ParseQuery.getQuery(Picture.class);
//
//    }

    public static void erroHandling(Context context,ParseException e)
    {
        Log.e(TAG, "Error: " + e.getMessage() + e.getCode());
        Util.showToast(context, ErrorCode.getToastMessageId(e));
    }







    public static void uploadProfilePicture(Context context,String filePath, final SCSaveCallback scSaveCallback)
    {

        Log.d(TAG,"uploadProfilePicture");
        Bitmap profileBitmap= ImageManipulate.getResizedImageFromPath(filePath,Constants.PROFILE_FULL_SIZE);

        final ParseFile profileFile=new ParseFile(new Date().getTime()+".JPEG",ImageManipulate.bitmapToByteArray(profileBitmap));
        profileFile.saveInBackground(new SCSaveCallback(context, new SCSaveCallback.Callback() {
            @Override
            public void done() {
                User user = (User) ParseUser.getCurrentUser();
                user.setProfile(profileFile);
                user.put("name", "dfgdg");
                user.saveInBackground(scSaveCallback);
            }
        }));

//        user.saveInBackground(new SCSaveCallback());

    }

    public static void signOut(Context context,DeleteCallback deleteCallback)
    {
        User user= (User) ParseUser.getCurrentUser();
        user.deleteInBackground(deleteCallback);
    }

    /**
     *
     * 사진 불러오기
     *
     */

    /**
     * 특정 앨범의 다운로드 하지 않은 사진들 가져오기
     */
    public static void findNotDownloadedPicturesInBackground(Album album,FindCallback<Picture> findCallback)
    {
        ParseQuery<Picture> query=ParseQuery.getQuery(Picture.class);
        query.fromPin(LABEL_PICTURE);
        query.whereEqualTo(Picture.ALBUM, album);
        query.whereEqualTo(Picture.SAVED,false);
        query.findInBackground(findCallback);
    }

    /**
     * find 앨범
     */
    public static void findAlbumByIdInBackground(String id, GetCallback<Album> getCallback) {
        ParseQuery<Album> query = ParseQuery.getQuery(Album.class);
        query.fromPin(LABEL_ALBUM);
        query.whereEqualTo(Album.LOCAL_ID,id);
        query.getFirstInBackground(getCallback);
//        query.getInBackground(String.valueOf(id), getCallback);
    }

    public static Album findAlbumById(String id) {
        ParseQuery<Album> query = ParseQuery.getQuery(Album.class);
        query.fromPin(LABEL_ALBUM);
        query.whereEqualTo(Album.LOCAL_ID, id);
        try {
            return query.getFirst();
        } catch (ParseException e) {

            e.printStackTrace();
            return null;

        }
//        query.getInBackground(String.valueOf(id), getCallback);
    }

    /**
     * find 앨범의 사진 수
     */

    public static void findPinnedAlbumPicturesSizeInBackground(Album album,CountCallback countCallback) throws JSONException {
        if(album.getType()==Album.SEND_ALBUM_TYPE_VALUE) {
            ParseAPI.findPinnedSendAlbumPictureCountInBackground(album, countCallback);
        }
        else if(album.getType()==Album.RECEIVE_ALBUM_TYPE_VALUE)
        {
            ParseAPI.findPinnedReceiveAlbumPictureCountInBackground(album, countCallback);
        }

    }

    /**
     * find 앨범의 사진들
     */
    public static void findPinnedAlbumPictruesInBackground(int page, Album album, FindCallback findCallback) throws JSONException {
        if(album.getType()==Album.SEND_ALBUM_TYPE_VALUE) {

            ParseAPI.findPinnedSendAlbumPictureInBackground(page, Constants.NUM_LOAD_PICTURE, album, findCallback);
        }
        else if(album.getType()==Album.RECEIVE_ALBUM_TYPE_VALUE)
        {
            ParseAPI.findPinnedReceiveAlbumPictureInBackground(page, Constants.NUM_LOAD_PICTURE, album, findCallback);
        }
    }

    /**
     * find 보낸 앨범의 사진
     */
    public static  void findPinnedSendAlbumPictureInBackground(int page, int num, Album sendAlbum, FindCallback<Picture> findCallback) throws JSONException {

        Log.d(TAG,"findPinnedSendAlbumPictureInBackground");
        ParseQuery<Picture> query=ParseQuery.getQuery(Picture.class);
        query.whereContainsAll(Picture.PHONE_LIST, sendAlbum.getPhoneArrayList());
        query.whereEqualTo(Picture.CREATED_BY,ParseUser.getCurrentUser());
        query.whereEqualTo(Picture.PHONE_LIST_SIZE, sendAlbum.getPhoneListSize());
        query.whereEqualTo(Picture.PHOTO_SYNCHED, true);
        query.whereGreaterThan("updatedAt", Util.getAvailablePictureLastDate());
        query.orderByDescending("updatedAt");
        query.setSkip((page - 1) * num);
        query.setLimit(num);
        query.fromPin(LABEL_PICTURE);
        query.findInBackground(findCallback);
    }


    /**
     * find 보낸 사진 앨범의 사진 갯수
     */
    public static void findPinnedSendAlbumPictureCountInBackground(Album sendAlbum,CountCallback countCallback) throws JSONException {
        Log.d(TAG, "findPinnedSendAlbumPictureCountInBackground");
        ParseQuery<Picture> query=ParseQuery.getQuery(Picture.class);
        query.whereContainsAll(Picture.PHONE_LIST, sendAlbum.getPhoneArrayList());
        query.whereEqualTo(Picture.CREATED_BY, ParseUser.getCurrentUser());
        query.whereEqualTo(Picture.PHONE_LIST_SIZE, sendAlbum.getPhoneListSize());
        query.whereEqualTo(Picture.PHOTO_SYNCHED, true);
        query.whereGreaterThan("updatedAt", Util.getAvailablePictureLastDate());
        query.fromPin(LABEL_PICTURE);
        query.countInBackground(countCallback);
    }


    /**
     * find 받은 앨범의 사진
     */
    public static  void findPinnedReceiveAlbumPictureInBackground(int page, int num, Album album, FindCallback<Picture> findCallback) throws JSONException {

        Log.d(TAG,"findPinnedReceiveAlbumPictureInBackground");
        ParseQuery<Picture> query=ParseQuery.getQuery(Picture.class);

//        query.whereEqualTo(Picture.PHONE_LIST_SIZE, album.getPhoneListSize());
        query.whereEqualTo(Picture.CREATED_BY, album.getSender());
        query.whereEqualTo(Picture.PHOTO_SYNCHED, true);
        query.whereGreaterThan("updatedAt", Util.getAvailablePictureLastDate());
        query.orderByDescending("updatedAt");
        query.setSkip((page - 1) * num);
        query.setLimit(num);
        query.fromPin(LABEL_PICTURE);
        query.findInBackground(findCallback);
    }


    /**
     *
     * find 받은 사진 앨범의 사진 갯수
     */
    public static void findPinnedReceiveAlbumPictureCountInBackground(Album album,CountCallback countCallback) throws JSONException {
        Log.d(TAG, "findPinnedReceiveAlbumPictureCountInBackground");
        ParseQuery<Picture> query=ParseQuery.getQuery(Picture.class);
        query.whereEqualTo(Picture.CREATED_BY, album.getSender());
//        query.whereEqualTo(Picture.PHONE_LIST_SIZE, album.getPhoneListSize());
        query.whereEqualTo(Picture.PHOTO_SYNCHED, true);
        query.whereGreaterThan("updatedAt", Util.getAvailablePictureLastDate());
        query.fromPin(LABEL_PICTURE);
        query.countInBackground(countCallback);
    }

    /**
     * find 자동 다운로드를 하기 위한 사진들을 다운로드
     */
    public static void findAutoDownloadPicturesInBackground(FindCallback<Picture> callback)
    {
        Log.d(TAG, "findAutoDownloadPicturesInBackground");
        // 받은 사진 앨범 중 autodownload true인 앨범들
        ParseQuery<Album> albumQuery=ParseQuery.getQuery(Album.class);
        albumQuery.fromPin(LABEL_ALBUM);
        albumQuery.whereEqualTo(Album.AUTO_DOWNALOD, true);
        albumQuery.whereEqualTo(Album.TYPE, Album.RECEIVE_ALBUM_TYPE_VALUE);
        //해당 앨범의 사진 중 expire되지 않고 사진 등록되어있는 picture 불러옴
        ParseQuery<Picture> pictureQuery=ParseQuery.getQuery(Picture.class);
        pictureQuery.whereGreaterThan("updatedAt", Util.getAvailablePictureLastDate());
        pictureQuery.whereEqualTo(Picture.PHOTO_SYNCHED, true);
        pictureQuery.whereEqualTo(Picture.SAVED, false);
        pictureQuery.whereMatchesQuery(Picture.ALBUM, albumQuery);

        pictureQuery.fromPin(LABEL_PICTURE);
        pictureQuery.findInBackground(callback);
    }

    /**
     * find 자동 다운로드를 하기 위한 사진들을 다운로드
     */
    public static List<Picture> findAutoDownloadPictures()
    {
        Log.d(TAG, "findAutoDownloadPicturesInBackground");
        // 받은 사진 앨범 중 autodownload true인 앨범들
        ParseQuery<Album> albumQuery=ParseQuery.getQuery(Album.class);
        albumQuery.fromPin(LABEL_ALBUM);
        albumQuery.whereEqualTo(Album.AUTO_DOWNALOD, true);
        albumQuery.whereEqualTo(Album.TYPE,Album.RECEIVE_ALBUM_TYPE_VALUE);
        //해당 앨범의 사진 중 expire되지 않고 사진 등록되어있는 picture 불러옴
        ParseQuery<Picture> pictureQuery=ParseQuery.getQuery(Picture.class);
        pictureQuery.whereGreaterThan("updatedAt", Util.getAvailablePictureLastDate());
        pictureQuery.whereEqualTo(Picture.PHOTO_SYNCHED, true);
        pictureQuery.whereEqualTo(Picture.SAVED, false);
        pictureQuery.whereMatchesQuery(Picture.ALBUM, albumQuery);
        pictureQuery.include(Picture.ALBUM);

        pictureQuery.fromPin(LABEL_PICTURE);
        try {
            return pictureQuery.find();
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }
    }

    //개인에게 받은 사진 목록 받아오기
    public static void getPicturesByMe(int page,int num,FindCallback<Picture> findCallback)  {
        ParseQuery<Picture> query=ParseQuery.getQuery(Picture.class);
        query.whereEqualTo("friendList", ParseUser.getCurrentUser().getObjectId());
        query.whereEqualTo("hasPhoto", true);
        query.whereEqualTo("photoSynched", true);
        query.orderByDescending("createdAt");
        query.setSkip((page - 1) * num);
        query.setLimit(num);
        query.findInBackground(findCallback);
    }


    /**
     * Notification 목록 불러옴
     */
    public static void findNotificationOrderByDate(int page,int num,FindCallback<Notification> findCallback)
    {
        Log.d(TAG, "findNotificationOrderByDate");
        ParseQuery<Notification> query=ParseQuery.getQuery(Notification.class);
        query.fromPin(LABEL_NOTIFICATION);
        query.orderByDescending(Notification.DATE);
        query.setSkip((page - 1) * num);
        query.setLimit(num);
        query.include(Notification.SEND_USER);
        query.include(Notification.PICTURE);
        query.findInBackground(findCallback);
    }

    /**
     * 앨범 화면 들어갈 떄 호출
     * 앨범 isNew -> true
     * 관련 notification -> true
     */
    public static void resetAlbumIsNew(final Context context,Album album)
    {
        //앨범 확인 반영
        album.setIsNew(false);
        try {
            album.pin(ParseAPI.LABEL_ALBUM);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(album.getType()==Album.RECEIVE_ALBUM_TYPE_VALUE) {
            //관련 notification -> true
            ParseQuery<Notification> query = ParseQuery.getQuery(Notification.class);
            query.fromPin(ParseAPI.LABEL_NOTIFICATION);
            query.whereEqualTo(Notification.ALBUM, album);
            query.findInBackground(new FindCallback<Notification>() {
                @Override
                public void done(List<Notification> list, ParseException e) {
                    if (e == null) {
                        for (int i = 0; i < list.size(); i++) {
                            list.get(i).setIsNew(false);
                        }
                        ParseObject.pinAllInBackground(ParseAPI.LABEL_NOTIFICATION, list);
                    } else {
                        ParseAPI.erroHandling(context, e);
                    }
                }
            });
        }

    }



//    /**
//     * 앨범 화면 들어갈 떄 호출
//     * 앨범 isNew -> true
//     * 관련 notification -> true
//     */
//    public static void resetPictureIsNew()
//    {
//
//    }

}


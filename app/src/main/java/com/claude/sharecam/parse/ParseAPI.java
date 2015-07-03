package com.claude.sharecam.parse;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.claude.sharecam.Util;
import com.claude.sharecam.api.ErrorCode;
import com.claude.sharecam.camera.ImageManipulate;
import com.claude.sharecam.share.IndividualItem;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Claude on 15. 5. 31..
 */
public class ParseAPI {

    //pin label name
    public static final String LABEL_FRIEND="friend";
    public static final String LABEL_SHARECONTACT="shareContact";


    public static final String SM_PHONE_VERIFY = "sm_phone_verify";
    public static final String SM_PHONE_CONFIRM = "sm_phone_confirm";
    public static final String SYNC_CONTACT="sync_contact";
    public static final String DELETE_CONTACT="delete_contact";
    public static final String SIGN_UP_COMPLETED="sign_up_completed";


    //회원가입시 초기화 진행
    //1. 연락처 서버에 올리기
    //2. 연락처로 친구 목록 동기화
    //3. 로컬로 친구들 불러오기

    public static void initialize(ParseUser user, final Context context, final Handler handler)
    {
        //연락처 초기화
        initContact(user,context,new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

                //친구목록 동기화
                ParseCloud.callFunctionInBackground(ParseAPI.SYNC_CONTACT, new HashMap<String, Object>(), new FunctionCallback<List<Friend>>() {
                    public void done(List<Friend> result, ParseException e) {

                        //존재하는 친구들 불러와서 local에 저장
                        getExistingFriends(context, new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);
                                handler.sendEmptyMessage(0);
                            }
                        });
                    }
                });
            }
        });
    }
    //delete exsting contact objects in parse server
    //upload local contact to parse server
    public static void initContact(ParseUser user, final Context context, final Handler handler) {

        //read contact list from content prover
        final ArrayList<IndividualItem> arItem = Util.getContactList(context);

        //delete all of contacts created by user
        ParseCloud.callFunctionInBackground(ParseAPI.DELETE_CONTACT, new HashMap<String, Object>(), new FunctionCallback<JSONObject>() {
            public void done(JSONObject result, ParseException e) {

                if (e == null) {
                    List<Contact> contactList = new ArrayList<Contact>();

                    for (int i = 0; i < arItem.size(); i++) {
                        Contact contact = new Contact();
                        contact.setCreatedBy(ParseUser.getCurrentUser());
                        contact.setPhone(Util.convertToInternationalNumber(context, arItem.get(i).phoneNumber));
                        contact.setACL(ParseUser.getCurrentUser());
                        contactList.add(contact);
                    }
                    //upload local contact list to parse server
                    ParseObject.saveAllInBackground(contactList, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null)
                                handler.sendEmptyMessage(0);
                            else
                                ParseAPI.erroHandling(context, e);
                        }
                    });
                } else
                    ParseAPI.erroHandling(context, e);
            }
        });
    }

    //기존 친구들 local에서 모두 삭제
    //기존 parse server에 등록되어 있는 친구들 불러와 local store에 추가
    public static void getExistingFriends(final Context context,final Handler handler)
    {
        //기존 친구 데이터 모두 삭제
        ParseObject.unpinAllInBackground(LABEL_FRIEND, new DeleteCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null) {
                    //서버로 부터 친구들 불러옴
                    ParseQuery<Friend> query2 = ParseQuery.getQuery(Friend.class);
                    query2.whereEqualTo("createdBy", ParseUser.getCurrentUser());
                    query2.include("friendUser");
                    query2.findInBackground(new FindCallback<Friend>() {
                        @Override
                        public void done(List<Friend> list, ParseException e) {
                            if (e == null) {
                                //불러온 데이터 저장
                                ParseObject.pinAllInBackground(list, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            handler.sendEmptyMessage(0);
                                        } else
                                            ParseAPI.erroHandling(context, e);
                                    }
                                });
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


    //업로드된 주소록 기반 쉐어캠 친구 목록 동기화
    // 서버에서 새로운 친구 추가 하고
    //클라이언트에서 새로운 친구를 local store에 추가
    public static void syncFriendWithContact(final Context context,final Handler handler)
    {
        Log.d("jyr", "sync friend with contact");
        ParseCloud.callFunctionInBackground(ParseAPI.SYNC_CONTACT, new HashMap<String, Object>(), new FunctionCallback<List<Friend>>() {
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
//                Log.d("jyr",result.toString());

            }
        });
    }

    //local에서 친구 목록 불러옴
    public static List<Friend> getFriends_Local(final Context context)
    {
        ParseQuery<Friend> query=ParseQuery.getQuery(Friend.class);
        query.whereEqualTo("createdBy", ParseUser.getCurrentUser());
        query.setLimit(1000);
        query.include("friendUser");
        query.fromLocalDatastore();
        try {
            return query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static List<SharePerson> getSharePerson_Local(final Context context)
    {
        ParseQuery<SharePerson> query=ParseQuery.getQuery(SharePerson.class);
        query.setLimit(1000);
        query.fromLocalDatastore();
        try {
            return query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }


    //공유 대상에게 이미지 업로드
    public static void uploadPicture(final Context context,ArrayList<SharePerson> spItems, final String filePath,ParseUser createdBy)
    {

        Log.d("jyr","uploadPicture");

        final Picture picture=new Picture();
        picture.setCreatedBy(createdBy);
        for(int i=0; i<spItems.size(); i++)
        {
            //쉐어캠 친구인 경우
            if(spItems.get(i).getIsFriend())
            {
                picture.setFriendId(spItems.get(i).getFriendObjectId());
            }

            //연락처 친구
            else
            {
                picture.setPhone(spItems.get(i).getInternationPhone(context));

            }
        }


        picture.saveEventually(new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e==null) {
                    Log.d("jyr", "picutre object upload done");
                    picture.setPhotoSynched();
                    picture.setImage( new ParseFile(picture.getObjectId()+".JPEG",ImageManipulate.convertImageToByte(context, filePath)));
                    picture.saveInBackground(new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("jyr", "image upload success");
                            } else
                                ParseAPI.erroHandling(context, e);
                        }
                    });

                }
                else {
                    ParseAPI.erroHandling(context,e);
                }
            }
        });
    }

    //공유 대상에게 이미지 업로드
    public static void uploadPicture(final Context context,ArrayList<SharePerson> spItems, final ArrayList<String> filePathList,ParseUser createdBy)
    {

        Log.d("jyr","uploadPicture");

        ArrayList<Picture> pictureList=new ArrayList<Picture>();
        for(int i=0; i<filePathList.size(); i++) {
            final Picture picture = new Picture();
            picture.setCreatedBy(createdBy);

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

            final int index=i;


            picture.saveEventually(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        Log.d("jyr", "picutre object upload done");
                        picture.setPhotoSynched();
                        picture.setImage(new ParseFile(picture.getObjectId() + ".JPEG", ImageManipulate.convertImageToByte(context, filePathList.get(index))));
                        picture.saveInBackground(new SaveCallback() {
                            @Override
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("jyr", "image upload success");
                                } else
                                    ParseAPI.erroHandling(context, e);
                            }
                        });

                    } else {
                        ParseAPI.erroHandling(context, e);
                    }
                }
            });
        }
    }

    public static void erroHandling(Context context,ParseException e)
    {

        Log.e("jyr", "Error: " + e.getMessage());
        Util.showToast(context, ErrorCode.getToastMessageId(e));
    }
}


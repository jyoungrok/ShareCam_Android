package com.claude.sharecam.parse;

import android.content.Context;
import android.os.Environment;
import android.os.Message;
import android.util.Log;

import com.claude.sharecam.Constants;
import com.claude.sharecam.download.AutoDownload;
import com.claude.sharecam.share.ShareItem;
import com.claude.sharecam.util.ImageManipulate;
import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * 생성
 *  new Picture()
 *  init();
 *
 * 업로드
 *  saveEventually
 *  saveInBackGroundWithAlbum (static method in Picture class)
 *
 */
@ParseClassName("Picture")
public class Picture extends ParseObject implements Serializable{

    byte[] byteFile;


    public Picture()
    {
//        //사진을 가진 object를 생성하였으나 사진을 서버에 등록 전
//        put("photoSynched",false);

    }

    //shareItem설정
    public void itemToPicture(ShareItem shareItem)
    {

        for(int i=0; i< shareItem.sharePhoneList.size(); i++)
        {
            setPhone(shareItem.sharePhoneList.get(i));
        }
    }

    //사진 저장 전에 object만 저징 시 시도
    public void init()
    {
        setCreatedBy(ParseUser.getCurrentUser());
        put("photoSynched",false);
    }

    //json object로 부터 Picture object의 일부 field
    //objectId, createdBy, phoneList, phoneListSize, photoSynched
    public void fromJson(JSONObject jsonObject) throws JSONException {
        setObjectId(jsonObject.getString("objectId"));
        User user=User.createWithoutData(User.class,jsonObject.getJSONObject(CREATED_BY).getString("objectId"));
//        user.setObjectId(jsonObject.getJSONObject(CREATED_BY).getString("objectId"));
        setCreatedBy(user);
        setPhoneList(jsonObject.getJSONArray(PHONE_LIST));
        setPhoneListSize(jsonObject.getInt(PHONE_LIST_SIZE));
        setPhotoSynched(jsonObject.getBoolean(PHOTO_SYNCHED));
//        setWidth(jsonObject.getInt(WIDTH));
//        setHeight(jsonObject.getInt(HEIGHT));
//        setImage((ParseFile) jsonObject.get(IMAGE));
//        setThumImage((ParseFile) jsonObject.get(THUM_IMAGE));

    }

    /**
     * field string
     */

    public static final String CLASS_NAME="Picture";
    public static final String IMAGE="image";
    public static final String THUM_IMAGE="thumImage";
    public static final String CREATED_BY="createdBy";
    public static final String PHONE_LIST="phoneList";
    public static final String PHONE_LIST_SIZE="phoneListSize";
    public static final String GROUP_LIST="groupList";
    public static final String GROUP_LIST_SIZE="groupListSize";
    public static final String PHOTO_SYNCHED="photoSynched";
    public static final String WIDTH ="width";
    public static final String HEIGHT="height";
    public static final String ALBUM="albumLocalId";

    /**
     * local field string
     */
    public static final String SAVED="saved";//local 저장 여부

    //savePicture에서 사진 저장 성공 실패 시 code로 사용
    public static final int SUCCESS_DOWNLOAD =1;
    public static final int FAIL_DOWNLOAD =-1;


    public void setByteFile(byte[] byteFile){ this.byteFile=byteFile;}
    public void setImage(ParseFile image){ put(IMAGE,image); }
    public void setThumImage(ParseFile image){ put(THUM_IMAGE,image); }
    public void setCreatedBy(ParseUser user){ put(CREATED_BY,user); }
    public void setPhone(String phone)
    {
        addUnique( PHONE_LIST,phone);
        setPhoneListSize(getPhoneListSize() + 1);

    }
    public void setPhotoSynched(boolean photoSynched)
    {
        put(PHOTO_SYNCHED, photoSynched);
    }
    public void setPhotoSynched()
    {
        put(PHOTO_SYNCHED, true);
    }
    public void setPhoneList(JSONArray phoneList){put(PHONE_LIST, phoneList);}
    public void setPhoneListSize(int phoneListSize){put(PHONE_LIST_SIZE,phoneListSize);}
    public void setWidth(int width){put(WIDTH,width);}
    public void setHeight(int height){put(HEIGHT,height);}
    public void setSaved(boolean saved){put(SAVED,saved);}
    public void setAlbum(Album album){put(ALBUM,album);}




    public byte[] getByteFile() { return  byteFile;}
    public int getWidth(){ return getInt(WIDTH);}
    public int getHeight() { return getInt(HEIGHT);}
    public User getCreatedBy(){return (User) get(CREATED_BY);}
    public String getImageURL() {return getParseFile(IMAGE).getUrl(); }
    public ParseFile getImageFile(){ return  getParseFile(IMAGE);}
    public String getThumImageURL(){ return getParseFile(THUM_IMAGE).getUrl();}
    public ParseFile getThumImageFile(){ return getParseFile(THUM_IMAGE);}
    public JSONArray getPhoneList(){return getJSONArray(PHONE_LIST);}
    public int getPhoneListSize(){return getInt(PHONE_LIST_SIZE);}
    public boolean getSaved(){return getBoolean(SAVED);}
    public Album getAlbum(){return (Album) get(ALBUM);}

    //    public Date getCreatedAt(){ return getDate("createdAt");}
    public String getCreatedAt_yyyyMMdd() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(getCreatedAt());}


    /**
     * 1. pin 전송하는 사진
     * 2. 전송하는 사진에 연결된 앨범 정보 업데이트 (SendAlbum) 보내는 앨범
     */
    public void handleSend(final Context context) throws JSONException {
        Log.d(ParseAPI.TAG,"sendAlbum handleSend");
        //보낸 사진 앨범 갱신

        final Picture picture=this;
        /**
         * 같은 사용자 목록에 공유한 앨범
         */
        ArrayList<String> phoneStrList=new ArrayList<String>();
        JSONArray phoneJSONArray=getPhoneList();
        for(int i=0; i<phoneJSONArray.length(); i++)
        {
            phoneStrList.add((String) phoneJSONArray.get(i));
        }

        ParseQuery<Album> query=ParseQuery.getQuery(Album.class);
        query.fromPin(ParseAPI.LABEL_ALBUM);
        query.whereEqualTo(Album.TYPE,Album.SEND_ALBUM_TYPE_VALUE);
        query.whereEqualTo(Album.RECEIVER_PHONE_LIST_SIZE,getPhoneList().length());
        query.whereContainsAll(Album.RECEIVER_PHONE_LIST, phoneStrList);
        query.getFirstInBackground(new GetCallback<Album>() {
            @Override
            public void done(Album album, ParseException e) {
                if(e==null || e.getCode()==101){
                    //기존 앨범이 존재하지 않는경우 -> 새로 생성
                    if (album == null) {

                        Log.d(ParseAPI.TAG, "new sendAlbum");
                        album = new Album();
                        album.setReceiverPhoneList(getPhoneList());
                        album.setType(Album.SEND_ALBUM_TYPE_VALUE);
                    }

                    album.setIsNew(true);
                    //대문 사진 변경
                    album.setPicture(picture);
                    //마지막 사진 시간 갱신
//                album.setLastUpdatedAt(new Date().getTime());
                    final Album finalAlbum = album;
                    album.pinInBackground(ParseAPI.LABEL_ALBUM, new SCSaveCallback(context, new SCSaveCallback.Callback() {
                        @Override
                        public void done() {

                            Log.d(ParseAPI.TAG, "sendAlbum handleSend done");

                            //default - > 저장 안함 설정
                            picture.setSaved(false);
                            picture.setAlbum(finalAlbum);
                            picture.pinInBackground(ParseAPI.LABEL_PICTURE, new SCSaveCallback(context, new SCSaveCallback.Callback() {
                                @Override
                                public void done() {
                                    Log.d(ParseAPI.TAG, "picture pinInBackGround done");
                                }
                            }));
                        }
                    }));
                }
                else {
                    ParseAPI.erroHandling(context, e);
                }
            }
        });


    }

    /**
     * <Notification으로 사진 받은 경우 호출>
     * 1. pin 받은 사진
     * 2. 받은 사진에 연결된 앨범 정보 업데이트
     * 3. pin Notification / 진동 알림
     * 4. 자동 다운로드 설정 된 경우 자동 다운로드 실행
     */
    public void handleReceived(final Context context, final android.os.Handler handler) throws JSONException {
        Log.d(ParseAPI.TAG, "receiveAlbum handleReceived");

        final Picture picture=this;
        final User sender=getCreatedBy();
//        final String senderPhone=sender.getPhone();

        //기존 해당 앨범 있는지 확인
        ParseQuery<Album> query=ParseQuery.getQuery(Album.class);
        query.fromPin(ParseAPI.LABEL_ALBUM);
        query.whereEqualTo(Album.TYPE, Album.RECEIVE_ALBUM_TYPE_VALUE);
        query.whereEqualTo(Album.SENDER, sender);
//        query.whereEqualTo(Album.SENDER_PHONE,senderPhone);


        query.getFirstInBackground(new GetCallback<Album>() {
            @Override
            public void done(Album album, ParseException e) {
                if(e==null || e.getCode()==101)
                {
                    //기존 앨범이 존재하지 않는경우 -> 새로 생성
                    if (album == null) {
                        Log.d(ParseAPI.TAG, "new receiveAlbum");
                        album = new Album();
                        album.setLocalId();
                        album.setSender(sender);
                        album.setType(Album.RECEIVE_ALBUM_TYPE_VALUE);
                        album.setAlarm(true);
                        album.setDefaultAutoDownload(context);
                    }

                    album.setIsNew(true);
                    //대문 사진 변경
                    album.setPicture(picture);
                    //마지막 사진 시간 갱신
//                album.setLastUpdatedAt(new Date().getTime());

                    final Album finalAlbum = album;
                    album.pinInBackground(ParseAPI.LABEL_ALBUM, new SCSaveCallback(context, new SCSaveCallback.Callback() {
                        @Override
                        public void done() {


                            Log.d(ParseAPI.TAG, "receiveAlbum handleReceived done");

                            picture.setSaved(false);
                            picture.setAlbum(finalAlbum);
                            picture.pinInBackground(ParseAPI.LABEL_PICTURE, new SCSaveCallback(context, new SCSaveCallback.Callback() {
                                @Override
                                public void done() {
                                    Log.d(ParseAPI.TAG, "picture pinInBackground done " + picture.getObjectId());

                                    final Notification notification=new Notification();
                                    notification.setAlbum(finalAlbum);
                                    notification.setDate(new Date());
                                    notification.setType(Notification.PICTURE_TYPE);
                                    notification.setSendUser(picture.getCreatedBy());
                                    notification.setPictrue(picture);
                                    notification.setContent(context);
                                    notification.setIsNew(true);
                                    notification.pinAndNotifyInBackground(finalAlbum.getLocalId(),context, new android.os.Handler() {
                                        @Override
                                        public void handleMessage(Message msg) {
                                            super.handleMessage(msg);
                                            handler.sendEmptyMessage(0);
                                            Log.d(ParseAPI.TAG, "notification pin and send done");

                                            // 자동 다운로드 설정되어있는 경우 다운로드 진행
                                            AutoDownload.downloadPictureIfNeededInBackground(context, picture, finalAlbum);

                                        }
                                    });

                                }
                            }));
                        }
                    }));

                }
                else{
                    ParseAPI.erroHandling(context, e);
                }

            }
        });

    }

    /**
     *
     */


    /**
     *  사진을 갤러리에 저장
     */
    public void downloadPictureInBackground(final Context context, final android.os.Handler handler)
    {
        new Thread() {
            @Override
            public void run() {
               if(downloadPicture(context))
               {
                   handler.sendEmptyMessage(SUCCESS_DOWNLOAD);
               }
                else{
                   handler.sendEmptyMessage(FAIL_DOWNLOAD);
               }
            }
        }.start();
//        //서버로 부터 사진 데이터 불러옴
//        getImageFile().getDataInBackground(new GetDataCallback() {
//            @Override
//            public void done(final byte[] bytes, ParseException e) {
//                if(e==null)
//                {
//                        new Thread(){
//                            @Override
//                            public void run()
//                            {
//                                File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
//                                        Environment.DIRECTORY_PICTURES), Constants.SD_CARD_FOLDER_NAME);
//                                File photo = new File(mediaStorageDir.getPath() + File.separator +  getCreatedBy().getPhone()+getObjectId()+".jpg");
//                                if (photo.exists()) {
//                                    photo.delete();
//                                }
//                                try {
//                                    FileOutputStream fos=new FileOutputStream(photo.getPath());
//                                    fos.write(bytes);
//                                    fos.close();
//
//                                    ImageManipulate.galleryAddPic(context, photo.getAbsolutePath());
//
//                                    setSaved(true);
//                                    try {
//                                        pin(ParseAPI.LABEL_PICTURE);
//                                        //저장 완료
//                                        handler.sendEmptyMessage(SUCCESS_DOWNLOAD);
//                                    } catch (ParseException e1) {
//                                        e1.printStackTrace();
//                                        handler.sendEmptyMessage(FAIL_DOWNLOAD);
//                                    }
//                                }
//                                catch (java.io.IOException e) {
//                                    Log.e("PictureDemo", "Exception in photoCallback", e);
//                                    handler.sendEmptyMessage(FAIL_DOWNLOAD);
//                                }
//
//
//                            }
//                        }.start();
//                }
//                else{
//
//                }
//            }
//        });
    }


    public boolean downloadPicture(final Context context)  {


        byte[] bytes= new byte[0];
        try {
            bytes = getImageFile().getData();
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), Constants.SD_CARD_FOLDER_NAME);
        File photo = new File(mediaStorageDir.getPath() + File.separator +  getCreatedBy().getPhone()+getObjectId()+".jpg");
        if (photo.exists()) {
            photo.delete();
        }
        try {
            FileOutputStream fos=new FileOutputStream(photo.getPath());
            fos.write(bytes);
            fos.close();

            ImageManipulate.galleryAddPic(context, photo.getAbsolutePath());

            setSaved(true);
            try {
                pin(ParseAPI.LABEL_PICTURE);
                //저장 완료
                return true;
            } catch (ParseException e1) {
                e1.printStackTrace();
                return false;
            }
        }
        catch (java.io.IOException e) {
            Log.e("PictureDemo", "Exception in photoCallback", e);
            return false;
        }


    }


}

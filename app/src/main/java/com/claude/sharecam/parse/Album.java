package com.claude.sharecam.parse;

import android.content.Context;

import com.claude.sharecam.Constants;
import com.claude.sharecam.Util;
import com.parse.FindCallback;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.UUID;

/**
 * UploadPicture 시에 auto generate
 *
 * Local에만 저장된 ParseObject
 * 보낸 앨범
 *
 * (int) type - 앨범 타입 0 - 보낸 앨범 / 1 - 받은 앨
 * (long) LAST_UPDATED_AT - 해당 앨범에 마지막 사진이 등록된 시간
 * (Pointer<Pictrue>) PICTURE - 마지막 등록 사진
 * (boolean) isNew - true - 아직 사용자가 앨범 사진 확인 안한 것이 있을 경우 / false - 그렇지 않은 경우
 *
 *      ** 받은 앨범의 경우 **
 * (String) senderPhone - 보낸 사람의 전화번호
 *
 *      ** 보낸 앨범의 경우 **
 * (Array<String>) RECEIVER_PHONE_LIST - 공유한 대상의 전화번호 리스트
 * (int) RECEIVER_PHONE_LIST_SIZE - phoneList array의 size 저장
 *
 */
@ParseClassName("Album")
public class Album extends ParseObject implements Serializable {

    public String albumName;
    public byte[] pictureFile;


    public Album()
    {

    }
    /**
     * field name
     */
    public static final String LOCAL_ID="localId";
    public static final String TYPE="type";
    public static final String SENDER="sender";
    public static final String SENDER_PHONE="senderPhone";
    public static final String RECEIVER_PHONE_LIST ="receiverPhoneList";
    public static final String RECEIVER_PHONE_LIST_SIZE ="receiverPhoneListSize";
    public static final String LAST_UPDATED_AT ="lastUpdatedAt";
    public static final String PICTURE ="picture";
    public static final String IS_NEW="isNew";
    public static final String AUTO_DOWNALOD="autoDownload";//자동 저장 설정
    public static final String ALARM="alarm";

    /**
     * field value
     */

    public static final int SEND_ALBUM_TYPE_VALUE=0;
    public static final int RECEIVE_ALBUM_TYPE_VALUE=1;


    public void setLocalId(){

        UUID localId = UUID.randomUUID();
        put(LOCAL_ID,localId.toString());


    }
    public void setType(int type){put(TYPE, type);}
    public void setIsNew(boolean isNew){put(IS_NEW,isNew);}
//    public void setLastUpdatedAt(long updatedAt) { put(LAST_UPDATED_AT,updatedAt);}
    public void setSender(User user){put(SENDER,user);}
    public void setSenderPhone(String phone){put(SENDER_PHONE,phone);}
    public void setReceiverPhoneList(JSONArray phoneList) { put(RECEIVER_PHONE_LIST,phoneList);
    put(RECEIVER_PHONE_LIST_SIZE,phoneList.length());}
    public void setPicture(Picture picture){put(this.PICTURE,picture);
    put(LAST_UPDATED_AT,picture.getUpdatedAt());}
    public void setPicture(Picture picture,Date updatedAt)
    {
        put(this.PICTURE,picture);
        put(LAST_UPDATED_AT,picture.getUpdatedAt());
    }
    public void setAutoDownload(boolean autoDownload){
        put(AUTO_DOWNALOD,autoDownload);
    }
    public void setDefaultAutoDownload(Context context)
    {
        setAutoDownload(Util.getAutoDownloadIndividual(context));
    }
    public void setAlarm(boolean alarm)
    {
        put(ALARM,alarm);
    }


    public String getLocalId(){return getString(LOCAL_ID);}
    public int getType(){return getInt(TYPE);}
    public User getSender(){return (User) get(SENDER);}
    public boolean getIsNew(){return getBoolean(IS_NEW);}
    public Date getLastUpdatedAt(){return getDate(LAST_UPDATED_AT);}
    public String getSenderPhone(){return getString(SENDER_PHONE);}
    public JSONArray getPhoneList(){return getJSONArray(RECEIVER_PHONE_LIST);}
    public ArrayList<String> getPhoneArrayList() throws JSONException {
        ArrayList<String> phoneStrList=new ArrayList<String>();
        JSONArray phoneJSONArray=getPhoneList();
        for(int i=0; i<phoneJSONArray.length(); i++)
        {
            phoneStrList.add((String) phoneJSONArray.get(i));
        }
        return phoneStrList;

    }
    public int getPhoneListSize() { return getInt(RECEIVER_PHONE_LIST_SIZE);}
    public Picture getPicture(){ return (Picture) get(PICTURE);}
    public boolean getAutoDownload(){return getBoolean(AUTO_DOWNALOD);}
    public boolean getAlarm(){return getBoolean(ALARM);}



    //보낸 사진 앨범 찾기
    public static void findPinnedSendAlbumInBackground(FindCallback<Album> findCallback)
    {
        /**
         * expire 되지 않은 보낸 사진 앨범 불러옴
         */
        ParseQuery<Album> query=ParseQuery.getQuery(Album.class);
//        query.whereGreaterThan(LAST_UPDATED_AT,new Date().getTime()- Constants.PICTURE_EXPIRE_DURATION);
        query.whereGreaterThan(LAST_UPDATED_AT, Util.getAvailablePictureLastDate());
        query.include(PICTURE);
        query.whereEqualTo(TYPE,SEND_ALBUM_TYPE_VALUE);
        query.fromPin(ParseAPI.LABEL_ALBUM);
        query.findInBackground(findCallback);
    }

    //받은 사진 앨범 찾기
    //보낸 사진 앨범 찾기
    public static void findPinnedReceieAlbumInBackground(FindCallback<Album> findCallback)
    {
        /**
         * expire 되지 않은 보낸 사진 앨범 불러옴
         */
        ParseQuery<Album> query=ParseQuery.getQuery(Album.class);
        query.whereGreaterThan(LAST_UPDATED_AT, Util.getAvailablePictureLastDate());
        query.include(PICTURE);
        query.whereEqualTo(TYPE,RECEIVE_ALBUM_TYPE_VALUE);
        query.fromPin(ParseAPI.LABEL_ALBUM);
        query.findInBackground(findCallback);
    }

}

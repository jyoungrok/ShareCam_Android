package com.claude.sharecam.parse;

import android.content.Context;
import android.util.Log;

import com.claude.sharecam.Util;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import org.json.JSONObject;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * local/server 데이터
 * Parse server 데이터
 *
 * //서버 데이터
 * createdBy (String)
 * phone (String)
 * recordId(long)
 * syncUpdatedAt - 서버에서 수정 됨
 *
 * //로컬  데이터
 * contactName(String)
 * contactPhotoUri(String)
 *
 * Content provider에서 데이터 불러온 경우 getContactName 사용
 * Parse pinned data에서 불러온 경우 getPinContactName 사용
 *
 *
 *
 * 저장 시
 * Save -> Pin(로켈 데이터와 함께)
 */
@ParseClassName("Contact")
public class Contact extends ParseObject implements Serializable{


    //IndiviudlaFragment에서 사용 되는 임시 데이터
    public byte[]friendThumProfileBytes;



    //연락처 데이터
    public String contactName;//연락처 이름
    public String contactPhotoUri;//연락처 사진 경로

    /**
     * local data
     */


    public void setContactName(String name) {
        contactName=name;}
    public void setContactPhotoUri(String photoUri) { contactPhotoUri=photoUri;}

    public void setPinContactName(String name){  put("contactName",name);}
    public void setPinContactPhotoUri(String photoUri){
        if(photoUri!=null)
            put("contactPhotoUri",photoUri);
    else
            put("contactPhotoUri",JSONObject.NULL);}
    /**
     * variable 가져옴
     */
    public String getContactName(){ return contactName;}
    public String getContactPhotoUri(){return contactPhotoUri;}

    /**
     * pin되어진 정보 불러옴
     */
    public String getPinContactName(){ return getString("contactName");}
    public String getPinContactPhotoUri(){ return getString("contactPhotoUri");}


    /**
     * server data
     */

    public void setPhoneNumber(Context context,String phone) {put("phone", Util.convertToInternationalNumber(context,phone));}
    public void setRecordId(long recordId){put("recordId",recordId);}

//    public long getSyncUpdatedAt(){return getLong("syncUpdatedAt");}
    public long getRecordId() {return  getLong("recordId");}
    public String getPhone()
    {
        return getString("phone");
    }
    public User getFriendUser(){return (User) getParseUser("friendUser");}

//    public void setCreatedBy(ParseUser user)
//    {
//        put("createdBy",user);
//    }
//    public void setDeleted(boolean deleted) { put("deleted",deleted);}

    //    public void setPhone(String phone)
//    {
//        put("phone",phone);
//    }

    //an object may only be read or written by a single user
//    public void setACL(ParseUser user)
//    {
//        setACL(new ParseACL(user));
//
//    }
    public Contact()
    {

    }

//    public void copy(Contact contact)
//    {
////        setContactName(contact.getPinContactName());
////        setFriendUser(contact.getFriendUser());
////        setRecordId(contact.getRecordId());
//        put("phone", (contact.getPhone()));
////        if(contact.getPinContactPhotoUri()==null)
////            resetContactPhotoUri();
////        else
////            setContactPhotoUri(contact.getPinContactPhotoUri());
//    }

    //연락처 생성시
    // contactName contactPhotoUri는 put하지 않고 variable에만 설정
    public Contact(Context context,String contactName,String contactPhotoUri,String phone, long recordId)
    {

        setContactName(contactName);
//        if(contactPhotoUri==null)
//            resetContactPhotoUri();
//        else
        setContactPhotoUri(contactPhotoUri);
        setRecordId(recordId);
        setPhoneNumber(context,phone);
//        setIsAdded(false);
    }

//    public void deleteLocalData()
//    {
////        resetContactName();
////        resetContactPhotoUri();
////        resetIsAdded();
////        resetContactUpdatedAt();
//    }
//
//    public void addLocalData() throws ParseException {
//        ParseQuery<Contact> query=ParseQuery.getQuery(Contact.class);
//        query.fromPin(ParseAPI.LABEL_CONTACT);
//        query.getInBackground(this.getObjectId());
//        Contact originContact = query.getFirst();
//        if(originContact!=null)
//        {
////            setContactUpdatedAt(originContact.getcontactUpdatedAt());
////            setIsAdded(originContact.getIsAdded());
//            setContactName(originContact.getPinContactName());
//            setContactPhotoUri(originContact.getPinContactPhotoUri());
//        }
//    }

    public static void copyServerData(ParseObject newData,Contact origin)
    {
        newData.put("phone", origin.getPhone());
        newData.put("recordId",origin.getRecordId());
    }

    //서버 처음 초기화 시 호출
    /**
     * 서버에 데이터 업로드 시 로컬에만 저장되는 데이터 제거 후 업로드
     * syncUpdateAt 마지막 동기화 시간으로 설정
     */
    public static void saveInBackgroundWithSQLite(Context context, ArrayList<Contact> contactList, SCSaveCallback callback)
    {
        Log.d(ParseAPI.TAG, "saveInBackgroundWithSQLite");
////        long syncUpdatedAt=Util.getContactSyncTime(context);
////        for(int i=0; i<contactList.size(); i++)
////        {
////            contactList.get(i).deleteLocalData();
//////            contactList.get(i).setSyncUpdatedAt(syncUpdatedAt);
////        }
//        Log.d(ParseAPI.TAG, "delete local data done");


//        ((Util)context.getApplicationContext()).dbHelper.getContactDao().create()

        ParseObject.saveAllInBackground(contactList,callback);
    }

    public void saveInBackgroundWithoutLocalData(SCSaveCallback callback)
    {
        long syncUpdatedAt=new Date().getTime();
//        deleteLocalData();
//        setSyncUpdatedAt(syncUpdatedAt);
        this.saveInBackground(callback);
    }

    /**
     * contactName, contactPhotoUri를 함께
     */
    public static void pinAllInBackgroundWithLocalData(String LABEL,ArrayList<Contact> contactList,SCSaveCallback callback) {


//        for(int i=0; i<contactList.size(); i++)
//        {
//            contactList.get(i).addLocalData();
//        }

        /**
         * set data
         */
        for(int i=0; i<contactList.size(); i++)

        {
            contactList.get(i).setPinContactName(contactList.get(i).contactName);
            contactList.get(i).setPinContactPhotoUri(contactList.get(i).contactPhotoUri);

        }
        ParseObject.pinAllInBackground(LABEL,contactList,callback);

//        this.pinInBackground(callback);
    }

    public static void pinAllWithLocalData(String LABEL,ArrayList<Contact> contactList) {


//        for(int i=0; i<contactList.size(); i++)
//        {
//            contactList.get(i).addLocalData();
//        }

        /**
         * set data
         */
        for(int i=0; i<contactList.size(); i++)

        {
            contactList.get(i).setPinContactName(contactList.get(i).contactName);
            contactList.get(i).setPinContactPhotoUri(contactList.get(i).contactPhotoUri);

        }
        ParseObject.pinAllInBackground(LABEL,contactList);

//        this.pinInBackground(callback);
    }
    /**
     * 서버에서 데이터를 받아오는 경우 기존 저장되어있는 로컬 데이터와 함께 저장
     */
    public void pinInBackgroundWithOriginLocalData(SCSaveCallback callback) throws ParseException {

//        addLocalData();
        this.pinInBackground(callback);
    }
}

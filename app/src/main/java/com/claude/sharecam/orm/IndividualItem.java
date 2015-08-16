package com.claude.sharecam.orm;

import android.content.Context;
import android.net.Uri;

import com.claude.sharecam.Util;
import com.claude.sharecam.parse.SerializableParseFile;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.table.DatabaseTable;
import com.parse.ParseFile;
import com.parse.ParseObject;

import java.io.Serializable;
import java.sql.SQLException;

/**
 * Created by Claude on 15. 6. 4..
 */
@DatabaseTable(tableName = "IndividualItem")
public class IndividualItem  implements Serializable {

    @DatabaseField
    public boolean isShortCut;//shortcut을 위한 item인지 여부
    public static final int CONTACT=1;
    public static final int FRIEND=2;
    @DatabaseField(generatedId = true)
    public int id;
    @DatabaseField
    public String personName;
    @DatabaseField
    public String contactProfile;//contact profile 의 uri
//    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    public byte[] friendProfileBytes;//쉐어캠 친구 profile 이미지 파일
//    @DatabaseField(dataType = DataType.BYTE_ARRAY)
    public byte[] friendThumProfileBytes;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public SerializableParseFile serializableFriendProfileFile;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    public SerializableParseFile serializableFriendThumProfileFile;

//    public ParseFile friendProfileFile;
//    public ParseFile friendThumProfileFile;
    @DatabaseField
    public String phoneNumber;
    @DatabaseField
    public String objectId;//쉐어캠 친구의 경우
//    public int friendIndex;//쉐어캠 친구인 경우 friendsItems 의 index
    @DatabaseField
    public boolean added;
    @DatabaseField
    public int MODE;
//
//    public PersonItem(String personName, String contactProfile, String phoneNumber) {
//        this.personName = personName;
//        this.contactProfile = contactProfile;
//        this.phoneNumber=phoneNumber;
//        added=false;
//        isFriend=false;
//        MODE=CONTACT;
//    }


    public IndividualItem()
    {

    }
    //얀락처
    public IndividualItem(String personName, String contactProfile, String phoneNumber) {
        this.personName = personName;
        this.contactProfile = contactProfile;
        this.phoneNumber=phoneNumber;
        added=false;
//        isFriend=false;
        this.MODE=CONTACT;
    }
//    public PersonItem(String objectId,String personName, String contactProfile, String phoneNumber,int MODE) {
//        this.objectId=objectId;
//        this.personName = personName;
//        this.contactProfile = contactProfile;
//        this.phoneNumber=phoneNumber;
//        added=false;
//        isFriend=false;
//        this.MODE=CONTACT;
//
//    }

    //쉐어캠 친구
//    public IndividualItem(String objectId, String personName, byte[] friendProfile, String phoneNumber) {
//        this.objectId=objectId;
//        this.personName = personName;
////        this.contactProfile = contactProfile;
//        this.phoneNumber=phoneNumber;
//        added=false;
//        isFriend=false;
//        this.MODE=FRIEND;
//
//    }

//    public IndividualItem(String objectId, String personName,  ParseFile friendProfileFile, String phoneNumber) {
//
//    }
    //쉐어캠 친구
    public IndividualItem(String objectId, String personName, ParseFile friendThumProfileFile, ParseFile friendProfileFile, String phoneNumber) {
//        this.friendThumProfileFile=friendThumProfileFile;
        this.objectId=objectId;
        this.personName = personName;
        serializableFriendProfileFile=new SerializableParseFile(friendProfileFile);
        serializableFriendThumProfileFile=new SerializableParseFile(friendThumProfileFile);
//        this.friendProfileFile = friendProfileFile;
        this.phoneNumber=phoneNumber;
        added=false;
//        isFriend=true;
        this.MODE=FRIEND;
        isShortCut=false;

    }

    public void create(Context context) throws SQLException {
        ((Util) context.getApplicationContext()).dbHelper.getIndividualItemDao().create(this);
    }

    public String getInternationalPhoneNumber(Context context)
    {
        return Util.convertToInternationalNumber(context,phoneNumber);
    }
}

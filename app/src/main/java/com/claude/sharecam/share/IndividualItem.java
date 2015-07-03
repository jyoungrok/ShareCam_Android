package com.claude.sharecam.share;

import android.net.Uri;

/**
 * Created by Claude on 15. 6. 4..
 */
public class IndividualItem {

    public static final int CONTACT=1;
    public static final int FRIEND=2;
    public String personName;
    public String contactProfile;//contact profile 의 uri
    public byte[] friendProfile;//쉐어캠 친구 profile 이미지 파일
    public String phoneNumber;
    public String objectId;//쉐어캠 친구의 경우
    public boolean isFriend;//연락처의 경우 쉐어캠 친구인지 여부
    public int friendIndex;//쉐어캠 친구인 경우 friendsItems 의 index
    public boolean added;
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

    //얀락처
    public IndividualItem(String personName, String contactProfile, String phoneNumber) {
        this.personName = personName;
        this.contactProfile = contactProfile;
        this.phoneNumber=phoneNumber;
        added=false;
        isFriend=false;
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
    public IndividualItem(String objectId, String personName, byte[] friendProfile, String phoneNumber) {
        this.objectId=objectId;
        this.personName = personName;
        this.contactProfile = contactProfile;
        this.phoneNumber=phoneNumber;
        added=false;
        isFriend=false;
        this.MODE=FRIEND;

    }
}
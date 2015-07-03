package com.claude.sharecam.parse;

import android.content.Context;

import com.claude.sharecam.Util;
import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 *  공유 설정한 연락처 + 쉐어캠 친구  목록
 *
 *  연락처 ( name,phone,profile(uri) ) + isFriend = false
 *  쉐어캠 친구 ( objectId,name,phone,uprofile(url) ) + isFriend = true
 */
@ParseClassName("ShareContact")
public class SharePerson extends ParseObject{

    public SharePerson()
    {

    }

    public SharePerson(String objectId,String name, String phone, String uri, boolean isFriend)
    {
        if(objectId!=null)
            setFriendObjectId(objectId);
        if(name!=null)
            setName(name);
        if(phone!=null)
            setNationalPhone(phone);
        if(uri!=null)
            setProfileURI(uri);

        setIsFriend(isFriend);

    }

    public SharePerson(String name, String phone, String uri, boolean isFriend)
    {
        if(name!=null)
        setName(name);
        if(phone!=null)
        setNationalPhone(phone);
        if(uri!=null)
        setProfileURI(uri);

        setIsFriend(isFriend);
    }

    public String getFriendObjectId() {return getString("friendObjectId");}
    public String getNationalPhone()
    {
        return getString("phone");
    }
    public String getInternationPhone(Context context) { return Util.convertToInternationalNumber(context,getString("phone"));}
    public String getName()
    {
        return getString("name");
    }
    public Boolean getIsFriend() { return  getBoolean("isFriend");}
    public String getProfileUri()
    {
        return getString("profile");
    }


    public void setNationalPhone(String phone)
    {
        put("phone",phone);
    }
    public void setName(String name)
    {
        put("name",name);
    }
    public void setProfileURI(String uri)
    {
        put("profile", uri);
    }
    public void setIsFriend(Boolean isFriend) { put("isFriend",isFriend);}
    public void setFriendObjectId(String objectId){ put("friendObjectId",objectId);}


}

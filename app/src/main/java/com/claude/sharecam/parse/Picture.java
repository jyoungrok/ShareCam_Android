package com.claude.sharecam.parse;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Created by Claude on 15. 6. 11..
 */
@ParseClassName("Picture")
public class Picture extends ParseObject{

    public Picture()
    {
        //사진을 가진 object를 생성하였으나 사진을 서버에 등록 전
        put("hasPhoto",true);
        put("photoSynched",false);
    }
    public void setImage(ParseFile image){ put("image",image); }
    public void setCreatedBy(ParseUser user){ put("createdBy",user); }


    public void setPhone(String phone)
    {
        addUnique( "phoneList",phone);
    }

    public void setFriendId(String objectId)
    {
        addUnique("friendList",objectId);
    }
    public void setFriend(Friend friend)
    {
        put("friendList",friend);
    }

    public void setPhotoSynched()
    {
        put("photoSynched",true);
    }


}

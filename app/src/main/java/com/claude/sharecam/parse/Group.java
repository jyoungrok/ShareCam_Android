package com.claude.sharecam.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Claude on 15. 7. 19..
 */
@ParseClassName("Group")
public class Group extends ParseObject{
    public void setName(String name) {put("name",name);}
    public void setPhoneId(String phone)
    {
        addUnique( "phoneList",phone);
    }
    public void setFriendId(String friend)
    {
        addUnique("friendList",friend);
    }
    public void setCreatedBy(ParseUser user) { put("createdBy", user);}
    public String getName(){return getString("name");}
}

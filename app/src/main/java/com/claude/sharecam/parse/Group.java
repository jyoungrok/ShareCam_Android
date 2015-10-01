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
    public void setPhoneList(String phone)
    {
        addUnique("phoneList",phone);

    }
    public void setUserList(User user)
    {
        addUnique("userList",user);
    }
//    public void setPhoneId(String phone)
//    {
//        addUnique( "phoneList",phone);
//    }
//    public void setUserId(String friend)
//    {
//        addUnique("friendList",friend);
//    }
    public void setCreatedBy(ParseUser user) { put("createdBy", user);}
    public String getName(){return getString("name");}
}

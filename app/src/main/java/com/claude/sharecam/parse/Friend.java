package com.claude.sharecam.parse;

import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Claude on 15. 6. 8..
 */
@ParseClassName("Friend")
public class Friend extends ParseObject{

    public ParseUser getCreatedBy()
    {
        return getParseUser("createdBy");
    }

    public ParseUser getFriendUser()
    {
        return getParseUser("friendUser");
    }

    public void setCreatedBy(ParseUser user)
    {
        put("createdBy",user);
    }

    public void setFriendUser(String phone)
    {
        put("friendUser",phone);
    }

//    //an object may only be read or written by a single user
//    public void setACL(ParseUser user)
//    {
//        setACL(new ParseACL(user));
//
//    }
}

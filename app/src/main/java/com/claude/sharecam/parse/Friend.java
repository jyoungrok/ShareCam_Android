package com.claude.sharecam.parse;

import com.parse.ParseACL;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * Created by Claude on 15. 6. 8..
 */
@ParseClassName("Friend")
public class Friend extends ParseObject{

    public static final String CLASS_NAME="Friend";
//    public ParseUser getCreatedBy()
//    {
//        return getParseUser("createdBy");
//    }

    public ParseUser getFriendUser()
    {
        return getParseUser("friendUser");
    }

    public byte[] getProfile()
    {
        try {
            return getParseUser("friendUser").getParseFile("profile").getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
//    public void setCreatedBy(ParseUser user)
//    {
//        put("createdBy",user);
//    }

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

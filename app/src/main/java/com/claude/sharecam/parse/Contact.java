package com.claude.sharecam.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;
import com.parse.ParseUser;

/**
 * createdBy (String)
 * phone (String)
 */
@ParseClassName("Contact")
public class Contact extends ParseObject{

    public ParseUser getCreatedBy()
    {
        return getParseUser("createdBy");
    }

    public String getPhone()
    {
        return getString("phone");
    }
    public void setCreatedBy(ParseUser user)
    {
        put("createdBy",user);
    }

    public void setPhone(String phone)
    {
        put("phone",phone);
    }
}

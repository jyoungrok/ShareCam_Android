package com.claude.sharecam.parse;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseUser;

/**
 * Created by Claude on 15. 6. 9..
 */
@ParseClassName("_User")
public class User extends ParseUser {
    public byte[] getProfileFile() {
        try {
            return getParseFile("profile").getData();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }
    public String getProfileURL()
    {
        return getParseFile("profile").getUrl();
    }

    public String getPhone()
    {
        return getString("phone");
    }
}

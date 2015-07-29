package com.claude.sharecam.parse;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

/**
 * Created by Claude on 15. 6. 9..
 */
@ParseClassName("_User")
public class User extends ParseUser {
    public ParseFile getProfileFile() {

            return getParseFile("profile");

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

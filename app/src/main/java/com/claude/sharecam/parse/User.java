package com.claude.sharecam.parse;

import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.Serializable;

/**
 * Created by Claude on 15. 6. 9..
 */
@ParseClassName("_User")
public class User extends ParseUser implements Serializable {
    public ParseFile getThumProfileFile()
    {
        return getParseFile("thumProfile");
    }
    public ParseFile getProfileFile() {

            return getParseFile("profile");

    }
    public void setProfile(ParseFile file){put("profile",file);}
    public void setThumProfile(ParseFile file){put("thumProfile",file);}
    public void setPhone(String phone) {put("phone",phone);}
    public void setName(String name){put("name",name);}
    public void setCompleted(boolean completed){put("completed",completed);}

    public void removeProfile()
    {
        remove("profile");
        remove("thumProfile");
    }

    public String getNmae(){return getString("name");}
    public String getProfileURL()
    {
        return getParseFile("profile").getUrl();
    }

    public String getPhone()
    {
        return getString("phone");
    }


    public boolean getCompleted() { return getBoolean("completed");}
}

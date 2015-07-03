package com.claude.sharecam.parse;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Claude on 15. 6. 10..
 */
@ParseClassName("ShareFriend")
public class ShareFriend extends ParseObject{

    public ShareFriend()
    {

    }
    public ShareFriend(String name,String phone,String url)
    {
        if(name!=null)
            setName(name);
        if(phone!=null)
            setPhone(phone);
        if(url!=null)
            setProfileURL(url);
    }

    public String getPhone()
    {
        return getString("phone");
    }

    public String getName()
    {
        return getString("name");
    }

    public String getProfileUri()
    {
        return getString("profile");
    }
    public void setPhone(String phone)
    {
        put("phone",phone);
    }
    public void setName(String name)
    {
        put("name",name);
    }
    public void setProfileURL(String url)
    {
        put("profile", url);
    }

}

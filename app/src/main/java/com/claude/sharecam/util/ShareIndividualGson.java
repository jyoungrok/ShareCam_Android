package com.claude.sharecam.util;

import android.content.Context;

import com.claude.sharecam.Util;

/**
 * Created by Claude on 15. 7. 7..
 */
public class ShareIndividualGson {

    public String objectId;
    public String name;
    public String phone;
    public String uri;
    public boolean isFriend;

    public ShareIndividualGson()
    {

    }

    public ShareIndividualGson(String objectId, String name, String phone, String uri, boolean isFriend)
    {
        this.objectId=objectId;
        this.name=name;
        this.phone=phone;
        this.uri=uri;
        this.isFriend=isFriend;

    }

    public ShareIndividualGson(String name, String phone, String uri, boolean isFriend)
    {

        this.name=name;
        this.phone=phone;
        this.uri=uri;
        this.isFriend=isFriend;
    }

}

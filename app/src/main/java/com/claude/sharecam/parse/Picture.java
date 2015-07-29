package com.claude.sharecam.parse;

import android.util.Log;

import com.parse.ParseClassName;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Claude on 15. 6. 11..
 */
@ParseClassName("Picture")
public class Picture extends ParseObject implements Serializable{

    byte[] byteFile;

    public Picture()
    {
//        //사진을 가진 object를 생성하였으나 사진을 서버에 등록 전
//        put("hasPhoto",true);
//        put("photoSynched",false);

    }

    //사진 저장 전에 object만 저징 시 시도
    public void init()
    {
        put("hasPhoto",true);
        put("photoSynched",false);
    }


    public void setByteFile(byte[] byteFile){ this.byteFile=byteFile;}
    public void setImage(ParseFile image){ put("image",image); }
    public void setCreatedBy(ParseUser user){ put("createdBy",user); }
    public void setPhone(String phone)
    {
        addUnique( "phoneList",phone);
    }
    public void setFriendId(String objectId)
    {
        addUnique("friendList",objectId);
    }
    public void setFriend(Friend friend)
    {
        put("friendList",friend);
    }
    public void setPhotoSynched()
    {
        put("photoSynched",true);
    }

    public byte[] getByteFile() { return  byteFile;}
    public int getWidth(){ return getInt("width");}
    public int getHeight() { return getInt("height");}
    public String getImageURL() {return getParseFile("image").getUrl(); }
    public ParseFile getImageFile(){ return  getParseFile("image");}
    public String getThumImageURL(){ return getParseFile("thumImage").getUrl();}
    public ParseFile getThumImageFile(){ return getParseFile("thumImage");}
    public JSONArray getFriendList(){return getJSONArray("friendList");}
//    public Date getCreatedAt(){ return getDate("createdAt");}
    public String getCreatedAt_yyyyMMdd() {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(getCreatedAt());}

}

package com.claude.sharecam.response;

import com.google.gson.annotations.SerializedName;

import org.parceler.Parcel;

@Parcel
public class RestError
{

    @SerializedName("code")
    public int code;

    @SerializedName("error_message")
    public String strMessage;

    public int status;

    public RestError()
    {

    }
    public RestError(String strMessage)
    {
        this.strMessage = strMessage;
    }

    //Getters and setters
}
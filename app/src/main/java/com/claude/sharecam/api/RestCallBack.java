package com.claude.sharecam.api;

import com.claude.sharecam.response.RestError;

import retrofit.Callback;
import retrofit.RetrofitError;

public abstract class RestCallBack<T> implements Callback<T>
{



    public abstract void failure(RestError restError);

    @Override
    public void failure(RetrofitError error)
    {

        RestError restError = (RestError) error.getBodyAs(RestError.class);



        if (restError != null) {
            if(error.getResponse()!=null)
                restError.status=error.getResponse().getStatus();
            failure(restError);
        }
        else
        {


            restError=new RestError(error.getMessage());

            if(error.getResponse()!=null)
                restError.status=error.getResponse().getStatus();

            failure(restError);
        }
    }
}
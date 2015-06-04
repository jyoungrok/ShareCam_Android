package com.claude.sharecam.api;

import android.content.Context;
import android.util.Log;

import com.claude.sharecam.Constants;
import com.claude.sharecam.Util;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.CookieHandler;
import java.net.CookiePolicy;
import java.net.HttpCookie;
import java.net.URI;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import retrofit.android.AndroidLog;
import retrofit.client.OkClient;
import retrofit.converter.GsonConverter;
import com.claude.sharecam.api.MyCookieManager;
import com.squareup.okhttp.OkHttpClient;

/**
 * Created by Claude on 15. 5. 7..
 */
public class ApiManager {
    private static ApiManager instance;
    public MyCookieManager cookieManager;
    public RestAdapter restAdapter;
    public Server server;


    public static ApiManager newInstance(Context context){
        if (instance == null) {
            instance = new ApiManager(context);
        }
        return instance;
    }

    public ApiManager(final Context context){

        cookieManager = new MyCookieManager();
        CookieHandler.setDefault(cookieManager);

//        Gson gson = new GsonBuilder()
//                .setDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss'.'SSS'Z'")
//                .create();

        restAdapter = new RestAdapter.Builder()
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .setEndpoint(Constants.SERVER_URL)
                .setClient(new OkClient(MySSLTrust.trustcert(context)))
                .setRequestInterceptor(new RequestInterceptor() {
                    @Override
                    public void intercept(RequestFacade request) {

//                        request.addHeader("Cookie", ((Util)context.getApplicationContext()).pref.getString(Constants.COOK));

                        Log.d("jyr","cookie="+cookieManager.getCurrentCookie() );
                        request.addHeader("Cookie", cookieManager.getCurrentCookie());
                    }
                })
                .setLog(new AndroidLog("RETROFIT"))
                .build();

        server = restAdapter.create(Server.class);
    }
}

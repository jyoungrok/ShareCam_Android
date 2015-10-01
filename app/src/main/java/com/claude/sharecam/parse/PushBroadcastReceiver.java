package com.claude.sharecam.parse;

import android.app.Notification;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.claude.sharecam.Util;
import com.claude.sharecam.util.NotificationUtils;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParsePushBroadcastReceiver;
import com.parse.ParseQuery;

import org.json.JSONException;
import org.json.JSONObject;

/**
 *
 * push notification 받았을 떄 이를 handling
 *
 *

 *
 *
 */
public class PushBroadcastReceiver extends ParsePushBroadcastReceiver {

    public static final String TAG = "PushBroadcastReceiver";
    private NotificationUtils notificationUtils;


    //push notification
    public static final String DATA="data";
    public static final String TYPE="type";//type of push notification
    public static final int PICTURE_TYPE=0;
    public static final String PICTURE="picture";

    @Override
    protected Notification getNotification(Context context, Intent intent) {
        // deactivate standard notification
        return null;
    }
    /**
     *A notification is received
     */
    @Override
    protected void onPushReceive(Context context, Intent intent) {

        Log.d(TAG,"push receive");

        try {
            handleReceivedPush(context, intent);
//            getNotification(context,receiveData);
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    /**
     * If the user taps on a Notification
     */
    @Override
    protected void onPushOpen(Context context, Intent intent) {
        super.onPushOpen(context, intent);
    }

    @Override
    protected void onPushDismiss(Context context, Intent intent) {
        super.onPushDismiss(context, intent);
    }


    /**
     *
     *  < push data 종류 >
     *  1. 사진을 받은 경우
     *      - Server로 부터 다시 Picture object를 받아옴
     *      (2015.9.10 / Parse에서의 문제점 같이 위와 같이 구현 (가능하다면 다시 요청 보낼 필요가 사실 없다.))
     */
    private void handleReceivedPush(final Context context,Intent intent) throws JSONException {
        Log.d(TAG,"handleReceivedPush");
        String title;
        String message;
        JSONObject receiveData = new JSONObject(intent.getExtras().getString("com.parse.Data"));
        switch(receiveData.getInt(TYPE))
        {
            //사진을 받은 경우
            case PICTURE_TYPE:
                /**
                 * 데이터를 서버에서 부터
                 */
                ParseQuery<Picture> query=ParseQuery.getQuery(Picture.class);
                query.include(Picture.CREATED_BY);
                query.getInBackground(receiveData.getJSONObject(DATA).getString("objectId"),new GetCallback<Picture>() {
                    @Override
                    public void done(Picture picture, ParseException e) {
                        if (e == null && picture != null) {
                            Log.d(TAG, "fetch done");
                            try {
                                //pin 받은 Picture & Album 갱신 -> Notification(알람이 설정되어있는경우)
                                picture.handleReceived(context,new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        super.handleMessage(msg);
                                        Log.d(TAG, "picture / album/ notification pinned and notification send done");
                                    }
                                });

                            } catch (JSONException e1) {
                                e1.printStackTrace();
                            }
                        } else
                            ParseAPI.erroHandling(context, e);
                    }
                });

                break;
        }


    }

    /**
     * Shows the notification message in the notification bar
     * If the app is in background, launches the app
     *
     * @param context
     * @param title
     * @param message
     * @param intent
     */
    private void showNotificationMessage(Context context, String title, String message, Intent intent) {

        Log.d(TAG,"showNotificationMessage");
        notificationUtils = new NotificationUtils(context);

//        intent.putExtras(parseIntent.getExtras());

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        notificationUtils.showNotificationMessage(title, message, intent);
    }
}

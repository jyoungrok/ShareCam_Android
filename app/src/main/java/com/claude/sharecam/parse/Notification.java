package com.claude.sharecam.parse;

import android.app.ActivityManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.RemoteViews;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.main.AlbumActivity;
import com.claude.sharecam.upload.UploadActivity;
import com.claude.sharecam.util.ImageManipulate;
import com.parse.GetDataCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

import java.util.Date;
import java.util.List;

/**
 * 알림 받은 것들
 */
@ParseClassName("Notification")
public class Notification extends ParseObject {

    public byte[] profileBytes;
    public byte[] imageBytes;


    public static final String TAG="Notification";


    public static final String IS_NEW="isNew";
    public static final String TYPE="type";//notification type
    public static final String DATE="date";
    public static final String SEND_USER="sendUser";//notification 보낸 사람(user)의 objectId
    public static final String CONTENT="content";//notification 내용
    public static final String PICTURE="picture";//picture notification인 경우 picture의 objectId
    public static final String ALBUM="album";

    public static final int PICTURE_TYPE=0;

    public void setIsNew(boolean isNew){put(IS_NEW,isNew);}
    public void setAlbum(Album album){put(ALBUM,album);}
    public void setType(int type){put(TYPE,type);}
    public void setDate(Date date){put(DATE,date);}
    public void setSendUser(User user){put(SEND_USER,user);}
    public void setContent(String content){put(CONTENT,content);}
    public void setContent(Context context)
    {
        String content="";
        switch (getType())
        {
            case PICTURE_TYPE:
                content+=Util.getContactNameByPhone(getSendUser().getPhone())+context.getString(R.string.notification_got_picture);
                break;
        }

        put(CONTENT,content);
    }

    public void setPictrue(Picture pictrue){put(PICTURE,pictrue);}

    public int getType(){return getInt(TYPE);}
    public Date getDate(){return getDate(DATE);}
    public User getSendUser() {return (User) get(SEND_USER);}
    public String getContent(){return getString(CONTENT);}
    public Picture getPicture(){return (Picture) get(PICTURE);}
    public Album getAlbum(){return (Album) get(ALBUM);}
    public boolean getIsNew(){return getBoolean(IS_NEW);}


    //Pin 하고 notification 보내기 (설정에 따라 notify)
    public void pinAndNotifyInBackground(final String albumId,final Context context, final Handler handler)
    {
        Log.d(ParseAPI.TAG, "pinAndNotifyInBackground");
        //pin notification
        pinInBackground(ParseAPI.LABEL_NOTIFICATION, new SCSaveCallback(context, new SCSaveCallback.Callback() {
            @Override
            public void done() {
                //알람 설정된 경우 notification 전송
                if (Util.getAlarmSet(context)) {
                    Log.d(ParseAPI.TAG, "send received picture notification ");

                    //Notification 보내기
                    showPictureNotification(albumId,context, new Handler() {
                        @Override
                        public void handleMessage(Message msg) {
                            super.handleMessage(msg);

                            Log.d(ParseAPI.TAG, "show notification done");
                            //알림 설정에 따라 진동 알림
                            switch (Util.getAlarmMode(context)) {
                                case Constants.PREF_ALARM_MODE_SOUND_AND_VIBRATION:
                                case Constants.PREF_ALARM_MODE_VIBRATE:
                                    AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
                                    if (audioManager.getRingerMode() == AudioManager.RINGER_MODE_SILENT) {

                                    } else {
                                        Vibrator vibe = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
                                        vibe.vibrate(500);
                                    }
                                    break;


                                case Constants.PREF_ALARM_MODE_MUTE:
                                    break;
                            }

                            //notification 처리 완료
                            handler.sendEmptyMessage(0);
                        }
                    });


                } else {

                }


            }
        }));
    }

    /**
     * notify notification (사진 받았을 때 notification)
     *
     */

    private void showPictureNotification(final String albumId,final Context context, final Handler handler)
    {


//        if(Util.isAppIsInBackground(context)) {
            Log.d(TAG,"show picture notification cuz app is background");
            String senderName = Util.getContactNameByPhone(getSendUser().getPhone());
            final String notificationContent = senderName + context.getString(R.string.notification_got_picture);

            final RemoteViews remoteViews = new RemoteViews(context.getPackageName(),
                    R.layout.normal_notification);

            remoteViews.setTextViewText(R.id.titleText, senderName);
            remoteViews.setTextViewText(R.id.contentText, notificationContent);
            remoteViews.setTextViewText(R.id.dateText, Util.getNotificationDateStr(getDate()));
            //사진 이미지 받아온 후에 notification
            getPicture().getThumImageFile().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    remoteViews.setImageViewBitmap(R.id.representativeImg, ImageManipulate.byteArrayToBitmap(bytes));
                    remoteViews.setImageViewResource(R.id.smallIcon, R.mipmap.ic_action_accept);


                    //        remoteViews.setImageViewBitmap(R.id.representativeImg,);

                    NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
                    builder.setSmallIcon(R.drawable.ic_launcher)
                            .setContentTitle(notificationContent)
                            .setTicker(notificationContent)
                            .setWhen(System.currentTimeMillis());


                    Intent startIntent = new Intent(context.getApplicationContext(),
                            AlbumActivity.class);
//                    startIntent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);

                    startIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                    startIntent.putExtra(AlbumActivity.GO_TO_ALBUM, true);
                    startIntent.putExtra(AlbumActivity.ALBUM_ID,albumId);

                    PendingIntent contentIntent = PendingIntent.getActivity(
                            context, UploadActivity.SERVICE_REQUEST_CODE, startIntent, PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(contentIntent);
                    builder.setContent(remoteViews);
                    builder.setAutoCancel(true);
                    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
                    notificationManager.notify(Constants.RECEIVED_PICTURE_NOTIFICATION_ID, builder.build());

                    handler.sendEmptyMessage(0);
                }
            });
//        }
//        else{
//            Log.d(TAG,"App is not in background");
//        }
    }





}

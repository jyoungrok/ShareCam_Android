package com.claude.sharecam.api;

import android.content.Context;
import android.graphics.Bitmap;
import android.widget.Toast;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.services.s3.AmazonS3Client;
import com.claude.sharecam.Constants;
import com.claude.sharecam.Util;
import com.claude.sharecam.camera.ImageManipulate;

import java.io.File;
import java.net.CookieHandler;
import java.util.Calendar;

import retrofit.RequestInterceptor;
import retrofit.RestAdapter;

/**
 * Created by Claude on 15. 5. 14..
 */
public class S3 {

    public static final String PUBLIC_BUCKET_NAME="sharecampublic";
    public static final String PROFILE_FOLRDER_NAME="profile";

    public static S3 instance;
    BasicSessionCredentials basicSessionCredentials;
    AmazonS3Client s3Client;
    TransferManager transferManager;


    public S3(){


    }


    //이미 init되어 있는경우는 바로 true return
    //credential setting 및 S3요청 준비 -> 권한 설정이 안되어 있을 경우 false return
    public boolean init(Context context)
    {

        //이미 세팅이 되어 있는 경우
        if(basicSessionCredentials!=null && basicSessionCredentials.getAWSAccessKeyId()!=null)
        {
            return true;
        }

        Calendar calendar=Calendar.getInstance();

        //federation token을 서버로 부터 잘 받아왔는지 확인
        String expiredDate=((Util)context.getApplicationContext()).pref.getString(Constants.PREF_FEDERATION_EXPIRATION,null);
        //expire되지 않은 경우
        if(expiredDate!=null && Util.StringToCalendar(expiredDate).getTimeInMillis()>calendar.getTimeInMillis()) {

            // for an S3 client object to use.
            BasicSessionCredentials basicSessionCredentials = new BasicSessionCredentials(
                    ((Util)context.getApplicationContext()).pref.getString(Constants.PREF_FEDERATION_ACCESS_KEY,null),
                    ((Util)context.getApplicationContext()).pref.getString(Constants.PREF_FEDERATION_SECRET_ACCESS_KEY,null),
                    ((Util)context.getApplicationContext()).pref.getString(Constants.PREF_FEDERATION_SESSION_TOKEN,null));

            s3Client=new AmazonS3Client(basicSessionCredentials);

            transferManager=new TransferManager(s3Client);


            return true;
        }
        else
            return false;
    }

    public void uploadProfileImage(final File bitmap, final int user_id)
    {

//        final Bitmap uploadBitmap = Bitmap.createScaledBitmap(bitmap, Constants.PROFILE_WIDTH, Constants.PROFILE_HEIGHT, true);

        new Runnable(){

            @Override
            public void run() {
                Upload upload=transferManager.upload(PUBLIC_BUCKET_NAME,PROFILE_FOLRDER_NAME+user_id, bitmap);

                while (!upload.isDone()){


                }
            }
        }.run();


    }
    /*
    public void uploadProfileImage(final Bitmap bitmap, final int user_id)
    {

        final Bitmap uploadBitmap = Bitmap.createScaledBitmap(bitmap, Constants.PROFILE_WIDTH, Constants.PROFILE_HEIGHT, true);

        new Runnable(){

            @Override
            public void run() {
                Upload upload=transferManager.upload(PUBLIC_BUCKET_NAME,PROFILE_FOLRDER_NAME+"/"+user_id, ImageManipulate.bitmapToFileCache(uploadBitmap,"profile"));

                while (!upload.isDone()){

                }
            }
        }.run();


    }
*/



}

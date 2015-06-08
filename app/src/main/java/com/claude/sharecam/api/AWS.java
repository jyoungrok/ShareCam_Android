package com.claude.sharecam.api;


import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;

import com.amazonaws.auth.BasicSessionCredentials;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.mobileconnectors.s3.transfermanager.TransferManager;
import com.amazonaws.mobileconnectors.s3.transfermanager.Upload;
import com.amazonaws.mobileconnectors.s3.transfermanager.model.UploadResult;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.AccessControlList;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.claude.sharecam.Constants;
import com.claude.sharecam.Util;
import com.kbeanie.imagechooser.api.ChosenImage;


import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.Calendar;

/*
 * This class just handles getting the client since we don't need to have more than
 * one per application
 */
public class AWS {


    public static final String PUBLIC_BUCKET_NAME="sharecampublic";
    public static final String PROFILE_FOLRDER_NAME="profile";

    BasicSessionCredentials basicSessionCredentials;
    TransferManager transferManager;

    private AmazonS3Client s3Client;



    public AmazonS3Client getS3Client() {

        return s3Client;
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

           SharedPreferences pref= ((Util)context.getApplicationContext()).pref;
            // for an S3 client object to use.
            BasicSessionCredentials basicSessionCredentials = new BasicSessionCredentials(
                   pref.getString(Constants.PREF_FEDERATION_ACCESS_KEY,null),
                   pref.getString(Constants.PREF_FEDERATION_SECRET_ACCESS_KEY,null),
                pref.getString(Constants.PREF_FEDERATION_SESSION_TOKEN,null));

            s3Client=new AmazonS3Client(basicSessionCredentials);

            transferManager=new TransferManager(s3Client);


            new Thread(){
                @Override
              public void run(){
//                    ObjectListing objects = s3Client.listObjects(PUBLIC_BUCKET_NAME);
//
//                    Log.d("jyr",""+objects.getMaxKeys());
                    S3Object object=s3Client.getObject(new GetObjectRequest(PUBLIC_BUCKET_NAME, "profile.png"));
                    Log.d("jyr", "object=" + object.toString());


                    ;
                    ;
                    ;
                }
            }.start();

            return true;
        }
        else
            return false;
    }

    /**
     * init() == true인 경우 사용 가능
     *
     * 프로필 이미지 등록
     */
    public void uploadProfileImage(Context context, final ChosenImage chosenImage, final int user_id, final Handler handler)
    {
        //네트워크 연결 체크
        if(!Util.checkNetwork(context))
            return;

        if(init(context))
        {
//            final ArrayList<UploadModel> modelList=new ArrayList<UploadModel>();
//            modelList.add(new UploadModel(context,PUBLIC_BUCKET_NAME,PROFILE_FOLRDER_NAME+"/"+user_id,chosenImage,transferManager));


            new Thread(){
                @Override
                public void run()
                {
                    //S3에 업로드
//                    Upload.upload(modelList);//보험 컨설팅

                    try {



//                        Upload upload=transferManager.upload(PUBLIC_BUCKET_NAME, PROFILE_FOLRDER_NAME + "/" + user_id + "." + chosenImage.getExtension(), new File(chosenImage.getFileThumbnailSmall()));

                       //public 으로 올림
                        PutObjectRequest putObjectRequest=new PutObjectRequest(PUBLIC_BUCKET_NAME, PROFILE_FOLRDER_NAME + "/" + user_id + "." + chosenImage.getExtension(), new File(chosenImage.getFileThumbnailSmall()));
                        putObjectRequest.setCannedAcl(CannedAccessControlList.PublicRead);
                        Upload upload=transferManager.upload(putObjectRequest);

                        // Or you can block and wait for the upload to finish
                        upload.waitForCompletion();

                        handler.sendEmptyMessage(0);


                    }catch (Exception e) {
                        Log.e("jyr", "", e);
                    }


                }
            }.start();


        }

    }

    public static String getPrefix(Context context) {
        return "1" + "/";
    }


    public static String getFileName(String path) {
        return path.substring(path.lastIndexOf("/") + 1);
    }

}

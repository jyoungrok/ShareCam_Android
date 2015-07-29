//package com.claude.sharecam.parse;
//
//import android.content.Context;
//import android.content.Intent;
//
//import com.claude.sharecam.service.UploadService;
//import com.parse.ParseClassName;
//import com.parse.ParseObject;
//
///**
// * Created by Claude on 15. 7. 10..
// */
//@ParseClassName("UploadingPicture")
//public class UploadingPicture extends ParseObject{
//
//    //upload
//    public static final int FAILED_UPLOADING_STATE=-1;
//    public static final int STANDBY_UPLOADING_STATE=0;
//    public static final int UPLOADING_STATE=1;
//    public static final int SUCCESS_UPLOADING_STATE=2;
//    public static final int FINISHED_UPLOADING_STATE=3;//업로드 완료 한 후 사용자가 지운 경우
//
//
//    public UploadingPicture()
//    {
//
//    }
//
//    public void init(String filePath)
//    {
//        put("state",STANDBY_UPLOADING_STATE);
//        put("percent",0);
//        setFilePath(filePath);
//    }
////    public UploadingPicture(String pictureId){
////        put("pictureId",pictureId);
////        put("state",STANDBY_UPLOADING_STATE);
////        put("percent",0);
////
////    }
//
//
//    public void setFilePath(String path){ put("filePath",path);}
//
//    public void setPictureId(String pictureId)
//    {
//        put("pictureId",pictureId);
//    }
//
//    public void setState(int state)
//    {
//        put("state",state);
//    }
//
//    public void setPercent(int percent)
//    {
//        put("percent",percent);
//    }
//
//    public String getPictureId()
//    {
//        return getString("pictureId");
//    }
//
//    public int getState()
//    {
//        return getInt("state");
//    }
//
//    public int getPercent()
//    {
//        return getInt("percent");
//    }
//
//    public String getFilePath(){ return getString("filePath");}
//
//    public void sendStateBroadCast(Context context)
//    {
//        Intent broadcastIntent = new Intent();
//        broadcastIntent.setAction(UploadService.broadcastStateAction);
//        broadcastIntent.putExtra(UploadService.STATE,getState());
//        context.sendBroadcast(broadcastIntent);
//    }
//}

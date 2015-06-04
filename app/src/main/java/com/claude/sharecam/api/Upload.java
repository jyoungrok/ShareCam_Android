//package com.claude.sharecam.api;
//
//
//import android.util.Log;
//
//import java.util.ArrayList;
//
///**
// * Created by Claude on 15. 4. 16..
// */
//public class Upload {
//
//    public static void upload(ArrayList<UploadModel> modelList)
//    {
//        for(int i=0; i<modelList.size();i++) {
//            if (modelList.get(i).mFile == null) {
//                modelList.get(i).saveTempFile();
//            }
//            if (modelList.get(i).mFile != null) {
//                try {
//                    modelList.get(i).mUpload = modelList.get(i).getTransferManager().upload(
//                            modelList.get(i).bucketName,
//                            modelList.get(i).key+ "."
//                                    + modelList.get(i).mExtension,
//                            modelList.get(i).mFile);
//                } catch (Exception e) {
//                                    Log.e("jyr", "", e);
//                }
//
//            }
//        }
//    }
//}

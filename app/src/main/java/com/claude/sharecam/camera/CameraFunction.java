package com.claude.sharecam.camera;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Environment;
import android.util.Log;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.util.ImageManipulate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Claude on 15. 4. 29..
 */
public class CameraFunction {


    public static int findFrontFacingCamera() {
        int cameraId = -1;
        // Search for the front facing camera
        int numberOfCameras = Camera.getNumberOfCameras();
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
                cameraId = i;
//                cameraSetting.setCameraFront(true);
                break;
            }
        }
        return cameraId;
    }

    public static int findBackFacingCamera() {
        int cameraId = -1;
        //Search for the back facing camera
        //get the number of cameras
        int numberOfCameras = Camera.getNumberOfCameras();
        //for every camera check
        for (int i = 0; i < numberOfCameras; i++) {
            Camera.CameraInfo info = new Camera.CameraInfo();
            Camera.getCameraInfo(i, info);
            if (info.facing == Camera.CameraInfo.CAMERA_FACING_BACK) {
                cameraId = i;
//                cameraSetting.setCameraFront(false);
                break;
            }
        }
        return cameraId;
    }
    public static boolean hasCamera(Context context) {
        //check if the device has camera
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            return true;
        } else {
            return false;
        }
    }


    public static File getOutputMediaFile() {
        //make a new file directory inside the "sdcard" folder
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), Constants.SD_CARD_FOLDER_NAME);

        //if this "camera folder does not exist
        if (!mediaStorageDir.exists()) {
            //if you cannot make this folder return
            if (!mediaStorageDir.mkdirs()) {
                Log.d("jyr", "failed to create directory");
                return null;
            }
        }

        //take the current timeStamp
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        //and make a media file:
        mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");

        return mediaFile;
    }

    //사진 저장
    public static String savePicture(Activity activity,CameraSetting cameraSetting,byte[] data,int orientation)
    {
        //make a new PICTURE file
        File pictureFile = CameraFunction.getOutputMediaFile();

        if (pictureFile == null) {
            return null;
        }
        try {
            //write the file
            FileOutputStream fos = new FileOutputStream(pictureFile);

            byte[] file = ImageManipulate.getImage_byte(activity, cameraSetting.ratio, data, R.mipmap.logo, ImageManipulate.getRotate(orientation, cameraSetting.cameraFront));
            //이미지에 로고 추가한 후 저장
            fos.write(file);
            fos.close();
//                    Toast toast = Toast.makeText(activity, "Picture saved: " + pictureFile.getAbsolutePath(), Toast.LENGTH_LONG);
//                    toast.show();

            //add image to gallery
            ImageManipulate.galleryAddPic(activity, pictureFile.getAbsolutePath());



        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        return pictureFile.getAbsolutePath();
    }

    //연속 촬영 사진 저장
    public static String saveContPictrue(Activity activity,CameraSetting cameraSetting,ArrayList<byte[]> dataItem,int orientation)
    {
        //make a new PICTURE file
        File pictureFile = CameraFunction.getOutputMediaFile();

        if (pictureFile == null) {
            return null;
        }
        try {
            //write the file
            FileOutputStream fos = new FileOutputStream(pictureFile);

            //이미지에 로고 추가한 후 저장
            fos.write(ImageManipulate.getContImage_byte(activity, cameraSetting.cont, cameraSetting.ratio, dataItem, R.mipmap.logo, ImageManipulate.getRotate(orientation, cameraSetting.cameraFront)));
            fos.close();
//                    Toast toast = Toast.makeText(activity, "Picture saved: " + pictureFile.getAbsolutePath(), Toast.LENGTH_LONG);
//                    toast.show();

            //add image to gallery
            ImageManipulate.galleryAddPic(activity, pictureFile.getAbsolutePath());



        } catch (FileNotFoundException e) {
        } catch (IOException e) {
        }

        return pictureFile.getAbsolutePath();
    }
}

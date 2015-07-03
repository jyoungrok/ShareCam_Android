package com.claude.sharecam.camera;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;
import android.view.Surface;
import android.view.WindowManager;

import com.claude.sharecam.Constants;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;

/**
 * Created by Claude on 15. 4. 29..
 */
public class ImageManipulate {

    //사진 회전 각도 구함
    //@param rotate - 사진 찍을 다시 화면 각도
    public static int getRotate(int rotate, boolean cameraFront)
    {
        if(cameraFront)
        {
            if(rotate==90 || rotate==270)
            {
                return (rotate+90)%360;
            }
            else{
                return (rotate+270)%360;
            }
        }
        else
        {
            return (rotate+90)%360;
        }
    }


    //3:4 Bitmap 이미지를 1:1Bitmap으로 변환
    public static Bitmap covertRatio34To11(Bitmap bitmap)
    {

        return Bitmap.createBitmap(bitmap, 0,(bitmap.getHeight()-bitmap.getWidth())/2,bitmap.getWidth(), bitmap.getWidth());
    }

    public static Bitmap convertToHorizontalHalf(Bitmap bitmap)
    {
        return Bitmap.createBitmap(bitmap, bitmap.getWidth()/4,0,bitmap.getWidth()/2, bitmap.getHeight());
    }

    public static Bitmap convertToHorizontalTwoThird(Bitmap bitmap)
    {
        return Bitmap.createBitmap(bitmap, bitmap.getWidth()/3,0,bitmap.getWidth()/3, bitmap.getHeight());
    }

    public static Bitmap rotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }



    public static byte[] bitmapToByteArray( Bitmap $bitmap ) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        $bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream) ;
        byte[] byteArray = stream.toByteArray() ;
        return byteArray ;
    }

    public static Bitmap byteArrayToBitmap( byte[] $byteArray ) {
        Bitmap bitmap = BitmapFactory.decodeByteArray($byteArray, 0, $byteArray.length) ;
        return bitmap ;
    }


    //이미지 2장 이어 붙이기
    //isVerticalMode = true -> 세로 / false - 가로
    public static  Bitmap combineImage(Bitmap first, Bitmap second, boolean isVerticalMode){
        BitmapFactory.Options option =new BitmapFactory.Options();
        option.inDither = true;
        option.inPurgeable = true;

        Bitmap bitmap = null;
        if(isVerticalMode)
            bitmap = Bitmap.createScaledBitmap(first, first.getWidth(), first.getHeight()+second.getHeight(), true);
        else
            bitmap = Bitmap.createScaledBitmap(first, first.getWidth()+second.getWidth(), first.getHeight(), true);

        Paint p = new Paint();
        p.setDither(true);
        p.setFlags(Paint.ANTI_ALIAS_FLAG);

        Canvas c = new Canvas(bitmap);
        c.drawBitmap(first, 0, 0, p);
        if(isVerticalMode)
            c.drawBitmap(second, 0, first.getHeight(), p);
        else
            c.drawBitmap(second, first.getWidth(), 0, p);

        first.recycle();
        second.recycle();

        return bitmap;
    }



    public static void putLogo(Bitmap bitmap, Bitmap overlay) {
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.FILTER_BITMAP_FLAG);
        canvas.drawBitmap(overlay, bitmap.getWidth() - overlay.getWidth(), bitmap.getHeight() - overlay.getHeight(), paint);
    }

    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        // We ask for the bounds if they have been set as they would be most
        // correct, then we check we are  > 0
        final int width = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().width() : drawable.getIntrinsicWidth();

        final int height = !drawable.getBounds().isEmpty() ?
                drawable.getBounds().height() : drawable.getIntrinsicHeight();

        // Now we check we are > 0
        final Bitmap bitmap = Bitmap.createBitmap(width <= 0 ? 1 : width, height <= 0 ? 1 : height,
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap convertToMutable(Bitmap imgIn) {
        try {
            //this is the file going to use temporally to save the bytes.
            // This file will not be a image, it will store the raw image data.
            File file = new File(Environment.getExternalStorageDirectory() + File.separator + "temp.tmp");

            //Open an RandomAccessFile
            //Make sure you have added uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"
            //into AndroidManifest.xml file
            RandomAccessFile randomAccessFile = new RandomAccessFile(file, "rw");

            // get the width and height of the source bitmap.
            int width = imgIn.getWidth();
            int height = imgIn.getHeight();
            Bitmap.Config type = imgIn.getConfig();

            //Copy the byte to the file
            //Assume source bitmap loaded using options.inPreferredConfig = Config.ARGB_8888;
            FileChannel channel = randomAccessFile.getChannel();
            MappedByteBuffer map = channel.map(FileChannel.MapMode.READ_WRITE, 0, imgIn.getRowBytes()*height);
            imgIn.copyPixelsToBuffer(map);
            //recycle the source bitmap, this will be no longer used.
            imgIn.recycle();
            System.gc();// try to force the bytes from the imgIn to be released

            //Create a new bitmap to load the bitmap again. Probably the memory will be available.
            imgIn = Bitmap.createBitmap(width, height, type);
            map.position(0);
            //load it back from temporary
            imgIn.copyPixelsFromBuffer(map);
            //close the temporary file and channel , then delete that also
            channel.close();
            randomAccessFile.close();

            // delete the temp file
            file.delete();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return imgIn;
    }


    public static Bitmap getRotatedImage(Activity activity,Bitmap bitmap)
    {


        int rotation = activity.getWindowManager().getDefaultDisplay()
                .getRotation();

        rotation=((WindowManager)activity.getSystemService(Activity.WINDOW_SERVICE)).getDefaultDisplay().getRotation();

        //각 rotation에 따라 이미지 회전하여 저장
        switch(rotation)
        {
            case Surface.ROTATION_0:

                Log.d("jyr", "rotate0");
                return rotateBitmap(bitmap, 270);

            case Surface.ROTATION_90:
                Log.d("jyr", "rotate90");
                return  rotateBitmap(bitmap, 270);

            case Surface.ROTATION_180:
                Log.d("jyr", "rotate180");
                return  rotateBitmap(bitmap, 270);
            case Surface.ROTATION_270:
                Log.d("jyr", "rotate270");
                return rotateBitmap(bitmap, 270);
            default:
                return rotateBitmap(bitmap, 270);
        }


    }


    //이미지 회전 후 로고 추가 하여 리턴
    //ratio에 따라서
    public static byte[] getImage_byte(Activity activity, int ratio, byte[] data, int logoId, int rotate)
    {
        Log.d("jyr","rotate="+rotate);
        Bitmap bitmap=rotateBitmap(convertToMutable(byteArrayToBitmap(data)), rotate);

        //1:1비율의 경우 3:4이미지에서 일부 자름
        if(ratio== Constants.PREF_RATIO_11)
        {
            bitmap=covertRatio34To11(bitmap);
        }

        Bitmap logo=drawableToBitmap(activity.getResources().getDrawable(logoId));

        putLogo(bitmap, logo);

        return bitmapToByteArray(bitmap);

    }

    public static byte[] getContImage_byte(Activity activity,int cont, int ratio, ArrayList<byte[]> dataItem, int logoId, int rotate)
    {
        Log.d("jyr","rotate="+rotate);

        ArrayList<Bitmap> bitmapItem=new ArrayList<Bitmap>();

        for(int i=0; i<dataItem.size(); i++)
        {
            bitmapItem.add(rotateBitmap(convertToMutable(byteArrayToBitmap(dataItem.get(i))), rotate));
        }

        Bitmap result=null;


        switch(cont)
        {
            default:
            case Constants.PREF_CONT_2_VERTICAL:
                for(int i=0; i<bitmapItem.size(); i++)
                {
                    bitmapItem.set(i, convertToHorizontalHalf(bitmapItem.get(i)));
                }
                result=combineImage(bitmapItem.get(0),bitmapItem.get(1),false);
                break;
            case Constants.PREF_CONT_3_VERTICAL:
                for(int i=0; i<bitmapItem.size(); i++)
                {
                    bitmapItem.set(i,convertToHorizontalTwoThird(bitmapItem.get(i)));
                }
                result=combineImage(bitmapItem.get(0),bitmapItem.get(1),false);
                result=combineImage(result,bitmapItem.get(2),false);
                break;

            case Constants.PREF_CONT_4:
                //크기 1/2로 줄이기
                for(int i=0; i<bitmapItem.size(); i++)
                {
                    bitmapItem.set(i,Bitmap.createScaledBitmap(bitmapItem.get(i), bitmapItem.get(i).getWidth() / 2, bitmapItem.get(i).getHeight() / 2, true));
                }
                result=combineImage(combineImage(bitmapItem.get(0),bitmapItem.get(1),false),combineImage(bitmapItem.get(2),bitmapItem.get(3),false),true);
                break;
        }


        //1:1비율의 경우 3:4이미지에서 일부 자름
        if(ratio== Constants.PREF_RATIO_11)
        {
            result=covertRatio34To11(result);
        }


        Bitmap logo=drawableToBitmap(activity.getResources().getDrawable(logoId));

        putLogo(result, logo);

        return bitmapToByteArray(result);


    }


    //갤러리에 추가
    public static void galleryAddPic(Context context,String mCurrentPhotoPath ) {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        context.sendBroadcast(mediaScanIntent);
    }

    //갤러리에서 삭제
    public static void renewGallery(Context context)
    {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        context.sendBroadcast(mediaScanIntent);

    }

    //파일 삭제
    public static boolean removeFile(String path)
    {
        File file = new File(path);
        boolean deleted = file.delete();

        return deleted;
    }

    public static File bitmapToFileCache(Bitmap bitmap, String strFilePath) {

        File fileCacheItem = new File(strFilePath);
        OutputStream out = null;

        try
        {
            fileCacheItem.createNewFile();
            out = new FileOutputStream(fileCacheItem);

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                out.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }

        return fileCacheItem;
    }

    public static byte[] convertImageToByte(Context context,String filePath){
        byte[] data = null;

        File file=new File(filePath);
        try {
            ContentResolver cr = context.getContentResolver();
            InputStream inputStream =new BufferedInputStream(new FileInputStream(file));
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            data = baos.toByteArray();
            bitmap.recycle();
        } catch (FileNotFoundException e) {
            Log.e("jyr","error convert image to byte");
            e.printStackTrace();
        }
        return data;
    }


//    public  void cropImage(Uri contentUri,Activity activity,int requestCode) {
//        Intent cropIntent = new Intent("com.android.camera.action.CROP");
//        //indicate image type and Uri of image
//        cropIntent.setDataAndType(contentUri, "image/*");
//        //set crop properties
//        cropIntent.putExtra("crop", "true");
//        //indicate aspect of desired crop
//        cropIntent.putExtra("aspectX", 1);
//        cropIntent.putExtra("aspectY", 1);
//        //indicate output X and Y
//        cropIntent.putExtra("outputX", 256);
//        cropIntent.putExtra("outputY", 256);
//        //retrieve data on return
//        cropIntent.putExtra("return-data", true);
//        activity.startActivityForResult(cropIntent, requestCode);
//    }
}

package com.claude.sharecam.signup;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;

/**
 * Load Image From url
 */
public class LoadImage extends AsyncTask<String,Void,Bitmap> {


    AfterLoadImage afterLoadImage;

    public LoadImage(AfterLoadImage afterLoadImage)
    {
        this.afterLoadImage=afterLoadImage;
    }
    @Override
    protected Bitmap doInBackground(String... url) {
        return download_Image(url[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {

        afterLoadImage.next(bitmap);
    }


    private Bitmap download_Image(String url) {
        //---------------------------------------------------
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(bis);
            bis.close();
            is.close();
        } catch (IOException e) {
            Log.e("Hub", "Error getting the image from server : " + e.getMessage().toString());
        }
        return bm;
        //---------------------------------------------------
    }


    interface AfterLoadImage {
        void next(Bitmap bitmap);
    }
}
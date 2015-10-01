package com.claude.sharecam.util;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;

import com.claude.sharecam.Constants;
import com.claude.sharecam.Util;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;

/**
 * 이미지 선택하여 편집 후 불러오는 fragment
 * setOnImageChosen function으로 이미지 선택 후 불러 왔을 경우 callback 인터페이스 정의 후 사용
 */
public class ImageSelectFragment extends Fragment  implements
        ImageChooserListener {

    public static final String TAG="ImageSelectFragment";
    private ImageChooserManager imageChooserManager;
    private int chooserType;
    private String mediaPath;
    private int prevRequeset;


    public interface OnImageChosen {
        void onImageChosen(ChosenImage image);
        void onError(String s);
    }
    OnImageChosen onImageChosen;

    public void setOnImageChosen(OnImageChosen onImageChosen)
    {
        this.onImageChosen=onImageChosen;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (chooserType != 0) {
            outState.putInt("chooser_type", chooserType);
        }
        if (mediaPath != null) {
            outState.putString("media_path", mediaPath);
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey("media_path")) {
                mediaPath = savedInstanceState.getString("media_path");
            }
            if (savedInstanceState.containsKey("chooser_type")) {
                chooserType = savedInstanceState.getInt("chooser_type");
            }
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d("On Activity Result", requestCode + "");
        super.onActivityResult(requestCode, resultCode, data);

        //사진 선택 -> avairy 편집화면
        if (resultCode == getActivity().RESULT_OK && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {

            prevRequeset=requestCode;
            if(data==null)
            {
                Log.d(TAG,"mediaPath = " + mediaPath);
                data=new Intent();
                data.setData(Uri.parse(mediaPath));
            }
            Log.d(TAG,"modify PICTURE -> avairy");
            Util.startAvairy(getActivity(), data);
        }
        //사진편집완료
        else if(resultCode == getActivity().RESULT_OK &&(requestCode== Constants.REQUEST_AVAIRY)) {
            Log.d(TAG,"avairy -> end");
            if (imageChooserManager == null) {
                imageChooserManager = new ImageChooserManager(this, requestCode, true);
                imageChooserManager.setImageChooserListener(this);
                imageChooserManager.reinitialize(mediaPath);
            }
            imageChooserManager.submit(prevRequeset, data);
        }

    }

    //사진촬영 시 호출
    public void takePicture() {
        chooserType = ChooserType.REQUEST_CAPTURE_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_CAPTURE_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            mediaPath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //이미지 선택 시 호출
    public void chooseImage() {
        chooserType = ChooserType.REQUEST_PICK_PICTURE;
        imageChooserManager = new ImageChooserManager(this,
                ChooserType.REQUEST_PICK_PICTURE, true);
        imageChooserManager.setImageChooserListener(this);
        try {
            mediaPath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onImageChosen(final ChosenImage image) {
        Log.d(TAG,"onImageChosen");
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                onImageChosen.onImageChosen(image);

            }
        });
    }

    @Override
    public void onError(final String s) {
        getActivity().runOnUiThread(new Runnable() {

            @Override
            public void run() {

                onImageChosen.onError(s);
            }
        });

    }
}

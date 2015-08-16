package com.claude.sharecam.parse;

import android.app.Activity;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.dialog.ListDialog;
import com.claude.sharecam.dialog.MyDialogBuilder;
import com.claude.sharecam.main.MainActivity;
import com.claude.sharecam.util.ImageManipulate;
import com.claude.sharecam.util.ImageSelectFragment;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

import java.util.ArrayList;

/**
 * Profile 설정하는 Fragment
 *
 * child fragment에서 1. setModifyProfileProgress()로 interface ModifyProfileProgress 구현
 *                   2. setProfileImageView()로 profileImageView 설정
 *
 */
public class ProfileSelectFragment extends ImageSelectFragment {

    public static final String TAG="ProfileSelectFragment";
    //call modify profile
    View.OnClickListener modifyProfileListener;
    ArrayList<String> strItems;
    ArrayList<View.OnClickListener> listenerItmes;
    ImageView profileImageView;
    ModifyProfileProgress modifyProfileProgress;
    //child fragment 에서 implement 필요
    public interface ModifyProfileProgress {
        void setProgressLayout(int mode);
    }
    public void setProfileImageView(ImageView profileImageView)
    {
        this.profileImageView=profileImageView;
        profileImageView.setOnClickListener(modifyProfileListener);
    }
    public void setModifyProfileProgress(ModifyProfileProgress modifyProfileProgress)
    {
        this.modifyProfileProgress=modifyProfileProgress;
    }
    ListDialog listDialog;

    @Override
    public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        strItems=new ArrayList<String>();
        strItems.add(getString(R.string.take_picture));
        strItems.add(getString(R.string.load_from_album));
        strItems.add(getString(R.string.basic_picture));

        listenerItmes=new ArrayList<View.OnClickListener>();
        //사진촬영
        listenerItmes.add(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyProfileProgress.setProgressLayout(Constants.PROGRESS_AND_LAYOUT_VISIBLE);
                takePicture();
                if(listDialog!=null)
                {
                    listDialog.dismiss();
                }
            }
        });

        //앨범에서 불러오기
        listenerItmes.add(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyProfileProgress.setProgressLayout(Constants.PROGRESS_AND_LAYOUT_VISIBLE);
                chooseImage();
                if(listDialog!=null)
                {
                    listDialog.dismiss();
                }
            }
        });

        //기본 사진 선택
        listenerItmes.add(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyProfileProgress.setProgressLayout(Constants.PROGRESS_AND_LAYOUT_VISIBLE);
                if(listDialog!=null)
                {
                    listDialog.dismiss();
                }
                User currentUser=(User)ParseUser.getCurrentUser();
                currentUser.removeProfile();
                currentUser.saveInBackground(new SCSaveCallback(getActivity(), new SCSaveCallback.Callback() {
                    @Override
                    public void done() {
                        setModifyProfileImg();
                    }
                }));

            }
        });

        modifyProfileListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                listDialog=MyDialogBuilder.showListDialog(getActivity(),getFragmentManager(),strItems,listenerItmes);
            }
        };



        //프로필 이미지 변경시
        setOnImageChosen(new OnImageChosen() {
            @Override
            public void onImageChosen(final ChosenImage image) {
//                image.
                Log.d(TAG, "profile image chosen and try uploading profile image to server");
                Log.d(TAG,"image path = "+image.getFilePathOriginal());
                ParseAPI.uploadProfilePicture(getActivity(),image.getFilePathOriginal(), new SCSaveCallback(getActivity(), new SCSaveCallback.Callback() {
                    @Override
                    public void done() {
//                        setModifyProfileImg(ImageManipulate.getThumbnailFromPath(image.getFilePathOriginal(),Constants.PROFILE_THUM_SIZE));

                        Log.d(TAG,"upload picture success");
                            setModifyProfileImg();
//                        modifyProfileImg.setImageBitmap(ImageManipulate.byteArrayToBitmap(((User)ParseUser.getCurrentUser()).getProfileFile().));
                    }
                }));
            }

            @Override
            public void onError(String s) {

            }
        });
    }


    public void setModifyProfileImg()
    {

        Log.d(TAG,"setModifyProfileImg()");
        if(((User) ParseUser.getCurrentUser()).getThumProfileFile()!=null) {
            ((User) ParseUser.getCurrentUser()).getThumProfileFile().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        profileImageView.setImageBitmap(ImageManipulate.byteArrayToBitmap(bytes));
                        modifyProfileProgress.setProgressLayout(Constants.PROGRESS_INVISIBLE);
                    } else {
                        ParseAPI.erroHandling(getActivity(), null);
                    }
                    Log.d(TAG,"setModifyProfileImg success");
                }
            });
        }
        else{
            modifyProfileProgress.setProgressLayout(Constants.PROGRESS_INVISIBLE);
            profileImageView.setImageResource(R.mipmap.profile);
            Log.d(TAG,"setModifyProfileImg success");
        }
    }

    public void setModifyProfileImg(Bitmap bitmap)
    {

        if(bitmap!=null)
             profileImageView.setImageBitmap(bitmap);
        else
            profileImageView.setImageResource(R.mipmap.profile);

        modifyProfileProgress.setProgressLayout(Constants.PROGRESS_INVISIBLE);

    }

}

package com.claude.sharecam.config;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.main.MainActivity;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.ProfileSelectFragment;
import com.claude.sharecam.parse.SCSaveCallback;
import com.claude.sharecam.parse.User;
import com.claude.sharecam.util.ImageManipulate;
import com.claude.sharecam.util.ImageSelectFragment;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseUser;

public class ModifyProfileFragment extends ProfileSelectFragment implements ProfileSelectFragment.ModifyProfileProgress {

    public static final String TAG="ModifyProfileFragment";
    private ImageChooserManager imageChooserManager;
    private int chooserType;
    private String mediaPath;


    ImageView modifyProfileImg;
    ImageView modifyProfileBtn;
    TextView modifyUserNameText;
    ImageView modifyUsernameBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View root=inflater.inflate(R.layout.fragment_modify_profile, container, false);


        modifyProfileImg=(ImageView)root.findViewById(R.id.modifyProfileImg);
        modifyProfileBtn=(ImageView)root.findViewById(R.id. modifyProfileBtn);
        modifyUserNameText =(TextView)root.findViewById(R.id.modifyUserNameText);
        modifyUsernameBtn=(ImageView)root.findViewById(R.id.modifyUsernameBtn);


        init();

        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        setProgressLayout(Constants.PROGRESS_INVISIBLE);
    }
    private void init()
    {
        /**
         * ProfileSelectFragment set
         */
        setProfileImageView(modifyProfileImg);
        setModifyProfileProgress(this);
        setModifyProfileImg();
//        //프로필 이미지 변경시
//        setOnImageChosen(new OnImageChosen() {
//            @Override
//            public void onImageChosen(ChosenImage image) {
////                image.
//                Log.d(TAG, "profile image chosen and try uploading profile image to server");
//                ParseAPI.uploadProfilePicture(image.getFilePathOriginal(), new SCSaveCallback(getActivity(), new SCSaveCallback.Callback() {
//                    @Override
//                    public void done() {
//                        setModifyProfileImg();
//
////                        modifyProfileImg.setImageBitmap(ImageManipulate.byteArrayToBitmap(((User)ParseUser.getCurrentUser()).getProfileFile().));
//                    }
//                }));
//            }
//
//            @Override
//            public void onError(String s) {
//
//            }
//        });
//
//        setModifyProfileImg();


        modifyProfileBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setProgressLayout(Constants.PROGRESS_AND_LAYOUT_VISIBLE);
                chooseImage();
            }
        });
        modifyUserNameText.setText(((User) ParseUser.getCurrentUser()).getNmae());
        modifyUsernameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.startFragment(getFragmentManager(),R.id.mainContainer,new ModifyUserNameFragment(),true,null);
            }
        });
    }

//    private void setModifyProfileImg()
//    {
//
//        if(((User)ParseUser.getCurrentUser()).getThumProfileFile()!=null) {
//            ((User) ParseUser.getCurrentUser()).getThumProfileFile().getDataInBackground(new GetDataCallback() {
//                @Override
//                public void done(byte[] bytes, ParseException e) {
//                    if (e == null) {
//                        modifyProfileImg.setImageBitmap(ImageManipulate.byteArrayToBitmap(bytes));
//                        ((MainActivity)getActivity()).setProgressLayout(Constants.PROGRESS_INVISIBLE);
//                    } else {
//                        ParseAPI.erroHandling(getActivity(), null);
//                    }
//                }
//            });
//        }
//    }

    @Override
    public void setProgressLayout(int mode) {
        ((MainActivity)getActivity()).setProgressLayout(mode);
    }
//
//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        Log.d("On Activity Result", requestCode + "");
//        super.onActivityResult(requestCode, resultCode, data);
//        //사진 선택 -> avairy 편집화면
//        if (resultCode == getActivity().RESULT_OK && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
//            Log.d(TAG,"modify picture -> avairy");
//            Util.startAvairy(getActivity(), data);
//        }
//        //사진편집완료
//        else if(resultCode == getActivity().RESULT_OK &&(requestCode== Constants.REQUEST_AVAIRY)) {
//            Log.d(TAG,"avairy -> end");
//            if (imageChooserManager == null) {
//                imageChooserManager = new ImageChooserManager(this, requestCode, true);
//                imageChooserManager.setImageChooserListener(this);
//                imageChooserManager.reinitialize(mediaPath);
//            }
//            imageChooserManager.submit(ChooserType.REQUEST_PICK_PICTURE, data);
//        }
//
//    }
//
//    private void chooseImage() {
//        chooserType = ChooserType.REQUEST_PICK_PICTURE;
//        imageChooserManager = new ImageChooserManager(this,
//                ChooserType.REQUEST_PICK_PICTURE, true);
//        imageChooserManager.setImageChooserListener(this);
//        try {
//            mediaPath = imageChooserManager.choose();
//        } catch (IllegalArgumentException e) {
//            e.printStackTrace();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//
//    @Override
//    public void onImageChosen(final ChosenImage image) {
//        getActivity().runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//
//                modifyProfileImg.setImageURI(Uri.parse(new File(image
//                        .getFileThumbnail()).toString()));
//            }
//        });
//    }
//
//    @Override
//    public void onError(String s) {
//        getActivity().runOnUiThread(new Runnable() {
//
//            @Override
//            public void run() {
//
//                Util.showToast(getActivity(),R.string.network_unavailable);
//            }
//        });
//
//    }

}

package com.claude.sharecam.signup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.aviary.android.feather.sdk.AviaryIntent;
import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.api.ErrorCode;
import com.claude.sharecam.camera.CameraActivity;
import com.claude.sharecam.parse.ParseAPI;
import com.kbeanie.imagechooser.api.ChooserType;
import com.kbeanie.imagechooser.api.ChosenImage;
import com.kbeanie.imagechooser.api.ImageChooserListener;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseUser;
import com.parse.SaveCallback;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;


public class InputProfileFragment extends Fragment {


    public static final String TAG="inputProfileFragmentTag";
    ImageView inputProfileImg;
    EditText inputProfileNameET;
    TextView inputProfileNextBtn;
    ProgressBar inputProfilePgb;


    ImageChooserManager imageChooserManager;

    private String filePath;

    String newProfileURL;//프로필 수정한 경우 할당 됨

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root =  inflater.inflate(R.layout.fragment_input_profile, container, false);

        inputProfileImg=(ImageView)root.findViewById(R.id.inputProfileImg);
        inputProfileNameET=(EditText)root.findViewById(R.id.inputProfileNameET);
        inputProfileNextBtn=(TextView)root.findViewById(R.id.inputProfileNextBtn);
        inputProfilePgb=(ProgressBar)root.findViewById(R.id.inputProfilePgb);

        String profileUrlStr=ParseUser.getCurrentUser().getParseFile("profile").getUrl();
        //프로필 사진 없을 경우 디폴트 이미지 세팅
//        profileUrlStr=profileUrlStr!=null?profileUrlStr:Util.getDrawableUriString(getActivity(),R.mipmap.profile);
        String profileNameStr= ParseUser.getCurrentUser().getUsername();
        newProfileURL="";

        /**
         * 프로필 이미지와 이름 set
         */
        inputProfileNameET.setText(profileNameStr);
        //if profile url exist, load image and set
        if(profileUrlStr!=null)
        {
            ((SignUpActivity)getActivity()).setProgressLayout(Constants.PROGRESS_VISIBLE);
            Picasso.with(getActivity()).load(profileUrlStr).into(inputProfileImg, new Callback() {
                @Override
                public void onSuccess() {
                    if( ((SignUpActivity)getActivity())!=null)
                        ((SignUpActivity)getActivity()).setProgressLayout(Constants.PROGRESS_INVISIBLE);
                }
                @Override
                public void onError() {
                    if( ((SignUpActivity)getActivity())!=null)
                        ((SignUpActivity)getActivity()).setProgressLayout(Constants.PROGRESS_INVISIBLE);

                }
            });
        }


        //if profile url doesn't exist, set default image
        else{
            inputProfileImg.setImageResource(R.mipmap.profile);
        }



        /**
         * set listener
         */
        // set pofile image listener
        inputProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                chooseImage(new ImageChooserListener() {
                    @Override
                    public void onImageChosen(final ChosenImage chosenImage) {

                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                Log.d("jyr","change input profile img");

                                //upload image to parse and load image to imageview for porifle
                                try {
                                    byte[] profileFile=org.apache.commons.io.FileUtils.readFileToByteArray(new File(chosenImage.getFileThumbnailSmall()));
                                    ParseFile file = new ParseFile(ParseUser.getCurrentUser().getObjectId()+"."+chosenImage.getExtension(),profileFile);
                                    ParseUser user=ParseUser.getCurrentUser();
                                    user.put("profile",file);
                                    user.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(ParseException e) {
                                            inputProfilePgb.setVisibility(View.GONE);

                                            if(e==null)
                                            {
                                                //invalidate origin profile url
                                                Picasso.with(getActivity()).invalidate(ParseUser.getCurrentUser().getParseFile("profile").getUrl());
                                                //set imageview to image  i've just upload
                                                Picasso.with(getActivity()).load(ParseUser.getCurrentUser().getParseFile("profile").getUrl()).into(inputProfileImg);
                                            }
                                        }
                                    });
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }

                            }
                        });
                    }

                    @Override
                    public void onError(final String reason) {
                        getActivity().runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                inputProfilePgb.setVisibility(View.GONE);
                                Toast.makeText(getActivity(), reason,
                                        Toast.LENGTH_LONG).show();
                            }
                        });

                    }
                });
            }
        });

        //로그인 완료
        inputProfileNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(inputProfileNameET.getText().toString().length()==0)
                {
                    Util.showToast(getActivity(),R.string.you_should_fill_out);
                    return;
                }

                ((SignUpActivity)getActivity()).setProgressLayout(Constants.PROGRESS_VISIBLE);

                //initialze contact (upload contact to server)
                //complete singing up logic
                //기존 주소록 삭제하고 최신 주소록 업로드
                ParseAPI.initContact(ParseUser.getCurrentUser(), getActivity(), new Handler() {
                    public void handleMessage(Message msg) {

                        //업로드된 주소록 기반 쉐어캠 친구 목록 동기화
                        ParseAPI.syncFriendWithContact(getActivity(),new Handler(){


                            public void handleMessage(Message msg)
                            {

                                //회원 가입 완료
                                ParseAPI.signUpCompleted(getActivity(),new Handler(){
                                    @Override
                                    public void handleMessage(Message msg) {
                                        super.handleMessage(msg);
                                        //카메라 실행
                                        Intent i = new Intent(getActivity(), CameraActivity.class); // Your list's Intent
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);

                                    }
                                });
                                /*

                                //complete the logic for singing up
                                ParseUser user = ParseUser.getCurrentUser();
                                user.put("username", inputProfileNameET.getText().toString());
                                user.put("completed",true);

                                //save user name and completer = true
                                user.saveInBackground(new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if (e == null) {
                                            //카메라 실행
                                            Intent i = new Intent(getActivity(), CameraActivity.class); // Your list's Intent
                                            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                            startActivity(i);
                                        } else {
                                            ParseAPI.erroHandling(getActivity(), e);
                                        }
                                    }
                                });*/

                            }
                        });

                    }
                });



            }






/*
                ((Util)getActivity().getApplicationContext()).apiManager.server.updateUser(((Util) getActivity().getApplicationContext()).pref.getInt(Constants.PREF_USER_ID, -1), profileName, profileURL, 1, new RestCallBack<User>() {
                    @Override
                    public void failure(RestError restError) {

                        Util.showToast(getActivity(), ErrorCode.getToastMessageId(getActivity(), restError));

                    }

                    @Override
                    public void success(User user, Response response) {

                        ((Util) getActivity().getApplicationContext()).editor.putBoolean(Constants.PREF_LOGIN,true).commit();
                        //카메라 실행
                        Intent i = new Intent(getActivity(), CameraActivity.class); // Your list's Intent
                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);


                    }
                });
                */


        });


        // Inflate the layout for this fragment
        return root;
    }



    //프로필 사진 선택
    private void chooseImage(ImageChooserListener imageChooserListener) {

        imageChooserManager = new ImageChooserManager(getActivity(),
                ChooserType.REQUEST_PICK_PICTURE, "myfolder", true);
        imageChooserManager.setImageChooserListener(imageChooserListener);
        try {
            inputProfilePgb.setVisibility(View.VISIBLE);
            filePath = imageChooserManager.choose();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {



        //사진 선택 -> avairy 편집화면
        if (resultCode == getActivity().RESULT_OK
                && (requestCode == ChooserType.REQUEST_PICK_PICTURE || requestCode == ChooserType.REQUEST_CAPTURE_PICTURE)) {
//            if (imageChooserManager == null) {
//                reinitializeImageChooser();
//            }       Uri uri= Uri.parse(new File(arItem.get(currentPosition)).toString());


            Log.d("jyr", "choose image ");
            //사진 편집 실행
            Intent intent=new AviaryIntent.Builder(getActivity())
                    .setData(data.getData())
                    .withOutput(data.getData())
                    .withOutputFormat(Bitmap.CompressFormat.JPEG)
                    .saveWithNoChanges(true)
                    .withPreviewSize(5000)
                    .withNoExitConfirmation(false)//저장 안하고 뒤로 가기 눌렀을 경우 Dialog 띄움
                    .withOutputQuality(90)
                    .build();

            getActivity().startActivityForResult(intent, Constants.REQUEST_AVAIRY);
//            imageChooserManager.submit(requestCode, data);
        }

        //사진편집완료
        else if(resultCode == getActivity().RESULT_OK
                &&(requestCode==Constants.REQUEST_AVAIRY))
        {
            Log.d("jyr", "choose avairyd ");
            imageChooserManager.submit( ChooserType.REQUEST_PICK_PICTURE , data);
        }
        else {
            inputProfilePgb.setVisibility(View.GONE);
        }
    }





}

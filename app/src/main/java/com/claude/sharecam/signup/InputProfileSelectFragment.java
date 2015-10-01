package com.claude.sharecam.signup;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.camera.CameraActivity;
import com.claude.sharecam.parse.ProfileSelectFragment;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.SCSaveCallback;
import com.claude.sharecam.parse.User;
import com.kbeanie.imagechooser.api.ImageChooserManager;
import com.parse.ParseUser;
//import com.squareup.picasso.Picasso;


public class InputProfileSelectFragment extends ProfileSelectFragment implements ProfileSelectFragment.ModifyProfileProgress {


    public static final String TAG="InputProfileFragment";
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

//        String profileUrlStr=ParseUser.getCurrentUser().getParseFile("profile").getUrl();
        //프로필 사진 없을 경우 디폴트 이미지 세팅
//        profileUrlStr=profileUrlStr!=null?profileUrlStr:Util.getDrawableUriString(getActivity(),R.mipmap.profile);
//        String profileNameStr= ParseUser.getCurrentUser().getUsername();
        newProfileURL="";

        /**
         * ProfileSelectFragment set
         */
        setProfileImageView(inputProfileImg);
        setModifyProfileProgress(this);

        /**
         * 프로필 이미지와 이름 set
         */
//        inputProfileNameET.setText(profileNameStr);
        //if profile url exist, load image and set
//        setModifyProfileImg();


        /**
         * set listener
         */
/*
        //프로필 이미지 변경시2
        setOnImageChosen(new OnImageChosen() {
            @Override
            public void onImageChosen(final ChosenImage image) {
//                image.
                Log.d(TAG, "profile image chosen and try uploading profile image to server");
                ParseAPI.uploadProfilePicture(image.getFilePathOriginal(), new SCSaveCallback(getActivity(), new SCSaveCallback.Callback() {
                    @Override
                    public void done() {
                        setModifyProfileImg();
                        ((SignUpActivity)getActivity()).setProgressLayout(Constants.PROGRESS_INVISIBLE);
//                        /ProfileImg.setImageBitmap(ImageManipulate.byteArrayToBitmap(((User)ParseUser.getCurrentUser()).getProfileFile().));
                    }
                }));
            }

            @Override
            public void onError(String s) {

            }
        });

        // set pofile image listener
        inputProfileImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ((SignUpActivity)getActivity()).setProgressLayout(Constants.PROGRESS_AND_LAYOUT_VISIBLE);
                chooseImage();
                
            }
        });*/

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


                //초기화
                ParseAPI.initialize(ParseUser.getCurrentUser(), getActivity(), new Handler(){
                    @Override
                    public void handleMessage(Message msg) {
                        super.handleMessage(msg);


                        //새 사용자 등록 알림
                        ParseAPI.informNewUser(getActivity(), new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                super.handleMessage(msg);


                                //이름 설정 및 회원 가입완료 설정
                                User user= (User) ParseUser.getCurrentUser();
                                user.setName(inputProfileNameET.getText().toString());
                                user.setCompleted(true);
                                user.saveInBackground(new SCSaveCallback(getActivity(), new SCSaveCallback.Callback() {
                                    @Override
                                    public void done() {
                                        //카메라 실행
                                        Intent i = new Intent(getActivity(), CameraActivity.class); // Your list's Intent
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    }
                                }));


                            }
                        });
                    }
                });
                /*
                //initialze contact (upload contact to server)
                //complete singing up logic
                //기존 주소록 삭제하고 최신 주소록 서버에 업로드
                ParseAPI.initContact(ParseUser.getCurrentUser(), getActivity(), new Handler() {
                    public void handleMessage(Message msg) {

                        //업로드된 주소록 기반 쉐어캠 친구 목록 동기화
                        ParseAPI.syncFriendWithContact(getActivity(), new Handler() {


                            public void handleMessage(Message msg) {

                                //회원 가입 완료
                                ParseAPI.informNewUser(getActivity(), new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        super.handleMessage(msg);
                                        //카메라 실행
                                        Intent i = new Intent(getActivity(), CameraActivity.class); // Your list's Intent
                                        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);

                                    }
                                });


                            }
                        });

                    }
                });*/



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

//    private void setModifyProfileImg()
//    {
//
//        if(((User)ParseUser.getCurrentUser()).getThumProfileFile()!=null) {
//            ((User) ParseUser.getCurrentUser()).getThumProfileFile().getDataInBackground(new GetDataCallback() {
//                @Override
//                public void done(byte[] bytes, ParseException e) {
//                    if (e == null) {
//                        inputProfileImg.setImageBitmap(ImageManipulate.byteArrayToBitmap(bytes));
//                    } else {
//                        ParseAPI.erroHandling(getActivity(), null);
//                    }
//                }
//            });
//        }
//        else{
//            inputProfileImg.setImageResource(R.mipmap.profile);
//        }
//    }

    @Override
    public void setProgressLayout(int mode) {
        ((SignUpActivity)getActivity()).setProgressLayout(mode);
    }

/*

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

            Log.d(TAG, "choose image ");
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
            Log.d(TAG, "choose avairy ");

            imageChooserManager.submit( ChooserType.REQUEST_PICK_PICTURE , data);
        }
        else {
            inputProfilePgb.setVisibility(View.GONE);
        }
    }
*/




}

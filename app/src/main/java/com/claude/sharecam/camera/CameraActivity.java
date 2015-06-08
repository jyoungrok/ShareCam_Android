package com.claude.sharecam.camera;

import java.io.File;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.hardware.Camera.PictureCallback;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.share.ShareActivity;
import com.parse.Parse;
import com.parse.ParseUser;

public class CameraActivity extends ActionBarActivity {


//    private static boolean takingTimerPicture;

    private final int EXTRA_LAYOUT=1;
    private final int CONT_LAYOUT=2;
    private final int GRID_Layout=3;
    private final int HORIZONTAL_HALF_LAYOUT =4;
    private final int TIMER_LAYOUT=5;
    private final int MULT_PICUTRE_TEXT_LAYOUT=6;
    private final int VERTICAL_HALF_LAYOUT =7;
    private final int VERTICAL_TWO_THIRD_LAYOUT =8;

    private Camera mCamera;
    private CameraPreview mPreview;
    private PictureCallback mPicture;
    private ImageView capture, switchBtn, flashBtn,extraFeatureBtn, contShootBtn,cont1Btn, cont2VerticalBtn, cont3VerticalBtn,cont4Btn, directModeBtn,onePictureModeBtn,multPictureModeBtn, multPictureAcceptBtn, shareConfigBtn;
    private Context myContext;
    private FrameLayout gridLayout;
    private CameraFrameLayout cameraPreview;
    private LinearLayout extraFeatureLayout,gridBtn,oneTouchBtn,timerBtn,contShootLayout, ratio34Btn, ratio11Btn;
    private TextView timerText, ratio11Text, ratio34Text, multPictureText;
    private LinearLayout horizontalTopLayout, horizontalBottomLayout, verticalLeftLayout, verticalRightLayout;
    private RelativeLayout verticalLayout, horizontalLayout;
    private boolean takingPicture;
//    RatioLayout verticalLayout;

    private CameraSetting cameraSetting;//카메라 세팅 관련 value
    private Util util;

    private CameraOrientationListener orientationListener;

    //사진 여러장 찍기 모드에서 저장한 이미지들의 경로
    private ArrayList<String> arItem;
    private ArrayList<byte[]> contItem;//연속 사진 촬영 시 촬영한 사진 임시 저장

    Context context;
    Activity activity;


    //타이머 촬영
    Timer captureTimer;
    TimerHandler timerHandler;

    //연속 촬영 타이머
    Timer contTimer;


    // 일반 촬영
    Timer normalCaptrueTimer;
    NormalCaptureHandler normalHandler;



    //사진 촬영 애니 메이션
    CaptureAnimationHandler captureAnimationHandler;




    private void chooseCamera() {
        //if the camera preview is the front
        if (cameraSetting.cameraFront) {
            int cameraId = CameraFunction.findBackFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview
                cameraSetting.setCameraFront(false);

                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        } else {
            int cameraId = CameraFunction.findFrontFacingCamera();
            if (cameraId >= 0) {
                //open the backFacingCamera
                //set a picture callback
                //refresh the preview

                cameraSetting.setCameraFront(true);
                mCamera = Camera.open(cameraId);
                mPicture = getPictureCallback();
                mPreview.refreshCamera(mCamera);
            }
        }
    }



    //사진 촬영 후 callback
    private PictureCallback getPictureCallback() {
        PictureCallback picture = new PictureCallback() {

            @Override
            public void onPictureTaken(byte[] data, Camera camera) {
                //make a new picture file
                File pictureFile = CameraFunction.getOutputMediaFile();

                if (pictureFile == null) {
                    return;
                }

                //

                String savedUriStr=null;

                //연속 촬영 중일 경우 (default - > 연속 촬영 아닐 때)
                switch(cameraSetting.cont)
                {
                    case Constants.PREF_CONT_2_VERTICAL:
                        contItem.add(data);
                        if(contItem.size()==2) {
                            savedUriStr=CameraFunction.saveContPictrue(activity,cameraSetting,contItem,orientationListener.getRememberedOrientation());
                            contItem.clear();
                        }
                        else{
                            return;
                        }
                        break;
                    case Constants.PREF_CONT_3_VERTICAL:
                        contItem.add(data);
                        if(contItem.size()==3) {
                            savedUriStr=CameraFunction.saveContPictrue(activity,cameraSetting,contItem,orientationListener.getRememberedOrientation());
                            contItem.clear();
                        }
                        else{
                            return;
                        }
                        break;
                    case Constants.PREF_CONT_4:
                        contItem.add(data);
                        if(contItem.size()==4) {
                            savedUriStr=CameraFunction.saveContPictrue(activity,cameraSetting,contItem,orientationListener.getRememberedOrientation());
                            contItem.clear();
                        }
                        else{
                            return;
                        }
                        break;
                    case Constants.PREF_CONT_1:
                    default:
                        savedUriStr= CameraFunction.savePicture(activity,cameraSetting,data,orientationListener.getRememberedOrientation());
                        break;
                }

                switch(cameraSetting.mode)
                {

                    case Constants.PREF_CAMERA_DIRECT_MODE:

                        break;

                    case Constants.PREF_CAMERA_ONE_PICTURE_MODE:
                        Intent intent=new Intent(activity,PictureModifyActivity.class);
                        intent.putExtra(PictureModifyActivity.MODE,Constants.PREF_CAMERA_ONE_PICTURE_MODE);
                        intent.putExtra(PictureModifyActivity.IMAGE_PATH,savedUriStr);
//                        intent.putExtra(PictureModifyActivity.IMAGE_BYTE,ImageManipulate.getImage_byte(activity,cameraSetting.ratio,data,R.mipmap.logo, ImageManipulate.getRotate(orientationListener.getRememberedOrientation(),cameraSetting.cameraFront)));
                        startActivity(intent);
                        break;

                    case Constants.PREF_CAMERA_MULTIPLE_PICTURE_MODE:
                        arItem.add(savedUriStr);
                        multPictureAcceptBtn.setVisibility(View.VISIBLE);
                        multPictureText.setText(String.valueOf(arItem.size()));
                        setLayout(MULT_PICUTRE_TEXT_LAYOUT);
                        break;

                }

//                try {
//                    //write the file
//                    FileOutputStream fos = new FileOutputStream(pictureFile);
//                    //이미지에 로고 추가한 후 저장
//                    fos.write(ImageManipulate.getImage_byte(activity, cameraSetting.ratio,data, R.mipmap.logo,ImageManipulate.getRotate(orientationListener.getRememberedOrientation(), cameraSetting.cameraFront)));
//                    fos.close();
////                    Toast toast = Toast.makeText(myContext, "Picture saved: " + pictureFile.getName(), Toast.LENGTH_LONG);
////                    toast.show();
//
//                    //add image to gallery
//                    ImageManipulate.galleryAddPic(context, pictureFile.getAbsolutePath());
//
//
//
//                } catch (FileNotFoundException e) {
//                } catch (IOException e) {
//                }

                //refresh camera to continue preview
                mPreview.refreshCamera(mCamera);
            }
        };
        return picture;
    }








    private void releaseCamera() {
        // stop and release camera
        if (mCamera != null) {
            mCamera.release();
            mCamera = null;
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ParseAPI.syncFriendWithContact(this, new Handler(){
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
            }
        });
//        ParseUser user=ParseUser.getCurrentUser();
//        user.put("phone","dfgdfg");
//        user.saveInBackground();


        getSupportActionBar().hide();
         Util.checkLogin(this);
        context=this;
        activity=this;
        setContentView(R.layout.activity_camera);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        myContext = this;
        initialize();
    }
    @Override
    public void onResume() {
        super.onResume();

        orientationListener.enable();

        if (!CameraFunction.hasCamera(myContext)) {
            Toast toast = Toast.makeText(myContext, "Sorry, your phone does not have a camera!", Toast.LENGTH_LONG);
            toast.show();
            finish();
        }
        if (mCamera == null) {
            //if the front facing camera does not exist
            if (CameraFunction.findFrontFacingCamera() < 0) {
                Toast.makeText(this, "No front facing camera found.", Toast.LENGTH_LONG).show();
                switchBtn.setVisibility(View.GONE);
            }
            initCamera();
            mPicture = getPictureCallback();
            mPreview.refreshCamera(mCamera);
        }

//        horizontalTopLayout.setPadding(0,(cameraPreview.getHeight()-cameraPreview.getWidth())/2,0,0);
//        cameraPreview.bringChildToFront(verticalLayout);
//        verticalLayout.requestLayout();
//        verticalLayout.invalidate();
    }


    @Override
    protected void onPause() {
        super.onPause();

        orientationListener.disable();
        //when on Pause, release camera in order to be used from other applications
        releaseCamera();
    }

    private void initialize() {
        arItem=new ArrayList<String >();
        contItem=new ArrayList<byte[]>();

        takingPicture=false;
        cameraSetting=new CameraSetting(this);
        orientationListener = new CameraOrientationListener(activity);
//        cameraSetting.=false;
//        oneTouch=util.pref.getBoolean(Constants.PREF_ONE_TOUCH,Constants.PREF_ONE_TOUCH_DEFAULT);

        normalHandler=new NormalCaptureHandler();
        timerHandler=new TimerHandler();
        captureAnimationHandler=new CaptureAnimationHandler();

        util=((Util)getApplicationContext());
        cameraPreview = (CameraFrameLayout) findViewById(R.id.cameraPreview);
        capture = (ImageView) findViewById(R.id.captureBtn);
        switchBtn = (ImageView) findViewById(R.id.switchBtn);
        flashBtn=(ImageView)findViewById(R.id.flashBtn);
        extraFeatureBtn=(ImageView)findViewById(R.id.extraFeatureBtn);
        gridLayout=(FrameLayout)findViewById(R.id.gridLayout);
        extraFeatureLayout=(LinearLayout)findViewById(R.id.extraFeatureLayout);
        gridBtn=(LinearLayout)findViewById(R.id.gridBtn);
        oneTouchBtn=(LinearLayout)findViewById(R.id.oneTouchBtn);
        timerBtn=(LinearLayout)findViewById(R.id.timerBtn);
        timerText=(TextView)findViewById(R.id.timerText);
        contShootBtn=(ImageView)findViewById(R.id.contShootBtn);
        contShootLayout=(LinearLayout)findViewById(R.id.contShootLayout);
        ratio11Btn=(LinearLayout)findViewById(R.id.ratio11Btn);
        ratio34Btn=(LinearLayout)findViewById(R.id.ratio34Btn);
        cont1Btn=(ImageView)findViewById(R.id.cont1Btn);
        cont2VerticalBtn =(ImageView)findViewById(R.id.cont2Vertical);
        cont3VerticalBtn =(ImageView)findViewById(R.id.cont3Vertical);
        cont4Btn=(ImageView)findViewById(R.id.cont4Btn);
        ratio11Text=(TextView)findViewById(R.id.ratio11Text);
        ratio34Text =(TextView)findViewById(R.id.ratio34Text);
        horizontalTopLayout =(LinearLayout)findViewById(R.id.horizontalTopLayout);
        horizontalBottomLayout =(LinearLayout)findViewById(R.id.horizontalBottomLayout);
        verticalLayout =(RelativeLayout)findViewById(R.id.verticalLayout);
        directModeBtn=(ImageView)findViewById(R.id.directModeBtn);
        onePictureModeBtn=(ImageView)findViewById(R.id.onePictureModeBtn);
        multPictureModeBtn=(ImageView)findViewById(R.id.multPictureModeBtn);
        multPictureAcceptBtn=(ImageView)findViewById(R.id.multPictureAcceptBtn);
        multPictureText=(TextView)findViewById(R.id.multPictureText);
        horizontalLayout=(RelativeLayout)findViewById(R.id.horizontalLayout);
//        horizontalTopLayout=(LinearLayout)findViewById(R.id.horizontalTopLayout);
//        horizontalBottomLayout=(LinearLayout)findViewById(R.id.horizontalBottomLayout);
        verticalLeftLayout =(LinearLayout)findViewById(R.id.verticalLeftLayout);
        verticalRightLayout =(LinearLayout)findViewById(R.id.verticalRightLayout);
        shareConfigBtn=(ImageView) findViewById(R.id.shareConfigBtn);

//        verticalLayout=(RatioLayout)findViewById(R.id.verticalLayout);



        mPreview = new CameraPreview(myContext, mCamera,cameraSetting);
        FrameLayout.LayoutParams layoutParams=new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.gravity= Gravity.CENTER_VERTICAL;
        mPreview.setLayoutParams(layoutParams);

        cameraPreview.addView(mPreview);

        /*
        //ratio 11 을 위한 레이아웃 설정
        cameraPreview.getViewTreeObserver().addOnGlobalLayoutListener(
                new ViewTreeObserver.OnGlobalLayoutListener() {

                    @Override
                    public void onGlobalLayout() {

                        Log.d("jyr", "cameraPreview onGlobalLayout");
                        //cameraPreview의 상단 하단에 1:1비율을 위한 검은색 공백 크기 할당
                        int ratio11LayoutHeight=(cameraPreview.getHeight()-cameraPreview.getWidth())/2;
                        horizontalTopLayout.setPadding(0, ratio11LayoutHeight, 0, 0);
                        horizontalBottomLayout.setPadding(0, ratio11LayoutHeight, 0, 0);


                        // only want to do this once
                        if (Build.VERSION.SDK_INT < 16) {
                            cameraPreview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                        } else {
                            cameraPreview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                        }


                    }
                });
*/
/*
        //1:1비율의 Preview를 위한 layout 추가
        cameraPreview.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
//
//                horizontalTopLayout = new LinearLayout(activity);
//                horizontalTopLayout.setBackgroundColor(Color.BLACK);
//                FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                layoutParams.gravity = Gravity.TOP;
//                horizontalTopLayout.setPadding(0,(cameraPreview.getHeight()-cameraPreview.getWidth())/2,0,0);
//                horizontalTopLayout.setLayoutParams(layoutParams);
//                cameraPreview.addView(horizontalTopLayout);
//
//
//                horizontalBottomLayout = new LinearLayout(activity);
//                horizontalBottomLayout.setBackgroundColor(Color.BLACK);
//                layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
//                layoutParams.gravity = Gravity.BOTTOM;
//                horizontalBottomLayout.setPadding(0, (cameraPreview.getHeight() - cameraPreview.getWidth()) / 2, 0, 0);
//                horizontalBottomLayout.setLayoutParams(layoutParams);
//                cameraPreview.addView(horizontalBottomLayout);

            }
        });

*/


        capture.setOnClickListener(captrureListener);
        switchBtn.setOnClickListener(switchCameraListener);
        flashBtn.setOnClickListener(flashBtnListener);
        extraFeatureBtn.setOnClickListener(extraFeatureBtnListener);
        gridBtn.setOnClickListener(gridBtnListener);
        timerBtn.setOnClickListener(timerBtnListener);
        oneTouchBtn.setOnClickListener(oneTouchBtnListener);
        contShootBtn.setOnClickListener(contShootBtnListener);
        ratio11Btn.setOnClickListener(ratio11BtnListener);
        ratio34Btn.setOnClickListener(ratio34BtnListener);
        cont1Btn.setOnClickListener(cont1BtnListener);
        cont2VerticalBtn.setOnClickListener(cont2VerticalListener);
        cont3VerticalBtn.setOnClickListener(cont3VerticalListener);
        cont4Btn.setOnClickListener(cont4BtnListener);
        directModeBtn.setOnClickListener(directModeBtnListener);
        onePictureModeBtn.setOnClickListener(onePictureModeBtnListener);
        multPictureModeBtn.setOnClickListener(multPictureModeBtnListener);
        multPictureAcceptBtn.setOnClickListener(multPictureAcceptBtnListener);
        shareConfigBtn.setOnClickListener(shareConfigBtnListener);


    }

    /**
     * should be called after initialize
     */
    //카메라 파라미터 세팅
    private void initCamera()
    {



        //카메라 설정
        if(cameraSetting.cameraFront)
            mCamera=Camera.open(CameraFunction.findFrontFacingCamera());
        else
            mCamera = Camera.open(CameraFunction.findBackFacingCamera());

        Camera.Parameters parameters=mCamera.getParameters();
        parameters.setFlashMode(cameraSetting.flash);
//        parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_VIDEO);
        mCamera.setParameters(parameters);

        //버튼 설정
        setFlashBtn(cameraSetting.flash);
        setOneTouch(cameraSetting.oneTouch);//camera preview Touch Listener 설정
        setGrid(cameraSetting.gridSet);
        setTimer(cameraSetting.timerCount);


        setMode(cameraSetting.mode);
        setRatio(cameraSetting.ratio);
        setCont(cameraSetting.cont);
    }


    //사진 촬영 버튼 글릭 시
    OnClickListener captrureListener = new OnClickListener() {
        @Override
        public void onClick(View v) {

            captruePicture();

        }
    };

    //사진 촬영 버튼 클릭한 경우
    private void captruePicture()
    {
        //사진 촬영 중인 경우 클릭 무시 (타이머 촬영 등)
        if(cameraSetting.takingTimerPicture)
        {
            Toast.makeText(myContext,getString(R.string.not_availalbe_click_while_taking_picture_toast),Toast.LENGTH_SHORT).show();
            return;
        }

        switch(cameraSetting.timerCount)
        {
            default:
            case Constants.PREF_TIMER_UNSET:
                takePicture();
                break;
            case Constants.PREF_TIMER_3:

            case Constants.PREF_TIMER_5:

            case Constants.PREF_TIMER_10:
                captureTimerPicture();
                break;
        }
    }

    //타이머 사진  촬영
    private void captureTimerPicture(){
        captureTimer =new Timer();

        cameraSetting.takingTimerPicture=true;

        captureTimer.schedule(new TimerTask() {
            @Override
            public void run() {


                if (cameraSetting.timerCount >= 1) {

                    //사진 촬영
                    if (cameraSetting.timerCount == 1) {
                        cameraSetting.takingTimerPicture = false;
                        cameraSetting.timerCount--;
                        timerHandler.sendEmptyMessage(0);
                        captureTimer.cancel();

                    }
                    //1초 감소
                    else {
                        cameraSetting.timerCount--;
                        timerHandler.sendEmptyMessage(0);
                    }


                } else {
                    captureTimer.cancel();
                }


            }
        }, 1000, 1000);
    }

    class TimerHandler extends Handler{

        @Override
        public void handleMessage(Message msg) {

            if(cameraSetting.timerCount==0)
            {
                takePicture();
                setTimer(cameraSetting.timerCount);
            }

            else {

                timerText.setText(String.valueOf(cameraSetting.timerCount));
            }

        }
    }

    //사진 촬영 기능 실행
    private void takePicture()
    {

        orientationListener.rememberOrientation();
        if(mCamera==null || takingPicture==true) {
            return;
        }

        //사진 촬영 중
        takingPicture=true;

        mCamera.autoFocus(new Camera.AutoFocusCallback() {

            public void onAutoFocus(boolean success, Camera camera) {

                Log.d("jyr", "takingPicture =" + success);
//                if(success){

//                    mCamera.takePicture(null, null, null);

                // 사진 촬영
                mCamera.takePicture(null, null, mPicture);

                // 사진 촬영 애니메이션
//        AnimationDrawable frameAnimation = (AnimationDrawable) cameraPreview.getBackground();
//        frameAnimation.start();


                captureAnimationHandler.sendEmptyMessage(0);


                if (cameraSetting.cont > 1) {
                    cameraSetting.contCount = cameraSetting.cont;

                    contTimer = new Timer();
                    contTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {


                            cameraSetting.contCount--;
                            if (cameraSetting.contCount > 0) {

                                mCamera.takePicture(null, null, mPicture);
                                captureAnimationHandler.sendEmptyMessage(0);
                            } else {
                                //사진 촬영 종료
                                normalHandler.sendEmptyMessage(0);
                                contTimer.cancel();
                            }
                        }
                    }, Constants.CONTINUOUS_CAPTRUE_SECONDS, Constants.CONTINUOUS_CAPTRUE_SECONDS);

                } else {

                    //Constants.CAPTURE_SECONDS 초 후에 takingPicture =false로 바꿔 준다 -> 이 후 부터 다시 사진 촬영 가능
                    normalCaptrueTimer = new Timer();
                    normalCaptrueTimer.schedule(new TimerTask() {
                        @Override
                        public void run() {

                            //사진 촬영 종료
                            normalHandler.sendEmptyMessage(0);

                            normalCaptrueTimer.cancel();


                        }
                    }, Constants.CAPTURE_SECONDS);
                }
            }

//            }

        });


    }

    //사진 촬영 애니메이션
    class CaptureAnimationHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {

            final Animation animation = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
            animation.setDuration(Constants.CAPTURE_BLINK_SECONDS); // duration - half a second
            animation.setInterpolator(new LinearInterpolator()); // do not alter animation rate
            mPreview.startAnimation(animation);

        }
    }
    //사진촬영 종료 설정
    class NormalCaptureHandler extends Handler{
        @Override
        public void handleMessage(Message msg) {

            takingPicture=false;

        }
    }

    private void setLayout(int layout)
    {

        switch(layout)
        {

            case EXTRA_LAYOUT:
                contShootLayout.setVisibility(View.GONE);
                extraFeatureLayout.setVisibility(View.VISIBLE);
                cameraPreview.bringChildToFront(extraFeatureLayout);
                extraFeatureLayout.invalidate();
                extraFeatureLayout.requestLayout();
                break;
            case CONT_LAYOUT:
                extraFeatureLayout.setVisibility(View.GONE);
                contShootLayout.setVisibility(View.VISIBLE);
                cameraPreview.bringChildToFront(contShootLayout);
                contShootLayout.invalidate();
                contShootLayout.requestLayout();
                break;
            case GRID_Layout:
                gridLayout.setVisibility(View.VISIBLE);
                cameraPreview.bringChildToFront(gridLayout);
                gridLayout.invalidate();
                gridLayout.requestLayout();
                break;
            case HORIZONTAL_HALF_LAYOUT:
                //1:1비율 위한 vertical layout의 height설정
                int ratio11LayoutHeight=(cameraPreview.getHeight()-cameraPreview.getWidth())/2;
                //onCreate에서 호출 되어 view의 width, height을 계산하기 힘든 경우
                if(ratio11LayoutHeight==0)
                {

                    cameraPreview.getViewTreeObserver().addOnGlobalLayoutListener(
                            new ViewTreeObserver.OnGlobalLayoutListener() {

                                @Override
                                public void onGlobalLayout() {

                                    Log.d("jyr", "cameraPreview onGlobalLayout");
                                    //cameraPreview의 상단 하단에 1:1비율을 위한 검은색 공백 크기 할당
                                    int ratio11LayoutHeight=(cameraPreview.getHeight()-cameraPreview.getWidth())/2;
                                    horizontalTopLayout.setPadding(0, ratio11LayoutHeight, 0, 0);
                                    horizontalBottomLayout.setPadding(0, ratio11LayoutHeight, 0, 0);


                                    // only want to do this once
                                    if (Build.VERSION.SDK_INT < 16) {
                                        cameraPreview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    } else {
                                        cameraPreview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    }


                                }
                            });
                }
                else {
                    horizontalTopLayout.setPadding(0, ratio11LayoutHeight, 0, 0);
                    horizontalBottomLayout.setPadding(0, ratio11LayoutHeight, 0, 0);
                }
                bringLayoutToFront();


                horizontalLayout.setVisibility(View.VISIBLE);
                horizontalLayout.requestLayout();
                horizontalLayout.invalidate();
                break;
            case TIMER_LAYOUT:
                timerText.setVisibility(View.VISIBLE);
                cameraPreview.bringChildToFront(timerText);
                timerText.invalidate();
                timerText.requestLayout();
                break;
            case MULT_PICUTRE_TEXT_LAYOUT:
                multPictureText.setVisibility(View.VISIBLE);
                cameraPreview.bringChildToFront(multPictureText);
                multPictureText.invalidate();
                multPictureText.requestLayout();
                break;
            case VERTICAL_HALF_LAYOUT:
                //1:1비율 위한 vertical layout의 height설정
                int verticalWidth=cameraPreview.getWidth()/4;

                if(verticalWidth==0)
                {
                    cameraPreview.getViewTreeObserver().addOnGlobalLayoutListener(
                            new ViewTreeObserver.OnGlobalLayoutListener() {

                                @Override
                                public void onGlobalLayout() {


                                    int verticalWidth=cameraPreview.getWidth()/4;
                                    verticalLeftLayout.setPadding(verticalWidth, 0, 0, 0);
                                    verticalRightLayout.setPadding(verticalWidth, 0, 0, 0);


                                    // only want to do this once
                                    if (Build.VERSION.SDK_INT < 16) {
                                        cameraPreview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    } else {
                                        cameraPreview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    }


                                }
                            });
                }
                else {
                    verticalLeftLayout.setPadding(verticalWidth, 0, 0, 0);
                    verticalRightLayout.setPadding(verticalWidth, 0, 0, 0);
                }

                bringLayoutToFront();


                verticalLayout.setVisibility(View.VISIBLE);
                verticalLayout.requestLayout();
                verticalLayout.invalidate();
                break;
            case VERTICAL_TWO_THIRD_LAYOUT:

                //1:1비율 위한 vertical layout의 height설정
                int verticalTwoThirdWidth=cameraPreview.getWidth()/3;

                if(verticalTwoThirdWidth==0)
                {
                    cameraPreview.getViewTreeObserver().addOnGlobalLayoutListener(
                            new ViewTreeObserver.OnGlobalLayoutListener() {

                                @Override
                                public void onGlobalLayout() {

                                    int verticalTwoThirdWidth=cameraPreview.getWidth()/3;
                                    verticalLeftLayout.setPadding(verticalTwoThirdWidth, 0, 0, 0);
                                    verticalRightLayout.setPadding(verticalTwoThirdWidth, 0, 0, 0);


                                    // only want to do this once
                                    if (Build.VERSION.SDK_INT < 16) {
                                        cameraPreview.getViewTreeObserver().removeGlobalOnLayoutListener(this);
                                    } else {
                                        cameraPreview.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                    }


                                }
                            });
                }
                else {
                    verticalLeftLayout.setPadding(verticalTwoThirdWidth, 0, 0, 0);
                    verticalRightLayout.setPadding(verticalTwoThirdWidth, 0, 0, 0);
                }
                bringLayoutToFront();

                verticalLayout.setVisibility(View.VISIBLE);
                verticalLayout.requestLayout();
                verticalLayout.invalidate();

                break;

        }

    }

    private void bringLayoutToFront()
    {
        cameraPreview.bringChildToFront(verticalLayout);
        cameraPreview.bringChildToFront(horizontalLayout);
        cameraPreview.bringChildToFront(gridLayout);
        cameraPreview.bringChildToFront(multPictureText);
        cameraPreview.bringChildToFront(contShootLayout);
    }

    private void resetLayout(int layout)
    {
        switch(layout)
        {
            case EXTRA_LAYOUT:
                extraFeatureLayout.setVisibility(View.GONE);
                break;
            case CONT_LAYOUT:
                contShootLayout.setVisibility(View.GONE);
                break;
            case GRID_Layout:
                gridLayout.setVisibility(View.GONE);
                break;
            case HORIZONTAL_HALF_LAYOUT:
                horizontalLayout.setVisibility(View.GONE);
                break;
            case TIMER_LAYOUT:
                timerText.setVisibility(View.GONE);
                break;
            case MULT_PICUTRE_TEXT_LAYOUT:
                multPictureText.setVisibility(View.GONE);
                break;
            case VERTICAL_TWO_THIRD_LAYOUT:
            case VERTICAL_HALF_LAYOUT:
                verticalLayout.setVisibility(View.GONE);
                break;
        }
    }
    OnClickListener switchCameraListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            //test
//            cameraPreview.bringChildToFront(verticalLayout);
//            verticalLayout.requestLayout();
//            verticalLayout.invalidate();
            //get the number of cameras
            int camerasNumber = Camera.getNumberOfCameras();
            if (camerasNumber > 1) {
                //release the old camera instance
                //switch camera, from the front and the back and vice versa

                releaseCamera();
                chooseCamera();
            } else {
                Toast toast = Toast.makeText(myContext, "Sorry, your phone has only one camera!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    };

    OnClickListener flashBtnListener=new OnClickListener() {

        @Override
        public void onClick(View v) {

            if(cameraSetting.cameraFront)
            {
                Log.d("jyr","front camera can't use the flash");
                return;
            }

            Camera.Parameters mCameraParameter;
            mCameraParameter = mCamera.getParameters();

            String newMode;
            switch(cameraSetting.flash)
            {
                //플래시 꺼짐 -> 플래시 auto
                case Camera.Parameters.FLASH_MODE_OFF:
                    newMode=Camera.Parameters.FLASH_MODE_AUTO;

                    break;
                //플래시 auto -> 항상
                case Camera.Parameters.FLASH_MODE_AUTO:
                    newMode=Camera.Parameters.FLASH_MODE_TORCH;
                    break;
                //플래시 항상 -> 꺼짐
                case Camera.Parameters.FLASH_MODE_TORCH:
                    newMode=Camera.Parameters.FLASH_MODE_OFF;

                    break;
                default:
                    newMode=Camera.Parameters.FLASH_MODE_OFF;
            }

            //버튼 모양 설정
            setFlashBtn(newMode);
            mCameraParameter.setFlashMode(newMode);
            cameraSetting.setFlash(newMode);


            mCamera.setParameters(mCameraParameter);

        }
    };

    OnClickListener extraFeatureBtnListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("jyr", "extraFeatureBtn click");

            //추가 기능 레이아웃 표시
            if(extraFeatureLayout.getVisibility()==View.GONE) {
                setLayout(EXTRA_LAYOUT);
            }
            else{
                resetLayout(EXTRA_LAYOUT);
            }
        }
    };

    OnClickListener gridBtnListener=new OnClickListener() {

        @Override
        public void onClick(View view) {
            Log.d("jyr", "gridBtn click listener");

            setGrid(!cameraSetting.gridSet);

        }
    };

    OnClickListener oneTouchBtnListener=new OnClickListener() {

        @Override
        public void onClick(View view) {

            setOneTouch(!cameraSetting.oneTouch);

        }
    };


    OnClickListener timerBtnListener=new OnClickListener() {
        @Override
        public void onClick(View view) {
            if(cameraSetting.takingTimerPicture)
            {
                return;
            }

            switch(cameraSetting.timerCount)
            {
                default:
                case Constants.PREF_TIMER_UNSET:
                    setTimer(Constants.PREF_TIMER_3);

                    break;
                case Constants.PREF_TIMER_3:
                    setTimer(Constants.PREF_TIMER_5);
                    break;
                case Constants.PREF_TIMER_5:
                    setTimer(Constants.PREF_TIMER_10);
                    break;
                case Constants.PREF_TIMER_10:
                    setTimer(Constants.PREF_TIMER_UNSET);
                    break;
            }
        }
    };

    //연속촬영 버튼 클릭
    OnClickListener contShootBtnListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("jyr", "contShootBtn click");

            //추가 기능 레이아웃 표시
            if(contShootLayout.getVisibility()==View.GONE) {

                setLayout(CONT_LAYOUT);
            }
            else{
                resetLayout(CONT_LAYOUT);
            }
        }
    };


    OnClickListener ratio11BtnListener=new OnClickListener() {
        @Override
        public void onClick(View v) {

            setRatio(Constants.PREF_RATIO_11);


        }
    };

    OnClickListener ratio34BtnListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            setRatio(Constants.PREF_RATIO_34);
        }
    };

    OnClickListener cont1BtnListener=new OnClickListener() {
        @Override
        public void onClick(View v) {

            setCont(Constants.PREF_CONT_1);
        }
    };

    OnClickListener cont2VerticalListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            setCont(Constants.PREF_CONT_2_VERTICAL);
        }
    };

    OnClickListener cont3VerticalListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            setCont(Constants.PREF_CONT_3_VERTICAL);

        }
    };

    OnClickListener cont4BtnListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            setCont(Constants.PREF_CONT_4);
        }
    };

    OnClickListener directModeBtnListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            setMode(Constants.PREF_CAMERA_DIRECT_MODE);
        }
    };
    OnClickListener onePictureModeBtnListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            setMode(Constants.PREF_CAMERA_ONE_PICTURE_MODE);
        }
    };
    OnClickListener multPictureModeBtnListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            setMode(Constants.PREF_CAMERA_MULTIPLE_PICTURE_MODE);
        }
    };

    OnClickListener multPictureAcceptBtnListener=new OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(activity,PictureModifyActivity.class);
            intent.putExtra(PictureModifyActivity.MODE,Constants.PREF_CAMERA_MULTIPLE_PICTURE_MODE);
            intent.putExtra(PictureModifyActivity.IMAGE_PATH_ARRAY, arItem);
//                        intent.putExtra(PictureModifyActivity.IMAGE_BYTE,ImageManipulate.getImage_byte(activity,cameraSetting.ratio,data,R.mipmap.logo, ImageManipulate.getRotate(orientationListener.getRememberedOrientation(),cameraSetting.cameraFront)));
            startActivity(intent);

            //reset multiple picture mode
            multPictureAcceptBtn.setVisibility(View.GONE);
            arItem=new ArrayList<String >();
            resetLayout(MULT_PICUTRE_TEXT_LAYOUT);

        }
    };

    OnClickListener shareConfigBtnListener=new OnClickListener() {
        @Override
        public void onClick(View v) {

            startActivity(new Intent(activity, ShareActivity.class));
        }
    };
    /**
     * set functions
     * called by initCamera in Resume() function
     *
     * preferance setting
     */

    private void setGrid(boolean gridSet)
    {


        Log.d("jyr", "grid set" + gridSet);
        if(gridSet)
        {
            setLayout(GRID_Layout);

        }
        else{
            resetLayout(GRID_Layout);
        }

        cameraSetting.setGridSet(gridSet);
    }



    private void setTimer(int timerSet)
    {
        Log.d("jyr", "setTimer=" + timerSet);
        switch(timerSet)
        {
            default:
            case Constants.PREF_TIMER_UNSET:
                resetLayout(TIMER_LAYOUT);
                cameraSetting.setTimerCount(Constants.PREF_TIMER_UNSET);
                break;
            case Constants.PREF_TIMER_3:
                setLayout(TIMER_LAYOUT);
                timerText.setText("3");
                cameraSetting.setTimerCount(Constants.PREF_TIMER_3);
                break;
            case Constants.PREF_TIMER_5:
                setLayout(TIMER_LAYOUT);
                timerText.setText("5");
                cameraSetting.setTimerCount(Constants.PREF_TIMER_5);
                break;
            case Constants.PREF_TIMER_10:
                setLayout(TIMER_LAYOUT);
                timerText.setText("10");
                cameraSetting.setTimerCount(Constants.PREF_TIMER_10);
                break;
        }

//        util.editor.putInt(Constants.PREF_TIMER, timerSet).commit();
    }



    private void setOneTouch(boolean oneTouch)
    {
        cameraSetting.setOneTouch(oneTouch);
//        this.oneTouch=oneTouch;
        if(oneTouch==true)
        {
            cameraPreview.setOnClickListener(null);
            cameraPreview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(extraFeatureLayout.getVisibility()==View.VISIBLE)
                    {
                        resetLayout(EXTRA_LAYOUT);
                        return;
                    }
                    else if(contShootLayout.getVisibility()==View.VISIBLE)
                    {
                        resetLayout(CONT_LAYOUT);
                        return;
                    }





                    captruePicture();


                }
            });

        }
        else{
            cameraPreview.setOnClickListener(null);
            cameraPreview.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(extraFeatureLayout.getVisibility()==View.VISIBLE)
                    {
                        resetLayout(EXTRA_LAYOUT);
                        return;
                    }
                    else if(contShootLayout.getVisibility()==View.VISIBLE)
                    {
                        resetLayout(CONT_LAYOUT);
                        return;
                    }
                    else {
                        mCamera.autoFocus(new Camera.AutoFocusCallback() {
                            @Override
                            public void onAutoFocus(boolean success, Camera camera) {
                                Log.d("jyr","touch autoFocus");
                            }
                        });
                    }

                }
            });

        }
//        util.editor.putBoolean(Constants.PREF_ONE_TOUCH, oneTouch).commit();
    }



    private void setFlashBtn(String mode)
    {
        switch(mode)
        {
            //플래시 꺼짐 -> 플래시 auto
            case Camera.Parameters.FLASH_MODE_OFF:
                flashBtn.setImageResource(R.mipmap.ic_action_flash_off);
                break;
            //플래시 auto -> 항상
            case Camera.Parameters.FLASH_MODE_AUTO:
                flashBtn.setImageResource(R.mipmap.ic_action_flash_automatic);
                break;
            //플래시 항상 -> 꺼짐
            case Camera.Parameters.FLASH_MODE_TORCH:
                flashBtn.setImageResource(R.mipmap.ic_action_flash_on);

                break;
            default:
                flashBtn.setImageResource(R.mipmap.ic_action_flash_off);
        }
    }

    private  void setRatio(int ratio)
    {
        cameraSetting.setRatio(ratio);
        switch(ratio)
        {
            case Constants.PREF_RATIO_11:
                ratio11Btn.setSelected(true);
                ratio11Text.setSelected(true);
                ratio34Btn.setSelected(false);
                ratio34Text.setSelected(false);
                setLayout(HORIZONTAL_HALF_LAYOUT);
//                mPreview.requestLayout();
                break;
            case Constants.PREF_RATIO_34:
                ratio34Btn.setSelected(true);
                ratio34Text.setSelected(true);
                ratio11Btn.setSelected(false);
                ratio11Text.setSelected(false);
                resetLayout(HORIZONTAL_HALF_LAYOUT);
//                mPreview.requestLayout();
                break;
        }

    }
    private void setCont(int cont)
    {
        cameraSetting.setCont(cont);
        switch (cont)
        {
            case Constants.PREF_CONT_1:

                cont1Btn.setSelected(true);
                cont2VerticalBtn.setSelected(false);
                cont3VerticalBtn.setSelected(false);
                cont4Btn.setSelected(false);
                resetLayout(VERTICAL_HALF_LAYOUT);

                break;
            case Constants.PREF_CONT_2_VERTICAL:

                cont1Btn.setSelected(false);
                cont2VerticalBtn.setSelected(true);
                cont3VerticalBtn.setSelected(false);
                cont4Btn.setSelected(false);
                setLayout(VERTICAL_HALF_LAYOUT);
                break;
            case Constants.PREF_CONT_3_VERTICAL:

                cont1Btn.setSelected(false);
                cont2VerticalBtn.setSelected(false);
                cont3VerticalBtn.setSelected(true);
                cont4Btn.setSelected(false);
                setLayout(VERTICAL_TWO_THIRD_LAYOUT);
                break;
            case Constants.PREF_CONT_4:

                cont1Btn.setSelected(false);
                cont3VerticalBtn.setSelected(false);
                cont2VerticalBtn.setSelected(false);
                cont4Btn.setSelected(true);
                resetLayout(VERTICAL_TWO_THIRD_LAYOUT);
                break;
        }
    }

    private void setMode(int mode)
    {
        cameraSetting.setMode(mode);
        switch(mode)
        {
            case Constants.PREF_CAMERA_DIRECT_MODE:
                directModeBtn.setSelected(true);
                onePictureModeBtn.setSelected(false);
                multPictureModeBtn.setSelected(false);
                break;
            case Constants.PREF_CAMERA_ONE_PICTURE_MODE:
                directModeBtn.setSelected(false);
                onePictureModeBtn.setSelected(true);
                multPictureModeBtn.setSelected(false);
                break;
            case Constants.PREF_CAMERA_MULTIPLE_PICTURE_MODE:
                directModeBtn.setSelected(false);
                onePictureModeBtn.setSelected(false);
                multPictureModeBtn.setSelected(true);
                break;
        }

    }


}
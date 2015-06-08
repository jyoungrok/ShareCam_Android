//package com.claude.sharecam.login;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.content.IntentSender;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.ActionBarActivity;
//import android.util.Log;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.View;
//import android.widget.FrameLayout;
//import android.widget.LinearLayout;
//
//import com.claude.sharecam.Constants;
//import com.claude.sharecam.R;
//import com.claude.sharecam.TestActivity;
//import com.claude.sharecam.Util;
//import com.claude.sharecam.api.ErrorCode;
//import com.claude.sharecam.api.RestCallBack;
//import com.claude.sharecam.response.Federation;
//import com.claude.sharecam.response.RestError;
//import com.claude.sharecam.response.User;
//import com.claude.sharecam.camera.CameraActivity;
//import com.facebook.CallbackManager;
//import com.facebook.FacebookCallback;
//import com.facebook.FacebookException;
//import com.facebook.FacebookSdk;
//import com.facebook.Profile;
//import com.facebook.ProfileTracker;
//import com.facebook.appevents.AppEventsLogger;
//import com.facebook.login.LoginManager;
//import com.facebook.login.LoginResult;
//import com.facebook.login.widget.LoginButton;
//import com.google.android.gms.auth.GoogleAuthUtil;
//import com.google.android.gms.common.ConnectionResult;
//import com.google.android.gms.common.GooglePlayServicesUtil;
//import com.google.android.gms.common.SignInButton;
//import com.google.android.gms.common.api.GoogleApiClient;
//import com.google.android.gms.common.api.Scope;
//import com.google.android.gms.plus.Plus;
//import com.google.android.gms.plus.model.people.Person;
//import java.util.Set;
//import retrofit.client.Response;
//
///**
//
// 로그인을 위한 Activity
// 로그인 되어 있는 경우 자동 로그 아웃
// **/
//
//public class LoginActivity extends ActionBarActivity   {
//
//
//    //google login
//    private static final int REQUEST_GOOGLE_LOGIN = 9000;
//    String googleAccessToken;
//
//    /* Client used to interact with Google APIs. */
//    private GoogleApiClient mGoogleApiClient;
//    private SignInButton googleSignInBtn;
//    private ConnectionResult mConnectionResult;
//    /**
//     * True if the sign-in button was clicked.  When true, we know to resolve all
//     * issues preventing sign-in without waiting.
//     */
//    private boolean mSignInClicked;
//
//    /**
//     * True if we are in the process of resolving a ConnectionResult
//     */
//    private boolean mIntentInProgress;
//
//
//
//    private static final int BEFORE_LOGIN=0;
//    private static final int DOING_LOGIN=1;
//    private static final int AFTER_LOGIN=2;
//
//    Activity myAcitivty;
//    //facebook login
//    CallbackManager facebookCallbackManager;
//    LoginButton facebookLoginBtn;
//    LinearLayout loginLayout;
//    ProfileTracker facebookProfileTracker;
//    LinearLayout loginProgressLayout;
//    FrameLayout loginContainer;
//
//    //사용자 장보
////    PrefUser prefUser;
//
//
//
//    Context context;
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        facebookCallbackManager = CallbackManager.Factory.create();
//        setContentView(R.layout.activity_login);
//
//        //로그인 취소
//        ((Util) getApplicationContext()).editor.putBoolean(Constants.PREF_LOGIN,false);
////        startActivity(new Intent(this, TestActivity.class));
//
//        myAcitivty=this;
//        loginLayout=(LinearLayout)findViewById(R.id.loginLayout);
//        loginProgressLayout=(LinearLayout)findViewById(R.id.loginProgressLayout);
//        loginContainer=(FrameLayout)findViewById(R.id.loginContainer);
//        googleSignInBtn=(SignInButton)findViewById(R.id.googleSignIn);
//
//        getSupportActionBar().hide();
//
////        prefUser=new PrefUser();
//
//
//        context=this;
//        /**
//         * 로그인 되어 있는 경우 로그 아웃
//         */
//        Util.logout(this);
//
//
//        /**
//         * facebook login
//         */
//
//        //로그인 되어 있는 경우 log out
//        FacebookSdk.sdkInitialize(getApplicationContext());
//        LoginManager.getInstance().logOut();
//
//        facebookLoginBtn = (LoginButton) findViewById(R.id.facebookSignIn);
//
//
//
////        facebookLoginBtn.setReadPermissions(Arrays.asList("user_friends","email"));
//        // Callback registration
//        facebookLoginBtn.registerCallback(facebookCallbackManager, new FacebookCallback<LoginResult>() {
//            //페이스북 로그인 성공
//            @Override
//            public void onSuccess(LoginResult loginResult) {
//                // App code
//                Log.d("jyr", "onSuccess facebook login");
////                Log.d("jyr", Profile.getCurrentProfile().getId());
//
//                Log.d("jyr", "facebook access token = " + loginResult.getAccessToken().getToken());
//
//                login(Constants.FACEBOOK_LOGIN, Profile.getCurrentProfile().getId(), loginResult.getAccessToken().getToken());
//
//
//            }
//
//            @Override
//            public void onCancel() {
//                // App code
//                Log.d("jyr", "onCancel facebook login");
//            }
//
//            @Override
//            public void onError(FacebookException exception) {
//                // App code
//                Log.d("jyr","onError facebook login");
//            }
//        });
//
//
//        /**
//         * Google Login
//         */
//        mSignInClicked=false;
//        mIntentInProgress=false;
//        googleSignInBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
////
////                if (!mGoogleApiClient.isConnecting()) {
////                    mSignInClicked = true;
////                    resolveSignInError();
////                }
//                mSignInClicked = true;
//                googlePlusLogin();
//
//            }
//        });
//
//        mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
//            @Override
//            public void onConnected(Bundle bundle) {
//
//                Log.i("jyr", "google connected");
//                //로그인 버튼 눌러 연결 된 경우
//                if(mSignInClicked)
//                    login(Constants.GOOGLE_LOGIN, Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getId(), googleAccessToken);
//
//                else {
//
//
//                    Log.i("jyr", "google disconnected");
//
//
//                    googlePlusLogout();
//
//                }
////                Person p =Plus.PeopleApi.getCurrentPerson(mGoogleApiClient);
//                mSignInClicked = false;
//
//
//
//            }
//
//            @Override
//            public void onConnectionSuspended(int i) {
//                mGoogleApiClient.connect();
////                updateProfile(false);
//
//            }
//        }).addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
//            @Override
//            public void onConnectionFailed(ConnectionResult result) {
//                if (!mIntentInProgress) {
//                    mConnectionResult=result;
//
//                    if (mSignInClicked) {
//                        resolveSignInError();
//                    }
//                }
//
//            }
//        }).requestServerAuthCode(Constants.GOOGLE_SERVER_CLIENT_ID, new GoogleApiClient.ServerAuthCodeCallbacks() {
//            @Override
//            public CheckResult onCheckServerAuthorization(String idToken, Set<Scope> scopeSet) {
//
////                String id = Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getId();
//                Log.d("jyr", "google onCheckServerAuthorization");
//
//
//                googleAccessToken=idToken;
//                Log.d("jyr","google access token="+idToken);
//
//                return CheckResult.newAuthNotRequiredResult();
//            }
//
//            @Override
//            public boolean onUploadServerAuthCode(String idToken, String serverAuthCode) {
//                Log.d("jyr","google onUploadServerAuthCode");
//                return false;
//            }
//        })
//                .addApi(Plus.API, Plus.PlusOptions.builder().build()).addScope(Plus.SCOPE_PLUS_LOGIN).build();
//
//    }
//
//    protected void onStart() {
//        super.onStart();
//        Log.d("jyr","onStart");
//        mGoogleApiClient.connect();
//    }
//
//    protected void onStop() {
//        super.onStop();
//        if (mGoogleApiClient.isConnected()) {
//            mGoogleApiClient.disconnect();
//        }
//    }
//
//    //페이스북 혹은 구글 로그인 후에 서버로 로그인 요청
//    //회원가입이 되어 있었던 경우 -> 카메라 화면으로
//    //회원가입이 안되어있는 경우 -> 휴대폰 인증
//    public void login(final int type, String type_id, final String access_token)
//    {
//        Log.d("jyr","login");
//        //로그인 중
//        setLoginLayout(DOING_LOGIN);
///*
//        // 서버에 로그인 요청 -> 프로필 업데이트 -> 파일서버 접속권한 받아옴
//        ((Util) getApplicationContext()).apiManager.server.login(type, type_id, access_token, new RestCallBack<User>() {
//
//            @Override
//            public void failure(RestError restError) {
//                Log.d("jyr","login fail");
//                Util.showToast(context, ErrorCode.getToastMessageId(context, restError));
//                setLoginLayout(BEFORE_LOGIN);
//            }
//
//            @Override
//            public void success(User user, Response response) {
//
//                //회원 가입이 되어 있는 경우 -> 바로 이동
//                //pref 설정
//                if (user.completed>0) {
//
//                    //preferance login 설정
//
//                    Intent intent = new Intent(context, CameraActivity.class);
//                    startActivity(intent);
//                }
//
//                //회원 가입 과정이 마무리 되어있지 않은 경우
//                else {
//                    //회원 이름, 프로필 update 이후 전화인증 화면으로
//                    String profileName=null;
//                    String profileURL=null;
//                    if(type==Constants.FACEBOOK_LOGIN)
//                    {
//                        profileName=Profile.getCurrentProfile().getName();
//                        profileURL=Profile.getCurrentProfile().getProfilePictureUri(128, 128).toString();
//                    }
//                    else if(type==Constants.GOOGLE_LOGIN)
//                    {
//                        profileName=Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getDisplayName();
//                        profileURL=Plus.PeopleApi.getCurrentPerson(mGoogleApiClient).getImage().getUrl();
//                    }
//
//
//                    ((Util) getApplicationContext()).apiManager.server.updateUser(user.id, profileName, profileURL,0, new RestCallBack<User>() {
//                        @Override
//                        public void failure(RestError restError) {
//                            Log.d("jyr","update user fail");
//                            Util.showToast(context, ErrorCode.getToastMessageId(context, restError));
//                            setLoginLayout(BEFORE_LOGIN);
//                        }
//
//                        @Override
//                        public void success(User user, Response response) {
//
//                            Util.setUserPref(context, user, access_token);
//
//                            //로그인 성공 -> 다음 화면으로
//                            //파일서버 접근 권한 받아옴
//                            ((Util) getApplicationContext()).apiManager.server.getFederation(new RestCallBack<Federation>() {
//                                @Override
//                                public void failure(RestError restError) {
//
//                                    Log.d("jyr","federation fail");
//                                    Util.showToast(context, ErrorCode.getToastMessageId(context, restError));
//                                    setLoginLayout(BEFORE_LOGIN);
//                                }
//
//                                @Override
//                                public void success(Federation federation, Response response) {
//
//                                    setLoginLayout(AFTER_LOGIN);
//                                    //federation preferance setting
//                                    Util.setFederationPref(context, federation);
//                                    Util.startFragment(getSupportFragmentManager(), R.id.loginContainer, new PhoneVerifyFragment(), false, null);
//
//                                }
//                            });
//
//                        }
//                    });
//
//
//                }
//
//            }
//        });
//*/
//    }
//
//    public void setLoginLayout(int state)
//    {
//
//        switch(state)
//        {
//            case BEFORE_LOGIN:
//                loginLayout.setVisibility(View.VISIBLE);
//                loginProgressLayout.setVisibility(View.GONE);
//                break;
//            case DOING_LOGIN:
//                loginLayout.setVisibility(View.GONE);
//                loginProgressLayout.setVisibility(View.VISIBLE);
//                break;
//            case AFTER_LOGIN:
//                loginLayout.setVisibility(View.GONE);
//                loginProgressLayout.setVisibility(View.GONE);
//                break;
//        }
//    }
//
//    public void setContainerLayout(int state)
//    {
//        switch(state)
//        {
//            case Constants.AFTER_REQUEST:
//            case Constants.BEFORE_REQUEST:
//                loginContainer.setVisibility(View.VISIBLE);
//                loginProgressLayout.setVisibility(View.GONE);
//                break;
//            case Constants.DOING_REQUEST:
//                loginContainer.setVisibility(View.GONE);
//                loginProgressLayout.setVisibility(View.VISIBLE);
//                break;
//
//        }
//    }
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//
//        //fragment onactivity result
//        Fragment fragment = getSupportFragmentManager().findFragmentByTag(InputProfileFragment.TAG);
//        if (fragment != null) {
//            ((InputProfileFragment) fragment).onActivityResult(requestCode, resultCode, data);
//        }
//
//        else if(requestCode==REQUEST_GOOGLE_LOGIN)
//        {
//
//            if (resultCode != RESULT_OK) {
//                mSignInClicked = false;
//            }
//
//            mIntentInProgress = false;
//
//            if (!mGoogleApiClient.isConnected()) {
//                mGoogleApiClient.connect();
//            }
//
//        }
//
//
//
//        //facebook login
//        else
//            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.menu_login, menu);
//        return true;
//    }
//
//
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//
//        // Logs 'install' and 'app activate' App Events.
//        AppEventsLogger.activateApp(this);
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//
//        // Logs 'app deactivate' App Event.
//        AppEventsLogger.deactivateApp(this);
//    }
//
//
//    /**
//     *
//     * Google API
//     *
//     */
//
//
//
//    //google plus login
//    private void googlePlusLogin() {
//        //연결되어있는 경우 로그 아웃
//        if(mGoogleApiClient.isConnected())
//        {
//            googlePlusLogout();
//            return;
//        }
//        if (!mGoogleApiClient.isConnecting()) {
//            mSignInClicked = true;
//            resolveSignInError();
//        }
//
//    }
//
//    private void resolveSignInError() {
//        if (mConnectionResult.hasResolution()) {
//            try {
////                mIntentInProgress = true;
//                mConnectionResult.startResolutionForResult(this, REQUEST_GOOGLE_LOGIN);
//            } catch (IntentSender.SendIntentException e) {
////                mIntentInProgress = false;
//                mGoogleApiClient.connect();
//            }
//        }
//    }
//
//
//
//    private void googlePlusLogout() {
//        if (mGoogleApiClient.isConnected()) {
//            Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
//            mGoogleApiClient.disconnect();
//            mGoogleApiClient.connect();
//        }
//    }
//
//
//
//
//}
//
//

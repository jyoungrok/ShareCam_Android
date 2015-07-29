package com.claude.sharecam;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.aviary.android.feather.sdk.IAviaryClientCredentials;
import com.claude.sharecam.api.ApiManager;
//import com.claude.sharecam.login.LoginActivity;
import com.claude.sharecam.orm.DBHelper;
import com.claude.sharecam.orm.UploadingPicture;
import com.claude.sharecam.parse.Contact;
import com.claude.sharecam.parse.Friend;
import com.claude.sharecam.parse.Group;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.Picture;
import com.claude.sharecam.parse.Individual;
import com.claude.sharecam.parse.ShareFriend;
//import com.claude.sharecam.parse.UploadingPicture;
import com.claude.sharecam.parse.Test;
import com.claude.sharecam.parse.User;
import com.claude.sharecam.response.Federation;
import com.claude.sharecam.share.IndividualItem;
import com.claude.sharecam.signup.SignUpActivity;
import com.claude.sharecam.util.ShareIndividualGson;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;


import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Claude on 15. 4. 9..
 */
public class Util extends Application implements IAviaryClientCredentials {

    public static final String TAG="Util";
    //adobe sdk
    private static final String CREATIVE_SDK_SAMPLE_CLIENT_ID = "4bd24623a672401bb64da2dd9620f48b";
    private static final String CREATIVE_SDK_SAMPLE_CLIENT_SECRET = "a824e42c-c4d9-46fe-909d-85d05d4de977";

    public ArrayList<UploadingPicture> beforeItems;//items before uploading success
    public ArrayList<UploadingPicture> afterItems;//items after uploading success

    public SharedPreferences pref;
    public SharedPreferences.Editor editor;

    public ApiManager apiManager;
//    public S3 s3;

//    public AWS aws;

    public DBHelper dbHelper;


    Context context;
    /**
     * onCreate()
     * 액티비티, 리시버, 서비스가 생성되기전 어플리케이션이 시작 중일때
     * Application onCreate() 메서드가 만들어 진다고 나와 있습니다.
     * by. Developer 사이트
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();
        pref = getSharedPreferences(Constants.PREF_NAME, 0);
        editor = pref.edit();
        context = this;
//        s3=new S3();

//        aws=new AWS();
        dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
        apiManager = ApiManager.newInstance(getApplicationContext());


        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Contact.class);
        ParseObject.registerSubclass(Friend.class);
        ParseObject.registerSubclass(User.class);
        ParseObject.registerSubclass(Individual.class);
        ParseObject.registerSubclass(ShareFriend.class);
        ParseObject.registerSubclass(Picture.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(Test.class);
//        ParseObject.registerSubclass(UploadingPicture.class);
        Parse.initialize(this, Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_KEY);
        ParseFacebookUtils.initialize(this, Constants.REQUEST_FACEBOOK_LOGIN);

        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(com.parse.ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }

        });


        syncData(context);


        /**
         * Test Code
         */
//        Log.d("jyr","start test");
//        ParseQuery<Test> query=ParseQuery.getQuery(Test.class);
//        query.findInBackground(new FindCallback<Test>() {
//            @Override
//            public void done(List<Test> list, com.parse.ParseException e) {
//                if(e==null)
//                {
//                    list.get(0).pinInBackground("Test", new SaveCallback() {
//                        @Override
//                        public void done(com.parse.ParseException e) {
//                            ParseQuery<Test> query = ParseQuery.getQuery(Test.class);
//                            query.fromPin("Test");
//                            query.findInBackground(new FindCallback<Test>() {
//                                @Override
//                                public void done(List<Test> list, com.parse.ParseException e) {
//                                    if (e == null) {
//                                        for (int i = 0; i < list.size(); i++) {
//                                            Log.d("jyr", "id=" + list.get(i).getObjectId());
//                                            Log.d("jyr","test = "+list.get(i).getString("ffsdf"));
//                                        }
//                                    }
//                                }
//                            });
//                        }
//                    });
//
//                }
//            }
//        });

        /**
         * test code
         */

//        Drawable d=getDrawable(R.mipmap.ic_action_accept); // the drawable (Captain Obvious, to the rescue!!!)
//        Bitmap bitmap = ((BitmapDrawable)d).getBitmap();
//        ByteArrayOutputStream stream = new ByteArrayOutputStream();
//        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
//        byte[] bitmapdata = stream.toByteArray();
//        final ParseFile file = new ParseFile(bitmapdata);
//        Test test=new Test();
//        test.put("file",file);
//        test.saveInBackground(new SaveCallback() {
//            @Override
//            public void done(com.parse.ParseException e) {
//                if(e==null)
//                {
//                    Log.d("jyr","test data save success");
//                }
//                else
//                    Log.d("jyr","test data save fail");
//            }
//        });
//
//
//        ParseQuery<Test> query=ParseQuery.getQuery(Test.class);
//        query.whereEqualTo("objectId","qupcsitmLL");
//        query.findInBackground(new FindCallback<Test>() {
//            @Override
//            public void done(List<Test> list, com.parse.ParseException e) {
//
//                if(e)
//            }
//        });



//        initUploadingData();

    }

    /**
     * 서버에서 변환된 데이터들을 동기화
     */
    public static void syncData(final Context context)
    {
        if(ParseUser.getCurrentUser()!=null) {

            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                }
            };

            Thread thread=new Thread(null, new Runnable() {
                @Override
                public void run() {

                    try {
                        Calendar friendCal=Calendar.getInstance();
                        friendCal.setTime(Util.getFriendLastUpdatedAt(context));
                        //최소 동기화 시간이 지난 경우 동기화 시도
                        if(Calendar.getInstance().getTimeInMillis()-friendCal.getTimeInMillis()>Constants.SYNC_FRIEND_GAP) {

                            //친구 연락처 목록 동기화
                            ParseAPI.syncFriendList(context);
                        }
                        else {
                            Log.d(TAG,"not sync friendList ");
                        }

                        Calendar contactCal=Calendar.getInstance();
                        contactCal.setTime(Util.getContactSyncTime(context));
                        //최소 동기화 시간이 지난 경우 동기화 시도
                        if(Calendar.getInstance().getTimeInMillis()-contactCal.getTimeInMillis()>Constants.SYNC_CONTACT_GAP) {
                            //연락처 동기화
                            ParseAPI.syncContact(context, true);
                        }
                        else {
                            Log.d(TAG,"not sync contactList");
                        }
                    } catch (com.parse.ParseException e) {

                        Log.e(TAG,"sync data error "+e.getMessage());
                        e.printStackTrace();
                    }
                }
            });
            thread.start();
        }
    }


    /**
     * onConfigurationChanged()
     * 컴포넌트가 실행되는 동안 단말의 화면이 바뀌면 시스템이 실행 한다.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }

    public static void shareImage(Context context, String path) { //공유 이미지 함수

        File file = new File(path);
        Uri mSaveImageUri = Uri.fromFile(file); //file의 경로를 uri로 변경합니다.
        Intent intent = new Intent(Intent.ACTION_SEND); //전송 메소드를 호출합니다. Intent.ACTION_SEND
        intent.setType("image/jpg"); //jpg 이미지를 공유 하기 위해 Type을 정의합니다.
        intent.putExtra(Intent.EXTRA_STREAM, mSaveImageUri); //사진의 Uri를 가지고 옵니다.
        context.startActivity(Intent.createChooser(intent, "Choose")); //Activity를 이용하여 호출 합니다.
    }

    @Override
    public String getClientID() {
        return CREATIVE_SDK_SAMPLE_CLIENT_ID;
    }

    @Override
    public String getClientSecret() {
        return CREATIVE_SDK_SAMPLE_CLIENT_SECRET;
    }


    @Override
    public String getBillingKey() {
        return "";
    }

    /**
     * uploadActivity uploadService
     */
    //DB에서 업로딩 데이터 불러옴
    public void initUploadingData() {

        beforeItems = new ArrayList<UploadingPicture>();
        afterItems = new ArrayList<UploadingPicture>();

        List<UploadingPicture> allItems = UploadingPicture.get_ne_finished_ob_createdAt(this);


        if (allItems != null) {
            Log.d("jyr", "uploading num" + allItems.size());
            for (int i = 0; i < allItems.size(); i++) {
                //업로드 완료한 경우
                if (allItems.get(i).getState() == UploadingPicture.SUCCESS_UPLOADING_STATE) {
                    afterItems.add(allItems.get(i));
                }
                //업로드 완료 전 (실패, 대기, 업로드 중)
                else {
                    beforeItems.add(allItems.get(i));
                }
            }
        }
    }

    /**
     * Login
     * set preferances
     */

    public static void setCookiePref(Context context) {


    }

    public static void setUserPref(Context context, ParseObject user) {
        SharedPreferences.Editor editor = ((Util) context.getApplicationContext()).editor;

        editor
                .putString(Constants.PREF_USER_ID, (String) user.getString("objectId"))
                .putString(Constants.PREF_USER_NAME, user.getString("username"))
                .putString(Constants.PREF_USER_PROFILE_URL, user.getString("profile"))
                .commit();
    }
/*
    public static void setUserPref(Context context,User user)
    {
        SharedPreferences.Editor editor=((Util) context.getApplicationContext()).editor;

        editor
                .putInt(Constants.PREF_USER_TYPE, user.type)
                .putInt(Constants.PREF_USER_ID, user.id)
                .putString(Constants.PREF_USER_TYPE_ID, user.type_id)
                .putString(Constants.PREF_USER_NAME, user.name)
                .putString(Constants.PREF_USER_PHONE,user.phone)
                .putString(Constants.PREF_USER_PROFILE_URL,user.profileURL)
                .commit();
    }

    public static void setUserPref(Context context,User user,String accessToken)
    {
        SharedPreferences.Editor editor=((Util) context.getApplicationContext()).editor;

        editor.putString(Constants.PREF_ACCESS_TOKEN,accessToken)
                .putInt(Constants.PREF_USER_TYPE, user.type)
                .putInt(Constants.PREF_USER_ID, user.id)
                .putString(Constants.PREF_USER_TYPE_ID, user.type_id)
                .putString(Constants.PREF_USER_NAME,user.name)
                .putString(Constants.PREF_USER_PHONE,user.phone)
                .putString(Constants.PREF_USER_PROFILE_URL, user.profileURL)

                .commit();
    }
*/



    public static int getAlubumType(Context context) {
        return ((Util) context.getApplicationContext()).pref.getInt(Constants.PREF_ALBUM_SORT_TYPE, Constants.PREF_ALBUM_SORT_TYPE_DEFAULT);
    }

    public static void setAlbumType(Context context, int type) {
        SharedPreferences.Editor editor = ((Util) context.getApplicationContext()).editor;
        editor.putInt(Constants.PREF_ALBUM_SORT_TYPE, type).commit();
    }

    public static void setFederationPref(Context context, Federation federation) {
        SharedPreferences.Editor editor = ((Util) context.getApplicationContext()).editor;

        editor.putString(Constants.PREF_FEDERATION_ACCESS_KEY, federation.Credentials.AccessKeyId)
                .putString(Constants.PREF_FEDERATION_SECRET_ACCESS_KEY, federation.Credentials.SecretAccessKey)
                .putString(Constants.PREF_FEDERATION_SESSION_TOKEN, federation.Credentials.SessionToken)
                .putString(Constants.PREF_FEDERATION_EXPIRATION, federation.Credentials.Expiration)
                .commit();
    }

    public static void logout(Context context) {
        SharedPreferences.Editor editor = ((Util) context.getApplicationContext()).editor;

        editor.putBoolean(Constants.PREF_LOGIN, false)
                .commit();
    }

    /**
     * Util
     */

    public static void showToast(Context context, int strId) {
        Toast.makeText(context, context.getResources().getString(strId), Toast.LENGTH_SHORT).show();
    }

    /**
     * Fragment
     */


    public static void startFragment(FragmentManager fm, int layout, Fragment fragment, boolean addBackStack, String TAG) {

        if (addBackStack)
            fm.beginTransaction().replace(layout, fragment, TAG).addToBackStack(null).commit();
        else
            fm.beginTransaction().replace(layout, fragment, TAG).commit();
    }

    public static String getDrawableUriString(Context context, int id) {
        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(id) + '/' + context.getResources().getResourceTypeName(id) + '/' + context.getResources().getResourceEntryName(id);
    }

    public static Calendar StringToCalendar(String time) {
        Calendar cal = Calendar.getInstance();
        SimpleDateFormat dtf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        dtf.setCalendar(cal);
        try {
            cal.setTime(dtf.parse(time));
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return cal;
    }


    public static boolean checkNetwork(Context context) {
        if (getOnlineType(context) == Constants.NETWORK_NOT_AVAILABLE) {
            Util.showToast(context, R.string.network_unavailable);
            return false;
        } else
            return true;
    }

    public static int getOnlineType(Context context) {
        try {
            ConnectivityManager conMan = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState(); // wifi
            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING)
                return Constants.NETWORK_WIFI;

            NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState();
            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING)
                return Constants.NETWORK_MOBILE;

        } catch (NullPointerException e) {
            return Constants.NETWORK_NOT_AVAILABLE;
        }
        return Constants.NETWORK_NOT_AVAILABLE;
    }

    public static String getAWSProfileURL(int user_id, String extension) {
        return Constants.AWS_PROFILE_URL + user_id + "." + extension;
    }

    //로그인이 되어 있는지 확인
    //로그인 되어 있지 않은 경우 로그인 화면으로 이동
    public static void checkLogin(Activity activity) {
//        if(((Util)activity.getApplicationContext()).pref.getBoolean(Constants.PREF_LOGIN,Constants.PREF_LOGIN_DEFAULT))
//            return;
//        else{
//            activity.startActivity(new Intent(activity, SignUpActivity.class));
//            activity.finish();
//        }


        ParseUser user = ParseUser.getCurrentUser();
//        Boolean fff=ParseUser.getCurrentUser().getBoolean("completed");

        if (ParseUser.getCurrentUser() != null && ParseUser.getCurrentUser().getBoolean("completed"))
            return;
        else {
            activity.startActivity(new Intent(activity, SignUpActivity.class));
            activity.finish();
        }

    }

    public static String getUri(Uri uri, Context context) {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public static byte[] uriToByteArray(Uri uri, Context context) throws IOException {
        InputStream iStream = context.getContentResolver().openInputStream(uri);
        byte[] inputData = getBytes(iStream);

        return inputData;
    }

    public static byte[] getBytes(InputStream inputStream) throws IOException {
        ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
        int bufferSize = 1024;
        byte[] buffer = new byte[bufferSize];

        int len = 0;
        while ((len = inputStream.read(buffer)) != -1) {
            byteBuffer.write(buffer, 0, len);
        }
        return byteBuffer.toByteArray();
    }

    //로컬 content provider를 통해 연락처 데이터 불러옴
    public static ArrayList<IndividualItem> getContactList(Context context) {


        ArrayList<IndividualItem> contactItems = new ArrayList<IndividualItem>();
        ArrayList numberList = new ArrayList();
        // 로컬에서 연락처 데이터 불러옴
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI},
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1", null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED  ASC");

        while (cursor.moveToNext()) {

            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            if (!numberList.toString().contains(phoneNumber)) {
                numberList.add(phoneNumber);
                contactItems.add(new IndividualItem(name, imageUri, Util.convertToNationalNumber(context, phoneNumber)));
//                Log.d("jyr",Util.convertToNationalNumber(context,phoneNumber));
            }
        }

        cursor.close();



        return contactItems;
    }

    public static String convertToInternationalNumber(Context context, String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            String locale = String.valueOf(context.getResources().getConfiguration().locale.getCountry());
            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(phoneNumber, locale);
            return phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            Log.e("jyr", "NumberParseException was thrown: " + e.toString());
        }
        return null;
    }

    public static String convertToNationalNumber(Context context, String phoneNumber) {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            String locale = String.valueOf(context.getResources().getConfiguration().locale.getCountry());
            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(phoneNumber, locale);
            return phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            Log.e("jyr", "NumberParseException was thrown: " + e.toString());
        }
        return null;
    }

    //preference에 공유 하고자 하는 개인(연락처 + 쉐어캠 친구) 추가
    public static void setSharePersonList(Context context, ArrayList<Individual> personItems) {
        Gson gson = new Gson();
        Log.d("jyr", gson.toJson(personItems));
        SharedPreferences.Editor editor = ((Util) context.getApplicationContext()).editor;

        ArrayList<ShareIndividualGson> gsonItems = new ArrayList<ShareIndividualGson>();
        for (int i = 0; i < personItems.size(); i++) {
            gsonItems.add(new ShareIndividualGson("dfg", "dfg", "dfg", "dfg", false));
        }
        editor.putString(Constants.PREF_SHARE_PERSON, gson.toJson(gsonItems));
        editor.commit();
    }

    //preference에서 공유 개인(연락처 + 쉐어캠 친구) 불러옴
    public static ArrayList<Individual> getSharePersonList(Context context) {
        Type resultType = new TypeToken<List<ShareIndividualGson>>() {
        }.getType();
        Gson gson = new Gson();
        return gson.fromJson(((Util) context.getApplicationContext()).pref.getString(Constants.PREF_SHARE_PERSON, Constants.PREF_SHARE_PERSON_DEFAULT), resultType);
    }

    public static String getAlbumDateFormat(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    //백버튼 누른 경우
    public static void setBackBtnListener(final ActionBarActivity actionBarActivity, final FragmentManager fm) {

        actionBarActivity.findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fm.getBackStackEntryCount() == 0) {
                    actionBarActivity.finish();
                } else {
                    fm.popBackStack();
                }
            }
        });
    }

    public static void setActionbarItems(final Activity actionBarActivity, View.OnClickListener item1Listener) {
        actionBarActivity.findViewById(R.id.actionItem1).setOnClickListener(item1Listener);
    }


    //유효한 전화번호 인지 확인
    public static boolean isValidPhoneNumber(Context context, String number) {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);

        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {

            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(mTelephonyMgr.getLine1Number(), mTelephonyMgr.getNetworkCountryIso().toUpperCase());
            if (phoneUtil.isValidNumber(phoneNumber)) {
                Log.d("jyr","valid phone number");
                return true;
            }
        } catch (NumberParseException e) {
            e.printStackTrace();
        }

        Log.d("jyr","invalid phone number");
        return false;
    }

    //International Phone number 로 변경
    public static String getE164PhoneNumber(Context context,String phoeNumber)
    {
        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        PhoneNumberUtil phoneUtil=PhoneNumberUtil.getInstance();
        try {

            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(phoeNumber,mTelephonyMgr.getNetworkCountryIso().toUpperCase());
//                        Log.d("jyr", PhoneNumberUtils.formatNumber("01033119561"));
//                        PhoneNumberUtils.formatNumberToE164()
            if(phoneUtil.isValidNumber(phoneNumber))
            {
                Log.d("jyr","+"+phoneNumber.getCountryCode()+phoneNumber.getNationalNumber());
                Log.d("jyr","valid number = "+phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164));
                return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
            }
            else{
                Log.d("jyr","invalid");
            }
//                        phoneNumberTxt.setText(phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
        } catch (NumberParseException e) {
            Log.d("jyr","phone covert error");
            e.printStackTrace();
        }

        return null;

    }

    //연락처 동기화 완료 시간 설정
    public static void setContactSyncTime(Context context)
    {
        Log.d(TAG,"setContactSyncTime");
        ((Util)context.getApplicationContext()).editor.putLong(Constants.PREF_SYNC_CONTACT_TIME, Calendar.getInstance().getTimeInMillis()).commit();
    }

    public static Date getContactSyncTime(Context context){

        long syncTime=((Util)context.getApplicationContext()).pref.getLong(Constants.PREF_SYNC_CONTACT_TIME, Constants.PREF_SYNC_CONTACT_TIME_DEFAULT);
//        Log.d(TAG,new )
        return new Date(syncTime);
    }

    //친구 동기화 완료 시간 설정
    public static void setFriendLastUpdatedAt(Context context,List<Friend> friendList)
    {
        if(friendList.size()==0)
            return;
        Log.d(TAG,"setFriendLastUpdatedAt");
        long lastUpdatedAt=0;
        //friend list중 last updatedAt찾기
        for(int i=0; i<friendList.size(); i++)
        {
            if(lastUpdatedAt<friendList.get(i).getFriendUser().getUpdatedAt().getTime()){
                lastUpdatedAt=friendList.get(i).getFriendUser().getUpdatedAt().getTime();
            }
        }
        ((Util)context.getApplicationContext()).editor.putLong(Constants.PREF_FRIEND_LATST_UPDATED_AT, lastUpdatedAt).commit();

    }

    //친구 동기화 했던 마지막 시간 얻어오기
    public static Date getFriendLastUpdatedAt(Context context)
    {

        long lastUpdatedAt=((Util)context.getApplicationContext()).pref.getLong(Constants.PREF_FRIEND_LATST_UPDATED_AT, Constants.PREF_FRIEND_LAST_UPDATED_AT_DEFAULT);
//        Log.d(TAG,new )
        return new Date(lastUpdatedAt);
    }

    public static void logCurrentThread(String TAG)
    {
        if(Looper.myLooper() == Looper.getMainLooper()) {
            // Current Thread is Main Thread.
            Log.d(TAG,"main thread");
        }
        else {
            Log.d(TAG,"another thread");
        }
    }
}

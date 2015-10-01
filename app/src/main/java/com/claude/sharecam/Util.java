package com.claude.sharecam;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.TextView;
import android.widget.Toast;

import com.aviary.android.feather.sdk.AviaryIntent;
import com.aviary.android.feather.sdk.IAviaryClientCredentials;
//import com.claude.sharecam.login.LoginActivity;
import com.claude.sharecam.camera.CameraActivity;
import com.claude.sharecam.dialog.MyDialogBuilder;
import com.claude.sharecam.orm.DBHelper;
import com.claude.sharecam.orm.UploadingPicture;
import com.claude.sharecam.parse.Contact;
import com.claude.sharecam.parse.Album;
import com.claude.sharecam.parse.Group;
import com.claude.sharecam.parse.Notification;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.Picture;
//import com.claude.sharecam.parse.Individual;
//import com.claude.sharecam.parse.UploadingPicture;
import com.claude.sharecam.parse.Test;
import com.claude.sharecam.parse.User;
import com.claude.sharecam.response.Federation;
import com.claude.sharecam.share.ContactItemList;
import com.claude.sharecam.share.ShareItem;
import com.claude.sharecam.signup.SignUpActivity;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;


import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

/**
 * Created by Claude on 15. 4. 9..
 */
public class Util extends Application implements IAviaryClientCredentials {


    public String currentActivityName;//현재 실행 중인 activity
    public boolean isFront;// 현재 앱이 켜져 있는지 여부
    public static final String TAG="Util";
    //adobe sdk
    private static final String CREATIVE_SDK_SAMPLE_CLIENT_ID = "4bd24623a672401bb64da2dd9620f48b";
    private static final String CREATIVE_SDK_SAMPLE_CLIENT_SECRET = "a824e42c-c4d9-46fe-909d-85d05d4de977";

    public ArrayList<UploadingPicture> beforeItems;//items before uploading success
    public ArrayList<UploadingPicture> afterItems;//items after uploading success

    public SharedPreferences pref;
    public SharedPreferences.Editor editor;
    public DBHelper dbHelper;


    Context context;

    public static boolean contactLoaded=false;
    //연락처 데이터들 메모리 로드 하여 사용
    public static ContactItemList contactItemList;


    /**
     * 연락처 데아터 로드 (데이터 로드 되어 있는 경우 재 로드 안하고 handler 호출)
     *  로드 시점
     *  1. 앱 실행시
     *  2. AlbumActivity
     *  3. ShareActivity
     */
    //연락처 데이터 로드
    public static void initContactItemList(final Context context,final Handler handler)
    {

        if(!contactLoaded) {
            new Thread() {
                @Override
                public void run() {
                    Util.contactItemList = new ContactItemList();

                    Log.d(TAG, "load contactItem list");
                    try {

                        Util.contactItemList.friendItems = new ArrayList<Contact>();
                        Util.contactItemList.contactItems = new ArrayList<Contact>();

                        List<Contact> allContactList = ParseAPI.getPinnedContactList(true);
                        Log.d(TAG, "contact load done");

                        for (int i = 0; i < allContactList.size(); i++) {
                            if (allContactList.get(i).getFriendUser() != null) {
                                Util.contactItemList.friendItems.add(allContactList.get(i));
                            } else {
                                Util.contactItemList.contactItems.add(allContactList.get(i));
                            }
                        }

                        Log.d(TAG, "distinguish frienad contact  done");


                        Log.d(TAG, "contactItem load done " + Util.contactItemList.contactItems.size());

                        Util.contactItemList.addedItems = ParseAPI.getShareContactList(context, Util.contactItemList.contactItems, Util.contactItemList.friendItems);

                        Log.d(TAG, "addedItem load done " + Util.contactItemList.addedItems.size());


                    } catch (com.parse.ParseException e) {
                        e.printStackTrace();
                    }

                    Util.contactLoaded = true;
                    handler.sendEmptyMessage(0);

                }
            }.start();
        }
        else{
            handler.sendEmptyMessage(0);

        }
    }

    /**
     * initContactItemList 이 후에 호출 가능
     *  특정 phone의 사용자 이름 찾기
     */
    public static String getContactNameByPhone(String phone)
    {

        String name=null;
        //이미 initContactItemList이 호출 된 경우
        if (contactItemList != null) {

            for (int i = 0; i < contactItemList.friendItems.size(); i++) {
                if (name != null)
                    break;
                if (phone.equals(contactItemList.friendItems.get(i).getPhone())) {
                    name = contactItemList.friendItems.get(i).getPinContactName();
                }
            }

            for (int i = 0; i < contactItemList.contactItems.size(); i++) {
                if (name != null)
                    break;
                if (phone.equals(contactItemList.contactItems.get(i).getPhone()))
                    name = contactItemList.contactItems.get(i).getPinContactName();
            }

            return name;
        }
        //contact데이터 로드가 되지 않은 경우
        else{
            try {
                return ParseAPI.getContactNameWithPhone(phone);
            } catch (com.parse.ParseException e) {

                e.printStackTrace();
                return null;
            }
        }

    }

    private static final class MyActivityLifecycleCallbacks implements ActivityLifecycleCallbacks {

        public void onActivityCreated(Activity activity, Bundle bundle) {
            Log.e(TAG,"onActivityCreated:" + activity.getLocalClassName());
        }

        public void onActivityDestroyed(Activity activity) {
            Log.e(TAG,"onActivityDestroyed:" + activity.getLocalClassName());
        }

        public void onActivityPaused(Activity activity) {
            ((Util)activity.getApplicationContext()).currentActivityName =activity.getLocalClassName();
            ((Util)activity.getApplicationContext()).isFront=false;
            Log.e(TAG,"onActivityPaused:" + activity.getLocalClassName());
        }

        public void onActivityResumed(Activity activity) {
            ((Util)activity.getApplicationContext()).currentActivityName =activity.getLocalClassName();
            ((Util)activity.getApplicationContext()).isFront=true;
            Log.e(TAG,"onActivityResumed:" + activity.getLocalClassName());
        }

        public void onActivitySaveInstanceState(Activity activity,
                                                Bundle outState) {
            Log.e(TAG,"onActivitySaveInstanceState:" + activity.getLocalClassName());
        }

        public void onActivityStarted(Activity activity) {
            Log.e(TAG,"onActivityStarted:" + activity.getLocalClassName());
        }

        public void onActivityStopped(Activity activity) {
            Log.e(TAG,"onActivityStopped:" + activity.getLocalClassName());
        }
    }

    /**
     * onCreate()
     * 액티비티, 리시버, 서비스가 생성되기전 어플리케이션이 시작 중일때
     * Application onCreate() 메서드가 만들어 진다고 나와 있습니다.
     * by. Developer 사이트
     */

    @Override
    public void onCreate() {
        super.onCreate();
        registerActivityLifecycleCallbacks(new MyActivityLifecycleCallbacks());
        pref = getSharedPreferences(Constants.PREF_NAME, 0);
        editor = pref.edit();
        context = this;
//        s3=new S3();

        Log.d(TAG,"Util start");
//        aws=new AWS();
        //db
        dbHelper = OpenHelperManager.getHelper(this, DBHelper.class);
//        apiManager = ApiManager.newInstance(getApplicationContext());




          // Enable Local Datastore.
          Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Contact.class);
//        ParseObject.registerSubclass(Friend.class);
        ParseObject.registerSubclass(User.class);
//        ParseObject.registerSubclass(Individual.class);
//        ParseObject.registerSubclass(ShareFriend.class);
        ParseObject.registerSubclass(Album.class);
        ParseObject.registerSubclass(Picture.class);
        ParseObject.registerSubclass(Group.class);
        ParseObject.registerSubclass(Test.class);
        ParseObject.registerSubclass(Notification.class);
//        ParseObject.registerSubclass(UploadingPicture.class);
        Parse.initialize(this, Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_KEY);
        ParseFacebookUtils.initialize(this, Constants.REQUEST_FACEBOOK_LOGIN);

//        ParsePush.subscribeInBackground("", new SaveCallback() {
//            @Override
//            public void done(com.parse.ParseException e) {
//                if (e == null) {
//                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
//                } else {
//                    Log.e("com.parse.push", "failed to subscribe for push", e);
//                }
//            }
//
//        });
        ParseUser.enableRevocableSessionInBackground();
        //자동으로 Anonymous user 생성
//        ParseUser.enableAutomaticUser();
//        ParseUser.getCurrentUser().saveInBackground();


        syncDataInBackground(context);


        /**
         * Test Code
         */



//        ParseQuery<User> query=ParseQuery.getQuery(User.class);
//        query.findPinnedSendAlbumInBackground(new FindCallback<User>() {
//            @Override
//            public void done(List<User> list, com.parse.ParseException e) {
//
//                for(int i=0; i<list.size(); i++)
//                {
//                    Log.d(TAG,list.get(i).get("authData").toString());
//                }
//            }
//        });
//        Log.d("jyr","start test");
//        ParseQuery<Test> query=ParseQuery.getQuery(Test.class);
//        query.findPinnedSendAlbumInBackground(new FindCallback<Test>() {
//            @Override
//            public void done(List<Test> list, com.parse.ParseException e) {
//                if(e==null)
//                {
//                    list.get(0).pinInBackground("Test", new SaveCallback() {
//                        @Override
//                        public void done(com.parse.ParseException e) {
//                            ParseQuery<Test> query = ParseQuery.getQuery(Test.class);
//                            query.fromPin("Test");
//                            query.findPinnedSendAlbumInBackground(new FindCallback<Test>() {
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
//        query.findPinnedSendAlbumInBackground(new FindCallback<Test>() {
//            @Override
//            public void done(List<Test> list, com.parse.ParseException e) {
//
//                if(e)
//            }
//        });


//        initUploadingData();

    }

    /**
     * 서버에서 변환된 데이터들을 동기화 및 불러와서 설정
     */
    public static void syncDataInBackground(final Context context)
    {
//        if(((Util)context.getApplicationContext()).pref.getBoolean(Constants.PREF_USER_LOGIN,Constants.PREF_USER_LOGIN_DEFAULT)) {

        if(ParseUser.getCurrentUser()!=null && ((User)ParseUser.getCurrentUser()).getCompleted()) {
            Log.d(TAG,"syncParseData Util");
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message msg) {
                    super.handleMessage(msg);
                    Log.d(TAG,"sync done");
                    //연락처 데이터 로딩
                    Util.initContactItemList(context,new Handler());
                }
            };

            Thread thread=new Thread(null, new Runnable() {
                @Override
                public void run() {

                    try {
                        if(Calendar.getInstance().getTimeInMillis()-Util.getContactSyncTime(context)>Constants.SYNC_CONTACT_GAP)
                        {
                            ParseAPI.syncContact(context,handler);
                        }
                        else{
                            handler.sendEmptyMessage(0);
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

    public static void
    setAlbumType(Context context, int type) {
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
//
//    public static void logout(Context context) {
//        SharedPreferences.Editor editor = ((Util) context.getApplicationContext()).editor;
//
//        editor.putBoolean(Constants.PREF_LOGIN, false)
//                .commit();
//    }

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
            fm.beginTransaction().replace(layout, fragment, TAG).addToBackStack(null).commitAllowingStateLoss();
        else
            fm.beginTransaction().replace(layout, fragment, TAG).commitAllowingStateLoss();
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
//        if(((Util)activity.getApp
// licationContext()).pref.getBoolean(Constants.PREF_LOGIN,Constants.PREF_LOGIN_DEFAULT))
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

    public static ArrayList<Contact> getContactList(Context context, long syncTime, String extraQuery)
    {
        ArrayList<Contact> contactItems = new ArrayList<Contact>();
        ArrayList numberList = new ArrayList();


        if (android.os.Build.VERSION.SDK_INT>=18) {
            Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.PHOTO_URI, ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP},
                    ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1 AND " + ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP + ">" + syncTime + extraQuery, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED  ASC");


            while (cursor.moveToNext()) {

                long recordId = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (!numberList.toString().contains(phoneNumber) && !Util.convertToInternationalNumber(context, phoneNumber).equals(((User) ParseUser.getCurrentUser()).getPhone())) {
                    numberList.add(phoneNumber);
                    contactItems.add(new Contact(context, name, imageUri, phoneNumber, recordId));

//                if(syncTime!=0)
//                {
//                    Log.d(TAG,name+" "+phoneNumber+" "+cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)));
//                }

                }
            }
            return contactItems;
        }
        else{
            Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.PHOTO_URI},
                    ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1 "+extraQuery, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED  ASC");


            while (cursor.moveToNext()) {

                long recordId = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (!numberList.toString().contains(phoneNumber) && !Util.convertToInternationalNumber(context, phoneNumber).equals(((User) ParseUser.getCurrentUser()).getPhone())) {
                    numberList.add(phoneNumber);
                    contactItems.add(new Contact(context, name, imageUri, phoneNumber, recordId));

//                if(syncTime!=0)
//                {
//                    Log.d(TAG,name+" "+phoneNumber+" "+cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)));
//                }

                }
            }
            return contactItems;
        }
    }

    //return syncTime 이후에 연락처
    public static ArrayList<Contact> getContactListFromCP(Context context, long syncTime) {

        ArrayList<Contact> contactItems = new ArrayList<Contact>();
        ArrayList numberList = new ArrayList();


        return Util.getContactList(context,syncTime,"");
        /*
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI,ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP},
                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1 AND "+ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP+">"+syncTime, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED  ASC");


        while (cursor.moveToNext()) {


            long recordId=cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            if (!numberList.toString().contains(phoneNumber) && !Util.convertToInternationalNumber(context,phoneNumber).equals(((User)ParseUser.getCurrentUser()).getPhone()) ) {
                numberList.add(phoneNumber);
                contactItems.add(new Contact(context,name, imageUri, phoneNumber,recordId));

                if(syncTime!=0)
                {
                    Log.d(TAG,name+" "+phoneNumber+" "+cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)));
                }

            }
        }

        return contactItems;*/
    }

    /**
     * pinnedContactList 중에서 syncTime 이 후에 수정된 연락처 데이터 불러옴
     */
    public static ArrayList<Contact> getPinnedContactListFromCP(Context context, List<Contact> pinnedContactList, long syncTime)
    {
        String INCLUDE_ID_SQL="";
        for(int i=0; i<pinnedContactList.size(); i++)
        {
            if(i==0)
                INCLUDE_ID_SQL=INCLUDE_ID_SQL+" AND ("+ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID+"="+pinnedContactList.get(i).getRecordId();
            else
                INCLUDE_ID_SQL=INCLUDE_ID_SQL+" OR "+ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID+"="+pinnedContactList.get(i).getRecordId();
            if(i==pinnedContactList.size()-1)
                INCLUDE_ID_SQL=INCLUDE_ID_SQL+")";
        }

        return Util.getContactList(context, syncTime, INCLUDE_ID_SQL);
        /*
        ArrayList<Contact> contactItems = new ArrayList<Contact>();
        ArrayList numberList = new ArrayList();

        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI,ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP},
                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1 AND "+ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP+">"+syncTime+INCLUDE_ID_SQL, null,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED  ASC");


        while (cursor.moveToNext()) {

            long recordId=cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            if (!numberList.toString().contains(phoneNumber) && !Util.convertToInternationalNumber(context,phoneNumber).equals(((User)ParseUser.getCurrentUser()).getPhone()) ) {
                numberList.add(phoneNumber);
                contactItems.add(new Contact(context,name, imageUri, phoneNumber,recordId));

                if(syncTime!=0)
                {
                    Log.d(TAG,name+" "+phoneNumber+" "+cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)));
                }

            }
        }
        return contactItems;*/
    }

    /**
     * 새로 추가된 contact list 불러옴(기존 pin data에 추가되지 않은 전화번호만 )
     */
    public static ArrayList<Contact> getContactListWithoutPinFromCP(Context context, List<Contact> pinnedContactList, long syncTime)
    {
        String NOT_INCLUDE_ID_SQL="";
        for(int i=0; i<pinnedContactList.size(); i++)
        {
            if(i==0)
                NOT_INCLUDE_ID_SQL=NOT_INCLUDE_ID_SQL+" AND ("+ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID+"!="+pinnedContactList.get(i).getRecordId();
            else
                NOT_INCLUDE_ID_SQL=NOT_INCLUDE_ID_SQL+" OR "+ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID+"!="+pinnedContactList.get(i).getRecordId();
            if(i==pinnedContactList.size()-1)
                NOT_INCLUDE_ID_SQL=NOT_INCLUDE_ID_SQL+")";
        }


        return Util.getContactList(context,syncTime,NOT_INCLUDE_ID_SQL);
        /*
        ArrayList<Contact> contactItems = new ArrayList<Contact>();
        ArrayList numberList = new ArrayList();
        for(int i=0; i<pinnedContactList.size(); i++)
        {
            numberList.add(pinnedContactList.get(i).getPhone());
        }

        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI,ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP},
                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1 AND "+ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP+">"+syncTime+NOT_INCLUDE_ID_SQL, null,  ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED  ASC");


        while (cursor.moveToNext()) {

            long recordId=cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.RAW_CONTACT_ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            String internationalPhone = Util.convertToInternationalNumber(context,phoneNumber);
            if (!numberList.toString().contains(internationalPhone) && !internationalPhone.equals(((User)ParseUser.getCurrentUser()).getPhone()) ) {
                numberList.add(phoneNumber);
                contactItems.add(new Contact(context,name, imageUri, phoneNumber,recordId));

                if(syncTime!=0)
                {
                    Log.d(TAG,name+" "+phoneNumber+" "+cursor.getLong(cursor.getColumnIndex(ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP)));
                }

            }
        }
        return contactItems;*/
    }
/*
    //
    //return syncTime 이후에 연락처
    public static ArrayList<IndividualItem> getContactList2(Context context, long syncTime) {


        ArrayList<IndividualItem> contactItems = new ArrayList<IndividualItem>();
        ArrayList numberList = new ArrayList();
        // 로컬에서 연락처 데이터 불러옴뇨
//        ContactsContract.CommonDataKinds.Phone.
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER,
                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI, ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP},
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1 AND "+ContactsContract.CommonDataKinds.Phone.CONTACT_LAST_UPDATED_TIMESTAMP+">"+syncTime, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED  ASC");

        while (cursor.moveToNext()) {

            long recordId=cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
            if (!numberList.toString().contains(phoneNumber) && !Util.convertToInternationalNumber(context,phoneNumber).equals(((User)ParseUser.getCurrentUser()).getPhone()) ){
                numberList.add(phoneNumber);
                contactItems.add(new IndividualItem(name, imageUri, Util.convertToNationalNumber(context, phoneNumber),recordId));
//                Log.d("jyr",Util.convertToNationalNumber(context,phoneNumber));
            }
        }

        cursor.close();

        return contactItems;
    }*/
    /*
        //return syncTime 이후에 연락처 (List<Contact> 제거하거 불러옴)
        public static ArrayList<Contact> getAllContactListWithoutFriend(Context context, List<Contact> friendList) {

            String witoutFriend = "";
            for (int i = 0; i < friendList.size(); i++) {
                witoutFriend = witoutFriend + " AND "+ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"!="+ friendList.get(i).getRecordId();
            }

            ArrayList<Contact> contactItems = new ArrayList<Contact>();
            ArrayList numberList = new ArrayList();
            // 로컬에서 연락처 데이터 불러옴
    //        ContactsContract.CommonDataKinds.Phone.CONTACT_STATUS_TIMESTAMP
    //        ContactsContract.CommonDataKinds.Phone.
    //        ContactsContract.DeletedContacts.CONTENT_URI

    //        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
    //                new String[]{ ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
    //                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
    //                        ContactsContract.CommonDataKinds.Phone.NUMBER,
    //                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI,ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP},
    //                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1 AND "+ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP+">"+syncTime, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED  ASC");

            Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.PHOTO_URI, ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP},
                    ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1" + witoutFriend, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED  ASC");


            while (cursor.moveToNext()) {


                long recordId = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));

                //이미 추가 안된 and 자기 전화번호가 아닌 연락처 데이터들 불러옴
                if (!numberList.toString().contains(phoneNumber) && !Util.convertToInternationalNumber(context,phoneNumber).equals(((User)ParseUser.getCurrentUser()).getPhone()) ) {
                    numberList.add(phoneNumber);
                    contactItems.add(new Contact(context, name, imageUri, phoneNumber, recordId));
    //                contactItems.add(new Contact(context,name, imageUri, phoneNumber,recordId));
    //                contactItems.add(new Contact(context,name, imageUri, phoneNumber,recordId));

                }
            }
            return contactItems;
        }


        //return syncTime 이후에 연락처 (List<Contact> 제거하거 불러옴)
        public static ArrayList<Contact> getAllContactListWithFriend(Context context, List<Long> ContactIdList) {

            String withFriend = "";
            for (int i = 0; i < ContactIdList.size(); i++) {
                if(i==0)
                    withFriend = withFriend + " AND ( "+ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+ ContactIdList.get(i);
                else
                    withFriend = withFriend + " OR "+ContactsContract.CommonDataKinds.Phone.CONTACT_ID+"="+ ContactIdList.get(i);

                if(i==ContactIdList.size()-1)
                    withFriend=withFriend+")";
            }

            ArrayList<Contact> contactItems = new ArrayList<Contact>();
            ArrayList numberList = new ArrayList();
            // 로컬에서 연락처 데이터 불러옴
    //        ContactsContract.CommonDataKinds.Phone.CONTACT_STATUS_TIMESTAMP
    //        ContactsContract.CommonDataKinds.Phone.
    //        ContactsContract.DeletedContacts.CONTENT_URI

    //        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
    //                new String[]{ ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
    //                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
    //                        ContactsContract.CommonDataKinds.Phone.NUMBER,
    //                        ContactsContract.CommonDataKinds.Phone.PHOTO_URI,ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP},
    //                ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1 AND "+ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP+">"+syncTime, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED  ASC");

            Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                    new String[]{ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                            ContactsContract.CommonDataKinds.Phone.NUMBER,
                            ContactsContract.CommonDataKinds.Phone.PHOTO_URI, ContactsContract.Contacts.CONTACT_LAST_UPDATED_TIMESTAMP},
                    ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER + "=1" + withFriend, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED  ASC");


            while (cursor.moveToNext()) {


                long recordId = cursor.getLong(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.CONTACT_ID));
                String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String imageUri = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI));
                if (!numberList.toString().contains(phoneNumber)) {
                    numberList.add(phoneNumber);
                    contactItems.add(new Contact(context, name, imageUri, phoneNumber, recordId));
    //                contactItems.add(new Contact(context,name, imageUri, phoneNumber,recordId));
    //                contactItems.add(new Contact(context,name, imageUri, phoneNumber,recordId));

                }
            }
            return contactItems;
        }*/
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
//    public static void setSharePersonList(Context context, ArrayList<Individual> personItems) {
//        Gson gson = new Gson();
//        Log.d("jyr", gson.toJson(personItems));
//        SharedPreferences.Editor editor = ((Util) context.getApplicationContext()).editor;
//
//        ArrayList<ShareIndividualGson> gsonItems = new ArrayList<ShareIndividualGson>();
//        for (int i = 0; i < personItems.size(); i++) {
//            gsonItems.add(new ShareIndividualGson("dfg", "dfg", "dfg", "dfg", false));
//        }
//        editor.putString(Constants.PREF_SHARE_PERSON, gson.toJson(gsonItems));
//        editor.commit();
//    }

//    //preference에서 공유 개인(연락처 + 쉐어캠 친구) 불러옴
//    public static ArrayList<Individual> getSharePersonList(Context context) {
//        Type resultType = new TypeToken<List<ShareIndividualGson>>() {
//        }.getType();
//        Gson gson = new Gson();
//        return gson.fromJson(((Util) context.getApplicationContext()).pref.getString(Constants.PREF_SHARE_PERSON, Constants.PREF_SHARE_PERSON_DEFAULT), resultType);
//    }

    public static String getAlbumDateFormat(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(date);
    }

    //백버튼 누른 경우
    public static void setBackBtnListener(final Activity actionBarActivity, final FragmentManager fm) {

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

    public static void initActionbar(ActionBarActivity actionBarActivity)
    {
        Util.setBackBtnListener(actionBarActivity, actionBarActivity.getSupportFragmentManager());
        actionBarActivity.getSupportActionBar().hide();
    }

    public static void initActionbar(ActionBarActivity actionBarActivity,String title)
    {
        Util.setBackBtnListener(actionBarActivity, actionBarActivity.getSupportFragmentManager());
        actionBarActivity.getSupportActionBar().hide();
        ((TextView)actionBarActivity.findViewById(R.id.actionbarTitle)).setText(title);
    }

    public static void resetActionbarItem_1(Activity actionBarActivity){
        actionBarActivity.findViewById(R.id.actionItem1).setVisibility(View.GONE);
    }

    public static void resetActionbarItem_2(Activity actionBarActivity){
        actionBarActivity.findViewById(R.id.actionItem2).setVisibility(View.GONE);
    }

    public static void setActionbarItem_1(final Activity actionBarActivity, View.OnClickListener item1Listener) {
        actionBarActivity.findViewById(R.id.actionItem1).setVisibility(View.VISIBLE);
        actionBarActivity.findViewById(R.id.actionItem1).setOnClickListener(item1Listener);
    }

    public static void setActionbarItem_2(final Activity actionBarActivity, View.OnClickListener item1Listener) {
        actionBarActivity.findViewById(R.id.actionItem2).setVisibility(View.VISIBLE);
        actionBarActivity.findViewById(R.id.actionItem2).setOnClickListener(item1Listener);
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

//    //International Phone number 로 변경
//    public static String getNationalPhoneNumber(Context context,String phoeNumber)
//    {
//        TelephonyManager mTelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//        PhoneNumberUtil phoneUtil=PhoneNumberUtil.getInstance();
//        try {
//
//            Phonenumber.PhoneNumber phoneNumber = phoneUtil.parse(phoeNumber,mTelephonyMgr.getNetworkCountryIso().toUpperCase());
////                        Log.d("jyr", PhoneNumberUtils.formatNumber("01033119561"));
////                        PhoneNumberUtils.formatNumberToE164()
//            if(phoneUtil.isValidNumber(phoneNumber))
//            {
//                Log.d("jyr","+"+phoneNumber.getCountryCode()+phoneNumber.getNationalNumber());
//                Log.d("jyr","valid number = "+phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164));
//                return phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.E164);
//            }
//            else{
//                Log.d("jyr","invalid");
//            }
////                        phoneNumberTxt.setText(phoneUtil.format(phoneNumber, PhoneNumberUtil.PhoneNumberFormat.INTERNATIONAL));
//        } catch (NumberParseException e) {
//            Log.d("jyr","phone covert error");
//            e.printStackTrace();
//        }
//
//        return null;
//
//    }

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


    /**
     * preferences
     *
     */

    public static void setShareIndividualWithString(Context context,String shareIndividualList)
    {

        ((Util) context.getApplicationContext()).editor.putString(Constants.PREF_SHARE_INDIVIDUAL, shareIndividualList).commit();
    }
    //공유 설정 전화번호 리스트 저장
    //  { user : [] , phone : []} /** deprecated **/
    // User의 objectId 혹은 phone은 각각 등록된 등록되지 않은 사용자의 identifier역할을 함/** deprecated **/
    // phone이 등록된 User의 identifier역할을 할 수 있지만 편의를 위해 등록된 user는 user object를 직접 사용/** deprecated **/

    //공유 대상의 phone list 저장
    public static void setShareIndividual(Context context,List<Contact> shareList) throws JSONException {

//        JSONObject jsonShareList=new JSONObject();
//        JSONArray userList=new JSONArray();
//        JSONArray phoneList=new JSONArray();
//
//        for(int i=0; i<shareList.size(); i++)
//        {
//            //쉐어캠 친구라면 user추가
//            if(shareList.get(i).getFriendUser()!=null)
//            {
//                userList.put(shareList.get(i).getFriendUser().getObjectId());
//            }
//            //쉐어캠 친구가 아니라면 전화번호 추가
//            else{
//                phoneList.put(shareList.get(i).getPhone());
//            }
//        }
//
//        jsonShareList.put("user",userList);
//        jsonShareList.put("phone",phoneList);
//
        JSONArray phoneList=new JSONArray();
        for(int i=0; i<shareList.size(); i++)
        {
            phoneList.put(shareList.get(i).getPhone());
        }
        ((Util) context.getApplicationContext()).editor.putString(Constants.PREF_SHARE_INDIVIDUAL, phoneList.toString()).commit();
    }

    public static ShareItem getShareList(Context context) throws JSONException {


//        ArrayList<String> shareUserList=new ArrayList<String>();
//        ArrayList<String> sharePhoneList=new ArrayList<String>();
//        JSONObject jsonShareList= new JSONObject( ((Util) context.getApplicationContext()).pref.getString(Constants.PREF_SHARE_INDIVIDUAL,Constants.PREF_SHARE_INDIVIDUAL_DEFAULT) );
//
//        JSONArray tempList= (JSONArray) jsonShareList.get("user");
//        for(int i=0; i<tempList.length(); i++)
//        {
//            shareUserList.add((String) tempList.get(i));
//        }
//        JSONArray tempList2= (JSONArray) jsonShareList.get("phone");
//        for(int i=0; i<tempList2.length(); i++)
//        {
//            sharePhoneList.add((String) tempList2.get(i));
//        }
//
//
//        return new ShareItem(shareUserList,sharePhoneList);
        ArrayList<String> sharePhoneList=new ArrayList<String>();
        JSONArray jsonPhoneList=new JSONArray( ((Util) context.getApplicationContext()).pref.getString(Constants.PREF_SHARE_INDIVIDUAL,Constants.PREF_SHARE_INDIVIDUAL_DEFAULT) );


        for(int i=0; i<jsonPhoneList.length(); i++)
        {
            sharePhoneList.add((String) jsonPhoneList.get(i));
        }

        return new ShareItem(sharePhoneList);
    }
//
//    public static ArrayList<String> getShareUserList(Context context) throws JSONException {
//        ArrayList<String> shareUserList=new ArrayList<String>();
//
//            JSONObject jsonShareList= new JSONObject( ((Util) context.getApplicationContext()).pref.getString(Constants.PREF_SHARE_INDIVIDUAL,Constants.PREF_SHARE_INDIVIDUAL_DEFAULT) );
//
//            JSONArray tempList= (JSONArray) jsonShareList.get("user");
//            for(int i=0; i<tempList.length(); i++)
//            {
//                shareUserList.add((String) tempList.get(i));
//            }
//
//        return shareUserList;
//    }
//
//    public static ArrayList<String> getSharePhoneList(Context context) throws JSONException {
//        ArrayList<String> phoneList=new ArrayList<String>();
//
//
//            JSONObject jsonShareList= new JSONObject( ((Util) context.getApplicationContext()).pref.getString(Constants.PREF_SHARE_INDIVIDUAL,Constants.PREF_SHARE_INDIVIDUAL_DEFAULT) );
//
//            JSONArray tempList= (JSONArray) jsonShareList.get("phone");
//            for(int i=0; i<tempList.length(); i++)
//            {
//                phoneList.add((String) tempList.get(i));
//            }
//
//        return phoneList;
//    }

    //    public static ArrayList<String> getShareIndividual(Context context)
//    {
//        ArrayList<String> shareIdList=new ArrayList<String>();
//        try {
//            JSONObject jsonShareList= new JSONObject( ((Util) context.getApplicationContext()).pref.getString(Constants.PREF_SHARE_INDIVIDUAL,Constants.PREF_SHARE_INDIVIDUAL_DEFAULT) );
//
//            for(int i=0; i<tempJSArray.length();i++)
//            {
//                shareIdList.add(tempJSArray.getString((i)));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return shareIdList;
//    }
    // /sync_all_contact로 연락처 기반 친구 동기화 후 설정
    //연락처 동기화 완료 시간 설정
    public static void setContactSyncTime(Context context,long syncTime)
    {
        Log.d(TAG,"setContactSyncTime2");
        ((Util)context.getApplicationContext()).editor.putLong(Constants.PREF_SYNC_CONTACT_TIME,syncTime).commit();
    }

    public static void setContactSyncTime2(Context context)
    {
        Log.d(TAG,"setContactSyncTime2");
        ((Util)context.getApplicationContext()).editor.putLong(Constants.PREF_SYNC_CONTACT_TIME, Calendar.getInstance().getTimeInMillis()).commit();
    }

    public static long getContactSyncTime(Context context){

        long syncTime=((Util)context.getApplicationContext()).pref.getLong(Constants.PREF_SYNC_CONTACT_TIME, Constants.PREF_SYNC_CONTACT_TIME_DEFAULT);
//        Log.d(TAG,new )
        return syncTime;
    }

    public static void setLoalContactSyncTime(Context context,long time)
    {
        Log.d(TAG,"setContactSyncTime2");
        ((Util)context.getApplicationContext()).editor.putLong(Constants.PREF_LOCAL_SYNC_CONTAT_TIME,time).commit();
    }

    public static long getLocalContactSyncTime(Context context){

        long syncTime=((Util)context.getApplicationContext()).pref.getLong(Constants.PREF_LOCAL_SYNC_CONTAT_TIME, Constants.PREF_LOCAL_SYNC_CONTAT_TIME_DEFAULT);
//        Log.d(TAG,new )
        return syncTime;
    }

    public static Date getContactSyncTime2(Context context){

        long syncTime=((Util)context.getApplicationContext()).pref.getLong(Constants.PREF_SYNC_CONTACT_TIME, Constants.PREF_SYNC_CONTACT_TIME_DEFAULT);
//        Log.d(TAG,new )
        return new Date(syncTime);
    }

    public static boolean getAlarmSet(Context context)
    {
        return  ((Util)context.getApplicationContext()).pref.getBoolean(Constants.PREF_ALARM,Constants.PREF_ALARM_DEFAULT);
    }

    public static void setAlarmSet(Context context, boolean alarm)
    {
        ((Util)context.getApplicationContext()).editor.putBoolean(Constants.PREF_ALARM,alarm).commit();
    }

    public static int getAlarmMode(Context context)
    {
        return ((Util)context.getApplicationContext()).pref.getInt(Constants.PREF_ALARM_MODE, Constants.PREF_ALARM_MODE_DEFAULT);
    }

    public static void setAlarmMode(Context context,int mode)
    {
        ((Util)context.getApplicationContext()).editor.putInt(Constants.PREF_ALARM_MODE, mode).commit();
    }

    public static void setAutoDownloadSet(Context context, boolean autoSave)
    {
        ((Util)context.getApplicationContext()).editor.putBoolean(Constants.PREF_AUTO_DOWNLOAD, autoSave).commit();
    }

    public static boolean getAutoDownloadSet(Context context)
    {
        return ((Util)context.getApplicationContext()).pref.getBoolean(Constants.PREF_AUTO_DOWNLOAD,Constants.PREF_AUTO_SAVE_DEFAULT);
    }

    public static void setAutoDownloadMode(Context context, int mode)
    {
        ((Util)context.getApplicationContext()).editor.putInt(Constants.PREF_AUTO_DOWNLOAD_MODE, mode).commit();
    }

    public static int getAutoDownloadMode(Context context)
    {
        return ((Util)context.getApplicationContext()).pref.getInt(Constants.PREF_AUTO_DOWNLOAD_MODE,Constants.PREF_AUTO_SAVE_MODE_DEFAULT);
    }

    public static void setAutoDownloadBasicConfig(Context context, int basicConfig)
    {
        ((Util)context.getApplicationContext()).editor.putInt(Constants.PREF_AUTO_SAVE_BASIC_CONFIG, basicConfig).commit();
    }

    public static int getAutoDownloadBasicConfig(Context context)
    {
        return ((Util)context.getApplicationContext()).pref.getInt(Constants.PREF_AUTO_SAVE_BASIC_CONFIG,Constants.PREF_AUTO_SAVE_BASIC_CONFIG_DEFAULT);
    }

    public static void setAutoDownloadIndividual(Context context,boolean autoDownload)
    {
        ((Util)context.getApplicationContext()).editor.putBoolean(Constants.PREF_AUTO_DOWNLOAD_INDIVIDUAL, autoDownload).commit();
    }
    public static boolean getAutoDownloadIndividual(Context context)
    {
        return ((Util)context.getApplicationContext()).pref.getBoolean(Constants.PREF_AUTO_DOWNLOAD_INDIVIDUAL, Constants.PREF_AUTO_DOWNLOAD_INDIVIDUAL_DEFAULT);
    }

    public static void setAutoDownloadGroup(Context context,boolean autoDownload)
    {
        ((Util)context.getApplicationContext()).editor.putBoolean(Constants.PREF_AUTO_DOWNLOAD_GROUP, autoDownload).commit();
    }
    public static boolean getAutoDownloadGroup(Context context)
    {
        return ((Util)context.getApplicationContext()).pref.getBoolean(Constants.PREF_AUTO_DOWNLOAD_GROUP, Constants.PREF_AUTO_DOWNLOAD_GROUP_DEFAULT);
    }

    public static int getSelectedAlbumType(Context context)
    {
        return ((Util)context.getApplicationContext()).pref.getInt(Constants.PREF_SELECTED_ALBUM_TYPE,Constants.PREF_SELECTED_SEND_ALBUM);
    }

    public static void setSelectedAlbumType(Context context,int selectedAlbumType)
    {
        ((Util)context.getApplicationContext()).editor.putInt(Constants.PREF_SELECTED_ALBUM_TYPE,selectedAlbumType).commit();
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

    public static String dateToUTCStr(Date date)
    {
        SimpleDateFormat sdf=new SimpleDateFormat("MM/dd/yyyy KK:mm:ss a Z");
        sdf.setTimeZone( TimeZone.getTimeZone("UTC") );
        return sdf.format(date).toString();
    }

    public static void startAvairy(Activity activity,Intent data)
    {
        Log.d(TAG, "choose image ");
        //사진 편집 실행
        Intent intent=new AviaryIntent.Builder(activity)
                .setData(data.getData())
                .withOutput(data.getData())
                .withOutputFormat(Bitmap.CompressFormat.JPEG)
                .saveWithNoChanges(true)
                .withPreviewSize(5000)
                .withNoExitConfirmation(false)//저장 안하고 뒤로 가기 눌렀을 경우 Dialog 띄움
                .withOutputQuality(90)
                .build();

        activity.startActivityForResult(intent, Constants.REQUEST_AVAIRY);

    }

    public static List<String> getFacebookPermission()
    {
        return Arrays.asList("public_profile");
    }

    public static String getDateStr(long time)
    {

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd HH:mm");
        String strDate = sdf.format(time);

        return strDate;
    }


    /**
     * 현재 시간과 같은 날이면 HH:mm
     * 현재 시간과 다른 날이면 yyyy/MM/dd
     */
    public static String getNotificationDateStr(Date date)
    {
        Calendar currentCal=Calendar.getInstance();
        Calendar dateCal=Calendar.getInstance();
        dateCal.setTime(date);

        SimpleDateFormat sdf;
        String strDate;
        //현재 시간과 같은 날이면 HH:mm
        if(currentCal.get(Calendar.YEAR)==dateCal.get(Calendar.YEAR) && currentCal.get(Calendar.MONTH)==dateCal.get(Calendar.MONTH) && currentCal.get(Calendar.DATE)==dateCal.get(Calendar.DATE))
        {
            sdf = new SimpleDateFormat("HH:mm");
            strDate=sdf.format(date);
        }
        // 현재 시간과 다른 날이면 yyyy/MM/dd
        else{
            sdf = new SimpleDateFormat("yyyy/MM/dd");
            strDate=sdf.format(date);
        }


        return strDate;
    }
    //바탕 화면에 탁축 아이콘 만들기
    public static void addIndividualShortcut(final Context context,FragmentManager fm, final Contact individualItem){

        ArrayList<String> strItmes=new ArrayList<String>();
        strItmes.add(context.getString(R.string.add_indiviudal_camera_shortcut));
        strItmes.add(context.getString(R.string.add_individual_album_shortcut));

        ArrayList<View.OnClickListener> listenrList=new ArrayList<View.OnClickListener>();
        //카메라 홈화면 추가
        listenrList.add(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * intent 생성 및 shortcut 생성
                 */
//                final String label=String.valueOf(new Date().getTime());//preferance 의 이름 임의 생성
                final String personName=individualItem.getFriendUser()==null?individualItem.getPinContactName():individualItem.getFriendUser().getNmae();
                JSONArray contactRecordIdList=new JSONArray();
                contactRecordIdList.put(individualItem.getRecordId());

                Intent shortcutIntent = new Intent( context, CameraActivity.class );
                shortcutIntent.setAction( Intent.ACTION_MAIN );
                shortcutIntent.putExtra(CameraActivity.IS_SHORTCUT,true);
                shortcutIntent.putExtra(CameraActivity.CONTACT_RECORD_ID_LIST,contactRecordIdList.toString());
                shortcutIntent.putExtra(CameraActivity.SHORTCUT_TYPE,CameraActivity.INDIVIDUAL_SHORTCUT);

                Intent shortcutAddIntent = new Intent();
                shortcutAddIntent.putExtra( Intent.EXTRA_SHORTCUT_NAME, personName );
                shortcutAddIntent.putExtra( Intent.EXTRA_SHORTCUT_ICON_RESOURCE,
                        Intent.ShortcutIconResource.fromContext( context, R.mipmap.ic_action_accept ) );
                shortcutAddIntent.putExtra( Intent.EXTRA_SHORTCUT_INTENT, shortcutIntent );
                shortcutAddIntent.setAction("com.android.launcher.action.INSTALL_SHORTCUT");
                context.sendBroadcast( shortcutAddIntent );


            }
        });
        //앨범 홈화면 추가
        listenrList.add(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        MyDialogBuilder.showListDialog(context,fm, strItmes,listenrList);
        Log.d(TAG, "addShortCut");

    }

    //사진 삭제 전까지 가능한 마지막 사진 생성 시간
    public static Date getAvailablePictureLastDate()
    {
        Date date=new Date();
        return new Date(date.getTime()-Constants.PICTURE_EXPIRE_DURATION);
    }





    /**
     * Method checks if the app is in background or not
     *
     * @param context
     * @return
     */
    public static boolean isAppIsInBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {

                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {



                        if (activeProcess.equals(context.getPackageName())) {

                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }

        return isInBackground;
    }



}

package com.claude.sharecam;

import android.app.Activity;
import android.app.Application;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.Toast;

import com.amazonaws.auth.BasicSessionCredentials;
import com.aviary.android.feather.sdk.IAviaryClientCredentials;
import com.claude.sharecam.api.AWS;
import com.claude.sharecam.api.ApiManager;
import com.claude.sharecam.api.S3;
//import com.claude.sharecam.login.LoginActivity;
import com.claude.sharecam.parse.Contact;
import com.claude.sharecam.response.Federation;
import com.claude.sharecam.response.User;
import com.claude.sharecam.share.ContactItem;
import com.claude.sharecam.signup.SignUpActivity;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Created by Claude on 15. 4. 9..
 */
public class Util extends Application implements IAviaryClientCredentials {

    //adobe sdk
    private static final String CREATIVE_SDK_SAMPLE_CLIENT_ID = "4bd24623a672401bb64da2dd9620f48b";
    private static final String CREATIVE_SDK_SAMPLE_CLIENT_SECRET = "a824e42c-c4d9-46fe-909d-85d05d4de977";


    public SharedPreferences pref;
    public SharedPreferences.Editor editor;

    public ApiManager apiManager;
//    public S3 s3;

    public AWS aws;



    /** onCreate()
     * 액티비티, 리시버, 서비스가 생성되기전 어플리케이션이 시작 중일때
     * Application onCreate() 메서드가 만들어 진다고 나와 있습니다.
     * by. Developer 사이트
     */
    @Override
    public void onCreate() {
        super.onCreate();
        pref=getSharedPreferences(Constants.PREF_NAME, 0);
        editor=pref.edit();
//        s3=new S3();

        aws=new AWS();
        apiManager=ApiManager.newInstance(getApplicationContext());



        // Enable Local Datastore.
        Parse.enableLocalDatastore(this);
        ParseObject.registerSubclass(Contact.class);
        Parse.initialize(this, Constants.PARSE_APPLICATION_ID, Constants.PARSE_CLIENT_KEY);
        ParseFacebookUtils.initialize(this, Constants.REQUEST_FACEBOOK_LOGIN);

//        ParseQuery<ParseObject> query = ParseQuery.getQuery("Verify_UserPhone");
//        query.findInBackground(new FindCallback<ParseObject>() {
//            @Override
//            public void done(List<ParseObject> list, com.parse.ParseException e) {
//
//                Log.d("jyr","dfg");
//            }
//
//
//        });


    }

    /**
     * onConfigurationChanged()
     * 컴포넌트가 실행되는 동안 단말의 화면이 바뀌면 시스템이 실행 한다.
     */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
    }
    public static void shareImage(Context context,String path) { //공유 이미지 함수

        File file =new File(path);
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
     * Login
     * set preferances
     */

    public static void setCookiePref(Context context)
    {

    }
    public static void setUserPref(Context context,ParseObject user)
    {
        SharedPreferences.Editor editor=((Util) context.getApplicationContext()).editor;

        editor
                .putString(Constants.PREF_USER_ID, (String) user.getString("objectId"))
                .putString(Constants.PREF_USER_NAME, user.getString("username"))
                .putString(Constants.PREF_USER_PROFILE_URL, user.getString("profile"))
                .commit();
    }

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


    public static void setFederationPref(Context context,Federation federation)
    {
        SharedPreferences.Editor editor=((Util) context.getApplicationContext()).editor;

        editor.putString(Constants.PREF_FEDERATION_ACCESS_KEY, federation.Credentials.AccessKeyId)
                .putString(Constants.PREF_FEDERATION_SECRET_ACCESS_KEY, federation.Credentials.SecretAccessKey)
                .putString(Constants.PREF_FEDERATION_SESSION_TOKEN,federation.Credentials.SessionToken)
                .putString(Constants.PREF_FEDERATION_EXPIRATION, federation.Credentials.Expiration)
                .commit();
    }

    public static void logout(Context context)
    {
        SharedPreferences.Editor editor=((Util) context.getApplicationContext()).editor;

        editor.putBoolean(Constants.PREF_LOGIN, false)
                .commit();
    }

    /**
     * Util
     */

    public static void showToast(Context context, int strId)
    {
        Toast.makeText(context, context.getResources().getString(strId), Toast.LENGTH_SHORT).show();
    }

    /**
     * Fragment
     */


    public static void startFragment(FragmentManager fm,int layout,Fragment fragment,boolean addBackStack,String TAG)
    {

        if(addBackStack)
            fm.beginTransaction().replace(layout, fragment, TAG).addToBackStack(null).commit();
        else
            fm.beginTransaction().replace(layout,fragment,TAG).commit();
    }

    public static String getDrawableUriString(Context context,int id)
    {
        return ContentResolver.SCHEME_ANDROID_RESOURCE + "://" + context.getResources().getResourcePackageName(id) + '/' + context.getResources().getResourceTypeName(id) + '/' + context.getResources().getResourceEntryName(id);
    }

    public static Calendar StringToCalendar(String time)  {
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


    public static boolean checkNetwork(Context context)
    {
        if(getOnlineType(context)==Constants.NETWORK_NOT_AVAILABLE) {
            Util.showToast(context, R.string.network_unavailable);
            return false;
        }
        else
            return true;
    }

    public static int getOnlineType(Context context) {
        try {
            ConnectivityManager conMan = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            NetworkInfo.State wifi = conMan.getNetworkInfo(1).getState(); // wifi
            if (wifi == NetworkInfo.State.CONNECTED || wifi == NetworkInfo.State.CONNECTING) return Constants.NETWORK_WIFI;

            NetworkInfo.State mobile = conMan.getNetworkInfo(0).getState();
            if (mobile == NetworkInfo.State.CONNECTED || mobile == NetworkInfo.State.CONNECTING) return Constants.NETWORK_MOBILE;

        } catch (NullPointerException e) {
            return Constants. NETWORK_NOT_AVAILABLE;
        }
        return Constants.NETWORK_NOT_AVAILABLE;
    }

    public static String getAWSProfileURL(int user_id,String extension)
    {
        return Constants.AWS_PROFILE_URL+user_id+"."+extension;
    }

    //로그인이 되어 있는지 확인
    //로그인 되어 있지 않은 경우 로그인 화면으로 이동
    public static void checkLogin(Activity activity)
    {
//        if(((Util)activity.getApplicationContext()).pref.getBoolean(Constants.PREF_LOGIN,Constants.PREF_LOGIN_DEFAULT))
//            return;
//        else{
//            activity.startActivity(new Intent(activity, SignUpActivity.class));
//            activity.finish();
//        }


//        ParseUser user=ParseUser.getCurrentUser();
//        Boolean fff=ParseUser.getCurrentUser().getBoolean("completed");
        if(ParseUser.getCurrentUser()!=null && ParseUser.getCurrentUser().getBoolean("completed"))
            return;
        else{
            activity.startActivity(new Intent(activity, SignUpActivity.class));
            activity.finish();
        }

    }

    public static String getUri(Uri uri,Context context)
    {
        ContentResolver cR = context.getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    public static byte[] uriToByteArray(Uri uri,Context context) throws IOException {
        InputStream iStream =   context.getContentResolver().openInputStream(uri);
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

    public static ArrayList<ContactItem> getContactList(Context context){


        ArrayList<ContactItem> contactItems =new ArrayList<ContactItem>();
        ArrayList numberList=new ArrayList();
        // 로컬에서 연락처 데이터 불러옴
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
                new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
                        ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                        ContactsContract.CommonDataKinds.Phone.NUMBER},
                ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1", null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED  ASC");

        while (cursor.moveToNext())
        {

            String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            if(!numberList.toString().contains(phoneNumber)) {
                numberList.add(phoneNumber);
                contactItems.add(new ContactItem(name, null, phoneNumber));
            }
        }

        cursor.close();

        return contactItems;
    }

    public static String convertToInternationalNumber(Context context,String phoneNumber)
    {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            String locale=String.valueOf(context.getResources().getConfiguration().locale.getCountry());
            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(phoneNumber,locale );
            return phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.E164);
        } catch (NumberParseException e) {
            Log.e("jyr","NumberParseException was thrown: " + e.toString());
        }
        return null;
    }
    public static String convertToNationalNumber(Context context,String phoneNumber)
    {
        PhoneNumberUtil phoneUtil = PhoneNumberUtil.getInstance();
        try {
            String locale=String.valueOf(context.getResources().getConfiguration().locale.getCountry());
            Phonenumber.PhoneNumber swissNumberProto = phoneUtil.parse(phoneNumber,locale );
            return phoneUtil.format(swissNumberProto, PhoneNumberUtil.PhoneNumberFormat.NATIONAL);
        } catch (NumberParseException e) {
            Log.e("jyr","NumberParseException was thrown: " + e.toString());
        }
        return null;
    }


}

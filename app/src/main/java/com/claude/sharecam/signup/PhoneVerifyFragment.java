package com.claude.sharecam.signup;

import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.provider.Telephony;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.api.ErrorCode;
import com.claude.sharecam.dialog.MyDialogBuilder;
import com.claude.sharecam.dialog.SimpleDialog;
import com.claude.sharecam.parse.User;
import com.claude.sharecam.util.Country;
import com.claude.sharecam.util.CountryMaster;
import com.claude.sharecam.util.CountrySpinnerAdapter;
import com.parse.FunctionCallback;
import com.parse.LogInCallback;
import com.parse.ParseAnonymousUtils;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;


/**
 * A placeholder fragment containing a simple view.
 */
public class PhoneVerifyFragment extends Fragment {

    public static final String TAG="PhoneVerifyFragment";
    static final String RECEIVE_MESSAGE_ACTION = "android.provider.Telephony.SMS_RECEIVED";
    static final String MESSAGE_IDENTIFICATION="ShareCam";

    Spinner countryCodeSpinner;
    TelephonyManager mTelephonyMgr;


    TextView phoneVerifyNumBtn;
    EditText phoneNumberTxt;
    EditText phoneVerifyNumTxt;

    int selectedCountryIndex;//선택 된 coutries의 index

    TextView phoneVerifyNextBtn;
//    TextView withoutPhoneVerifyBtn;
    SimpleDialog simepleDialog;

//    String verifiedPhoneNum;

    LinearLayout phoneVerifyPgrLayout;
    LinearLayout phoneVerifyLayout;

    //인증번호 메세지 받는 Broadcast Receiver
    private BroadcastReceiver receiver = new BroadcastReceiver() {
        @TargetApi(Build.VERSION_CODES.KITKAT)
        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"goe sms broadcast receiver");


            //parsing message i've just received
            // 1. sharecam 메세지인지 판단
            // 2. sharecam 메세지인 경우 인증번호 추출 및 확인
            if(intent.getAction().equals(RECEIVE_MESSAGE_ACTION))
            {
                SmsMessage[] messages = Telephony.Sms.Intents.getMessagesFromIntent(intent);
                for(int i=0; i<messages.length; i++)
                {
                    String msgStr=messages[i].getDisplayMessageBody();
                    Log.d(TAG,"message = "+msgStr);
                    int startIndex=msgStr.indexOf("[");
                    int endIndex=msgStr.indexOf("]");
                    if(MESSAGE_IDENTIFICATION.equals(msgStr.substring(startIndex+1,endIndex)))
                    {
                        Log.d(TAG,"parsed message is sharecam message");
                        int startIndex2=msgStr.lastIndexOf("[");
                        int endIndex2=msgStr.lastIndexOf("]");
                        String vNumber = msgStr.substring(startIndex2 + 1, endIndex2);
                        Log.d(TAG,"verification number = "+vNumber);
                        phoneVerifyNumTxt.setText(vNumber);
                        phoneVerifyNextBtn.performClick();

                    }
                    else{
                        Log.d(TAG,"parsed message is NOT sharecam message");
                    }
                }

            }

        }
    };

    @Override
    public void onCreate(Bundle savedInstance)
    {
        super.onCreate(savedInstance);
        Log.d(TAG,"onCreate");
        IntentFilter filter = new IntentFilter();
        filter.addAction(RECEIVE_MESSAGE_ACTION);
        getActivity().registerReceiver(receiver, filter);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        Log.d(TAG,"onDestroy");
        getActivity().unregisterReceiver(receiver);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_phone_verify, container, false);

        CountryMaster cm = CountryMaster.getInstance(getActivity());
        final ArrayList<Country> countries = cm.getCountries();
//        String countryIsoCode = cm.getDefaultCountryIso();

        countryCodeSpinner = (Spinner) root.findViewById(R.id.countryCodeSpinner);
        CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(getActivity(), R.layout.phone_code_spinner_item, countries);
        countryCodeSpinner.setAdapter(adapter);
//        countryCodeSpinner.setSelection();

        phoneNumberTxt=(EditText)root.findViewById(R.id.phoneNumberTxt);
        phoneVerifyNumTxt =(EditText)root.findViewById(R.id.phoneVeriftNumTxt);
        phoneVerifyNumBtn=(TextView)root.findViewById(R.id.phoneVerifyNumBtn);
        phoneVerifyNextBtn=(TextView)root.findViewById(R.id.phoneVerifyNextBtn);
//        withoutPhoneVerifyBtn=(TextView)root.findViewById(R.id.withoutPhoneVerifyBtn);
        phoneVerifyLayout=(LinearLayout)root.findViewById(R.id.phoneVerifyLayout);
        phoneVerifyPgrLayout=(LinearLayout)root.findViewById(R.id.phoneVerifyPgrLayout);

        mTelephonyMgr = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);


        //휴대폰에서 전화번호 가져와서 설정
        for(int i=0; i<countries.size(); i++)
        {
            if(countries.get(i).mCountryIso.toLowerCase().equals( mTelephonyMgr.getNetworkCountryIso().toLowerCase()))
            {

                countryCodeSpinner.setSelection(i);
                break;
            }
        }


        //나라코드 설정 시 edittext 설정
        countryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //디바이스에 등록된 자신의 전화번호 표시
                if(countries.get(position).mCountryIso.toLowerCase().equals( mTelephonyMgr.getNetworkCountryIso().toLowerCase())) {

                    phoneNumberTxt.setText(Util.getE164PhoneNumber(getActivity(),mTelephonyMgr.getLine1Number()));

                    return;
                }
                Log.d("jyr","selected country code spinner");
                phoneNumberTxt.setText("+"+countries.get(position).mDialPrefix);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        //인증번호 받기 버튼
        //전화번호를 +국제번호 형식으로 변환하여 전송
//        phoneVerifyNumBtn.setOnClickListener(new OneClick(new OneClick.Listener() {
//
//
//            @Override
//            public void onClick(View v) {
//
//            }
//        }));
        phoneVerifyNumBtn.setOnClickListener(new View.OnClickListener() {

            boolean isClicked=false;

            void start() {
                isClicked = true;
            }
            void finish()
            {
                isClicked=false;
            }
            @Override
            public void onClick(View v) {
                if(!isClicked) {
                    start();

                    if (phoneNumberTxt.getText().toString().length() == 0) {
                        Util.showToast(getActivity(), R.string.you_should_fill_out);
                        finish();
                        return;
                    } else if (Util.getE164PhoneNumber(getActivity(), phoneNumberTxt.getText().toString()) == null) {
                        Util.showToast(getActivity(), R.string.phone_format_invalid);
                        finish();
                        return;
                    }



                    HashMap<String, Object> params = new HashMap<String, Object>();
                    params.put("phone", Util.getE164PhoneNumber(getActivity(), phoneNumberTxt.getText().toString()));

                    ParseCloud.callFunctionInBackground(ParseAPI.SM_PHONE_VERIFY, params, new FunctionCallback<JSONObject>() {
                        public void done(JSONObject result, ParseException e) {
                            if (e == null) {
                                MyDialogBuilder.showSimpleDialog(getActivity(), getActivity().getSupportFragmentManager(), R.string.phone_verify_message_sent);
//                                verifiedPhoneNum = phoneNumberTxt.getText().toString();
                            } else {

                                Util.showToast(getActivity(), ErrorCode.getToastMessageId(e));
                            }
                            finish();
                        }
                    });
                }
            }
        });


        //휴대폰 번호 인증 확인
        phoneVerifyNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                if(phoneVerifyNumTxt.getText().toString().length()==0)
                {
                    Util.showToast(getActivity(),R.string.you_should_fill_out);
                    return;
                }

                ((SignUpActivity)getActivity()).setProgressLayout(Constants.PROGRESS_VISIBLE);
                verifyPhoneNumber();
//                setLayout(Constants.DOING_REQUEST);
/*
                if(ParseUser.getCurrentUser()==null) {
                    User user = new User();
                    user.setPassword("my pass");
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Hooray! Let them use the app now.

                                verifyPhoneNumber();
                            } else {
                                // Sign up didn't succeed. Look at the ParseException
                                // to figure out what went wrong
                            }
                        }
                    });
//                    //사용자가 아직 없는 경우 생성
//                    ParseAnonymousUtils.logIn(new LogInCallback() {
//                        @Override
//                        public void done(ParseUser user, ParseException e) {
//                            if (e == null) {
//                                Log.d(TAG, "Anonymous user logged in.");
//
//                                verifyPhoneNumber();
//
//                            } else {
//                                Log.d(TAG, "Anonymous login failed.");
//                            }
//                        }
//                    });
                }
                else{

                }*/

            }
        });
//
//        withoutPhoneVerifyBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Util.startFragment(getFragmentManager(),R.id.signupContainer,new InputProfileSelectFragment(),false, InputProfileSelectFragment.TAG);
//            }
//        });


        return root;
    }

    //인증번호 확인
    private void verifyPhoneNumber(){
        HashMap<String, Object> params = new HashMap<String, Object>();
        params.put("vNumber", phoneVerifyNumTxt.getText().toString());

        ParseCloud.callFunctionInBackground(ParseAPI.SM_PHONE_CONFIRM, params, new FunctionCallback<String>() {
            public void done(String sessionToken, ParseException e) {

                if (e == null) {
                    //전화번호 인증 성공
                    Log.d(TAG, "verication number confirmed");


                    ParseUser.becomeInBackground(sessionToken, new LogInCallback() {
                        public void done(ParseUser user, ParseException e) {
                            if (user != null) {
                                //preferance 세팅
                                Util.startFragment(getFragmentManager(), R.id.signupContainer, new InputProfileSelectFragment(), false, InputProfileSelectFragment.TAG);
                            } else {
                                // The token could not be validated.
                                Util.showToast(getActivity(), ErrorCode.getToastMessageId(e));
                            }
                        }
                    });

                } else {
                    Log.d(TAG, "verication number confirm error = " + e.getMessage());
                    Util.showToast(getActivity(), ErrorCode.getToastMessageId(e));
                }
                ((SignUpActivity) getActivity()).setProgressLayout(Constants.PROGRESS_INVISIBLE);
            }
        });
    }

    public void onActivityCreated(Bundle onSavedInstance)
    {
        super.onActivityCreated(onSavedInstance);
    }


//    private void setLayout(int state)
//    {
//        switch (state)
//        {
//            case Constants.BEFORE_REQUEST:
//                phoneVerifyLayout.setVisibility(View.VISIBLE);
//                phoneVerifyPgrLayout.setVisibility(View.GONE);
//                break;
//            case Constants.DOING_REQUEST:
//                phoneVerifyLayout.setVisibility(View.GONE);
//                phoneVerifyPgrLayout.setVisibility(View.VISIBLE);
//                break;
//        }
//    }

//    public class CountrySpinnerAdapter extends ArrayAdapter<Country> {
//
//        private Context context;
//        private ArrayList<Country> itemList;
//        private LayoutInflater layoutInflater;
//        private int textViewResourceId;
//        public CountrySpinnerAdapter(Context context, int textViewResourceId, ArrayList<Country> itemList) {
//
//            super(context, textViewResourceId,itemList);
//            this.context=context;
//            this.itemList=itemList;
//            layoutInflater=LayoutInflater.from(context);
//            this.textViewResourceId=textViewResourceId;
//        }
//
//        public View getView(int position, View convertView, ViewGroup parent) {
//
//            return getCustomView(position,convertView,parent);
//        }
//
//        public View getDropDownView(int position, View convertView, ViewGroup parent) {
//
//            return getCustomView(position,convertView,parent);
//        }
//
//        private View getCustomView(int position, View convertView, ViewGroup parent)
//        {
//            View root=layoutInflater.inflate(textViewResourceId,parent,false);
//
//            ((TextView)root.findViewById(R.id.countryNameTxt)).setText(itemList.get(position).mCountryName);
//            ((TextView)root.findViewById(R.id.phoneCodeTxt)).setText(itemList.get(position).mDialPrefix);
//
//
//            return root;
//        }
//
//    }


}

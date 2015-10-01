package com.claude.sharecam.config;

import android.content.Context;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.api.ErrorCode;
import com.claude.sharecam.dialog.MyDialogBuilder;
import com.claude.sharecam.main.AlbumActivity;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.User;
import com.claude.sharecam.util.Country;
import com.claude.sharecam.util.CountryMaster;
import com.claude.sharecam.util.CountrySpinnerAdapter;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ModifyPhoneFragment extends Fragment {

    public static final String TAG= "ModifyPhoneFragment";
    Spinner mpCountryCodeSpinner;
    EditText mpPhoneNumberTxt;
    TextView mpPhoneVerifyNumBtn;
    EditText mpPhoneVerifyNumTxt;
    TextView mpPhoneVerifyNextBtn;
    CountrySpinnerAdapter countrySpinnerAdapter;
    TelephonyManager mTelephonyMgr;
    String verifiedPhoneNum;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_modify_phone, container, false);

        mpCountryCodeSpinner=(Spinner)root.findViewById(R.id.mpCountryCodeSpinner);
        mpPhoneNumberTxt=(EditText)root.findViewById(R.id.mpPhoneNumberTxt);
        mpPhoneVerifyNumBtn=(TextView)root.findViewById(R.id.mpPhoneVerifyNumBtn);
        mpPhoneVerifyNumTxt=(EditText)root.findViewById(R.id.mpPhoneVerifyNumTxt);
        mpPhoneVerifyNextBtn=(TextView)root.findViewById(R.id.mpPhoneVerifyNextBtn);
        init();

        return root;
    }

    private void init()
    {
        CountryMaster cm = CountryMaster.getInstance(getActivity());
        final ArrayList<Country> countries = cm.getCountries();
        countrySpinnerAdapter = new CountrySpinnerAdapter(getActivity(), R.layout.phone_code_spinner_item, countries);
        mpCountryCodeSpinner.setAdapter(countrySpinnerAdapter);
        mTelephonyMgr = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
        //휴대폰에서 전화번호 가져와서 설정
        for(int i=0; i<countries.size(); i++)
        {
            if(countries.get(i).mCountryIso.toLowerCase().equals( mTelephonyMgr.getNetworkCountryIso().toLowerCase()))
            {

                mpCountryCodeSpinner.setSelection(i);
                break;
            }
        }

        //나라코드 설정 시 edittext 설정
        mpCountryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //디바이스에 등록된 자신의 전화번호 표시
                if(countries.get(position).mCountryIso.toLowerCase().equals( mTelephonyMgr.getNetworkCountryIso().toLowerCase())) {

                    mpPhoneNumberTxt.setText(Util.getE164PhoneNumber(getActivity(), mTelephonyMgr.getLine1Number()));

                    return;
                }
                Log.d("jyr", "selected country code spinner");
                mpPhoneNumberTxt.setText("+"+countries.get(position).mDialPrefix);

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {


            }
        });

        //인증번호 받기 버튼
        //전화번호를 +국제번호 형식으로 변환하여 전송
        mpPhoneVerifyNumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(mpPhoneNumberTxt.getText().toString().length()==0)
                {
                    Util.showToast(getActivity(), R.string.you_should_fill_out);
                    return;
                }

                else if(Util.getE164PhoneNumber(getActivity(),mpPhoneNumberTxt.getText().toString())==null)
                {
                    Util.showToast(getActivity(), R.string.phone_format_invalid);
                    return;
                }



                MyDialogBuilder.showSimpleDialog(getActivity(), getActivity().getSupportFragmentManager(), R.string.phone_verify_message_sent);

                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("phone", Util.getE164PhoneNumber(getActivity(),mpPhoneNumberTxt.getText().toString()));


                ParseCloud.callFunctionInBackground(ParseAPI.SM_PHONE_VERIFY, params, new FunctionCallback<JSONObject>() {
                    public void done(JSONObject result, ParseException e) {
                        if (e == null) {
                            verifiedPhoneNum = mpPhoneNumberTxt.getText().toString();
                        } else {
                            Util.showToast(getActivity(), ErrorCode.getToastMessageId(e));
                        }
                    }
                });

            }
        });

        //휴대폰 번호 인증 확인
        mpPhoneVerifyNextBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {


                if(mpPhoneVerifyNumTxt.getText().toString().length()==0)
                {
                    Util.showToast(getActivity(),R.string.you_should_fill_out);
                    return;
                }


                ((AlbumActivity)getActivity()).setProgressLayout(Constants.PROGRESS_VISIBLE);

                HashMap<String, Object> params = new HashMap<String, Object>();
                params.put("vNumber", mpPhoneVerifyNumTxt.getText().toString());

                ParseCloud.callFunctionInBackground(ParseAPI.SM_PHONE_CONFIRM, params, new FunctionCallback<JSONObject>() {
                    public void done(JSONObject result, ParseException e) {

                        if (e == null) {
//                            final User user=(User)ParseUser.getCurrentUser();
                            ((User) ParseUser.getCurrentUser()).fetchInBackground(new GetCallback<ParseObject>() {
                                @Override
                                public void done(ParseObject parseObject, ParseException e) {
                                    ((AlbumActivity) getActivity()).setProgressLayout(Constants.PROGRESS_INVISIBLE);
                                    if (e == null) {

//                                        ((User) ParseUser.getCurrentUser()).setPhone(((User) parseObject).getPhone());

                                        Log.d(TAG, "current user phone = " + ((User) ParseUser.getCurrentUser()).getPhone());
                                        Log.d(TAG, "fetch user phone = " + ((User) parseObject).getPhone());
                                        getFragmentManager().popBackStack();
                                    } else {
                                        Util.showToast(getActivity(), ErrorCode.getToastMessageId(e));
                                    }
                                }
                            });
                            //전화번호 인증 성공
                            Log.d("jyr","verication number confirmed");

//                            Util.startFragment(getFragmentManager(),R.id.signupContainer,new InputProfileFragment(),false,InputProfileFragment.TAG);
                        }
                        else {
                            ((AlbumActivity)getActivity()).setProgressLayout(Constants.PROGRESS_INVISIBLE);
                            Util.showToast(getActivity(), ErrorCode.getToastMessageId(e));
                        }
                    }
                });
            }
        });

    }



}

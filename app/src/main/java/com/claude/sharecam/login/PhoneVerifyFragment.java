//package com.claude.sharecam.login;
//
//import android.content.Context;
//import android.support.v4.app.Fragment;
//import android.os.Bundle;
//import android.telephony.TelephonyManager;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.AdapterView;
//import android.widget.ArrayAdapter;
//import android.widget.EditText;
//import android.widget.LinearLayout;
//import android.widget.Spinner;
//import android.widget.TextView;
//
//import com.claude.sharecam.Constants;
//import com.claude.sharecam.R;
//import com.claude.sharecam.Util;
//import com.claude.sharecam.api.ErrorCode;
//import com.claude.sharecam.api.RestCallBack;
//import com.claude.sharecam.response.Empty;
//import com.claude.sharecam.response.RestError;
//import com.claude.sharecam.response.User;
//import com.claude.sharecam.dialog.MyDialogBuilder;
//import com.claude.sharecam.dialog.SimpleDialog;
//import com.claude.sharecam.util.Country;
//import com.claude.sharecam.util.CountryMaster;
//
//import java.util.ArrayList;
//
//import retrofit.client.Response;
//
//
///**
// * A placeholder fragment containing a simple view.
// */
//public class PhoneVerifyFragment extends Fragment {
//
//
//    Spinner countryCodeSpinner;
//    TelephonyManager mTelephonyMgr;
//
//
//    TextView phoneVerifyNumBtn;
//    EditText phoneNumberTxt;
//    EditText phoneVeriftNumTxt;
//
//    int selectedCountryIndex;//선택 된 coutries의 index
//
//    TextView phoneVerifyNextBtn;
//    TextView withoutPhoneVerifyBtn;
//    SimpleDialog simepleDialog;
//
//    String verifiedPhoneNum;
//
//    LinearLayout phoneVerifyPgrLayout;
//    LinearLayout phoneVerifyLayout;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//        View root= inflater.inflate(R.layout.fragment_phone_verify, container, false);
//
//        CountryMaster cm = CountryMaster.getInstance(getActivity());
//        final ArrayList<Country> countries = cm.getCountries();
//        String countryIsoCode = cm.getDefaultCountryIso();
//
//        countryCodeSpinner = (Spinner) root.findViewById(R.id.countryCodeSpinner);
//        CountrySpinnerAdapter adapter = new CountrySpinnerAdapter(getActivity(), R.layout.phone_code_spinner_item, countries);
//        countryCodeSpinner.setAdapter(adapter);
////        countryCodeSpinner.setSelection();
//
//        phoneNumberTxt=(EditText)root.findViewById(R.id.phoneNumberTxt);
//        phoneVeriftNumTxt=(EditText)root.findViewById(R.id.phoneVeriftNumTxt);
//        phoneVerifyNumBtn=(TextView)root.findViewById(R.id.phoneVerifyNumBtn);
//        phoneVerifyNextBtn=(TextView)root.findViewById(R.id.phoneVerifyNextBtn);
//        withoutPhoneVerifyBtn=(TextView)root.findViewById(R.id.withoutPhoneVerifyBtn);
//        phoneVerifyLayout=(LinearLayout)root.findViewById(R.id.phoneVerifyLayout);
//        phoneVerifyPgrLayout=(LinearLayout)root.findViewById(R.id.phoneVerifyPgrLayout);
//
//        mTelephonyMgr = (TelephonyManager)getActivity().getSystemService(Context.TELEPHONY_SERVICE);
//
//
//        //휴대폰에서 전화번호 가져와서 설정
//        for(int i=0; i<countries.size(); i++)
//        {
//            if(countries.get(i).mCountryIso.toLowerCase().equals( mTelephonyMgr.getNetworkCountryIso().toLowerCase()))
//            {
//
//                countryCodeSpinner.setSelection(i);
//                break;
//            }
//        }
//
//
//        //나라코드 설정 시 edittext 설정
//        countryCodeSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
//
//                if(countries.get(position).mCountryIso.toLowerCase().equals( mTelephonyMgr.getNetworkCountryIso().toLowerCase())) {
//                    Log.d("jyr","device line number="+mTelephonyMgr.getLine1Number());
//                    phoneNumberTxt.setText(mTelephonyMgr.getLine1Number());
//                    return;
//                }
//                Log.d("jyr","selected country code spinner");
//                phoneNumberTxt.setText("+"+countries.get(position).mDialPrefix);
//
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> parent) {
//
//
//            }
//        });
//
//        //인증번호 받기 버튼
//        phoneVerifyNumBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if(phoneNumberTxt.getText().toString().length()==0)
//                {
//
//                    Util.showToast(getActivity(), R.string.you_should_fill_out);
//                    return;
//
//                }
//
//                MyDialogBuilder.showSimpleDialog(getActivity(),getActivity().getSupportFragmentManager(),R.string.phone_verify_message_sent);
//
//                ((Util)getActivity().getApplicationContext()).apiManager.server.getPhoneVerifyMessage(phoneNumberTxt.getText().toString(), new RestCallBack<Empty>(){
//
//                    @Override
//                    public void success(Empty empty, Response response) {
//
//                        Log.d("jyr","success"+response.toString());
//
//                        verifiedPhoneNum=phoneNumberTxt.getText().toString();
//
//
//                    }
//
//                    @Override
//                    public void failure(RestError restError) {
//
//                        Util.showToast(getActivity(), ErrorCode.getToastMessageId(getActivity(), restError));
//
////                        Toast.makeText(getActivity(), ErrorCode.getToastMessage(getActivity(), error.code),Toast.LENGTH_SHORT).show();
//                    }
//                });
//            }
//        });
//
//        //휴대폰 번호 인증 확인
//        phoneVerifyNextBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(final View v) {
//
//
//                if(phoneVeriftNumTxt.getText().toString().length()==0)
//                {
//
//                    Util.showToast(getActivity(),R.string.you_should_fill_out);
//                    return;
//
//                }
//
//                setLayout(Constants.DOING_REQUEST);
///*
//                //휴대폰 인증 확인 및 서버에 휴대폰 정보 등록
//                ((Util)getActivity().getApplicationContext()).apiManager.server.confirmVerificationNumber(phoneVeriftNumTxt.getText().toString(), new RestCallBack<User>() {
//                    @Override
//                    public void failure(RestError restError) {
//                        setLayout(Constants.BEFORE_REQUEST);
//                        Util.showToast(getActivity(),ErrorCode.getToastMessageId(getActivity(),restError));
//                    }
//
//
//                    @Override
//                    public void success(User user, Response response) {
//                        //전화번호 인증 성공
//                        Log.d("jyr","verication number confirmed");
//
//                        //preferance 세팅
//                        Util.setUserPref(getActivity(), user);
//                        Util.startFragment(getFragmentManager(),R.id.loginContainer,new InputProfileFragment(),false,InputProfileFragment.TAG);
//
//
//                    }
//                });*/
//            }
//        });
//
//        withoutPhoneVerifyBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                InputProfileFragment inputProfileFragment=new InputProfileFragment();
//                Util.startFragment(getFragmentManager(),R.id.loginContainer,new InputProfileFragment(),false,InputProfileFragment.TAG);
//            }
//        });
//
//
//        return root;
//    }
//
//    public void onActivityCreated(Bundle onSavedInstance)
//    {
//        super.onActivityCreated(onSavedInstance);
//
//
//
//    }
//
//
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
//
//    private class CountrySpinnerAdapter extends ArrayAdapter<Country> {
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
//
//
//}

package com.claude.sharecam.config;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.main.MainActivity;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

import bolts.Task;

public class AccountFragment extends Fragment {

    public static final String TAG = "AccountFragment";
    TextView accountPhoneTxt;
    RelativeLayout accountPhoneLayout;
    RelativeLayout accountFacebookLayout;
    TextView facebookRegisterTxt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_account, container, false);
        accountPhoneTxt=(TextView)root.findViewById(R.id.accountPhoneTxt);
        accountPhoneLayout=(RelativeLayout)root.findViewById(R.id.accountPhoneLayout);
        accountFacebookLayout=(RelativeLayout)root.findViewById(R.id.accountFacebookLayout);
        facebookRegisterTxt=(TextView)root.findViewById(R.id.facebookRegisterTxt);

        init();
        // Inflate the layout for this fragment
        return root;
    }

    private void init()
    {
//        ((User) ParseUser.getCurrentUser()).fetchInBackground(new GetCallback<ParseObject>() {
//            @Override
//            public void done(ParseObject parseObject, ParseException e) {
//
//                Log.d(TAG,"fetch phone = "+parseObject.getString("phone"));
//            }
//        });

        //전화번호
        ParseUser user= ParseUser.getCurrentUser();
        String phone = ParseUser.getCurrentUser().getString("phone");
        Log.d(TAG,"phone = "+phone);
        if(phone == null || phone.length()==0)
        {
            phone=getString(R.string.not_registered);
        }
        accountPhoneTxt.setText(phone);
        accountPhoneTxt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.startFragment(getFragmentManager(),R.id.mainContainer,new ModifyPhoneFragment(),true,null);
            }
        });


        setFacebookText();

        accountFacebookLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity)getActivity()).setProgressLayout(Constants.PROGRESS_AND_LAYOUT_VISIBLE);
                if(!ParseFacebookUtils.isLinked(ParseUser.getCurrentUser()))
                {

                   Task<Void> ff=ParseFacebookUtils.linkWithReadPermissionsInBackground(ParseUser.getCurrentUser(), getActivity(), Util.getFacebookPermission(), new SaveCallback() {
                        @Override
                        public void done(ParseException ex) {

                            if (ex==null && ParseFacebookUtils.isLinked(ParseUser.getCurrentUser())) {
                                Log.d(TAG, "Woohoo, user logged in with Facebook!");
                                setFacebookText();
                            }
                            else{
                                ParseAPI.erroHandling(getActivity(),ex);
                            }
                            ((MainActivity)getActivity()).setProgressLayout(Constants.PROGRESS_INVISIBLE);
                        }
                    });
                    Log.d(TAG, "test Facebook!");


                }

                else {
                    ParseFacebookUtils.unlinkInBackground(ParseUser.getCurrentUser(), new SaveCallback() {
                        @Override
                        public void done(ParseException ex) {
                            if (ex == null) {
                                Log.d(TAG, "The user is no longer associated with their Facebook account.");
                                setFacebookText();
                            } else {
                                ParseAPI.erroHandling(getActivity(), ex);
                            }

                            ((MainActivity) getActivity()).setProgressLayout(Constants.PROGRESS_INVISIBLE);
                        }
                    });
                }

            }
        });
    }

    private void setFacebookText()
    {

        //페이스북
        //페이스북 연결 표시
        if(!ParseFacebookUtils.isLinked(ParseUser.getCurrentUser()))
        {
            facebookRegisterTxt.setText(R.string.not_registered);
        }
        else
        {
            facebookRegisterTxt.setText(R.string.registered);
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult account fragment");
        if(requestCode==Constants.REQUEST_FACEBOOK_LOGIN)
            ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);


    }


}

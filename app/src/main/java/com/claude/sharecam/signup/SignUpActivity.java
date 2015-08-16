package com.claude.sharecam.signup;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.dialog.MyDialogBuilder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class SignUpActivity extends ActionBarActivity {

    ProgressBar signUpProgressBar;
    FrameLayout signupContainer;
    Context context;
    public static final String TAG="SignUpActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        context=this;
//        ArrayList<String> strItem=new ArrayList<String>();
//        strItem.add("dfg");
//        strItem.add("dfgdfg");
//
//        ArrayList<View.OnClickListener> listenerItems=new ArrayList<View.OnClickListener>();
//        listenerItems.add(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Util.showToast(context,R.string.network_unavailable);
//            }
//        });
//        listenerItems.add(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Util.showToast(context,R.string.network_unavailable);
//            }
//        });
//        MyDialogBuilder.showListDialog(this,getSupportFragmentManager(), strItem,listenerItems);

        // Add code to print out the key hash
        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.claude.sharecam",
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }


        signUpProgressBar=(ProgressBar)findViewById(R.id.signUpProgressBar);
        signupContainer=(FrameLayout)findViewById(R.id.signupContainer);

        getSupportActionBar().hide();;
//        Util.startFragment(getSupportFragmentManager(),R.id.signupContainer,new SignUpFragment(),false,SignUpFragment.TAG);

        Util.startFragment(getSupportFragmentManager(),R.id.signupContainer,new PhoneVerifyFragment(),false,SignUpFragment.TAG);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SignUpFragment.TAG);
        if (fragment != null) {
            ((SignUpFragment) fragment).onActivityResult(requestCode, resultCode, data);
        }


        fragment = getSupportFragmentManager().findFragmentByTag(InputProfileSelectFragment.TAG);
        if (fragment != null) {
            ((InputProfileSelectFragment) fragment).onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setProgressLayout(int state)
    {

        switch(state)
        {
            case Constants.PROGRESS_AND_LAYOUT_VISIBLE:
//                Log.d(TAG,"PROGRESS_AND_LAYOUT_VISIBLE");
                signUpProgressBar.setVisibility(View.VISIBLE);
                signupContainer.setVisibility(View.VISIBLE);
                break;
            case Constants.PROGRESS_VISIBLE:
                signUpProgressBar.setVisibility(View.VISIBLE);
                signupContainer.setVisibility(View.GONE);
                break;
            case Constants.PROGRESS_INVISIBLE:
                signUpProgressBar.setVisibility(View.GONE);
                signupContainer.setVisibility(View.VISIBLE);
                break;

        }
    }
}

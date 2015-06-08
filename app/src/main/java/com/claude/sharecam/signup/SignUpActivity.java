package com.claude.sharecam.signup;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ProgressBar;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.signup.InputProfileFragment;
import com.kbeanie.imagechooser.api.ChooserType;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SignUpActivity extends ActionBarActivity {

    ProgressBar signUpProgressBar;
    FrameLayout signupContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

//        // Add code to print out the key hash
//        try {
//            PackageInfo info = getPackageManager().getPackageInfo(
//                    "com.claude.sharecam",
//                    PackageManager.GET_SIGNATURES);
//            for (Signature signature : info.signatures) {
//                MessageDigest md = MessageDigest.getInstance("SHA");
//                md.update(signature.toByteArray());
//                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
//            }
//        } catch (PackageManager.NameNotFoundException e) {
//
//        } catch (NoSuchAlgorithmException e) {
//
//        }


        signUpProgressBar=(ProgressBar)findViewById(R.id.signUpProgressBar);
        signupContainer=(FrameLayout)findViewById(R.id.signupContainer);

        getSupportActionBar().hide();;
        Util.startFragment(getSupportFragmentManager(),R.id.signupContainer,new SignUpFragment(),false,SignUpFragment.TAG);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Fragment fragment = getSupportFragmentManager().findFragmentByTag(SignUpFragment.TAG);
        if (fragment != null) {
            ((SignUpFragment) fragment).onActivityResult(requestCode, resultCode, data);
        }


        fragment = getSupportFragmentManager().findFragmentByTag(InputProfileFragment.TAG);
        if (fragment != null) {
            ((InputProfileFragment) fragment).onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setProgressLayout(int state)
    {

        switch(state)
        {
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

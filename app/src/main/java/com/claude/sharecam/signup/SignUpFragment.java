package com.claude.sharecam.signup;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.camera.CameraActivity;
import com.claude.sharecam.camera.ImageManipulate;
import com.facebook.Profile;
import com.kbeanie.imagechooser.api.ChooserType;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseFile;
import com.parse.ParseUser;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Arrays;

public class SignUpFragment extends Fragment {


    public static final String TAG="SignUpFragment";

    LinearLayout facebookSignInBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sign_up, container, false);

        //로그인 되어 있다면 로그아웃
        if(ParseUser.getCurrentUser()!=null)
            ParseUser.getCurrentUser().logOut();




        facebookSignInBtn= (LinearLayout) root.findViewById(R.id.facebookSignInBtn);



        facebookSignInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(getActivity(), Arrays.asList("public_profile"), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Error: " + err);
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else

                            //sign up
                            if (!user.getBoolean("completed")) {


//                                new SignUpNext(user).execute(Profile.getCurrentProfile().getProfilePictureUri(Constants.PROFILE_WIDTH, Constants.PROFILE_HEIGHT).toString());

                                //load profile image from facebook url and call signUpNext()
                                new LoadImage(new LoadImage.AfterLoadImage() {
                                    @Override
                                    public void next(Bitmap bitmap) {

                                        ParseUser user=ParseUser.getCurrentUser();
                                        //update user's profile and name
                                        ParseFile file = new ParseFile(user.getObjectId()+".png", ImageManipulate.bitmapToByteArray(bitmap));
                                        user.put("profile", file);
                                        user.put("username", Profile.getCurrentProfile().getName());
                                        user.put("name", "name");
                                        user.saveInBackground();

                                        Util.startFragment(getActivity().getSupportFragmentManager(), R.id.signupContainer, new PhoneVerifyFragment(), false, null);
                                    }
                                }).execute(Profile.getCurrentProfile().getProfilePictureUri(Constants.PROFILE_WIDTH, Constants.PROFILE_HEIGHT).toString());
                            }

                            //sign in
                            else {
                                Intent intent = new Intent(getActivity(), CameraActivity.class);
                                startActivity(intent);
                            }


                    }
                });
            }
        });

        // Inflate the layout for this fragment
        return root;
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("jyr", "onActivityResult singup fragment");
        if(requestCode== Constants.REQUEST_FACEBOOK_LOGIN)
            ParseFacebookUtils.onActivityResult(requestCode, resultCode, data);


    }


/*

    //download image
    class SignUpNext extends AsyncTask<String,Void,Bitmap> {

        ParseUser user;

        public SignUpNext(ParseUser user){
            this.user=user;
        }


        @Override
        protected Bitmap doInBackground(String... url) {
            return download_Image(url[0]);
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {

            signUpNext(bitmap);
        }


        private Bitmap download_Image(String url) {
            //---------------------------------------------------
            Bitmap bm = null;
            try {
                URL aURL = new URL(url);
                URLConnection conn = aURL.openConnection();
                conn.connect();
                InputStream is = conn.getInputStream();
                BufferedInputStream bis = new BufferedInputStream(is);
                bm = BitmapFactory.decodeStream(bis);
                bis.close();
                is.close();
            } catch (IOException e) {
                Log.e("Hub","Error getting the image from server : " + e.getMessage().toString());
            }
            return bm;
            //---------------------------------------------------
        }

        //after updating user's profile and name , go to next step
        private void signUpNext(Bitmap bitmap)
        {

            //update user's profile and name
            ParseFile file = new ParseFile(user.getObjectId()+".png", ImageManipulate.bitmapToByteArray(bitmap));
            user.put("profile", file);
            user.put("username", Profile.getCurrentProfile().getName());
            user.put("name", "name");
            user.saveInBackground();

            Util.startFragment(getActivity().getSupportFragmentManager(), R.id.signupContainer, new PhoneVerifyFragment(), false, null);
        }

    }
*/
}

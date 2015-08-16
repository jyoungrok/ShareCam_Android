package com.claude.sharecam.config;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.dialog.MyDialogBuilder;
import com.claude.sharecam.main.MainActivity;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.User;
import com.claude.sharecam.util.ImageManipulate;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class ConfigFragment extends Fragment {


    ImageView configProfileImg;
    TextView configUserName;
//    ImageView modifyUserBtn;
    RelativeLayout configProfileLayout;
    RelativeLayout configAccountLayout;//계정설정
//    RelativeLayout connectedDevicesLayout;
    RelativeLayout manageFriendLayout;
    RelativeLayout alarmLayout;
    RelativeLayout autoSaveLayout;
    RelativeLayout noticeLayout;
    RelativeLayout appInfoLayout;
    RelativeLayout signOutLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_config, container, false);

        ((MainActivity)getActivity()).setProgressLayout(Constants.PROGRESS_INVISIBLE);
        configProfileImg=(ImageView)root.findViewById(R.id.configProfileImg);
        configUserName=(TextView)root.findViewById(R.id.configUserName);
        configProfileLayout =(RelativeLayout)root.findViewById(R.id.configProfileLayout);
        configAccountLayout=(RelativeLayout)root.findViewById(R.id.configAccountLayout);
        manageFriendLayout=(RelativeLayout)root.findViewById(R.id.manageFriendLayout);
        alarmLayout=(RelativeLayout)root.findViewById(R.id.alarmLayout);
        autoSaveLayout=(RelativeLayout)root.findViewById(R.id.autoSaveLayout);
        noticeLayout=(RelativeLayout)root.findViewById(R.id.noticeLayout);
        appInfoLayout=(RelativeLayout)root.findViewById(R.id.appInfoLayout);
        signOutLayout=(RelativeLayout)root.findViewById(R.id.signOutLayout);

        init();


        // Inflate the layout for this fragment
        return root;
    }

    private void init(){
        if(((User)ParseUser.getCurrentUser()).getThumProfileFile()!=null) {
            ((User) ParseUser.getCurrentUser()).getThumProfileFile().getDataInBackground(new GetDataCallback() {
                @Override
                public void done(byte[] bytes, ParseException e) {
                    if (e == null) {
                        configProfileImg.setImageBitmap(ImageManipulate.byteArrayToBitmap(bytes));
                    } else {
                        ParseAPI.erroHandling(getActivity(), e);
                    }
                }
            });
        }
        configUserName.setText(((User)ParseUser.getCurrentUser()).getNmae());
        configProfileLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.startFragment(getFragmentManager(), R.id.mainContainer, new ModifyProfileFragment(), true, ModifyProfileFragment.TAG);
            }
        });
        configAccountLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.startFragment(getFragmentManager(), R.id.mainContainer, new AccountFragment(), true,AccountFragment.TAG);

            }
        });


        manageFriendLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.startFragment(getFragmentManager(),R.id.mainContainer,new ManageFriendFragment(),true,null);
            }
        });

        alarmLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.startFragment(getFragmentManager(),R.id.mainContainer,new AlarmFragment(),true,null);
            }
        });

        autoSaveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.startFragment(getFragmentManager(),R.id.mainContainer,new AutoSaveFragment(),true,null);
            }
        });

        noticeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        appInfoLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        signOutLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MyDialogBuilder.showSignOutDialog(getActivity(),getFragmentManager());
            }
        });



    }
}

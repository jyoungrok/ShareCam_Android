package com.claude.sharecam.config;

import android.app.Activity;
import android.net.Uri;
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

public class AutoSaveFragment extends Fragment {

    RelativeLayout autoSaveSetLayout;
    ImageView autoSaveSetBtn;
    RelativeLayout autoSaveModeLayout;
    TextView autoSaveModeTxt;
    RelativeLayout basicAutoSaveLayout;
    TextView basicAutoSaveTxt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_auto_save, container, false);

        autoSaveSetLayout=(RelativeLayout)root.findViewById(R.id.autoSaveSetLayout);
        autoSaveSetBtn=(ImageView)root.findViewById(R.id.autoSaveSetBtn);
        autoSaveModeLayout=(RelativeLayout)root.findViewById(R.id.autoSaveModeLayout);
        autoSaveModeTxt=(TextView)root.findViewById(R.id.autoSaveModeTxt);
        basicAutoSaveLayout=(RelativeLayout)root.findViewById(R.id.basicAutoSaveLayout);
        basicAutoSaveTxt=(TextView)root.findViewById(R.id.basicAutoSaveTxt);


        init();

        return root;
    }

    private void init()
    {
        setAutoSaveSet();
        setAutoSaveMode();
        setAutoSaveBasicConfig();

        autoSaveSetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.setAutoSaveSet(getActivity(),!Util.getAutoSaveSet(getActivity()));
                setAutoSaveSet();

            }
        });

        autoSaveModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode=Util.getAutoSaveMode(getActivity());
                switch (mode)
                {
                    case Constants.PREF_AUTO_SAVE_WHENEVER:
                        Util.setAutoSaveMode(getActivity(),Constants.PREF_AUTO_SAVE_ONLY_WIFI);
                        break;
                    case Constants.PREF_AUTO_SAVE_ONLY_WIFI:
                        Util.setAutoSaveMode(getActivity(),Constants.PREF_AUTO_SAVE_WHENEVER);
                        break;
                }
                setAutoSaveMode();
            }
        });

        basicAutoSaveLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int basicConfig=Util.getAutoSaveBasicConfig(getActivity());
                switch (basicConfig)
                {
                    case Constants.PREF_AUTO_SAVE_BASIC_ALL:
                        Util.setAutoSaveBasicConfig(getActivity(),Constants.PREF_AUTO_SAVE_BASIC_GROUP);
                        break;
                    case Constants.PREF_AUTO_SAVE_BASIC_GROUP:
                        Util.setAutoSaveBasicConfig(getActivity(),Constants.PREF_AUTO_SAVE_BASIC_INDIVIDUAL);
                        break;
                    case Constants.PREF_AUTO_SAVE_BASIC_INDIVIDUAL:
                        Util.setAutoSaveBasicConfig(getActivity(),Constants.PREF_AUTO_SAVE_BASIC_ALL);
                        break;
                }

                setAutoSaveBasicConfig();
            }
        });






    }

    private void setAutoSaveSet()
    {
        boolean autoSave= Util.getAutoSaveSet(getActivity());
        if(autoSave)
        {
            autoSaveSetBtn.setSelected(true);
        }
        else
            autoSaveSetBtn.setSelected(false);

    }

    private void setAutoSaveMode()
    {
        int mode=Util.getAutoSaveMode(getActivity());
        switch (mode)
        {
            case Constants.PREF_AUTO_SAVE_WHENEVER:
                autoSaveModeTxt.setText(getString(R.string.whenever));
                break;
            case Constants.PREF_AUTO_SAVE_ONLY_WIFI:
                autoSaveModeTxt.setText(getString(R.string.only_wifi));
                break;
        }
    }

    private void setAutoSaveBasicConfig()
    {

        int basicConfig=Util.getAutoSaveBasicConfig(getActivity());

        switch (basicConfig)
        {
            case Constants.PREF_AUTO_SAVE_BASIC_ALL:
                basicAutoSaveTxt.setText(getString(R.string.individual_and_group));
                break;
            case Constants.PREF_AUTO_SAVE_BASIC_GROUP:
                basicAutoSaveTxt.setText(getString(R.string.group));
                break;
            case Constants.PREF_AUTO_SAVE_BASIC_INDIVIDUAL:
                basicAutoSaveTxt.setText(getString(R.string.individual));
                break;
        }


    }




}

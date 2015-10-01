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

public class AutoDownloadFragment extends Fragment {

    RelativeLayout autoSaveSetLayout;
    ImageView autoSaveSetBtn;
    RelativeLayout autoSaveModeLayout;
    TextView autoSaveModeTxt;
//    RelativeLayout basicAutoSaveLayout;
//    TextView basicAutoSaveTxt;
    RelativeLayout autoDownloadIndividual;
    ImageView autoDownloadIndividualImg;
    RelativeLayout autoDownloadGroup;
    ImageView autoDownloadGroupImg;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_auto_save, container, false);

        autoSaveSetLayout=(RelativeLayout)root.findViewById(R.id.autoSaveSetLayout);
        autoSaveSetBtn=(ImageView)root.findViewById(R.id.autoSaveSetBtn);
        autoSaveModeLayout=(RelativeLayout)root.findViewById(R.id.autoSaveModeLayout);
        autoSaveModeTxt=(TextView)root.findViewById(R.id.autoSaveModeTxt);
        autoDownloadIndividual=(RelativeLayout)root.findViewById(R.id.autoDownloadIndividual);
        autoDownloadIndividualImg=(ImageView)root.findViewById(R.id.autoDownloadIndividualImg);
        autoDownloadGroup=(RelativeLayout)root.findViewById(R.id.autoDownloadGroup);
        autoDownloadGroupImg=(ImageView)root.findViewById(R.id.autoDownloadGroupImg);


//        basicAutoSaveLayout=(RelativeLayout)root.findViewById(R.id.basicAutoSaveLayout);
//        basicAutoSaveTxt=(TextView)root.findViewById(R.id.basicAutoSaveTxt);


        init();

        return root;
    }

    private void init()
    {
        setAutoSaveSet();
        setAutoSaveMode();
        setAutoDownloadGroup();;
        setAutoDownloadIndividual();
//        setAutoSaveBasicConfig();

        autoSaveSetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.setAutoDownloadSet(getActivity(), !Util.getAutoDownloadSet(getActivity()));
                setAutoSaveSet();

            }
        });

        autoSaveModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int mode=Util.getAutoDownloadMode(getActivity());
                switch (mode)
                {
                    case Constants.PREF_AUTO_SAVE_WHENEVER:
                        Util.setAutoDownloadMode(getActivity(), Constants.PREF_AUTO_SAVE_ONLY_WIFI);
                        break;
                    case Constants.PREF_AUTO_SAVE_ONLY_WIFI:
                        Util.setAutoDownloadMode(getActivity(), Constants.PREF_AUTO_SAVE_WHENEVER);
                        break;
                }
                setAutoSaveMode();
            }
        });

//        basicAutoSaveLayout.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                int basicConfig = Util.getAutoDownloadBasicConfig(getActivity());
//                switch (basicConfig) {
//                    case Constants.PREF_AUTO_SAVE_BASIC_ALL:
//                        Util.setAutoDownloadBasicConfig(getActivity(), Constants.PREF_AUTO_SAVE_BASIC_GROUP);
//                        break;
//                    case Constants.PREF_AUTO_SAVE_BASIC_GROUP:
//                        Util.setAutoDownloadBasicConfig(getActivity(), Constants.PREF_AUTO_SAVE_BASIC_INDIVIDUAL);
//                        break;
//                    case Constants.PREF_AUTO_SAVE_BASIC_INDIVIDUAL:
//                        Util.setAutoDownloadBasicConfig(getActivity(), Constants.PREF_AUTO_SAVE_BASIC_NOBODY);
//                        break;
//                    case Constants.PREF_AUTO_SAVE_BASIC_NOBODY:
//                        Util.setAutoDownloadBasicConfig(getActivity(), Constants.PREF_AUTO_SAVE_BASIC_ALL);
//                        break;
//                }
//
//            }
//        });

        autoDownloadGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean autoDownloadGroup=Util.getAutoDownloadGroup(getActivity());
                if(autoDownloadGroup)
                {
                    Util.setAutoDownloadGroup(getActivity(),false);
                }

                else{
                    Util.setAutoDownloadGroup(getActivity(),true);
                }
                setAutoDownloadGroup();
            }
        });

        autoDownloadIndividual.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean autoDownloadIndividual=Util.getAutoDownloadIndividual(getActivity());
                if(autoDownloadIndividual)
                {
                    Util.setAutoDownloadIndividual(getActivity(), false);
                }

                else{
                    Util.setAutoDownloadIndividual(getActivity(),true);
                }
                setAutoDownloadIndividual();
            }
        });







    }

    private void setAutoSaveSet()
    {
        boolean autoSave= Util.getAutoDownloadSet(getActivity());
        if(autoSave)
        {
            autoSaveSetBtn.setSelected(true);
        }
        else
            autoSaveSetBtn.setSelected(false);

    }

    private void setAutoSaveMode()
    {
        int mode=Util.getAutoDownloadMode(getActivity());
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

    private void setAutoDownloadIndividual()
    {
        boolean autoDownload=Util.getAutoDownloadIndividual(getActivity());
        if(autoDownload)
        {
            autoDownloadIndividualImg.setSelected(true);
        }
        else{
            autoDownloadIndividualImg.setSelected(false);
        }
    }

    private void setAutoDownloadGroup()
    {
        boolean autoDownload=Util.getAutoDownloadGroup(getActivity());
        if(autoDownload)
        {
            autoDownloadGroupImg.setSelected(true);
        }
        else{
            autoDownloadGroupImg.setSelected(false);
        }
    }

//    private void setAutoSaveBasicConfig()
//    {
//
//        int basicConfig=Util.getAutoDownloadBasicConfig(getActivity());
//
//        switch (basicConfig)
//        {
//            case Constants.PREF_AUTO_SAVE_BASIC_ALL:
//                basicAutoSaveTxt.setText(getString(R.string.individual_and_group));
//                break;
//            case Constants.PREF_AUTO_SAVE_BASIC_GROUP:
//                basicAutoSaveTxt.setText(getString(R.string.group));
//                break;
//            case Constants.PREF_AUTO_SAVE_BASIC_INDIVIDUAL:
//                basicAutoSaveTxt.setText(getString(R.string.individual));
//                break;
//            case Constants.PREF_AUTO_SAVE_BASIC_NOBODY:
//                basicAutoSaveTxt.setText(getString(R.string.no_config));
//                break;
//        }
//
//
//    }




}

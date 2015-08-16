package com.claude.sharecam.config;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;

public class AlarmFragment extends Fragment {

    public static final String TAG="AlarmFragment";
    RelativeLayout alarmSetLayout;
    ImageView alarmSetBtn;
    RelativeLayout alarmModeLayout;
    TextView alarmModeTxt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_alarm, container, false);

        alarmSetLayout=(RelativeLayout)root.findViewById(R.id.alarmSetLayout);
        alarmSetBtn=(ImageView)root.findViewById(R.id.alarmSetBtn);
        alarmModeLayout=(RelativeLayout)root.findViewById(R.id.alarmModeLayout);
        alarmModeTxt=(TextView)root.findViewById(R.id.alarmModeTxt);

        init();

        // Inflate the layout for this fragment
        return root;
    }

    private void init()
    {
        setAlarmSet();
        setAlarmMode();
        //알림 설정 <-> 해제
        alarmSetLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG,"click alarm set layout");
                Util.setAlarmSet(getActivity(),!Util.getAlarmSet(getActivity()));
                setAlarmSet();
            }
        });

        alarmModeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d(TAG,"click alarm mode layout");

                int alarmMode=Util.getAlarmMode(getActivity());

                switch(alarmMode)
                {
                    case Constants.PREF_ALARM_MODE_SOUND_AND_VIBRATION:
                        Util.setAlarmMode(getActivity(),Constants.PREF_ALARM_MODE_SOUND);
                        break;
                    case Constants.PREF_ALARM_MODE_SOUND:
                        Util.setAlarmMode(getActivity(),Constants.PREF_ALARM_MODE_VIBRATION);
                        break;
                    case Constants.PREF_ALARM_MODE_VIBRATION:
                        Util.setAlarmMode(getActivity(),Constants.PREF_ALARM_MODE_SOUND_AND_VIBRATION);
                        break;
                }

                setAlarmMode();


            }
        });


    }

    private void setAlarmSet()
    {
        boolean alarm=Util.getAlarmSet(getActivity());
        if(alarm)
        {
            Log.d(TAG,"set alarm");
            alarmSetBtn.setSelected(true);
        }
        else
        {
            Log.d(TAG,"reset alarm");
            alarmSetBtn.setSelected(false);
        }
    }

    private void setAlarmMode()
    {

        int alarmMode=Util.getAlarmMode(getActivity());
        switch (alarmMode)
        {
            case Constants.PREF_ALARM_MODE_SOUND_AND_VIBRATION:
                alarmModeTxt.setText(getString(R.string.sound_and_vibration));
                break;
            case Constants.PREF_ALARM_MODE_SOUND:
                alarmModeTxt.setText(getString(R.string.sound));
                break;
            case Constants.PREF_ALARM_MODE_VIBRATION:
                alarmModeTxt.setText(getString(R.string.vibration));
                break;
        }
    }


}

package com.claude.sharecam.util;

import android.app.Activity;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.TextView;

import com.claude.sharecam.R;
import com.claude.sharecam.Util;

/**
 * Created by Claude on 15. 9. 15..
 */
public class ActionBarUtil {


    public static void initActionbar(ActionBarActivity actionBarActivity)
    {
        setBackBtnListener(actionBarActivity, actionBarActivity.getSupportFragmentManager());
        actionBarActivity.getSupportActionBar().hide();
       actionBarActivity.findViewById(R.id.actionbarSpinner).setVisibility(View.VISIBLE);
        ((TextView)actionBarActivity.findViewById(R.id.actionbarTitle)).setVisibility(View.GONE);
    }

    public static void initActionbar(ActionBarActivity actionBarActivity,String title)
    {
        setBackBtnListener(actionBarActivity, actionBarActivity.getSupportFragmentManager());
        actionBarActivity.getSupportActionBar().hide();
        actionBarActivity.findViewById(R.id.actionbarSpinner).setVisibility(View.GONE);
        ((TextView)actionBarActivity.findViewById(R.id.actionbarTitle)).setVisibility(View.VISIBLE);
        ((TextView)actionBarActivity.findViewById(R.id.actionbarTitle)).setText(title);
    }

    public static void resetActionbarItem_1(Activity actionBarActivity){
        actionBarActivity.findViewById(R.id.actionItem1).setVisibility(View.GONE);
    }

    public static void resetActionbarItem_2(Activity actionBarActivity){
        actionBarActivity.findViewById(R.id.actionItem2).setVisibility(View.GONE);
    }

    public static void setActionbarItem_1(final Activity actionBarActivity, View.OnClickListener item1Listener) {
        actionBarActivity.findViewById(R.id.actionItem1).setVisibility(View.VISIBLE);
        actionBarActivity.findViewById(R.id.actionItem1).setOnClickListener(item1Listener);
    }

    public static void setActionbarItem_2(final Activity actionBarActivity, View.OnClickListener item1Listener) {
        actionBarActivity.findViewById(R.id.actionItem2).setVisibility(View.VISIBLE);
        actionBarActivity.findViewById(R.id.actionItem2).setOnClickListener(item1Listener);
    }

    //백버튼 누른 경우
    public static void setBackBtnListener(final Activity actionBarActivity, final FragmentManager fm) {

        actionBarActivity.findViewById(R.id.backBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (fm.getBackStackEntryCount() == 0) {
                    actionBarActivity.finish();
                } else {
                    fm.popBackStack();
                }
            }
        });
    }


}

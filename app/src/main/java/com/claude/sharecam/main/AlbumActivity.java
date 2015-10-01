package com.claude.sharecam.main;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.config.AccountFragment;
import com.claude.sharecam.config.ModifyProfileFragment;
import com.claude.sharecam.view.SlidingTabLayout;

public class AlbumActivity extends ActionBarActivity {

    //intent data
    public static final String GO_TO_ALBUM="goToAlbum";
    public static final String ALBUM_ID="albumId";



    public static final String TAG="AlbumActivity";
    SlidingTabLayout mainSlidingTabs;
    ViewPager mainPager;
    //    MainPagerAdpater mainPagerAdpater;
    FrameLayout albumContainer;
    LinearLayout albumProgressLayout;
    DrawerLayout albumDrawerLayout;



    public static final int SEND_ALBUM=0;
    public static final int RECEIVE_ALBUM=1;

    Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate");
        setContentView(R.layout.activity_album);


        context=this;
        albumContainer =(FrameLayout)findViewById(R.id.albumContainer);
        albumProgressLayout =(LinearLayout)findViewById(R.id.albumProgressLayout);


        initDrawer();

        startAlbumFragment(getIntent());
        //앨범 설정 불러와 받은 앨범 / 보낸 앨범 fragment 실행
//        AlbumFragment.startFragment(this);
//        initActionbar();
    }

    private void initDrawer()
    {
        albumDrawerLayout=(DrawerLayout)findViewById(R.id.albumDrawerLayout);

        //drawer 비 활성화
        albumDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG, "onNewIntent");

     startAlbumFragment(intent);
    }


    @Override
    public void onResume()
    {
        Log.d(TAG, "onResume");
        super.onResume();
    }


/*
    private void initActionbar()
    {


        //actionbar 기본 설정
        Util.initActionbar(this);

        Spinner actionbarSpinner;
        actionbarSpinner= ((Spinner)findViewById(R.id.actionbarSpinner));
        actionbarSpinner.setVisibility(View.VISIBLE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.album_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionbarSpinner.setAdapter(adapter);
        actionbarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position) {
                    case SEND_ALBUM:
                        Util.setSelectedAlbumType(context, Constants.PREF_SELECTED_SEND_ALBUM);
                        break;
                    case RECEIVE_ALBUM:
                        Util.setSelectedAlbumType(context, Constants.PREF_SELECTED_RECEIVED_ALBUM);
                        break;
                }

                startAlbumFragment();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setAlbumSpinner(actionbarSpinner);

        //환경 설정
        Util.setActionbarItem_1(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.startFragment(getSupportFragmentManager(), R.id.albumContainer, new ConfigFragment(), true, null);
            }
        });

        //notification list
        Util.setActionbarItem_2(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Util.startFragment(getSupportFragmentManager(), R.id.albumContainer, new NotificationListFragment(), true, null);
            }
        });
    }

    private void setAlbumSpinner(Spinner actionbarSpinner)
    {
        switch(Util.getSelectedAlbumType(context))
        {
            case Constants.PREF_SELECTED_SEND_ALBUM:
                actionbarSpinner.setSelection(SEND_ALBUM);
                break;
            case Constants.PREF_SELECTED_RECEIVED_ALBUM:
                actionbarSpinner.setSelection(RECEIVE_ALBUM);
                break;
        }
    }*/

    /**
     //     * preference에서 설정된 앨범 정보 확인 하고 앨범 fragment 실행
     //     */

    public void startAlbumFragment(Intent intent)
    {
        Bundle args = new Bundle();
        args.putInt(AlbumFragment.ALBUM_TYPE, AlbumFragment.RECEIVE_ALBUM_TYPE_VALUE);

        //앨범 실행 intent가 닮겨 있는 경우 (Notification에서 클릭해서 호출 된경우)
        if(intent.getBooleanExtra(GO_TO_ALBUM,false))
        {
            Log.d(TAG,"goToAlbum set");
            args.putBoolean(AlbumFragment.GO_TO_ALBUM,true);
            args.putString(AlbumFragment.ALBUM_ID, intent.getStringExtra(ALBUM_ID));
        }


        switch(Util.getSelectedAlbumType(context))
        {
            case Constants.PREF_SELECTED_SEND_ALBUM:
                Log.d(TAG,"start send album");
                args.putInt(AlbumFragment.ALBUM_TYPE, AlbumFragment.SEND_ALBUM_TYPE_VALUE);
                break;
            case Constants.PREF_SELECTED_RECEIVED_ALBUM:
                Log.d(TAG,"start receive album");
                args.putInt(AlbumFragment.ALBUM_TYPE, AlbumFragment.RECEIVE_ALBUM_TYPE_VALUE);
                break;
        }

        AlbumFragment albumFragment = new AlbumFragment();
        albumFragment.setArguments(args);
        Util.startFragment(getSupportFragmentManager(), R.id.albumContainer, albumFragment, false, null);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Fragment fragment = getSupportFragmentManager().findFragmentByTag(ModifyProfileFragment.TAG);
        if (fragment != null) {
            ((ModifyProfileFragment) fragment).onActivityResult(requestCode, resultCode, data);
        }
        Fragment accountFragment = getSupportFragmentManager().findFragmentByTag(AccountFragment.TAG);
        if (accountFragment != null) {
            ((AccountFragment) accountFragment).onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setProgressLayout(int state) {

        switch (state) {
            case Constants.PROGRESS_AND_LAYOUT_VISIBLE:
                albumContainer.setVisibility(View.VISIBLE);
                albumProgressLayout.setVisibility(View.VISIBLE);
                break;
            case Constants.PROGRESS_VISIBLE:
                albumContainer.setVisibility(View.GONE);
                albumProgressLayout.setVisibility(View.VISIBLE);
                break;
            case Constants.PROGRESS_INVISIBLE:
                albumContainer.setVisibility(View.VISIBLE);
                albumProgressLayout.setVisibility(View.GONE);
                break;

        }
    }


}

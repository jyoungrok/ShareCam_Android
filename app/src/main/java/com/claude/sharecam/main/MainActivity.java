package com.claude.sharecam.main;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.config.AccountFragment;
import com.claude.sharecam.config.ModifyProfileFragment;
import com.claude.sharecam.view.SlidingTabLayout;

import java.util.Locale;

public class MainActivity extends ActionBarActivity {

    SlidingTabLayout mainSlidingTabs;
    ViewPager mainPager;
    MainPagerAdpater mainPagerAdpater;
    FrameLayout mainContainer;
    LinearLayout mainProgressLayout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContainer=(FrameLayout)findViewById(R.id.mainContainer);
        mainProgressLayout=(LinearLayout)findViewById(R.id.mainProgressLayout);
        Util.startFragment(getSupportFragmentManager(),R.id.mainContainer,new MainFragment(),false,null);
        Util.setBackBtnListener(this,getSupportFragmentManager());
        getSupportActionBar().hide();


//        mainSlidingTabs= (SlidingTabLayout) findViewById(R.id.mainSlidingTabs);
//        mainPager=(ViewPager) findViewById(R.id.mainPager);
//        mainPagerAdpater=new MainPagerAdpater(getSupportFragmentManager());
//        mainPager.setAdapter(mainPagerAdpater);
//        mainSlidingTabs.setDistributeEvenly(true);
//        mainSlidingTabs.setViewPager(mainPager);




    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class MainPagerAdpater extends FragmentStatePagerAdapter {

        public MainPagerAdpater(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
            switch (position)
            {
                default:
                case 0:
                    return new MyAlbumFragment();
                case 1 :
                    return new MyAlbumFragment();
                case 2 :
                    return new MyAlbumFragment();
                case 3:
                    return new MyAlbumFragment();
            }

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.private_tab);
                case 1:
                    return getString(R.string.group_tab);
                case 2:
                    return getString(R.string.channel_tab);
                case 3:
                    return getString(R.string.channel_tab);
            }
            return null;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);


        Fragment fragment = getSupportFragmentManager().findFragmentByTag(ModifyProfileFragment.TAG);
        if (fragment != null) {
            ((ModifyProfileFragment) fragment).onActivityResult(requestCode, resultCode, data);
        }
        Fragment accountFragment=getSupportFragmentManager().findFragmentByTag(AccountFragment.TAG);
        if(accountFragment!=null)
        {
            ((AccountFragment) accountFragment).onActivityResult(requestCode, resultCode, data);
        }
    }

    public void setProgressLayout(int state)
    {

        switch(state)
        {
            case Constants.PROGRESS_AND_LAYOUT_VISIBLE:
                mainContainer.setVisibility(View.VISIBLE);
                mainProgressLayout.setVisibility(View.VISIBLE);
                break;
            case Constants.PROGRESS_VISIBLE:
                mainContainer.setVisibility(View.GONE);
                mainProgressLayout.setVisibility(View.VISIBLE);
                break;
            case Constants.PROGRESS_INVISIBLE:
                mainContainer.setVisibility(View.VISIBLE);
                mainProgressLayout.setVisibility(View.GONE);
                break;

        }
    }


}

package com.claude.sharecam.main;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.share.GroupFragment;
import com.claude.sharecam.share.IndividualFragment;
import com.claude.sharecam.view.SlidingTabLayout;

import java.util.Locale;

public class MainActivity extends ActionBarActivity {

    SlidingTabLayout mainSlidingTabs;
    ViewPager mainPager;
    MainPagerAdpater mainPagerAdpater;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Util.startFragment(getSupportFragmentManager(),R.id.mainContainer,new MainFragment(),false,null);

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

}

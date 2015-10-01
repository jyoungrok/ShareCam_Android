package com.claude.sharecam.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.claude.sharecam.R;
import com.claude.sharecam.config.ConfigFragment;
import com.claude.sharecam.view.SlidingTabLayout;

import java.util.Locale;

public class MainFragment extends Fragment {

    SlidingTabLayout mainSlidingTabs;
    ViewPager mainPager;
    MainPagerAdpater mainPagerAdpater;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_main, container, false);


        mainSlidingTabs= (SlidingTabLayout) root.findViewById(R.id.mainSlidingTabs);
        mainPager=(ViewPager) root.findViewById(R.id.mainPager);
        mainPagerAdpater=new MainPagerAdpater(getFragmentManager());
        mainPager.setAdapter(mainPagerAdpater);
        mainSlidingTabs.setDistributeEvenly(true);
        mainSlidingTabs.setViewPager(mainPager);
        // Inflate the layout for this fragment
        return root;
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
                case 1:
                case 2:
                case 3:
                    Bundle args=new Bundle();
                    args.putInt(AlbumFragment.ALBUM_TYPE,AlbumFragment.RECEIVE_ALBUM_TYPE_VALUE);
                    AlbumFragment albumFragment=new AlbumFragment();
                    albumFragment.setArguments(args);
                    return albumFragment;
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

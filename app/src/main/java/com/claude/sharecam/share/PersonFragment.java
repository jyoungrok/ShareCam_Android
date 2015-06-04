package com.claude.sharecam.share;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.claude.sharecam.R;

public class PersonFragment extends Fragment {

    private FragmentTabHost mTabHost;
    private EditText searchPersonET;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_person, container, false);

        searchPersonET= (EditText) root.findViewById(R.id.searchPersonET);
        mTabHost = (FragmentTabHost) root.findViewById(android.R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);


        Bundle args1=new Bundle();
        args1.putInt(ContactFragment.MODE, ContactFragment.RECOMMEND);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.recommend_tab)).setIndicator(getString(R.string.recommend_tab)),
                ContactFragment.class, args1);

        Bundle args2=new Bundle();
        args2.putInt(ContactFragment.MODE, ContactFragment.CONTACT);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.contact_tab)).setIndicator(getString(R.string.contact_tab)),
                ContactFragment.class, args2);

        Bundle args3=new Bundle();
        args3.putInt(ContactFragment.MODE, ContactFragment.SHARECAM);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.sharecam_tab)).setIndicator(getString(R.string.sharecam_tab)),
                ContactFragment.class, args3);



        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mTabHost = null;
    }
}

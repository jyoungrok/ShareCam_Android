package com.claude.sharecam.main;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.claude.sharecam.R;
import com.claude.sharecam.Util;

public class MyAlbumFragment extends Fragment {

    RecyclerView myAlbumRecyclerView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_my_album, container, false);


        root.findViewById(R.id.individualAlbumBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Util.startFragment(getFragmentManager(),R.id.mainContainer,new IndividualAlbumFragment(),true,null);
            }
        });

        // Inflate the layout for this fragment
        return root;
    }

}

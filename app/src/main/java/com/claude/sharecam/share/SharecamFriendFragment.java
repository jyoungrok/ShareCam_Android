package com.claude.sharecam.share;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.claude.sharecam.R;

/**
 * Created by Claude on 15. 5. 27..
 */
public class SharecamFriendFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_sharecam_friend, container, false);

        return root;
    }
}

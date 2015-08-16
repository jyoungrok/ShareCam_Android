package com.claude.sharecam.config;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.main.MainActivity;
import com.claude.sharecam.parse.Friend;
import com.claude.sharecam.parse.ParseAPI;
import com.parse.ParseException;

import java.util.Date;

public class ManageFriendFragment extends Fragment {

    public static final String TAG="ManageFriendFragment";
    EditText searchFriendsET;
    TextView syncDateText;
    ImageView syncFriendBtn;
    RecyclerView friendsRecyclerView;
    boolean isSynchronizing;//동기화 진행중인 경우 true
    ManageFriendFragment thisFragment;

    Handler syncHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            if(thisFragment.isAdded()) {
                setSyncDate();
                ((MainActivity) getActivity()).setProgressLayout(Constants.PROGRESS_INVISIBLE);
                isSynchronizing = false;
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root=inflater.inflate(R.layout.fragment_manage_friend, container, false);
        thisFragment=this;
        searchFriendsET=(EditText)root.findViewById(R.id.searchFriendsET);
        syncDateText=(TextView)root.findViewById(R.id.syncDateText);
        syncFriendBtn=(ImageView)root.findViewById(R.id.syncFriendBtn);
        friendsRecyclerView=(RecyclerView)root.findViewById(R.id.friendsRecyclerView);

        init();
        // Inflate the layout for this fragment
        return root;
    }

    private void init()
    {
        isSynchronizing=false;
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        friendsRecyclerView.setLayoutManager(layoutManager);

        setSyncDate();
        //수동 동기화
        //연락처 동기화 수행
        syncFriendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!isSynchronizing) {
                    Log.d(TAG,"try sync");
                    isSynchronizing=true;
                    ((MainActivity) getActivity()).setProgressLayout(Constants.PROGRESS_AND_LAYOUT_VISIBLE);

                    new Thread() {
                        @Override
                        public void run() {
                            try {
                                ParseAPI.syncParseData(getActivity(), Friend.CLASS_NAME);
                                //수정된  local 연락처 정보 반영
                                ParseAPI.syncContact(getActivity());
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            syncHandler.sendEmptyMessage(0);
                        }
                    }.start();
                }
                else{
                    Log.d(TAG,"aleady sync is in progress");
                }

            }
        });
    }

    private void setSyncDate()
    {

            //마지막 동기화 시간
            syncDateText.setText(getString(R.string.final_sync_time) + " " + Util.getDateStr(Util.getContactSyncTime(getActivity()).getTime()));

    }



}

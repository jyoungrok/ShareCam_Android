//package com.claude.sharecam.share;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.net.Uri;
//import android.os.Bundle;
//import android.support.v4.app.Fragment;
//import android.support.v7.app.ActionBarActivity;
//import android.support.v7.widget.GridLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ImageView;
//
//import com.claude.sharecam.R;
//import com.claude.sharecam.Util;
//import com.claude.sharecam.group.AddGroupActivity;
//import com.claude.sharecam.group.AddGroupFragment;
//import com.claude.sharecam.parse.ParseAPI;
//
//public class GroupFragment extends Fragment {
//
//    public static final int ROW_NUM=3;
//
//    RecyclerView groupRecyclerView;
//    ImageView addGroupBtn;
//
//    @Override
//    public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                             Bundle savedInstanceState) {
//
//        View root= inflater.inflate(R.layout.fragment_group, container, false);
//        groupRecyclerView=(RecyclerView)root.findViewById(R.id.groupRecyclerView);
//        GridLayoutManager gridLayoutManager=new GridLayoutManager(getActivity(),ROW_NUM);
//        groupRecyclerView.setLayoutManager(gridLayoutManager);
//        addGroupBtn=(ImageView)root.findViewById(R.id.addGroupBtn);
//
//        addGroupBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(getActivity(), AddGroupActivity.class);
//                getActivity().startActivity(intent);
//            }
//        });
//
////
////        ParseAPI.getGroupsByMe()
//
////        Util.setActionbarItem_1((ActionBarActivity)getActivity(), new View.OnClickListener() {
////            @Override
////            public void onClick(View v) {
////                Util.startFragment(getFragmentManager(),new   PersonListFragment());
////            }
////        });
//
//
//        // Inflate the layout for this fragment
//        return root;
//    }
//
//
//
//}

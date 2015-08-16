package com.claude.sharecam.group;


import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.share.IndividualFragment;
//import com.claude.sharecam.share.PersonListFragment;

public class AddGroupFragment extends Fragment {

    interface GroupName {
        void setGroupName(String groupName);
    }

    GroupName groupName;
    EditText groupNameEditTxt;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_add_group, container, false);
        groupNameEditTxt = (EditText) root.findViewById(R.id.groupNameEditTxt);
        groupName=(GroupName)getActivity();
//        addedItems=new ArrayList<IndividualItem>();

        Util.setActionbarItem_1(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (groupNameEditTxt.getText().length() != 0) {
                    //activity의 variable설정
                    groupName.setGroupName(groupNameEditTxt.getText().toString());
                    IndividualFragment individualFragment = new IndividualFragment();
                    Bundle args = new Bundle();
                    args.putInt(IndividualFragment.MODE, IndividualFragment.ADD_NORMAL_MODE);
                    individualFragment.setArguments(args);
                    Util.startFragment(getFragmentManager(), R.id.addGroupLayout, individualFragment, false, null);
                } else {
                    Util.showToast(getActivity(), R.string.you_should_fill_out);
                }
            }
        });


        // Inflate the layout for this fragment
        return root;
    }


}
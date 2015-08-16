package com.claude.sharecam.config;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.main.MainActivity;
import com.claude.sharecam.parse.SCSaveCallback;
import com.claude.sharecam.parse.User;
import com.parse.ParseUser;

public class ModifyUserNameFragment extends Fragment {

    EditText modifyUserNameET;
    ImageView clearUserNameBtn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root= inflater.inflate(R.layout.fragment_modify_user_name, container, false);

        modifyUserNameET=(EditText)root.findViewById(R.id.modifyUserNameET);
        clearUserNameBtn=(ImageView)root.findViewById(R.id.clearUserNameBtn);

        init();
        // Inflate the layout for this fragment
        return root;
    }

    private void init()
    {
        modifyUserNameET.setText(((User)ParseUser.getCurrentUser()).getNmae());
        clearUserNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                modifyUserNameET.setText("");
            }
        });

        Util.setActionbarItem_1(getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(modifyUserNameET.getText().toString().length()==0)
                {
                    Util.showToast(getActivity(),R.string.you_should_fill_out);
                    return;
                }
                User user= (User) ParseUser.getCurrentUser();
                user.setName(modifyUserNameET.getText().toString());

                ((MainActivity)getActivity()).setProgressLayout(Constants.PROGRESS_VISIBLE);
                user.saveInBackground(new SCSaveCallback(getActivity(), new SCSaveCallback.Callback() {
                    @Override
                    public void done() {
                        ((MainActivity)getActivity()).setProgressLayout(Constants.PROGRESS_INVISIBLE);
                        getFragmentManager().popBackStack();
                    }
                }));

            }
        });
    }
}

package com.claude.sharecam.dialog;

import android.app.Dialog;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.claude.sharecam.R;

/**
 * Created by Claude on 15. 5. 8..
 */
public class SimpleDialog extends DialogFragment {

    public static final String DIALOG_TEXT="dialogText";

    TextView sdTxt;
    TextView sdAccept;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root=inflater.inflate(R.layout.simple_dialog_fragment, container, false);

        sdAccept= (TextView) root.findViewById(R.id.sdAccept);
        sdTxt=(TextView)root.findViewById(R.id.sdTxt);



        Bundle args=getArguments();

        sdTxt.setText(args.getString(DIALOG_TEXT)!=null?args.getString(DIALOG_TEXT):"");

        sdAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             dismiss();
            }
        });

        return root;

    }
        @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()){
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }
}

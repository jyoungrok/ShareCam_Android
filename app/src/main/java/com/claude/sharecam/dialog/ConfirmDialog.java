package com.claude.sharecam.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.claude.sharecam.R;
import com.claude.sharecam.view.RecycleLinearLayoutManager;

import java.util.ArrayList;

/**
 * Created by Claude on 15. 8. 6..
 */
public class ConfirmDialog extends DialogFragment {

    public static final String CONFIRM_TEXT="confirmText";
    public static final String ACCEPT_LISTENER="acceptListener";


    interface DialogListener {
        void accept();
    }

    DialogListener dialogListener;
    TextView confirmDialogTxt;
    TextView cdAcceptBtn;
    TextView cdCancelBtn;

    String confirmTextStr;
    View.OnClickListener acceptListener;

    public void setDialogListener(DialogListener dialogListener){
        this.dialogListener=dialogListener;
    }
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);

        View root = inflater.inflate(R.layout.confirm_dialog_fragment, container, false);

        confirmDialogTxt=(TextView)root.findViewById(R.id.confirmDialogTxt);
        cdAcceptBtn=(TextView)root.findViewById(R.id.cdAcceptBtn);
        cdCancelBtn=(TextView)root.findViewById(R.id.cdCancelBtn);

        Bundle args=getArguments();
        confirmTextStr=args.getString(CONFIRM_TEXT);
        acceptListener= (View.OnClickListener) args.getSerializable(ACCEPT_LISTENER);

        init();
        return root;

    }


    private void init() {

        confirmDialogTxt.setText(confirmTextStr);
        cdAcceptBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialogListener.accept();
            }
        });
        cdCancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });


    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }

}

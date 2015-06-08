package com.claude.sharecam.dialog;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;

import com.claude.sharecam.R;

/**
 * Created by Claude on 15. 5. 8..
 */
public class MyDialogBuilder {
    public static void showSimpleDialog(Context context,FragmentManager fm,int strId)
    {
        SimpleDialog simepleDialog=new SimpleDialog();
        Bundle args=new Bundle();
        args.putString(SimpleDialog.DIALOG_TEXT,context.getResources().getString(R.string.phone_verify_message_sent));
        simepleDialog.setArguments(args);
        simepleDialog.show(fm,"simpleDialog");

    }
}

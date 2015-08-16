package com.claude.sharecam.dialog;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.View;

import com.claude.sharecam.R;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.SerializableParseFile;
import com.claude.sharecam.orm.IndividualItem;
import com.claude.sharecam.signup.SignUpActivity;
import com.parse.DeleteCallback;
import com.parse.ParseException;

import java.util.ArrayList;

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
        simepleDialog.show(fm,"SimpleDialog");
    }

    public static ListDialog showListDialog(Context context,FragmentManager fm,ArrayList<String> strItems,ArrayList<View.OnClickListener> listenerItems)
    {
        ListDialog listDialog=new ListDialog();
        Bundle args=new Bundle();
        args.putSerializable(ListDialog.STR_ITEMS,strItems);
        args.putSerializable(ListDialog.LISTENER_ITEMS,listenerItems);
        listDialog.setArguments(args);
        listDialog.show(fm,"ListDialog");
        return listDialog;
    }

    public static void showSignOutDialog(final Context context,FragmentManager fm)
    {
        final ConfirmDialog confirmDialog=new ConfirmDialog();
        Bundle args=new Bundle();
        args.putString(ConfirmDialog.CONFIRM_TEXT,context.getString(R.string.sign_out_dialog_text));
        confirmDialog.setDialogListener(new ConfirmDialog.DialogListener() {
            @Override
            public void accept() {
                Log.d(ParseAPI.TAG,"try signing out ");
                ParseAPI.signOut(context, new DeleteCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e==null)
                        {
                            Log.d(ParseAPI.TAG,"sign out done");
                            confirmDialog.dismiss();
                            Intent intent=new Intent(context,SignUpActivity.class);
                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                        else{
                            ParseAPI.erroHandling(context,e);
                        }
                    }
                });
            }
        });

//        args.put(ConfirmDialog.ACCEPT_LISTENER, (Serializable) signOutListener);
        confirmDialog.setArguments(args);
        confirmDialog.show(fm,"ConfirmDialog");
    }

    public static void showProfileDialog(Context context,FragmentManager fm,IndividualItem individualItem)
    {
        ProfileDialog profileDialog=new ProfileDialog();
        Bundle args=new Bundle();
        args.putSerializable(ProfileDialog.INDIVIDUAL_ITEM,  individualItem);
//        args.putInt(ProfileDialog.CONTAINER_ID,containerId);
//        profileDialog.setStyle( ProfileImageDialog.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen );
        profileDialog.setArguments(args);
        profileDialog.show(fm,"ProfileDialog");
    }

    public static void showProfileImageDialog(FragmentManager fm,SerializableParseFile serializableParseFile)
    {
        Bundle args=new Bundle();
        args.putSerializable(ProfileImageDialog.IMAGE_PARSE_FILE,serializableParseFile);
        ProfileImageDialog profileImageDialog=new ProfileImageDialog();
        profileImageDialog.setStyle( ProfileImageDialog.STYLE_NORMAL, android.R.style.Theme_Black_NoTitleBar_Fullscreen );
        profileImageDialog.setArguments(args);
        profileImageDialog.show(fm,"ProfileImageDialog");
    }
}

package com.claude.sharecam.parse;

import android.content.Context;
import android.database.Cursor;
import android.os.Handler;
import android.provider.ContactsContract;
import android.util.Log;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.api.ErrorCode;
import com.claude.sharecam.share.ContactItem;
import com.claude.sharecam.signup.InputProfileFragment;
import com.parse.DeleteCallback;
import com.parse.FindCallback;
import com.parse.FunctionCallback;
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseCloud;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Claude on 15. 5. 31..
 */
public class ParseAPI {

    public static final String SM_PHONE_VERIFY = "sm_phone_verify";
    public static final String SM_PHONE_CONFIRM = "sm_phone_confirm";
    public static final String SYNC_CONTACT="sync_contact";
    public static final String DELETE_CONTACT="delete_contact";
    public static final String SIGN_UP_COMPLETED="sign_up_completed";

    //delete exsting contact objects in parse server
    //upload local contact to parse server
    public static void initContact(ParseUser user, final Context context, final Handler handler) {

        //read contact list from content prover
        final ArrayList<ContactItem> arItem = Util.getContactList(context);

        //delete all of contacts created by user
        ParseCloud.callFunctionInBackground(ParseAPI.DELETE_CONTACT, new HashMap<String, Object>(), new FunctionCallback<JSONObject>() {
            public void done(JSONObject result, ParseException e) {

                if (e == null) {
                    List<Contact> contactList = new ArrayList<Contact>();

                    for (int i = 0; i < arItem.size(); i++) {
                        Contact contact = new Contact();
                        contact.setCreatedBy(ParseUser.getCurrentUser());
                        contact.setPhone(Util.convertToInternationalNumber(context, arItem.get(i).phoneNumber));
                        contact.setACL(ParseUser.getCurrentUser());
                        contactList.add(contact);
                    }
                    //upload local contact list to parse server
                    ParseObject.saveAllInBackground(contactList, new SaveCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e==null)
                                handler.sendEmptyMessage(0);
                            else
                                ParseAPI.erroHandling(context, e);
                        }
                    });
                } else
                    ParseAPI.erroHandling(context, e);
            }
        });
/*
        //find contacts created by current user for deleting
        ParseQuery<Contact> query = ParseQuery.getQuery(Contact.class);
        query.whereEqualTo("createdBy", user);
        query.setLimit(1);
        query.findInBackground(new FindCallback<Contact>() {
            @Override
            public void done(List<Contact> results, ParseException e) {
                if (e == null) {

                    //delete all exting contacts in parse
                    ParseObject.deleteAllInBackground(results, new DeleteCallback() {
                        @Override
                        public void done(ParseException e) {
                            if (e == null) {
                                List<Contact> contactList = new ArrayList<Contact>();

                                for (int i = 0; i < arItem.size(); i++) {
                                    Contact contact = new Contact();
                                    contact.setCreatedBy(ParseUser.getCurrentUser());
                                    contact.setPhone(Util.convertToInternationalNumber(context,arItem.get(i).phoneNumber));
                                    contact.setACL(ParseUser.getCurrentUser());
                                    contactList.add(contact);
                                }

                                //upload local contact list to parse server
                                ParseObject.saveAllInBackground(contactList, new SaveCallback() {
                                    @Override
                                    public void done(ParseException e) {
                                        if(e==null)
                                            handler.sendEmptyMessage(0);
                                        else
                                            ParseAPI.erroHandling(context, e);
                                    }
                                });
                            }
                            else{
                                ParseAPI.erroHandling(context, e);
                            }
                        }
                    });
                } else {
                    ParseAPI.erroHandling(context, e);
                }
            }
        });
*/
    }



    //업로드된 주소록 기반 쉐어캠 친구 목록 동기화
    public static void syncFriendWithContact(final Context context,final Handler handler)
    {
        Log.d("jyr", "sync friend with contact");
        ParseCloud.callFunctionInBackground(ParseAPI.SYNC_CONTACT, new HashMap<String, Object>(), new FunctionCallback<List<Friend>>() {
            public void done(List<Friend> result, ParseException e) {

//                Log.d("jyr",result.toString());
                if (e == null) {
                    handler.sendEmptyMessage(0);
                } else
                    ParseAPI.erroHandling(context, e);
            }
        });
    }

    public static void signUpCompleted(final Context context, final Handler handler){
        ParseCloud.callFunctionInBackground(ParseAPI.SIGN_UP_COMPLETED, new HashMap<String, Object>(), new FunctionCallback<JSONObject>() {
            public void done(JSONObject result, ParseException e) {

                if(e==null) {
                    Log.d("jyr","sign up completed and fetch current user");
                    //refresh user data
                    ParseUser.getCurrentUser().fetchInBackground(new GetCallback<ParseObject>() {
                        @Override
                        public void done(ParseObject parseObject, ParseException e) {
                            if (e == null) {
                                handler.sendEmptyMessage(0);
                                Log.d("jyr","user completed="+ParseUser.getCurrentUser().get("completed"));
                            } else
                                ParseAPI.erroHandling(context, e);
                        }
                    });
                }
                else
                    ParseAPI.erroHandling(context, e);
//                Log.d("jyr",result.toString());

            }
        });
    }
    public static void erroHandling(Context context,ParseException e)
    {

        Log.e("jyr", "Error: " + e.getMessage());
        Util.showToast(context, ErrorCode.getToastMessageId(e));
    }
}


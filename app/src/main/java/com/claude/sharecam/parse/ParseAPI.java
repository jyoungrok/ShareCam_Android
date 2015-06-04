package com.claude.sharecam.parse;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract;
import android.util.Log;

import com.claude.sharecam.share.ContactItem;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;

/**
 * Created by Claude on 15. 5. 31..
 */
public class ParseAPI {

    public static final String SM_PHONE_VERIFY="sm_phone_verify";
    public static final String SM_PHONE_CONFIRM="sm_phone_confirm";

    //upload local contact to parse server
    public static void initContact(Context context)
    {
        String[] projection = {ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME, ContactsContract.CommonDataKinds.Phone.NUMBER,ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER};
        //QUERY FOR THE PEOPLE IN YOUR ADDRESS BOOK
        Cursor cursor = context.getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");
        ArrayList<ContactItem> arItem=new ArrayList<ContactItem>();


        if(cursor.moveToFirst()) {
            do{

                //휴대폰 번호가 있는 경우
                if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.HAS_PHONE_NUMBER)))>0) {
                    String name = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
                    String phone = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                    arItem.add(new ContactItem(name,null,phone));
                }

            }while(cursor.moveToNext());

            setContact(arItem);
        }

    }

    //upload array list of contactItem to parse server
    public static void setContact(ArrayList<ContactItem> arItem)
    {

        for(int i=0; i<arItem.size(); i++)
        {
            Contact contact=new Contact();
            contact.setCreatedBy(ParseUser.getCurrentUser());
            contact.setPhone(arItem.get(i).phoneNumber);
            final int a=i;
            contact.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    Log.d("jyr","setContact"+a);
                }
            });
        }


    }
}

package com.claude.sharecam.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.claude.sharecam.parse.Contact;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.share.ContactItemList;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * 연락처, 쉐어캠 친구 목록 불러옴
 * IndividualFragment에서 사용
 *
 *
 *
 *
 */
public class ContactLoader  extends AsyncTaskLoader<ContactItemList> {

    public static final String TAG="ContactLoader";
    boolean withShareUser;//공유 사용자 목록도 불러오는 경우
    Context context;
    public ContactLoader(Context context,boolean withShareUser) {
        super(context);
        this.context=context;
        this.withShareUser=withShareUser;
    }

    @Override
    public ContactItemList loadInBackground() {

        ContactItemList contactItemList=new ContactItemList();

        Log.d(TAG,"loadInBackground");
        try {

            contactItemList.friendItems=new ArrayList<Contact>();
            contactItemList.contactItems=new ArrayList<Contact>();

            List<Contact> allContactList=ParseAPI.getPinnedContactList(true);
            Log.d(TAG,"contact load done");

            for(int i=0; i<allContactList.size(); i++)
            {
                if(allContactList.get(i).getFriendUser()!=null)
                {
                    contactItemList.friendItems.add(allContactList.get(i));
                }
                else{
                    contactItemList.contactItems.add(allContactList.get(i));
                }
            }

            Log.d(TAG,"distinguish frienad contact  done");

//            contactItemList.friendItems=ParseAPI.getContactListWithFriendUser();
//            Log.d(TAG,"friendItem load done "+contactItemList.friendItems.size());
//            contactItemList.contactItems=Util.getAllContactListWithoutFriend(context,contactItemList.friendItems);
            Log.d(TAG,"contactItem load done "+contactItemList.contactItems.size());
            if(withShareUser) {

                contactItemList.addedItems = ParseAPI.getShareContactList(context, contactItemList.contactItems,contactItemList.friendItems);

                Log.d(TAG, "addedItem load done " + contactItemList.addedItems.size());
            }

//            contactItemList.addedItems=ParseAPI.getShareContactList();
//            Log.d(TAG,"addedItem load done");
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return contactItemList;
    }
    @Override
    protected void onStartLoading() {
        Log.d("jyr","start loading");
        forceLoad();
    }
}

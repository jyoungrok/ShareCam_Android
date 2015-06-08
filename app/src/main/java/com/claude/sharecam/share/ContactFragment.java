package com.claude.sharecam.share;

import android.database.Cursor;
import android.os.Bundle;

import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.claude.sharecam.R;
import com.claude.sharecam.Util;

import java.util.ArrayList;

public class ContactFragment extends Fragment {


    public static final String MODE = "mode";
    public static final int RECOMMEND = 0;
    public static final int CONTACT = 1;
    public static final int SHARECAM = 2;

    RecyclerView personRecyclerView;
    PersonAdapter personAdapter;
    ArrayList<ContactItem> contactItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_personlist, container, false);


        personRecyclerView = (RecyclerView) root.findViewById(R.id.personRecyclerView);

//        personItems = new PersonItem[]{new PersonItem("aaa", "aaa"), new PersonItem("aaa", "aaa")};
//        personAdapter = (PersonAdapter) new PersonAdapter(personItems);

        personRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        personRecyclerView.setAdapter(personAdapter);


        setAdapter(getArguments().getInt(MODE));


        // Inflate the layout for this fragment
        return root;
    }

    private void setAdapter(int mode) {

//        contactItems =new ArrayList<ContactItem>();

        switch (mode) {
            case RECOMMEND:

                break;
            case CONTACT:
                contactItems=Util.getContactList(getActivity());

//                ArrayList numberList=new ArrayList();
//                // 로컬에서 연락처 데이터 불러옴
//                Cursor cursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI,
//                        new String[]{ContactsContract.CommonDataKinds.Phone._ID, ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
//                                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
//                                ContactsContract.CommonDataKinds.Phone.NUMBER},
//                        ContactsContract.Contacts.HAS_PHONE_NUMBER + "=1", null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " COLLATE LOCALIZED  ASC");
//
//                while (cursor.moveToNext())
//                {
//
//                    String name=cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
//                    String phoneNumber = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
//                    if(!numberList.toString().contains(phoneNumber)) {
//                        numberList.add(phoneNumber);
//                        contactItems.add(new ContactItem(name, null, phoneNumber));
//                    }
//                }
//
//                cursor.close();


               /*
                //QUERY FOR THE PEOPLE IN YOUR ADDRESS BOOK
                Cursor cursor = getActivity().getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " ASC");

                if(cursor.moveToFirst()) {
                    do{

                        //휴대폰 번호가 있는 경우
                        if(Integer.parseInt(cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER)))>0) {
                            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                            String contact_id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID ));

                            // Query and loop for every phone number of the contact
                            Cursor phoneCursor = getActivity().getContentResolver().query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null, ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[] { contact_id }, null);

                            if(phoneCursor.moveToFirst())
                            {
                                String phone = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                                contactItems.add(new ContactItem(name,null,phone));
                            }

                            phoneCursor.close();


                        }

                    }while(cursor.moveToNext());

                    cursor.close();
                }*/


                break;
            case SHARECAM:

                break;
        }

        personAdapter=new PersonAdapter(contactItems);

        personRecyclerView.setAdapter(personAdapter);
    }



    private class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {

        ArrayList<ContactItem> contactItems;

        public PersonAdapter(ArrayList<ContactItem> contactItems) {
            this.contactItems = contactItems;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.person_recycle_item, null);

            // create ViewHolder
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }


        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, int position) {

            // - get data from your itemsData at this position
            // - replace the contents of the view with that itemsData

            viewHolder.personNameTxt.setText(contactItems.get(position).personName);
//            viewHolder.personProfileImg.setImageResource(personItems[position].personProfile);


        }

        @Override
        public int getItemCount() {
            return contactItems.size();
        }

        // inner class to hold a reference to each item of RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView personNameTxt;
            public ImageView personProfileImg;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                personNameTxt = (TextView) itemLayoutView.findViewById(R.id.personNameTxt);
                personProfileImg = (ImageView) itemLayoutView.findViewById(R.id.personProfileImg);
            }
        }

    }
/*
    private class PersonCursorAdapter extends RecyclerView.Adapter<PersonCursorAdapter.ViewHolder> {

        CursorAdapter mCursorAdapter;
        Context mContext;

        public PersonCursorAdapter(Context context, Cursor c) {

            mContext = context;

            mCursorAdapter = new CursorAdapter(mContext, c, 0) {

                @Override
                public View newView(Context context, Cursor cursor, ViewGroup parent) {
                    // create a new view
                    View itemLayoutView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.person_recycle_item, null);

                    // create ViewHolder

                    ViewHolder viewHolder = new ViewHolder(itemLayoutView);
                    return itemLayoutView;
                }

                @Override
                public void bindView(View view, Context context, Cursor cursor) {
                    // Binding operations
                    ((TextView)view.findViewById(R.id.personNameTxt)).setText(cursor.);

                }
            };
        }


        @Override
        public PersonCursorAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // Passing the inflater job to the cursor-adapter
            View v = mCursorAdapter.newView(mContext, mCursorAdapter.getCursor(), parent);
            return new ViewHolder(v);
        }

        @Override
        public void onBindViewHolder(PersonCursorAdapter.ViewHolder holder, int position) {
            // Passing the binding operation to cursor loader
            mCursorAdapter.bindView(holder.itemView, mContext, mCursorAdapter.getCursor());
        }

        @Override
        public int getItemCount() {
            return mCursorAdapter.getCount();
        }
        // inner class to hold a reference to each item of RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView personNameTxt;
            public ImageView personProfileImg;

            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                personNameTxt = (TextView) itemLayoutView.findViewById(R.id.personNameTxt);
                personProfileImg = (ImageView) itemLayoutView.findViewById(R.id.personProfileImg);
            }
        }
    }
*/
}

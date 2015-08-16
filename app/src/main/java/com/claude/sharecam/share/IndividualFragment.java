package com.claude.sharecam.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.EditText;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.dialog.MyDialogBuilder;
import com.claude.sharecam.loader.IndividualLoader;
//import com.claude.sharecam.loader.ShareIndividualLoader;
import com.claude.sharecam.orm.IndividualItem;
import com.claude.sharecam.view.ImageViewRecyclable;
import com.parse.GetDataCallback;
import com.parse.ParseException;

import java.util.ArrayList;

/**
 연락처 , 쉐어캠 친구 추가
 */
public class IndividualFragment extends Fragment{

    public static final String TAG="IndividualFragment";

    //호출하는 activity에서 구현
    public interface AddedItems{
        void setAddedItems(ArrayList<IndividualItem> addedItems);
        ArrayList<IndividualItem> getAddedItems();
        void addItem(IndividualItem individualItem);
        void removeItem(IndividualItem individualItem);
    }


    //액션바 아이템
    public interface ActionbarItems{
        void clickActionItem1();
    }
    AddedItems addedItems;
    ActionbarItems actionbarItems;

    ArrayList<IndividualItem> contactItems = null;
    ArrayList<IndividualItem> friendItems = null;

    ArrayList<IndividualItem> searchContactItems;
    ArrayList<IndividualItem> searchFriendItems;

    public int mode;//모드에 따라 데이터를 불러와 할당하는 종루가 달라진다.
    public static final String MODE="mode";

    public static final int ADD_NORMAL_MODE=0;// 쉐어캠 친구, 연락처 목록만 불러옴
    public static final int ADD_SHARE_USER_MODE=1;// 쉐어캠 친구, 연락처, 공유 대상으로 추가되어 있던 사용자 목록을 불러온다.
//    //the name of extra data
//    public static final String PARENT_ACTIVITY="parentActivity";
//
//    //the values of extra data
//    public static final int PARENT_SHARE_ACTIVITY=0;
//    public static final int PARENT_ADD_GROUP_ACTIVITY=1;
//    public static final int PARENT_DEFAULT_ACTIVITY=PARENT_SHARE_ACTIVITY;

//    public int parentActivity;


    //    private FragmentTabHost mTabHost;
    private EditText searchPersonET;
    private RecyclerView apRecycleView;
//    private LinearLayout tabLayout;



//    public ArrayList<PersonItem> personItems;
//    public ArrayList<PersonItem> friendItems;

    public AddedPersonAdapter apAdapter;
//    private ArrayList<PersonItem> addedItems;

    ExpandableListView individualListview;
    IndividualAdapter individualAdapter;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_person, container, false);


        addedItems=(AddedItems)getActivity();
        mode=getArguments().getInt(MODE);
//        parentActivity=getArguments().getInt(PARENT_ACTIVITY,PARENT_DEFAULT_ACTIVITY);

        searchPersonET= (EditText) root.findViewById(R.id.searchPersonET);
        apRecycleView=(RecyclerView)root.findViewById(R.id.apRecycleView);
        individualListview=(ExpandableListView)root.findViewById(R.id.individualListview);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        apRecycleView.setLayoutManager(linearLayoutManager);
        apAdapter=new AddedPersonAdapter(addedItems.getAddedItems());
        apRecycleView.setAdapter(apAdapter);

        searchFriendItems=new ArrayList<IndividualItem>();
        searchContactItems=new ArrayList<IndividualItem>();




        if(mode==ADD_NORMAL_MODE)
        {
            actionbarItems=(ActionbarItems)getActivity();
            Util.setActionbarItem_1(getActivity(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionbarItems.clickActionItem1();
                }
            });
        }

        //공유 대상을 설정 할 떄 (ShareActivity에서 실행한 경우 )
        //연락처, 쉐어캠 친구 데이터를 아직 불러오지 않은 경우
        if((contactItems ==null ||  friendItems==null)) {
            Log.d("jyr", "load data");

            //개인, 쉐어캠 친구 데이터들 불러옴
            getLoaderManager().initLoader(Constants.SHARE_INDIVIDUAL_LOADER, null, new LoaderManager.LoaderCallbacks<IndividualItemList>() {
                @Override
                public Loader<IndividualItemList> onCreateLoader(int id, Bundle args) {

                    if(mode==ADD_NORMAL_MODE)
                        return new IndividualLoader(getActivity(),false);
                    else if(mode==ADD_SHARE_USER_MODE)
                        return new IndividualLoader(getActivity(),true);
                    else
                        return new IndividualLoader(getActivity(),false);

                }

                @Override
                public void onLoadFinished(Loader<IndividualItemList> loader, IndividualItemList data) {

                    Log.d("jyr", "person item list on load finished");
                    if(contactItems ==null || friendItems==null)
                    {

                        //불러온 데이터 저장
                        contactItems=data.contactItems;
                        friendItems=data.friendItems;
//                        setAdapter(getArguments().getInt(MODE));

                        if(mode==ADD_SHARE_USER_MODE) {
                            //추가된 데이터들 설정
                            addedItems.setAddedItems(data.addedItems);
                        }
//                        ((ShareActivity) getActivity()).setAddedItems(data.addedItems);
                        refreshItems();

                        setIndividualAdapter();


                    }
                }
                @Override
                public void onLoaderReset(Loader<IndividualItemList> loader) {

                }
            });
        }

        //데이터를 불러왔던 경우 그대로 설정
        else {
            setIndividualAdapter();
        }


        searchPersonET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {

                searchContactItems.clear();
                searchFriendItems.clear();

                String searchStr=searchPersonET.getText().toString();

                for(int i=0; i<friendItems.size(); i++)
                {
                    if(friendItems.get(i).personName.contains(searchStr))
                    {
                        searchFriendItems.add(friendItems.get(i));
                    }
                }

                for(int i=0; i<contactItems.size(); i++)
                {
                    if(contactItems.get(i).personName.contains(searchStr))
                    {
                        searchContactItems.add(contactItems.get(i));
                    }
                }

                individualAdapter.setItems(searchContactItems,searchFriendItems);
                individualAdapter.notifyDataSetChanged();


            }
        });
/*
        Bundle args2=new Bundle();
        args2.putInt(PersonListFragment.MODE, PersonListFragment.CONTACT);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.contact_tab)).setIndicator(getString(R.string.contact_tab)),
                PersonListFragment.class, args2);

        Bundle args3=new Bundle();
        args3.putInt(PersonListFragment.MODE, PersonListFragment.SHARECAM);
        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.sharecam_tab)).setIndicator(getString(R.string.sharecam_tab)),
                PersonListFragment.class, args3);
        Bundle args1=new Bundle();*/
//        args1.putInt(PersonListFragment.MODE, PersonListFragment.RECOMMEND);
//        mTabHost.addTab(mTabHost.newTabSpec(getString(R.string.recommend_tab)).setIndicator(getString(R.string.recommend_tab)),
//                PersonListFragment.class, args1);



        // Inflate the layout for this fragment
        return root;
    }


//    public void setAddedItmes(ArrayList<IndividualItem> items)
//    {
//        switch(parentActivity)
//        {
//            default:
//            case PARENT_SHARE_ACTIVITY:
//                ((ShareActivity)getActivity()).addedItems=items;
//            case PARENT_ADD_GROUP_ACTIVITY:
//                ((AddGroupActivity)getActivity()).addedItems=items;
//        }
//    }
//
//    public ArrayList<IndividualItem> getAddedItems()
//    {
//        switch(parentActivity)
//        {
//            default:
//            case PARENT_SHARE_ACTIVITY:
//                return ((ShareActivity)getActivity()).addedItems;
//    public void setContactItems(ArrayList<IndividualItem> items)
//    {
//        switch(parentActivity)
//        {
//            default:
//            case PARENT_SHARE_ACTIVITY:
//                ((ShareActivity)getActivity()).contactItems=items;
//            case PARENT_ADD_GROUP_ACTIVITY:
//                ((AddGroupActivity)getActivity()).contactItems=items;
//        }
//    }
//
//    public ArrayList<IndividualItem> getContactItems()
//    {
//        switch(parentActivity)
//        {
//            default:
//            case PARENT_SHARE_ACTIVITY:
//                return ((ShareActivity)getActivity()).contactItems;
//            case PARENT_ADD_GROUP_ACTIVITY:
//                return   ((AddGroupActivity)getActivity()).contactItems;
//        }
//    }
//
//    public void setFriendItems(ArrayList<IndividualItem> items)
//    {
//        switch(parentActivity)
//        {
//            default:
//            case PARENT_SHARE_ACTIVITY:
//                ((ShareActivity)getActivity()).friendItems=items;
//            case PARENT_ADD_GROUP_ACTIVITY:
//                ((AddGroupActivity)getActivity()).friendItems=items;
//        }
//
//    }
//
//    public ArrayList<IndividualItem> getFriendItems()
//    {
//        switch(parentActivity)
//        {
//            default:
//            case PARENT_SHARE_ACTIVITY:
//                return ((ShareActivity)getActivity()).friendItems;
//            case PARENT_ADD_GROUP_ACTIVITY:
//                return   ((AddGroupActivity)getActivity()).friendItems;
//        }
//    }

//            case PARENT_ADD_GROUP_ACTIVITY:
//                return   ((AddGroupActivity)getActivity()).addedItems;
//        }
////    }
//    public void setContactItems(ArrayList<IndividualItem> items)
//    {
//        switch(parentActivity)
//        {
//            default:
//            case PARENT_SHARE_ACTIVITY:
//                ((ShareActivity)getActivity()).contactItems=items;
//            case PARENT_ADD_GROUP_ACTIVITY:
//                ((AddGroupActivity)getActivity()).contactItems=items;
//        }
//    }
//
//    public ArrayList<IndividualItem> getContactItems()
//    {
//        switch(parentActivity)
//        {
//            default:
//            case PARENT_SHARE_ACTIVITY:
//                return ((ShareActivity)getActivity()).contactItems;
//            case PARENT_ADD_GROUP_ACTIVITY:
//                return   ((AddGroupActivity)getActivity()).contactItems;
//        }
//    }
//
//    public void setFriendItems(ArrayList<IndividualItem> items)
//    {
//        switch(parentActivity)
//        {
//            default:
//            case PARENT_SHARE_ACTIVITY:
//                ((ShareActivity)getActivity()).friendItems=items;
//            case PARENT_ADD_GROUP_ACTIVITY:
//                ((AddGroupActivity)getActivity()).friendItems=items;
//        }
//
//    }
//
//    public ArrayList<IndividualItem> getFriendItems()
//    {
//        switch(parentActivity)
//        {
//            default:
//            case PARENT_SHARE_ACTIVITY:
//                return ((ShareActivity)getActivity()).friendItems;
//            case PARENT_ADD_GROUP_ACTIVITY:
//                return   ((AddGroupActivity)getActivity()).friendItems;
//        }
//    }

    //연락처, 쉐어캠 친구 리스트 설정
    private void  setIndividualAdapter()
    {
        individualAdapter=new IndividualAdapter(getActivity(),contactItems,friendItems);

        individualListview.setAdapter(individualAdapter);

        //모든 group expand해줌
        for(int i=0; i<IndividualAdapter.GROUP_NUM; i++)
        {
            individualListview.expandGroup(i);
        }

        //쉐어캠 친구들의 경우 사진 프로필 사진 설정
        for(int i=0; i<friendItems.size(); i++)
        {
            final int index=i;
            if(friendItems.get(index).serializableFriendProfileFile.getParseFile()!=null) {
                friendItems.get(index).serializableFriendThumProfileFile.getParseFile().getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if (e == null) {
                            friendItems.get(index).friendThumProfileBytes = bytes;
                            individualAdapter.notifyDataSetChanged();
                            apAdapter.notifyDataSetChanged();
                        }

                    }
                });
            }

        }
    }
    public void refreshItems()
    {
        if(apAdapter.addedItems.size()>0)
            apRecycleView.setVisibility(View.VISIBLE);

        apAdapter.notifyDataSetChanged();
    }

    public void clickItem(IndividualItem individualItem)
    {
        IndividualItem addedIndividualItem;

        //연락처에서 쉐어캠 친구를 클릭한 경우 쉐어캠 친구 item넘겨줌
//        if(individualItem.MODE== IndividualItem.CONTACT && individualItem.isFriend)
//        {
//            addedIndividualItem =friendItems.get(individualItem.friendIndex);
//        }
//        else
            addedIndividualItem = individualItem;

        if(!addedIndividualItem.added)
            addItem(addedIndividualItem);
        else
            removeItem(addedIndividualItem);
    }
    public void addItem(IndividualItem individualItem)
    {
//        int addPosition=((ShareActivity) getActivity()).addedItems.size();
        apRecycleView.setVisibility(View.VISIBLE);
        individualItem.added=true;
        Log.d("jyr","addItem+"+ individualItem.personName);
        addedItems.addItem(individualItem);
        apAdapter.notifyDataSetChanged();
//        apAdapter.notifyDataSetChanged();
    }

    public void removeItem(IndividualItem individualItem)
    {
        Log.d("jyr","removeItem+"+ individualItem.personName);
//        int removePosition=((ShareActivity) getActivity()).addedItems.size()-1;
        individualItem.added=false;
        addedItems.removeItem(individualItem);
//        Log.d("jyr","addedItem size= "+((ShareActivity)getActivity()).addedItems.size());
        apAdapter.notifyDataSetChanged();
        if(addedItems.getAddedItems().size()==0)
            apRecycleView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        mTabHost = null;
    }




    //추가된 사용자들을 위한 adapter
    private class AddedPersonAdapter extends RecyclerView.Adapter<AddedPersonAdapter.ViewHolder> {

        ArrayList<IndividualItem> addedItems;

        public AddedPersonAdapter(ArrayList<IndividualItem> addedItems) {
            this.addedItems = addedItems;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.added_person_item, null);

            // create ViewHolder
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }


        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(final ViewHolder viewHolder, final int position) {

            // - get data from your itemsData at this position
            // - replace the contents of the view with that itemsData

            viewHolder.addedPersonName.setText(addedItems.get(position).personName);


                //쉐어캠 친구 프로필
                if (addedItems.get(position).MODE == IndividualItem.FRIEND) {

                    if(addedItems.get(position).friendProfileBytes!=null)
                    {
                        Bitmap bmp = BitmapFactory.decodeByteArray(addedItems.get(position).friendProfileBytes,0,addedItems.get(position).friendProfileBytes.length);
                        viewHolder.addedPersonProfile.setImageBitmap(bmp);
                    }
//                    Bitmap bmp = BitmapFactory.decodeByteArray(addedItems.get(position).friendProfile, 0, addedItems.get(position).friendProfile.length);
//                    viewHolder.addedPersonProfile.setImageBitmap(bmp);
// Set the Bitmap data to the ImageView

//                    Picasso.with(getActivity()).load(addedItems.get(position).contactProfile).into(viewHolder.addedPersonProfile);
                }
                else if (addedItems.get(position).MODE == IndividualItem.CONTACT && addedItems.get(position).contactProfile!=null)
                    viewHolder.addedPersonProfile.setImageURI(Uri.parse(addedItems.get(position).contactProfile));
                else{
                    viewHolder.addedPersonProfile.setImageResource(R.mipmap.profile);
                }


            viewHolder.addedPersonProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickItem(addedItems.get(position));
                }
            });

        }

        @Override
        public int getItemCount() {
            return addedItems.size();
        }

        // inner class to hold a reference to each item of RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView addedPersonName;
            public ImageView addedPersonProfile;


            public ViewHolder(View itemLayoutView) {
                super(itemLayoutView);
                addedPersonName = (TextView) itemLayoutView.findViewById(R.id.addedPersonName);
                addedPersonProfile = (ImageView) itemLayoutView.findViewById(R.id.addedPersonProfile);

            }
        }

    }


    //연락처, 쉐어캠 친구 리스트
    public class IndividualAdapter extends BaseExpandableListAdapter {

        public static final int GROUP_NUM =2;
        private final int contactPosition=1;
        private final int friendPosition=0;
        private LayoutInflater inflater = null;

        ArrayList<IndividualItem> contactItems;
        ArrayList<IndividualItem> friendItems;


        GroupViewHolder groupViewHolder;
        ChildViewHolder childViewHolder;


        public IndividualAdapter(Context context, ArrayList<IndividualItem> contactItems, ArrayList<IndividualItem> friendItems)
        {
            this.inflater = LayoutInflater.from(context);
            this.contactItems=contactItems;
            this.friendItems=friendItems;
        }


        public void setItems(ArrayList<IndividualItem> contactItems, ArrayList<IndividualItem> friendItems)
        {
            this.contactItems=contactItems;
            this.friendItems=friendItems;
        }
        private ArrayList<IndividualItem> getIndividualItem(int position)
        {
            switch (position)
            {
                case contactPosition:
                    return contactItems;
                case friendPosition:
                    return friendItems;
                default:
                    return friendItems;
            }
        }


        @Override
        public int getGroupCount() {
            return GROUP_NUM;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
//            Log.d("jyr","group count"+groupPosition);
            return getIndividualItem(groupPosition).size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return getIndividualItem(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return getIndividualItem(groupPosition).get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {


            View v = convertView;

            if(v == null){
                groupViewHolder = new GroupViewHolder();
                v = inflater.inflate(R.layout.individual_group_item, parent, false);
                groupViewHolder.groupText = (TextView) v.findViewById(R.id.groupText);
                groupViewHolder.groupExpandBtn= (ImageView) v.findViewById(R.id.groupExpandBtn);
                v.setTag(groupViewHolder);
            }else{
                groupViewHolder = (GroupViewHolder)v.getTag();
            }

            // 그룹을 펼칠때와 닫을때 아이콘을 변경해 준다.
//            if(isExpanded){
//                viewHolder.iv_image.setBackgroundColor(Color.GREEN);
//            }else{
//                viewHolder.iv_image.setBackgroundColor(Color.WHITE);
//            }

            if(groupPosition==friendPosition)
                groupViewHolder.groupText.setText(getString(R.string.existing_sharecam_friend));
            else if(groupPosition==contactPosition)
                groupViewHolder.groupText.setText(getString(R.string.not_exisiting_sharecam_friend));

            return v;

        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

            View v = convertView;

            if(v == null){
                childViewHolder=new ChildViewHolder();
                v=inflater.inflate(R.layout.idividual_item, parent, false);
                childViewHolder.personNameTxt = (TextView) v.findViewById(R.id.personNameTxt);
                childViewHolder.personProfileImg = (ImageViewRecyclable) v.findViewById(R.id.personProfileImg);
                childViewHolder.addPersonBtn=(ImageView)v.findViewById(R.id.addPersonBtn);
                childViewHolder.individualLayout=(LinearLayout)v.findViewById(R.id.individualLayout);
                v.setTag(childViewHolder);
            }
            else{
                childViewHolder= (ChildViewHolder) v.getTag();
            }

            final IndividualItem individualItem=getIndividualItem(groupPosition).get(childPosition);

            //연락처에서 쉐어캠 친구에 추가된 item은 빼고 보여줌
//            if(groupPosition==contactPosition && individualItem.isFriend)
//            {
//                childViewHolder.individualLayout.setVisibility(View.GONE);
//            }

//            else{

//            Log.d(TAG,"before before set profie");
            childViewHolder.individualLayout.setVisibility(View.VISIBLE);
//            if(individualItem.isFriend)
//                childViewHolder.personNameTxt.setText(individualItem.personName);
//            else
                childViewHolder.personNameTxt.setText(individualItem.personName);
//            if(individualItem.contactProfile !=null) {


            childViewHolder.personProfileImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyDialogBuilder.showProfileDialog(getActivity(),getFragmentManager(),individualItem);
                }
            });

            //쉐어캠 친구인 경우
            if (individualItem.MODE == IndividualItem.FRIEND)
            {
                    Log.d(TAG,"before set profie");
                if(individualItem.friendThumProfileBytes!=null)
                {
                    Bitmap bmp = BitmapFactory.decodeByteArray(individualItem.friendThumProfileBytes,0,individualItem.friendThumProfileBytes.length);
                    childViewHolder.personProfileImg.setImageBitmap(bmp);
                }
                else{
                    childViewHolder.personProfileImg.setImageResource(R.mipmap.profile);
                }


//                individualItem.friendProfileFile.getDataInBackground(new GetDataCallback() {
//                    @Override
//                    public void done(byte[] bytes, ParseException e) {
//                        Log.d(TAG, "set profie");
//                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
//                        childViewHolder.personProfileImg.setImageBitmap(bmp);
////                        notifyDataSetChanged();
//                    }
//                });
//                    Picasso.with(getActivity()).load(individualItem.contactProfile).into(childViewHolder.personProfileImg);
            }


            //연락처인 경우
            else if (individualItem.MODE == IndividualItem.CONTACT && individualItem.contactProfile!=null)
                childViewHolder.personProfileImg.setImageURI(Uri.parse(individualItem.contactProfile));
            else
                childViewHolder.personProfileImg.setImageResource(R.mipmap.profile);

//
//            }
//            else
//                childViewHolder.personProfileImg.setImageResource(R.mipmap.profile);


            childViewHolder.addPersonBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickItem(individualItem);
                }
            });

//            }
            return v;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }


        private class GroupViewHolder{
            TextView groupText;
            ImageView groupExpandBtn;

        }

        private class ChildViewHolder{
            public LinearLayout individualLayout;
            public TextView personNameTxt;
            public ImageViewRecyclable personProfileImg;
            public ImageView addPersonBtn;

        }
    }
}

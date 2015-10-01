package com.claude.sharecam.share;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
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

import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.dialog.MyDialogBuilder;
//import com.claude.sharecam.loader.ShareIndividualLoader;
import com.claude.sharecam.parse.Contact;
import com.claude.sharecam.util.ActionBarUtil;
import com.claude.sharecam.view.ImageViewRecyclable;
import com.parse.GetDataCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

/**
 * 1. ShareCam에 있는 연락처 목록 Parse로 부터 load
 * 2. ShareCam에 없는 연락처 목록 Content Provide로 부터 load
 * 3. 공유 설정된 phone에 부합하는 친구, 연락처 목록 addedItem에 추가
 *
 * --> 공유 설정은 Contact의 objectId가 아닌 phone으로 저장
 *      why?
 *          서버에 저장하는 아이디가 아닐 뿐 더러 연락처 목록 불러올 떄 안드로이드 기기에서 데이터를 불러오므로
 */
public class IndividualFragment extends Fragment{

    public static final String TAG="IndividualFragment";

    //호출하는 activity에서 구현
//    public interface AddedItems{
////        void setAddedItems(List<Contact> addedItems);
////        ArrayList<Contact> getAddedItems();
//        boolean isAdded(Contact individualItem);
//        void addItem(Contact individualItem);
//        void removeItem(Contact individualItem);
//    }


    //액션바 아이템
    public interface ActionbarItems{
        void clickActionItem1();
    }
//    AddedItems addedItems;
    ActionbarItems actionbarItems;
//
//    ArrayList<IndividualItem> contactItems = null;
//    ArrayList<IndividualItem> friendItems = null;
//
    ArrayList<Contact> searchContactItems;
    ArrayList<Contact> searchFriendItems;

//    List<Contact> contactItems = null;
//    List<Contact> friendItems = null;


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
//    private ArrayList<Contact> addedItems;

    ExpandableListView individualListview;
    IndividualAdapter individualAdapter;


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_person, container, false);


//        addedItems=(AddedItems)getActivity();
        mode=getArguments().getInt(MODE);
//        parentActivity=getArguments().getInt(PARENT_ACTIVITY,PARENT_DEFAULT_ACTIVITY);

        searchPersonET= (EditText) root.findViewById(R.id.searchPersonET);
        apRecycleView=(RecyclerView)root.findViewById(R.id.apRecycleView);
        individualListview=(ExpandableListView)root.findViewById(R.id.individualListview);


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        apRecycleView.setLayoutManager(linearLayoutManager);


        searchFriendItems=new ArrayList<Contact>();
        searchContactItems=new ArrayList<Contact>();



//
        if(mode==ADD_NORMAL_MODE)
        {
            actionbarItems=(ActionbarItems)getActivity();
            ActionBarUtil.setActionbarItem_1(getActivity(), new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    actionbarItems.clickActionItem1();
                }
            });
        }

        //공유 대상을 설정 할 떄 (ShareActivity에서 실행한 경우 )
        //연락처, 쉐어캠 친구 데이터를 아직 불러오지 않은 경우

            Log.d("jyr", "load data");
            //연락처 데이터 로드
            Util.initContactItemList(getActivity(),new Handler(){
                @Override
                public void handleMessage(Message msg){

                    //불러온 데이터 저장
//                        contactItems=Util.contactItemList.contactItems;
//                        friendItems=Util.contactItemList.friendItems;
////                        setAdapter(getArguments().getInt(MODE));
//
//                        if(mode==ADD_SHARE_USER_MODE) {
//                            //추가된 데이터들 설정
//                            addedItems.setAddedItems(Util.contactItemList.addedItems);
//                        }
//                        ((ShareActivity) getActivity()).setAddedItems(data.addedItems);
                        //추가된 데이터들 갱신


                        setAdapter();

//                    refreshAPItems();
                }
            });

//            //개인, 쉐어캠 친구 데이터들 불러옴
//            getLoaderManager().initLoader(Constants.SHARE_INDIVIDUAL_LOADER, null, new LoaderManager.LoaderCallbacks<ContactItemList>() {
//                @Override
//                public Loader<ContactItemList> onCreateLoader(int id, Bundle args) {
//
//                    if(mode==ADD_NORMAL_MODE)
//                        return new ContactLoader(getActivity(),false);
//                    else if(mode==ADD_SHARE_USER_MODE)
//                        return new ContactLoader(getActivity(),true);
//                    else
//                        return new ContactLoader(getActivity(),false);
//
//                }
//
//                @Override
//                public void onLoadFinished(Loader<ContactItemList> loader, ContactItemList data) {
//
//                    Log.d("jyr", "person item list on load finished");
//                    if(contactItems ==null || friendItems==null)
//                    {
//
//                        //불러온 데이터 저장
//                        contactItems=data.contactItems;
//                        friendItems=data.friendItems;
////                        setAdapter(getArguments().getInt(MODE));
//
//                        if(mode==ADD_SHARE_USER_MODE) {
//                            //추가된 데이터들 설정
//                            addedItems.setAddedItems(data.addedItems);
//                        }
////                        ((ShareActivity) getActivity()).setAddedItems(data.addedItems);
//                        //추가된 데이터들 갱신
//                        refreshAPItems();
//
//                        setAdapter();
//
//
//                    }
//                }
//                @Override
//                public void onLoaderReset(Loader<ContactItemList> loader) {
//
//                }
//            });



        searchPersonET.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
//
                searchContactItems.clear();
                searchFriendItems.clear();

                String searchStr=searchPersonET.getText().toString();

                for(int i=0; i<Util.contactItemList.friendItems.size(); i++)
                {
                    if(Util.contactItemList.friendItems.get(i).getFriendUser().getNmae().contains(searchStr))
                    {
                        searchFriendItems.add(Util.contactItemList.friendItems.get(i));
                    }
                }

                for(int i=0; i<Util.contactItemList.contactItems.size(); i++)
                {
                    if(Util.contactItemList.contactItems.get(i).getPinContactName().contains(searchStr))
                    {
                        searchContactItems.add(Util.contactItemList.contactItems.get(i));
                    }
                }

                individualAdapter.setItems(searchContactItems,searchFriendItems);
                individualAdapter.notifyDataSetChanged();


            }
        });



        // Inflate the layout for this fragment
        return root;
    }


    /**
     * 추가한 연락처
     * 연락처 리스트 설정
     */
    private void setAdapter()
    {
        //추가한 연락처 설정
        apAdapter=new AddedPersonAdapter((ArrayList<Contact>) Util.contactItemList.addedItems);
        apRecycleView.setAdapter(apAdapter);
        if(apAdapter.addedItems.size()>0) {
            apRecycleView.setVisibility(View.VISIBLE);
        }
        apAdapter.notifyDataSetChanged();

//        if(mode==ADD_SHARE_USER_MODE)
//        addedItems.setAddedItems(Util.contactItemList.addedItems);

        individualAdapter=new IndividualAdapter(getActivity(),Util.contactItemList.contactItems,Util.contactItemList.friendItems);

        individualListview.setAdapter(individualAdapter);

        //모든 group expand해줌
        for(int i=0; i<IndividualAdapter.GROUP_NUM; i++)
        {
            individualListview.expandGroup(i);
        }

        //쉐어캠 친구들의 경우 사진 프로필 사진 설정
        for(int i=0; i<Util.contactItemList.friendItems.size(); i++)
        {
            final int index=i;
            if(Util.contactItemList.friendItems.get(index).getFriendUser().getThumProfileFile()!=null) {
                Util.contactItemList.friendItems.get(index).getFriendUser().getThumProfileFile().getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if (e == null) {
                            Util.contactItemList.friendItems.get(index).friendThumProfileBytes = bytes;
                            individualAdapter.notifyDataSetChanged();
//                            individualAdapter.getChild(IndividualAdapter.friendPosition,index).notify();
                            apAdapter.notifyDataSetChanged();
                        }

                    }
                });
            }

        }
    }
    public void refreshAPItems()
    {
        if(apAdapter.addedItems.size()>0) {
//            for(int i=0; i<apAdapter.addedItems.size(); i++)
//            apAdapter.addedItems.
            apRecycleView.setVisibility(View.VISIBLE);
        }

        apAdapter.notifyDataSetChanged();
    }

    public void clickItem(Contact individualItem)
    {
        Contact addedIndividualItem;

        //연락처에서 쉐어캠 친구를 클릭한 경우 쉐어캠 친구 item넘겨줌
//        if(individualItem.MODE== IndividualItem.CONTACT && individualItem.isFriend)
//        {
//            addedIndividualItem =friendItems.get(individualItem.friendIndex);
//        }
//        else
        addedIndividualItem = individualItem;

        if(!Util.contactItemList.isAdded(addedIndividualItem))
            addItem(addedIndividualItem);
        else
            removeItem(addedIndividualItem);
    }
    public void addItem(Contact individualItem)
    {
//        int addPosition=((ShareActivity) getActivity()).addedItems.size();
        apRecycleView.setVisibility(View.VISIBLE);
//        individualItem.added=true;
        Log.d("jyr","addItem+"+ individualItem.getPhone());
//        addedItems.addItem(individualItem);
        Util.contactItemList.addedItems.add(individualItem);
        apAdapter.notifyDataSetChanged();
//        apAdapter.notifyDataSetChanged();
    }

    public void removeItem(Contact individualItem)
    {
        Log.d("jyr","removeItem+"+ individualItem.getPhone());
//        int removePosition=((ShareActivity) getActivity()).addedItems.size()-1;
//        individualItem.added=false;
        Util.contactItemList.addedItems.remove(individualItem);
//        addedItems.removeItem(individualItem);
//        Log.d("jyr","addedItem size= "+((ShareActivity)getActivity()).addedItems.size());
        apAdapter.notifyDataSetChanged();
        if(Util.contactItemList.addedItems.size()==0)
            apRecycleView.setVisibility(View.GONE);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
//        mTabHost = null;
    }




    //    //추가된 사용자들을 위한 adapter
    private class AddedPersonAdapter extends RecyclerView.Adapter<AddedPersonAdapter.ViewHolder> {

        ArrayList<Contact> addedItems;

        public AddedPersonAdapter(ArrayList<Contact> addedItems) {
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

            //쉐어캠에 있는 연락처인 경우
            if(addedItems.get(position).getFriendUser()!=null)
            {
                viewHolder.addedPersonName.setText(addedItems.get(position).getPinContactName());
                if(addedItems.get(position).friendThumProfileBytes!=null)
                {
                    Bitmap bmp = BitmapFactory.decodeByteArray(addedItems.get(position).friendThumProfileBytes,0,addedItems.get(position).friendThumProfileBytes.length);
                    viewHolder.addedPersonProfile.setImageBitmap(bmp);
                }
                else{
                    viewHolder.addedPersonProfile.setImageResource(R.mipmap.profile);
                }
            }
            else{
                viewHolder.addedPersonName.setText(addedItems.get(position).getPinContactName());

                if(addedItems.get(position).getPinContactPhotoUri()!=null)
                    viewHolder.addedPersonProfile.setImageURI(Uri.parse(addedItems.get(position).getPinContactPhotoUri()));
                else{
                    viewHolder.addedPersonProfile.setImageResource(R.mipmap.profile);
                }
            }

            viewHolder.addedPersonProfile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickItem(addedItems.get(position));
                }
            });


//            //쉐어캠 친구 프로필
//            if (addedItems.get(position)) {
//
//                if(addedItems.get(position).friendProfileBytes!=null)
//                {
//                    Bitmap bmp = BitmapFactory.decodeByteArray(addedItems.get(position).friendProfileBytes,0,addedItems.get(position).friendProfileBytes.length);
//                    viewHolder.addedPersonProfile.setImageBitmap(bmp);
//                }
////                    Bitmap bmp = BitmapFactory.decodeByteArray(addedItems.get(position).friendProfile, 0, addedItems.get(position).friendProfile.length);
////                    viewHolder.addedPersonProfile.setImageBitmap(bmp);
//// Set the Bitmap data to the ImageView
//
////                    Picasso.with(getActivity()).load(addedItems.get(position).contactProfile).into(viewHolder.addedPersonProfile);
//            }
//            else if (addedItems.get(position).MODE == IndividualItem.CONTACT && addedItems.get(position).contactProfile!=null)
//                viewHolder.addedPersonProfile.setImageURI(Uri.parse(addedItems.get(position).contactProfile));
//            else{
//                viewHolder.addedPersonProfile.setImageResource(R.mipmap.profile);
//            }




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
        public static final int contactPosition=1;
        public static final int friendPosition=0;
        private LayoutInflater inflater = null;

        List<Contact> contactItems;
        List<Contact> friendItems;


        GroupViewHolder groupViewHolder;
        ChildViewHolder childViewHolder;


        public IndividualAdapter(Context context, List<Contact> contactItems, List<Contact> friendItems)
        {
            this.inflater = LayoutInflater.from(context);
            this.contactItems=contactItems;
            this.friendItems=friendItems;
        }


        public void setItems(List<Contact> contactItems, List<Contact> friendItems)
        {
            this.contactItems=contactItems;
            this.friendItems=friendItems;
        }
//        private List<Contact> getIndividualItem(int position)
//        {
//            switch (position)
//            {
//                case contactPosition:
//                    return contactItems;
//                case friendPosition:
//                    return friendItems;
//                default:
//                    return friendItems;
//            }
//        }


        @Override
        public int getGroupCount() {
            return GROUP_NUM;
        }

        @Override
        public int getChildrenCount(int groupPosition) {
//            Log.d("jyr","group count"+groupPosition);
            switch (groupPosition)
            {
                case friendPosition:
                    return friendItems.size();
                default:
                case contactPosition:
                    return contactItems.size();
            }
        }

        @Override
        public Object getGroup(int groupPosition) {
            return groupPosition;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            switch (groupPosition)
            {
                case friendPosition:
                    return friendItems.get(childPosition);
                default:
                case contactPosition:
                    return contactItems.get(childPosition);
            }

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
                groupViewHolder.groupText.setText(getString(R.string.existing_sharecam_friend)+"("+friendItems.size()+")");
            else if(groupPosition==contactPosition)
                groupViewHolder.groupText.setText(getString(R.string.not_exisiting_sharecam_friend)+"("+contactItems.size()+")");

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

            switch (groupPosition)
            {
                case friendPosition:
                    final Contact friendContact = friendItems.get(childPosition);
                    childViewHolder.personNameTxt.setText(friendContact.getPinContactName());
                    if(friendItems.get(childPosition).friendThumProfileBytes!=null)
                    {
                        Bitmap bmp = BitmapFactory.decodeByteArray(friendContact.friendThumProfileBytes,0,friendContact.friendThumProfileBytes.length);
                        childViewHolder.personProfileImg.setImageBitmap(bmp);
                    }
                    else{
                        childViewHolder.personProfileImg.setImageResource(R.mipmap.profile);
                    }
                    childViewHolder.addPersonBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickItem(friendContact);
                        }
                    });

                    //프로필 클릭한 경우
                    childViewHolder.personProfileImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyDialogBuilder.showProfileDialog(getActivity(), getFragmentManager(), friendContact);
                        }
                    });

                    break;
                case contactPosition:
                    final Contact contact = contactItems.get(childPosition);
                    childViewHolder.personNameTxt.setText(contact.getPinContactName());
                    if(contact.getPinContactPhotoUri()!=null) {
//                        Log.d(TAG,"contact image "+contact.getPhone());
                        childViewHolder.personProfileImg.setImageURI(Uri.parse(contact.getPinContactPhotoUri()));
                    }
                    else {
                        childViewHolder.personProfileImg.setImageResource(R.mipmap.profile);
//                        Log.d(TAG,"image no");
                    }
                    childViewHolder.addPersonBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            clickItem(contact);
                        }
                    });
                    //프로필 클릭한 경우
                    childViewHolder.personProfileImg.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            MyDialogBuilder.showProfileDialog(getActivity(), getFragmentManager(), contact);
                        }
                    });


                    break;
            }





////            Log.d(TAG,"before before set profie");
//            childViewHolder.individualLayout.setVisibility(View.VISIBLE);
////            if(individualItem.isFriend)
////                childViewHolder.personNameTxt.setText(individualItem.personName);
////            else
//            childViewHolder.personNameTxt.setText(individualItem.personName);
////            if(individualItem.contactProfile !=null) {
//
//
//            childViewHolder.personProfileImg.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    MyDialogBuilder.showProfileDialog(getActivity(),getFragmentManager(),individualItem);
//                }
//            });
//
//            //쉐어캠 친구인 경우
//            if (individualItem.MODE == IndividualItem.FRIEND)
//            {
//                Log.d(TAG,"before set profie");
//                if(individualItem.friendThumProfileBytes!=null)
//                {
//                    Bitmap bmp = BitmapFactory.decodeByteArray(individualItem.friendThumProfileBytes,0,individualItem.friendThumProfileBytes.length);
//                    childViewHolder.personProfileImg.setImageBitmap(bmp);
//                }
//                else{
//                    childViewHolder.personProfileImg.setImageResource(R.mipmap.profile);
//                }
//
//
////                individualItem.friendProfileFile.getDataInBackground(new GetDataCallback() {
////                    @Override
////                    public void done(byte[] bytes, ParseException e) {
////                        Log.d(TAG, "set profie");
////                        Bitmap bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
////                        childViewHolder.personProfileImg.setImageBitmap(bmp);
//////                        notifyDataSetChanged();
////                    }
////                });
////                    Picasso.with(getActivity()).load(individualItem.contactProfile).into(childViewHolder.personProfileImg);
//            }
//
//
//            //연락처인 경우
//            else if (individualItem.MODE == IndividualItem.CONTACT && individualItem.contactProfile!=null)
//                childViewHolder.personProfileImg.setImageURI(Uri.parse(individualItem.contactProfile));
//            else
//                childViewHolder.personProfileImg.setImageResource(R.mipmap.profile);
//
////
////            }
////            else
////                childViewHolder.personProfileImg.setImageResource(R.mipmap.profile);
//
//


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

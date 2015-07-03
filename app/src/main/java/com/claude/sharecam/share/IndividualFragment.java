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
import com.claude.sharecam.loader.PersonLoader;
import com.claude.sharecam.view.ImageViewRecyclable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class IndividualFragment extends Fragment{

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


        searchPersonET= (EditText) root.findViewById(R.id.searchPersonET);
        apRecycleView=(RecyclerView)root.findViewById(R.id.apRecycleView);
        individualListview=(ExpandableListView)root.findViewById(R.id.individualListview);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        apRecycleView.setLayoutManager(linearLayoutManager);
        apAdapter=new AddedPersonAdapter(((ShareActivity)getActivity()).addedItems);
        apRecycleView.setAdapter(apAdapter);




        //연락처, 쉐어캠 친구 데이터를 아직 불러오지 않은 경우
        if(((ShareActivity)getActivity()).contactItems ==null ||  ((ShareActivity)getActivity()).friendItems==null) {
            Log.d("jyr", "load data");

            //개인, 쉐어캠 친구 데이터들 불러옴
            getLoaderManager().initLoader(Constants.PERSON_LOADER, null, new LoaderManager.LoaderCallbacks<IndividualItemList>() {
                @Override
                public Loader<IndividualItemList> onCreateLoader(int id, Bundle args) {

                    return new PersonLoader(getActivity());
                }

                @Override
                public void onLoadFinished(Loader<IndividualItemList> loader, IndividualItemList data) {

                    Log.d("jyr", "person item list on load finished");
                    if(((ShareActivity)getActivity()).contactItems ==null ||  ((ShareActivity)getActivity()).friendItems==null)
                    {


                        //불러온 데이터 저장
                        ((ShareActivity) getActivity()).contactItems = data.contactItems;
                        ((ShareActivity) getActivity()).friendItems = data.friendItems;
//                        setAdapter(getArguments().getInt(MODE));

                        //추가된 데이터들 설정
                        ((ShareActivity) getActivity()).setAddedItems(data.addedItems);
                        refreshItems();

                        setIndividualAdapter();


                    }
                }

                @Override
                public void onLoaderReset(Loader<IndividualItemList> loader) {

                }
            });
        }
        else {
            setIndividualAdapter();
        }

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

    //연락처, 쉐어캠 친구 리스트 설정
    private void  setIndividualAdapter()
    {
        individualAdapter=new IndividualAdapter(getActivity(),((ShareActivity)getActivity()).contactItems,((ShareActivity)getActivity()).friendItems);

        individualListview.setAdapter(individualAdapter);

        //모든 group expand해줌
        for(int i=0; i<IndividualAdapter.GROUP_NUM; i++)
        {
            individualListview.expandGroup(i);
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
        if(individualItem.MODE== IndividualItem.CONTACT && individualItem.isFriend)
        {
            addedIndividualItem =((ShareActivity)getActivity()).friendItems.get(individualItem.friendIndex);
        }
        else
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
        ((ShareActivity)getActivity()).addedItems.add(individualItem);
        apAdapter.notifyDataSetChanged();
//        apAdapter.notifyDataSetChanged();
    }

    public void removeItem(IndividualItem individualItem)
    {
        Log.d("jyr","removeItem+"+ individualItem.personName);
//        int removePosition=((ShareActivity) getActivity()).addedItems.size()-1;
        individualItem.added=false;
        ((ShareActivity)getActivity()).addedItems.remove(individualItem);
        Log.d("jyr","addedItem size= "+((ShareActivity)getActivity()).addedItems.size());
        apAdapter.notifyDataSetChanged();
        if(((ShareActivity)getActivity()).addedItems.size()==0)
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
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // - get data from your itemsData at this position
            // - replace the contents of the view with that itemsData

            viewHolder.addedPersonName.setText(addedItems.get(position).personName);

            if(addedItems.get(position).contactProfile !=null) {

                //쉐어캠 친구 프로필
                if (addedItems.get(position).MODE == IndividualItem.FRIEND || addedItems.get(position).isFriend) {

                    Bitmap bmp = BitmapFactory.decodeByteArray(addedItems.get(position).friendProfile, 0, addedItems.get(position).friendProfile.length);
                    viewHolder.addedPersonProfile.setImageBitmap(bmp);
// Set the Bitmap data to the ImageView

//                    Picasso.with(getActivity()).load(addedItems.get(position).contactProfile).into(viewHolder.addedPersonProfile);
                }
                else if (addedItems.get(position).MODE == IndividualItem.CONTACT)
                    viewHolder.addedPersonProfile.setImageURI(Uri.parse(addedItems.get(position).contactProfile));
            }
            else
                viewHolder.addedPersonProfile.setImageResource(R.mipmap.profile);

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

                childViewHolder.individualLayout.setVisibility(View.VISIBLE);
                if(individualItem.isFriend)
                    childViewHolder.personNameTxt.setText(individualItem.personName);
                else
                    childViewHolder.personNameTxt.setText(individualItem.personName);
                if(individualItem.contactProfile !=null) {

                    //쉐어캠 친구인 경우
                    if (individualItem.MODE == IndividualItem.FRIEND || individualItem.isFriend)
                        Picasso.with(getActivity()).load(individualItem.contactProfile).into(childViewHolder.personProfileImg);
                        //연락처인 경우
                    else if (individualItem.MODE == IndividualItem.CONTACT)
                        childViewHolder.personProfileImg.setImageURI(Uri.parse(individualItem.contactProfile));

                }
                else
                    childViewHolder.personProfileImg.setImageResource(R.mipmap.profile);


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


package com.claude.sharecam.share;

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
import android.widget.ImageView;
import android.widget.TextView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.loader.PersonLoader;
import com.claude.sharecam.view.ImageViewRecyclable;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class PersonListFragment extends Fragment {


    public static final String MODE = "mode";
    public static final int RECOMMEND = 0;
    public static final int CONTACT = 1;
    public static final int SHARECAM = 2;

    RecyclerView personRecyclerView;
    PersonAdapter personAdapter;
//    ArrayList<PersonItem> personItems;
//    ArrayList<PersonItem> friendItems;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_personlist, container, false);


        personRecyclerView = (RecyclerView) root.findViewById(R.id.personRecyclerView);



        personRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//        personRecyclerView.setAdapter(personAdapter);




        if((getArguments().getInt(MODE)==CONTACT && ((ShareActivity)getActivity()).contactItems ==null ||  ((ShareActivity)getActivity()).friendItems==null)) {
            Log.d("jyr","load data");

            //개인, 쉐어캠 친구 데이터들 불러옴
            getLoaderManager().initLoader(Constants.PERSON_LOADER, null, new LoaderManager.LoaderCallbacks<IndividualItemList>() {
                @Override
                public Loader<IndividualItemList> onCreateLoader(int id, Bundle args) {
                    Log.d("jyr", "on create load from " + getArguments().getInt(MODE));
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
                    setAdapter(getArguments().getInt(MODE));

                    //추가된 데이터들 설정
                    ((ShareActivity) getActivity()).setAddedItems(data.addedItems);
                    ((IndividualFragment) getParentFragment()).refreshItems();
                    }
                }

                @Override
                public void onLoaderReset(Loader<IndividualItemList> loader) {

                }
            });
        }
        else{
            Log.d("jyr","not load data");
            ((IndividualFragment) getParentFragment()).refreshItems();
            setAdapter(getArguments().getInt(MODE));

        }






        // Inflate the layout for this fragment
        return root;
    }




    private void setAdapter(int mode) {


//        contactItems =new ArrayList<ContactItem>();

        switch (mode) {
            case RECOMMEND:

                break;
            case CONTACT:
                personAdapter=new PersonAdapter(((ShareActivity)getActivity()).contactItems);

                personRecyclerView.setAdapter(personAdapter);

                break;
            case SHARECAM:

                personAdapter=new PersonAdapter(((ShareActivity)getActivity()).friendItems);
                personRecyclerView.setAdapter(personAdapter);

                break;
        }
    }



    private class PersonAdapter extends RecyclerView.Adapter<PersonAdapter.ViewHolder> {

        ArrayList<IndividualItem> contactItems;

        public PersonAdapter(ArrayList<IndividualItem> contactItems) {
            this.contactItems = contactItems;
        }

        // Create new views (invoked by the layout manager)
        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            // create a new view
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.idividual_item, null);

            // create ViewHolder
            ViewHolder viewHolder = new ViewHolder(itemLayoutView);
            return viewHolder;
        }


        // Replace the contents of a view (invoked by the layout manager)
        @Override
        public void onBindViewHolder(ViewHolder viewHolder, final int position) {

            // - get data from your itemsData at this position
            // - replace the contents of the view with that itemsData

            if(contactItems.get(position).isFriend)
                viewHolder.personNameTxt.setText(contactItems.get(position).personName);
            else
                viewHolder.personNameTxt.setText(contactItems.get(position).personName);
            if(contactItems.get(position).contactProfile !=null) {

                if (contactItems.get(position).MODE == IndividualItem.FRIEND || contactItems.get(position).isFriend)
                    Picasso.with(getActivity()).load(contactItems.get(position).contactProfile).into(viewHolder.personProfileImg);
                else if (contactItems.get(position).MODE == IndividualItem.CONTACT)
                    viewHolder.personProfileImg.setImageURI(Uri.parse(contactItems.get(position).contactProfile));

            }
            else
                viewHolder.personProfileImg.setImageResource(R.mipmap.profile);


            viewHolder.addPersonBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((IndividualFragment)getParentFragment()).clickItem(contactItems.get(position));
                }
            });


        }

        @Override
        public int getItemCount() {
            return contactItems.size();
        }

        // inner class to hold a reference to each item of RecyclerView
        public class ViewHolder extends RecyclerView.ViewHolder {

            public TextView personNameTxt;
            public ImageViewRecyclable personProfileImg;
            public ImageView addPersonBtn;

            public ViewHolder(View itemLayoutView) {

                super(itemLayoutView);
                personNameTxt = (TextView) itemLayoutView.findViewById(R.id.personNameTxt);
                personProfileImg = (ImageViewRecyclable) itemLayoutView.findViewById(R.id.personProfileImg);
                addPersonBtn=(ImageView)itemLayoutView.findViewById(R.id.addPersonBtn);
            }
        }

    }

}

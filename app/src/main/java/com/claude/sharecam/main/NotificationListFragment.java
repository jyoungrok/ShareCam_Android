package com.claude.sharecam.main;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.parse.Notification;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.util.ActionBarUtil;
import com.claude.sharecam.util.ImageManipulate;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;

import java.util.List;


public class NotificationListFragment extends Fragment {

    public static final String TAG= "NotificationListFragment";
    int page;

    List<Notification> arItem;
    RecyclerView recyclerView;
    NotificationAdapter notificationAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root= inflater.inflate(R.layout.fragment_notification_list, container, false);
        recyclerView= (RecyclerView) root.findViewById(R.id.notificationRecylcerView);


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        initActionBar();
        init();
        // Inflate the layout for this fragment
        return root;
    }

    private void initActionBar()
    {
        ActionBarUtil.initActionbar((ActionBarActivity) getActivity(),getString(R.string.title_notification));
        ActionBarUtil.resetActionbarItem_1(getActivity());
        ActionBarUtil.resetActionbarItem_2(getActivity());
    }
    //notification 데이터 로드 후 표시
    private void init()
    {
        page=1;
        nextPages();
        /*
        ParseAPI.findNotificationOrderByDate(page,Constants.NUM_LOAD_NOTIFICATION,new FindCallback<Notification>() {
            @Override
            public void done(List<Notification> list, ParseException e) {
                if(e==null)
                {
                    arItem=list;
                    notificationAdapter=new NotificationAdapter(arItem);
                    recyclerView.setAdapter(notificationAdapter);

                    for(int i=0; i<list.size(); i++)
                    {

                    }
                }
                else
                    ParseAPI.erroHandling(getActivity(),e);
            }
        });*/
    }

    private void nextPages()
    {
        ParseAPI.findNotificationOrderByDate(page,Constants.NUM_LOAD_NOTIFICATION,new FindCallback<Notification>() {
            @Override
            public void done(final List<Notification> list, ParseException e) {
                if(e==null)
                {
                    //첫 페이지인 경우
                    if(page==1)
                    {
                        arItem=list;
                        notificationAdapter=new NotificationAdapter(arItem);
                        recyclerView.setAdapter(notificationAdapter);

                    }
                    page++;


                    //프로필 사진, 받은 사진 서버로 부터 불러온 후 리스트 갱신
                    for(int i=0; i<list.size(); i++) {
                        final int index = i;
                        //프로필 사진 불러오기 (프로필 사진이 있는 경우)
                        if (list.get(index).getSendUser().getThumProfileFile() != null) {
                            list.get(index).getSendUser().getThumProfileFile().getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] bytes, ParseException e) {
                                    if (e == null) {
                                        list.get(index).profileBytes = bytes;
                                        //받은 사진 불러오기
                                        list.get(index).getPicture().getThumImageFile().getDataInBackground(new GetDataCallback() {
                                            @Override
                                            public void done(byte[] bytes, ParseException e) {
                                                if (e == null) {
                                                    list.get(index).imageBytes = bytes;
                                                    notificationAdapter.notifyDataSetChanged();
                                                } else {
                                                    ParseAPI.erroHandling(getActivity(), e);
                                                }
                                            }
                                        });

                                    } else
                                        ParseAPI.erroHandling(getActivity(), e);
                                }
                            });
                        }
                        else{
                            //받은 사진 불러오기
                            list.get(index).getPicture().getThumImageFile().getDataInBackground(new GetDataCallback() {
                                @Override
                                public void done(byte[] bytes, ParseException e) {
                                    if (e == null) {
                                        list.get(index).imageBytes = bytes;
                                        notificationAdapter.notifyDataSetChanged();
                                    } else {
                                        ParseAPI.erroHandling(getActivity(), e);
                                    }
                                }
                            });


                        }




                    }


                }
                else
                    ParseAPI.erroHandling(getActivity(),e);
            }
        });
    }

    public class NotificationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        List<Notification> arItem;

        public NotificationAdapter(List<Notification> arItem)
        {
            this.arItem=arItem;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.received_picture_notification_layout, parent, false);

            ViewHolder viewHolder=new ViewHolder(itemLayoutView);
            return viewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if(arItem.get(position).getIsNew())
            {
                ((ViewHolder)holder).notiLayout.setBackgroundColor(Color.parseColor("#FF0000"));
            }
            else{
                ((ViewHolder)holder).notiLayout.setBackgroundColor(Color.parseColor("#FFFFFF"));
            }
            if(arItem.get(position).profileBytes!=null)
                ((ViewHolder)holder).notiUserProfileImg.setImageBitmap(ImageManipulate.byteArrayToBitmap(arItem.get(position).profileBytes));
            else
                ((ViewHolder)holder).notiUserProfileImg.setImageResource(R.mipmap.profile);
            if(arItem.get(position).imageBytes!=null)
                ((ViewHolder)holder).notiPictureImg.setImageBitmap(ImageManipulate.byteArrayToBitmap(arItem.get(position).imageBytes));
            else
                ((ViewHolder)holder).notiPictureImg.setImageResource(R.mipmap.profile);

            ((ViewHolder) holder).notiContentTxt.setText(arItem.get(position).getContent());
            ((ViewHolder)holder).notiDateTxt.setText(Util.getNotificationDateStr(arItem.get(position).getDate()));


            ((ViewHolder)holder).notiLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    switch(arItem.get(position).getType()) {
                        //사진 받은 경우의 notification
                        case Notification.PICTURE_TYPE:
                            Intent intent = new Intent(getActivity(),
                                    AlbumActivity.class);

//                            intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                            intent.putExtra(AlbumActivity.GO_TO_ALBUM, true);
                            intent.putExtra(AlbumActivity.ALBUM_ID, arItem.get(position).getAlbum().getLocalId());
                            getActivity().startActivity(intent);
                            /*
                            arItem.get(position).setIsNew(false);
                            try {
                                arItem.get(position).pin(ParseAPI.LABEL_NOTIFICATION);
                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            Intent intent=new Intent(getActivity(),AlbumImageViewFragment.class);
                            intent.putExtra(AlbumImageViewFragment.MODE,AlbumImageViewFragment.SINGLE_IMAGE_MODE);
                            intent.putExtra(AlbumImageViewFragment.PICTURE, arItem.get(position).getPicture());
                            Bundle args=new Bundle();
                            args.putInt(AlbumImageViewFragment.MODE, AlbumImageViewFragment.SINGLE_IMAGE_MODE);
                            args.putSerializable(AlbumImageViewFragment.PICTURE, arItem.get(position).getPicture());
                            AlbumImageViewFragment albumImageViewFragment=new AlbumImageViewFragment();
                            albumImageViewFragment.setArguments(args);
                            Util.startFragment(getActivity().getSupportFragmentManager(),R.id.mainContainer,albumImageViewFragment,true,null);
                          */
                            break;
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return arItem.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            RelativeLayout notiLayout;
            ImageView notiUserProfileImg;
            TextView notiContentTxt;
            TextView notiDateTxt;
            ImageView notiPictureImg;

            public ViewHolder(View itemView) {
                super(itemView);

                notiLayout=(RelativeLayout)itemView.findViewById(R.id.notiLayout);
                notiUserProfileImg= (ImageView) itemView.findViewById(R.id.notiUserProfileImg);
                notiContentTxt= (TextView) itemView.findViewById(R.id.notiContentTxt);
                notiDateTxt= (TextView) itemView.findViewById(R.id.notiDateTxt);
                notiPictureImg= (ImageView) itemView.findViewById(R.id.notiPictureImg);
            }
        }
    }

}

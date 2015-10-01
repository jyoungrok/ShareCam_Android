package com.claude.sharecam.main;

import android.content.Intent;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.parse.Album;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.util.ActionBarUtil;
import com.claude.sharecam.util.ImageManipulate;
import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;

import org.json.JSONException;

import java.util.List;

/**
 * 내가 공유한 앨범들 보기
 */
public class AlbumFragment extends Fragment {

    public static final String TAG="AlbumFragment";
    RecyclerView AlbumRecyclerView;
    GridLayoutManager gridLayoutManager;
    AlbumAdapter albumAdapter;

    /**
     * bundle string
     */
    public static final String ALBUM_TYPE="albumType";
    public static final String GO_TO_ALBUM="goToAlbum";
    public static final String ALBUM_ID="albumId";

    /**
     * bundle value
     */
    public static final int SEND_ALBUM_TYPE_VALUE=0;
    public static final int RECEIVE_ALBUM_TYPE_VALUE=1;

    int albumType;

    /**
     * actionbar
     */
    Spinner actionbarSpinner;
    public static final int SEND_ALBUM=0;
    public static final int RECEIVE_ALBUM=1;

    boolean isFirstArgumentCheck=true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_send_album, container, false);

        AlbumRecyclerView =(RecyclerView)root.findViewById(R.id.sendAlbumRecyclerView);
        gridLayoutManager=new GridLayoutManager(getActivity(), Constants.COL_NUM_ALBUM);
        AlbumRecyclerView.setLayoutManager(gridLayoutManager);

        Bundle args=getArguments();
        albumType=args.getInt(ALBUM_TYPE,SEND_ALBUM_TYPE_VALUE);

//        load();

        initActionBar();

        checkGoToAlbum(args.getBoolean(GO_TO_ALBUM, false), args.getString(ALBUM_ID, "-1"));

        // Inflate the layout for this fragment
        return root;
    }

    //앨범으로 가기 요청(notification click시 ) 하였는지 여부 확인
    private void checkGoToAlbum(final boolean goToAlbum,String albumId)
    {
        if(goToAlbum && isFirstArgumentCheck)
        {

            Log.d(TAG,"goToAlbum set");
           ParseAPI.findAlbumByIdInBackground(albumId, new GetCallback<Album>() {
               @Override
               public void done(Album album, ParseException e) {
                   if (e == null) {
                       goToAlbum(album);
                   } else {
                       Log.e(TAG, String.valueOf(e.getCode()));
                       ParseAPI.erroHandling(getActivity(), e);
                   }
               }
           });
        }
        isFirstArgumentCheck=false;
    }

    //앨범 실행
    private void goToAlbum(Album album)
    {
//        ParseAPI.setAlbumIsNew(getActivity(),album);

        Log.d(TAG,"goToAlbum");
        Bundle args=new Bundle();
        args.putSerializable(AlbumImageListFragment.ALBUM, album);
        AlbumImageListFragment albumImageListFragment=new AlbumImageListFragment();
        albumImageListFragment.setArguments(args);
        //TAG - > fragment name + album objectId
        Util.startFragment(getFragmentManager(),R.id.albumContainer,albumImageListFragment,true,AlbumImageListFragment.TAG+album.getObjectId());

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        //액션바 설정
//        initActionBar();
    }

    /**
     * 액션바 설정
     * 1. spinner
     * 2. 설정 버튼
     * 3. notification list button
     */
    private void initActionBar()
    {
        Log.d(TAG, "initActionBar");
        //환경 설정
        ActionBarUtil.setActionbarItem_1((ActionBarActivity) getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Notification 수행 과정을 위해 다른 activity 호출
                Intent intent=new Intent(getActivity(),MainActivity.class);
                intent.putExtra(MainActivity.FRAGMENT_TYPE, MainActivity.ConfigFragment);
                startActivity(intent);
            }
        });

        //notification list
        ActionBarUtil.setActionbarItem_2((ActionBarActivity) getActivity(), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Notification 수행 과정을 위해 다른 activity 호출
                Intent intent=new Intent(getActivity(),MainActivity.class);
                intent.putExtra(MainActivity.FRAGMENT_TYPE,MainActivity.NotificationListFragment);
                startActivity(intent);
//                Util.startFragment(getFragmentManager(), R.id.albumContainer, new NotificationListFragment(), true, null);
            }
        });
        //actionbar 기본 설정
        ActionBarUtil.initActionbar((ActionBarActivity) getActivity());

        ((TextView)getActivity().findViewById(R.id.actionbarTitle)).setVisibility(View.GONE);
        actionbarSpinner= ((Spinner)getActivity().findViewById(R.id.actionbarSpinner));
        actionbarSpinner.setVisibility(View.VISIBLE);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),R.array.album_spinner, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        actionbarSpinner.setAdapter(adapter);
        actionbarSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemSelected " + position);
                switch (position) {
                    case SEND_ALBUM:
                        Util.setSelectedAlbumType(getActivity(), Constants.PREF_SELECTED_SEND_ALBUM);
                        albumType=Constants.PREF_SELECTED_SEND_ALBUM;
                        break;
                    case RECEIVE_ALBUM:
                        Util.setSelectedAlbumType(getActivity(), Constants.PREF_SELECTED_RECEIVED_ALBUM);
                        albumType=Constants.PREF_SELECTED_RECEIVED_ALBUM;
                        break;
                }

                load();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        setAlbumSpinner();

    }

    private void setAlbumSpinner()
    {
        switch(Util.getSelectedAlbumType(getActivity()))
        {
            case Constants.PREF_SELECTED_SEND_ALBUM:
                actionbarSpinner.setSelection(SEND_ALBUM);
                break;
            case Constants.PREF_SELECTED_RECEIVED_ALBUM:
                actionbarSpinner.setSelection(RECEIVE_ALBUM);
                break;
        }
    }


    /**
     * Spinner listener 에서 호출
     */
    private void load()
    {
        Log.d(TAG, "load");


        AlbumRecyclerView.setAdapter(null);
        //각 앨범 별 데이터 로드
        switch (albumType)
        {
            case SEND_ALBUM_TYPE_VALUE:
                setSendAlbum();
                break;
            case RECEIVE_ALBUM_TYPE_VALUE:
                setReceiveAlbum();
                break;
        }
    }

    private void setSendAlbum()
    {
        Album.findPinnedSendAlbumInBackground(new FindCallback<Album>() {
            @Override
            public void done(final List<Album> list, ParseException e) {

                if(e==null) {
                    Log.d(TAG, "set send album " + list.size());

                    Util.initContactItemList(getActivity(), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {

                            for (int j = 0; j < list.size(); j++) {

                                String albumName = "";
                                //앨범 별로 공유한 유저이름 찾기
                                for (int i = 0; i < list.get(j).getPhoneList().length(); i++) {
                                    try {
                                        String tempName = Util.getContactNameByPhone((String) list.get(j).getPhoneList().get(i));

                                        //사용자가 있는 경우 이름 추가
                                        if (tempName != null)
                                            albumName = albumName + tempName;
                                            //없는 경우 번호 추가
                                        else
                                            albumName = albumName + list.get(j).getPhoneList().get(i);

                                    } catch (JSONException e1) {
                                        e1.printStackTrace();
                                    }
                                }

                                list.get(j).albumName = albumName;
                            }

                            //데이터 로드 완료 및 set adapter
                            albumAdapter = new AlbumAdapter(list);
                            AlbumRecyclerView.setAdapter(albumAdapter);

                            //사진 불러옴
                            for (int i = 0; i < list.size(); i++) {
                                final int index = i;
                                list.get(i).getPicture().getThumImageFile().getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] bytes, ParseException e) {
                                        list.get(index).pictureFile = bytes;
                                        albumAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    });
                }
                else
                    ParseAPI.erroHandling(getActivity(),e);
            }
        });
    }

    private void setReceiveAlbum()
    {
        Album.findPinnedReceieAlbumInBackground(new FindCallback<Album>() {
            @Override
            public void done(final List<Album> list, ParseException e) {

                if(e==null) {
                    Log.d(TAG, "set receive album " + list.size());

                    Util.initContactItemList(getActivity(), new Handler() {
                        @Override
                        public void handleMessage(Message msg) {

                            for (int j = 0; j < list.size(); j++) {

                                String albumName = "";

                                String tempName = Util.getContactNameByPhone((String) list.get(j).getSender().getPhone());

                                //사용자가 있는 경우 이름 추가
                                if (tempName != null)
                                    albumName = albumName + tempName;
                                    //없는 경우 번호 추가
                                else
                                    albumName = albumName + list.get(j).getSenderPhone();


                                list.get(j).albumName = albumName;
                            }

                            //데이터 로드 완료 및 set adapter
                            albumAdapter = new AlbumAdapter(list);
                            AlbumRecyclerView.setAdapter(albumAdapter);

                            //사진 불러옴
                            for (int i = 0; i < list.size(); i++) {
                                final int index = i;
                                list.get(i).getPicture().getThumImageFile().getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] bytes, ParseException e) {
                                        list.get(index).pictureFile = bytes;
                                        albumAdapter.notifyDataSetChanged();
                                    }
                                });
                            }
                        }
                    });
                }
                else
                    ParseAPI.erroHandling(getActivity(),e);
            }
        });
    }



    private class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

        List<Album> albumList;


        public AlbumAdapter(List<Album> albumList)
        {
            this.albumList = albumList;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemLayoutView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.album_item, parent, false);
            // create ViewHolder
            ViewHolder pictureViewHolder = new ViewHolder(itemLayoutView);
            return pictureViewHolder;
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            if(albumList.get(position).pictureFile!=null)
                ((ViewHolder)holder).albumImageView.setImageBitmap(ImageManipulate.byteArrayToBitmap(albumList.get(position).pictureFile));

            ((ViewHolder)holder).albumNameText.setText(albumList.get(position).albumName);

            if(albumList.get(position).getIsNew())
                ((ViewHolder)holder).albumNewImg.setVisibility(View.VISIBLE);
            else
                ((ViewHolder)holder).albumNewImg.setVisibility(View.GONE);


            ((ViewHolder)holder).albumImageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(albumType==SEND_ALBUM_TYPE_VALUE)
                    {
                        goToAlbum(albumList.get(position));
//                        Bundle args=new Bundle();
//                        args.putSerializable(AlbumImageListFragment.ALBUM, albumList.get(position));
//                        AlbumImageListFragment albumImageListFragment=new AlbumImageListFragment();
//                        albumImageListFragment.setArguments(args);
//                        //TAG - > fragment name + album objectId
//                        Util.startFragment(getFragmentManager(),R.id.albumContainer,albumImageListFragment,true,AlbumImageListFragment.TAG+albumList.get(position).getObjectId());
                    }
                    else if(albumType==RECEIVE_ALBUM_TYPE_VALUE)
                    {
                        goToAlbum(albumList.get(position));
//                        Bundle args=new Bundle();
//                        args.putSerializable(AlbumImageListFragment.ALBUM, albumList.get(position));
//                        AlbumImageListFragment albumImageListFragment=new AlbumImageListFragment();
//                        albumImageListFragment.setArguments(args);
//                        //TAG - > fragment name + album objectId
//                        Util.startFragment(getFragmentManager(),R.id.albumContainer,albumImageListFragment,true,AlbumImageListFragment.TAG+albumList.get(position).getObjectId());
                    }
                }
            });

        }

        @Override
        public int getItemCount() {
            return albumList.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder {

            ImageView albumImageView;
            TextView albumNameText;
            ImageView albumNewImg;

            public ViewHolder(View itemView) {
                super(itemView);
                albumNewImg=(ImageView)itemView.findViewById(R.id.albumNewImg);
                albumImageView= (ImageView) itemView.findViewById(R.id.albumImageView);
                albumNameText= (TextView) itemView.findViewById(R.id.albumNameText);
            }
        }
    }
}

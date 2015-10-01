package com.claude.sharecam.main;

import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.download.Download;
import com.claude.sharecam.download.DownloadHandler;
import com.claude.sharecam.parse.Album;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.Picture;
import com.claude.sharecam.util.ActionBarUtil;
import com.claude.sharecam.util.ImageManipulate;
import com.claude.sharecam.util.ItemOffsetDecoration;
import com.claude.sharecam.view.ResizableImageView;
import com.claude.sharecam.view.SquareImageView;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;

import org.json.JSONException;

import java.io.Serializable;
import java.util.List;


/**
 *  Fragment 실행 시 TAG - > AlbumImageListFragment + album의 objectId
 */
public class AlbumImageListFragment extends Fragment {

    public static final String  TAG="AlbumImageListFragment";


    /**
     * Intent Extra
     */
    public static final String ALBUM_TYPE="albumType";
    public static final String ALBUM="album";

    /**
     * Intent value
     */
    Album album;
    RecyclerView albumRecyclerView;
    AlbumAdapter albumAdapter;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
    GridLayoutManager gridLayoutManager;

    DrawerLayout albumDrawerLayout;
    RelativeLayout albumDrawer;
    LinearLayout memberLayout;
    LinearLayout shortcutLayout;
    LinearLayout groupConfigLayout;
    LinearLayout allDownLayout;
    LinearLayout leaveAlbumLayout;
    LinearLayout alarmSetLayout;
    LinearLayout autoDownSetLayout;
//    public static final int SORT_BY_SIZE=0;
//    public static final int SORT_BY_DATE=1;

//    int sortType;
    int numImages;
    int currentPage;
    int fetchedImages;
    int fetchedItems;
    boolean loading;

//    PictureListWithDate arItem;
    PictureListWithDate arItem;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root=inflater.inflate(R.layout.fragment_album, container, false);
        albumRecyclerView=(RecyclerView)root.findViewById(R.id.albumRecyclerView);




        numImages=0;
        currentPage=0;
        fetchedImages=0;
        fetchedItems=0;
        loading=false;
//        sortType=Util.getAlubumType(getActivity());

        Bundle args=getArguments();
//        albumType=args.getInt(ALBUM_TYPE);
        album= (Album) args.getSerializable(ALBUM);

        ParseAPI.resetAlbumIsNew(getActivity(), album);



        init();
        //액션바 설정
        initActionBar();
        //NavigationDrawer 설정
        initDrawer();


        try {
            //앨범 내 사진 갯수 가져옴
            ParseAPI.findPinnedAlbumPicturesSizeInBackground(album, new CountCallback() {
                @Override
                public void done(int i, ParseException e) {

                    if (e == null) {
                        try {
                            //처음 이미지들 불러옴
                            nextPictures();
                        } catch (JSONException e1) {
                            e1.printStackTrace();
                        }
                    } else
                        ParseAPI.erroHandling(getActivity(), e);
                }
            });

        } catch (JSONException e) {
            e.printStackTrace();
        }


        // Inflate the layout for this fragment
        return root;
    }

    @Override
    public void onResume()
    {
        super.onResume();
        //drawer 활성화
        albumDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
        //drawer 비 활성화
        albumDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
    }
    private void initDrawer()
    {
        albumDrawerLayout=(DrawerLayout)getActivity().findViewById(R.id.albumDrawerLayout);
        albumDrawer=(RelativeLayout)getActivity().findViewById(R.id.albumDrawer);
        memberLayout=(LinearLayout)getActivity().findViewById(R.id.memberLayout);
        shortcutLayout=(LinearLayout)getActivity().findViewById(R.id.shortcutLayout);
        groupConfigLayout=(LinearLayout)getActivity().findViewById(R.id.groupConfigLayout);
        allDownLayout =(LinearLayout)getActivity().findViewById(R.id.allDownLayout);
        leaveAlbumLayout=(LinearLayout)getActivity().findViewById(R.id.leaveAlbumLayout);

        allDownLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG,"all download");
                Util.showToast(getActivity(),R.string.start_download);
                ParseAPI.findNotDownloadedPicturesInBackground(album,new FindCallback<Picture>() {
                    @Override
                    public void done(List<Picture> list, ParseException e) {
                        if(e==null)
                        {
                            Download.manuallyDownloadPicture(getActivity(), list, new DownloadHandler() {
                                @Override
                                public void manualDownloadDoneHandler(int successNum,int failNum) {
                                    super.manualDownloadDoneHandler(successNum,failNum);
                                    Toast.makeText(getActivity(),getString(R.string.download)+" "+getString(R.string.success)+" "+successNum+" / "+getString(R.string.fail)+" "+failNum,Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void manualDownloadStopHandler(int successNum,int failNum) {
                                    super.manualDownloadStopHandler(successNum,failNum);
                                    Toast.makeText(getActivity(),getString(R.string.download)+" "+getString(R.string.stop)+" "+getString(R.string.success)+" "+successNum+" / "+getString(R.string.fail)+" "+failNum,Toast.LENGTH_SHORT).show();

                                }
                            });
                        }
                        else
                            ParseAPI.erroHandling(getActivity(),e);
                    }
                });

            }
        });


    }


    private void init()
    {
        try {
            //앨범 내 사진 갯수 가져옴
            ParseAPI.findPinnedAlbumPicturesSizeInBackground(album, new CountCallback() {
                @Override
                public void done(int i, ParseException e) {
                    numImages = i;
                    Log.d(TAG, "the number of all of images=" + numImages);
                    try {
                        nextPictures();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            });
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

    /**
     * 액션바 아이템 숨기기
     */
    private void initActionBar()
    {
        ActionBarUtil.resetActionbarItem_1(getActivity());
        ActionBarUtil.resetActionbarItem_2(getActivity());
        ActionBarUtil.initActionbar((ActionBarActivity) getActivity(), album.albumName);

    }


    private void setAdapter(List<Picture> pictureList)
    {
        //사진들간의 space적용
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.album_grid_space);
        albumRecyclerView.addItemDecoration(itemDecoration);

        arItem=new PictureListWithDate(pictureList,Util.getAlubumType(getActivity()));
//        arItem=new ArrayList<Picture>();
        albumAdapter = new AlbumAdapter(arItem,Constants.COL_NUM_SORT_BY_DATE,Constants.COL_NUM_SORT_BY_PICTURE);

        gridLayoutManager=new GridLayoutManager(getActivity(), Constants.COL_NUM_SORT_BY_DATE);
        gridLayoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                return albumAdapter.getSpanSizeSortByDate(position);
            }
        });
        staggeredGridLayoutManager =new StaggeredGridLayoutManager(Constants.COL_NUM_SORT_BY_PICTURE,StaggeredGridLayoutManager.VERTICAL);
        staggeredGridLayoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        setRecycleLayoutManager(Util.getAlubumType(getActivity()));
        albumRecyclerView.setAdapter(albumAdapter);

    }

    //이미지 데이터들 불러옴
    private void nextPictures() throws JSONException {

        Log.d(TAG,"nextPictures");


        //보낸 사진 앨범

        //처음 페이지이거나 받아올 데이터가 남아 있는 경우
        if ((currentPage == 0 || fetchedItems < numImages) && !loading) {
            //로딩 시작 설정
            startLoading();

            ParseAPI.findPinnedAlbumPictruesInBackground(currentPage + 1, album, new FindCallback<Picture>() {
                @Override
                public void done(final List<Picture> list, ParseException e) {
                    Log.d(TAG, "get pictures " + list.size());

                    //첫번째 사진들 불러온 경우
                    if(currentPage==0)
                    {
                        //불러올 사진이 없는 경우
                        if(list.size()==0)
                        {
                            Util.showToast(getActivity(),R.string.all_pictures_exipired);

                        }

                        else
                        setAdapter(list);
                    }
                    else {
                        arItem.addLastItems( list);
//                    arItem.addAll(list);
                        albumAdapter.notifyDataSetChanged();
                    }
                    currentPage++;
                    fetchedItems += list.size();
                    finishLoading(list);

                    //사진 이미지파일들 불러옴
                    for (int i = 0; i < list.size(); i++) {
                        final int index = i;
                        list.get(index).getThumImageFile().getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, ParseException e) {
                                if (e == null) {
                                    list.get(index).setByteFile(bytes);
                                    albumAdapter.notifyDataSetChanged();
                                }
                                else
                                    ParseAPI.erroHandling(getActivity(),e);
                            }
                        });

                    }
                }
            });
        }

    }


    private void setRecycleLayoutManager(final int type){
        switch (type)
        {
            case Constants.PREF_SORT_BY_PICTURE:
                albumRecyclerView.setLayoutManager(staggeredGridLayoutManager);

                break;
            case Constants.PREF_SORT_BY_DATE:
                albumRecyclerView.setLayoutManager(gridLayoutManager);
//                albumRecyclerView.addOnScrollListener(null);

                break;
        }

//        albumRecyclerView.addOnScrollListener(null);
        //다음 이미지 로드 할 때
        albumRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                switch (type) {
                    case Constants.PREF_SORT_BY_PICTURE:
                        int visibleItemCount = staggeredGridLayoutManager.getChildCount();
                        int totalItemCount = staggeredGridLayoutManager.getItemCount();
                        int[] firstVisibleItems = null;
                        firstVisibleItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                        if ((visibleItemCount + firstVisibleItems[0]) >= totalItemCount && totalItemCount>=Constants.NUM_LOAD_PICTURE) {

                            Log.d("tag", "LOAD NEXT ITEM");
                            try {
                                nextPictures();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                    case Constants.PREF_SORT_BY_DATE:
                        int visibleItemCount2 = gridLayoutManager.getChildCount();
                        int totalItemCount2 = gridLayoutManager.getItemCount();
                        int firstVisibleItems2 = gridLayoutManager.findFirstVisibleItemPosition();
                        if ((visibleItemCount2 + firstVisibleItems2) >= totalItemCount2 && totalItemCount2>=Constants.NUM_LOAD_PICTURE) {
                            Log.d("tag", "LOAD NEXT ITEM");
                            try {
                                nextPictures();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                        break;
                }

            }
        });


    }

    //사진 로딩 시작 시 호출
    private void startLoading()
    {
        loading=true;
        Log.d("jyr","startLoading");
    }

    //사진 로딩 종료 시 호출
    private void finishLoading(List<Picture> list)
    {
        Log.d("jyr","finishLoading");
//        arItem.addLastItems(list);
////        arItem.addAll(list);
//        albumAdapter.notifyDataSetChanged();
        loading=false;


    }




    private class AlbumAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{


        public final int HEADER_TYPE=0;//정렬 버튼 표시
        public final int PICTURE_TYPE =1;
        public final int DATE_TYPE=2;//시간표시
        public final int FOOTER_TYPE=3;//progress bar


        int colNumSortByDate;
        int colNumSortByPicture;
        PictureListWithDate arItem;
//        ArrayList<Picture> arItem;

        public void clickImage(int position)
        {
            if (numImages > 0) {
//                Intent intent = new Intent(getActivity(), ImageActivity.class);
//                ArrayList<String> imageUrlList = new ArrayList<String>();
//                for (int i = 0; i < arItem.pictureList.size(); i++) {
//                    imageUrlList.add(arItem.pictureList.get(i).getImageURL());
//                }

                int currentIndex=arItem.sortType==Constants.PREF_SORT_BY_PICTURE?position-1:arItem.getPictureIndexFromPictureWithDateItem(position-1);
                AlbumImageViewFragment albumImageViewFragment=new AlbumImageViewFragment();
                Bundle args=new Bundle();
                args.putSerializable(AlbumImageViewFragment.ALBUM,album);
                args.putSerializable(AlbumImageViewFragment.PICTURE_LIST, (Serializable) arItem.pictureList);
                args.putInt(AlbumImageViewFragment.NUM_PICTURES,numImages);
                args.putInt(AlbumImageViewFragment.CURRENT_PAGE,currentPage);
                args.putInt(AlbumImageViewFragment.CURRENT_INDEX,currentIndex);
                albumImageViewFragment.setArguments(args);

                Util.startFragment(getFragmentManager(),R.id.albumContainer,albumImageViewFragment,true,null);
//                int currentIndex=arItem.type==PictureListWithDate.SORT_BY_PICTURE?position-1:arItem.getPictureIndexFromPictureWithDateItem(position-1);
//                intent.putExtra(ImageActivity.IMAGE_URL_LIST, imageUrlList);
//                intent.putExtra(ImageActivity.NUM_IMAGES, numImages);
//                intent.putExtra(ImageActivity.CURRENT_PAGE, currentPage);
//                intent.putExtra(ImageActivity.CURRENT_INDEX,currentIndex);
////                        intent.putExtra(ImageActivity.NEXT_PAGE,nextPage);
//                startActivity(intent);
            }
        }


        public AlbumAdapter(PictureListWithDate arItem,int colNumSortByDate,int colNumSortByPicture){
            this.arItem=arItem;
            this.colNumSortByDate=colNumSortByDate;
            this.colNumSortByPicture=colNumSortByPicture;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if(viewType==HEADER_TYPE)
            {
                View itemLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.header_album_adapter, parent, false);

                // create ViewHolder
                HeaderViewHolder headerViewHolder = new HeaderViewHolder(itemLayoutView);
                return headerViewHolder;
            }
            else if(viewType== PICTURE_TYPE) {
                // create a new view
                View itemLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.picture_item, parent, false);
                // create ViewHolder
                PictureViewHolder pictureViewHolder = new PictureViewHolder(itemLayoutView);
                return pictureViewHolder;
            }
            else if (viewType == DATE_TYPE) {
                // create a new view
                View itemLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.date_item, parent, false);
                // create ViewHolder
                DateViewHolder dateViewHolder = new DateViewHolder(itemLayoutView);
                return dateViewHolder;
            } else if (viewType == FOOTER_TYPE) {
                View itemLayoutView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.footer_album_adapter, parent, false);
                // create ViewHolder
                FooterViewHolder viewHolder = new FooterViewHolder(itemLayoutView);
                return viewHolder;
            } else
                return null;
        }


        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            switch (getItemViewType(position)) {
                case HEADER_TYPE:



                    setFullSpan(holder);
                    ((HeaderViewHolder)holder).sortByPictureBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.setAlbumType(getActivity(),Constants.PREF_SORT_BY_PICTURE);
                            arItem.sortType=Constants.PREF_SORT_BY_PICTURE;
//                            arItem.type=PictureListWithDate.SORT_BY_PICTURE;
                            setRecycleLayoutManager(Constants.PREF_SORT_BY_PICTURE);
                            notifyDataSetChanged();
                        }
                    });

                    ((HeaderViewHolder)holder).sortByDateBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.setAlbumType(getActivity(),Constants.PREF_SORT_BY_DATE);
                            arItem.sortType=Constants.PREF_SORT_BY_DATE;
//                            arItem.type=PictureListWithDate.SORT_BY_DATE;
                            setRecycleLayoutManager(Constants.PREF_SORT_BY_DATE);
                            notifyDataSetChanged();
                        }
                    });


                    break;
                case DATE_TYPE:

                    //full span 설정
                    setFullSpan(holder);
//                    StaggeredGridLayoutManager.LayoutParams params3 = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
//                    if(params3 == null) {
//                        params3 = new StaggeredGridLayoutManager.LayoutParams(StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT, StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
//                    }
//                    params3.setFullSpan(true);
//                    holder.itemView.setLayoutParams(params3);
                    ((DateViewHolder)holder).dateText.setText(arItem.pictureWithDateList.get(position - 1).date);

                    break;

                case PICTURE_TYPE:


                    Picture picture = null;
                    if(arItem.sortType==Constants.PREF_SORT_BY_PICTURE)
                    {
                        ((PictureViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                        ((PictureViewHolder) holder).imageViewSortyDate.setVisibility(View.GONE);
                        picture=arItem.pictureList.get(position - 1);
                        //이미지 로딩 전
                        if(picture.getByteFile()==null)
                        {
                            ((PictureViewHolder) holder).imageView.setRatio(picture.getHeight()/picture.getWidth());
                        }
                        //이미지 로딩 후
                        else {
                            ((PictureViewHolder) holder).imageView.setImageBitmap(ImageManipulate.byteArrayToBitmap(picture.getByteFile()));


                        }


                        ((PictureViewHolder)holder).imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickImage(position);
                            }
                        });
                    }
                    else if(arItem.sortType==Constants.PREF_SORT_BY_DATE){
                        ((PictureViewHolder) holder).imageView.setVisibility(View.GONE);
                        ((PictureViewHolder) holder).imageViewSortyDate.setVisibility(View.VISIBLE);
                        picture=arItem.pictureWithDateList.get(position-1).getPicture();
                        //이미지 로딩 전
                        if(picture.getByteFile()==null)
                        {
                            ((PictureViewHolder) holder).imageViewSortyDate.setImageResource(R.mipmap.ic_action_accept);
                        }
                        //이미지 로딩 후
                        else {
                            ((PictureViewHolder) holder).imageViewSortyDate.setImageBitmap(ImageManipulate.byteArrayToBitmap(picture.getByteFile()));
                        }



                        ((PictureViewHolder)holder).imageViewSortyDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickImage(position);
                            }
                        });
                    }


                    final Picture finalPicture=picture;

                    /**
                     * 저장 안된 경우 버튼 표시 listener 등록
                     */
                    //저장 버튼 표시 안함
                    if(picture.getSaved())
                    {
                        ((PictureViewHolder) holder).saveBtn.setVisibility(View.INVISIBLE);

                    }
                    else
                    {
                        ((PictureViewHolder) holder).saveBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ((AlbumActivity)getActivity()).setProgressLayout(Constants.PROGRESS_AND_LAYOUT_VISIBLE);
                                finalPicture.downloadPictureInBackground(getActivity(), new Handler() {
                                    @Override
                                    public void handleMessage(Message msg) {
                                        super.handleMessage(msg);
                                        //사진 저장 성공
                                        if (msg.what == Picture.SUCCESS_DOWNLOAD) {
                                            Util.showToast(getActivity(), R.string.save_picture_success);
                                            finalPicture.setSaved(true);
                                            notifyDataSetChanged();
                                        }
                                        //사진 저장 실패
                                        else if (msg.what == Picture.FAIL_DOWNLOAD) {
                                            Util.showToast(getActivity(), R.string.save_picture_fail);
                                        }

                                        ((AlbumActivity) getActivity()).setProgressLayout(Constants.PROGRESS_INVISIBLE);
                                    }
                                });
                            }
                        });
                        ((PictureViewHolder) holder).saveBtn.setVisibility(View.VISIBLE);
                    }
                    break;
                case FOOTER_TYPE:

                    Log.d(TAG,"fetchedItems="+fetchedItems+" , numImages="+numImages);
                    //이미지를 모두 불러온 경우
                    if(fetchedItems==numImages && numImages!=0)
                    {
                        holder.itemView.setVisibility(View.GONE);
                    }
                    else
                    {
                        holder.itemView.setVisibility(View.VISIBLE);
                        //full span 설정
                        setFullSpan(holder);
                    }


                    break;

            }


        }




        public int getSpanSizeSortByDate(int position)
        {
            switch (getItemViewType(position))
            {
                case HEADER_TYPE:
                case FOOTER_TYPE:
                case DATE_TYPE:
                    return Constants.COL_NUM_SORT_BY_DATE;
                default:
                case PICTURE_TYPE:
                    return 1;
            }
        }
        @Override
        public int getItemViewType(int position) {
            if(arItem.sortType==Constants.PREF_SORT_BY_PICTURE) {
                if (position == 0)
                    return HEADER_TYPE;
                else if (position == arItem.pictureList.size() + 1)
                    return FOOTER_TYPE;
                else
                    return PICTURE_TYPE;
            }
            else if(arItem.sortType==Constants.PREF_SORT_BY_DATE)
            {
                if (position == 0)
                    return HEADER_TYPE;
                else if (position == arItem.pictureWithDateList.size() + 1)
                    return FOOTER_TYPE;
                else if(arItem.pictureWithDateList.get(position-1).getType()==PictureListWithDate.PictureItem.PICTURE_TYPE)
                    return PICTURE_TYPE;
                else if(arItem.pictureWithDateList.get(position-1).getType()==PictureListWithDate.PictureItem.DATE_TYPE)
                    return DATE_TYPE;
            }

            return position;
        }

        @Override
        public int getItemCount() {
            if(arItem.sortType==Constants.PREF_SORT_BY_PICTURE)
                return arItem.pictureList.size()+2;
            else if(arItem.sortType==Constants.PREF_SORT_BY_DATE)
                return arItem.pictureWithDateList.size()+2;

            return arItem.pictureList.size()+2;

        }

        private void setFullSpan(RecyclerView.ViewHolder holder)
        {
            if(arItem.sortType==Constants.PREF_SORT_BY_PICTURE)
            {
                //full span 설정
                StaggeredGridLayoutManager.LayoutParams params = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
                if(params == null) {
                    params = new StaggeredGridLayoutManager.LayoutParams(StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT, StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
                }
                params.setFullSpan(true);
                holder.itemView.setLayoutParams(params);
            }
            else
            {
//                GridLayoutManager.LayoutParams params=(GridLayoutManager.LayoutParams)holder.itemView.getLayoutParams();
//                if(params==null)
//                {
//                    params=new GridLayoutManager.LayoutParams(GridLayoutManager.LayoutParams.MATCH_PARENT,GridLayoutManager.LayoutParams.MATCH_PARENT);
//                }

            }
        }

        public class HeaderViewHolder extends RecyclerView.ViewHolder {

            public ImageView sortByDateBtn;
            public ImageView sortByPictureBtn;


            public HeaderViewHolder(View itemView) {
                super(itemView);

                sortByDateBtn=(ImageView)itemView.findViewById(R.id.sortByDateBtn);
                sortByPictureBtn=(ImageView)itemView.findViewById(R.id.sortByPictureBtn);

            }
        }
        public class DateViewHolder extends RecyclerView.ViewHolder {

            public TextView dateText;

            public DateViewHolder(View itemView) {
                super(itemView);
                dateText=(TextView)itemView.findViewById(R.id.dateText);
            }
        }

        public class PictureViewHolder extends RecyclerView.ViewHolder {

            public SquareImageView imageViewSortyDate;
            public ResizableImageView imageView;
            public ImageView saveBtn;

            public PictureViewHolder(View itemView) {
                super(itemView);
                imageViewSortyDate=(SquareImageView)itemView.findViewById(R.id.imageViewSortyDate);
                imageView=(ResizableImageView)itemView.findViewById(R.id.imageView);
                saveBtn=(ImageView)itemView.findViewById(R.id.saveBtn);
            }
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }
    }


}

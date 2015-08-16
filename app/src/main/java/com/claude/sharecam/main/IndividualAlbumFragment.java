package com.claude.sharecam.main;

import android.content.Intent;
import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.Picture;
import com.claude.sharecam.share.ImageActivity;
import com.claude.sharecam.util.ImageManipulate;
import com.claude.sharecam.util.ItemOffsetDecoration;
import com.claude.sharecam.view.ResizableImageView;
import com.claude.sharecam.view.SquareImageView;
import com.parse.CountCallback;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;

import java.util.ArrayList;
import java.util.List;

public class IndividualAlbumFragment extends Fragment {

    public static final String  TAG="IndividualAlbumFragment";
//    ArrayList<Picture> arItem;
    //    ArrayList<IndividualImageItem> arItem;
    RecyclerView albumRecyclerView;
    AlbumAdapter albumAdapter;
//    GridLayoutManager gridLayoutManager;
    StaggeredGridLayoutManager staggeredGridLayoutManager;
        GridLayoutManager gridLayoutManager;

    int numImages;
    int currentPage;
    int fetchedImages;
    int fetchedItems;
    boolean loading;

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

        //test
//        arItem=new ArrayList<IndividualImageItem>();
//        arItem.add(new IndividualImageItem("http://img.tenasia.hankyung.com/webwp_kr/wp-content/uploads/2015/01/2015011517284126778.jpg"));
//        arItem.add(new IndividualImageItem("http://img.tenasia.hankyung.com/webwp_kr/wp-content/uploads/2015/01/2015011517284126778.jpg"));


        //사진들간의 space적용
        ItemOffsetDecoration itemDecoration = new ItemOffsetDecoration(getActivity(), R.dimen.album_grid_space);
        albumRecyclerView.addItemDecoration(itemDecoration);

        arItem=new PictureListWithDate(new ArrayList<Picture>(), Util.getAlubumType(getActivity()));
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





//        albumRecyclerView.setRecycleLayoutManager(new GridLayoutManager(getActivity(),3));


        //처음 이미지들 불러옴
        nextImages();







        // Inflate the layout for this fragment
        return root;
    }

    //이미지 데이터들 불러옴
    private void nextImages(){

        Log.d(TAG,"nextImages");
        //처음 페이지이거나 받아올 데이터가 남아 있는 경우
        if((currentPage==0 || fetchedItems<numImages) && !loading) {


            startLoading();
            //처음 이미지들 불러오고 총 이미지의 갯수 불러옴
            ParseAPI.getPicturesByMe(currentPage+1,Constants.NUM_LOAD_PICTURE, new FindCallback<Picture>() {
                @Override
                public void done(final List<Picture> list, ParseException e) {

                    Log.d(TAG,"get pictures "+list.size());

                    arItem.addLastItems(list);
                    albumAdapter.notifyDataSetChanged();
//                    SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
//
//                    Log.d("jyr",format.format(list.get(0).getCreatedAt()));
                    //첫번째로 페이지 불러오는 경우 촘 아이템 갯수 가져옴
                    if (currentPage == 0) {
                        ParseAPI.getPicturesCountByMe(new CountCallback() {
                            @Override
                            public void done(int i, ParseException e) {
                                numImages = i;
                                Log.d(TAG, "the number of all of images=" + numImages);
                                albumAdapter.notifyDataSetChanged();
                            }
                        });
                    }
                    currentPage++;
                    fetchedItems += list.size();
                    finishLoading(list);

                    //사진 이미지파일들 불러옴
                    for (int i = 0; i < list.size(); i++) {
                        final int index=i;
                        list.get(index).getThumImageFile().getDataInBackground(new GetDataCallback() {
                            @Override
                            public void done(byte[] bytes, ParseException e) {
                                if(e==null)
                                {
                                    list.get(index).setByteFile(bytes);
                                    albumAdapter.notifyDataSetChanged();
                                }
                            }
                        });
//                        Picasso.with(getActivity()).load(list.get(i).getThumImageURL()).fetch(new Callback() {
//                            @Override
//                            public void onSuccess() {
//                                fetchedImages++;
//                                Log.d("jyr","success fetchedImages"+fetchedImages);
//                                if (fetchedItems <=fetchedImages) {
//                                    finishLoading(list);
//                                }
//                            }
//
//                            @Override
//                            public void onError() {
//                                fetchedImages++;
//                                Log.d("jyr","error fetchedImages"+fetchedImages);
//                                if (fetchedItems <= fetchedImages) {
//                                    finishLoading(list);
//                                }
//                            }
//                        });
                    }


                }
            });
        }
    }

    private void setRecycleLayoutManager(final int type){
        switch (type)
        {
            case PictureListWithDate.SORT_BY_PICTURE:
                albumRecyclerView.setLayoutManager(staggeredGridLayoutManager);

                break;
            case PictureListWithDate.SORT_BY_DATE:
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
                    case PictureListWithDate.SORT_BY_PICTURE:
                        int visibleItemCount = staggeredGridLayoutManager.getChildCount();
                        int totalItemCount = staggeredGridLayoutManager.getItemCount();
                        int[] firstVisibleItems = null;
                        firstVisibleItems = staggeredGridLayoutManager.findFirstVisibleItemPositions(firstVisibleItems);
                        if ((visibleItemCount + firstVisibleItems[0]) >= totalItemCount && totalItemCount>=Constants.NUM_LOAD_PICTURE) {

                            Log.d("tag", "LOAD NEXT ITEM");
                            nextImages();
                        }
                        break;
                    case PictureListWithDate.SORT_BY_DATE:
                        int visibleItemCount2 = gridLayoutManager.getChildCount();
                        int totalItemCount2 = gridLayoutManager.getItemCount();
                        int firstVisibleItems2 = gridLayoutManager.findFirstVisibleItemPosition();
                        if ((visibleItemCount2 + firstVisibleItems2) >= totalItemCount2 && totalItemCount2>=Constants.NUM_LOAD_PICTURE) {
                            Log.d("tag", "LOAD NEXT ITEM");
                            nextImages();
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

//        ArrayList<IndividualImageItem> arItem;

        public final int HEADER_TYPE=0;//정렬 버튼 표시
        public final int PICTURE_TYPE =1;
        public final int DATE_TYPE=2;//시간표시
        public final int FOOTER_TYPE=3;//progress bar


        int colNumSortByDate;
        int colNumSortByPicture;
        PictureListWithDate arItem;

        public void clickImage(int position)
        {
            if (numImages > 0) {
                Intent intent = new Intent(getActivity(), ImageActivity.class);
                ArrayList<String> imageUrlList = new ArrayList<String>();
                for (int i = 0; i < arItem.pictureList.size(); i++) {
                    imageUrlList.add(arItem.pictureList.get(i).getImageURL());
                }

                int currentIndex=arItem.type==PictureListWithDate.SORT_BY_PICTURE?position-1:arItem.getPictureIndexFromPictureWithDateItem(position-1);
                intent.putExtra(ImageActivity.IMAGE_URL_LIST, imageUrlList);
                intent.putExtra(ImageActivity.NUM_IMAGES, numImages);
                intent.putExtra(ImageActivity.CURRENT_PAGE, currentPage);
                intent.putExtra(ImageActivity.CURRENT_INDEX,currentIndex);
//                        intent.putExtra(ImageActivity.NEXT_PAGE,nextPage);
                startActivity(intent);
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
                            Util.setAlbumType(getActivity(),PictureListWithDate.SORT_BY_PICTURE);
                            arItem.type=PictureListWithDate.SORT_BY_PICTURE;
                            setRecycleLayoutManager(PictureListWithDate.SORT_BY_PICTURE);
                            notifyDataSetChanged();
                        }
                    });

                    ((HeaderViewHolder)holder).sortByDateBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Util.setAlbumType(getActivity(),PictureListWithDate.SORT_BY_DATE);
                            arItem.type=PictureListWithDate.SORT_BY_DATE;
                            setRecycleLayoutManager(PictureListWithDate.SORT_BY_DATE);
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
                    ((DateViewHolder)holder).dateText.setText(arItem.pictureWithDateList.get(position-1).date);

                    break;

                case PICTURE_TYPE:


                    Picture picture;
                    if(arItem.type==PictureListWithDate.SORT_BY_PICTURE)
                    {
                        ((PictureViewHolder) holder).imageView.setVisibility(View.VISIBLE);
                        ((PictureViewHolder) holder).imageViewSortyDate.setVisibility(View.GONE);
                        picture=arItem.pictureList.get(position-1);
                        //이미지 로딩 전
                        if(picture.getByteFile()==null)
                        {
                            ((PictureViewHolder) holder).imageView.setRatio(picture.getHeight()/picture.getWidth());
                        }
                        //이미지 로딩 후
                        else {
                            ((PictureViewHolder) holder).imageView.setImageBitmap(ImageManipulate.byteArrayToBitmap(picture.getByteFile()));
                        }
//                        Picasso.with(getActivity()).load(picture.getThumImageURL()).into(((PictureViewHolder) holder).imageView, new Callback() {
//                            @Override
//                            public void onSuccess() {
//                            }
//
//                            @Override
//                            public void onError() {
//
//                            }
//                        });

                        ((PictureViewHolder)holder).imageView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickImage(position);
                            }
                        });
                    }
                    else{
                        ((PictureViewHolder) holder).imageView.setVisibility(View.GONE);
                        ((PictureViewHolder) holder).imageViewSortyDate.setVisibility(View.VISIBLE);
                        picture=arItem.pictureWithDateList.get(position-1).picture;
                        //이미지 로딩 전
                        if(picture.getByteFile()==null)
                        {
                            ((PictureViewHolder) holder).imageView.setRatio(picture.getHeight()/picture.getWidth());
                        }
                        //이미지 로딩 후
                        else {
                            ((PictureViewHolder) holder).imageView.setImageBitmap(ImageManipulate.byteArrayToBitmap(picture.getByteFile()));
                        }
//                        Picasso.with(getActivity()).load(picture.getThumImageURL()).into(((PictureViewHolder) holder).imageViewSortyDate, new Callback() {
//                            @Override
//                            public void onSuccess() {
//                            }
//
//                            @Override
//                            public void onError() {
//
//                            }
//                        });

                        ((PictureViewHolder)holder).imageViewSortyDate.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                clickImage(position);
                            }
                        });
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
//                        StaggeredGridLayoutManager.LayoutParams params2 = (StaggeredGridLayoutManager.LayoutParams) holder.itemView.getLayoutParams();
//                        if(params2 == null) {
//                            params2 = new StaggeredGridLayoutManager.LayoutParams(StaggeredGridLayoutManager.LayoutParams.MATCH_PARENT, StaggeredGridLayoutManager.LayoutParams.WRAP_CONTENT);
//                        }
//                        params2.setFullSpan(true);
//                        holder.itemView.setLayoutParams(params2);
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
            if(arItem.type==PictureListWithDate.SORT_BY_PICTURE) {
                if (position == 0)
                    return HEADER_TYPE;
                else if (position == arItem.pictureList.size() + 1)
                    return FOOTER_TYPE;
                else
                    return PICTURE_TYPE;
            }
            else if(arItem.type==PictureListWithDate.SORT_BY_DATE)
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
            if(arItem.type==PictureListWithDate.SORT_BY_PICTURE)
                 return arItem.pictureList.size()+2;
            else if(arItem.type==PictureListWithDate.SORT_BY_DATE)
                return arItem.pictureWithDateList.size()+2;

            return arItem.pictureList.size()+2;

        }

        private void setFullSpan(RecyclerView.ViewHolder holder)
        {
            if(arItem.type==PictureListWithDate.SORT_BY_PICTURE)
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

            public PictureViewHolder(View itemView) {
                super(itemView);
                imageViewSortyDate=(SquareImageView)itemView.findViewById(R.id.imageViewSortyDate);
                imageView=(ResizableImageView)itemView.findViewById(R.id.imageView);
            }
        }

        public class FooterViewHolder extends RecyclerView.ViewHolder {

            public FooterViewHolder(View itemView) {
                super(itemView);
            }
        }
    }


}

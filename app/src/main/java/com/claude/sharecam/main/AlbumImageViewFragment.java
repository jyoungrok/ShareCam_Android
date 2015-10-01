package com.claude.sharecam.main;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.parse.Album;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.Picture;
import com.claude.sharecam.util.ImageManipulate;
import com.claude.sharecam.view.ResizableImageView;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

import uk.co.senab.photoview.PhotoViewAttacher;

public class AlbumImageViewFragment extends Fragment {

    public static final String TAG="AlbumImageViewFragment";
    /**
     * bundle string
     */
    public static final String MODE="mode";
    public static final String PICTURE="picture";
    public static final String ALBUM="album";
    public static final String PICTURE_LIST="pictureList";//ALbumImageListFragment에서 이미 불러온 picture List
    public static final String NUM_PICTURES="numPictures";
    public static final String CURRENT_PAGE="currentPage";
    public static final String CURRENT_INDEX="currentIndex";

    public static final int MULTIPLE_IMAGES_MODE=0;
    public static final int SINGLE_IMAGE_MODE=1;
    /**
     * bundle value
     */
    int mode;
    Album album;
    int numPictures;
    int currentPage;
    int currentIndex;
    ArrayList<Picture> arItem;
    Picture picture;


    ViewPager albumImageViewPager;
    PictureListAdapter pictureListAdapter;



    boolean loading;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d(TAG,"onCreateView");
        View root = inflater.inflate(R.layout.fragment_album_image_view, container, false);
        albumImageViewPager= (ViewPager) root.findViewById(R.id.albumImageViewPager);


        loading=false;
        Bundle args=getArguments();

        mode=args.getInt(MODE,MULTIPLE_IMAGES_MODE);
        if(mode == MULTIPLE_IMAGES_MODE)
        {
            album= (Album) args.getSerializable(ALBUM);
            numPictures= args.getInt(NUM_PICTURES);
            currentPage=args.getInt(CURRENT_PAGE);
            currentIndex=args.getInt(CURRENT_INDEX);
            arItem= (ArrayList<Picture>) args.getSerializable(PICTURE_LIST);
        }

        else if(mode == SINGLE_IMAGE_MODE)
        {
            arItem=new ArrayList<Picture>();
            arItem.add((Picture) args.getSerializable(PICTURE));
            numPictures=1;
        }

        pictureListAdapter=new PictureListAdapter(getActivity(),arItem);
        albumImageViewPager.setAdapter(pictureListAdapter);

        albumImageViewPager.setCurrentItem(currentIndex);


        // Inflate the layout for this fragment
        return root;
    }



    class PictureListAdapter extends PagerAdapter {

        ArrayList<Picture> arItem;
        Context context;
        LayoutInflater inflater;

        public PictureListAdapter(Context context, ArrayList<Picture> arItem)
        {
            inflater = LayoutInflater.from(context);
            this.arItem=arItem;
        }

        @Override
        public int getCount() {
            return numPictures;
        }

        @Override
        public Object instantiateItem(ViewGroup pager, final int position) {

            Log.d("jyr", "instantiateItem" + position);
            //아직 데이터를 불러오지 않은 경우 -> 데이터를 불러온 후 -> notifyDataSetChanged();
            if(arItem.size()<=position && !loading && mode==MULTIPLE_IMAGES_MODE)
            {
                loading=true;
                Log.d("jyr", "load extra images currentPage="+currentPage);

                try {
                    ParseAPI.findPinnedAlbumPictruesInBackground(currentPage+1,album, new FindCallback<Picture>() {
                        @Override
                        public void done(List<Picture> list, ParseException e) {
                            if(e==null) {
                                loading = false;
                                arItem.addAll(list);
                                currentPage++;
                                notifyDataSetChanged();
                            }
                            else
                                ParseAPI.erroHandling(getActivity(),e);
                        }
                    });
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                View v = inflater.inflate(R.layout.full_image_item, pager, false);

                return v;
            }
            else {


                //이미지 불러와 set
                final PhotoViewAttacher mAttacher;
                View v = inflater.inflate(R.layout.full_image_item, pager, false);
                final ResizableImageView imageView = (ResizableImageView) v.findViewById(R.id.fullImageView);
                mAttacher = new PhotoViewAttacher(imageView);
                arItem.get(position).getImageFile().getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if(e==null)
                        {
                            (imageView).setImageBitmap(ImageManipulate.byteArrayToBitmap(bytes));
                            mAttacher.update();
                            notifyDataSetChanged();
                        }
                        else
                            ParseAPI.erroHandling(context,e);
                    }
                });
                ((ViewPager) pager).addView(v, 0);
                return v;
            }


        }

        @Override
        public void destroyItem(ViewGroup pager, int position, Object view) {
            ((ViewPager)pager).removeView((View)view);
        }

        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj;
        }

    }


}

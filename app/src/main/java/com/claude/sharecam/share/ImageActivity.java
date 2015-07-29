package com.claude.sharecam.share;

import android.content.Context;
import android.content.Intent;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.Picture;
import com.claude.sharecam.view.ResizableImageView;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * 이미지 전체화면 보기
 */
public class ImageActivity extends ActionBarActivity {

    public static final String IMAGE_URL_LIST="imageUrlList";
    public static final String NUM_IMAGES="numImages";
//    public static final String NEXT_PAGE="nextPage";
    public static final String CURRENT_PAGE="currentPage";
    public static final String CURRENT_INDEX="currentIndex";

//    ResizableImageView fullImageView;
    ArrayList<String> arItem;
    Context context;
    ViewPager fullImageViewPager;
    int numImages;//이미지 총 갯수
//    int nextPage;
    int currentPage;
    int currentIndex;

    boolean loading;
    ImageListAdapter imageListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image);
//        fullImageView=(ResizableImageView)findViewById(R.id.fullImageView);
        fullImageViewPager=(ViewPager)findViewById(R.id.fullImageViewPager);

        getSupportActionBar().hide();
        context=this;

        Intent intent=getIntent();
        arItem= (ArrayList<String>) intent.getSerializableExtra(IMAGE_URL_LIST);
        numImages=intent.getIntExtra(NUM_IMAGES,arItem.size());
        currentPage =intent.getIntExtra(CURRENT_PAGE,1);
        currentIndex=intent.getIntExtra(CURRENT_INDEX,0);
        loading=false;

        imageListAdapter=new ImageListAdapter(this,arItem);
        fullImageViewPager.setAdapter(imageListAdapter);
        fullImageViewPager.setCurrentItem(currentIndex);

        fullImageViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                Log.d("jyr","pageSelected"+position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });



    }



    class ImageListAdapter extends PagerAdapter {

        ArrayList<String> arItem;
        Context context;
         LayoutInflater inflater;

        public ImageListAdapter(Context context, ArrayList<String> arItem)
        {
            inflater = LayoutInflater.from(context);
            this.arItem=arItem;
        }

        @Override
        public int getCount() {
            return numImages;
        }

        @Override
        public Object instantiateItem(ViewGroup pager, int position) {

            Log.d("jyr","instantiateItem"+position);
            //아직 데이터를 불러오지 않은 경우
            if(arItem.size()<=position && !loading)
            {
                loading=true;
                Log.d("jyr", "load extra images currentPage="+currentPage);
                ParseAPI.getPicturesByMe(currentPage + 1, Constants.NUM_LOAD_PICTURE, new FindCallback<Picture>() {
                    @Override
                    public void done(List<Picture> pictureList, ParseException e) {
                        Log.d("jyr", "load extra images success"+pictureList.size());
                        ArrayList<String> imageUrlList = new ArrayList<String>();
                        for (int i = 0; i < pictureList.size(); i++) {
                            imageUrlList.add(pictureList.get(i).getImageURL());
                        }
                        arItem.addAll(imageUrlList);
                        notifyDataSetChanged();

                        currentPage++;
                        loading=false;

                    }
                });

                View v = inflater.inflate(R.layout.full_image_item, pager, false);

                return v;
            }
            else {
                View v = inflater.inflate(R.layout.full_image_item, pager, false);
                Picasso.with(context).load(arItem.get(position)).into((ResizableImageView) v.findViewById(R.id.fullImageView));
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

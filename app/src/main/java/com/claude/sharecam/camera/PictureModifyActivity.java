package com.claude.sharecam.camera;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.aviary.android.feather.sdk.AviaryIntent;
import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.Individual;
import com.claude.sharecam.view.ResizableImageView;
import com.parse.ParseUser;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class PictureModifyActivity extends ActionBarActivity {

    //intent string
    public static final String MODE="mode";
    public static final String IMAGE_BYTE="imageByte";
    public static final String IMAGE_PATH="imagePath";
    public static final String IMAGE_PATH_ARRAY="imagePathArray";

    int currentPosition;
    //1장 / 여러장
    int mode;

    //이미지들 저장
    private ArrayList<String> arItem;

    // View
    //촬영한 이미지
//    ImageView pictureImg;

    private CameraFrameLayout pictureModifyFrameLayout;
    private ViewPager pictureViewPager;
    private PagerAdapter pagerAdapter;
    private TextView viewPagerIndicator;
    private ImageView cameraBtn,shareBtn,deleteBtn,editBtn,androidShareBtn,shareConfigBtn;

    Context context;

    List<Individual> spItems;//공유 개인 목록

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_picture_modify);

        getSupportActionBar().hide();

        initialize();

    }

    private void initialize()
    {

        spItems=ParseAPI.getSharePerson_Local(this);
//        spItems=Util.getSharePersonList(this);
        context=this;
//        pictureImg=(ImageView)findViewById(R.id.pictureImg);
        pictureViewPager=(ViewPager)findViewById(R.id.pictureViewPager);
        viewPagerIndicator=(TextView)findViewById(R.id.viewPagerIndicator);
        pictureModifyFrameLayout=(CameraFrameLayout)findViewById(R.id.pictureModifyFrameLayout);
        deleteBtn=(ImageView)findViewById(R.id.deleteBtn);
        editBtn=(ImageView)findViewById(R.id.editBtn);
        androidShareBtn=(ImageView)findViewById(R.id.androidShareBtn);
        shareConfigBtn=(ImageView)findViewById(R.id.shareConfigBtn);
        cameraBtn=(ImageView)findViewById(R.id.cameraBtn);
        shareBtn=(ImageView)findViewById(R.id.shareBtn);

        arItem=new ArrayList<String>();

        Intent intent=getIntent();
        mode=intent.getIntExtra(MODE, Constants.PREF_CAMERA_ONE_PICTURE_MODE);
        if(mode==Constants.PREF_CAMERA_ONE_PICTURE_MODE)
        {
            arItem.add(intent.getStringExtra(IMAGE_PATH));
//            pictureImg.setImageBitmap(arItem.get(0));
        }
        else if(mode==Constants.PREF_CAMERA_MULTIPLE_PICTURE_MODE)
        {
            arItem= (ArrayList<String>) intent.getSerializableExtra(IMAGE_PATH_ARRAY);
            viewPagerIndicator.setVisibility(View.VISIBLE);
            pictureModifyFrameLayout.bringChildToFront(viewPagerIndicator);
            viewPagerIndicator.requestLayout();
            viewPagerIndicator.invalidate();
        }

        pagerAdapter=new PagerAdapter(arItem,this);
        pictureViewPager.setAdapter(pagerAdapter);

        currentPosition=0;
        selectPage(currentPosition+1);

        pictureViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

                currentPosition = position;
                selectPage(currentPosition + 1);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        deleteBtn.setOnClickListener(deleteBtnListener);
        editBtn.setOnClickListener(editBtnListener);
        androidShareBtn.setOnClickListener(androidShareBtnListener);
        shareConfigBtn.setOnClickListener(shareConfigBtnListener);
        cameraBtn.setOnClickListener(cameraBtnListener);
        shareBtn.setOnClickListener(shareBtnListener);
    }

    private View.OnClickListener deleteBtnListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            //이미지 삭제 후 toast 실행
            if(ImageManipulate.removeFile(arItem.get(currentPosition)))
                Toast.makeText(context,context.getResources().getString(R.string.toast_delete_picture),Toast.LENGTH_SHORT).show();


            //갤러리 이미지 삭제 알림
            ImageManipulate.renewGallery(context);


            arItem.remove(currentPosition);

            //찍은 사진이 모두 삭제 된 경우 activity 종료
            if(arItem.size()==0)
            {
                finish();
            }

            else {
                pagerAdapter.notifyDataSetChanged();
                if(currentPosition==0) {
                    pictureViewPager.setCurrentItem(currentPosition);
                    selectPage(1);
                }
                else{
                    currentPosition--;
                    pictureViewPager.setCurrentItem(currentPosition);
                }
            }

        }
    };

    private View.OnClickListener editBtnListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Log.d("jyr", "width="+getResources().getDisplayMetrics().widthPixels);

            Uri uri= Uri.parse(new File(arItem.get(currentPosition)).toString());
            Intent intent=new AviaryIntent.Builder(context)
                    .setData(uri)
                    .withOutput(uri)
                    .withOutputFormat(Bitmap.CompressFormat.JPEG)
                    .saveWithNoChanges(true)
                    .withPreviewSize(5000)
                    .withNoExitConfirmation(false)//저장 안하고 뒤로 가기 눌렀을 경우 Dialog 띄움
                    .withOutputQuality(90)
                    .build();

            startActivityForResult(intent,0);

        }
    };

    @Override
    public void onActivityResult(int requestCode,int resultCode, Intent data)
    {
        if(resultCode==RESULT_OK)
        {
            Bundle extras=data.getExtras();
            boolean changed=extras.getBoolean(com.aviary.android.feather.sdk.internal.Constants.EXTRA_OUT_BITMAP_CHANGED);
//            pictureViewPager.setAdapter(pagerAdapter);
            if(changed) {

                pagerAdapter=new PagerAdapter(arItem,context);
                pictureViewPager.setAdapter(pagerAdapter);
                pictureViewPager.setCurrentItem(currentPosition);
                selectPage(currentPosition+1);


                Log.d("jyr","changed");
            }
        }
    }


    private View.OnClickListener androidShareBtnListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Util.shareImage(context,arItem.get(currentPosition));
        }
    };

    private View.OnClickListener shareConfigBtnListener =new View.OnClickListener() {
        @Override
        public void onClick(View v) {

        }
    };

    //카메라로 돌아 가기
    private View.OnClickListener cameraBtnListener= new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            finish();
        }
    };

    //찍은 사진들 공유하기
    private View.OnClickListener shareBtnListener=new View.OnClickListener() {
        @Override
        public void onClick(View v) {


            ParseAPI.uploadPicture(context, spItems, arItem, ParseUser.getCurrentUser());
            finish();
        }
    };

    private void selectPage(int page) {
        viewPagerIndicator.setText(page + " / " + arItem.size());
    }

    private class PagerAdapter extends android.support.v4.view.PagerAdapter{

        private LayoutInflater inflater;
        Context context;
        ArrayList<String > arItem;
        public PagerAdapter(ArrayList<String > arItem,Context context)
        {
            this.context=context;
            inflater=LayoutInflater.from(context);
            this.arItem=arItem;
        }
        @Override
        public int getCount() {
            return arItem.size();
        }

        @Override
        public Object instantiateItem(ViewGroup pager, int position) {

            Log.d("jyr","instatiateItem");
            View root=inflater.inflate(R.layout.picture_laoyout,null);

            ResizableImageView imageView= (ResizableImageView) root.findViewById(R.id.pictureImg);
//            imageView.setImageBitmap(arItem.get(position));
            File picture = new File(arItem.get(position));
            if (picture.exists()) {

                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inSampleSize = 2;
                Bitmap myBitmap = BitmapFactory.decodeFile(picture.getAbsolutePath(), options);
                imageView.setImageBitmap(myBitmap);
            }

            ((ViewPager)pager).addView(root, 0);

            root.setTag(arItem.get(position));
            return root;
        }



        @Override
        public boolean isViewFromObject(View pager, Object obj) {
            return pager == obj;
        }

        @Override
        public void destroyItem(ViewGroup pager, int position, Object object){
            ((ViewPager)pager).removeView((View) object);
        }



        @Override
        public int getItemPosition (Object object)
        {
            View o = (View) object;
            int index = arItem.indexOf(o.getTag());
            if (index == -1)
                return POSITION_NONE;
            else
                return index;
        }

    }
}

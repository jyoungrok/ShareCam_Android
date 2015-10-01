package com.claude.sharecam.main;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.claude.sharecam.R;
import com.claude.sharecam.Util;

/**
 * ImageViewFragment의 중간다리 역할
 */
public class ImageViewActivity extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_view);

        Bundle args=new Bundle();
        Intent intent=getIntent();
        ImageViewFragment imageViewFragment=new ImageViewFragment();
        imageViewFragment.setArguments(intent.getBundleExtra(ImageViewFragment.IMAGE_PARSE_FILE));
        Util.startFragment(getSupportFragmentManager(),R.id.imageViewLayout,imageViewFragment,false,null);



    }

}

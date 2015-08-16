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
//        args.putSerializable(ImageViewFragment.IMAGE_PARSE_FILE,intent.getSerializableExtra(ImageViewFragment.IMAGE_PARSE_FILE));
        ImageViewFragment imageViewFragment=new ImageViewFragment();
        imageViewFragment.setArguments(intent.getBundleExtra(ImageViewFragment.IMAGE_PARSE_FILE));
        Util.startFragment(getSupportFragmentManager(),R.id.imageViewLayout,imageViewFragment,false,null);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_image_view, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}

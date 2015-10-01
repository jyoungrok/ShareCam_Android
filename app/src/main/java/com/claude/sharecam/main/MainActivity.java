package com.claude.sharecam.main;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.config.ConfigFragment;

public class MainActivity extends ActionBarActivity {

    public static final String FRAGMENT_TYPE="fragmentType";

    public static final int ConfigFragment=0;
    public static final int NotificationListFragment=1;

    FrameLayout mainContainer;
    LinearLayout mainProgressLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mainContainer =(FrameLayout)findViewById(R.id.mainContainer);
        mainProgressLayout =(LinearLayout)findViewById(R.id.mainProgressLayout);


        switch (getIntent().getIntExtra(FRAGMENT_TYPE,0))
        {
            case ConfigFragment:
                Util.startFragment(getSupportFragmentManager(),R.id.mainContainer,new ConfigFragment(),false,null);
                break;
            case NotificationListFragment:
                Util.startFragment(getSupportFragmentManager(),R.id.mainContainer,new NotificationListFragment(),false,null);
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void setProgressLayout(int state) {

        switch (state) {
            case Constants.PROGRESS_AND_LAYOUT_VISIBLE:
                mainContainer.setVisibility(View.VISIBLE);
                mainProgressLayout.setVisibility(View.VISIBLE);
                break;
            case Constants.PROGRESS_VISIBLE:
                mainContainer.setVisibility(View.GONE);
                mainProgressLayout.setVisibility(View.VISIBLE);
                break;
            case Constants.PROGRESS_INVISIBLE:
                mainContainer.setVisibility(View.VISIBLE);
                mainProgressLayout.setVisibility(View.GONE);
                break;

        }
    }
}

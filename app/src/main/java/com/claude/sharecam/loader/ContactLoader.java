package com.claude.sharecam.loader;

import android.content.Context;
import android.support.v4.content.AsyncTaskLoader;
import android.util.Log;

import com.claude.sharecam.Util;
import com.claude.sharecam.orm.IndividualItem;

import java.util.ArrayList;

/**
 * Created by Claude on 15. 7. 24..
 */
public class ContactLoader extends AsyncTaskLoader<ArrayList<IndividualItem>> {
    Context context;
    public ContactLoader(Context context) {
        super(context);
        this.context=context;
    }

    @Override
    public ArrayList<IndividualItem> loadInBackground() {


        return Util.getContactList(context);
    }

    @Override
    protected void onStartLoading() {
        Log.d("jyr", "start loading");
        forceLoad();
    }
}

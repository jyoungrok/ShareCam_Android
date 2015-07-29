package com.claude.sharecam.parse;

import android.content.Context;

import com.parse.DeleteCallback;
import com.parse.ParseException;

/**
 * Created by Claude on 15. 7. 24..
 */
public class SCDeleteCallback implements DeleteCallback {


    Callback callback;
    Context context;
    public SCDeleteCallback(Context context,Callback callback)
    {
        this.context=context;
        this.callback=callback;
    }

    public SCDeleteCallback() {

    }

    @Override
    public void done(ParseException e) {
        if(e==null)
        {
            callback.done();
        }
        else
            ParseAPI.erroHandling(context, e);
    }

    public interface Callback {
        void done();
    }

}

package com.claude.sharecam.parse;

import android.content.Context;

import com.parse.ParseException;
import com.parse.SaveCallback;

/**
 * Created by Claude on 15. 7. 10..
 */
public class SCSaveCallback implements SaveCallback {

    Callback callback;
    Context context;
    public SCSaveCallback(Context context,Callback callback)
    {
        this.context=context;
        this.callback=callback;
    }

    public SCSaveCallback() {

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

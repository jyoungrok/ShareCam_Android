//package com.claude.sharecam.parse;
//
//import android.content.Context;
//
//import com.parse.FindCallback;
//import com.parse.ParseException;
//
//import java.util.List;
//
///**
// * Created by Claude on 15. 7. 24..
// */
//public class SCFindCallback implements FindCallback {
//
//    Callback callback;
//    Context context;
//    public SCFindCallback(Context context,Callback callback)
//    {
//        this.context=context;
//        this.callback=callback;
//    }
//
//    public SCFindCallback() {
//
//    }
//
//    @Override
//    public void done(List list, ParseException e) {
//        if(e==null)
//        {
//            callback.done(list);
//        }
//        else
//            ParseAPI.erroHandling(context, e);
//    }
//
//    public interface Callback {
//        void done(List list);
//    }
//}

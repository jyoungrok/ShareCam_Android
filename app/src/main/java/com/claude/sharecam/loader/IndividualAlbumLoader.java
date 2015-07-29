//package com.claude.sharecam.loader;
//
//import android.content.Context;
//import android.support.v4.content.AsyncTaskLoader;
//import android.util.Log;
//
//import com.claude.sharecam.Util;
//import com.claude.sharecam.main.IndividualImageItem;
//import com.claude.sharecam.parse.Friend;
//import com.claude.sharecam.parse.ParseAPI;
//import com.claude.sharecam.parse.Picture;
//import com.claude.sharecam.parse.ShareIndividual;
//import com.claude.sharecam.parse.User;
//import com.claude.sharecam.share.IndividualItem;
//import com.claude.sharecam.share.IndividualItemList;
//import com.parse.ParseException;
//
//import java.util.ArrayList;
//import java.util.List;
//
///**
// * Created by Claude on 15. 7. 7..
// */
//public class IndividualAlbumLoader extends AsyncTaskLoader<List<Picture>> {
//
//    Context context;
//
//    public IndividualAlbumLoader(Context context) {
//        super(context);
//        this.context=context;
//    }
//
//    @Override
//    public List<Picture> loadInBackground() {
//
//        return  ParseAPI.getPicturesByMe();
//    }
//    @Override
//    protected void onStartLoading() {
//        Log.d("jyr","start loading individual album");
//        forceLoad();
//    }
//}

//package com.claude.sharecam.view;
//
//import android.view.View;
//
///**
// *  중복 클릭 방지 clickListener
// */
//public class OneClick implements View.OnClickListener {
//
//    Listener listener;
//
//    public OneClick()
//    {
//    }
//    public OneClick(Listener listener)
//    {
//        this.listener=listener;
//    }
////
//    public interface Listener{
//        boolean isClicked=false;
//        void onClick(View v);
//    }
//    @Override
//    public void onClick(View v) {
//
//        if(!listener.isClicked) {
//            listener.onClick(v);
//        }
//    }
//
//}

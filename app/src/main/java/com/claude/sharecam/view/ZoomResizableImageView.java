//package com.claude.sharecam.view;
//
//import android.content.Context;
//import android.content.res.Configuration;
//import android.graphics.drawable.Drawable;
//import android.util.AttributeSet;
//
//import it.sephiroth.android.library.imagezoom.ImageViewTouch;
//
///**
// * Created by Claude on 15. 8. 13..
// */
//public class ZoomResizableImageView extends ImageViewTouch {
//    public ZoomResizableImageView(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    private boolean ratioIsSet=false;
//    private float ratio; // height/width
//
//
//    @Override
//    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
//
//        if(!ratioIsSet) {
//            Drawable d = getDrawable();
//
//            if (d != null) {
//                // ceil not round - avoid thin vertical gaps along the left/right edges
//                int width = MeasureSpec.getSize(widthMeasureSpec);
//                int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
//                setMeasuredDimension(width, height);
//            } else {
//                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
//            }
//        }
//        else{
//            int width;
//            int height;
//            if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT) {
//                width = MeasureSpec.getSize(widthMeasureSpec);
//                height = (int) Math.ceil((float) width * (float) ratio);
//            }
//            else
//            {
//                height=MeasureSpec.getSize(heightMeasureSpec);
//                width=(int) Math.ceil((float) height * (float) ratio);
//            }
//            setMeasuredDimension(width, height);
//        }
//    }
//
//    public void setRatio(float ratio)
//    {
//        this.ratio=ratio;
//        requestLayout();
//        invalidate();
//    }
//}

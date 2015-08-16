package com.claude.sharecam.view;

/**
 * Portrait인 경우 width에 이미지 크기 맞춤
 * Landscape인 경우 height에 이미지 크기 맞춤
 */

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;



public class ResizableImageView extends ImageView {

    private boolean ratioIsSet=false;
    private float ratio; // height/width
    public ResizableImageView(Context context){super(context);}

    public ResizableImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){

        if(!ratioIsSet) {
            Drawable d = getDrawable();

            if (d != null) {
                // ceil not round - avoid thin vertical gaps along the left/right edges
                int width = MeasureSpec.getSize(widthMeasureSpec);
                int height = (int) Math.ceil((float) width * (float) d.getIntrinsicHeight() / (float) d.getIntrinsicWidth());
                setMeasuredDimension(width, height);
            } else {
                super.onMeasure(widthMeasureSpec, heightMeasureSpec);
            }
        }
        else{
            int width;
            int height;
            if(getResources().getConfiguration().orientation== Configuration.ORIENTATION_PORTRAIT) {
                width = MeasureSpec.getSize(widthMeasureSpec);
                height = (int) Math.ceil((float) width * (float) ratio);
            }
            else
            {
                height=MeasureSpec.getSize(heightMeasureSpec);
                width=(int) Math.ceil((float) height * (float) ratio);
            }
            setMeasuredDimension(width, height);
        }
    }

    public void setRatio(float ratio)
    {
        this.ratio=ratio;
        requestLayout();
        invalidate();
    }

}

package com.claude.sharecam.view;

/**
 * Created by Claude on 15. 5. 1..
 */

import android.content.Context;
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
            int width = MeasureSpec.getSize(widthMeasureSpec);
            int height =(int) Math.ceil((float) width * (float) ratio);
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

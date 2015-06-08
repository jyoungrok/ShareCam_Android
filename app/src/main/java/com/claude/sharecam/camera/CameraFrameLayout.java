package com.claude.sharecam.camera;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

/**
 * Created by Claude on 15. 4. 29..
 *
 *
 */
public class CameraFrameLayout extends FrameLayout {

    private static final double ASPECT_RATIO_34 = 3.0 / 4.0;

    public CameraFrameLayout(Context context) {
        super(context);
    }

    public CameraFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height;
        int width = MeasureSpec.getSize(widthMeasureSpec);

        height = (int) (width / ASPECT_RATIO_34 + .5);



        setMeasuredDimension(width, height);

        //Child View들의 크기 지정해줌
        int childCount = getChildCount();
        for(int i=0; i<childCount;i++) {
            View v = getChildAt(i);

            int widthSpec=0;
            int heightSpec=0;
           MarginLayoutParams lp = (MarginLayoutParams) v.getLayoutParams();

            switch(lp.width) {
                case ViewGroup.LayoutParams.WRAP_CONTENT:
                    widthSpec = getChildMeasureSpec(widthMeasureSpec, +lp.leftMargin + lp.rightMargin, lp.width);

                    break;
                case ViewGroup.LayoutParams.MATCH_PARENT:
                    widthSpec=widthMeasureSpec;
                    widthSpec = MeasureSpec.makeMeasureSpec(width -
                                    lp.leftMargin - lp.rightMargin,
                            MeasureSpec.EXACTLY);
                    break;

                default:
                    widthSpec=lp.width;
                    break;
            }

            switch(lp.height) {
                case ViewGroup.LayoutParams.WRAP_CONTENT:
                    heightSpec = getChildMeasureSpec(heightMeasureSpec,lp.topMargin + lp.bottomMargin,
                        lp.height);
                    break;
                case ViewGroup.LayoutParams.MATCH_PARENT:
                    heightSpec= MeasureSpec.makeMeasureSpec(height-lp.topMargin - lp.bottomMargin,
                            MeasureSpec.EXACTLY);
                    break;

                default:
                    heightSpec=lp.height;
                    break;
            }

            v.measure(widthSpec, heightSpec);


        }

    }

//    @Override
//    protected void onLayout(boolean changed, int l, int t, int r, int b) {
//
//        int childCount = getChildCount();
//        for(int i=0; i<childCount;i++) {
//            View v = getChildAt(i);
//            v.layout(l,t,r,b);
//        }
//    }


}

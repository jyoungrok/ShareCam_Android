package com.claude.sharecam.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * Created by Claude on 15. 6. 12..
 */
public class ImageViewRecyclable extends ImageView
{
    private Bitmap bitmap;

    public ImageViewRecyclable(Context context)
    {
        super(context);
    }
    public ImageViewRecyclable(Context context, AttributeSet attrs) {
        super(context, attrs, 0);
    }



    @Override
    public void setImageBitmap(Bitmap bm)
    {
        super.setImageBitmap(bm);
        if (bitmap != null) bitmap.recycle();
        this.bitmap = bm;
    }
}

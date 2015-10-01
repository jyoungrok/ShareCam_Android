package com.claude.sharecam.main;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.claude.sharecam.R;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.SerializableParseFile;
import com.claude.sharecam.util.ImageManipulate;
import com.claude.sharecam.view.ResizableImageView;
import com.parse.GetDataCallback;
import com.parse.ParseException;

/**
 * Created by Claude on 15. 8. 6..
 */
public class ImageViewFragment extends Fragment {

    public static final String IMAGE_PARSE_FILE="imageParseFile";
    ResizableImageView resizableImageView;
    SerializableParseFile imageParseFile;
    ProgressBar imageViewProgressBar;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.image_view_fragment, container, false);
        resizableImageView=(ResizableImageView)root.findViewById(R.id.resizableImageView);
        imageViewProgressBar=(ProgressBar)root.findViewById(R.id.imageViewProgressBar);

        Bundle args=new Bundle();
        imageParseFile= (SerializableParseFile) args.getSerializable(IMAGE_PARSE_FILE);
        init();
        return root;

    }


    private void init() {

        imageViewProgressBar.setVisibility(View.VISIBLE);
        imageParseFile.getParseFile().getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
               if(e==null)
               {
                   resizableImageView.setImageBitmap(ImageManipulate.byteArrayToBitmap(bytes));
               }
                else
                   ParseAPI.erroHandling(getActivity(),e);
                imageViewProgressBar.setVisibility(View.GONE);
            }
        });
    }



}

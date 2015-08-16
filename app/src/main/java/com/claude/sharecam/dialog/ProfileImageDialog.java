package com.claude.sharecam.dialog;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.claude.sharecam.R;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.SerializableParseFile;
import com.claude.sharecam.util.ImageManipulate;
import com.claude.sharecam.view.ResizableImageView;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Created by Claude on 15. 8. 13..
 */
public class ProfileImageDialog extends DialogFragment {

    public static final String IMAGE_PARSE_FILE="imageParseFile";

    ParseFile imageParseFile;
    ResizableImageView profileImageView;

    ImageView closeProfileImageBtn;
    PhotoViewAttacher mAttacher;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {


//        getDialog().setCanceledOnTouchOutside(true);

        View root = inflater.inflate(R.layout.profile_image_dialog_fragment, container, false);
        profileImageView=(ResizableImageView)root.findViewById(R.id.profileImageView);
        closeProfileImageBtn=(ImageView)root.findViewById(R.id.closeProfileImageBtn);

        Bundle args=getArguments();
        imageParseFile=((SerializableParseFile)args.getSerializable(IMAGE_PARSE_FILE)).getParseFile();
//        profileImageView.setDisplayType(ImageViewTouchBase.DisplayType.FIT_TO_SCREEN);
//        profileImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        //clear 처리 하지 않으면 savedInstanceState에 bundle이 들어가는데 serializable을 넘겨주지 못해 에러 발생한다.
        args.clear();


        init();
        return root;

    }
//    @Override
//    public void onActivityCreated(Bundle savedInstance)
//    {
//        super.onActivityCreated(savedInstance);
//
//
//
//    }


    private void init() {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        //status bar  감추기

        mAttacher = new PhotoViewAttacher(profileImageView);
        imageParseFile.getDataInBackground(new GetDataCallback() {
            @Override
            public void done(byte[] bytes, ParseException e) {
                if(e==null)
                {
                    profileImageView.setImageBitmap(ImageManipulate.byteArrayToBitmap(bytes));
                    mAttacher.update();
                }
                else{
                    ParseAPI.erroHandling(getActivity(),e);
                }

            }
        });

        closeProfileImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme()) {
            @Override
            public void onBackPressed() {
                dismiss();
            }
        };
    }

}

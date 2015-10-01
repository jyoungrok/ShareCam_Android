package com.claude.sharecam.dialog;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.main.ImageViewActivity;
import com.claude.sharecam.parse.Contact;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.SerializableParseFile;
import com.claude.sharecam.util.ImageManipulate;
import com.parse.GetDataCallback;
import com.parse.ParseException;

/**
 * Created by Claude on 15. 8. 6..
 */
public class ProfileDialog extends DialogFragment {

    public static final String INDIVIDUAL_ITEM="individualItem";
    public static final String CONTAINER_ID="containerId";//fragment가 보여지는 view  id

//    int containerId;
    Contact individualItem;

    ImageView dialogProfileImg;
    TextView dialogUserName;
    LinearLayout dialogInviteLayout;
    LinearLayout dialogAddToHomeLayout;
    ImageView dialogInviteBtn;
    ImageView dialogAddToHomeBtn;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);

        View root = inflater.inflate(R.layout.profile_dialog_fragment, container, false);
        dialogProfileImg=(ImageView)root.findViewById(R.id.dialogProfileImg);
        dialogUserName=(TextView)root.findViewById(R.id.dialogUserName);
        dialogInviteLayout=(LinearLayout)root.findViewById(R.id.dialogInviteLayout);
        dialogAddToHomeLayout=(LinearLayout)root.findViewById(R.id.dialogAddToHomeLayout);
        dialogInviteBtn=(ImageView)root.findViewById(R.id.dialogInviteBtn);
        dialogAddToHomeBtn=(ImageView)root.findViewById(R.id.dialogAddToHomeBtn);

        Bundle args=getArguments();
        individualItem = (Contact) args.getSerializable(INDIVIDUAL_ITEM);
        //clear 처리 하지 않으면 savedInstanceState에 bundle이 들어가는데 serializable을 넘겨주지 못해 에러 발생한다.
        args.clear();
//        containerId=args.getInt(CONTAINER_ID);


        init();
        return root;

    }


    private void init() {

        //쉐어캠 친구인 경우

        if(individualItem.getFriendUser()!=null)
        {
            if(individualItem.getFriendUser().getThumProfileFile()!=null)
            {
                individualItem.getFriendUser().getThumProfileFile().getDataInBackground(new GetDataCallback() {
                    @Override
                    public void done(byte[] bytes, ParseException e) {
                        if (e == null) {
                            dialogProfileImg.setImageBitmap(ImageManipulate.byteArrayToBitmap(bytes));
                        } else {
                            ParseAPI.erroHandling(getActivity(), e);
                        }
                    }
                });

                dialogProfileImg.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(getActivity(), ImageViewActivity.class);
                        Bundle args=new Bundle();
                        SerializableParseFile serializableParseFile =new SerializableParseFile(individualItem.getFriendUser().getThumProfileFile());
                        MyDialogBuilder.showProfileImageDialog(getActivity().getSupportFragmentManager(),serializableParseFile);
                    }
                });
                dialogInviteLayout.setVisibility(View.GONE);
                dialogAddToHomeBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Util.addIndividualShortcut(getActivity(),getFragmentManager(),individualItem);
                    }
                });
            }
        }

        //연락처인 경우
        else
        {
            if(individualItem.getPinContactPhotoUri()!=null) {
                dialogProfileImg.setImageURI(Uri.parse(individualItem.getPinContactPhotoUri()));
            }
            dialogInviteLayout.setVisibility(View.VISIBLE);
            dialogInviteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    Intent sendIntent = new Intent(Intent.ACTION_VIEW);
                    sendIntent.putExtra("address",individualItem.getPhone());
                    sendIntent.putExtra("sms_body", getString(R.string.sms_invite_msg));
                    sendIntent.setType("vnd.android-dir/mms-sms");
                    startActivity(sendIntent);

                }
            });
            dialogAddToHomeBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Util.addIndividualShortcut(getActivity(),getFragmentManager(),individualItem);
                }
            });

        }

        dialogUserName.setText(individualItem.getPinContactName());

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

    @Override
    public void onPause()
    {
        super.onPause();
        dismiss();
    }
}

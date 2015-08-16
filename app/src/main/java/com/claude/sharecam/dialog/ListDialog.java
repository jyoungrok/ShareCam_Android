package com.claude.sharecam.dialog;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.claude.sharecam.R;
import com.claude.sharecam.view.RecycleLinearLayoutManager;

import java.util.ArrayList;

/**
 * Created by Claude on 15. 8. 6..
 */
public class ListDialog extends DialogFragment {

    public static final String DIALOG_TEXT = "dialogText";

    public static final String STR_ITEMS = "strItems";
    public static final String LISTENER_ITEMS="listenerItems";

    RecyclerView listDialogRecyclerView;
    ArrayList<String> strItems;
    DialogListAdapter dialogListAdapter;
    ArrayList<View.OnClickListener> listenerItems;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().setCanceledOnTouchOutside(true);

        View root = inflater.inflate(R.layout.list_dialog_fragment, container, false);
        listDialogRecyclerView = (RecyclerView) root.findViewById(R.id.listDialogRecyclerView);

        Bundle args=getArguments();
        strItems = (ArrayList<String>) args.getSerializable(STR_ITEMS);
        listenerItems=(ArrayList<View.OnClickListener>)args.getSerializable(LISTENER_ITEMS);
        args.clear();

        init();
        return root;

    }

//    @Override
//    public void onResume()
//    {
//        super.onResume();
//        Window window = getDialog().getWindow();
//        window.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT);
//        window.setGravity(Gravity.CENTER);
//    }

    private void init() {
//        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        RecycleLinearLayoutManager layoutManager=new RecycleLinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        listDialogRecyclerView.setLayoutManager(layoutManager);
        dialogListAdapter=new DialogListAdapter(strItems,listenerItems);
        listDialogRecyclerView.setAdapter(dialogListAdapter);

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

    private class DialogListAdapter extends RecyclerView.Adapter {
        ArrayList<String> arItem;
        ArrayList<View.OnClickListener> listenerItems;

        public DialogListAdapter (ArrayList<String> arItem,  ArrayList<View.OnClickListener> listenerItems)
        {
            this.arItem=arItem;
            this.listenerItems=listenerItems;
        }
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.
                    from(parent.getContext()).
                    inflate(R.layout.list_dialog_item, parent, false);
            return new ViewHolder(itemView);
        }



        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

            ((ViewHolder)holder).itemTextView.setText(arItem.get(position));
            ((ViewHolder)holder).itemTextView.setOnClickListener(listenerItems.get(position));
        }

        @Override
        public int getItemCount() {
            return arItem.size();
        }

        public class ViewHolder extends RecyclerView.ViewHolder
        {

            public TextView itemTextView;

            public ViewHolder(View itemView) {
                super(itemView);
                this.itemTextView= (TextView) itemView.findViewById(R.id.itemTextView);
            }
        }
    }
}

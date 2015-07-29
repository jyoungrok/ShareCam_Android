package com.claude.sharecam.upload;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.Image;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.camera.ImageManipulate;
import com.claude.sharecam.orm.UploadingPicture;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.view.ImageViewRecyclable;

import java.util.ArrayList;

//import io.github.codefalling.recyclerviewswipedismiss.SwipeDismissRecyclerViewTouchListener;

public class UploadActivity extends ActionBarActivity {

    public static final String TAG="UploadActivity";
//    public static final String broadcastUploadDataChanged="com.claude.service.uploadchanged";//새로운 uploading
    public static final String STATE="state";
    public static final String ID="id";

    Context context;

    RecyclerView uploadingRecyclerView;
    UploadingAdapter uploadingAdapter;
    private IntentFilter intentFilter;

    LinearLayoutManager linearLayoutManager;
//
//    ArrayList<UploadingPicture> beforeItems;//items before uploading success
//    ArrayList<UploadingPicture> afterItems;//items after uploading success
        private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            uploadingAdapter.notifyDataSetChanged();
        }
    };
//    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//
//            //기존에 추가되어 있는 upload object의 state가 바뀐 경우
//            if(intent.getAction()==UploadService.broadcastStateAction)
//            {
//
//                int state=intent.getIntExtra(UploadService.STATE,-1);
//                int id=intent.getIntExtra(UploadService.ID,-1);
//                String pictureId=intent.getStringExtra(UploadService.PICTURE_ID);
//
//                Log.d("jyr","get broadcast state action state = "+state+"   id = "+id);
//
//                switch(state)
//                {
//                    default:
//
//                        // -> 업로드 실패 / 업로드 중으로 상태 변경
//                    case UploadingPicture.FAILED_UPLOADING_STATE:
//                    case UploadingPicture.UPLOADING_STATE:
//                        for(int i=0; i<beforeItems.size(); i++)
//                        {
//                            if(beforeItems.get(i).getId()==id)
//                            {
//                                beforeItems.get(i).setState(state);
//                                beforeItems.get(i).setPictureId(pictureId);
//                                uploadingAdapter.notifyDataSetChanged();
//                                break;
//                            }
//
//                        }
//                        break;
//
//                    // -> 업로드 성공
//                    case UploadingPicture.SUCCESS_UPLOADING_STATE:
//                        for(int i=0; i<beforeItems.size(); i++)
//                        {
//                            if(beforeItems.get(i).getId()==id)
//                            {
//                                beforeItems.get(i).setState(state);
//                                beforeItems.get(i).setPictureId(pictureId);
//                                afterItems.add(0,beforeItems.get(i));
//                                beforeItems.remove(i);
//                                uploadingAdapter.notifyDataSetChanged();
//                                break;
//                            }
//                        }
//                        break;
//                }
//            }
//
//            //새로운 업로드 추가
//            else if(intent.getAction()==UploadService.broadcastNewUploadingAction)
//            {
//                String filePath=intent.getStringExtra(UploadService.FILE_PATH);
//                int state=intent.getIntExtra(UploadService.STATE,-1);
//                int id=intent.getIntExtra(UploadService.ID,-1);
//
//                UploadingPicture newBeforeItem=new UploadingPicture();
//                newBeforeItem.setFilePath(filePath);
//                newBeforeItem.setState(state);
//                newBeforeItem.setId(id);
//
//                beforeItems.add(0,newBeforeItem);
//                uploadingAdapter.notifyDataSetChanged();
//
//            }
//
//
//        }
//    };

    public static final int SERVICE_REQUEST_CODE=1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload);

        getSupportActionBar().hide();
//        inidData();
        context=this;
        intentFilter=new IntentFilter();
//        intentFilter.addAction(broadcastUploadDataChanged);
        intentFilter.addAction(UploadService.broadcastStateAction);
//        intentFilter.addAction(UploadService.broadcastPercentAction);


        uploadingRecyclerView= (RecyclerView) findViewById(R.id.uploadingRecyclerView);
        linearLayoutManager=new LinearLayoutManager(this);
        uploadingRecyclerView.setLayoutManager(linearLayoutManager);


        uploadingAdapter=new UploadingAdapter(UploadService.instance.beforeItems,UploadService.instance.afterItems);
        uploadingRecyclerView.setAdapter(uploadingAdapter);
//
//        // init swipe to dismiss logic
        ItemTouchHelper swipeToDismissTouchHelper = new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(
                ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                // callback for drag-n-drop, false to skip this feature
                return false;
            }

            @Override
            public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                Log.d(TAG,"swiped ");
                //데이터 삭제
                if(uploadingAdapter.isAvaialbeToDismiss(viewHolder.getAdapterPosition())) {
                    UploadService.instance.deleteItem(uploadingAdapter.getItem(viewHolder.getAdapterPosition()));
                    uploadingAdapter.notifyItemRemoved(viewHolder.getAdapterPosition());
                }
                //데이터 삭제 안되는 경우
                else {
                    uploadingAdapter.notifyItemChanged(viewHolder.getAdapterPosition());
                }

            }
        });
        swipeToDismissTouchHelper.attachToRecyclerView(uploadingRecyclerView);
//
//        SwipeDismissRecyclerViewTouchListener listener = new SwipeDismissRecyclerViewTouchListener.Builder(
//                uploadingRecyclerView,
//                new SwipeDismissRecyclerViewTouchListener.DismissCallbacks() {
//                    @Override
//                    public boolean canDismiss(int position) {
////                        if(uploadingAdapter.isAvaialbeToDismiss(position)) {
////                            UploadService.instance.deleteItem(uploadingAdapter.getItem(position));
////                            uploadingAdapter.notifyDataSetChanged();
////                        }
//                       return  uploadingAdapter.isAvaialbeToDismiss(position);
//                    }
//
//                    @Override
//                    public void onDismiss(View view) {
//                        int id = uploadingRecyclerView.getPosi(view);
//                        UploadService.instance.deleteItem(uploadingAdapter.getItem(position));
//                            uploadingAdapter.notifyDataSetChanged();
//
//                    }
//                })
//                .setIsVertical(false)
//                .setItemTouchCallback(
//                        new SwipeDismissRecyclerViewTouchListener.OnItemTouchCallBack() {
//                            @Override
//                            public void onTouch(int index) {
//                                // Do what you want when item be touched
//                            }
//                        })
//                .create();
//
//        uploadingRecyclerView.setOnTouchListener(listener);

    }

//    //DB에서 데이터 불러와 설정
//    private void inidData()
//    {
//        beforeItems=new ArrayList<UploadingPicture>();
//        afterItems=new ArrayList<UploadingPicture>();
//
//        List<UploadingPicture> allItems=UploadingPicture.get_ne_finished_ob_createdAt(this);
//
//
//        if(allItems!=null) {
//            Log.d("jyr","uploading num"+allItems.size());
//            for (int i = 0; i < allItems.size(); i++) {
//                //업로드 완료한 경우
//                if (allItems.get(i).getState() == UploadingPicture.SUCCESS_UPLOADING_STATE) {
//                    afterItems.add(allItems.get(i));
//                }
//                //업로드 완료 전 (실패, 대기, 업로드 중)
//                else {
//                    beforeItems.add(allItems.get(i));
//                }
//            }
//        }
//    }


    @Override
    public void onResume() {
        super.onResume();
        registerReceiver(broadcastReceiver, intentFilter);
        Log.d("jyr","register intent filter by uploadActivity");
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
        Log.d("jyr","unregister intent filter by uploadActivity");
    }

    private class UploadingAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>
    {
        public final int BEFORE_UPLOADING_PARENT_TYPE=0;
        public final int AFTER_UPLOADING_PARENT_TYPE=1;
        public final int BEFORE_UPLOADING_CHILD_TYPE=2;
        public final int AFTER_UPLOADING_CHILD_TYPE=3;

        ArrayList<UploadingPicture> beforeItems;
        ArrayList<UploadingPicture> afterItems;

        public UploadingAdapter(ArrayList<UploadingPicture> beforeItems,ArrayList<UploadingPicture> afterItems )
        {
            this.beforeItems=beforeItems;
            this.afterItems=afterItems;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            switch(viewType)
            {
                case BEFORE_UPLOADING_PARENT_TYPE:
                case AFTER_UPLOADING_PARENT_TYPE:
                    View itemLayoutView = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.uploading_parent, parent, false);
                    ParentViewHolder parentViewHolder=new ParentViewHolder(itemLayoutView);
                    return parentViewHolder;
                default:
                case BEFORE_UPLOADING_CHILD_TYPE:
                case AFTER_UPLOADING_CHILD_TYPE:
                    View itemLayoutView2 = LayoutInflater.from(parent.getContext())
                            .inflate(R.layout.uploading_child, parent, false);
                    ChildViewHolder childViewHolder=new ChildViewHolder(itemLayoutView2);
                    return childViewHolder;
            }
        }

        @Override
        public void onBindViewHolder(RecyclerView.ViewHolder holder, final int position) {

            switch(getItemViewType(position))
            {
                case BEFORE_UPLOADING_PARENT_TYPE:
                    ((ParentViewHolder)holder).upParentText.setText(getString(R.string.uploading));
                    break;
                case AFTER_UPLOADING_PARENT_TYPE:
                    ((ParentViewHolder)holder).upParentText.setText(getString(R.string.success_uploading));
                    break;
                //업로딩
                case BEFORE_UPLOADING_CHILD_TYPE:
                    ((ChildViewHolder)holder).upChildImageVIew.setImageBitmap(ImageManipulate.getThumbnailFromPath(beforeItems.get(getBeforeItemsPosition(position)).getFilePath(), Constants.THUMB_NAIL_SIZE));
                    ((ChildViewHolder)holder).upStateText.setText(beforeItems.get(getBeforeItemsPosition(position)).getStateName(context));
                    if(beforeItems.get(getBeforeItemsPosition(position)).getState()==UploadingPicture.FAILED_UPLOADING_STATE)
                    {
                        ((ChildViewHolder)holder).reUploadBtn.setVisibility(View.VISIBLE);
                        ((ChildViewHolder)holder).reUploadBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                ParseAPI.reUploadFailedPicture(context,beforeItems.get(getBeforeItemsPosition(position)));
                            }
                        });
                    }
                    else
                        ((ChildViewHolder)holder).reUploadBtn.setVisibility(View.GONE);

                    break;
                //업로드 완료
                case AFTER_UPLOADING_CHILD_TYPE:
                    ((ChildViewHolder)holder).upChildImageVIew.setImageBitmap(ImageManipulate.getThumbnailFromPath(afterItems.get(getAfterItemPosition(position)).getFilePath(), Constants.THUMB_NAIL_SIZE));
                    ((ChildViewHolder)holder).upStateText.setText(afterItems.get(getAfterItemPosition(position)).getStateName(context));

                    break;
            }
        }

        public UploadingPicture getItem(int position)
        {
            if(position>0 &&  position<=beforeItems.size())
                return beforeItems.get(getBeforeItemsPosition(position));
            else if(position>beforeItems.size()+1)
                return afterItems.get(getAfterItemPosition(position));
            else
                return null;
        }

        private int getBeforeItemsPosition(int position)
        {
            return position-1;
        }

        private int getAfterItemPosition(int position)
        {
            return position-(beforeItems.size()+2);
        }

        public boolean isAvaialbeToDismiss(int position)
        {
            if(getItemViewType(position)==AFTER_UPLOADING_CHILD_TYPE)
                return true;
            else return false;
        }

        @Override
        public int getItemViewType(int position) {
            if(position==0)
                return BEFORE_UPLOADING_PARENT_TYPE;
            else if(position==beforeItems.size()+1)
                return AFTER_UPLOADING_PARENT_TYPE;
            else if(0<position && position<beforeItems.size()+1)
                return BEFORE_UPLOADING_CHILD_TYPE;
            else
                return AFTER_UPLOADING_CHILD_TYPE;
        }



        @Override
        public int getItemCount() {
            return beforeItems.size()+afterItems.size()+2;
        }


        class ParentViewHolder extends RecyclerView.ViewHolder {
            TextView upParentText;

            public ParentViewHolder(View itemView) {
                super(itemView);
                upParentText= (TextView) itemView.findViewById(R.id.upParentText);
            }
        }
        class ChildViewHolder extends RecyclerView.ViewHolder {

            ImageView reUploadBtn;
            ImageViewRecyclable upChildImageVIew;
            TextView upStateText;

            public ChildViewHolder(View itemView) {
                super(itemView);


                reUploadBtn=(ImageView)itemView.findViewById(R.id.reUploadBtn);
                upChildImageVIew=(ImageViewRecyclable)itemView.findViewById(R.id.upChildImageVIew);
                upStateText=(TextView)itemView.findViewById(R.id.upStateText);

            }
        }
    }



}

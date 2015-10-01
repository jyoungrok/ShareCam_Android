package com.claude.sharecam.upload;

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;

import com.claude.sharecam.Constants;
import com.claude.sharecam.R;
import com.claude.sharecam.share.ShareItem;
import com.claude.sharecam.util.ImageManipulate;
import com.claude.sharecam.orm.UploadingPicture;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.Picture;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

/**
 * onCreate 에서 DB로 부터 upload 데이터들을 불러온다.
 * foreground로 서비스 실행
 * 서비스에서 업로드 관련 데이터를 관리한다. (처음에 서비스 생성 시 업로드 데이터를 불러오고 이후의 데이터 변화는 receiver를 통해 받아서 갱신)
 * UploadActivity는 UploadService의 instance를 통해 데이터에 접근하고 receiver를 통해 데이터의 변화 여부에 대해 알림을 받는다.
 */

public class UploadService extends IntentService {

    class UploadNumber {

        public UploadNumber(){
            numFailed=0;
            numSuccess=0;
            numUploading=0;
            numStandby=0;

        }
        int numUploading;//업로드 중 수
        int numFailed;//업로드 실패 개수
        int numSuccess;//업로드 성공 개수
        int numStandby;//업로드 대기 중 수

        public void updateNumber(int state)
        {
            switch(state)
            {
                case UploadingPicture.FAILED_UPLOADING_STATE:
                    numFailed++;
                    break;
                case UploadingPicture.STANDBY_UPLOADING_STATE:
                    numStandby++;
                    break;
                case UploadingPicture.UPLOADING_STATE:
                    numUploading++;
                    break;
                case UploadingPicture.SUCCESS_UPLOADING_STATE:
                    numSuccess++;
                    break;
            }

            Log.i(TAG,"update number standby="+numStandby+" uploading="+numUploading+" failed="+numFailed+" success="+numSuccess);
        }

        //state update 에 따른 각 state 개수 변경
        public void updateNumber(int prevState,int curState)
        {
            switch(prevState)
            {
                case UploadingPicture.FAILED_UPLOADING_STATE:
                    numFailed--;
                    break;
                case UploadingPicture.STANDBY_UPLOADING_STATE:
                    numStandby--;
                    break;
                case UploadingPicture.UPLOADING_STATE:
                    numUploading--;
                    break;
                case UploadingPicture.SUCCESS_UPLOADING_STATE:
                    numSuccess--;
                    break;
            }

            switch(curState)
            {
                case UploadingPicture.FAILED_UPLOADING_STATE:
                    numFailed++;
                    break;
                case UploadingPicture.STANDBY_UPLOADING_STATE:
                    numStandby++;
                    break;
                case UploadingPicture.UPLOADING_STATE:
                    numUploading++;
                    break;
                case UploadingPicture.SUCCESS_UPLOADING_STATE:
                    numSuccess++;
                    break;
            }
//            //업로드 -> 실패
//            if((prevState==UploadingPicture.STANDBY_UPLOADING_STATE || prevState==UploadingPicture.UPLOADING_STATE) && curState==UploadingPicture.FAILED_UPLOADING_STATE)
//            {
//                numUploading--;
//                numFailed++;
//            }
//            //업로드 -> 업로드 성공
//            else if((prevState==UploadingPicture.STANDBY_UPLOADING_STATE || prevState==UploadingPicture.UPLOADING_STATE) && curState==UploadingPicture.SUCCESS_UPLOADING_STATE)
//            {
//                numUploading--;
//                numSuccess++;
//            }
//            else if(prevState==UploadingPicture.FAILED_UPLOADING_STATE && (curState==UploadingPicture.UPLOADING_STATE || curState==UploadingPicture.STANDBY_UPLOADING_STATE))
//            {
//                numUploading++;
//                numFailed--;
//            }

            Log.i(TAG,"update number standby="+numStandby+" uploading="+numUploading+" failed="+numFailed+" success="+numSuccess);
        }

        public void deleteNumber(int state)
        {
            switch(state)
            {
                case UploadingPicture.FAILED_UPLOADING_STATE:
                    numFailed--;
                    break;
                case UploadingPicture.STANDBY_UPLOADING_STATE:
                    numStandby--;
                    break;
                case UploadingPicture.UPLOADING_STATE:
                    numUploading--;
                    break;
                case UploadingPicture.SUCCESS_UPLOADING_STATE:
                    numSuccess--;
                    break;
            }

            Log.i(TAG,"delete number standby="+numStandby+" uploading="+numUploading+" failed="+numFailed+" success="+numSuccess);
        }
    }
    UploadNumber uploadNumber;
    RemoteViews remoteViews;


    Notification notification;
    NotificationCompat.Builder builder;
    public static final String TAG="UploadService";
    ArrayList<UploadingPicture> beforeItems;//items before uploading success
    ArrayList<UploadingPicture> afterItems;//items after uploading success

    public static UploadService instance;


    public static final String MULTIPLE_UPLOAD_ACTION="com.claude.service.upload.action";//여러장 업로드 요청
    public static final String FINISH_SERVICE_ACTION="com.claude.service.upload.finish";
    public static final String UPLOAD_ACTION="com.claude.service.upload.action";  //업로드 요청
    public static final String RE_UPLOAD_ACTION="com.claude.service.reupload.action";//실패한 업로드 재 요청 시도

    //intent extra data
    public static final String PICTURE_LIST="pictureList";
    public static final String PICTURE="PICTURE";
    public static final String FILE_PATH="filePath";
    public static final String UPLOADING_PICTURE="uploadingPicture";
//    public static final String FILE_PATH="filePath";


    public static final String ID="id";
    public static final String STATE="state";
    public static final String PERCENT="percent";

    public static final String PICTURE_ID="pictureId";

    public static final String broadcastNewUploadingAction="com.claude.service.newupload";//새로운 uploading
    public static final String broadcastStateAction="com.claude.sharecam.service.state";//uploading picture의 state가 바뀜
    public static final String broadcastPercentAction="com.claude.sharecam.service.percent";

    private IntentFilter intentFilter;
//    final int UPLOAD_NOTIFICATION_ID=101;

    Context context;

//    public static ArrayList<UploadingPicture> beforeItems;//items before uploading success
//    public static ArrayList<UploadingPicture> afterItems;//items after uploading success

    public UploadService() {
        super("UploadService");
    }
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {



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
//                Log.d(TAG,"get broadcast state action state = "+state+"   id = "+id);
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
////                                uploadingAdapter.notifyDataSetChanged();
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
////                                uploadingAdapter.notifyDataSetChanged();
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
////                uploadingAdapter.notifyDataSetChanged();
//
//            }
//
//
//
//            Intent dataChangedBroadIntent=new Intent();
//            dataChangedBroadIntent.setAction(UploadActivity.broadcastUploadDataChanged);
//            context.sendBroadcast(dataChangedBroadIntent);
//        }
//    };
//
//    @Override
//    public void onCreate()
//    {
//        initItems();
//    }
//
//    //DB에서 데이터 불러와 설정
//    private void initItems()
//    {
//        beforeItems=new ArrayList<UploadingPicture>();
//        afterItems=new ArrayList<UploadingPicture>();
//
//        List<UploadingPicture> allItems=UploadingPicture.get_ne_finished_ob_createdAt(this);
//
//
//        if(allItems!=null) {
//            Log.d(TAG,"uploading num"+allItems.size());
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


    //    public static UploadService getInstance()
//    {
//
//        return instance;
//    }



    //DB에서 데이터 불러와 설정
    private void initItems()
    {
        uploadNumber=new UploadNumber();
        beforeItems=new ArrayList<UploadingPicture>();
        afterItems=new ArrayList<UploadingPicture>();

        List<UploadingPicture> allItems=UploadingPicture.get_ne_finished_ob_createdAt(this);


        if(allItems!=null) {
            Log.d(TAG,"uploading num"+allItems.size());
            for (int i = 0; i < allItems.size(); i++) {
                Log.d(TAG,"uploading id="+allItems.get(i).getId());
                //업로드 완료한 경우
                if (allItems.get(i).getState() == UploadingPicture.SUCCESS_UPLOADING_STATE) {
                    afterItems.add(allItems.get(i));
                }
                //업로드 완료 전 (실패, 대기, 업로드 중)
                else {

                    //업로드 중이었던 것은 업로드 실패로 변환 (서비스 실행 시 업로드 중인 것은 업로드 실패)
                    if(allItems.get(i).getState()==UploadingPicture.UPLOADING_STATE)
                        allItems.get(i).setState(UploadingPicture.FAILED_UPLOADING_STATE);

                    beforeItems.add(allItems.get(i));
                }

                //개수 업데이
                uploadNumber.updateNumber(allItems.get(i).getState());
            }
        }
    }

    private void createItem(String filePath,UploadingPicture uploadingPicture)
    {

//        Log.d(TAG,"createItem");
        int state=uploadingPicture.getState();
        int id=uploadingPicture.getId();
        Log.d(TAG,"create item state = "+uploadingPicture.getStateName(context)+"   id = "+id);

        UploadingPicture newBeforeItem;
        try {
            newBeforeItem=uploadingPicture.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            newBeforeItem=new UploadingPicture();
        }
//        newBeforeItem.setFilePath(filePath);
//        newBeforeItem.setState(state);
//        newBeforeItem.setId(id);

        beforeItems.add(0,newBeforeItem);
        uploadNumber.updateNumber(state);

        updateNotification();
    }

    //기존에 추가되어 있는 upload object의 state가 바뀐 경우
    private void updateItem(UploadingPicture uploadingPicture)
    {


        Log.d(TAG,"updateItem");
        int state = uploadingPicture.getState();
        int id=uploadingPicture.getId();
//        String pictureId=uploadingPicture.getPictureId();
        Log.d(TAG,"update item state = "+uploadingPicture.getStateName(context)+"   id = "+id);

        //아이템 업데이트
        //state 갯수 업데이트
        switch(state)
        {
            default:

                // -> 업로드 실패 / 업로드 중으로 상태 변경
            case UploadingPicture.FAILED_UPLOADING_STATE:
            case UploadingPicture.UPLOADING_STATE:
                for(int i=0; i<beforeItems.size(); i++)
                {
                    if(beforeItems.get(i).getId()==id)
                    {
                        uploadNumber.updateNumber(beforeItems.get(i).getState(),state);
                        try {
                            beforeItems.add(i,uploadingPicture.clone());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                        beforeItems.remove(i+1);
//                        beforeItems.get(i).setState(state);
//                        beforeItems.get(i).setPictureId(pictureId);
                        break;
                    }
                }
                break;

            // -> 업로드 성공
            case UploadingPicture.SUCCESS_UPLOADING_STATE:
                for(int i=0; i<beforeItems.size(); i++)
                {
                    if(beforeItems.get(i).getId()==id)
                    {
                        uploadNumber.updateNumber(beforeItems.get(i).getState(),state);
//                        beforeItems.get(i).setState(state);
//                        beforeItems.get(i).setPictureId(pictureId);
                        try {
                            afterItems.add(0,uploadingPicture.clone());
                        } catch (CloneNotSupportedException e) {
                            e.printStackTrace();
                        }
                        beforeItems.remove(i);
                        break;
                    }
                }
                break;
        }

        updateNotification();
    }

    public void deleteItem(UploadingPicture uploadingPicture)
    {
        //완료한 업로드 제거
        if(uploadingPicture.getState()==UploadingPicture.SUCCESS_UPLOADING_STATE)
        {
            Log.d(TAG,"delete finished item");
            uploadNumber.deleteNumber(uploadingPicture.getState());
            uploadingPicture.setState(UploadingPicture.FINISHED_UPLOADING_STATE);
            uploadingPicture.update(context);
            afterItems.remove(uploadingPicture);

        }
        updateNotification();
    }
    @Override
    public void onCreate()
    {
        super.onCreate();
        instance=this;
        initItems();
        builder = new NotificationCompat.Builder(this);
    }
    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }

    @Override
    protected void onHandleIntent(Intent intent) {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {


        context=this;
        Log.d(TAG,"startCommand");
        //처음 실행하는 경우 intentFilter 등록
        if(intentFilter==null)
        {
            intentFilter=new IntentFilter();
            intentFilter.addAction(broadcastStateAction);
            intentFilter.addAction(broadcastPercentAction);
            registerReceiver(broadcastReceiver, intentFilter);
        }

        startForeground(Constants.UPLOAD_NOTIFICATION_ID, getUploadNotification());

        if(intent==null)

        {
            Log.e(TAG,"intent is null");
            return START_STICKY;
        }
        //notificiation에서 service종료 버튼 누른 경우
        if(FINISH_SERVICE_ACTION.equals(intent.getAction()))
        {
            Log.d(TAG, "finish upload service");
            this.stopSelf();
//            if(Up)
        }
        else if(UPLOAD_ACTION.equals(intent.getAction()))
        {
            ShareItem shareItem = (ShareItem)intent.getSerializableExtra(PICTURE);
            final Picture picture= new Picture();
            picture.init();
            picture.itemToPicture(shareItem);
//                    itemToPicture((ShareItem) intent.getSerializableExtra(PICTURE));
            final String filePath=intent.getStringExtra(FILE_PATH);
//            Log.d(TAG,"upload friend length="+PICTURE.getFriendList().length());
            uploadPicture(picture, shareItem,filePath);

        }

//        else if(MULTIPLE_UPLOAD_ACTION.equals(intent.getAction()))
//        {
//            List<Picture> pictureList=(List<Picture>)intent.getSerializableExtra(PICTURE_LIST);
//
//        }

        else if(RE_UPLOAD_ACTION.equals(intent.getAction()))
        {
            final UploadingPicture uploadingPicture= (UploadingPicture) intent.getSerializableExtra(UPLOADING_PICTURE);
            final String filePath=intent.getStringExtra(FILE_PATH);
            reUploadPicture(uploadingPicture);
        }

        return START_STICKY;
    }

    //PictrueItem obejct -> Picture object
//    private Picture itemToPicture(ShareItem shareItem)
//    {
//        Picture PICTURE=new Picture();
//        PICTURE.setCreatedBy(ParseUser.getCurrentUser());
//        PICTURE.init();
//        for(int i=0; i< shareItem.shareUserList.size(); i++)
//        {
//            PICTURE.setUserId(shareItem.shareUserList.get(i));
//        }
//
//        for(int i=0; i< shareItem.sharePhoneList.size(); i++)
//        {
//            Log.d(TAG,"add sharephone "+shareItem.sharePhoneList.get(i));
//            PICTURE.setPhone(shareItem.sharePhoneList.get(i));
//        }
//
//        return PICTURE;
//    }
    //사진 파일 재전송 시도
    private void reUploadPicture(final UploadingPicture uploadingPicture)
    {
        uploadingPicture.updateStateAndSendBR(context,UploadingPicture.UPLOADING_STATE);
//        Log.d(TAG,"uploadPicture Id2 = "+uploadingPicture.getId());
        updateItem(uploadingPicture);


        ParseQuery<Picture> query = ParseQuery.getQuery(Picture.class);
        query.whereEqualTo("objectId",uploadingPicture.getPictureId());
        query.findInBackground(new FindCallback<Picture>() {
            @Override
            public void done(List<Picture> list, ParseException e) {
                if(e==null || list.size()!=0)
                {
                    uploadPictureFile(list.get(0), uploadingPicture);
                }
                else
                    handleFailedUploading(uploadingPicture,e);
            }
        });
    }


    //사진 전송 시도
    private void uploadPicture(final Picture picture, ShareItem shareItem,final String filePath)
    {
        Log.d(TAG,"upload PICTURE start");

//        Log.d(TAG,"upload friend length="+PICTURE.getFriendList().length());

        final UploadingPicture uploadingPicture=new UploadingPicture(filePath);
        uploadingPicture.setShareItem(shareItem);
        //업로드 object local에 저장 및 update item
//            uploadingPicture.create(this);
        uploadingPicture.createAndSendBR(this);
//            Log.d(TAG,"uploadPicture Id = "+uploadingPicture.getId());
        createItem(filePath, uploadingPicture);


        //object만 업로드(without 파일) (네트워크 연결 된 경우)
        picture.saveEventually(new SaveCallback() {

            @Override
            public void done(ParseException e) {
                if(e==null) {
                    Log.d(TAG, "has uploaded PICTURE object");
                   uploadPictureFile(picture,uploadingPicture);
                }
                else
                {
                    handleFailedUploading(uploadingPicture,e);
                }
            }
        });
    }

    //object업로드 완료 후 사진 파일 업로드
    private void uploadPictureFile(final Picture picture, final UploadingPicture uploadingPicture)
    {

        //전송중 local 저장
        uploadingPicture.setPictureId(picture.getObjectId());
        uploadingPicture.updateStateAndSendBR(context,UploadingPicture.UPLOADING_STATE);
//        Log.d(TAG,"uploadPicture Id2 = "+uploadingPicture.getId());
        updateItem(uploadingPicture);


        //전송 REMOTE 시도
        picture.setPhotoSynched();
        picture.setImage(new ParseFile(picture.getObjectId() + ".JPEG", ImageManipulate.convertImageToByte(context, uploadingPicture.getFilePath())));
        picture.saveInBackground(new SaveCallback() {
            @Override
            public void done(ParseException e) {

                if(e==null) {
                    Log.d(TAG, "has uploaded the file of PICTURE");
                    //전송완료 local 저장
                    uploadingPicture.updateStateAndSendBR(context, UploadingPicture.SUCCESS_UPLOADING_STATE);
                    updateItem(uploadingPicture);
//                            //전송 완료 broadcast
//                            uploadingPicture.sendStateBroadCast(context);
                    Log.d(TAG, "image upload success");

                    //사진 업로드 -> 업로드 관련 앨범 로컬에 생성
                    try {
                        picture.handleSend(context);
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
                else
                {
                    Log.e(TAG,"uploadPictureFile error");
                   handleFailedUploading(uploadingPicture,e);
                }
            }
        });
    }

    //업로드 실패 시
    private void handleFailedUploading(UploadingPicture uploadingPicture,ParseException e)
    {
        Log.e(TAG,"upload failed");
        ParseAPI.erroHandling(context, e);
        uploadingPicture.updateStateAndSendBR(context, UploadingPicture.FAILED_UPLOADING_STATE);
        updateItem(uploadingPicture);
    }
    @Override
    public void onDestroy()
    {
        unregisterReceiver(broadcastReceiver);
    }

    private Notification getUploadNotification() {

         remoteViews = new RemoteViews(getPackageName(),
                R.layout.upload_notification);

        builder.setSmallIcon(R.drawable.ic_launcher)
                .setContentTitle("uplaoding title")
                .setTicker("uploading pictures")
                .setWhen(System.currentTimeMillis());

        Intent startIntent = new Intent(getApplicationContext(),
                UploadActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(
                this, UploadActivity.SERVICE_REQUEST_CODE, startIntent, 0);
        builder.setContentIntent(contentIntent);
        builder.setContent(remoteViews);
        notification = builder.build();


        return notification;
    }

    //notification의 숫자들 갱신
    private void updateNotification()
    {
        Intent finishIntent = new Intent(context, UploadService.class);
        finishIntent.setAction(FINISH_SERVICE_ACTION);
        PendingIntent pendingIntent=PendingIntent.getService(context,0,finishIntent,0);

        remoteViews.setOnClickPendingIntent(R.id.finishUploadNotiBtn,pendingIntent);
        // 사진 업로드 중인 경우
        if(uploadNumber.numUploading>0) {

            remoteViews.setViewVisibility(R.id.uploadFailImg, View.GONE);
            remoteViews.setViewVisibility(R.id.uploadProgressBar,View.VISIBLE);
            remoteViews.setTextViewText(R.id.uploadNumText,String.valueOf(uploadNumber.numUploading));
            remoteViews.setTextViewText(R.id.uploadStateText,getString(R.string.notification_uploading_state));
        }

        //전송실패한 것이 있는 경우
        else if(uploadNumber.numFailed>0)
        {
            remoteViews.setViewVisibility(R.id.uploadFailImg, View.VISIBLE);
            remoteViews.setViewVisibility(R.id.uploadProgressBar,View.GONE);
            remoteViews.setTextViewText(R.id.uploadNumText,String.valueOf(uploadNumber.numFailed));
            remoteViews.setTextViewText(R.id.uploadStateText,getString(R.string.notification_upload_fail_state));
        }

        //전송 대기 중인 경우
        else if(uploadNumber.numStandby>0)
        {
            remoteViews.setViewVisibility(R.id.uploadFailImg, View.GONE);
            remoteViews.setViewVisibility(R.id.uploadProgressBar,View.VISIBLE);
            remoteViews.setTextViewText(R.id.uploadNumText,String.valueOf(uploadNumber.numStandby));
            remoteViews.setTextViewText(R.id.uploadStateText,getString(R.string.notification_upload_standby_state));
        }

        //모두 업로드 완료 된 경우 service 종료
        else
        {
//            remoteViews.setViewVisibility(R.id.uploadFailImg, View.GONE);
//            remoteViews.setViewVisibility(R.id.uploadProgressBar,View.GONE);
//            remoteViews.setTextViewText(R.id.uploadNumText,"");
//            remoteViews.setTextViewText(R.id.uploadStateText,getString(R.string.notification_upload_success_state));
            this.stopSelf();
            return;
        }



//        remoteViews.setTextViewText(R.id.uploadingNumText,String.valueOf(uploadNumber.numUploading));
//       remoteViews.setTextViewText(R.id.uploadingNumText,String.valueOf(uploadNumber.numUploading));
//        remoteViews.setTextViewText(R.id.successNumText,String.valueOf(uploadNumber.numSuccess));
//        remoteViews.setTextViewText(R.id.failNumText,String.valueOf(uploadNumber.numFailed));

        NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(Constants.UPLOAD_NOTIFICATION_ID, notification);

//        synchronized (notification)
//        {
//            notification.notify();
//        }


    }

}

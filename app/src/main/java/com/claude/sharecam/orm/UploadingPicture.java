package com.claude.sharecam.orm;

import android.content.Context;
import android.content.Intent;

import com.claude.sharecam.R;
import com.claude.sharecam.Util;
import com.claude.sharecam.share.ShareItem;
import com.claude.sharecam.upload.UploadService;
import com.j256.ormlite.field.DataType;
import com.j256.ormlite.field.DatabaseField;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.DatabaseTable;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

/**
 * Created by Claude on 15. 7. 12..
 */
@DatabaseTable(tableName = "UploadingPicture")
public class UploadingPicture implements Serializable,Cloneable {

    public UploadingPicture clone() throws CloneNotSupportedException {
        UploadingPicture a = (UploadingPicture)super.clone();
        return a;
    }

    //upload
    public static final int FAILED_UPLOADING_STATE=-1;
    public static final int STANDBY_UPLOADING_STATE=0;
    public static final int UPLOADING_STATE=1;
    public static final int SUCCESS_UPLOADING_STATE=2;
    public static final int FINISHED_UPLOADING_STATE=3;//업로드 완료 한 후 사용자가 지운 경우

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField
    String filePath;
    @DatabaseField
    int state;
//    @DatabaseField
//    int percent;
    @DatabaseField
    String pictureId;

    @DatabaseField(dataType = DataType.SERIALIZABLE)
    ShareItem shareItem;

    @DatabaseField(columnName = "createdAt", dataType = DataType.DATE_STRING,
            format = "yyyy-MM-dd HH:mm:ss")
    Date createdAt;

    public UploadingPicture()
    {

    }

    public UploadingPicture(String filePath)
    {
        this.filePath=filePath;
        state=STANDBY_UPLOADING_STATE;
//        percent=0;
    }

    //현재 시간 설정
    public void setCurrentDate()
    {
        Calendar calendar = Calendar.getInstance();
        java.util.Date date = calendar.getTime();
        this.createdAt=date;

    }





    public void setId(int id){this.id=id;}
    public void  setPictureId(String pictureId)
    {
        this.pictureId=pictureId;
    }

    public void setState(int state)
    {
        this.state=state;
    }
    public void setShareItem(ShareItem shareItem){this.shareItem=shareItem;}

    public void setFilePath(String filePath) {this.filePath=filePath;}

    public String getPictureId()
    {
        return pictureId;
    }
    public String getFilePath() {return filePath;}
    public int getId()
    {
        return id;
    }
    public int getState()
    {
        return state;
    }

    public ShareItem getShareItem(){return shareItem;}
    public  String getStateName(Context context)
    {
        switch (state)
        {
            default:
            case FAILED_UPLOADING_STATE:
                return context.getString(R.string.failed_uploading_state);
            case STANDBY_UPLOADING_STATE:
                return context.getString(R.string.standby_uploading_state);
            case UPLOADING_STATE:
                return context.getString(R.string.uploading_state);
            case SUCCESS_UPLOADING_STATE:
                return context.getString(R.string.success_uploading_state);
            case FINISHED_UPLOADING_STATE:
                return context.getString(R.string.finished_uploading_state);
        }
    }

//    public int getPercent()
//    {
//        return percent;
//    }



    public void sendNewUploadingBroadCast(Context context)
    {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(UploadService.broadcastNewUploadingAction);

        broadcastIntent.putExtra(UploadService.FILE_PATH,filePath);
        broadcastIntent.putExtra(UploadService.ID,id);
        broadcastIntent.putExtra(UploadService.STATE,state);
        context.sendBroadcast(broadcastIntent);
    }



    //state에 대한 broadcast 보내기
    public void sendStateBroadCast(Context context)
    {
        Intent broadcastIntent = new Intent();
        broadcastIntent.setAction(UploadService.broadcastStateAction);
        broadcastIntent.putExtra(UploadService.FILE_PATH,filePath);
        broadcastIntent.putExtra(UploadService.PICTURE_ID,pictureId);
        broadcastIntent.putExtra(UploadService.ID,id);
        broadcastIntent.putExtra(UploadService.STATE,state);
        context.sendBroadcast(broadcastIntent);
    }

    public void create(Context context)
    {
        setCurrentDate();
        try {
            ((Util)context.getApplicationContext()).dbHelper.getUploadingPictureDao().create(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void update(Context context)
    {
        try {
            ((Util)context.getApplicationContext()).dbHelper.getUploadingPictureDao().update(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void delete(Context context)
    {
        try {
            ((Util)context.getApplicationContext()).dbHelper.getUploadingPictureDao().delete(this);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

//    public void updateState(Context context,int state)
//    {
//        setState(state);
//        update(context);
//    }

    //새로운 uploading PICTURE db에 저장하고 broadcast
    public void createAndSendBR(Context context)
    {
        create(context);
        sendNewUploadingBroadCast(context);
    }
    //state update하고 broadcast
    public void updateStateAndSendBR(Context context,int state)
    {
        setState(state);
        update(context);
        sendStateBroadCast(context);
    }

    //state가 finished인 uploading picture응 제외하고 모두 불러옴
    //get not equal finished state ordered by createdAt
    public static List<UploadingPicture> get_ne_finished_ob_createdAt(Context context)
    {
        QueryBuilder<UploadingPicture,Long> queryBuilder=null;
        try {
            queryBuilder=((Util)context.getApplicationContext()).dbHelper.getUploadingPictureDao().queryBuilder();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(queryBuilder!=null)
        {
            try {
                queryBuilder.orderBy("createdAt",false);
                return queryBuilder.where().ne("state",FINISHED_UPLOADING_STATE).query();
//                return ((Util)context.getApplicationContext()).dbHelper.getUploadingPictureDao().queryForAll();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return null;
    }

}

package com.claude.sharecam.download;

import android.content.Context;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;

import com.claude.sharecam.parse.Picture;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * Created by Claude on 15. 9. 29..
 */
public class DownloadThread extends Thread {

    public static final String TAG="DownloadThread";

    Queue<Picture> autoDownloadQueue;//자동 다운로드 항목들
    ArrayList<Picture> autoSuccessList;//자동 다운로드 성공 항목들
    ArrayList<Picture> autoFailedList;//자동 다운로드 실패 항목들


    Queue<Picture> manuallyDownloadQueue;//수동 다운로드 항목들
    ArrayList<Picture> manualSuccessList;//수동 다운로드 성공 항목들
    ArrayList<Picture> manualFailedList;//수동 다운로드 실패 항목들
    Context context;
    DownloadHandler handler;

    public DownloadThread(Context context, DownloadHandler handler){


        initAutoDownload();
        initManualDownload();
        this.context=context;
        this.handler=handler;
    }

    public void initManualDownload()
    {
        manuallyDownloadQueue =new LinkedList<Picture>();
        manualSuccessList =new ArrayList<Picture>();
        manualFailedList =new ArrayList<Picture>();
    }

    public void initAutoDownload()
    {
        autoDownloadQueue=new LinkedList<Picture>();
    }

    public void addManualDownloadPicture(List<Picture> pictureList)
    {
        for(int i=0; i<pictureList.size(); i++)
        {
            manuallyDownloadQueue.add(pictureList.get(i));
        }

    }

    public void setHandler(DownloadHandler downloadHandler)
    {
        this.handler=downloadHandler;
    }

    public void addManualDownloadPicture(Picture picture)
    {
        manuallyDownloadQueue.add(picture);
    }

    @Override
    public void run() {
        super.run();

        Log.d(TAG, "run download Thread" );
        //다운로드 수행
        //수동 다운로드 -> 자동 다운로드
        while(!isInterrupted() && manuallyDownloadQueue !=null  && autoDownloadQueue!=null && (manuallyDownloadQueue.size()>0 || autoDownloadQueue.size()>0))
        {
            Picture picture;
            //수동 다운로드 진행
            if(manuallyDownloadQueue.size()>0) {
                picture = manuallyDownloadQueue.poll();
                Log.d(TAG,"download manually picture "+picture.getObjectId());
                if(picture.downloadPicture(context))
                    manualSuccessList.add(picture);
                else
                    manualFailedList.add(picture);

                //수동 다운로드 완료
                if(manuallyDownloadQueue.size()==0)
                {
                    //다운로드 완료 알림
                    Bundle args=new Bundle();
                    args.putInt(DownloadHandler.DOWNLOAD_TYPE,DownloadHandler.MANUAL_DOWNLOAD_DONE);
                    args.putInt(DownloadHandler.SUCCESS_DONWLOAD_NUM, manualSuccessList.size());
                    args.putInt(DownloadHandler.FAIL_DOWNLOAD_NUM, manualFailedList.size());
                    Message message=new Message();
                    message.setData(args);
                    handler.sendMessage(message);

                    initManualDownload();
                }

            }

            //자동 다운로드 진행
            else if(autoDownloadQueue.size()>0)
            {
                picture = autoDownloadQueue.poll();
            }

            else
            {
                return;
            }



        }

        //다운로드 중간에 중지 된 경우
        if(isInterrupted())
        {

        }

        //다운로드 시도가 모두 끝난 경우
        else if(manuallyDownloadQueue.size()==0)
        {

        }
    }






}
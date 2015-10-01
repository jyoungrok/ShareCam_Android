package com.claude.sharecam.download;

import android.content.Context;

import com.claude.sharecam.parse.Picture;

import java.util.List;

/**
 *
 *  downloadThread이 Singleton 방식으로 Queue(수동, 자동 다운로드를 위한 2개의 queue)에서 다운로드 하고자 하는 Picture
 *
 *  즉, Queue에 다운로드 하고자 하는 picture를 넣은 후 Thread 생성하면 사진 다운로드
 *
 */
public class Download {

    public static DownloadThread downloadThread;
//    public static DownloadHandler downloadHandler;

    public static void manuallyDownloadPicture(Context context,List<Picture> pictureList,DownloadHandler downloadHandler)
    {
        DownloadThread downloadThread=getManuallyDownloadThread(context,downloadHandler);
        downloadThread.addManualDownloadPicture(pictureList);
        if(!downloadThread.isAlive())
        {
            downloadThread.start();
        }
    }

    public static DownloadThread getManuallyDownloadThread(Context context,DownloadHandler downloadHandler)
    {
        if(downloadThread ==null)
        {
            downloadThread =new DownloadThread(context,downloadHandler);
        }
        else{
            downloadThread.setHandler(downloadHandler);
        }
        return downloadThread;
    }
/*
    public static DownloadHandler getDownloadHandler()
    {
        if(downloadHandler ==null)
        {
            downloadHandler=new DownloadHandler();
//            downloadThread =new DownloadThread(context,downloadHandler);
        }
        return downloadHandler;
    }
    */
}

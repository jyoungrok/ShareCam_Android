package com.claude.sharecam.download;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.claude.sharecam.parse.Picture;

import java.util.Queue;

/**
 * 다운로드 관련된 작업을 컨트롤 하는 Thread
 * 1. Auto Download
 * 2. Manually Download
 *
 */
public class DownloadService extends Service {


    //Intent action
    public static final String DOWNLOAD_ACTION="com.claude.service.download.action";//여러장 업로드 요청



    AutoDownloadThread autoDownloadThread;//자동 다운로드 용 쓰레드
    DownloadThread downloadThread;//수동 다운로드 용 쓰레드

    Queue<Picture> autoDownQueue;//자동 다운로드 항목들


    public DownloadService() {
    }


    @Override
    public void onCreate()
    {
        super.onCreate();
    }
    @Override
    public IBinder onBind(Intent intent) {
        throw null;
    }



    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        return START_STICKY;
    }


    public class AutoDownloadThread extends Thread {
        @Override
        public void run() {
            super.run();
        }
    }



}

package com.claude.sharecam.download;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/**
 * Created by Claude on 15. 9. 29..
 */
public class DownloadHandler extends Handler {
    public static final String TAG="DownloadHandler";
    //bundle arg string
    public static final String DOWNLOAD_TYPE="downloadType";//수동 다운로드 / 자동 다운로드
    public static final String SUCCESS_DONWLOAD_NUM="successDownloadNum";//다운로드 성공 개수
    public static final String FAIL_DOWNLOAD_NUM="failDownloadNum";//다운로드 실패 개수

    //bundle arg value
    public static final int MANUAL_DOWNLOAD_DONE =0;
    public static final int MANUAL_DOWNLOAD_STOP=1;
    public static final int AUTO_DOWNLOAD_DONE =2;
    public static final int AUTO_DOWNLOAD_STOP=3;

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        Bundle args=msg.getData();

        int downloadType=args.getInt(DOWNLOAD_TYPE);
        int successNum=args.getInt(SUCCESS_DONWLOAD_NUM);
        int failNum=args.getInt(FAIL_DOWNLOAD_NUM);
        switch (downloadType)
        {
            case MANUAL_DOWNLOAD_DONE:
                Log.d(TAG, "manual download done");
                manualDownloadDoneHandler(successNum,failNum);
                break;
            case MANUAL_DOWNLOAD_STOP:
                Log.d(TAG, "manual download stop");
                manualDownloadStopHandler(successNum,failNum);
                break;
            case AUTO_DOWNLOAD_DONE:
                Log.d(TAG, "auto download done");
                break;
            case AUTO_DOWNLOAD_STOP:
                Log.d(TAG, "auto download stop");
                break;
        }
    }


    public void manualDownloadDoneHandler(int successNum,int failNum)
    {
        Log.d(TAG, "manualDownloadDoneHandler");

    }

    public void manualDownloadStopHandler(int successNum,int failNum)
    {
        Log.d(TAG, "manualDownloadStopHandler");
    }

}

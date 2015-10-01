package com.claude.sharecam.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;

import com.claude.sharecam.Util;
import com.claude.sharecam.download.AutoDownload;
import com.claude.sharecam.util.Connectivity;

/**
 * 네트워크 환경이 바뀔 때 호출
 */
public class NetworkChangeReceiver extends BroadcastReceiver {

    public static final String networkInfo="networkInfo";
    public static final String TAG="NetworkChangeReceiver";
    public static Thread autoDownloadThread;

    @Override
    public void onReceive(final Context context, final Intent intent) {

        Log.d(TAG,TAG);
//
//        Log.v(TAG, "action: " + intent.getAction());
//        Log.v(TAG, "component: " + intent.getComponent());
        Bundle extras = intent.getExtras();
//        if (extras != null) {
//            for (String key: extras.keySet()) {
//                Log.v(TAG, "key [" + key + "]: " +
//                        extras.get(key));
//            }
//        }
//        else {
//            Log.v(TAG, "no extras");
//        }

        if(Connectivity.isConnected(context) && ((NetworkInfo)extras.get(networkInfo)).isConnected())
        {
            int autoDownloadMode=Util.getAutoDownloadMode(context);
//            Connectivity.getNetworkInfo(context).getType()
            if(Connectivity.isConnectedWifi(context) )
            {
                Log.d(TAG,"wifi connected and downalod picture");
//                AutoDownload.downloadPictureIfNeededInBackground(context, autoDownloadThread);
            }
            else if(Connectivity.isConnectedMobile(context)){
//                AutoDownload.downloadPictureIfNeededInBackground(context, autoDownloadThread);
                Log.d(TAG,"3g/4g connected and downalod picture");
            }
        }

    }
}

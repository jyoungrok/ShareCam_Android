package com.claude.sharecam.download;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.claude.sharecam.Constants;
import com.claude.sharecam.Util;
import com.claude.sharecam.parse.Album;
import com.claude.sharecam.parse.ParseAPI;
import com.claude.sharecam.parse.Picture;
import com.claude.sharecam.util.Connectivity;

import java.util.List;

/**
 *  다음 2가지 사항에 대하여 자동 다운로드 시행 (자동 다운로드 설정되어 있는 경우만!!!!!!!)
 *  1. 사진 Notification 알람 받은 경우 -> 해당 사진 다운로드
 *  2. Network State가 변한 경우 -> 아직 다운 받지 않은 사진 다운로드
 *
 *  사진 다운로드 실패시 -> Notification 알림
 *
 *
 */
public class AutoDownload {

    public static final String TAG="AutoDownload";



    /**
     *  네트워크 상태와 현재 자동 저장 모드 설정에 따라 자동 저장 필요 여부 확인
     */
    public static boolean checkAutoDownloadAvailable(Context context)
    {
        if(Util.getAutoDownloadSet(context)) {
            int autoDownloadMode = Util.getAutoDownloadMode(context);

            if (Connectivity.isConnectedWifi(context) && (autoDownloadMode == Constants.PREF_AUTO_SAVE_ONLY_WIFI || autoDownloadMode == Constants.PREF_AUTO_SAVE_WHENEVER)) {
                return true;
            } else if (Connectivity.isConnectedMobile(context) && autoDownloadMode == Constants.PREF_AUTO_SAVE_WHENEVER) {
                return true;
            }
        }

        return false;
    }


    /**
     * ( Network state가 변했을 때 호출 됨 )
     * 자동 다운로드가 설정 되어 있는 경우
     * 다운로드 해야할 사진들 찾아서 다운로드
     *
     *  sync버전으로 다운로드 순차적 수행행
     *      Double check !!!
     *      1. 처음 자동 다운로드 설정 여부 확인 후 필요 사진 모두 불러온다.
     *      2. 다운로드 직전에 현재 네트워크 상태 및 해당 사진의 자동 다운로드 설정 다시 확인(1개씩)
    */
    public static void downloadPictureIfNeededInBackground(final Context context)
    {

        Log.d(TAG,"downlaodPictureIfNeeded called by network state change");

        //다운로드 를 위한 thread 생성
        new Thread(){
            @Override
            public void run() {
                super.run();
                //자동 다운로드가 설정 되어있는 경우
                if(checkAutoDownloadAvailable(context))
                {
                    List<Picture> list= ParseAPI.findAutoDownloadPictures();
                    if(list!=null)
                    {
                        for(int i=0; i<list.size(); i++)
                        {
                            if(checkAutoDownloadAvailable(context))
                            {
                                //앨범에서 자동 다운로드 설정 되어 있는지 다시 한번 확인 (중간에 자동 다운로드 설정 끈 경우를 위해)
                                if(ParseAPI.findAlbumById(list.get(i).getAlbum().getObjectId()).getAutoDownload()) {
                                    //다운로드 성공
                                    if (list.get(i).downloadPicture(context)) {
                                        Log.d(TAG, "download picture done " + list.get(i).getObjectId());
                                    }
                                    //다운로드 실패
                                    else {
                                        Log.d(TAG, "download picture fail " + list.get(i).getObjectId());
                                    }
                                }
                            }
                        }
                    }

                }
            }
        }.start();

        /*
        //자동 다운로드가 설정 되어있는 경우
        if(checkAutoDownloadAvailable(context))
        {
            //다운로드 하지 않은 사진들 불러옴
            ParseAPI.findAutoDownloadPicturesInBackground(new FindCallback<Picture>() {
                @Override
                public void done(List<Picture> list, ParseException e) {
                    if(e==null)
                    {

                        Log.d(TAG,"download picture "+list.size());
                        for(int i=0; i<list.size(); i++)
                        {
                            if(checkAutoDownloadAvailable(context)) {
                                //사진 다운로드 요창
                                downloadPictureInBackground(context, list.get(i));
                            }
                        }
                    }
                    else{
                        ParseAPI.erroHandling(context, e);
                    }
                }
            });
        }


        thread.start();
        //자동 다운로드가 설정 되어있는 경우
        if(checkAutoDownloadAvailable(context))
        {
            //다운로드 하지 않은 사진들 불러옴
            ParseAPI.findAutoDownloadPicturesInBackground(new FindCallback<Picture>() {
                @Override
                public void done(List<Picture> list, ParseException e) {
                    if(e==null)
                    {
                        Log.d(TAG,"download picture "+list.size());
                        for(int i=0; i<list.size(); i++)
                        {
                            if(checkAutoDownloadAvailable(context)) {
                                //사진 다운로드 요창
                                downloadPictureInBackground(context, list.get(i));
                            }
                        }
                    }
                    else{
                        ParseAPI.erroHandling(context, e);
                    }
                }
            });
        }*/
    }
    /**
     * ( Notification 으로 사진 받았을 때 호출 됨)
     * 자동저장 설정 되어있는 경우 -> 다운로드 -> 실패 시 재시도
     */
    public static void downloadPictureIfNeededInBackground(final Context context, final Picture picture, Album album)
    {
        Log.d(TAG,"downloadPictureIfNeededInBackground");
//        boolean autoDownload=false;
        //기본 자동 다운로드 설정 && 자동 다운로드 설정 앨범인 경우
        if(checkAutoDownloadAvailable(context) && album.getAutoDownload())
        {
            switch(Util.getAutoDownloadMode(context))
            {
                //와이 파이 설정일 때만 자동 다운로드
                case Constants.PREF_AUTO_SAVE_ONLY_WIFI:
                    if(Connectivity.isConnectedWifi(context))
                    {
                        downloadPictureInBackground(context, picture);
                    }
                    break;
                //언제나...
                case Constants.PREF_AUTO_SAVE_WHENEVER:
                    if(Connectivity.isConnected(context))
                    {
                        downloadPictureInBackground(context, picture);
                    }
                    break;
            }
        }
    }


    /**
     * downloadPictureIfNeeded에 의해 호출됨
     * 다운로드 요청
     */
    private static void downloadPictureInBackground(final Context context, final Picture picture)
    {
        Log.d(TAG,"downloadPictureInBackground");
        picture.downloadPictureInBackground(context, new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                if (msg.what == Picture.SUCCESS_DOWNLOAD) {
//                    picture.setSaved(true);
//                    picture.pinInBackground(ParseAPI.LABEL_PICTURE);
                    Log.d(TAG, "success auto download " + picture.getObjectId());
                } else if (msg.what == Picture.FAIL_DOWNLOAD) {
                    Log.d(TAG, "fail auto download " + picture.getObjectId());
//                    downloadPictureIfNeededInBackground(context,picture);
                }
            }
        });
    }


}



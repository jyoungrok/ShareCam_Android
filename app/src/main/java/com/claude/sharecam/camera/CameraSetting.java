package com.claude.sharecam.camera;

import android.content.Context;

import com.claude.sharecam.Constants;
import com.claude.sharecam.Util;

/**
 * 카메라 촬영 시 필요한 세팅들을 저장
 */
public class CameraSetting {


    public int contCount;
    public boolean takingTimerPicture;//사진 촬영 중인지 여부


    //pref
    public boolean cameraFront;//전면 후면 카메라 실행 여부
    public boolean oneTouch;//원터치 카메라 설정 여부
    public int timerCount;//설정된 타이머 카운트
    public boolean gridSet;//그리드 설정 여부
    public String  flash;//플래시 모드
    public int ratio;//사진 촬영 비율
    public int cont;//연속촬영 값
    public int mode;//카메라 촬영 모드

    Util util;
    public CameraSetting(Context context)
    {
        util=((Util)context.getApplicationContext());
        cameraFront=util.pref.getBoolean(Constants.PREF_CAMERA_FRONT,Constants.PREF_CAMERA_FRONT_DEFAULT);
        oneTouch=util.pref.getBoolean(Constants.PREF_ONE_TOUCH,Constants.PREF_ONE_TOUCH_DEFAULT);
        timerCount=util.pref.getInt(Constants.PREF_TIMER, Constants.PREF_TIMER_DEFAULT);
        gridSet=util.pref.getBoolean(Constants.PREF_GRID, Constants.PREF_GRID_DEFAULT);
        flash=util.pref.getString(Constants.PREF_FLASH, Constants.PREF_FLASH_DEFAULT);
        ratio=util.pref.getInt(Constants.PREF_CAMERA_RATIO, Constants.PREF_RATIO_DEFAULT);
        cont=util.pref.getInt(Constants.PREF_CAMERA_CONT, Constants.PREF_CONT_DEFAULT);
        mode=util.pref.getInt(Constants.PREF_CAMERA_MODE,Constants.PREF_CAMERA_MODE_DEFAULT);
        takingTimerPicture=false;
    }

    public void setCameraFront(boolean cameraFront)
    {
        util.pref.edit().putBoolean(Constants.PREF_CAMERA_FRONT,cameraFront).commit();
        this.cameraFront=cameraFront;
    }

    public void  setOneTouch(boolean oneTouch)
    {
        util.pref.edit().putBoolean(Constants.PREF_ONE_TOUCH,oneTouch).commit();
        this.oneTouch=oneTouch;
    }

    public void setTimerCount(int timerCount)
    {
        util.pref.edit().putInt(Constants.PREF_TIMER, timerCount).commit();
        this.timerCount=timerCount;
    }

    public void setGridSet(boolean gridSet)
    {
        util.pref.edit().putBoolean(Constants.PREF_GRID, gridSet).commit();
        this.gridSet=gridSet;
    }

    public void setFlash(String flash)
    {
        this.flash=flash;
        util.pref.edit().putString(Constants.PREF_FLASH,flash).commit();
    }

    public void setRatio(int ratio)
    {
        this.ratio=ratio;
        util.pref.edit().putInt(Constants.PREF_CAMERA_RATIO, ratio).commit();
    }
    public void setCont(int cont)
    {
        this.cont=cont;
        util.pref.edit().putInt(Constants.PREF_CAMERA_CONT,cont).commit();
    }

    public void setMode(int mode)
    {
        this.mode=mode;
        util.pref.edit().putInt(Constants.PREF_CAMERA_MODE,mode).commit();
    }

}

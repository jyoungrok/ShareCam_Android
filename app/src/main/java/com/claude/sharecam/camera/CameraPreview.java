package com.claude.sharecam.camera;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.claude.sharecam.Constants;

public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {

    private static final double ASPECT_RATIO_11=1.0 / 1.0;
    private static final double ASPECT_RATIO_34 = 3.0 / 4.0;
    private static final int PICTURE_SIZE_MAX_WIDTH = 1280;
    private static final int PREVIEW_SIZE_MAX_WIDTH = 640;

    private SurfaceHolder mHolder;
    private Camera mCamera;
    private CameraSetting cameraSetting;

    public CameraPreview(Context context, Camera camera,CameraSetting cameraSetting) {
        super(context);
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        this.cameraSetting=cameraSetting;

    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            // create the surface and start camera preview
            if (mCamera == null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void refreshCamera(Camera camera) {
        if (mHolder.getSurface() == null) {
            // preview surface does not exist
            return;
        }
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e) {
            // ignore: tried to stop a non-existent preview
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings

        setCamera(camera);
        setupCamera();
        try {
            mCamera.setDisplayOrientation(90);
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.
        refreshCamera(mCamera);
    }

    public void setCamera(Camera camera) {
        //method to set a camera instance
        mCamera = camera;
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // TODO Auto-generated method stub
        // mCamera.release();

    }


    /**
     * Measure the view and its content to determine the measured width and the
     * measured height.
     */
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);


        if (width > height * ASPECT_RATIO_34) {
            width = (int) (height * ASPECT_RATIO_34 + .5);
        } else {
            height = (int) (width / ASPECT_RATIO_34 + .5);
        }


        setMeasuredDimension(width, height);
    }

    /**
     * Setup the camera parameters.
     */
    public void setupCamera() {
        Camera.Parameters parameters = mCamera.getParameters();

        Camera.Size bestPreviewSize = determineBestPreviewSize(parameters);
        Camera.Size bestPictureSize = determineBestPictureSize(parameters);

        parameters.setPreviewSize(bestPreviewSize.width, bestPreviewSize.height);
        parameters.setPictureSize(bestPictureSize.width, bestPictureSize.height);

//        parameters.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);

        mCamera.setParameters(parameters);
    }

    private Camera.Size determineBestPreviewSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPreviewSizes();

        return determineBestSize(sizes, PREVIEW_SIZE_MAX_WIDTH);
    }

    private Camera.Size determineBestPictureSize(Camera.Parameters parameters) {
        List<Camera.Size> sizes = parameters.getSupportedPictureSizes();

        return determineBestSize(sizes, PICTURE_SIZE_MAX_WIDTH);
    }

    protected Camera.Size determineBestSize(List<Camera.Size> sizes, int widthThreshold) {
        Camera.Size bestSize = null;


        for (Camera.Size currentSize : sizes) {

            boolean isDesiredRatio = (currentSize.width / 4) == (currentSize.height / 3);
            boolean isBetterSize = (bestSize == null || currentSize.width > bestSize.width);
            boolean isInBounds = currentSize.width <= PICTURE_SIZE_MAX_WIDTH;

            if (isDesiredRatio && isInBounds && isBetterSize) {
                bestSize = currentSize;
            }

//                else if(cameraSetting.ratio==Constants.PREF_RATIO_11){
//                    boolean isDesiredRatio = currentSize.width== currentSize.height;
//                    boolean isBetterSize = (bestSize == null || currentSize.width > bestSize.width);
//                    boolean isInBounds = currentSize.width <= PICTURE_SIZE_MAX_WIDTH;
//
//                    if (isDesiredRatio && isInBounds && isBetterSize) {
//                        bestSize = currentSize;
//                    }
//                }

        }

        if (bestSize == null) {


            return sizes.get(0);
        }


        return bestSize;
    }
}
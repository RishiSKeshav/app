package com.rishi.app.app;

import java.io.IOException;
import java.util.List;

import android.content.Context;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.os.Environment;
import android.content.pm.PackageManager;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.hardware.Camera;
import android.hardware.Camera.CameraInfo;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;


public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder mHolder;
    private Camera mCamera;
    private Context mContext;

    private SurfaceHolder anotherHolder;

    public CameraPreview(Context context, Camera camera) {
        super(context);

        mContext=context;
        mCamera = camera;
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {

        Camera.Parameters param;
        param = mCamera.getParameters();

        //Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        int rotation = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int orientation = 0;
        Camera.CameraInfo info = new Camera.CameraInfo();
        mCamera.getCameraInfo(0, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            orientation = (info.orientation - degrees + 360) % 360;

            /*orientation = (info.orientation + degrees) % 360;
            orientation = (360 - orientation) % 360;  // compensate the mirror*/
        } else {  // back-facing
            orientation = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(orientation);

        List<String> supportedFocusModes = param.getSupportedFocusModes();

        if (supportedFocusModes.contains(param.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            param.setFocusMode(param.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (supportedFocusModes.contains(param.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            param.setFocusMode(param.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        List<String> supportedFlashModes = param.getSupportedFlashModes();
        if (supportedFlashModes != null && supportedFlashModes.contains(param.FLASH_MODE_AUTO)) {
            param.setFlashMode(param.FLASH_MODE_AUTO);
        }

        /*if (param.getMaxNumFocusAreas() > 0) {
            this.focusAreaSupported = true;
        }

        if (p.getMaxNumMeteringAreas() > 0) {
            this.meteringAreaSupported = true;
        }*/

        mCamera.setParameters(param);

        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (Exception e) {
            // check for exceptions
            //System.err.println(e);
            return;
        }
        /*try {
            // create the surface and start camera preview
            if (mCamera == null) {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            }
        } catch (IOException e) {
            Log.d(VIEW_LOG_TAG, "Error setting camera preview: " + e.getMessage());
        }*/
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

        Camera.Parameters param;
        param = mCamera.getParameters();

        //Display display = ((WindowManager)getSystemService(WINDOW_SERVICE)).getDefaultDisplay();

        int rotation = ((WindowManager)mContext.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }

        int orientation = 0;
        Camera.CameraInfo info = new Camera.CameraInfo();
        mCamera.getCameraInfo(0, info);
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            //orientation = (info.orientation - degrees + 360) % 360;

            orientation = (info.orientation + degrees) % 360;
            orientation = (360 - orientation) % 360;  // compensate the mirror
        } else {  // back-facing
            orientation = (info.orientation - degrees + 360) % 360;
        }
        mCamera.setDisplayOrientation(orientation);

        List<String> supportedFocusModes = param.getSupportedFocusModes();

        if (supportedFocusModes.contains(param.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            param.setFocusMode(param.FOCUS_MODE_CONTINUOUS_PICTURE);
        } else if (supportedFocusModes.contains(param.FOCUS_MODE_CONTINUOUS_VIDEO)) {
            param.setFocusMode(param.FOCUS_MODE_CONTINUOUS_VIDEO);
        }

        List<String> supportedFlashModes = param.getSupportedFlashModes();
        if (supportedFlashModes != null && supportedFlashModes.contains(param.FLASH_MODE_AUTO)) {
            param.setFlashMode(param.FLASH_MODE_AUTO);
        }

        /*if (param.getMaxNumFocusAreas() > 0) {
            this.focusAreaSupported = true;
        }

        if (p.getMaxNumMeteringAreas() > 0) {
            this.meteringAreaSupported = true;
        }*/

        mCamera.setParameters(param);

        try {
            // The Surface has been created, now tell the camera where to draw
            // the preview.
            mCamera.setPreviewDisplay(anotherHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            // check for exceptions
            //System.err.println(e);
            return;
        }


        /*try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        } catch (Exception e) {
            Log.d(VIEW_LOG_TAG, "Error starting camera preview: " + e.getMessage());
        }*/
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {
        // If your preview can change or rotate, take care of those events here.
        // Make sure to stop the preview before resizing or reformatting it.

        anotherHolder = holder;

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
}
package com.mathilde.customcam.camera;

import android.content.Context;
import android.content.res.Configuration;
import android.hardware.Camera;
import android.os.Build;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.mathilde.customcam.R;

import java.io.IOException;

/**
 * Created by gui on 24/07/2014.
 */
public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
    private static final String TAG = "CameraPreview";

    private SurfaceHolder mHolder;
    private Camera mCamera;

    public CameraPreview(Context context, Camera camera) {
        super(context);
        mCamera = camera;

        // Install a SurfaceHolder.Callback so we get notified when the
        // underlying surface is created and destroyed.
        mHolder = getHolder();
        mHolder.addCallback(this);
        // deprecated setting, but required on Android versions prior to 3.0
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB)mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
    }

    public void surfaceCreated(SurfaceHolder holder) {
        // The Surface has been created, now tell the camera where to draw the
        // preview.
        try {
            setOrientation();
            mCamera.setPreviewDisplay(holder);
            mCamera.startPreview();
        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mHolder.getSurface() == null) return;
        // stop preview before making changes
        try {
            mCamera.stopPreview();
        } catch (Exception e){
            // ignore: tried to stop a non-existent preview
        }
        try {
            setOrientation();
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();

        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }


    public void previewCamera() {
        try {
            mCamera.setPreviewDisplay(mHolder);
            mCamera.startPreview();
        }catch (IOException e) {
            Log.e(TAG, getContext().getString(R.string.error_camera_preview) + e.getMessage());
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        this.mHolder = holder;
    }

    public void setOrientation(){
        Camera.Parameters params = mCamera.getParameters();
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_PORTRAIT) {
            if (Build.VERSION.SDK_INT != 7 && Build.VERSION.SDK_INT != 20) {
                //TODO remettre en place sur device
                mCamera.setDisplayOrientation(90);
            } else {
                Log.d("System out", "Portrait " + Build.VERSION.SDK_INT);
                params.setRotation(90);
                mCamera.setParameters(params);
            }
        }
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            if (Build.VERSION.SDK_INT != 7 && Build.VERSION.SDK_INT != 20) {
                mCamera.setDisplayOrientation(0);
            } else {
                Log.d("System out", "Landscape " + Build.VERSION.SDK_INT);
                params.set("orientation", "landscape");
                params.set("rotation", 90);
                mCamera.setParameters(params);
            }
        }
    }

    public void update(Camera camera){
        mCamera = camera;
        setOrientation();
        previewCamera();
    }
}
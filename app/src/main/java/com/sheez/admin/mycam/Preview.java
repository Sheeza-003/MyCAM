package com.sheez.admin.mycam;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;

import java.io.IOException;

public class
Preview extends SurfaceView implements SurfaceHolder.Callback {
    private SurfaceHolder sHolder;
    private Camera myCamera;
    public final String TAG = "Preview";

    public Preview(Context context, Camera camera) {
        super(context);
        myCamera = camera;
        sHolder = getHolder();
        sHolder.addCallback(this);
        sHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

    }

    public void surfaceCreated(SurfaceHolder holder) {
        try {
            myCamera.setDisplayOrientation(90);
            myCamera.getParameters();
            myCamera.setPreviewDisplay(holder);
            myCamera.startPreview();

        } catch (IOException e) {
            Log.d(TAG, "Error setting camera preview: " + e.getMessage());
        }
    }

    public void surfaceDestroyed(SurfaceHolder holder) {
        this.getHolder().removeCallback(this);
        myCamera.stopPreview();
        myCamera.release();
        myCamera = null;
        Log.e("surfaceDestroyed", "surfaceDestroyed");
      //  myCamera.stopPreview();
    }

    public void surfaceChanged(SurfaceHolder holder, int format, int w, int h) {

        if (sHolder.getSurface() == null){
            return;
        }
        try {
            myCamera.stopPreview();
        } catch (Exception e){
        }
        // set preview size and make any resize, rotate or
        // reformatting changes here
        // start preview with new settings
        try {
            myCamera.setPreviewDisplay(sHolder);
            myCamera.startPreview();
        } catch (Exception e){
            Log.d(TAG, "Error starting camera preview: " + e.getMessage());
        }
    }
}

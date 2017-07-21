package com.sheez.admin.mycam;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Camera;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Toast;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.jar.Manifest;

import static com.sheez.admin.mycam.R.id.go_back;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private Camera mCamera;

    public static final String TAG = "MainActivity";
    private boolean isFilterOpen=false;
    private ImageView filterButton;
    private Button viewPhoto;
    private ScrollView scrollView;
    Preview mPreview;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCamera = getCameraInstance();
        Preview mPreview = new Preview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.camera_preview);
       if (preview != null) {
           preview.addView(mPreview);
        }
        ImageView capturebutton = (ImageView) findViewById(R.id.button_capture);
        if (capturebutton != null) {
            capturebutton.setOnClickListener(this);

            findViewById(R.id.go_back).setVisibility(View.VISIBLE);
            isFilterOpen=true;
        }

        filterButton = (ImageView) findViewById(R.id.filterButton);
        if (filterButton != null) {
            filterButton.setOnClickListener(this);
        }
        viewPhoto = (Button) findViewById(R.id.viewPhoto);
        if (viewPhoto != null) {
            viewPhoto.setOnClickListener(this);
        }
    }

    public static Camera getCameraInstance(){
        Camera c = null;
        try {
            c = Camera.open(); // attempt to get a Camera instance
        }
        catch (Exception e){
            // Camera is not available (in use or does not exist)
        }
        return c; // returns null if camera is unavailable
    }

    public void colorEffectFilter(View v){
        try {
            Camera.Parameters parameters = mCamera.getParameters();
            switch (v.getId()) {
                case R.id.effectNone:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_NONE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectAqua:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_AQUA);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectBlackboard:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_BLACKBOARD);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectMono:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_MONO);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectNegative:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_NEGATIVE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectPosterize:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_POSTERIZE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectSepia:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_SEPIA);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectSolarize:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_SOLARIZE);
                    mCamera.setParameters(parameters);
                    break;
                case R.id.effectWhiteboard:
                    parameters.setColorEffect(Camera.Parameters.EFFECT_WHITEBOARD);
                    mCamera.setParameters(parameters);
                    break;
            }
        }catch (Exception ex){
            Log.d(TAG,ex.getMessage());
        }
    }

    private Camera.PictureCallback mPicture = new Camera.PictureCallback() {

        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile();
            if (pictureFile == null){
                Log.d(TAG, "Error creating media file, check storage permissions: ");
                return;
            }

            MediaScannerConnection.scanFile(MainActivity.this,
                    new String[] { pictureFile.toString() }, null,
                    new MediaScannerConnection.OnScanCompletedListener() {
                        public void onScanCompleted(String path, Uri uri) {
                            Log.i("ExternalStorage", "Scanned " + path + ":");
                            Log.i("ExternalStorage", "-> uri=" + uri);
                        }
                    });

            try {
                FileOutputStream fos = new FileOutputStream(pictureFile);
                fos.write(data);
                fos.close();
            } catch (FileNotFoundException e) {
                Log.d(TAG, "File not found: " + e.getMessage());
            } catch (IOException e) {
                Log.d(TAG, "Error accessing file: " + e.getMessage());
            }
        }
    };

       //creating file to save image
    private static File getOutputMediaFile(){
        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "CamPictures");

        // creating storage dir
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d(TAG, "failed to create directory");
                return null;
            }
        }

        // creating file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());

        return new File(mediaStorageDir.getAbsolutePath() + File.separator +
                    "IMG_"+ timeStamp + ".jpg");
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.filterButton:
                if (!isFilterOpen){
                    findViewById(R.id.record_filter_layout).setVisibility(View.VISIBLE);
                    filterButton.setImageResource(R.drawable.filter_on);
                    isFilterOpen=true;
                }
                else {
                    findViewById(R.id.record_filter_layout).setVisibility(View.GONE);
                    filterButton.setImageResource(R.drawable.filter_off);
                    isFilterOpen=false;
                }
                break;
            case R.id.button_capture:
                mCamera.takePicture(null,null,mPicture);
               // if (mCamera != null){}
                break;

            case R.id.viewPhoto:
              //  PickImageDialog.build(new PickSetup()).show(this);
                AlertDialog.Builder dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setCancelable(false);
                dialog.setTitle("Options");
               // dialog.setMessage("Are you sure you want to delete this entry?" );
                dialog.setPositiveButton("Gallery", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        //Action for "Delete".
                        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(
                                "content://media/internal/images/media"));
                        startActivity(i);
                    }
                })
                .setNegativeButton("Refresh", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                findViewById(R.id.go_back).setVisibility(View.VISIBLE);
                // filterButton.setImageResource(R.drawable.filter_on);
                isFilterOpen=true;
                            }
                })
                        .setNeutralButton("Cancel ", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                               // Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                //startActivity(intent);
                            }
                        });
                final AlertDialog alert = dialog.create();
                alert.show();

    }}
    public void go_back(View v){

      //  finish();
        if (mCamera == null) {
            mCamera = getCameraInstance();
        }
        mCamera.startPreview();
      //  startActivity(getIntent());


    }
/*
    @Override
    public void onBackPressed() {

        //Action for "Cancel".
     //  finish();

       // Intent i = new Intent(getApplicationContext(), MainActivity.class);
       // intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
       // startActivity(i);
//        new AlertDialog.Builder(this)
//                .setTitle("Really Exit?")
//                .setMessage("Are you sure you want to exit?")
//                .setNegativeButton(android.R.string.no, null)
//                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
//
//                    public void onClick(DialogInterface arg0, int arg1) {
//                        MainActivity.super.onBackPressed();
//                    }
//                }).create().show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCamera.setPreviewCallback(null);
 *//*       mCamera.stopPreview();
        mCamera.release();
        mCamera=null;
        mCamera.setPreviewCallback(null);*//*
    }
       *//*
        mCamera.setPreviewCallback(null);
        mPreview.getHolder().removeCallback(mPreview);
        mCamera.release();
        mCamera = null;
        finish();
        *//*

    @Override
    protected void onStop() {
        super.onStop();
        // release the camera immediately on pause event
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mCamera == null) {
            mCamera = getCameraInstance();
        }
        mCamera.startPreview();
    }

    private void releaseCamera(){
        if (mCamera != null){
            mCamera.setPreviewCallback(null);
            mCamera.stopPreview();
            mCamera.release();        // release the camera for other applications
            mCamera = null;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        releaseCamera();
  *//*      mCamera.stopPreview();
        mCamera.release();
        mCamera = null;*//*
    }*/
}

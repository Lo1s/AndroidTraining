package com.example.android.androidtraining;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.Toast;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class CameraActivity extends AppCompatActivity {
    private static final String LOG_TAG = "CameraActivity";

    public static final int MEDIA_TYPE_IMAGE = 1;

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private Camera.Parameters mParameters;

    /** 6th: Capture and Save Files */
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MEDIA_TYPE_IMAGE);
            if (pictureFile == null) {
                Log.d(LOG_TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
                fileOutputStream.write(data);
                fileOutputStream.close();
            } catch (FileNotFoundException ex) {
                Log.d(LOG_TAG, "File not found: " + ex.getMessage());
            } catch (IOException ex) {
                Log.d(LOG_TAG, "Error accessing file: " + ex.getMessage());
            }
        }
    };

    /** 3rd: Create a Preview Class - previews the live images from the camera */
    public class CameraPreview extends SurfaceView implements SurfaceHolder.Callback {
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
            mHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            // The Surface has been created, now tell the camera where to draw the preview.
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException ex) {
                Log.d(LOG_TAG, "Error setting camera preview: " + ex.getMessage());
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            // If your preview can change or rotate, take care of those events here.
            // Make sure to stop the preview before resizing or reformatting it.

            if (mHolder.getSurface() == null) {
                // preview surface does not exist
                return;
            }

            // stop preview before making changes
            try {
                mCamera.stopPreview();
            } catch (Exception ex) {
                // ignore: tried to stop a non-existent preview
                Log.d(LOG_TAG, "stopPreview failed");
            }

            // set preview size and make any resize, rotate or
            // reformatting changes here


            // start preview with new settings
            try {
                mCamera.setPreviewDisplay(holder);
                mCamera.startPreview();
            } catch (IOException ex) {
                Log.d(LOG_TAG, "Error starting camera preview: " + ex.getMessage());
            }
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            // empty. Take care of releasing the Camera preview in your activity.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        // Create an instance of Camera
        mCamera = getCameraInstance();

        /** 4th: Build a Preview Layout */
        // Create our Preview view and set it as the content of our activity.
        mCameraPreview = new CameraPreview(this, mCamera);
        FrameLayout preview = (FrameLayout) findViewById(R.id.frame_layout_camera_preview);
        preview.addView(mCameraPreview);

        /** 5th: Setup Listeners for capture */
        Button captureButton = (Button) findViewById(R.id.button_capture);
        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // get image from the camera
                mCamera.takePicture(null, null, mPictureCallback);
            }
        });

        if (!checkCameraHardware(this)) {
            Toast.makeText(CameraActivity.this, "Camera not found", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MediaActivity.class));            
        }

        // Check camera parameters
        mParameters = checkCameraFeatures();

    }

    /** 7th: Release the Camera */
    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCamera.release();
        mCameraPreview = new CameraPreview(this, null);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_camera, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** 1st: Detect if this device has a camera */
    private boolean checkCameraHardware(Context context) {
        if (context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            Toast.makeText(CameraActivity.this, "Number of cameras: " + Camera.getNumberOfCameras(), Toast.LENGTH_SHORT).show();
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /** 2nd: Safe way to get an instance of the Camera object */
    public static Camera getCameraInstance() {
        Camera camera = null;

        try {
            camera = Camera.open(); // attempt to open Camera instance

        } catch (Exception e) {
            // Camera is not available (in use or does not exist)
            Log.e(LOG_TAG, "Camera not available");
        }
        return camera; // returns null if the Camera is unavailable
    }

    /** Checking camera features */
    public Camera.Parameters checkCameraFeatures() {
        Camera.Parameters cameraParameters = null;
        if (mCamera != null) {
            cameraParameters = mCamera.getParameters();
            Log.i("checkCameraParameters: ", "Zoom supported: " + cameraParameters.isZoomSupported());
            Log.i("checkCameraParameters: ", "Auto exposure: " + cameraParameters.isAutoExposureLockSupported());
        }
        return cameraParameters;
    }

    private static Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    private static File getOutputMediaFile(int type) {
        return null; // TODO: implement
    }

}

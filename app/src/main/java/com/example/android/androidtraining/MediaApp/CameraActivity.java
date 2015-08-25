package com.example.android.androidtraining.MediaApp;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Rect;
import android.hardware.Camera;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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

import com.example.android.androidtraining.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class CameraActivity extends AppCompatActivity {
    private static final String LOG_TAG = "CameraActivity";

    private Camera mCamera;
    private CameraPreview mCameraPreview;
    private Camera.Parameters mParameters;
    private String mCurrentPhotoPath = "";
    private boolean isPicTaken = false;


    /**
     * 6th: Capture and Save Files
     */
    private Camera.PictureCallback mPictureCallback = new Camera.PictureCallback() {
        @Override
        public void onPictureTaken(byte[] data, Camera camera) {

            File pictureFile = getOutputMediaFile(MediaActivity.MEDIA_TYPE_IMAGE);
            mCurrentPhotoPath = pictureFile.getAbsolutePath();
            if (pictureFile == null) {
                Log.d(LOG_TAG, "Error creating media file, check storage permissions");
                return;
            }

            try {
                FileOutputStream fileOutputStream = new FileOutputStream(pictureFile);
                fileOutputStream.write(data);
                fileOutputStream.close();
                isPicTaken = true;
            } catch (FileNotFoundException ex) {
                Log.d(LOG_TAG, "File not found: " + ex.getMessage());
            } catch (IOException ex) {
                Log.d(LOG_TAG, "Error accessing file: " + ex.getMessage());
            }
        }
    };

    /**
     * 3rd: Create a Preview Class - previews the live images from the camera
     */
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

                startFaceDetection(); // start face detection feature
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

                startFaceDetection(); // re-start the face detection
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

        if (!checkCameraHardware(this)) {
            Toast.makeText(CameraActivity.this, "Camera not found", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(this, MediaActivity.class));
        }

        // Create an instance of Camera
        mCamera = getCameraInstance();

        /** Metering and focus areas */
        // set Camera parameters
        mParameters = mCamera.getParameters();

        if (mParameters.getMaxNumMeteringAreas() > 0) { // check that metering areas are supported
            List<Camera.Area> meteringAreas = new ArrayList<Camera.Area>();

            Rect areaRect1 = new Rect(-100, -100, 100, 100); // specify an area in center of image
            meteringAreas.add(new Camera.Area(areaRect1, 600)); // set weight to 60%
            Rect areaRect2 = new Rect(800, -1000, 1000, -800);  // specify an area in upper right corner of image
            meteringAreas.add(new Camera.Area(areaRect2, 400)); // set weight to 40%
            mParameters.setMeteringAreas(meteringAreas);
        }

        mCamera.setParameters(mParameters);
        mCamera.setFaceDetectionListener(new MyFaceDetectionListener());

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
                // Restart the Preview after picture is taken
                if (!isPicTaken)
                    mCamera.takePicture(null, null, mPictureCallback);
                else {
                    mCamera.startPreview();
                    galleryAddPic();
                    isPicTaken = false;
                }
            }
        });

    }

    /**
     * 7th: Release the Camera
     */
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

    /**
     * 1st: Detect if this device has a camera
     */
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

    /**
     * 2nd: Safe way to get an instance of the Camera object
     */
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

    /**
     * Checking camera features
     */
    public Camera.Parameters checkCameraFeatures() {
        Camera.Parameters cameraParameters = null;
        if (mCamera != null) {
            cameraParameters = mCamera.getParameters();
            Log.i("checkCameraParameters: ", "Zoom supported: " + cameraParameters.isZoomSupported());
            Log.i("checkCameraParameters: ", "Auto exposure: " + cameraParameters.isAutoExposureLockSupported());
        }
        return cameraParameters;
    }

    /**
     * Create a File for saving an image or video
     */
    private static File getOutputMediaFile(int type) {
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (!Environment.isExternalStorageEmulated()) {
            return new File("/sdcard/DCIM/");
        }

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "MyCameraApp");
        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdir()) {
                Log.d("MyCameraApp", "failed to create directory");
                return null;
            }
        }

        // Create media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile;
        if (type == MediaActivity.MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_" + timeStamp + ".jpg");
        } else if (type == MediaActivity.MEDIA_TYPE_VIDEO) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator + "VID_" + timeStamp + ".mp4");
        } else {
            return null;
        }

        return mediaFile;
    }

    class MyFaceDetectionListener implements Camera.FaceDetectionListener {

        @Override
        public void onFaceDetection(Camera.Face[] faces, Camera camera) {
            if (faces.length > 0) {
                Toast.makeText(getApplicationContext(), "Face detected ! @ " + "xCenter: " +
                        faces[0].rect.centerX() + " yCenter: " + faces[0].rect.centerY(), Toast.LENGTH_SHORT).show();
                Log.d("FaceDetection: ", "face detected: " + faces.length +
                        " Face 1 Location X: " + faces[0].rect.centerX() +
                        " Y: " + faces[0].rect.centerY());
            }
        }
    }

    public void startFaceDetection() {
        // Try starting face detection
        Camera.Parameters params = mCamera.getParameters();

        // start face detection only *after* preview has started
        if (mParameters.getMaxNumDetectedFaces() > 0) {
            // camera support face detection, so can start it
            mCamera.startFaceDetection();
        }
    }

    // Add pic to media gallery
    private void galleryAddPic() {
        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        File f = new File(mCurrentPhotoPath);
        Uri contentUri = Uri.fromFile(f);
        mediaScanIntent.setData(contentUri);
        this.sendBroadcast(mediaScanIntent);
    }
}

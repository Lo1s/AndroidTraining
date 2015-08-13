package com.example.android.androidtraining;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.support.v4.print.PrintHelper;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


public class MediaActivity extends ActionBarActivity {

    private MediaPlayer mediaPlayer;
    private WebView mWebView;
    private ArrayList<PrintJob> mPrintJobs = new ArrayList<>();

    public static final int MEDIA_TYPE_IMAGE = 1;
    public static final int MEDIA_TYPE_VIDEO = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);
    }

    @Override
    protected void onStop() {
        super.onStop();
        // If exists, release the mediaplayer when onStop is called
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_media, menu);
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

    // Own methods
    // Testing http://developer.android.com/guide/topics/media/mediaplayer.html
    // http://developer.android.com/training/managing-audio/index.html

    public void release(View view) {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
            Toast.makeText(this, "MediaPlayer nullified !", Toast.LENGTH_SHORT).show();
        }
    }

    public void playRaw(View view) {
        mediaPlayer = MediaPlayer.create(this, R.raw.thescript);
        mediaPlayer.start(); // no need to call prepare(); create() does that for you
    }

    public void playURI(View view) throws IOException {
        File file = new File("/sdcard/Download/The-Script---Hall-of-Fame-ft.-will.i.am.mp3");
        mediaPlayer = new MediaPlayer();
        Uri myUri = Uri.fromFile(file);
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        mediaPlayer.setDataSource(getApplicationContext(), myUri);
        mediaPlayer.prepare();
        mediaPlayer.start();
    }

    public void playURL(View view) {
        String url = "http://www.stephaniequinn.com/Music/The%20Irish%20Washerwoman.mp3";
        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(url);
            mediaPlayer.prepare(); // // might take long! (for buffering, etc)
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Connection problem", Toast.LENGTH_SHORT).show();
        }
    }

    // Play media in a service
    public void playViaService(View view) {
        startService(new Intent(MediaService.ACTION_PLAY));
    }

    // Destroy service
    public void destroyService(View view) {
        startService(new Intent(MediaService.ACTION_STOP));
    }

    // Play media in a foreground service
    public void playViaForegroundService(View view) {
        startService(new Intent(MediaService.ACTION_PLAY_FOREGROUND));
    }

    // Stop the foreground service
    public void stopForegroundService(View view) {
        startService(new Intent(MediaService.ACTION_STOP_FOREGROUND));
    }

    // Start the record activity
    public void recordThis(View view) {
        Intent recordIntent = new Intent(this, RecordActivity.class);
        startActivity(recordIntent);
    }

    // Launch Camera App
    public void launchCameraApp(View view) {
        startActivity(new Intent(this, CameraActivity.class));
    }

    // Launch Video App
    public void launchVideoApp(View view) {
        startActivity(new Intent(this, VideoActivity.class));
    }

    // Print that photo
    public void doPhotoPrint(View view) {
        PrintHelper printHelper = new PrintHelper(this);
        printHelper.setScaleMode(PrintHelper.SCALE_MODE_FIT);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rafa);
        printHelper.printBitmap("rafa.jpg - test print", bitmap);
    }

    // Create WebView client to load the HTML document
    public void doWebViewPrint(View view) {
        // Create a WebView object specifically for printing
        WebView webView = new WebView(getApplicationContext());
        webView.setWebViewClient(new WebViewClient() {

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return super.shouldOverrideUrlLoading(view, url);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                Log.i("MediaActivity", "page finished loading " + url);
                createWebPrintJob(view);
                mWebView = null;
            }
        });

        // Generate an HTML document on the fly
        String htmlDocument = "<html><body><h1>Test Content</h1><p>Testing, testing, testing..</p>" +
                "</body></html>";
        webView.loadDataWithBaseURL(null, htmlDocument, "text/HTML", "UTF-8", null);

        // Keep a reference to WebView object until you pass the PrintDocumentAdapter
        // to the PrintManager
        mWebView = webView;
    }

    // Creates the print job for the HTML document
    private void createWebPrintJob(WebView webView) {

        // Get a PrintManager instance
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        // Get a print adapter instance
        PrintDocumentAdapter printDocumentAdapter = webView.createPrintDocumentAdapter();

        // Create a print job with name and adapter instance
        String jobName = getString(R.string.app_name) + " Document";
        PrintJob printJob = printManager.print(jobName, printDocumentAdapter, new PrintAttributes.Builder().build());

        // Save the job object for later status checking
        mPrintJobs.add(printJob);
    }

    // Testing bitmap loading
    public void loadBitmaps(View view) {
        startActivity(new Intent(this, BitmapActivity.class));
    }

}

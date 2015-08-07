package com.example.android.androidtraining;

import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;


public class MediaActivity extends ActionBarActivity {

    private MediaPlayer mediaPlayer;


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
}

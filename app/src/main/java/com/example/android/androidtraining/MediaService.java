package com.example.android.androidtraining;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.IBinder;
import android.os.PowerManager;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;

/**
 * Created by jslapnicka on 4.8.2015.
 */
public class MediaService extends Service implements MediaPlayer.OnPreparedListener, MediaPlayer.OnErrorListener {
    public static final String ACTION_PLAY = "com.example.android.androidtraining.action.PLAY";
    public static final String ACTION_STOP = "com.example.android.androidtraining.action.STOP";
    public static final String ACTION_PLAY_FOREGROUND = "com.example.android.androidtraining.action.PLAY_FOREGROUND";
    public static final String ACTION_STOP_FOREGROUND = "com.example.android.androidtraining.action.STOP_FOREGROUND";
    MediaPlayer mMediaPlayer = null;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent.getAction().equals(ACTION_PLAY)) {
            initMediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync(); // prepare async to not block main thread
            Toast.makeText(this, "Service running", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(ACTION_STOP)) {
            onDestroy();
        } else if (intent.getAction().equals(ACTION_PLAY_FOREGROUND)) {
            final int NOTIFICATION_ID = 1337;
            String songName = "The Script - Hall of Fame";
            PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(), 0,
                    new Intent(getApplicationContext(), MainActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            Notification notification = new Notification();
            notification.tickerText = "tickerText";
            notification.icon = R.drawable.ic_media_play;
            notification.flags |= Notification.FLAG_ONGOING_EVENT;
            notification.setLatestEventInfo(getApplicationContext(), "Music Player",
                    "Playing: " + songName, pendingIntent);
            startForeground(NOTIFICATION_ID, notification);
            initMediaPlayer();
            mMediaPlayer.setOnPreparedListener(this);
            mMediaPlayer.prepareAsync(); // prepare async to not block main thread
            Toast.makeText(MediaService.this, "Foreground service running", Toast.LENGTH_SHORT).show();
        } else if (intent.getAction().equals(ACTION_STOP_FOREGROUND)) {
            stopForeground(true);
            Toast.makeText(MediaService.this, "Service stopped", Toast.LENGTH_SHORT).show();
            onDestroy();
        }
        return START_STICKY;

    }

    @Override
    public void onDestroy() {
        mMediaPlayer.release();
        mMediaPlayer = null;
        super.onDestroy();
        Toast.makeText(MediaService.this, "Service destroyed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void initMediaPlayer() {
        try {
            File file = new File("/sdcard/Download/The-Script---Hall-of-Fame-ft.-will.i.am.mp3");
            mMediaPlayer = new MediaPlayer();
            // Create the wake-lock to prevent shutting down the service when screen is off
            mMediaPlayer.setWakeMode(getApplicationContext(), PowerManager.PARTIAL_WAKE_LOCK);
            Uri myUri = Uri.fromFile(file);
            mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mMediaPlayer.setDataSource(getApplicationContext(), myUri);
            mMediaPlayer.setOnErrorListener(this);
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MediaService.this, "Error during prepare()", Toast.LENGTH_SHORT).show();
        }
    }

    /** Called when MediaPlayer is ready */
    @Override
    public void onPrepared(MediaPlayer mp) {
        mp.start();
        Toast.makeText(this, "MediaPlayer started", Toast.LENGTH_SHORT).show();
    }

    @Override
    public boolean onError(MediaPlayer mp, int what, int extra) {
        // ... react appropriately ...
        // The MediaPlayer has moved to the Error state, must be reset!
        mp.reset();
        Toast.makeText(this, "onError()", Toast.LENGTH_LONG).show();
        return true;
    }
}
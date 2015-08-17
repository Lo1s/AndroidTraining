package com.example.android.androidtraining;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import java.io.IOException;

// http://developer.android.com/guide/topics/media/audio-capture.html
public class RecordActivity extends AppCompatActivity {

    private static final String LOG_TAG = "RecordActivity";
    private static String mFileName = null;

    private RecordButton mRecordButton = null;
    private MediaRecorder mRecorder = null;

    private PlayButton mPlayButton = null;
    private MediaPlayer mPlayer = null;
    private boolean mStartPlaying = true;

    private Intent mainIntent;
    private AudioManager audioManager;
    private ComponentName mRemoteControlReceiver;
    private AudioManager.OnAudioFocusChangeListener audioFocusChangeListener;
    private NoisyAudioStreamReceiver noisyAudioStreamReceiver;

    public RecordActivity() {
        mFileName = Environment.getExternalStorageDirectory().getAbsolutePath();
        mFileName += "/audiorecordtest.3gp";
    }

    private void onRecord(boolean start) {
        if (start) {
            Toast.makeText(RecordActivity.this, "Recording...", Toast.LENGTH_SHORT).show();
            startRecording();
        } else
            stopRecording();
    }

    private void onPlay(boolean start) {
        if (start) {
            Toast.makeText(RecordActivity.this, "Playing...", Toast.LENGTH_SHORT).show();
            startPlaying();
        } else
            stopPlaying();
    }

    public void startPlaying() {
        mPlayer = new MediaPlayer();
        try {
            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
            Toast.makeText(this, "Playing...", Toast.LENGTH_SHORT).show();
        } catch (IOException ex) {
            Toast.makeText(RecordActivity.this, "prepare() failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    private void startRecording() {
        mRecorder = new MediaRecorder();
        mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mRecorder.setOutputFile(mFileName);
        mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);

        try {
            mRecorder.prepare();
        } catch (IOException ex) {
            Toast.makeText(RecordActivity.this, "prepare() failed", Toast.LENGTH_SHORT).show();
        }

        mRecorder.start();
    }

    private void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    class RecordButton extends Button {
        boolean mStartRecording = true;

        public RecordButton(Context ctx) {
            super(ctx);
            setText("Start recording");
            setOnClickListener(clicker);
        }

        OnClickListener clicker = new OnClickListener() {
            @Override
            public void onClick(View v) {
                onRecord(mStartRecording);
                if (mStartRecording) {
                    setText("Stop recording");
                } else {
                    setText("Start recording");
                }
                mStartRecording = !mStartRecording;
            }
        };
    }

    class PlayButton extends Button {

        public PlayButton(Context ctx) {
            super(ctx);
            setText("Start playing");
            setOnClickListener(clicker);
        }

        OnClickListener clicker = new OnClickListener() {
            @Override
            public void onClick(View v) {
                onPlay(mStartPlaying);

                if (mStartPlaying) {
                    setText("Stop playing");
                } else
                    setText("Start playing");
                mStartPlaying = !mStartPlaying;
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        LinearLayout linearLayout = new LinearLayout(this);
        mRecordButton = new RecordButton(this);
        linearLayout.addView(mRecordButton, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        mPlayButton = new PlayButton(this);
        linearLayout.addView(mPlayButton, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                0));
        setContentView(linearLayout);

        // Start listening for button pressing on the remote device (e.g. wireless headphones)
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        mRemoteControlReceiver = new ComponentName(this, RemoteReceiver.class);
        audioManager.registerMediaButtonEventReceiver(mRemoteControlReceiver);

        // Create listener in order to listen for a audio focus
        audioFocusChangeListener = new AudioManager.OnAudioFocusChangeListener() {
            // TODO: Do something meaningful here
            @Override
            public void onAudioFocusChange(int focusChange) {
                if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT) {
                    // Pause playback
                    Toast.makeText(RecordActivity.this, "Audio Focus Lost - Pausing...", Toast.LENGTH_SHORT).show();
                } else if (focusChange == AudioManager.AUDIOFOCUS_GAIN) {
                    // Resume playback / Raise the volume back to normal after ducking
                    Toast.makeText(RecordActivity.this, "Audio Focus Resumed - Resuming...", Toast.LENGTH_SHORT).show();
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS) {
                    Toast.makeText(RecordActivity.this, "Audio Focus Lost Permanently", Toast.LENGTH_SHORT).show();
                    audioManager.unregisterMediaButtonEventReceiver(mRemoteControlReceiver);
                    audioManager.abandonAudioFocus(audioFocusChangeListener);
                } else if (focusChange == AudioManager.AUDIOFOCUS_LOSS_TRANSIENT_CAN_DUCK) {
                    // Lower the volume
                }
            }
        };
        // Request the Audio Focus
        int result = audioManager.requestAudioFocus(audioFocusChangeListener,
                // Use the music stream
                AudioManager.STREAM_MUSIC,
                // Request permanent focus
                AudioManager.AUDIOFOCUS_GAIN_TRANSIENT_MAY_DUCK);

        // Check what hardware is being used
        if (audioManager.isBluetoothA2dpOn()) {
            Toast.makeText(RecordActivity.this, "Bluetooth headset is on !", Toast.LENGTH_SHORT).show();
        } else if (audioManager.isSpeakerphoneOn()) {
            Toast.makeText(RecordActivity.this, "Speakers are active !", Toast.LENGTH_SHORT).show();
        } else if (audioManager.isWiredHeadsetOn()) {
            Toast.makeText(RecordActivity.this, "Headset is on !", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(RecordActivity.this, "I don't know what audio device is connected.", Toast.LENGTH_SHORT).show();
        }

        // When headset is being unplugged make sure that the volume will be on acceptable level
        // when transmitting to speakers or just pause the music
        IntentFilter intentFilter = new IntentFilter(AudioManager.ACTION_AUDIO_BECOMING_NOISY);
        noisyAudioStreamReceiver = new NoisyAudioStreamReceiver();
        registerReceiver(noisyAudioStreamReceiver, intentFilter);
    }


    @Override
    protected void onPause() {
        super.onPause();

        // Unregister the dynamically build noisy receiver
        if (noisyAudioStreamReceiver != null) {
            unregisterReceiver(noisyAudioStreamReceiver);
        }

        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_record, menu);
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


}

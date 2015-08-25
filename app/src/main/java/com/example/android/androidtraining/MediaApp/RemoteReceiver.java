package com.example.android.androidtraining.MediaApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * Created by jslapnicka on 6.8.2015.
 */
public class RemoteReceiver extends BroadcastReceiver {
    public static final String BLUETOOTH_KEY_PRESSED = "com.example.android.androidtraining.BLUETOOTH_KEY_PRESSED";
    RecordActivity recordActivity = null;

    public RemoteReceiver() {
        //super();
    }

    // TODO: Look up how to call methods in other activities from the broadcast receiver
    void setRecordActivityHandler(RecordActivity recordActivity) {
        this.recordActivity = recordActivity;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        // Handle the media buttons (from a remote device - e.g. wireless headphones)
        if (Intent.ACTION_MEDIA_BUTTON.equals(intent.getAction())) {
            KeyEvent keyEvent = (KeyEvent)intent.getParcelableExtra(Intent.EXTRA_KEY_EVENT);
            if (KeyEvent.KEYCODE_MEDIA_PLAY == keyEvent.getKeyCode()) {
                // Handle key press
                Toast.makeText(context, "Play pressed on headSet!", Toast.LENGTH_SHORT).show();

                /*startPlaying();
                if (mStartPlaying) {
                    mPlayButton.setText("Stop playing");
                } else
                    mPlayButton.setText("Start playing");
                mStartPlaying = !mStartPlaying;*/
            }
        }
    }
}
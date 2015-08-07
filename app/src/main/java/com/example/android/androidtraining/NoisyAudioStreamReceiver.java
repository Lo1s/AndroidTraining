package com.example.android.androidtraining;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;

/**
 * Created by jslapnicka on 6.8.2015.
 */

// When headset is being unplugged make sure that the volume will be on acceptable level
// when transmitting to speakers or just pause the music
public class NoisyAudioStreamReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if (AudioManager.ACTION_AUDIO_BECOMING_NOISY.equals(intent.getAction())) {
            // Pause music
        }
    }
}

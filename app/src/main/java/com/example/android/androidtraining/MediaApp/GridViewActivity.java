package com.example.android.androidtraining.MediaApp;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

/**
 * Created by jiris on 17.08.2015.
 */
public class GridViewActivity extends FragmentActivity {
    private static final String TAG = "ImageGridActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getSupportFragmentManager().findFragmentByTag(TAG) == null) {
            final android.support.v4.app.FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.add(android.R.id.content, new GridViewFragment(), TAG);
            ft.commit();
        }
    }
}
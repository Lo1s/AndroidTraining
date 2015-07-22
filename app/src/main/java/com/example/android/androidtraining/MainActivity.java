// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.example.android.androidtraining;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

// Referenced classes of package com.example.android.androidtraining:
//            DisplayImageActivity, DisplayMessageActivity

public class MainActivity extends ActionBarActivity {


    /**
     * Global variables
     */
    // Intent extra message
    public static final String EXTRA_MESSAGE = "com.example.android.androidtraining.MESSAGE";

    // Identifier for the saved instance
    static final String COUNT = "counter";

    // Counter
    private int count;

    // ActionBar listener
    private ActionBar.OnNavigationListener mOnNavigationListener;

    // TextViews global declaration
    public TextView activityMonitor;
    public TextView counter;
    // SharedPreferences global decleration
    SharedPreferences sharedPreferences;

    public MainActivity() {
        count = 0;
    }

    /**
     * Lifecycle methods
     */
    protected void onCreate(Bundle bundle) {
        // Always call super for lifecycle methods
        super.onCreate(bundle);
        // Setting up the layout for the activity
        setContentView(R.layout.activity_main);
        // Initializing the TextViews that are handled programatically
        counter = (TextView) findViewById(R.id.counter);
        activityMonitor = (TextView) findViewById(R.id.activityMonitor);
        // If the app is started for the first time load the count value from the shared preferences
        if (count == 0) {
            count = restoreCountFromSharedPreferences();
        }
        // Set the counter after creating
        counter.setText(getString(R.string.count) + ": " + count);

        // Checking the SDK during runtime
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            disableToast();
        }
        // Activity monitor message
        activityMonitor.append(getLocalClassName() + ": onCreate()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Activity monitor message
        activityMonitor.append(getLocalClassName() + ": onStart()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity monitor message
        activityMonitor.append(getLocalClassName() + ": onResume()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Get share preferences
        saveCountToSharedPreferences();
        // Activity monitor message
        activityMonitor.setText(getLocalClassName() + ": onPause()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Activity monitor message
        activityMonitor.setText(getLocalClassName() + ": onRestart()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Activity monitor message
        activityMonitor.setText(getLocalClassName() + ": onStop()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Activity monitor message
        activityMonitor.setText(getLocalClassName() + ": onDestroy()" + System.getProperty("line.separator"));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handling the selected item within the ActionBar
    public boolean onOptionsItemSelected(MenuItem menuitem) {
        int itemId = menuitem.getItemId();
        if (itemId == R.id.action_settings) {
            return true;
        }
        switch (itemId) {
            default:
                return super.onOptionsItemSelected(menuitem);

            case R.id.menu_display:
                display();
                return true;

            case R.id.menu_add:
                add();
                break;
            case R.id.menu_tabs:
                Intent intentTabs = new Intent(this, TabActivity.class);
                startActivity(intentTabs);
                break;
            case R.id.menu_dropdown:
                Intent intentDropDown = new Intent(this, DropDownNavigationActivity.class);
                startActivity(intentDropDown);
                break;
            case R.id.menu_login:
                Intent intentLogin = new Intent(this, LoginActivity.class);
                startActivity(intentLogin);
                break;
        }
        return true;
    }

    // Saving the instance for keeping the value in counter
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(COUNT, count);
        super.onSaveInstanceState(outState);
    }

    // Restoring the saved instance
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        count = savedInstanceState.getInt(COUNT);
        counter.setText(new StringBuilder("Count: ").append(count));
    }

    /**
     * Own methods
     */
    // Sending message from the EditText to anither activity via Intent
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    // Testing Toasts (Checking the minimal SDK version)
    public void disableToast() {
        Toast.makeText(this, "Min SDK Test", Toast.LENGTH_SHORT).show();
    }

    // Increment counter
    public void add() {
        count = count + 1;
        ((TextView) findViewById(R.id.counter)).setText((new StringBuilder()).append(getString(R.string.count) + ": ").append(count).toString());
    }

    // ActionBar method for DisplayActivity
    public void display() {
        startActivity(new Intent(this, DisplayImageActivity.class));
    }

    // Save the count value into SharedPreferences
    public void saveCountToSharedPreferences() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.count_value_key), count);
        editor.commit();
    }

    // Load the count value from the SharedPreferences
    public int restoreCountFromSharedPreferences() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        return sharedPreferences.getInt(getString(R.string.count_value_key), 0);
    }

    // Starts the activity with intent test
    public void startIntentsActivity(View view) {
        Intent intent = new Intent(this, IntentsActivity.class);
        startActivity(intent);
    }

    // Starts the HelloIntentService service
    public void startIntentService(View view) {
        Intent startIntentService = new Intent(MainActivity.this, HelloIntentService.class);
        startService(startIntentService);

    }

    // Starts the HelloService service
    public void startService(View view) {
        Intent startService = new Intent(this, HelloService.class);
        startService(startService);
    }
}


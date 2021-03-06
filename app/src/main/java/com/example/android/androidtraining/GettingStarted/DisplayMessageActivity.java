// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.example.android.androidtraining.GettingStarted;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.androidtraining.R;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;

public class DisplayMessageActivity extends ActionBarActivity {

    public TextView textView;
    public String message;
    public final String FILE_NOT_FOUND = "File not found";
    private String filename = "message";
    public DisplayMessageActivity() {

    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        // Setting up the layout with the message from the MainActivity
        setContentView(R.layout.activity_display_message);
        textView = (TextView) findViewById(R.id.textViewMessage);
        // Get the intent
        Intent intent = getIntent();
        if (intent.hasExtra(IntentsActivity.IMPLICIT_TEST)) {
            message = intent.getStringExtra(IntentsActivity.IMPLICIT_TEST);
            textView.setText("Implicit intent: " + System.getProperty("line.separator") + message);
            /*try {
                Thread.sleep(2000);
                setResult(RESULT_OK);
                finish();
            } catch (InterruptedException ex) {
                Log.e("Pause", "Pause Failed");
            }*/
        } else if (intent.hasExtra(MainActivity.EXTRA_MESSAGE)){
            message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
            textView.setText("Explicit intent: " + System.getProperty("line.separator") + message);
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display_message, menu);

        // Testing the SearchView widget inside the ActionBar
        MenuItem searchItem = menu.findItem(R.id.set_message);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setQueryHint(getString(R.string.search));
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                textView.setText(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem) {
        if (menuitem.getItemId() == R.id.action_settings) {
            return true;
        } else {
            return super.onOptionsItemSelected(menuitem);
        }
    }

    // Save the message to the file at internal memory
    private void saveMessageToFile(String string) {
        FileOutputStream outputStream;
        File file = new File(getFilesDir() + "");
        double freeSpace = file.getFreeSpace() / 1000000000D;
        double totalSpace = file.getTotalSpace() / 1000000000D;
        String space = "Free space: " + String.format("%.2f", freeSpace) + " GB / "
                + String.format("%.2f", totalSpace) + " GB";
        try {
            outputStream = openFileOutput(filename, MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
            Toast.makeText(this, space, Toast.LENGTH_LONG).show();
        } catch (Exception ex) {
            Log.d(FILE_NOT_FOUND, "Error during creating FileOutputStream");
            Toast.makeText(this, "File not created !", Toast.LENGTH_SHORT).show();
        }
    }

    // Load the message from the file at the internal memory
    private String loadMessageFromFile() {
        FileInputStream fileInputStream;
        InputStreamReader inputStreamReader;
        BufferedReader bufferedReader;
        StringBuilder builder = new StringBuilder();
        try {
            fileInputStream = openFileInput(filename);
            inputStreamReader = new InputStreamReader(fileInputStream);
            bufferedReader = new BufferedReader(inputStreamReader);
            String temp;
            while ((temp = bufferedReader.readLine()) != null) {
                builder.append(temp + System.getProperty("line.separator"));
            }
            fileInputStream.close();
        } catch (Exception ex) {
            Log.d(FILE_NOT_FOUND, "Error while reading the file !");
            Toast.makeText(this, "File not found !", Toast.LENGTH_SHORT).show();
        }
        return builder.toString();
    }

    // Display the message saved from the file
    public void displayMessageFromIM(View view) {
        TextView textView = (TextView) findViewById(R.id.textViewSavedMessage);
        textView.setText(loadMessageFromFile());
    }

    // Save message by button
    public void saveMessageToIM(View view) {
        saveMessageToFile(textView.getText().toString());
    }
}

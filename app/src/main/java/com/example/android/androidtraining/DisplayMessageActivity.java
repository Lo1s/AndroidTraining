// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.example.android.androidtraining;

import android.content.Context;
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

import java.io.FileInputStream;
import java.io.FileOutputStream;

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
        message = getIntent().getStringExtra(MainActivity.EXTRA_MESSAGE);
        textView = (TextView) findViewById(R.id.textViewMessage);
        textView.setText(message);
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

        try {
            outputStream = openFileOutput(filename, Context.MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception ex) {
            Log.e(FILE_NOT_FOUND, "Error during creating FileOutputStream");
            Toast.makeText(this, "File not created !", Toast.LENGTH_SHORT).show();
        }
    }

    // Load the message from the file at the internal memory
    private String loadMessageFromFile() {
        FileInputStream inputStream;
        StringBuilder builder = new StringBuilder();
        try {
            inputStream = openFileInput(filename);
            while (openFileInput(filename).available() >= 0) {
                builder.append((char)(openFileInput(filename).read()));
            }
            inputStream.close();
        } catch (Exception ex) {
            Log.e(FILE_NOT_FOUND, "Error while reading the file !");
            Toast.makeText(this, "File not found !", Toast.LENGTH_SHORT).show();
        }
        return builder.toString();
    }

    // Display the message saved from the file
    public void displayMessage(View view) {
        TextView textView = (TextView) findViewById(R.id.textViewSavedMessage);
        textView.setText(loadMessageFromFile());
    }

    // Save message by button
    public void saveMessage(View view) {
        saveMessageToFile(textView.getText().toString());
    }
}

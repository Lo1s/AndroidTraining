package com.example.android.androidtraining.GettingStarted;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.example.android.androidtraining.Navigation.NavigationActivity;
import com.example.android.androidtraining.R;

public class MoreActivity extends AppCompatActivity {

    public static final String ACTION_MATERIAL = "com.example.android.androidtraining.GettingStarted.action.MATERIAL";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_more, menu);
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

    // Retrieve contacts
    public void retrieveContacts(View view) {
        startActivity(new Intent(this, RetrieveContactsActivity.class));
    }

    // Start navigation activity
    public void startNavigationActivity(View view) {
        startActivity(new Intent(this, NavigationActivity.class));
    }

    // Start Material Design app
    public void startMaterialDesignApp(View view) {
        Intent intent = new Intent(ACTION_MATERIAL);
        if (intent.resolveActivity(getPackageManager()) != null)
            startActivity(intent);
        else
            Toast.makeText(MoreActivity.this, "Not found", Toast.LENGTH_SHORT).show();
    }

    // Starts the Gestures activity
    public void startGesturesActivity(View view) {
        startActivity(new Intent(this, GesturesActivity.class));
    }
}

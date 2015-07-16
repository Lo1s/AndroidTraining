// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.example.android.androidtraining;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

public class DisplayMessageActivity extends ActionBarActivity {

    public TextView textView;
    public String message;

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
}

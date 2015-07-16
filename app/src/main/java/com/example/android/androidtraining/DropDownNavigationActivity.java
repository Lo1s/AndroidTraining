package com.example.android.androidtraining;

import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;


public class DropDownNavigationActivity extends ActionBarActivity {

    private SpinnerAdapter mSpinnerAdapter;
    private ActionBar.OnNavigationListener mOnNavigationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_dropdown);
        // Set up drop-down menu
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
        mSpinnerAdapter = ArrayAdapter.createFromResource(this, R.array.action_list, android.R.layout.simple_spinner_dropdown_item);
        // Implement OnNavigationListener interface
        mOnNavigationListener = new ActionBar.OnNavigationListener() {
            // Get the same strings provided for the drop-down's ArrayAdapter
            String[] strings = getResources().getStringArray(R.array.action_list);

            @Override
            public boolean onNavigationItemSelected(int itemPosition, long itemId) {
                // Create new fragment from our own fragment class
                DropDownNavigationFragment newFragment = new DropDownNavigationFragment();
                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

                // Replace whatever is in the fragment container with this fragment
                // and give the fragment a tag name equal to the string at the position
                // selected

                transaction.replace(android.R.id.content, newFragment, strings[itemPosition]);

                // Apply changes
                transaction.commit();
                return true;
            }
        };
        // Setting the callback
        actionBar.setListNavigationCallbacks(mSpinnerAdapter, mOnNavigationListener);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_drop_down_navigation, menu);
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

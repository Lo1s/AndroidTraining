package com.example.android.androidtraining;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;


public class TabActivity extends ActionBarActivity {

    // Implementing Tab listener for ActionBar Tabs
    // Caution: You must not call commit() for the fragment transaction in each of these callbacks
    // the system calls it for you and it may throw an exception if you call it yourself.
    // You also cannot add these fragment transactions to the back stack.
    public static class TabListener<T extends Fragment> implements ActionBar.TabListener {
        private Fragment mFragment;
        private final Activity mActivity;
        private final String mTag;
        private final Class<T> mClass;

        /** Constructor used each time a new tab is created.
         * @param activity  The host Activity, used to instantiate the fragment
         * @param tag  The identifier tag for the fragment
         * @param clz  The fragment's Class, used to instantiate the fragment
         */
        public TabListener(Activity activity, String tag, Class<T> clz) {
            mActivity = activity;
            mTag = tag;
            mClass = clz;
        }

        /** The following are each of the ActionBar.TabListener callbacks */
        @Override
        public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
            // Check if the fragment is already initialized
            if (mFragment == null) {
                // If not, instantiate and add it to the activity
                mFragment = Fragment.instantiate(mActivity, mClass.getName());
                ft.add(android.R.id.content, mFragment, mTag);
            } else {
                // If it exists, simply attach it in order to show it
                ft.attach(mFragment);
            }
        }

        @Override
        public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
            if (mFragment != null) {
                ft.detach(mFragment);
            }
        }

        @Override
        public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
            // User selected the already selected tab. Usually do nothing.
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Setup actionBar for tabs
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        // Add Tab1
        ActionBar.Tab tab1 = actionBar.newTab()
                .setText("Tab1")
                .setTabListener(new TabListener<Tab1Fragment>(this, "tab1", Tab1Fragment.class));
        actionBar.addTab(tab1);

        // Add Tab2
        ActionBar.Tab tab2 = actionBar.newTab()
                .setText("Tab2")
                .setTabListener(new TabListener<Tab2Fragment>(this, "tab2", Tab2Fragment.class));
        actionBar.addTab(tab2);

        // Add Tab3
        ActionBar.Tab tab3 = actionBar.newTab()
                .setText("Tab3")
                .setTabListener(new TabListener<Tab3Fragment>(this, "tab3", Tab3Fragment.class));
        actionBar.addTab(tab3);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_tab, menu);
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

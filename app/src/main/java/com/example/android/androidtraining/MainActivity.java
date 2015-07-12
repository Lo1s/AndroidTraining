// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.example.android.androidtraining;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

// Referenced classes of package com.example.android.androidtraining:
//            DisplayImageActivity, DisplayMessageActivity

public class MainActivity extends ActionBarActivity {

    // TODO: Action bar Tabs
    // TODO: Action bar Spinner (Drop down menu)


    public static final String EXTRA_MESSAGE = "com.example.android.androidtraining.MESSAGE";
    static final String COUNT = "counter";
    private final int PICK_CONTACT_REQUEST = 1;
    private int count;
    private ActionBar.OnNavigationListener mOnNavigationListener;
    private SpinnerAdapter mSpinnerAdapter;
    public TextView activityMonitor;
    public TextView showContact;
    public TextView counter;

    public MainActivity() {
        count = 0;

    }

    public void add() {
        count = count + 1;
        ((TextView) findViewById(R.id.counter)).setText((new StringBuilder()).append(getString(R.string.count) + ": ").append(count).toString());
    }

    public void display() {
        startActivity(new Intent(this, DisplayImageActivity.class));
    }

    private void showWebsite() {

    }

    public void pickContact(View view) {
        // Create an intent to pick the contact
        Intent intent = new Intent(Intent.ACTION_PICK, ContactsContract.Contacts.CONTENT_URI);
        startActivityForResult(intent, PICK_CONTACT_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Check if the request went ok and if it was PICK_CONTACT_REQUEST
        // TODO: Update this method for actually picking the contact when you learn about content providers
        if (resultCode == Activity.RESULT_OK && requestCode == PICK_CONTACT_REQUEST) {
            showContact.setText(data.getDataString());
            Toast.makeText(this, "activityForResult went ok !", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(COUNT, count);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        count = savedInstanceState.getInt(COUNT);
        counter.setText(new StringBuilder("Count: ").append(count));
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        counter = (TextView) findViewById(R.id.counter);
        activityMonitor = (TextView) findViewById(R.id.activityMonitor);
        showContact = (TextView) findViewById(R.id.showContact);
        counter.setText(getString(R.string.count) + ": " + count);
        /** Testing of checking the SDK during runtime */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            disableToast();
        }
        activityMonitor.append(getLocalClassName() + ": onCreate()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityMonitor.append(getLocalClassName() + ": onStart()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityMonitor.append(getLocalClassName() + ": onResume()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityMonitor.setText(getLocalClassName() + ": onPause()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        activityMonitor.setText(getLocalClassName() + ": onRestart()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityMonitor.setText(getLocalClassName() + ": onStop()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityMonitor.setText(getLocalClassName() + ": onDestroy()" + System.getProperty("line.separator"));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public boolean onOptionsItemSelected(MenuItem menuitem) {
        int itemId = menuitem.getItemId();
        if (itemId == R.id.action_settings) {
            return true;
        }
        switch (itemId) {
            default:
                return super.onOptionsItemSelected(menuitem);

            case R.id.action_display:
                display();
                return true;

            case R.id.action_add:
                add();
                break;
        }
        return true;
    }

    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    public void disableToast() {
        Toast.makeText(this, "Min SDK Test", Toast.LENGTH_SHORT).show();
    }
}

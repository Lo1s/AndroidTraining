// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.example.android.androidtraining;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import android.widget.Toast;

// Referenced classes of package com.example.android.androidtraining:
//            DisplayImageActivity, DisplayMessageActivity

public class MainActivity extends ActionBarActivity {

    // TODO: Action bar Tabs
    // TODO: Action bar Spinner (Drop down menu)


    public static final String EXTRA_MESSAGE = "com.example.android.androidtraining.MESSAGE";
    private int count;
    private ActionBar.OnNavigationListener mOnNavigationListener;
    private SpinnerAdapter mSpinnerAdapter;

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

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_main);
        TextView textView = (TextView) findViewById(R.id.counter);
        textView.setText(getString(R.string.count) + ": " + count);
        /** Testing of checking the SDK during runtime */
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            disableToast();
        }
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

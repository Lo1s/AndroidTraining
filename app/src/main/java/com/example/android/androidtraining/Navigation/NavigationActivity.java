package com.example.android.androidtraining.Navigation;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;

import com.example.android.androidtraining.GettingStarted.CommonConstants;
import com.example.android.androidtraining.R;

public class NavigationActivity extends ActionBarActivity {

    private Switch switchBigView = (Switch) findViewById(R.id.switch_big_view);
    private boolean isSwitchChecked = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        switchBigView.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    isSwitchChecked = true;
                } else {
                    isSwitchChecked = false;
                }
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch(id) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                Intent upIntent = NavUtils.getParentActivityIntent(this);
                if (NavUtils.shouldUpRecreateTask(this, upIntent)) {
                    // This activity is NOT part of this app's task, so create a new task
                    // when navigating up, with a synthesized back stack.
                    android.support.v4.app.TaskStackBuilder.create(this)
                            // Add all of this activity's parents to the back stack
                            .addNextIntentWithParentStack(upIntent)
                            // Navigate up to the closest parent
                            .startActivities();
                } else {
                    NavUtils.navigateUpFromSameTask(this);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Start the View Pager activity
    public void startViewPager(View view) {
        startActivity(new Intent(this, ViewPagerDesignActivity.class));
    }

    // Start the Navigation Drawer activity
    public void startDrawer(View view) {
        startActivity(new Intent(this, DrawerActivity.class));
    }

    // Build a notification
    private int notifyNum = 0;
    public void notifyMe(View view) {
        // Sets an ID for the notification
        int mNotificationId = 001;

        if (!isSwitchChecked) {
            NotificationCompat.Builder mBuilder =
                    new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.machine)
                    .setContentInfo("My Notification")
                    .setContentText("Hello World")
                    .setAutoCancel(true)
                    .setContentTitle("Notification #" + notifyNum++);

            Intent resultIntent = new Intent(this, NavigationActivity.class);
            TaskStackBuilder stackBuilder = TaskStackBuilder.create(this);
            // Adds the back stack
            stackBuilder.addParentStack(NavigationActivity.class);
            // Adds the Intent to the top of the stack
            stackBuilder.addNextIntent(resultIntent);
            // Gets a PendingIntent containing the entire back stack
            PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            mBuilder.setContentIntent(resultPendingIntent);

            // Gets an instance of the NotificationManager service
            NotificationManager mNotifyMgr =
                    (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            // Builds the notification and issues it.
            mNotifyMgr.notify(mNotificationId, mBuilder.build());
        } else {
            // Sets up the Snooze and Dismiss action buttons that will appear in the
            // big view of the notification.
            Intent dismissIntent = new Intent(this, NavigationActivity.class);
            dismissIntent.setAction(CommonConstants.ACTION_DISMISS);
            PendingIntent piDismiss = PendingIntent.getService(this, 0, dismissIntent, 0);

            Intent snoozeIntent = new Intent(this, NavigationActivity.class);
            snoozeIntent.setAction(CommonConstants.ACTION_SNOOZE);
            PendingIntent piSnooze = PendingIntent.getService(this, 0, snoozeIntent, 0);

            // Constructs the Builder object.
            NotificationCompat.Builder builder =
                    new NotificationCompat.Builder(this)
                    .setSmallIcon(R.drawable.machine)
                    .setContentTitle("Big View Notification")
                    .setContentText("Don't forget to feed the dogs before you leave for work. " +
                            "Also, check the garage to make sure we're not running low on dog food.")
                    .setDefaults(Notification.DEFAULT_ALL)

                    .setStyle(new NotificationCompat.BigTextStyle()
                    .bigText(msg))
                    .addAction(R.drawable.)
        }

    }


}

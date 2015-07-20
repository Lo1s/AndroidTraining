package com.example.android.androidtraining;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import org.apache.http.protocol.HTTP;

import java.util.Calendar;
import java.util.List;


public class IntentsActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intents);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_intents, menu);
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

    /**
     * Intent methods
     */

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }


    // ACTION_DIAL
    public void phoneIntent(View view) {
        Uri number = Uri.parse("tel:0609112567");
        Intent phoneIntent = new Intent(Intent.ACTION_DIAL, number);
        // Check if the activity is existent on the phone
        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(phoneIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (activities.size() > 0)
            startActivity(phoneIntent);
    }

    // ACTION_VIEW (map)
    public void mapIntent(View view) {
        // Map point to the adress
        Uri location = Uri.parse("geo:0,0?q=1600+Amphitheatre+Parkway,+Mountain+View,+California");
        // Or map point based on latitude/longitude
        // Uri location = Uri.parse("geo:37.422219,-122.08364?z=14"); // z param is zoom level
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, location);
        // Check if the activity is existent on the phone
        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(mapIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (activities.size() > 0)
            startActivity(mapIntent);
    }

    // ACTION_VIEW (webpage)
    public void webIntent(View view) {
        Uri webpage = Uri.parse("http://www.android.com");
        Intent webIntent = new Intent(Intent.ACTION_VIEW, webpage);
        // Check if the activity is existent on the phone
        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(webIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (activities.size() > 0)
            startActivity(webIntent);
    }

    // ACTION_SEND (send an email with an attachment
    public void mailIntent(View view) {
        Intent mailIntent = new Intent(Intent.ACTION_SEND);
        // The intent does not have a URI, so declare the "text/plain" MIME type
        mailIntent.setType(HTTP.PLAIN_TEXT_TYPE);
        mailIntent.putExtra(Intent.EXTRA_EMAIL, new String[]{"hydRwa@gmail.com", "jiri.slapnicka@canon.cz"}); // recipients
        mailIntent.putExtra(Intent.EXTRA_SUBJECT, "Email subject");
        mailIntent.putExtra(Intent.EXTRA_TEXT, "Email text message");
        // Multiple items can be attached by ArrayList of URI's
        mailIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///sdcard/image.JPG"));

        // Set up the Chooser (in case of multiple app can handle the intent)
        // Calling the chooser explicitly disables the "make default" option
        // which should be used with intents where user may use several different options
        String title = getResources().getString(R.string.chooser_title);
        // Create intent for a chooser
        Intent chooser = Intent.createChooser(mailIntent, title);
        // Check if the activity is existent on the phone
        PackageManager packageManager = getPackageManager();
        List activities = packageManager.queryIntentActivities(mailIntent, PackageManager.MATCH_DEFAULT_ONLY);
        if (activities.size() > 0)
            // Start activity via chooser
            startActivity(chooser);
    }

    // ACTION_INSERT (calendar intent)
    public void calendarIntent(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            Intent calendarIntent = new Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI);
            Calendar beginTime = Calendar.getInstance();
            beginTime.set(2015, 06, 11, 11, 30);
            Calendar endTime = Calendar.getInstance();
            endTime.set(2015, 06, 11, 13, 15);
            calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, beginTime.getTimeInMillis());
            calendarIntent.putExtra(CalendarContract.EXTRA_EVENT_END_TIME, endTime.getTimeInMillis());
            calendarIntent.putExtra(CalendarContract.Events.TITLE, "UK acceptance - failed :'( or not ?! :)");
            calendarIntent.putExtra(CalendarContract.Events.EVENT_LOCATION, "UK MFF");
            // Check if the activity is existent on the phone
            PackageManager packageManager = getPackageManager();
            List activities = packageManager.queryIntentActivities(calendarIntent, PackageManager.MATCH_DEFAULT_ONLY);
            if (activities.size() > 0)
                startActivity(calendarIntent);
        } else {
            Toast.makeText(this, "Min API for this intent is 14", Toast.LENGTH_LONG).show();
        }
    }



}

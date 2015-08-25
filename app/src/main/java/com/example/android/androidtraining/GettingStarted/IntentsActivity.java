package com.example.android.androidtraining.GettingStarted;

import android.app.SearchManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.provider.CalendarContract;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.android.androidtraining.R;
import com.google.android.gms.actions.ReserveIntents;

import org.apache.http.protocol.HTTP;

import java.util.Calendar;
import java.util.List;


public class IntentsActivity extends ActionBarActivity {

    static final int PICK_CONTACT_REQUEST = 1; // The request code
    static final int SEND_IMPLICIT_INTENT = 2;
    static final int REQUEST_IMAGE_CAPTURE = 3;
    static final int REQUEST_IMAGE_CONTENT = 4;
    static final int REQUEST_IMAGE_OPEN = 5;

    static final Uri mLocationPhotos = Uri.parse("file///sdcard");

    public static final String IMPLICIT_TEST = "com.example.android.androidtraining.IMPLICIT_INTENT";


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
        // Check which intent it is that we're responding to
        if (PICK_CONTACT_REQUEST == requestCode) { // Intent for pickContact()
            // Check if the request was successfull
            if (resultCode == RESULT_OK) {
                // Get the URI that points to the selected contact
                Uri contactUri = data.getData();
                // We only need the NUMBER column, because there will be only one row in the result
                String[] projection = {ContactsContract.CommonDataKinds.Phone.NUMBER};

                // Perform the query on the contact to get the NUMBER column
                // We don't need a selection or sort order (there's only one result for the given URI)
                // CAUTION: The query() method should be called from a separate thread to avoid blocking
                // your app's UI thread. (For simplicity of the sample, this code doesn't do that.)
                // Consider using CursorLoader to perform the query.
                Cursor cursor = getContentResolver().query(contactUri, projection, null, null, null);
                cursor.moveToFirst();

                // Retrieve the phone number from the NUMBER column
                int column = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
                String number = cursor.getString(column);
                Toast.makeText(this, "Intent result (phone number): " + number, Toast.LENGTH_LONG).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Noooo ! Why did you hit the back/cancel button ?! :(", Toast.LENGTH_LONG).show();
            }
            // Testing custom implicit intent and startActivityForResult()
        } else if (SEND_IMPLICIT_INTENT == requestCode) {
            if (RESULT_OK == resultCode) {
                Toast.makeText(this, "Result received !", Toast.LENGTH_SHORT).show();
            }
        } else if (REQUEST_IMAGE_CAPTURE == requestCode) {
            if (RESULT_OK == resultCode) {
                // TODO: Complete this to view the image !
                Toast.makeText(this, "Complete this to view the image !", Toast.LENGTH_SHORT).show();
                // Set the photo as background
                Bundle extras = data.getExtras();
                Bitmap imageBitmap = (Bitmap) extras.get("data");
                LinearLayout linearLayout = (LinearLayout) findViewById(R.id.linearlayout_intents);
                Drawable imageDrawable = new BitmapDrawable(imageBitmap);
                linearLayout.setBackground(imageDrawable);
            }
        } else if (REQUEST_IMAGE_CONTENT == requestCode) {
            if (RESULT_OK == resultCode) {
                Bitmap thumbnail = data.getParcelableExtra("data");
                Uri fullPhotoUri = data.getData();
                // TODO: Do something with the image (maybe display it ? what about thumbnail ?)
                Toast.makeText(this, "Result received, but that's lame. Do something with it !", Toast.LENGTH_LONG).show();
            }
        } else if (REQUEST_IMAGE_OPEN == requestCode) {
            if (RESULT_OK == resultCode) {
                Uri fullPhotoUri = data.getData();
                // TODO: Do something with the image (maybe display it ? what about thumbnail ?)
                Toast.makeText(this, "Result received, but that's lame. Do something with it !", Toast.LENGTH_LONG).show();
            }
        }
    }

    // ACTION_DIAL (phone app - dialer)
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

    // ACTION_PICK (pick contact)
    public void pickContactIntent(View view) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK, Uri.parse("content://contacts"));
        pickContactIntent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE); // Show user only contacts w/ phone numbers
        startActivityForResult(pickContactIntent, PICK_CONTACT_REQUEST);
    }

    // DISPLAY_MESSAGE (implicit intent)
    public void sendImplicitIntent(View view) {
        Button button = (Button) findViewById(R.id.btn_implicitTest);

        Intent implicitIntent = new Intent();
        implicitIntent.setAction("com.example.android.androidtraining.activity.DISPLAY_MESSAGE");
        // Just extract the title of the button as message
        implicitIntent.setType("text/plain");
        implicitIntent.putExtra(IMPLICIT_TEST, "Sent from a button !");
        // startActivityForResult(implicitIntent, SEND_IMPLICIT_INTENT);
        startActivity(implicitIntent);
    }

    // ACTION_SET_ALARM
    public void setAlarmIntent(View view) {
        Intent alarmIntent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, "Wake up !")
                .putExtra(AlarmClock.EXTRA_HOUR, 23)
                .putExtra(AlarmClock.EXTRA_MINUTES, 59);

        if (alarmIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(alarmIntent);
        }
    }

    // ACTION_SET_TIMER
    public void startTimerIntent(View view) {
        Intent timerIntent = new Intent(AlarmClock.ACTION_SET_TIMER)
                .putExtra(AlarmClock.EXTRA_MESSAGE, "Ready, Set, Go !")
                .putExtra(AlarmClock.EXTRA_LENGTH, 30)
                .putExtra(AlarmClock.EXTRA_SKIP_UI, true);

        if (timerIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(timerIntent);
        }
    }

    // ACTION_IMAGE_CAPTURE
    public void captureImageIntent(View view) {
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.withAppendedPath(mLocationPhotos, "test"));
        if (cameraIntent.resolveActivity(getPackageManager()) != null)
            startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE);
    }

    // ACTION_STILL_IMAGE_CAMERA
    public void captureStillImageIntent(View view) {
        Intent stillCameraIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
        if (stillCameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(stillCameraIntent);
        }
    }

    // ACTION_VIDEO_CAMERA
    public void startVideoIntent(View view) {
        Intent startVideoIntent =  new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        if (startVideoIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(startVideoIntent);
        }
    }

    // ACTION_GET_CONTENT
    public void selectImageIntent(View View) {
        Intent selectimageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        selectimageIntent.setType("image/*");
        if (selectimageIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(selectimageIntent, REQUEST_IMAGE_CONTENT);
        }
    }

    // ACTION_OPEN_DOCUMENT
    public void openImageIntent(View view) {
        Intent openImageIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        openImageIntent.setType("image/*");
        openImageIntent.addCategory(Intent.CATEGORY_OPENABLE); // To return only "openable" files that can be represented as a file stream with
        // Only the system receives the ACTION_OPEN_DOCUMENT, so no need to test.
        startActivityForResult(openImageIntent, REQUEST_IMAGE_OPEN);
    }

    // ACTION_RESERVE_TAXI_RESERVATION
    public void callCarIntent(View view) {
        Intent callCarIntent = new Intent(ReserveIntents.ACTION_RESERVE_TAXI_RESERVATION);
        if (callCarIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(callCarIntent);
        } else {
            Toast.makeText(this, "No application found !", Toast.LENGTH_LONG).show();
        }
    }

    // ACTION_VIEW (play music)
    public void playMediaIntent(View view) {
        Uri uriFile = Uri.parse("file:///sdcard/Ringtones/hangouts_message.ogg");
        Intent musicIntent = new Intent(Intent.ACTION_VIEW);
        musicIntent.setType("audio/*");
        musicIntent.setData(uriFile);
        if (musicIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(musicIntent);
        } else {
            Toast.makeText(this, "No application found !", Toast.LENGTH_LONG).show();
        }

    }

    // ACTION_MEDIA_PLAY_FROM_SEARCH
    public void playSearchArtistIntent(View view) {
        // TODO: Add Destorm mp3 into internal memory
        String artist = "Destorm";
        Intent playSearchIntent = new Intent(MediaStore.INTENT_ACTION_MEDIA_PLAY_FROM_SEARCH);
        playSearchIntent.putExtra(MediaStore.EXTRA_MEDIA_FOCUS, MediaStore.Audio.Artists.ENTRY_CONTENT_TYPE);
        playSearchIntent.putExtra(MediaStore.EXTRA_MEDIA_ARTIST, artist);
        playSearchIntent.putExtra(SearchManager.QUERY, artist);
        if (playSearchIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(playSearchIntent);
        }
    }

   // ACTION_SEARCH
    public void webSearchIntent(View view) {
        Intent webSearchIntent = new Intent(Intent.ACTION_SEARCH);
        webSearchIntent.putExtra(SearchManager.QUERY, "Whatever");
        if (webSearchIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(webSearchIntent);
        } else {
            Toast.makeText(this, "No application found !", Toast.LENGTH_LONG).show();
        }
    }

    // ACTION_SETTINGS
    public void settingsIntent(View view) {
        Intent settingsIntent = new Intent(Settings.ACTION_WIFI_SETTINGS);
        if (settingsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(settingsIntent);
        } else {
            Toast.makeText(this, "No application found !", Toast.LENGTH_LONG).show();
        }
    }

    // ACTION_SENDTO
    public void composeMmsMessageIntent(View view) {
        Intent mmsIntent = new Intent(Intent.ACTION_SEND);
        mmsIntent.setData(Uri.parse("smsto:")); // This ensures only SMS apps respond
        mmsIntent.setType("text/plain");
        mmsIntent.setType("image/*");
        mmsIntent.putExtra("sms_body", "Hello there !");
        mmsIntent.putExtra(Intent.EXTRA_STREAM, Uri.parse("file:///image.JPG"));
        if (mmsIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mmsIntent);
        } else {
            Toast.makeText(this, "No application found !", Toast.LENGTH_LONG).show();
        }
    }

}

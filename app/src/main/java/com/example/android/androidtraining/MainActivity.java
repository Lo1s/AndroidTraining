// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.example.android.androidtraining;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileNotFoundException;

// Referenced classes of package com.example.android.androidtraining:
//            DisplayImageActivity, DisplayMessageActivity

public class MainActivity extends ActionBarActivity {

    private static final String TAG = "MainActivity";

    // Bind service variables
    BindService mBindService;
    boolean mBound = false;

    // Variables for requesting file from internal memory
    private final int REQUEST_FILE = 0;
    private Intent mRequestFileIntent;
    private ParcelFileDescriptor mInputPFD;

    /**
     * Global variables
     */
    // Intent extra message
    public static final String EXTRA_MESSAGE = "com.example.android.androidtraining.MESSAGE";
    // Identifier for the saved instance
    static final String COUNT = "counter";
    // Counter
    public static int count;
    // ActionBar listener
    private ActionBar.OnNavigationListener mOnNavigationListener;
    // TextViews global declaration
    public TextView activityMonitor;
    public TextView counter;
    // SharedPreferences global decleration
    SharedPreferences sharedPreferences;
    // NFC transfer
    NfcAdapter mNfcAdapter;
    // Flag to indicate that Android Beam is available
    boolean mAndroidBeamAvailable = false;
    private Uri[] mFileUris = new Uri[10];
    private String transferfile = "rafa.jpg";
    private File extDir = new File("/sdcard/files/myimages/");
    private File requestFile = new File(extDir, transferfile);
    private FileUriCallback mFileUriCallback;
    // A File object containing the path to the transferred files
    private File mParentPath;
    private Intent mIntentNFCtransfer;

    /**
     * Defines callbacks for service binding, passed to bindService()
     */
    private ServiceConnection mConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            BindService.LocalBinder binder = (BindService.LocalBinder) service;
            mBindService = binder.getService();
            mBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBound = false;
        }
    };

    public MainActivity() {
    }

    /**
     * Lifecycle methods
     */
    protected void onCreate(Bundle bundle) {
        // Always call super for lifecycle methods
        super.onCreate(bundle);
        // Setting up the layout for the activity
        setContentView(R.layout.activity_main);
        // Initializing the TextViews that are handled programatically
        counter = (TextView) findViewById(R.id.counter);
        activityMonitor = (TextView) findViewById(R.id.activityMonitor);
        // If the app is started for the first time load the count value from the shared preferences
        if (count == 0) {
            count = restoreCountFromSharedPreferences();
        }
        // Set the counter after creating
        counter.setText(getString(R.string.count) + ": " + count);

        // Checking the SDK during runtime
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
            disableToast();
        }
        // Activity monitor message
        activityMonitor.append(getLocalClassName() + ": onCreate()" + System.getProperty("line.separator"));

        // Check programmatically that device & android api supports NFC
        // NFC isn't available on the device
        if (!getPackageManager().hasSystemFeature(PackageManager.FEATURE_NFC)) {
            /*
             * Disable NFC features here.
             * For example, disable menu items or buttons that activate
             * NFC-related features
             */
            // Android Beam file transfer isn't supported
        } else if (Build.VERSION.SDK_INT < Build.VERSION_CODES.JELLY_BEAN_MR1) {
            // If Android Beam isn't available, don't continue.
            mAndroidBeamAvailable = false;
            /*
             * Disable Android Beam file transfer features here.
             */
        } else {
            // Android Beam file transfer is available, continue
            mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
            /*
             * Instantiate a new FileUriCallback to handle requests for
             * URIs
             */
            mFileUriCallback = new FileUriCallback();
            // Set the dynamic callback for URI requests.
            mNfcAdapter.setBeamPushUrisCallback(mFileUriCallback, this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Activity monitor message
        activityMonitor.append(getLocalClassName() + ": onStart()" + System.getProperty("line.separator"));

        // Bind to BindService
        Intent bindIntent = new Intent(this, BindService.class);
        bindService(bindIntent, mConnection, Context.BIND_AUTO_CREATE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        // Activity monitor message
        activityMonitor.append(getLocalClassName() + ": onResume()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Get share preferences
        saveCountToSharedPreferences();
        // Activity monitor message
        activityMonitor.setText(getLocalClassName() + ": onPause()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // Activity monitor message
        activityMonitor.setText(getLocalClassName() + ": onRestart()" + System.getProperty("line.separator"));
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Activity monitor message
        activityMonitor.setText(getLocalClassName() + ": onStop()" + System.getProperty("line.separator"));

        // Unbind from the service
        if (mBound) {
            unbindService(mConnection);
            mBound = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Activity monitor message
        activityMonitor.setText(getLocalClassName() + ": onDestroy()" + System.getProperty("line.separator"));
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Handling the selected item within the ActionBar
    public boolean onOptionsItemSelected(MenuItem menuitem) {
        int itemId = menuitem.getItemId();
        if (itemId == R.id.action_settings) {
            return true;
        }
        switch (itemId) {
            default:
                return super.onOptionsItemSelected(menuitem);

            case R.id.menu_display:
                display();
                return true;

            case R.id.menu_add:
                add();
                break;
            case R.id.menu_tabs:
                Intent intentTabs = new Intent(this, TabActivity.class);
                startActivity(intentTabs);
                break;
            case R.id.menu_dropdown:
                Intent intentDropDown = new Intent(this, DropDownNavigationActivity.class);
                startActivity(intentDropDown);
                break;
            case R.id.menu_login:
                Intent intentLogin = new Intent(this, LoginActivity.class);
                startActivity(intentLogin);
                break;
        }
        return true;
    }

    // Saving the instance for keeping the value in counter
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(COUNT, count);
        super.onSaveInstanceState(outState);
    }

    // Restoring the saved instance
    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        count = savedInstanceState.getInt(COUNT);
        counter.setText(new StringBuilder("Count: ").append(count));
    }

    // Access the requested file from the internal memory
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // If the selection didn't work
        if (resultCode != RESULT_OK) {
            // Exit without doing anything else
            return;
        } else {
            // Get the file's content URI from the incoming Intent
            Uri returnUri = data.getData();
            /*
             * Try to open the file for "read" access using the
             * returned URI. If the file isn't found, write to the
             * error log and return.
             */
            try {
                /*
                 * Get the content resolver instance for this context, and use it
                 * to get a ParcelFileDescriptor for the file.
                 */
                mInputPFD = getContentResolver().openFileDescriptor(returnUri, "r");
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
                Log.e(TAG, "File not found");
                return;
            }
            // Get a regular file descriptor for the file
            FileDescriptor fd = mInputPFD.getFileDescriptor();
            // ...
            // Get the files MIME type
            String mimeType = getContentResolver().getType(returnUri);
            // Query the server's app to get the file's name and size
            Cursor returnCursor = getContentResolver().query(returnUri, null, null, null, null);
            /**
             * Get the column indexes of the data in the Cursor,
             * move to the first row in the Cursor, get the data,
             * and display it.
             */
            int nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();
            Toast.makeText(this, "Name:" + returnCursor.getString(nameIndex)
                    + ", Size: " + returnCursor.getString(sizeIndex), Toast.LENGTH_LONG).show();
        }
    }

    // TODO: Check the sending via NFC transfer
    // NFC transfer
    // ... set up the URIs

    /**
     * Callback that Android Beam file transfer calls to get
     * files to share
     */
    private class FileUriCallback implements NfcAdapter.CreateBeamUrisCallback {

        public FileUriCallback() {

        }

        /**
         * Create content URIs as needed to share with another device
         */
        @Override
        public Uri[] createBeamUris(NfcEvent event) {
            requestFile.setReadable(true, false);
            // Get a URI for the File and add it to the list of URIs
            Uri fileUri = Uri.fromFile(requestFile);
            if (fileUri != null) {
                mFileUris[0] = fileUri;
            } else {
                Log.e(TAG, "No File URI available for file");
            }
            return mFileUris;
        }
    }


    /**
     * Own methods
     */
    // Sending message from the EditText to anither activity via Intent
    public void sendMessage(View view) {
        Intent intent = new Intent(this, DisplayMessageActivity.class);
        EditText editText = (EditText) findViewById(R.id.edit_message);
        String message = editText.getText().toString();
        intent.putExtra(EXTRA_MESSAGE, message);
        startActivity(intent);
    }

    // Testing Toasts (Checking the minimal SDK version)
    public void disableToast() {
        Toast.makeText(this, "Min SDK Test", Toast.LENGTH_SHORT).show();
    }

    // Increment counter
    public void add() {
        count = count + 1;
        ((TextView) findViewById(R.id.counter)).setText((new StringBuilder()).append(getString(R.string.count) + ": ").append(count).toString());
    }

    // ActionBar method for DisplayActivity
    public void display() {
        startActivity(new Intent(this, DisplayImageActivity.class));
    }

    // Save the count value into SharedPreferences
    public void saveCountToSharedPreferences() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt(getString(R.string.count_value_key), count);
        editor.commit();
    }

    // Load the count value from the SharedPreferences
    public int restoreCountFromSharedPreferences() {
        sharedPreferences = getPreferences(MODE_PRIVATE);
        return sharedPreferences.getInt(getString(R.string.count_value_key), 0);
    }

    // Starts the activity with intent test
    public void startIntentsActivity(View view) {
        Intent intent = new Intent(this, IntentsActivity.class);
        startActivity(intent);
    }

    // Starts the HelloIntentService service
    public void startIntentService(View view) {
        Intent startIntentService = new Intent(MainActivity.this, HelloIntentService.class);
        startService(startIntentService);

    }

    // Starts the HelloService service
    public void startService(View view) {
        Intent startService = new Intent(this, HelloService.class);
        startService(startService);
    }

    // Starts the binded service
    public void startBindService(View view) {
        // Call a method from the LocalService.
        // However, if this call were something that might hang, then this request should
        // occur in a separate thread to avoid slowing down the activity performance.
        if (mBound) {
            int num = mBindService.getRandomNumber();
            Toast.makeText(this, num + "", Toast.LENGTH_SHORT).show();
        }
    }

    // Request the file from the internal memory
    public void requestFile(View view) {
        mRequestFileIntent = new Intent(Intent.ACTION_PICK);
        mRequestFileIntent.setType("image/jpg");

        if (mRequestFileIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(mRequestFileIntent, REQUEST_FILE);
        }
    }

     /** TODO: Check the receiving via NFC transfer
     * Called from onNewIntent() for a SINGLE_TOP Activity
     * or onCreate() for a new Activity. For onNewIntent(),
     * remember to call setIntent() to store the most
     * current Intent
     *
     */
    private void handleViewIntent() {
        // Get the intent action
        mIntentNFCtransfer = getIntent();
        String action = mIntentNFCtransfer.getAction();
        /*
         * For ACTION_VIEW, the Activity is being asked to display data.
         * Get the URI.
         */
        if (TextUtils.equals(action, Intent.ACTION_VIEW)) {
            // Get the URI from the Intent
            Uri beamUri = mIntentNFCtransfer.getData();
            /*
             * Test for the type of URI, by getting its scheme value
             */
            if (TextUtils.equals(beamUri.getScheme(), "file")) {
                mParentPath = handleContentUri(beamUri);
            }
        }
    }

    public String handleFileUri(Uri beamUri) {
        // Get the path part of the URI
        String fileName = beamUri.getPath();
        // Create a File object for this filename
        File copiedFile = new File(fileName);
        // Get a string containing the file's parent directory
        return copiedFile.getParent();
    }

    public File handleContentUri(Uri beamUri) {
        // Position of the filename in the query Cursor
        int filenameIndex;
        // File object for the filename
        File copiedFile;
        // The filename stored in MediaStore
        String fileName;
        // Test the authority of the URI
        if (!TextUtils.equals(beamUri.getAuthority(),MediaStore.AUTHORITY)) {
            /*
             * Handle content URIs for other content providers
             */
        } else {
            // For a MediaStore content URI
            // Get the column that contains the file name
            String[] projection = { MediaStore.MediaColumns.DATA };
            Cursor pathCursor = getContentResolver().query(beamUri, projection, null, null, null);
            // Check for a valid cursor
            if (pathCursor != null && pathCursor.moveToFirst()) {
                // Get the column index in the Cursor
                filenameIndex = pathCursor.getColumnIndex(MediaStore.MediaColumns.DATA);
                // Get the full file name including path
                fileName = pathCursor.getString(filenameIndex);
                // Create a File object for the filename
                copiedFile = new File(fileName);
                return new File(copiedFile.getParent());
            } else {
                // The query didn't work; return null
                return null;
            }
        }
        return null;
    }

    // Start media activity
    public void startMediaActivity(View view) {
        startActivity(new Intent(this, MediaActivity.class));
    }

    public static int incrementCount() {
        count++;
        return count;
    }

    // Start Animation activity
    public void startAnimationActivity(View view) {
        startActivity(new Intent(this, AnimationActivity.class));
    }

    // Start the Connectivity & Cloud Activity
    public void startConnectivityActivity(View view) {
        startActivity(new Intent(this, ConnectivityActivity.class));
    }
}


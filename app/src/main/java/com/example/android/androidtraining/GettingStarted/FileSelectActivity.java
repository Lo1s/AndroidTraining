package com.example.android.androidtraining.GettingStarted;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.androidtraining.R;

import java.io.File;


public class FileSelectActivity extends ActionBarActivity {

    // The path to the root of this app's internal storage
    private File mPrivateRootDir;
    // The path to the "images" subdirectory
    private File mImagesDir;
    // Array of files in the images subdirectory
    File[] mImageFiles;
    // Array of filenames corresponding to mImageFiles
    String[] mImageFileNames;
    // Intent to send back to apps that requested file
    Intent mResultIntent;

    // Initialize
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);

        /**
         *  1st step: Define permissions in your manifest file - http://developer.android.com/training/secure-file-sharing/setup-sharing.html
         *  2nd step: Set up the selection activity - http://developer.android.com/training/secure-file-sharing/share-file.html
         *  3rd step: Handle the selection (e.g. the click in the ListView) & generate the URI to file
         *  4th step: Grant permissions for the other app to view the file
         *
         *  */

        // Set up an Intent to send back to apps that request a file
        mResultIntent = new Intent("com.example.android.androidtraining.ACTION_RETURN_FILE");
        // Get the files/ subdirectory of internal storage
        mPrivateRootDir = getFilesDir();
        // Get the files/images subdirectory;
        mImagesDir = new File(mPrivateRootDir, "myimages");
        // Get the files in the images subdirectory
        mImageFiles = mImagesDir.listFiles();

        for (int i = 0; i < mImageFiles.length; i++) {
            mImageFileNames[i] = mImageFiles[i].getAbsolutePath();
        }

        // Set the Activity's result to null to begin with
        setResult(RESULT_CANCELED, null);
         /*
         * TODO: Display the file names in the ListView mFileListView.
         * Back the ListView with the array mImageFilenames, which
         * you can create by iterating through mImageFiles and
         * calling File.getAbsolutePath() for each File
         * + listViewListener @ http://developer.android.com/training/secure-file-sharing/share-file.html
         */

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_file_select, menu);
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

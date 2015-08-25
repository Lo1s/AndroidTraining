// Decompiled by Jad v1.5.8e. Copyright 2001 Pavel Kouznetsov.
// Jad home page: http://www.geocities.com/kpdus/jad.html
// Decompiler options: braces fieldsfirst space lnc 

package com.example.android.androidtraining.GettingStarted;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.ShareActionProvider;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.example.android.androidtraining.R;

// Referenced classes of package com.example.android.androidtraining:
//            DisplayMessageActivity

public class DisplayImageActivity extends ActionBarActivity implements ButtonsFragment.OnButtonClickListener {


    // TODO: Transparent Action Bar for Images

    // Declaring the listener for the buttons fragment
    ButtonsFragment.OnButtonClickListener onButtonClickListener;

    // Testing the ShareActionProvider for sharing the image on social networks
    private ShareActionProvider mShareActionProvider;

    @Override
    public void onButtonClick(View view) {
        int id = view.getId();
        ImageFragment imageFragment = (ImageFragment) getSupportFragmentManager().findFragmentById(R.id.image_fragment);
        switch (id) {
            case R.id.btnDogs:
                // The user pressed the button to display prairie dogs image
                if (imageFragment != null) {
                    // If image_fragment (id within the XML 'land\activity_display_image') is available we are in two pane layout
                    imageFragment.setImage(R.drawable.prairiedogs);
                } else {
                    // If the fragment is not available, we're in the one-pane layout and must swap fragments...
                    // Create fragment and give it an argument for the selected image
                    ImageFragment newFragment = new ImageFragment();
                    Bundle args = new Bundle();
                    args.putInt(ImageFragment.ARG_IMG, R.drawable.prairiedogs);
                    newFragment.setArguments(args);
                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    // Replace whatever is in the fragment_container view with this fragment,
                    // and add the transaction to the back stack so the user can navigate back
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null).commit();
                }
                break;
            case R.id.btnRafa:
                // The user pressed the button to display the Rafa image
                if (imageFragment != null) {
                    imageFragment.setImage(R.drawable.rafa);
                } else {
                    ImageFragment newFragment = new ImageFragment();

                    Bundle args = new Bundle();
                    args.putInt(ImageFragment.ARG_IMG, R.drawable.rafa);
                    newFragment.setArguments(args);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null).commit();
                }
                break;
            case R.id.btnAndroid:
                // The user pressed the button to display the Android image
                if (imageFragment != null) {
                    imageFragment.setImage(R.drawable.androidparty);
                } else {
                    ImageFragment newFragment = new ImageFragment();

                    Bundle args = new Bundle();
                    args.putInt(ImageFragment.ARG_IMG, R.drawable.androidparty);
                    newFragment.setArguments(args);

                    FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, newFragment);
                    transaction.addToBackStack(null).commit();
                }
                break;
        }
    }

    public DisplayImageActivity() {

    }

    // Creating Intent for ShareActionProvider
    private Intent getDefaultIntent() {
        Intent intent = new Intent("android.intent.action.SEND");
        intent.setType("image/*");
        // TODO: Update the uri for selected image instead of the default one
        Uri uriImage = Uri.parse("android.resource://" + getPackageName() + "/drawable/rafa.jpg");
        intent.putExtra(Intent.EXTRA_STREAM, uriImage);
        return intent;
    }

    public Intent getSupportParentActivityIntent() {
        return new Intent(this, DisplayMessageActivity.class);
    }

    protected void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        getSupportActionBar();
        setContentView(R.layout.activity_display_image);

        // Check if the phone is in the portrait mode,
        // if so - it is necessary to add the fragment into the container
        // (otherwise the layout is defined in the xml)
        if (findViewById(R.id.fragment_container) != null) {
            // Check if created for the first time
            if (bundle != null)
                return;

            // Create an instance of the fragment
            ButtonsFragment buttonsFragment = new ButtonsFragment();

            // In case this activity was started with special instructions from an Intent,
            // pass the Intent's extras to the fragment as arguments
            buttonsFragment.setArguments(getIntent().getExtras());

            // Add the fragment to the 'fragment_container' FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, buttonsFragment).commit();
        }
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_display_image, menu);
        mShareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(menu.findItem(R.id.action_share));
        mShareActionProvider.setShareIntent(getDefaultIntent());
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem menuitem) {
        if (menuitem.getItemId() == R.id.action_settings) {
            return true;
        } else {
            mShareActionProvider.setShareIntent(getDefaultIntent());
            return super.onOptionsItemSelected(menuitem);
        }
    }
}

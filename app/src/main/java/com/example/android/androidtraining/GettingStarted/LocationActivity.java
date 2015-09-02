package com.example.android.androidtraining.GettingStarted;

import android.content.Intent;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.androidtraining.R;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import java.text.DateFormat;
import java.util.Date;

public class LocationActivity extends AppCompatActivity implements
        GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks,
        LocationListener {

    private GoogleApiClient mGoogleApiClient;
    private TextView textViewLongitude;
    private TextView textViewLatitude;
    private TextView textViewLastUpdated;
    public static final String SAVED_LONGITUDE = "longitude";
    public static final String SAVED_LATITUDE = "latitude";
    private boolean mRequestingLocationUpdates;

    // TODO: 1.9.2015 Check it at home (mLocation is null)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and
            // make sure that the Start Updates and Stop Updates buttons are
            // correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
            }
            // Update the value of mCurrentLocation from the Bundle and update the
            // UI to show the correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that
                // mCurrentLocation is not null.
                mLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
        }

        textViewLongitude = (TextView) findViewById(R.id.textView_longitude);
        textViewLatitude = (TextView) findViewById(R.id.textView_latitude);
        textViewLastUpdated = (TextView) findViewById(R.id.textView_last_updated);
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    protected void stopLocationUpdates() {
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected() && !mRequestingLocationUpdates) {
                startLocationUpdates();
            }
        }
    }

    public static final String REQUESTING_LOCATION_UPDATES_KEY = "req_loc_update_key";
    public static final String LOCATION_KEY = "loc_key";
    public static final String LAST_UPDATED_TIME_STRING_KEY = "last_update_key";

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        outState.putParcelable(LOCATION_KEY, mLocation);
        outState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_location, menu);
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

    // Get location via Google Play Services Api
    public void getLocation(View view) {
        buildGoogleApiClient();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    // Interface methods
    private Location mLocation;
    private double latitude;
    private double longitude;

    @Override
    public void onConnected(Bundle bundle) {
        Log.i("GoogleApiClient", "Connection Successful");
        mLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mLocation != null) {
            latitude = mLocation.getLatitude();
            longitude = mLocation.getLongitude();
            textViewLatitude.setText("Latitude: " + latitude);
            textViewLongitude.setText("Longitude: " + longitude);
            textViewLastUpdated.setText("Last Updated: " + mLastUpdateTime);
            // Determine whether a Geocoder is available.
            if (!Geocoder.isPresent()) {
                Toast.makeText(LocationActivity.this, "No Geocoder available", Toast.LENGTH_SHORT)
                        .show();
                return;
            }

            if (mAddressRequested) {
                startIntentService();
            }
        }

        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    // Start location updates
    public void startLocationUpdates() {
        createLocationRequest();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    private String mLastUpdateTime;

    @Override
    public void onLocationChanged(Location location) {
        mLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
    }

    public void updateUI() {
        textViewLatitude.setText("Latitude: " + mLocation.getLatitude());
        textViewLongitude.setText("Longitude: " + mLocation.getLongitude());
        textViewLastUpdated.setText("Last Updated: " + mLastUpdateTime);
    }

    @Override
    public void onConnectionSuspended(int i) {
        Toast.makeText(LocationActivity.this, "Connection suspended", Toast.LENGTH_SHORT).show();
        Log.i("GoogleApiClient", "Connection Suspended");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Toast.makeText(LocationActivity.this, "Connection Failed", Toast.LENGTH_SHORT).show();
        Log.i("GoogleApiClient", "Connection Failed");
    }

    // Create Location Request for periodic update of the location
    private LocationRequest mLocationRequest;
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(2000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    // Frequent updates allowed ?
    public void frequentUpdates(View view) {
        CheckBox chkBoxUpdates = (CheckBox) findViewById(R.id.checkBox_location_updates);
        mRequestingLocationUpdates = chkBoxUpdates.isChecked();
        Toast.makeText(LocationActivity.this, "Location will be updated", Toast.LENGTH_SHORT).show();
    }

    private String mAddressOutput;
    public class AddressResultReceiver extends ResultReceiver {

        public AddressResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            // Display the address string
            // or an error message sent from the intent service
            mAddressOutput = resultData.getString(FetchAddressIntentService
                    .Constants.RESULT_DATA_KEY);
            TextView textViewAddress = (TextView) findViewById(R.id.textView_address);
            textViewAddress.setText("Address: " + mAddressOutput);

            // Show a toast message if an address was found.
            showToast("Address Found !");
        }
    }

    public void showToast(String string) {
        Toast.makeText(LocationActivity.this, string, Toast.LENGTH_SHORT).show();
    }

    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    private boolean mAddressRequested = false;
    // Get Address from the longitude and latitude
    public void getAddress(View view) {
        // Only start the service to fetch the address if GoogleApiClient is
        // connected.
        if (mGoogleApiClient != null) {
            if (mGoogleApiClient.isConnected() && mLocation != null) {
                startIntentService();
            }
        } else {
            Toast.makeText(LocationActivity.this, "GoogleApiClient not connected", Toast.LENGTH_SHORT).show();
        }

        // If GoogleApiClient isn't connected, process the user's request by
        // setting mAddressRequested to true. Later, when GoogleApiClient connects,
        // launch the service to fetch the address. As far as the user is
        // concerned, pressing the Fetch Address button
        // immediately kicks off the process of getting the address.
        mAddressRequested = true;
    }

    public void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
    }

    // TODO: 1.9.2015 Add Geofencing
    public void startGeofencing(View view) {
        Toast.makeText(LocationActivity.this, "TODO !", Toast.LENGTH_SHORT).show();
    }
}

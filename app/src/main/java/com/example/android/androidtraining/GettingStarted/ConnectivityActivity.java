package com.example.android.androidtraining.GettingStarted;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.android.androidtraining.ChatApp.NsdActivity;
import com.example.android.androidtraining.R;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;

public class ConnectivityActivity extends AppCompatActivity {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connectivity);
    }

    // Start NSD
    public void startNSD(View view) {
        startActivity(new Intent(this, NsdActivity.class));
    }

    // Check networking
    public void checkNetwork(View view) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected()) {
            // fetch data
            Toast.makeText(ConnectivityActivity.this, "Network connection established", 
                    Toast.LENGTH_SHORT).show();
        } else {
            // display error
            Toast.makeText(ConnectivityActivity.this, "Network connection failed",
                    Toast.LENGTH_SHORT).show();
        }
    }

    // Check networking in the background thread via AsyncTask
    private static final String DEBUG_TAG = "NetworkingTest";
    private EditText urlText;
    private TextView textView;
    public static final String WEB_STRING = "webString";

    // When user clicks button, calls AsyncTask.
    // Before attempting to fetch the URL, makes sure that there is a network connection.
    public void checkNetworkInBackground(View view) {
        urlText = (EditText) findViewById(R.id.editText_url);
        String stringURL = urlText.getText().toString();
        if (stringURL.length() > 1) {
            ConnectivityManager connMgr = (ConnectivityManager)
                    getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            if (networkInfo != null && networkInfo.isConnected()) {
                new NetworkAsycTask().execute(stringURL);
            } else {
                // connection failed
                Toast.makeText(ConnectivityActivity.this, "Network connection failed",
                        Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(ConnectivityActivity.this, "Enter a valid URL", Toast.LENGTH_SHORT)
                    .show();
        }
    }

    // Uses AsyncTask to create a task away from the main UI thread. This task takes a
    // URL string and uses it to create an HttpUrlConnection. Once the connection
    // has been established, the AsyncTask downloads the contents of the webpage as
    // an InputStream. Finally, the InputStream is converted into a string, which is
    // displayed in the UI by the AsyncTask's onPostExecute method.
    private class NetworkAsycTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            // params comes from the execute() call: params[0] is the url.
            try {
                return downloadUrl(urls[0]);
            } catch (IOException ex) {
                return "Unable to retrieve web page. URL may be invalid.";
            }
        };

        // onPostExecute displays the results of the AsyncTask.

        @Override
        protected void onPostExecute(String s) {
            Intent intent = new Intent(getApplicationContext(), ShowWebPageActivity.class);
            intent.putExtra(WEB_STRING, s);
            startActivity(intent);
        }
    }

    // Given a URL, establishes an HttpUrlConnection and retrieves
    // the web page content as a InputStream, which it returns as
    // a string.
    private String downloadUrl(String myUrl) throws IOException {
        InputStream stream = null;
        // Only display the first 500 characters of the retrieved
        // web page content.
        int len = 500;

        try {
            URL url = new URL(myUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(10000 /* milliseconds */);
            conn.setConnectTimeout(15000 /* milliseconds */);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            // Starts the query
            conn.connect();
            int response = conn.getResponseCode();
            Log.d(DEBUG_TAG, "The response is: " + response);
            stream = conn.getInputStream();

            // Convert the InputStream into a String
            String contentAsString = readIt(stream, len);
            return contentAsString;

            // Makes sure that the InputStream is closed after the app is
            // finished using it.
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }


    // Reads an InputStream and converts it to a String
    public String readIt(InputStream stream, int len) throws IOException,
            UnsupportedEncodingException {
        Reader reader = null;
        reader = new InputStreamReader(stream, "UTF-8");
        char[] buffer = new char[len];
        reader.read(buffer);
        return new String(buffer);
    }

    // Tests the connectivity
    public void testConnection(View view) {
        ConnectivityManager connMgr = (ConnectivityManager)
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        boolean isWifiConnection = networkInfo.isConnected();
        networkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        boolean isMobileConnection = networkInfo.isConnected();

        TextView textView_mobile = (TextView) findViewById(R.id.textView_mobile_connection);
        TextView textView_wifi = (TextView) findViewById(R.id.textView_wifi_connection);

        if (isMobileConnection) {
            textView_mobile.setText("Mobile connection: On");
        } else {
            textView_mobile.setText("Mobile connection: Off");
        }

        if (isWifiConnection) {
            textView_wifi.setText("Wi-Fi connection: On");
        } else {
            textView_wifi.setText("Wi-Fi connection: Off");
        }
    }

    // Start XML Parser activity
    public void startXmlParser(View view) {
        startActivity(new Intent(this, NetworkActivity.class));
    }

    TextView textViewVolley;
    // Create new Volley request
    public void volleyRequest(View view) {
        textViewVolley = (TextView) findViewById(R.id.textView_volley_result);
        // Instantiate the RequestQueue.
        com.android.volley.RequestQueue requestQueue = Volley.newRequestQueue(this);
        String url = "http://www.google.com";

        // Request a string response from the provided URL.
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                        textViewVolley.setText("Result: " + response.substring(0, 500));
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                textViewVolley.setText("That did not work !");
            }
        });

        // Add the request to the queue
        requestQueue.add(stringRequest);
    }

}

package com.example.android.androidtraining.ChatApp;

import android.content.Context;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;

/**
 * Created by jslapnicka on 24.8.2015.
 */
public class NsdHelper {
    private ServerSocket mServerSocket;
    private int mLocalPort;
    private NsdManager.RegistrationListener mRegistrationListener;
    private String mServiceName;
    private NsdManager mNsdManager;
    private NsdManager.DiscoveryListener mDiscoveryListener;
    private final String LOG_TAG = "NsdHelper";
    public static final String SERVICE_TYPE = "_http._tcp.";
    private NsdManager.ResolveListener mResolveListener;
    private NsdServiceInfo mService;
    private Context mContext;

    public NsdHelper(Context context) {
        mContext = context;
        mNsdManager = (NsdManager) context.getSystemService(Context.NSD_SERVICE);
    }

    // Register NSD service
    public void registerService(int port) {
        // Create the NsdServiceInfo object, and populate it.
        NsdServiceInfo serviceInfo = new NsdServiceInfo();

        // The name is subject to change based on conflicts
        // with other services advertised on the same network.
        serviceInfo.setServiceName("NsdChat");
        serviceInfo.setServiceType("_http._tcp.");
        serviceInfo.setPort(port);

        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);
    }

    // Initialize a socket if used
    public void initializeServerSocket() {
        // Initialize a server socket on the next available port.
        try {
            mServerSocket = new ServerSocket(0);

            // Store the chosen port.
            mLocalPort = mServerSocket.getLocalPort();
        } catch (IOException ex) {
            Toast.makeText(mContext, "Error during port initialization", Toast.LENGTH_SHORT).show();
            ex.printStackTrace();
        }

    }

    // Implement Registration listener for info about success/failure of registration/unregistration
    public void initializeRegistrationListener() {
        mRegistrationListener = new NsdManager.RegistrationListener() {
            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed.  Put debugging code here to determine why.
            }

            @Override
            public void onServiceRegistered(NsdServiceInfo serviceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                mServiceName = serviceInfo.getServiceName();
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo serviceInfo) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
            }
        };
    }

    // Initialize a listener for services that are broadcasted on the network
    public void initializeDiscoveryListener() {

        // Instantiate a new DiscoveryListener
        mDiscoveryListener = new NsdManager.DiscoveryListener() {
            @Override
            public void onStartDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(LOG_TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            @Override
            public void onStopDiscoveryFailed(String serviceType, int errorCode) {
                Log.e(LOG_TAG, "Discovery failed: Error code:" + errorCode);
                mNsdManager.stopServiceDiscovery(this);
            }

            //  Called as soon as service discovery begins.
            @Override
            public void onDiscoveryStarted(String serviceType) {
                Log.d(LOG_TAG, "Service discovery started");
            }

            @Override
            public void onDiscoveryStopped(String serviceType) {
                Log.i(LOG_TAG, "Discovery stopped: " + serviceType);
            }

            @Override
            public void onServiceFound(NsdServiceInfo serviceInfo) {
                // A service was found!  Do something with it.
                Log.d(LOG_TAG, "Service discovery success" + serviceInfo);
                if (!serviceInfo.getServiceType().equals(SERVICE_TYPE)) {
                    // Service type is the string containing the protocol and
                    // transport layer for this service.
                    Log.d(LOG_TAG, "Unknown service type: " + serviceInfo.getServiceType());
                } else if (serviceInfo.getServiceName().equals(mServiceName)) {
                    // The name of the service tells the user what they'd be
                    // connecting to. It could be "Bob's Chat App".
                    Log.d(LOG_TAG, "Same machine: " + mServiceName);
                } else if (serviceInfo.getServiceName().contains("NsdChat")) {
                    mNsdManager.resolveService(serviceInfo, mResolveListener);
                }
            }

            @Override
            public void onServiceLost(NsdServiceInfo serviceInfo) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(LOG_TAG, "service lost" + serviceInfo);
            }
        };
    }

    public void initializeResolveListener() {
        mResolveListener = new NsdManager.ResolveListener() {
            @Override
            public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Called when the resolve fails.  Use the error code to debug.
                Log.e(LOG_TAG, "Resolve failed" + errorCode);
            }

            @Override
            public void onServiceResolved(NsdServiceInfo serviceInfo) {
                Log.e(LOG_TAG, "Resolve succeeded." + serviceInfo);

                if (serviceInfo.getServiceName().equals(mServiceName)) {
                    Log.d(LOG_TAG, "Same IP");
                    return;
                }
                mService = serviceInfo;
                int port = mService.getPort();
                InetAddress host = mService.getHost();
            }
        };
    }

    public void discoveryServices() {
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener);
    }

    // NsdHelper's tearDown method
    public void tearDown() {
        mNsdManager.unregisterService(mRegistrationListener);
        mNsdManager.stopServiceDiscovery(mDiscoveryListener);
    }
}

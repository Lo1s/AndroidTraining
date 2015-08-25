package com.example.android.androidtraining.ChatApp;

import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.example.android.androidtraining.R;

import java.util.ArrayList;
import java.util.List;

public class NsdActivity extends AppCompatActivity {

    private NsdHelper mNsdHelper;
    private ChatConnection mConnection;
    private final IntentFilter intentFilter = new IntentFilter();
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager mManager;
    private boolean isWifiP2pEnabled = false;
    public static final String LOG_TAG = "NsdActivity";
    private WifiReceiver mReceiver;
    private List<WifiP2pDevice> peers = new ArrayList();
    private WifiP2pManager.PeerListListener peerListListener;
    private WifiP2pManager.ConnectionInfoListener connectionInfoListener;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nsd);
        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        mManager = (WifiP2pManager)getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);

        mNsdHelper = new NsdHelper(this);
    }

    @Override
    protected void onPause() {
        if (mNsdHelper != null) {
            mNsdHelper.tearDown();
        }
        super.onPause();

        unregisterReceiver(mReceiver);

    }

    /** register the BroadcastReceiver with the intent values to be matched */
    @Override
    protected void onResume() {
        super.onResume();
        if (mNsdHelper != null) {
            mNsdHelper.registerService(mConnection.getLocalPort());
            mNsdHelper.discoveryServices();
        }

        if (peerListListener != null) {
            mReceiver = new WifiReceiver(mManager, mChannel, this, peerListListener,
                    connectionInfoListener);
            registerReceiver(mReceiver, intentFilter);
        }
    }

    @Override
    protected void onDestroy() {
        mNsdHelper.tearDown();
        mConnection.tearDown();
        super.onDestroy();
    }

    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    // Start peers discovery
    public void discoverPeers(View view) {
        Toast.makeText(NsdActivity.this, "Peer discovery started", Toast.LENGTH_SHORT).show();

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
                Toast.makeText(NsdActivity.this, "Peer discovery successful", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reason) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
                Toast.makeText(NsdActivity.this, "Failed peer discovery", Toast.LENGTH_SHORT).show();
            }
        });

        peerListListener = new WifiP2pManager.PeerListListener() {
            @Override
            public void onPeersAvailable(WifiP2pDeviceList peersList) {
                // Out with the old, in with the new.
                peers.clear();
                peers.addAll(peersList.getDeviceList());
                Toast.makeText(NsdActivity.this, "Peer list updated", Toast.LENGTH_SHORT).show();
                // If an AdapterView is backed by this data, notify it
                // of the change.  For instance, if you have a ListView of available
                // peers, trigger an update.
                // TODO: try to implement after proficient in listViews
                /*((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
                if (peers.size() == 0) {
                    Log.d(WiFiDirectActivity.TAG, "No devices found");
                    return;
                }*/
            }
        };

        mReceiver = new WifiReceiver(mManager, mChannel, this, peerListListener,
                connectionInfoListener);
        registerReceiver(mReceiver, intentFilter);
    }

    // Connect to the peer
    public void connect(View view) {
        // Picking the first device found on the network.
        WifiP2pDevice device = peers.get(0);

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                // WiFiDirectBroadcastReceiver will notify us. Ignore for now.
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(NsdActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });

        // Get connection notification
        connectionInfoListener =
                new WifiP2pManager.ConnectionInfoListener() {
            @Override
            public void onConnectionInfoAvailable(WifiP2pInfo info) {
                Toast.makeText(NsdActivity.this, "Connection available", Toast.LENGTH_SHORT).show();
                // InetAddress from WifiP2pInfo struct.
                String groupOwnerAddress = info.groupOwnerAddress.getHostAddress();

                // After the group negotiation, we can determine the group owner.
                if (info.groupFormed && info.isGroupOwner) {
                    // Do whatever tasks are specific to the group owner.
                    // One common case is creating a server thread and accepting
                    // incoming connections.
                } else if (info.groupFormed) {
                    // The other device acts as the client. In this case,
                    // you'll want to create a client thread that connects to the group
                    // owner.
                }
            }
        };

    }

}


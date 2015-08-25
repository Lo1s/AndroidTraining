package com.example.android.androidtraining.ChatApp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager;
import android.util.Log;
import android.widget.Toast;

import com.example.android.androidtraining.R;

public class WifiReceiver extends BroadcastReceiver {

    private NsdActivity mActivity;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private WifiP2pManager.PeerListListener mPeerListener;
    private WifiP2pManager.ConnectionInfoListener mConnectionInfoListener;

    public WifiReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                        NsdActivity activity, WifiP2pManager.PeerListListener peerListListener,
                        WifiP2pManager.ConnectionInfoListener connectionInfoListener) {
        this.mManager = manager;
        this.mActivity = activity;
        this.mChannel = channel;
        this.mPeerListener = peerListListener;
        this.mConnectionInfoListener = connectionInfoListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                mActivity.setIsWifiP2pEnabled(true);
            } else {
                mActivity.setIsWifiP2pEnabled(false);
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {
            // The peer list has changed!  We should probably do something about
            // that.
            // TODO: Implement the peer list interface for further information
            // http://developer.android.com/guide/topics/connectivity/wifip2p.html

            // Request available peers from the wifi p2p manager. This is an
            // asynchronous call and the calling activity is notified with a
            // callback on PeerListListener.onPeersAvailable()
            if (mManager != null) {
                mManager.requestPeers(mChannel, mPeerListener);
            }
            Log.d(NsdActivity.LOG_TAG, "P2P peers changed");

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Connection state changed!  We should probably do something about
            // that.
            Toast.makeText(mActivity, "Connection state changed !", Toast.LENGTH_SHORT).show();

            if (mManager == null) {
                return;
            }

            NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);

            if (networkInfo.isConnected()) {
                mManager.requestConnectionInfo(mChannel, mConnectionInfoListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            DeviceListFragment fragment = (DeviceListFragment) mActivity.getFragmentManager()
                    .findFragmentById(R.id.frag_list);
            fragment.updateThisDevice((WifiP2pDevice) intent.getParcelableExtra(
                    WifiP2pManager.EXTRA_WIFI_P2P_DEVICE));
        }
    }

    public void method() {

    }
}

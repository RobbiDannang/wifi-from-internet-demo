package com.roaddeum.wififrominternetdemo;

/**
 * Created by diannerobbi on 7/18/17.
 */


import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pManager.PeerListListener;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class WiFiDirectBroadcastReceiver extends BroadcastReceiver  {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    WifiP2pManager.PeerListListener myPeerListListener;
    static int listSize, s = 2;

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;


    public WiFiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel,
                                       MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;
    }

    private WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener() {
        @Override
        public void onPeersAvailable(WifiP2pDeviceList peerList) {

            Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
            if (!refreshedPeers.equals(peers)) {
                peers.clear();
                peers.addAll(refreshedPeers);
                MainActivity.longList.append(peers.toString());

                // If an AdapterView is backed by this data, notify it
                // of the change.  For instance, if you have a ListView of
                // available peers, trigger an update.

                // Perform any other updates needed based on the new list of
                // peers connected to the Wi-Fi P2P network.
            }

            if (peers.size() == 0) {

                Toast.makeText(mActivity, "No devices", Toast.LENGTH_SHORT).show();
                MainActivity.longList.append("No devices found");
                return;
            }
        }
    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        if(mManager != null){
            mManager.requestPeers(mChannel, peerListListener);
//            mManager.requestPeers(mChannel, new WifiP2pManager.PeerListListener(){
//                @Override
//                public void onPeersAvailable(WifiP2pDeviceList wifiP2pDeviceList) {
//                    Collection<WifiP2pDevice> deviceList = wifiP2pDeviceList.getDeviceList();
//
//                  //  MainActivity.longList.append("List size: " + deviceList.size());
//
//                    for(WifiP2pDevice device : deviceList){
//                       // if(s > 0) {
//                     //       MainActivity.longList.append(device.deviceName + "..");
//                        //    s--;
//                       // }
////                        if(device.deviceName.startsWith(MainActivity.name))
////                            MainActivity.longList.append("It exists \n");
////                        else
////                            MainActivity.longList.append(device.deviceName + ".\t.");
//                    }
//                }
//            });
        }

        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
             //   Toast.makeText(mActivity, "Channel Received", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(mActivity, "Channel Not Received", Toast.LENGTH_SHORT).show();
            }
        });

        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Check to see if Wi-Fi is enabled and notify appropriate activity

            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                // Wifi Direct mode is enabled
                //mActivity.setIsWifiP2pEnabled(true);
                Log.e("WifiDIRECT-","Enabled");
            } else {
                //mActivity.setIsWifiP2pEnabled(false);
                Log.e("WifiDIRECT-","Disabled");
                // mActivity.resetData();

            }
            Log.d(MainActivity.TAG, "P2P state changed - " + state);

        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            if (mManager != null) {
                mManager.requestPeers(mChannel, myPeerListListener );
                Log.e("Peer Changed:", "Detected Peer");
            }

            // Call WifiP2pManager.requestPeers() to get a list of current peers

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {
            // Respond to new connection or disconnections
            if (mManager == null) {
                return;
            }
        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            // Respond to this device's wifi state changing
        }

    }



}
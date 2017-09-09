package com.roaddeum.wififrominternetdemo;

import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.p2p.WifiP2pDevice;
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

import java.lang.annotation.Target;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements WifiP2pManager.ChannelListener,WifiP2pManager.PeerListListener {

    WifiP2pManager wifiP2pManager;
    WifiManager wifiManager;
    WifiP2pManager.Channel mChannel;
    BroadcastReceiver mReceiver;
    IntentFilter mIntentFilter;
    static TextView list, longList;
    WifiP2pDevice wifiP2pDevice;
    WifiP2pDeviceList wifiP2pDeviceList;
    WifiInfo wifiInfo;

    static String name, address;

    public static final String TAG = "MainActivity";

    private List<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    ProgressDialog progressDialog = null;
    View mContentView = null;
    private WifiP2pDevice device;

    private WifiP2pManager manager;
    private boolean isWifiP2pEnabled = false;
    private boolean retryChannel = false;

    private final IntentFilter intentFilter = new IntentFilter();


    TextView value;

/*
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
               this.isWifiP2pEnabled = isWifiP2pEnabled;
           }
           */

    @Override
    @TargetApi(23)
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
       // Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
       // setSupportActionBar(toolbar);

        wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        wifiP2pManager = (WifiP2pManager)getApplicationContext().getSystemService(WIFI_P2P_SERVICE);

        list = (TextView)findViewById(R.id.list);
        longList = (TextView)findViewById(R.id.long_list);

        mChannel = wifiP2pManager.initialize(this, getMainLooper(), null);

        mReceiver = new WiFiDirectBroadcastReceiver(wifiP2pManager, mChannel, this);

        wifiInfo = wifiManager.getConnectionInfo();
        List<ScanResult> results = wifiManager.getScanResults();
        for (ScanResult result:results){
            if (result.SSID.startsWith("PETFUN_C4D4BB")) {
                name = result.SSID;
                list.append(result.SSID + "\nofficial name is:" + name + ".");

            }
        }
        list.append("Wifi Info:" + wifiInfo.toString());
        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);
    }



    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(mReceiver, mIntentFilter);
    }
    /* unregister the broadcast receiver */
    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        manager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Discovery Initiated",
                        Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onFailure(int reasonCode) {
                Toast.makeText(MainActivity.this, "Discovery Failed : " + reasonCode,
                        Toast.LENGTH_SHORT).show();
            }
        });
/*
        Button clk = (Button) findViewById(R.id.showDetails);
        clk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MainActivity.this, "Button Clicked", Toast.LENGTH_SHORT).show();
                TextView view2 = (TextView) findViewById(R.id.value);
                view2.setText(device.deviceAddress);

            }
        });
*/

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {

        Toast.makeText(MainActivity.this, "Peers Available", Toast.LENGTH_SHORT).show();
        if (progressDialog != null && progressDialog.isShowing()) {
            progressDialog.dismiss();
        }
        peers.clear();
        peers.addAll(peerList.getDeviceList());
        Log.e("Peer Size-* * * *", String.valueOf(peers.size())+peerList.toString());

        //((WiFiPeerListAdapter) getListAdapter()).notifyDataSetChanged();
        if (peers.size() == 0) {
            Log.d(MainActivity.TAG, "No devices found");
            Log.e("Peer Size-","No Device Found * * * *");
            longList.setText("NO PEERS");
            return;
        }
        else{
        }

    }

    @Override
    public void onChannelDisconnected() {

        // we will try once more
        if (manager != null && !retryChannel) {
            Toast.makeText(this, "Channel lost. Trying again", Toast.LENGTH_LONG).show();
            // resetData();
            retryChannel = true;
            manager.initialize(this, getMainLooper(), this);
        } else {
            Toast.makeText(this,
                    "Severe! Channel is probably lost premanently. Try Disable/Re-Enable P2P.",
                    Toast.LENGTH_LONG).show();
        }

    }
}
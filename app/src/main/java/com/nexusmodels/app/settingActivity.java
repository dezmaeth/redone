package com.nexusmodels.app;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


public class settingActivity extends ActionBarActivity {
    private Set<BluetoothDevice> pairedDevices;
    private BluetoothDevice mmDevice;
    private BluetoothSocket mmSocket;
    OutputStream mmOutputStream;
    InputStream mmInputStream;
    private BluetoothAdapter BA;
    private ListView lv;
    private ArrayList<String> arrayList;
    private ArrayAdapter<String> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        BA = BluetoothAdapter.getDefaultAdapter();
        Button sendButton = (Button)findViewById(R.id.send);
        sendButton.setOnClickListener(new View.OnClickListener()
        {
            public void onClick(View v)
            {
                try
                {
                    sendData();
                }
                catch (IOException ex) { }
            }
        });
        list();

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }
    }

    protected void list() {
        lv = (ListView)findViewById(R.id.devicesList);
        pairedDevices = BA.getBondedDevices();
        arrayList = new ArrayList<String>();
        for(BluetoothDevice bt : pairedDevices) {
            if (bt.getAddress() == "00:12:05:24:96:74") {
                mmDevice = bt;
            }
            arrayList.add(bt.getName() + "\n" + bt.getAddress());
        }

        Toast.makeText(getApplicationContext(),"listing paired bluetooth devices",Toast.LENGTH_SHORT).show();

        if(arrayList.isEmpty()) {
            lv.setAdapter(null);
        }
        else
        {
            adapter = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, arrayList);
            lv.setAdapter(adapter);
        }
    }

    void openBT() throws IOException
    {
        UUID uuid = UUID.fromString("00001101-0000-1000-8000-00805f9b34fb"); //Standard SerialPortService ID
        mmSocket = mmDevice.createRfcommSocketToServiceRecord(uuid);
        mmSocket.connect();
        mmOutputStream = mmSocket.getOutputStream();
        mmInputStream = mmSocket.getInputStream();
    }

    void sendData() throws IOException
    {
        openBT();

        String msg = "2";
        msg += "\n";
        mmOutputStream.write(msg.getBytes());
    }


    void closeBT() throws IOException
    {
        mmOutputStream.close();
        mmInputStream.close();
        mmSocket.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
            return rootView;
        }
    }
}

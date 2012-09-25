package com.example.leftright;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;
import android.widget.Toast;

public class BluetoothAsyncTask extends AsyncTask<BluetoothAdapter, Void, Boolean> {
	MainActivity _instance;
	
	@Override
	protected Boolean doInBackground(BluetoothAdapter... mBluetoothAdapter) {
		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter[0].getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().equals("SeeedBTSlave")) {
				    try {
					    _instance.connection = device.createRfcommSocketToServiceRecord(
					    		UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
					    _instance.connection.connect();
					    return true;
				    } catch (IOException e) {
    					e.printStackTrace();
    					//Toast.makeText(_instance, e.getMessage(), Toast.LENGTH_LONG).show();
	    			}
		    	}
			}
		}
		return false;
	}

	protected void onPostExecute(Boolean ready) {
        _instance.bt_ready = ready;
    }
}

package eu.paylogic.BTRemote;

import java.io.IOException;
import java.util.Set;
import java.util.UUID;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.os.AsyncTask;

public class BluetoothAsyncTask extends AsyncTask<BluetoothAdapter, Void, Boolean> {
	BTRemote _instance;
	
	@Override
	protected Boolean doInBackground(BluetoothAdapter... mBluetoothAdapter) {
		_instance.bt_loading = true;
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
	    			}
		    	}
			}
		}
		return false;
	}

	protected void onPostExecute(Boolean ready) {
		_instance.bt_loading = false;
        _instance.bt_ready = ready;
    }
}

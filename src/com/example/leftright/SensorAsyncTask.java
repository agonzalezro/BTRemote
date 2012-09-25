package com.example.leftright;

import java.util.List;

import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;

public class SensorAsyncTask extends AsyncTask<SensorManager, Void, Boolean> {
	MainActivity _instance;
	
	@Override
	protected Boolean doInBackground(SensorManager... sm) {
		List<Sensor> sensors = sm[0].getSensorList(Sensor.TYPE_ACCELEROMETER);        
        if (sensors.size() > 0) {
        	sm[0].registerListener(_instance, sensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        	return true;
        }
		return false;
	}

	protected void onPostExecute(Boolean ready) {
        _instance.sensor_ready = ready;
    }
}

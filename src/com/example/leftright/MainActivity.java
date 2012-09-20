package com.example.leftright;

import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;

public class MainActivity extends Activity implements SensorEventListener {
    private float z = 0, y = 0;
    
    private Integer min_servo_direction = 60;
    private Integer max_servo_direction = 130;
    private Integer min_servo_speed = 103;
    private Integer max_servo_speed = 127;
    
    private Integer previous_speed = 0;
    private Integer previous_direction = 0;
    
    private Integer REQUEST_ENABLE_BT = 1;
	private BluetoothSocket connection = null;
	
	private SeekBar min_speed_seekbar = null;
	private SeekBar max_speed_seekbar = null;
	private SeekBar min_direction_seekbar = null;
	private SeekBar max_direction_seekbar = null;
	
	private ToggleButton bt_connection = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTheme(android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        setupButtons();
        setupTrimmers();
        setupAcelerometer();
        setupBluetooth();
    }
    
	/*@Override
	public void onDestroy() {
		super.onDestroy();
		if (connection != null) {
			try {
				connection.close();
			} catch (IOException e) { }
		}
	}*/

    private void setupTrimmers() {
    	min_speed_seekbar = (SeekBar) findViewById(R.id.min_speed);
        max_speed_seekbar = (SeekBar) findViewById(R.id.max_speed);
        min_direction_seekbar = (SeekBar) findViewById(R.id.min_direction);
        max_direction_seekbar = (SeekBar) findViewById(R.id.max_direction);
        
        min_speed_seekbar.setEnabled(false);
        max_speed_seekbar.setEnabled(false);
        min_direction_seekbar.setEnabled(false);
        max_direction_seekbar.setEnabled(false);
        
        min_speed_seekbar.setProgress(min_servo_speed);
        max_speed_seekbar.setProgress(max_servo_speed);
        min_direction_seekbar.setProgress(min_servo_direction);
        max_direction_seekbar.setProgress(max_servo_direction);
        
        min_speed_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        		min_servo_speed = min_speed_seekbar.getProgress();
        	    Log.d("BT", String.valueOf(min_servo_speed));
        	}
			public void onStartTrackingTouch(SeekBar seekBar) { }
			public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        
        max_speed_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        		max_servo_speed = max_speed_seekbar.getProgress();
                Log.d("BT", String.valueOf(max_servo_speed));
        	}
			public void onStartTrackingTouch(SeekBar seekBar) { }
			public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        
        min_direction_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        		min_servo_direction = min_direction_seekbar.getProgress();
                Log.d("BT", String.valueOf(min_servo_direction));
        	}
			public void onStartTrackingTouch(SeekBar seekBar) { }
			public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        
        max_direction_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        		max_servo_direction = max_direction_seekbar.getProgress();
                Log.d("BT", String.valueOf(max_servo_direction));
        	}
			public void onStartTrackingTouch(SeekBar seekBar) { }
			public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
    
    private void setupAcelerometer() {
    	SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        List<Sensor> sensors = sm.getSensorList(Sensor.TYPE_ACCELEROMETER);        
        if (sensors.size() > 0) {
        	sm.registerListener(this, sensors.get(0), SensorManager.SENSOR_DELAY_NORMAL);
        }
    }
    
    private void setupBluetooth() {
    	BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
		if (!mBluetoothAdapter.isEnabled()) {
			Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
			startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
		}

		Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();
		if (pairedDevices.size() > 0) {
			for (BluetoothDevice device : pairedDevices) {
				if (device.getName().equals("SeeedBTSlave")) {
				    try {
					    connection = device.createRfcommSocketToServiceRecord(
					    		UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"));
					    connection.connect();
				    } catch (IOException e) {
    					e.printStackTrace();
	    			}
		    	}
			}
		}
    }
    
    private void setupButtons() {
    	ToggleButton trimmers = (ToggleButton) findViewById(R.id.trimmers);
    	bt_connection = (ToggleButton) findViewById(R.id.connection);
    	
    	trimmers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	        enableOrDisableTrimmers(isChecked);
    	    }
    	});
    	
    	bt_connection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	        setupBluetooth();
    	    }
    	});
    }
    
    private void enableOrDisableTrimmers(boolean enabled) {    	
        min_speed_seekbar.setEnabled(enabled);
        max_speed_seekbar.setEnabled(enabled);
        min_direction_seekbar.setEnabled(enabled);
        max_direction_seekbar.setEnabled(enabled);
    }

	public void onAccuracyChanged(Sensor sensor, int accuracy) {}

	public void onSensorChanged(SensorEvent event) {
		y = event.values[1] + 7; //7 is the minimal angle from the left 
		z = event.values[2] + 3; //The range on the mobile is from -3 to 7, this will do the calculation easier :)
		
		int range_speed = max_servo_speed - min_servo_speed;
		int offset_speed = range_speed / 13; // 13 are the degrees of freedom in the speed (from -3 to 10)
		
		int range_direction = max_servo_direction - min_servo_direction;
		int offset_direction = range_direction / 14; // 14 are the degrees of freedom in the direction
		
		int speed = Math.round(
				min_servo_speed + (z * offset_speed));
		
		int direction = Math.round(
				min_servo_direction + (y * offset_direction));
		
		int[] to_send = {previous_speed, previous_direction};
	
		if ((z >= 0) && (speed != previous_speed)) {
			previous_speed = speed;
			to_send[0] = speed;
		}
		
		if ((y >= 0) && (direction != previous_direction)) {
			previous_direction = direction;
			to_send[1] = direction;
		}
		
		
		try {
			if (connection != null) {
				bt_connection.setChecked(true);
			    connection.getOutputStream().write((char) to_send[0]);
			    connection.getOutputStream().write((char) to_send[1]);
			    //connection.getOutputStream().write((char) '\n');
			}
		} catch (IOException e) {
			bt_connection.setChecked(false);
			// FIXME: Deactivated because call this without any delay breaks the app
			//setupBluetooth();
			e.printStackTrace();
		}
		
		String message = "Direction: " + direction + " & speed: "+ speed;
		((TextView) findViewById(R.id.position)).setText(message);
	}
}

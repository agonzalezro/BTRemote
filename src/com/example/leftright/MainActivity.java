package com.example.leftright;

import java.io.IOException;
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
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Toast;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;
import android.widget.ToggleButton;


public class MainActivity extends Activity implements SensorEventListener {
	public Boolean sensor_ready = false;
	public Boolean bt_ready = false;
    private float z = 0, y = 0;
    
    private Integer min_servo_direction = 65;
    private Integer max_servo_direction = 130;
    private Integer min_servo_speed = 50;
    private Integer max_servo_speed = 127;
    
    private Integer previous_speed = 0;
    private Integer previous_direction = 0;
    
    private Integer REQUEST_ENABLE_BT = 1;
	private BluetoothSocket connection = null;
	
	private SeekBar min_speed_seekbar = null;
	private SeekBar max_speed_seekbar = null;
	private SeekBar min_direction_seekbar = null;
	private SeekBar max_direction_seekbar = null;
	
	private TextView txt_view_min_speed = null;
    private TextView txt_view_max_speed = null;
    private TextView txt_view_min_direction = null;
    private TextView txt_view_max_direction = null;
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setTheme(android.R.style.Theme_DeviceDefault_NoActionBar_Fullscreen);
        setContentView(R.layout.activity_main);
        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        
        setupButtons();
        setupTrimmers();
    }
    
    @Override
    protected void onResume() {
        super.onResume();
        setupAcelerometer();
        setupBluetooth();
    }

    private void setupTrimmers() {
        txt_view_min_speed = (TextView) findViewById(R.id.txt_view_min_speed);
        txt_view_max_speed = (TextView) findViewById(R.id.txt_view_max_speed);
        txt_view_min_direction = (TextView) findViewById(R.id.txt_view_min_direction);
        txt_view_max_direction = (TextView) findViewById(R.id.txt_view_max_direction);
    	
    	min_speed_seekbar = (SeekBar) findViewById(R.id.min_speed);
        max_speed_seekbar = (SeekBar) findViewById(R.id.max_speed);
        min_direction_seekbar = (SeekBar) findViewById(R.id.min_direction);
        max_direction_seekbar = (SeekBar) findViewById(R.id.max_direction);
        
        min_speed_seekbar.setEnabled(false);
        max_speed_seekbar.setEnabled(false);
        min_direction_seekbar.setEnabled(false);
        max_direction_seekbar.setEnabled(false);
        
        min_speed_seekbar.setProgress(min_servo_speed);
        txt_view_min_speed.setText(String.valueOf(min_servo_speed));
        max_speed_seekbar.setProgress(max_servo_speed);
        txt_view_max_speed.setText(String.valueOf(max_servo_speed));
        min_direction_seekbar.setProgress(min_servo_direction);
        txt_view_min_direction.setText(String.valueOf(min_servo_direction));
        max_direction_seekbar.setProgress(max_servo_direction);
        txt_view_max_direction.setText(String.valueOf(max_servo_direction));
        
        min_speed_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        		min_servo_speed = min_speed_seekbar.getProgress();
        		txt_view_min_speed.setText(String.valueOf(min_servo_speed));
        	}
			public void onStartTrackingTouch(SeekBar seekBar) { }
			public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        
        max_speed_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        		max_servo_speed = max_speed_seekbar.getProgress();
        		txt_view_max_speed.setText(String.valueOf(max_servo_speed));
        	}
			public void onStartTrackingTouch(SeekBar seekBar) { }
			public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        
        min_direction_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        		min_servo_direction = min_direction_seekbar.getProgress();
        		txt_view_min_direction.setText(String.valueOf(min_servo_direction));
        	}
			public void onStartTrackingTouch(SeekBar seekBar) { }
			public void onStopTrackingTouch(SeekBar seekBar) { }
        });
        
        max_direction_seekbar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
        	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        		max_servo_direction = max_direction_seekbar.getProgress();
        		txt_view_max_direction.setText(String.valueOf(max_servo_direction));
        	}
			public void onStartTrackingTouch(SeekBar seekBar) { }
			public void onStopTrackingTouch(SeekBar seekBar) { }
        });
    }
    
    private void setupAcelerometer() {
    	SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        SensorAsyncTask sat = new SensorAsyncTask();
        sat._instance = this;
        sat.execute(sm);
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
    					Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
	    			}
		    	}
			}
		}
    }
    
    private void setupButtons() {
    	ToggleButton trimmers = (ToggleButton) findViewById(R.id.trimmers);
    	Button bt_connection = (Button) findViewById(R.id.connection);
    	
    	trimmers.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    	    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
    	        enableOrDisableTrimmers(isChecked);
    	    }
    	});
    	
        bt_connection.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
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
		if (!sensor_ready || !bt_ready)  // FIXME: I think that this is not needed, the listener will not be called without ready sensor lol
			return;
		
		y = event.values[1] + 7; //7 is the minimal angle from the left
		z = event.values[2] + 3; //The range on the mobile is from -3 to 12, this will do the calculation easier :)
		
		int range_speed = max_servo_speed - min_servo_speed;
		int offset_speed = range_speed / 16; // 13 are the degrees of freedom in the speed (from -3 to 12)
		
		int range_direction = max_servo_direction - min_servo_direction;
		int offset_direction = range_direction / 15; // 15 are the degrees of freedom in the direction
		
		int[] to_send = {previous_speed, previous_direction};
	
		if (z >= 0) {
			to_send[0] = Math.round(min_servo_speed + (z * offset_speed));
			previous_speed = to_send[0];
		}
		
		if (y >= 0) {
			to_send[1] = Math.round(min_servo_direction + (y * offset_direction));;
			previous_direction = to_send[1];
		}
		
		
		if (connection != null) {
			try {			
				//bt_connection.setChecked(true);
				connection.getOutputStream().write((char) to_send[0]);
				connection.getOutputStream().write((char) to_send[1]);
				connection.getOutputStream().flush();
				//connection.getOutputStream().write((char) '\n');
			} catch (IOException e) {
				//bt_connection.setChecked(false);
				// FIXME: Deactivated because call this without any delay breaks the app
				//setupBluetooth();
				e.printStackTrace();
				Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
			}
		}
		
		String message = "Speed: " + to_send[0] + " & direction: "+ to_send[1];
		((TextView) findViewById(R.id.position)).setText(message);
	}
}

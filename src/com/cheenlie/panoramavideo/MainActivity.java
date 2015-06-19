package com.cheenlie.panoramavideo;


import android.app.Activity;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorManager;
import android.hardware.SensorEventListener;

public class MainActivity extends Activity{
	private MySurfaceView mGLSurfaceView;
	static boolean threadFlag;
	private SensorManager sensorManager = null;
	private Sensor orientationSensor = null;
	TextView tvX;
	
	private static final float NS2S = 1.0f/1000000000.0f;
    private float timestamp;
    
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
				WindowManager.LayoutParams.FLAG_FULLSCREEN);
		//设置为竖屏模式
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		//切换到主界面
		setContentView(R.layout.main);
		
		mGLSurfaceView = new MySurfaceView(this);
		//setContentView(mGLSurfaceView);
		mGLSurfaceView.requestFocus();
		mGLSurfaceView.setFocusableInTouchMode(true);
	
		LinearLayout f1 = (LinearLayout)findViewById(R.id.container);
		f1.addView(mGLSurfaceView);
		
		sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
		orientationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
		//tvX = (TextView)findViewById(R.id.text1);
	}
	
	private SensorEventListener mySensorListener = new SensorEventListener(){
		
	 
		public void onAccuracyChanged(Sensor sensor, int accurity)
		{
			
		}
		public void onSensorChanged(SensorEvent event)
		{
			
			float x = event.values[0];
			float y = event.values[1];
			float z = event.values[2];
			
			mGLSurfaceView.changed(x, y, z);
			//tvX.setText("zhao: " + x + " " + y + " " + z);
		}
	};
	
	
	@Override
	protected void onResume()
	{
		super.onResume();

		threadFlag = true;
		mGLSurfaceView.onResume();
		sensorManager.registerListener(
				mySensorListener,
				orientationSensor,
				SensorManager.SENSOR_DELAY_NORMAL);
	}
	
	@Override
	protected void onPause()
	{
		super.onPause();
		threadFlag = false;
		mGLSurfaceView.onPause();
		sensorManager.unregisterListener(mySensorListener);
	}
	
}

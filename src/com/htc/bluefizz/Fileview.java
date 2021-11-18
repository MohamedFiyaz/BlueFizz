package com.htc.bluefizz;

import java.io.FileInputStream;

import android.app.Activity;
import android.bluetooth.BluetoothGatt;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;

public class Fileview extends Activity{

	public TextView text;
	final Handler fileupdateHandler = new Handler();
	private Runnable fileupadeTimer;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.filedata);
		text = (TextView) findViewById(R.id.filedata);
		 
	}

	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		fileupdateHandler.removeCallbacks(fileupadeTimer);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		filextact();
	}

	private void filextact() {
		// TODO Auto-generated method stub
		fileupadeTimer = new Runnable() {
			@Override
			public void run() {
				FileInputStream fin = null;
				try {
					fin = openFileInput("brspdata.txt");
					 int c;
		   	         String temp ="";
		   	         if((c = fin.read()) != -1){
						Log.v("testingfile","notnull");
						do{
							temp = temp + Character.toString((char)c);
						}while( (c = fin.read()) != -1);
						temp = temp.substring(0, temp.length());
						text.setText(temp);
						Log.d("data",temp);
			   	     }
				}catch(Exception e){
					 e.printStackTrace();
				}		
				fileupdateHandler.postDelayed(this, 10000);
			}
		};
		fileupdateHandler.postDelayed(fileupadeTimer, 0);
		
	}
	

}

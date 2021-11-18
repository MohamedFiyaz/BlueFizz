package com.htc.bluefizz;

import android.animation.ObjectAnimator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.telephony.SmsManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.blueradios.Brsp;
import com.blueradios.BrspCallback;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;



public class MainActivity extends Activity {
	
	public static final int REQUEST_SELECT_DEVICE = 0;
	public static final int REQUEST_ENABLE = 1;
	private Brsp _brsp;
	public DatabaseHandler dbhandler;
	private BluetoothAdapter myBluetoothAdapter;
	private BluetoothDevice selectedDeviceItem;
	private ArrayList<BluetoothDevice> pairedDevices;
	
	private EditText patientname;
	private EditText patientage;
	private EditText doctorname;
	private EditText doctornumber;
	private EditText emergencyno;
	private TextView currentheart;
	private TextView currentspo;
	private TextView currentacc;
	private TextView currentemp;
	private TextView batteryrate;
	private RadioGroup radioSexGroup;
	private RadioButton radioSexButton;
	private AlertDialog alertDialog;
	private AlertDialog panicDialog;
	private MenuItem scandevice;
	private MenuItem resetdevice;
	private MenuItem batteryindicator;
	private TextView checktemperature;
	public ImageView heartview;
	public ImageView spoview;
	public ImageView accview;
	public ImageView tempview;
	public ImageView refreshview;
	public ImageView connectview;
	public ImageView disconnectview;
	public ImageView imgBlink;
    public RoundImage roundedImage;
    public Resources res;
    public Drawable drawable;
    public Animation anima;
    public Animation textblink;
    public ProgressBar mProgress;
    public ObjectAnimator animation;
    private Switch mySwitch;
    private ArrayList<String> diseaselist;
	private ArrayList<String> ar ;
	
	public String patientprofile = "profile.txt";
	public String dfile = "mydevice.txt";
	public String settingsfile = "setfile.txt";
	public String devicedata = "brspdata.txt";
	private String selectedisease = null;
	private String filedata ;
	
	
	private int inrc = 1;
	private int Settingstimer = 0;
	private int synTimer = 0;
	private int currentempdisplaycount = 0;
	private int currentaccdisplaycount = 0;
	private int currentspodisplaycount = 0;
	private int currentheartdisplaycount = 0;
	private int tempcount = 0;
	private int acccount = 0;
	private int spocount = 0;
	private int heartcount = 0;
	private int batteryval = 35;
	
	private boolean currentempdisplaycheck = false;
	private boolean currentaccdisplaycheck = false;
	private boolean currentspodisplaycheck = false;
	private boolean currentheartdisplaycheck = false;
	private boolean Ackchck = false;
	private boolean flag = false;
	private boolean dbat = false;
	private boolean fileemptycheck = false;
	private boolean incomingdata = false;
	private boolean panicalert = false;
	private boolean bodytemp = false;
	private boolean alert = false;
	
	final Handler pushsettingsHandler = new Handler();
	final Handler synchHandler = new Handler();
	final Handler blueconnectionHandler = new Handler();
	final Handler deviceconnectionHandler = new Handler();
	final Handler currentupdateHandler = new Handler();
	private Runnable pushsettingsTimer,synchTimer,blueconnectionTimer,currentupdateTimer,deviceconnectionTimer;
	private TextView spovalue,spoper,tempval,stepval,calval,heartval;
	
	private BrspCallback _brspCallback = new BrspCallback() {
	   	@Override
		public void onSendingStateChanged(Brsp obj) {
			Log.v("fiyazcheckonsending","onsendingstate");
			
			// Log.d( "onSendingStateChanged thread id:");
	   	}

	   	@Override
	   	public void onConnectionStateChanged(Brsp obj) {
	   		Log.v("fiyazcheck","onconnectionstate");
	   		// Log.d( "onConnectionStateChanged state:" , obj.getConnectionState() );

	   		final Brsp brspObj = obj;
	   			runOnUiThread(new Runnable() {
	   				@Override
	   				public void run() {
	   					invalidateOptionsMenu();
	   					BluetoothDevice currentDevice = brspObj.getDevice();
	   					if (currentDevice != null && brspObj.getConnectionState() == BluetoothProfile.STATE_CONNECTED) {
	   						// Log.d(TAG, "Creating bond for device:" +
	   						// currentDevice.getAddress());
	   						// currentDevice.createBond();
	   					}
	   				}
	   			});
	   	}

	   	@Override
	   	public void onDataReceived(final Brsp obj) {
    
	   		runOnUiThread(new Runnable() {
	   			@Override
	   			public void run() {
	   				byte[] bytes = obj.readBytes();
	   				if (bytes != null) {
	   					String input = new String(bytes);
	   					if(input.equals("ACK")){
	   						Ackchck = true;
	   					}
	   					if(input.equals("ACKL")){
	   						setProgressBarIndeterminateVisibility(false);
	   					}
	   					if(input.equals("Dincome")){
	   						incomingdata = true;
	   						synchHandler.removeCallbacks(synchTimer);
	   					}
	   					if(input.equals("Dend")){
	   						incomingdata = false;
	   					}
	   					if(input.equals("Cbat")){
	   						dbat = true;
	   					}
	   					if(input.equals("Ctemp")){
	   						bodytemp = true;
	   					}
	   					if(input.equals("PANALERT")){
	   						panicalert = true;
	   					}
	   					if(input.equals("ALERT")){
	   						alert = true;
	   					}
	   					if(input.equals("SEND_DATE")){
	   						pushData();
	   					}
	   					alertcheck(input);
	   					panicAlertcheck(input);
	   					writdatatotable(input);
	   					writebattery(input);
	   					writeTemp(input);
	   				/*	writeAlldata(input);*/
	   					writdatatofile(input);
	   				//	Toast.makeText(getBaseContext(),input, Toast.LENGTH_SHORT).show();
	   				} 
	   			}

				
	   		});
	   		super.onDataReceived(obj);
	   	}

	   	@Override
	   	public void onError(Brsp obj, Exception e) {
	   		//   Log.d( "onError:" + e.getMessage() + " thread id:" + Process.myTid());
	   		super.onError(obj, e);
	   	}

	   	@Override
	   	public void onBrspModeChanged(Brsp obj) {
	   		Log.v("fiyazcheck","onBrspModechange");
	   		super.onBrspModeChanged(obj);
	   		runOnUiThread(new Runnable() {
	   			@Override
	   			public void run() {
	   			//	invalidateOptionsMenu();
	   			}
	   		});
	   	}
	   	@Override
		public void onRssiUpdate(Brsp obj) {
		    
		    super.onRssiUpdate(obj);
		    final Brsp brspObj = obj;
   			runOnUiThread(new Runnable() {
   				@Override
   				public void run() {
   					Log.d( "Remote device RSSI:",  ""+brspObj.getLastRssi()); // Log RSSI
   					//showToast(""+brspObj.getLastRssi());
   				}
   			});
		    
		}
	   	@Override
	   	public void onBrspStateChanged(Brsp obj) {
	   		Log.v("fiyazcheck","onbrspstatechange");
	   		super.onBrspStateChanged(obj);
	   	//	invalidateOptionsMenu();
	   		int currentState = obj.getBrspState();
	   		obj.readRssi(); // read the RSSI once
	   		if (obj.getBrspState() == Brsp.BRSP_STATE_READY) {
	   			// Ready to write
	   			// _brsp.writeBytes("Test".getBytes());
	   		}
	   	}
	};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        try {
	         FileOutputStream fOut = openFileOutput(patientprofile,MODE_APPEND);
	         fOut.close();
	         
	      } catch (Exception e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      }
        try {
	         FileOutputStream fOut = openFileOutput(dfile,MODE_APPEND);
	         fOut.close();
	         
	      } catch (Exception e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      }
        try {
	         FileOutputStream fOut = openFileOutput(settingsfile,MODE_APPEND);
	         fOut.close();
	         
	      } catch (Exception e) {
	         // TODO Auto-generated catch block
	         e.printStackTrace();
	      }
        final BluetoothManager bluetoothManager = (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
    	if (myBluetoothAdapter == null) {
    		myBluetoothAdapter = bluetoothManager.getAdapter();
    	}
    	if ( myBluetoothAdapter.isEnabled()) {
    		dialogcheck();
		}
    	 registerReceiver(mPairReceiver, new IntentFilter(BluetoothDevice.ACTION_BOND_STATE_CHANGED));
    	_brsp = new Brsp(_brspCallback, 3000, 3000);
    	requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);
       
        dbhandler = new DatabaseHandler(this);  
        
        heartview = (ImageView) findViewById(R.id.heartpic);
    /*  Bitmap h = BitmapFactory.decodeResource(getResources(),R.drawable.hearet);
        roundedImage = new RoundImage(h);
        heartview.setImageDrawable(roundedImage);*/
       
        spoview = (ImageView) findViewById(R.id.spopic);
       /* Bitmap s = BitmapFactory.decodeResource(getResources(),R.drawable.spoj);
        roundedImage = new RoundImage(s);
        spoview.setImageDrawable(roundedImage);*/
        
        tempview = (ImageView) findViewById(R.id.tempic);
      /*  Bitmap t = BitmapFactory.decodeResource(getResources(),R.drawable.bodytemp);
        roundedImage = new RoundImage(t);
        tempview.setImageDrawable(roundedImage);*/
       /* 
        accview = (ImageView) findViewById(R.id.accpic);
        Bitmap a = BitmapFactory.decodeResource(getResources(),R.drawable.foot);
        roundedImage = new RoundImage(a);
        accview.setImageDrawable(roundedImage);
        */
   /*     refreshview = (ImageView) findViewById(R.id.refreshbutton);
        Bitmap r = BitmapFactory.decodeResource(getResources(),R.drawable.refresh);
        roundedImage = new RoundImage(r);
        refreshview.setImageDrawable(roundedImage);
     */   
        ActionBar bar = getActionBar();
        bar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#0A0A29")));
     //   bar.setSubtitle("HTIC Project");
        
        
        currentheart = (TextView) findViewById(R.id.textview_bpm);
        currentspo = (TextView) findViewById(R.id.textview_spo);
     //   currentacc = (TextView) findViewById(R.id.accrate);
        currentemp = (TextView) findViewById(R.id.textview_temp);
     //   batteryrate = (TextView) findViewById(R.id.batteryrate);
    //    checktemperature = (TextView) findViewById(R.id.checktemperature);
     //   mySwitch = (Switch) findViewById(R.id.swit);
        res = getResources();
        drawable = res.getDrawable(R.drawable.background);
   /*    mProgress = (ProgressBar) findViewById(R.id.progressbar1);
      //  mProgress.setProgress(25);   // Main Progress
      //  mProgress.setSecondaryProgress(50); // Secondary Progress
        mProgress.setMax(100); // Maximum Progress
        mProgress.setProgressDrawable(drawable);*/
		spovalue = (TextView) findViewById(R.id.textview_spo);
		spoper = (TextView) findViewById(R.id.spopercentage);
		tempval = (TextView) findViewById(R.id.textview_temp);
		stepval = (TextView) findViewById(R.id.textview_steps);
		calval = (TextView) findViewById(R.id.textview_calory);
		heartval = (TextView) findViewById(R.id.textview_bpm);
		float textSize = spovalue.getTextSize();
		boolean a = isTablet(this);
		//Log.v("textsize",a+"");



		String inputSystem;
		inputSystem = android.os.Build.ID;
		Log.d("hai",inputSystem);
		Display display = getWindowManager().getDefaultDisplay();
		int width = display.getWidth();  // deprecated
		int height = display.getHeight();  // deprecated
		Log.d("hai",width+"");
		Log.d("hai",height+"");
		DisplayMetrics dm = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		double x = Math.pow(width/dm.xdpi,2);
		double y = Math.pow(height/dm.ydpi,2);
		double screenInches = Math.sqrt(x+y);
		Log.d("hai","Screen inches : " + screenInches+"");
		if( screenInches < 5){
			spovalue.setTextSize(50);
			spoper.setTextSize(50);
			spoper.setPadding(0,76,0,0);
			tempval.setTextSize(35);
			stepval.setTextSize(35);
			calval.setTextSize(35);
			heartval.setTextSize(50);
		}else{
			spovalue.setTextSize(80);
			spoper.setTextSize(80);
			tempval.setTextSize(80);
			stepval.setTextSize(50);
			calval.setTextSize(50);
			heartval.setTextSize(80);
		}
    }
	public boolean isTablet(Context context) {
		boolean xlarge = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == 4);
		boolean large = ((context.getResources().getConfiguration().screenLayout & Configuration.SCREENLAYOUT_SIZE_MASK) == Configuration.SCREENLAYOUT_SIZE_LARGE);
		return (xlarge || large);
	}
    @Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		currentupdateHandler.removeCallbacks(currentupdateTimer);
	}

	@Override
    protected void onResume() {
    	super.onResume();
    //	writSettingsfile();
    	displaycurrentreadings();
    //	connectionstatus();
    }
    
    private void connectionstatus() {
		// TODO Auto-generated method stub
    	deviceconnectionTimer = new Runnable() {
			@Override
			public void run() {
				mySwitch.setClickable(false);
				
				if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED){
					mySwitch.setChecked(true);
				//	Log.v("connection","connected");
				}else{
		    		mySwitch.setChecked(false);
		    	//	Log.v("connection","disconnected");
		    	}
				
				deviceconnectionHandler.postDelayed(this, 100);
			}
		};
		deviceconnectionHandler.postDelayed(deviceconnectionTimer, 0);
	}

	private void displaycurrentreadings() {
		// TODO Auto-generated method stub
		
    	currentupdateTimer = new Runnable() {
			@Override
			public void run() {
				try{
					int a =dbhandler.getheartableCount();
					if(currentheartdisplaycount != a){
						Log.d("inside","hello");
						blinkblinkImage(heartview);
						currentheartdisplaycheck = true;
						currentheartdisplaycount = a;	
					}
					if(currentheartdisplaycheck == true){
						heartcount = heartcount +1;
						if(heartcount == 2){
						heartview.clearAnimation();
						heartcount = 0;
						currentheartdisplaycheck = false;
						}
					}
					if(a != 0){
					Heartrate test = dbhandler.getlastheartdata(a);
					currentheart.setText(test.getVal());
					}
					
				}catch(Exception e){
					e.printStackTrace();
				}
				try{
					int a =dbhandler.getspotableCount();
					if(currentspodisplaycount != a){
						Log.d("inside","hello");
						blinkblinkImage(spoview);
						currentspodisplaycheck = true;
						currentspodisplaycount = a;	
					}
					if(currentspodisplaycheck == true){
						spocount = spocount +1;
						if(spocount == 2){
						spoview.clearAnimation();
						spocount = 0;
						currentspodisplaycheck = false;
						}
					}
					if(a != 0){
					Sporate test = dbhandler.getlastspodata(a);
					currentspo.setText(test.getVal());
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				try{
					int a =dbhandler.getacctableCount();
					if(currentaccdisplaycount != a){
						Log.d("inside","hello");
						blinkblinkImage(accview);
						currentaccdisplaycheck = true;
						currentaccdisplaycount = a;	
					}
					if(currentaccdisplaycheck == true){
						acccount = acccount +1;
						if(acccount == 2){
						accview.clearAnimation();
						acccount = 0;
						currentaccdisplaycheck = false;
						}
					}
					if(a != 0){
						Accrate test = dbhandler.getlastaccdata(a);
						currentacc.setText(test.getVal());
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				try{
					
					int a =dbhandler.getemptableCount();
					
					if(currentempdisplaycount != a){
						Log.d("inside","hello");
						blinkblinkImage(tempview);
						currentempdisplaycheck = true;
						currentempdisplaycount = a;	
					}
					if(currentempdisplaycheck == true){
						tempcount = tempcount +1;
						if(tempcount == 2){
						tempview.clearAnimation();
						tempcount = 0;
						currentempdisplaycheck = false;
						}
					}
					if(a != 0){
						Temprate test = dbhandler.getlastempdata(a);
						currentemp.setText(test.getVal());
						checktemperature.setText(test.getVal());
					}
				}catch(Exception e){
					e.printStackTrace();
				}
				currentupdateHandler.postDelayed(this, 3000);
			}
		};
		currentupdateHandler.postDelayed(currentupdateTimer, 0);
		
	}
  /*  
	private void writSettingsfile() {
		// TODO Auto-generated method stub
    	
		 FileInputStream fin = null;
			try {
				fin = openFileInput("setfile.txt");
				
	   	         int c;
	   	         String temp ="";
	   	         if((c = fin.read()) != -1){
					Log.v("setfile","notnull");
				 }else{
					 Log.v("setfile","null");
					 
					 FileInputStream finD = null;
						try {
							finD = openFileInput("profile.txt");
							
				   	         int d;
				   	         String temval ="";
					   	         if((d = finD.read()) != -1){
					   	        	Log.v("profile","notnull");
									do{
										temval = temval + Character.toString((char)d);
									}while( (d = finD.read()) != -1);
									temval = temval.substring(0, temval.length());
									String loctemp[] = temval.split(",");
								//	age = Integer.parseInt(loctemp[1]);
								//	Log.d("age",""+age);
								//	writesettingage(age);
								 }
				   	         }catch(Exception e){
								 e.printStackTrace();
								}
				}
			}catch(Exception e){
				 e.printStackTrace();
			}
	}
*/
	private void writesettingage(String i) {
		// TODO Auto-generated method stub
		int age = Integer.parseInt(i);
		Log.d("age",""+age);
		if(age >=  18 && age <= 35){
			String separator = ",";
			String heartval = "DH002+++++qq";
			String spoval = "DS003+++++qq";
			String tempval = "DT001+++++qq";
			String interval = "DD005+++++qq";
			 try {
	        FileOutputStream fOut = openFileOutput(settingsfile,MODE_PRIVATE);
	        fOut.write(heartval.getBytes());
	        fOut.write(separator.getBytes());
	        fOut.write(spoval.getBytes());
	        fOut.write(separator.getBytes());
	        fOut.write(tempval.getBytes());
	        fOut.write(separator.getBytes());
	        fOut.write(interval.getBytes());
	        fOut.close();
			} catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
			}
			
		}else if(age >= 36 && age <= 40 ){
			String separator = ",";
			String heartval = "DH002+++++qq";
			String spoval = "DS003+++++qq";
			String tempval = "DT001+++++qq";
			String interval = "DD005+++++qq";
			 try {
	        FileOutputStream fOut = openFileOutput(settingsfile,MODE_PRIVATE);
	        fOut.write(heartval.getBytes());
	        fOut.write(separator.getBytes());
	        fOut.write(spoval.getBytes());
	        fOut.write(separator.getBytes());
	        fOut.write(tempval.getBytes());
	        fOut.write(separator.getBytes());
	        fOut.write(interval.getBytes());
	        fOut.close();
			} catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
			}
		}else if(age >= 41 && age <= 55){
			String separator = ",";
			String heartval = "DH002+++++qq";
			String spoval = "DS003+++++qq";
			String tempval = "DT001+++++qq";
			String interval = "DD005+++++qq";
			 try {
	        FileOutputStream fOut = openFileOutput(settingsfile,MODE_PRIVATE);
	        fOut.write(heartval.getBytes());
	        fOut.write(separator.getBytes());
	        fOut.write(spoval.getBytes());
	        fOut.write(separator.getBytes());
	        fOut.write(tempval.getBytes());
	        fOut.write(separator.getBytes());
	        fOut.write(interval.getBytes());
	        fOut.close();
			} catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
			}
		}else if(age >= 56){
			String separator = ",";
			String heartval = "DH002+++++qq";
			String spoval = "DS003+++++qq";
			String tempval = "DT001+++++qq";
			String interval = "DD005+++++qq";
			 try {
	        FileOutputStream fOut = openFileOutput(settingsfile,MODE_PRIVATE);
	        fOut.write(heartval.getBytes());
	        fOut.write(separator.getBytes());
	        fOut.write(spoval.getBytes());
	        fOut.write(separator.getBytes());
	        fOut.write(tempval.getBytes());
	        fOut.write(separator.getBytes());
	        fOut.write(interval.getBytes());
	        fOut.close();
			} catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
			}
		}
		
	}

  @Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	  // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        scandevice = (MenuItem) menu.findItem(R.id.scans);
        resetdevice = (MenuItem) menu.findItem(R.id.reset);
        batteryindicator = (MenuItem) menu.findItem(R.id.batindicator);
      //  resetdevice.setEnabled(false);
        FileInputStream fin = null;
		try {
			fin = openFileInput("mydevice.txt");
			
   	         int c;
   	         String temp ="";
   	         if((c = fin.read()) != -1){
				Log.v("mydevice","notnull");
				 try {
					 	do{
							temp = temp + Character.toString((char)c);
						}while( (c = fin.read()) != -1);
						filedata = temp;
						pairedevicelist();
						checkforpairedevice(filedata);
						
						if (myBluetoothAdapter == null || !myBluetoothAdapter.isEnabled()) {
					    	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
					    	startActivityForResult(enableBtIntent, REQUEST_ENABLE);
					    	
						}
						} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				// 	scandevice.setEnabled(false);
			 }else{
				 Log.v("mydevice","null");
				 fileemptycheck = true;
				 scandevice.setEnabled(true);
				 if (myBluetoothAdapter == null || !myBluetoothAdapter.isEnabled()) {
				    	Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
				    	startActivityForResult(enableBtIntent, REQUEST_ENABLE);
				}
			}
		}catch(Exception e){
			 e.printStackTrace();
		}
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch (item.getItemId()) {
		case R.id.action_settings:
			settings();
		break;
		case R.id.scans:
			scanfordevice();
		break;
		case R.id.reset:
			resetting();
		break;
		case R.id.push_settings:
			pushData();
		break;
		case R.id.synch_settings:
			synchdata();
		break;
		case R.id.heartgraph:
			viewheartgraph();
		break;
		case R.id.spograph:
			viewspograph();
		break;
		case R.id.tempgraph:
			viewtempgraph();
		break;
		case R.id.checkfile:
			showfiledata();
		break;
		case R.id.filedelet:
			deletefiledata();
		break;
	}
        return super.onOptionsItemSelected(item);
    }
    
    private void deletefiledata() {
		// TODO Auto-generated method stub
    	deleteFile("brspdata.txt");
	}

	private void showfiledata() {
		// TODO Auto-generated method stub
    	Intent g = new Intent();
    	g.setAction("com.htc.bluefizz.FILEVIEW");
    	startActivity(g);
		
	}

	private void synchdata() {
		// TODO Auto-generated method stub
    	
    	if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED){
    		onDataSentoDevice("SYNC READGqq");
    		synchfunction();
    	}else{
    		showToast("please connect to device");
    	}
	}

	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
    	final MenuItem item;
    	item = menu.findItem(R.id.indication);
    	Log.d("check","icon");
    //	blueconnectionTimer = new Runnable() {
		//	@Override
	//		public void run() {
				/*if (myBluetoothAdapter.isEnabled()){
		    		item.setIcon(R.drawable.connect);
				}else{
		    		item.setIcon(R.drawable.disconnect);
		    	}*/

		    	if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED){
		    		item.setIcon(R.drawable.fiz);
		    	
		    	}else{
		    		item.setIcon(R.drawable.brokenchain);
		    	}
		//		blueconnectionHandler.postDelayed(this, 100);
		//	}
	//	};
	//	blueconnectionHandler.postDelayed(blueconnectionTimer, 0);
		    	 if(batteryval >= 95){
				    	batteryindicator.setIcon(R.drawable.batteryfull);
				    }else if(batteryval >= 85){
				    	batteryindicator.setIcon(R.drawable.batterythreequarters);
				    }else if(batteryval >= 50){
				    	batteryindicator.setIcon(R.drawable.batteryhalf);
				    }else if(batteryval >= 25){
				    	batteryindicator.setIcon(R.drawable.batteryquarter);
				    }else {
				    	batteryindicator.setIcon(R.drawable.batterylow);
				    }
		return super.onPrepareOptionsMenu(menu);
	}

	private void pushData(){
    	if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED){
			Senddevicesettings();
			onDataSentoDevice(ar.get(0));
			
			flag=true;
			setProgressBarIndeterminateVisibility(true);
			pushsettingsTimer = new Runnable() {
				@Override
				public void run() {
					if(flag == true)
		        	{
		        		Log.v("flag","true");
		        		if(inrc < ar.size()){
		        			Acknowcheck(ar.get(inrc));
		        			
						}
		        		if(Settingstimer > 5){
		        			Settingstimer = 0;
		        			onDataSentoDevice(ar.get(inrc-1));
		        		}
		        	}
					
					pushsettingsHandler.postDelayed(this, 5000);
				}
			};
			pushsettingsHandler.postDelayed(pushsettingsTimer, 0);
	
    	}else {
    			showToast("Please connect your Mobile with Device");
    	}
    }
    
    private void Senddevicesettings(){
		 FileInputStream fin = null;
			try {
				fin = openFileInput("setfile.txt");
				 int c;
	   	         String temp ="";
	   	         if((c = fin.read()) != -1){
					Log.v("setfile","notnull");
					do{
						temp = temp + Character.toString((char)c);
					}while( (c = fin.read()) != -1);
					temp = temp.substring(0, temp.length());
					String loctemp[] = temp.split(",");
					String firstdata = "SYNC SETTGqq";
					ar = new ArrayList<String>();
					ar.add(firstdata);
					for(int k=0;k<loctemp.length;k++){
						ar.add(loctemp[k]);
						Log.v("arraydata",ar.get(k));
					}
					String currentDateandTime = dateTime();
		   	        String currdat[] = currentDateandTime.split("_");
		   	        String datevar = datetimeformate(currdat[0]);
		   	        datevar = "Sd"+datevar+"qq";
		   	        String timevar = datetimeformate(currdat[1]);
		   	        timevar = "St"+timevar+"qq";
		   	        ar.add(datevar);
		   	        ar.add(timevar);
		   	     }
			}catch(Exception e){
				 e.printStackTrace();
			}
	}
	private void Acknowcheck(String s){
		if(Ackchck == true){
			onDataSentoDevice(s);
			Ackchck = false;
			inrc++;
			Settingstimer = 0;
			Log.v("size outside",""+inrc);
			if(ar.size()==inrc){
				Log.v("size inside",""+inrc);
				flag=false;
				inrc=1;
				pushsettingsHandler.removeCallbacks(pushsettingsTimer);
			}
		}else{
			Settingstimer = Settingstimer +1;
			showToast("waiting for acknownlegment");
		}
	}
	
	private void onDataSentoDevice(String s) {
		Log.v("ondatsent",s);
		if (_brsp.getBrspState() == Brsp.BRSP_STATE_READY) {
		    if (s.length() > 0) {
			String commandString = s+"\r";
			Log.v("commandString",commandString);
			_brsp.writeBytes(commandString.getBytes());
			}
		} else {
		    Toast.makeText(MainActivity.this, "BRSP not ready", Toast.LENGTH_SHORT).show();
		}
		
	}
	
	public String dateTime(){
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy_HHmmss");
    	String currentDateandTime = sdf.format(new Date());
    	return currentDateandTime;
	}
	
	public String datetimeformate(String a){
		a = a.substring(0, 2) + ":" + a.substring(2,4)+":"+a.substring(4,6);
		return a;
	}
   
    
    private void resetting() {
		// TODO Auto-generated method stub
    	
    	new AlertDialog.Builder(this).setIcon(R.drawable.warning).setTitle(R.string.msg_quit_title)
		.setMessage(R.string.msg_quit_detail).setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
		    public void onClick(DialogInterface dialog, int which) {
		    	deleteFile("mydevice.txt");
		    	deleteFile("profile.txt");
		    	deleteFile("setfile.txt");
		    	Log.d("Deleting: ", "deleting all row"); 
				int a = dbhandler.deleteAllheart();
				Log.v("rows",""+a);
				int b = dbhandler.deleteAllspo();
				Log.v("rows",""+b);
				int c = dbhandler.deleteAllacc();
				Log.v("rows",""+c);
				int d = dbhandler.deleteAlltemp();
				Log.v("rows",""+d);
				dodisconnect();
		    	finish();
		    }
		}).setNegativeButton(R.string.no, null).show();
    	
	}

	private void scanfordevice() {
		// TODO Auto-generated method stub
		if (_brsp.getConnectionState() == BluetoothGatt.STATE_DISCONNECTED){
		Intent g = new Intent(this, Scanning.class);
		g.setAction("com.htc.blueapp.SCANNING");
		startActivityForResult(g, REQUEST_SELECT_DEVICE);
		}else{
			showToast("Your App already got connected with a Device");
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		try{
			super.onActivityResult(requestCode, resultCode, data);
			
			switch (requestCode) {
			    case REQUEST_SELECT_DEVICE:
					if (resultCode == RESULT_OK) {
						selectedDeviceItem = data.getParcelableExtra("device");
						
						String title = data.getStringExtra("title");
					 //   setTitle(title);
					    String mydevice = selectedDeviceItem.toString();
					   
					    try {
					         FileOutputStream fOut = openFileOutput(dfile,MODE_PRIVATE);
					         fOut.write(mydevice.getBytes());
					         fOut.close();
					         doConnect();
					   //      scandevice.setEnabled(false);
					         
					      } catch (Exception e) {
					         // TODO Auto-generated catch block
					         e.printStackTrace();
					      }
					}
				break;
			    case REQUEST_ENABLE:
			    	if (resultCode == RESULT_OK){
				    	if(fileemptycheck == true){
				    		dialogcheck();
				    	/*	 Intent g = new Intent(this, Scanning.class);
							 g.setAction("com.htc.blueapp.SCANNING");
							 startActivityForResult(g, REQUEST_SELECT_DEVICE);*/
				    	}else{	
					    	pairedevicelist();
							checkforpairedevice(filedata);
							dialogcheck();
				    	}
			    	}
			    break;	
				default:
			  }
		}catch(Exception ex){
			showToast(ex.toString());
		}
	}

	
	private void settings() {
		// TODO Auto-generated method stub
		Intent g = new Intent();
    	g.setAction("com.htc.bluefizz.SETTINGS");
    	startActivity(g);
		
	}
	
	public void baterycheck(View v){
		if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED){
    		onDataSentoDevice("BATT LEVELqq");
    	}else{
    		showToast("please connect to device");
    	}
	}
	
	public void ontempcheck(View v){
		if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED){
    		onDataSentoDevice("BODY TEMPRqq");
    	}else{
    		showToast("please connect to device");
    	}
	}
	
	public void writebattery(String z){
		if(dbat == true){
			String dat = z.substring(0,2);
			Log.v("bat",z);
			if(!dat.equals("Cb")){
			float val = Float.parseFloat(z);
			
			val = val/1000;
			val = (float) (((val - 3.3) / .9)*100);
			 batteryval = (int) val;
			
		    if(batteryval >= 95){
		    	batteryindicator.setIcon(R.drawable.batteryfull);
		    }else if(batteryval >= 85){
		    	batteryindicator.setIcon(R.drawable.batterythreequarters);
		    }else if(batteryval >= 50){
		    	batteryindicator.setIcon(R.drawable.batteryhalf);
		    }else if(batteryval >= 25){
		    	batteryindicator.setIcon(R.drawable.batteryquarter);
		    }else {
		    	batteryindicator.setIcon(R.drawable.batterylow);
		    }
			/*String s = Integer.toString(batteryval);
			animation = ObjectAnimator.ofInt(mProgress, "progress", 0, batteryval);
	        animation.setDuration(1000);
	        animation.setInterpolator(new DecelerateInterpolator());
	        animation.start();
	        batteryrate.setText(s+"%");
			*/
			dbat = false;
			}
		}
	}
	
	public void writeTemp(String z){
		if(bodytemp == true){
			String dat = z.substring(0,1);
			Log.v("bat",z);
			if(dat.equals("T")){
				String loc[]=z.split(",");
				loc[0]=loc[0].substring(1,loc[0].length());
				float variable = Float.parseFloat(loc[0]);
				variable = variable/10;
				checktemperature.setText(variable+"");
				blinkblinkText(checktemperature);
				String s = Float.toString(variable);
				loc[2] = loc[2].substring(0,loc[2].length()-4);
				loc[2] = loc[2].substring(0,2)+":"+loc[2].substring(2,loc[2].length());
				Log.d("Insert: ", "Inserting into temptable");  
			    dbhandler.addTempdata(new Temprate(s,loc[1],loc[2])); 
			    bodytemp = false;
			}
		}
	}
	
	public void synchfunction(){
		synchTimer = new Runnable() {
			@Override
			public void run() {
				synTimer = synTimer + 1;
					if(synTimer>5){
						onDataSentoDevice("SYNC READGqq");
						synTimer = 0;
					}
				
				synchHandler.postDelayed(this, 3000);
			}
		};
		synchHandler.postDelayed(synchTimer, 3000);
	}

	private void pairedevicelist(){
		 // get paired devices
		  pairedDevices = new ArrayList<BluetoothDevice>(myBluetoothAdapter.getBondedDevices());
	    //  pairedDevices = myBluetoothAdapter.getBondedDevices();
		 Log.v("pairedDevice",""+pairedDevices.size());

	}
	
	public void checkforpairedevice(String filedata){
		 Log.v("filedata",filedata);
		 for(int j=0;j<pairedDevices.size();j++){
				if(pairedDevices.get(j).getAddress().equals(filedata)){
					selectedDeviceItem = pairedDevices.get(j);
					Log.v("selectedDeviceItem",selectedDeviceItem.toString());
					 doConnect();
					break;
				}
		 }
		 
	 }
	
	 private void doConnect() {
			if (selectedDeviceItem != null && _brsp.getConnectionState() == BluetoothGatt.STATE_DISCONNECTED) {
			    boolean result = false;
			  String bondStateText = "";
			  switch (selectedDeviceItem.getBondState()) {
			  	case BluetoothDevice.BOND_BONDED:
			  		bondStateText = "BOND_BONDED";
			  		break;
			  	case BluetoothDevice.BOND_BONDING:
			  		bondStateText = "BOND_BONDING";
			  		break;
			  	case BluetoothDevice.BOND_NONE:
			  		bondStateText = "BOND_NONE";
			  		break;
			  }
			  Log.d("bondstate", "Bond State:" + bondStateText);

			  result = _brsp.connect(this.getApplicationContext(), selectedDeviceItem);
			  Log.d("result", "Connect result:" + result);
			  
			  
				//  showToast("Device Connected");
			  
			}
	}
	
	public void dodisconnect(){
		   if (_brsp.getConnectionState() != BluetoothGatt.STATE_DISCONNECTED)
		   {
			   Log.d("disconnect","check");
			    _brsp.disconnect();
		   }
	}
	
	private void dialogcheck(){
		FileInputStream fin = null;
		try {
			fin = openFileInput("profile.txt");
			
   	         int c;
   	         String temp ="";
   	         if((c = fin.read()) != -1){
				Log.v("profile","notnull");
			 }else{
				 Log.v("profile","null");
				 Signindialog();
					
			 }
		}catch(Exception e){
			 e.printStackTrace();
		}
	}
	
	public void writdatatotable(String a){
		if(incomingdata == true){
			Log.v("incomedata","true");
			Log.v("a",a);
			String data = null;
			if(a.length() > 0){
				 data = a.substring(0,1);
				 Log.v("data",data);
				 if(data.equals("H")){
						Log.v("heart",a);
						String loc[]=a.split(",");
						loc[0]=loc[0].substring(1,loc[0].length());
						loc[2] = loc[2].substring(0,loc[2].length()-4);
						loc[2] = loc[2].substring(0,2)+":"+loc[2].substring(2,loc[2].length());
						Log.d("Insert: ", "Inserting into heartable");  
					    dbhandler.addHeartdata(new Heartrate(loc[0],loc[1],loc[2])); 
						
					}else if(data.equals("S")){
						Log.v("spo",a);
						String loc[]=a.split(",");
						loc[0]=loc[0].substring(1,loc[0].length());
						loc[2] = loc[2].substring(0,loc[2].length()-4);
						loc[2] = loc[2].substring(0,2)+":"+loc[2].substring(2,loc[2].length());
						Log.d("Insert: ", "Inserting into spotable");  
					    dbhandler.addSpodata(new Sporate(loc[0],loc[1],loc[2])); 
						
					}else if(data.equals("T")){
						Log.v("temp",a);
						String loc[]=a.split(",");
						loc[0]=loc[0].substring(1,loc[0].length());
						float variable = Float.parseFloat(loc[0]);
						variable = variable/10;
						String s = Float.toString(variable);
						loc[2] = loc[2].substring(0,loc[2].length()-4);
						loc[2] = loc[2].substring(0,2)+":"+loc[2].substring(2,loc[2].length());
						Log.d("Insert: ", "Inserting into temptable");  
					    dbhandler.addTempdata(new Temprate(s,loc[1],loc[2])); 
						
					}else if(data.equals("A")){
						Log.v("Acc",a);
						String loc[]=a.split(",");
						loc[0]=loc[0].substring(1,loc[0].length());
						loc[2] = loc[2].substring(0,loc[2].length()-4);
						loc[2] = loc[2].substring(0,2)+":"+loc[2].substring(2,loc[2].length());
						Log.d("Insert: ", "Inserting into acctable");  
					    dbhandler.addAccdata(new Accrate(loc[0],loc[1],loc[2])); 
					}
			}
			
		}
	}
	
	public void viewheartgraph(){
		Intent g = new Intent();
    	g.setAction("com.htc.bluefizz.HEARTGRAPH");
    	startActivity(g);
	}
	
	public void viewspograph(){
		Intent g = new Intent();
    	g.setAction("com.htc.bluefizz.SPOGRAPH");
    	startActivity(g);
		
		/* Log.d("Reading: ", "Reading all rows..");  
	     List<Heartrate> d = dbhandler.getAllHeartdata();         

	     for (Heartrate cn : d) {  
	      String log = "Id: "+cn.getID()+" ,Val: " + cn.getVal() + " ,date: " +   
	         cn.getdate()+",time: "+cn.getime();  
	     // Writing Contacts to log  
	     Log.d("Name: ", log);  
	 } */
	}
	/*
	public void viewaccgraph(View v){
		Intent g = new Intent();
    	g.setAction("com.htc.bluefizz.ACCGRAPH");
    	startActivity(g);
	/*	//Log.d("Deleting: ", "deleting single row");  
		//dbhandler.deleteRow("081214");
		Log.d("Deleting: ", "deleting all row"); 
		int a = dbhandler.deleteAllheart();
		//int d = dbhandler.getProfilesCount();
		Log.v("count",""+a);*/
	//}
	
	public void viewtempgraph(){
		Intent g = new Intent();
    	g.setAction("com.htc.bluefizz.TEMPGRAPH");
    	startActivity(g);
	/*	 Log.d("Reading: ", "Reading specific rows..");  
	     List<Heartrate> d = dbhandler.getselectedheartdata("101214");         

	     for (Heartrate cn : d) {  
	      String log = "Id: "+cn.getID()+" ,Val: " + cn.getVal() + " ,date: " +   
	         cn.getdate()+",time: "+cn.getime();  
	     // Writing Contacts to log  
	     Log.d("Name: ", log);  
	 } */
    	
	}
	
	public void scheck(View v){
		if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED){
    		onDataSentoDevice("MEASR SPO2qq");
    	}else{
    		showToast("please connect to device");
    	}
	}
	
	public void hcheck(View v){
		if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED){
    		onDataSentoDevice("MEASR HBPMqq");
    	}else{
    		showToast("please connect to device");
    	}
	}
	
	public void tcheck(View v){
		if (_brsp.getConnectionState() == BluetoothGatt.STATE_CONNECTED){
    		onDataSentoDevice("BODY TEMPRqq");
    	}else{
    		showToast("please connect to device");
    	}
	}
	
	public void stepcheck(View v){
		showToast("Currently No functionality available");
	}
	
	public void ccheck(View v){
		showToast("Currently No functionality available");
	}
	
	public void onSwitchClicked(View view) {
     /*   switch(view.getId()){
        case R.id.swit:
                if(mySwitch.isChecked()) {
                
                
                }
                else {
               
                
                }
         break;
        }  */
    }
	
	private void Signindialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        TextView title = new TextView(this);
        title.setText("Patient Profile");
        title.setPadding(10, 10, 10, 10);
        title.setGravity(Gravity.CENTER);
        title.setTextColor(Color.rgb(0, 0, 0));
        title.setTextSize(23);
        builder.setCustomTitle(title);
        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View layout_view = inflater.inflate(R.layout.screen_popup,null);
        builder.setView(layout_view);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
        
        diseaselist = new ArrayList<String>();
        diseaselist.add("High Blood Pressure");
        diseaselist.add("Low Blood Pressure");
        diseaselist.add("Heart Disease");
        final Spinner spn = (Spinner) layout_view.findViewById(R.id.spinner1);
        ArrayAdapter<String> disease = new ArrayAdapter<String>(this,   android.R.layout.simple_spinner_item, diseaselist);
        disease.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // The drop down view
        spn.setAdapter(disease);
        spn.setPadding(10, 10, 10, 10);
        spn.setOnItemSelectedListener(
                  new AdapterView.OnItemSelectedListener() {
                      @Override
                      public void onNothingSelected(AdapterView<?> arg0) {
                          // TODO Auto-generated method stub
                      }
					@Override
					public void onItemSelected(AdapterView<?> parent,
							View view, int position, long id) {
						int  i = spn.getSelectedItemPosition();
						selectedisease = diseaselist.get(i);
					}
                  });
        
       Button dialogButton = (Button) layout_view.findViewById(R.id.btn_close_popup);
		dialogButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				String name = null ,age = null ,dname = null ,sex = null ,separator = "," ,dnum = null,emergencycontact = null;
				radioSexGroup = (RadioGroup) layout_view.findViewById(R.id.sex);
				int selectedId = radioSexGroup.getCheckedRadioButtonId();
				radioSexButton = (RadioButton) layout_view.findViewById(selectedId);
				patientname = (EditText) layout_view.findViewById(R.id.pname);
				patientage = (EditText) layout_view.findViewById(R.id.page);
				doctorname = (EditText) layout_view.findViewById(R.id.docname);
				doctornumber = (EditText) layout_view.findViewById(R.id.docnum);
				emergencyno = (EditText) layout_view.findViewById(R.id.emergencycontact);
				name = patientname.getText().toString();
				age = patientage.getText().toString();
				dname = doctorname.getText().toString();
				dnum = doctornumber.getText().toString();
				emergencycontact = emergencyno.getText().toString();

				if(!name.matches("") && !age.matches("") && selectedId != -1 && !dnum.matches("") && !dname.matches("") && !emergencycontact.matches("")){
					if(isValidMobile(emergencycontact) && isValidMobile(dnum)){
						sex = radioSexButton.getText().toString();

						try {
							FileOutputStream fOut = openFileOutput(patientprofile,MODE_PRIVATE);
							fOut.write(name.getBytes());
							fOut.write(separator.getBytes());
							fOut.write(age.getBytes());
							fOut.write(separator.getBytes());
							fOut.write(sex.getBytes());
							fOut.write(separator.getBytes());
							fOut.write(selectedisease.getBytes());
							fOut.write(separator.getBytes());
							fOut.write(dname.getBytes());
							fOut.write(separator.getBytes());
							fOut.write(dnum.getBytes());
							fOut.write(separator.getBytes());
							fOut.write(emergencycontact.getBytes());
							fOut.close();
							alertDialog.cancel();
							writesettingage(age);
							openscannerpage();

						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}else{
						showToast("Please Enter Valide phone number");
					}
				}else{
					showToast("Please Fill the fields");
				}
			}
		});

	}

	private boolean isValidMobile(String phone)
	{
		if (phone.length()!=10) {
			return false;
		} else {
			return android.util.Patterns.PHONE.matcher(phone).matches();
		}
	}

	protected void openscannerpage() {
		// TODO Auto-generated method stub
		if(fileemptycheck == true){
			dialogcheck();
			Intent g = new Intent(this, Scanning.class);
			g.setAction("com.htc.blueapp.SCANNING");
			startActivityForResult(g, REQUEST_SELECT_DEVICE);
		}
	}

	private void panicAlertcheck(String input) {
		// TODO Auto-generated method stub
		if(panicalert == true){
			openalertdialog();
			panicalert=false;
		}

	}

	private void alertcheck(String input){
		if(alert == true){
			opendialogalert();
			alert=false;
		}
	}

	private void opendialogalert() {
		// TODO Auto-generated method stub
		String doctorphonenumber = null;
		String emergencycontact = null;
		String patientname = null;
		String sex = null;
		String disease = null;
		String age = null;
		FileInputStream fin = null;
		try {
			fin = openFileInput("profile.txt");

			int c;
			String temp ="";
			if((c = fin.read()) != -1){
				Log.v("profile","notnull");
				do{
					temp = temp + Character.toString((char)c);
				}while( (c = fin.read()) != -1);
				temp = temp.substring(0, temp.length());
				String loctemp[] = temp.split(",");

				patientname=loctemp[0];
				age=loctemp[1];
				sex=loctemp[2];
				disease=loctemp[3];
				doctorphonenumber=loctemp[5];
				emergencycontact=loctemp[6];
				if(doctorphonenumber != null){
					SmsManager sms = SmsManager.getDefault();
					sms.sendTextMessage(doctorphonenumber, null, "HELP!"+"\nPatient Name: "+patientname+"\nAge: "+age+"\nSex: "+sex+"\nDisease: "+disease, null, null);
				}
				if(emergencycontact != null){
					SmsManager sms = SmsManager.getDefault();
					sms.sendTextMessage(emergencycontact, null, "HELP!"+"\nPatient Name: "+patientname+"\nAge: "+age+"\nSex: "+sex+"\nDisease: "+disease, null, null);
				}
			}else{
				Log.v("profile","null");
			}
		}catch(Exception e){
			e.printStackTrace();
		}

	}

	private void openalertdialog() {
		// TODO Auto-generated method stub
		String doctorphonenumber = null;
		String emergencynumber = null;
		String patientname = null;
		String sex = null;
		String disease = null;
		String age = null;
		FileInputStream fin = null;
		try {
			fin = openFileInput("profile.txt");

			int c;
			String temp ="";
			if((c = fin.read()) != -1){
				Log.v("profile","notnull");
				do{
					temp = temp + Character.toString((char)c);
				}while( (c = fin.read()) != -1);
				temp = temp.substring(0, temp.length());
				String loctemp[] = temp.split(",");

				patientname=loctemp[0];
				age=loctemp[1];
				sex=loctemp[2];
				disease=loctemp[3];
				doctorphonenumber=loctemp[5];
				emergencynumber=loctemp[6];
				if(doctorphonenumber != null){
					SmsManager sms = SmsManager.getDefault();
					sms.sendTextMessage(doctorphonenumber, null, "Emergency!"+"\nPatient Name: "+patientname+"\nAge: "+age+"\nSex: "+sex+"\nDisease: "+disease, null, null);
				}
				if(emergencynumber != null){
					SmsManager sms = SmsManager.getDefault();
					sms.sendTextMessage(emergencynumber, null, "HELP!"+"\nPatient Name: "+patientname+"\nAge: "+age+"\nSex: "+sex+"\nDisease: "+disease, null, null);
				}
				if(emergencynumber != null){
					Intent intent = new Intent(Intent.ACTION_CALL);
					intent.setData(Uri.parse("tel:"+emergencynumber));
					startActivity(intent);
				}
			}else{
				Log.v("profile","null");
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	        TextView title = new TextView(this);
	        title.setText("PANIC ALERT");
	        title.setPadding(10, 10, 10, 10);
	        title.setGravity(Gravity.CENTER);
	        title.setTextColor(Color.rgb(255, 0, 0));
	        title.setTextSize(23);
	        builder.setCustomTitle(title);
	       // builder.setIcon(icon);
	        LayoutInflater inflater = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	        final View layout_view = inflater.inflate(R.layout.panicalert,null);
	        builder.setView(layout_view);
	        builder.setCancelable(false);
	        panicDialog = builder.create();
	        panicDialog.show();
	        onDataSentoDevice("PANIC MODEqq");
	        Uri soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
	        Ringtone r = RingtoneManager.getRingtone(getApplicationContext(), soundUri);
	        r.play();
	        imgBlink=(ImageView) layout_view.findViewById(R.id.panicimage);
	        blinkblinkImage(imgBlink);
	        Button okbutton=(Button) layout_view.findViewById(R.id.panicokay); 
	        okbutton.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					
					panicDialog.cancel();
				}
			});
		
	}
	
	protected void blinkblinkImage(ImageView img) {
			anima = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
		    anima.setDuration(400); // duration - half a second
		    anima.setInterpolator(new LinearInterpolator()); // do not alter
		    anima.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
		    anima.setRepeatMode(Animation.REVERSE); // Reverse animation at
		    img.startAnimation(anima);
		  //img.clearAnimation();

		}
	protected void blinkblinkText(TextView text) {
		textblink = new AlphaAnimation(1, 0); // Change alpha from fully visible to invisible
		textblink.setDuration(1000); // duration - half a second
		textblink.setInterpolator(new LinearInterpolator()); // do not alter
		textblink.setRepeatCount(Animation.INFINITE); // Repeat animation infinitely
		textblink.setRepeatMode(Animation.REVERSE); // Reverse animation at
		text.startAnimation(textblink);
	  //text.clearAnimation();

	}

	private final BroadcastReceiver mPairReceiver = new BroadcastReceiver() {
		 public void onReceive(Context context, Intent intent) {
		   String action = intent.getAction();
		   	if (BluetoothDevice.ACTION_BOND_STATE_CHANGED.equals(action)) {
		   		final int state = intent.getIntExtra(BluetoothDevice.EXTRA_BOND_STATE, BluetoothDevice.ERROR);
		   		final int prevState = intent.getIntExtra(BluetoothDevice.EXTRA_PREVIOUS_BOND_STATE, BluetoothDevice.ERROR);
		   		if (state == BluetoothDevice.BOND_BONDED && prevState == BluetoothDevice.BOND_BONDING) {
		   			showToast("Paired");
					pushData();
		   			//doConnect();
		   		} else if (state == BluetoothDevice.BOND_NONE && prevState == BluetoothDevice.BOND_BONDED){
		   			//resetdevice.setEnabled(true);
		   			showToast("your device is Unpaired. Please restart your app by pressing restart button");
		   		}
		   	}
		 }
	};
	
    @Override
   	protected void onDestroy() {
   		// TODO Auto-generated method stub
      	//deleteFile("mydevice.txt");
    /*	Log.d("Deleting: ", "deleting all row"); 
		int a = dbhandler.deleteAllheart();
		Log.v("rows",""+a);*/
    	dodisconnect();
    	unregisterReceiver(mPairReceiver);
       super.onDestroy();
   	}
    
    private void writdatatofile(String input) {
		// TODO Auto-generated method stub
    	String sample = "\n\n";
    	String separater = "(::)";
    	if(incomingdata == true){
    	try {
	        FileOutputStream fOut = openFileOutput(devicedata,MODE_APPEND);
	        if(input.equals("Dincome")){
	        	fOut.write(sample.getBytes());
	        }
	        fOut.write(input.getBytes());
	        fOut.write(separater.getBytes());
	        fOut.close();
		  } catch (Exception e) {
	        // TODO Auto-generated catch block
	        e.printStackTrace();
	     }
    	}
		
	}
    
    public void showToast(String message) {
	 	   Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	 	}
 
}

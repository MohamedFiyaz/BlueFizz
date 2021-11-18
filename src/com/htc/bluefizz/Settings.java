package com.htc.bluefizz;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.FileInputStream;
import java.io.FileOutputStream;

public class Settings extends Activity {
	
	private EditText editheart;
	private EditText editspo;
	private EditText edittemp;
	private EditText editinter;
	private EditText emergencyphone;
	private TextView patname;
	private TextView patage;
	private Button setbutton;
    private Button savenumber;
	private String separator = ",";
	public String settingsfile = "setfile.txt";
	private String tempdata = "";
	public String patientprofile = "profile.txt";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings);
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		editheart = (EditText) findViewById(R.id.heartrate);
		editspo = (EditText) findViewById(R.id.spo2);
		edittemp = (EditText) findViewById(R.id.temp);
		editinter = (EditText) findViewById(R.id.interval);
		setbutton = (Button) findViewById(R.id.set);
        savenumber = (Button) findViewById(R.id.editsaves);
		editheart.setEnabled(false); 
	//	editheart.setFocusable(false);
		editspo.setEnabled(false);
		edittemp.setEnabled(false);
		editinter.setEnabled(false);
		setbutton.setEnabled(false);
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
		   	        String ar[] = temp.split(","); 
		   	        String heartdata = datadisplay(ar[0]);
		   	        String spodata = datadisplay(ar[1]);
		   	        String tempdata = datadisplay(ar[2]);
		   	        String interdata = datadisplay(ar[3]);
					editheart.setText(heartdata.toString());
					editspo.setText(spodata.toString());
					edittemp.setText(tempdata.toString());
					editinter.setText(interdata.toString());
					
					
				 }
			}catch(Exception e){
				 e.printStackTrace();
			}
		patientsettings();
	}
	private void patientsettings() {
		// TODO Auto-generated method stub
		patname = (TextView) findViewById(R.id.patname);
		patage = (TextView) findViewById(R.id.patage);
		emergencyphone = (EditText) findViewById(R.id.ephonenumber);

		FileInputStream fin = null;
		try {
			fin = openFileInput("profile.txt");
			
   	         int c;
   	         String temp ="";
   	         if((c = fin.read()) != -1){
				Log.v("setfile","notnull");
				do{
					temp = temp + Character.toString((char)c);
				}while( (c = fin.read()) != -1);
				temp = temp.substring(0, temp.length());
				 tempdata = temp;
	   	        String ar[] = temp.split(","); 
	   	        patname.setText(ar[0]);
	   	        patage.setText(ar[1]);
                emergencyphone.setText(ar[6]);
			}
		}catch(Exception e){
			 e.printStackTrace();
		}
		
	}

    public void editsavefunction(View v){

        if(savenumber.getText().toString().equals("edit")){
            emergencyphone.setEnabled(true);
            savenumber.setText("save");
        }else{
            emergencyphone.setEnabled(false);
            savenumber.setText("edit");
			Log.v("temp",tempdata);
			String changednumber = emergencyphone.getText().toString();
			if(isValidMobile(changednumber)){
				String ar[] = tempdata.split(",");
				try {
					FileOutputStream fOut = openFileOutput(patientprofile,MODE_PRIVATE);
					fOut.write(ar[0].getBytes());
					fOut.write(separator.getBytes());
					fOut.write(ar[1].getBytes());
					fOut.write(separator.getBytes());
					fOut.write(ar[2].getBytes());
					fOut.write(separator.getBytes());
					fOut.write(ar[3].getBytes());
					fOut.write(separator.getBytes());
					fOut.write(ar[4].getBytes());
					fOut.write(separator.getBytes());
					fOut.write(ar[5].getBytes());
					fOut.write(separator.getBytes());
					fOut.write(changednumber.getBytes());
					fOut.close();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}else{
				showToast("Please Enter Valide phone number");
			}
		}
	}

	private boolean isValidMobile(String phone)
	{
		if (phone.length()!=10) {
			return false;
		} else {
			return android.util.Patterns.PHONE.matcher(phone).matches();
		}
	}

	public void clickchangebutton(View view){
		editheart.setEnabled(true);
		editspo.setEnabled(true);
		edittemp.setEnabled(true);
		editinter.setEnabled(true);
		setbutton.setEnabled(true);
	}
	
	public void clicksetbutton(View view){
		String h = editheart.getText().toString();
		String s = editspo.getText().toString();
		String t = edittemp.getText().toString();
		String i = editinter.getText().toString();
		if(h.length() == 2){
			h="0"+h;
		}else if(h.length() == 1){
			h="00"+h;
		}
		if(s.length() == 2){
			s="0"+s;
		}else if(s.length() == 1){
			s="00"+s;
		}
		if(t.length() == 2){
			t="0"+t;
		}else if(t.length() == 1){
			t="00"+t;
		}
		if(i.length() == 2){
			i="0"+i;
		}else if(i.length() == 1){
			i="00"+i;
		}
		String heartval = "DH" + h;
		String spoval = "DS" + s;
		String tempval = "DT" + t;
		String interval = "DD" + i;
	
		
		heartval = heartval + datastruct(heartval.length());
		spoval = spoval + datastruct(spoval.length());
		tempval = tempval + datastruct(tempval.length());
		interval = interval + datastruct(interval.length());
		
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
		Toast.makeText(getApplicationContext(),"Please push the updated settings to the Device.By clicking 'Push Button' in ActionBar", Toast.LENGTH_LONG).show();
		editheart.setEnabled(false);
		editspo.setEnabled(false);
		edittemp.setEnabled(false);
		editinter.setEnabled(false);
		setbutton.setEnabled(false);
	}
	
	public String datastruct (int val){
		String plus="";
		 for(int i=0;i<10-val;i++){
			  plus =plus + "+";
		 }plus = plus + "qq";
		return plus;
		
	}
	
	public String datadisplay(String data){
		String val = data.toString().substring(2, 10);
		String test = "";
		
		for(int i = 0; i < val.length(); i++)
		{	
			test   = test + val.charAt(i);
			if(test.endsWith("+")){
			break;	
			}
				
		}
		String result = test.substring(0, test.length()-1);
		  Log.v("test",result);
		return result;
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
	}
	
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}

	@Override
	public void finish() {
		// TODO Auto-generated method stub
		super.finish();
	}
	public void showToast(String message) {
	 	   Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	 	}
}

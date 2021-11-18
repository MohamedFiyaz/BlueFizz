package com.htc.bluefizz;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.BarChart.Type;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.BasicStroke;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;
import org.achartengine.tools.PanListener;
import org.achartengine.tools.ZoomEvent;
import org.achartengine.tools.ZoomListener;
import org.achartengine.util.MathHelper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.Paint.Align;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class Accgraph extends Activity{

	private XYMultipleSeriesRenderer multiRenderer;
	private View mChart;
	public DatabaseHandler dbhandler;
	private ArrayList<String> accdata;
	private ArrayList<String> acctime;
	private ArrayList<String> accdate;
	private Calendar calendar;
	private TextView dateView ;
	private Button go; 
	private int year, month, day;
	final Handler accrateHandler = new Handler();
	private Runnable accrateTimer;
	private boolean zoomenabler = false;
	String selecteday = initialdisplay();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.accgraph);
		dbhandler = new DatabaseHandler(this);
		
		dateView = (TextView) findViewById(R.id.selectedate);
		go = (Button) findViewById(R.id.go);
		go.setEnabled(false);
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		super.onPause();
		accrateHandler.removeCallbacks(accrateTimer);
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		accgraphdisplay();
	}
	
	public void accgraphdisplay(){
		
		accrateTimer = new Runnable() {
			@Override
			public void run() {
			
				currentdatacheck();
				Log.v("test","fiyaz");
				accrateHandler.postDelayed(this, 10000);
			}
		};
		accrateHandler.postDelayed(accrateTimer, 0);
		
	}
	private void currentdatacheck() {
		// TODO Auto-generated method stub
		String currentdate = date();
		
		Log.d("Reading: ", "Reading specific rows..");  
	     List<Accrate> d = dbhandler.getselectedaccdata(currentdate);         
	     if(d.size()>0){
	    	accdata = new ArrayList<String>();
	 		accdate = new ArrayList<String>();
	 		acctime = new ArrayList<String>();
			 for (Accrate cn : d) {  
				 accdata.add(cn.getVal());
				 accdate.add(cn.getdate());
				 acctime.add(cn.getime());
				 String log = "Id: "+cn.getID()+" ,Val: " + cn.getVal() + " ,date: " +   cn.getdate()+",time: "+cn.getime();  
				 Log.d("Name: ", log);  
			 } 
			 opengraph();
	     }else{
	    	 LinearLayout chartContainer = (LinearLayout) findViewById(R.id.drawgraph);
	     	 chartContainer.removeAllViews();
	    	 showToast("No data available to display graph");
	     }
	}
	
	@Override
	   protected Dialog onCreateDialog(int id) {
	   // TODO Auto-generated method stub
	      if (id == 999) {
	    	 
	         DatePickerDialog dialog = new DatePickerDialog(this, myDateListener, year, month, day);
	         dialog.getDatePicker().setMaxDate(new Date().getTime());
	         return dialog;
	       }
	      return null;
	   }

	   

	private DatePickerDialog.OnDateSetListener myDateListener
	   = new DatePickerDialog.OnDateSetListener() {
		  
		   @Override
		   public void onDateSet(DatePicker arg0, int arg1, int arg2, int arg3) {
		      // TODO Auto-generated method stub
		      // arg1 = year
		      // arg2 = month
		      // arg3 = day
		      showDate(arg1, arg2+1, arg3);
		   }
		   
	   };

	   private void showDate(int year, int month, int day) {
		   dateView.setText(new StringBuilder().append(day).append("/")
				      .append(month).append("/").append(year));
	     
	   }

	  
	
	public void ondateclick(View v){
		showDialog(999);
		go.setEnabled(true);
	
	}
	
	public void onviewdateclick(View v){
		String result = null;
		go.setEnabled(false);
		 String day =  dateView.getText().toString();
		 Log.v("day",day);
		 
		   String loctem[]=day.split("/");
		   if(loctem[0].length() < 2){
			   loctem[0]="0"+loctem[0];
		   }
		   if(loctem[1].length() < 2){
			   loctem[1]="0"+loctem[1];
		   }
		   loctem[2]=loctem[2].substring(2,loctem[2].length());
		   result = loctem[0]+loctem[1]+loctem[2];
		   getdatafromtable(result);
	}
	
	private void getdatafromtable(String result) {
		// TODO Auto-generated method stub
		if(result.equals(date())){
			Log.d("same", "same date"); 
			accgraphdisplay();
		}else{
			accrateHandler.removeCallbacks(accrateTimer);
			 Log.d("Reading: ", "Reading specific rows..");  
			 List<Accrate> d = dbhandler.getselectedaccdata(result);         
			 if(d.size()>0){
				accdata = new ArrayList<String>();
				accdate = new ArrayList<String>();
				acctime = new ArrayList<String>();
				 for (Accrate cn : d) {  
					 accdata.add(cn.getVal());
					 accdate.add(cn.getdate());
					 acctime.add(cn.getime());
					 String log = "Id: "+cn.getID()+" ,Val: " + cn.getVal() + " ,date: " +   cn.getdate()+",time: "+cn.getime();  
					 Log.d("Name: ", log);  
				 } 
				 opengraph();
			 }else{
				 LinearLayout chartContainer = (LinearLayout) findViewById(R.id.drawgraph);
			     chartContainer.removeAllViews();
				 showToast("No data available to display graph");
			 }
		}
	}

	public void opengraph(){
		Double highestval = 0.0;
		XYSeries accRate = new XYSeries("Accval");
		XYMultipleSeriesDataset dataset = new XYMultipleSeriesDataset();
			 Double[] aval = new Double[accdata.size()];
    		 for (int i = 0; i < accdata.size(); i++) {
    			 aval[i] = Double.parseDouble(accdata.get(i));
    		}
    		// Adding data to Series
        	for(int i=0;i<aval.length;i++){
	        	accRate.add(i,aval[i]);
	        	if(aval[i] > highestval){
	        		highestval = aval[i];
	        	}
	        	
        	}
        	
        	// Creating a dataset to hold each series
        	dataset.addSeries(accRate);
       	// Creating XYSeriesRenderer to customize accRate
    	XYSeriesRenderer accRenderer = new XYSeriesRenderer();
    	accRenderer.setColor(Color.MAGENTA); 
    	accRenderer.setFillPoints(true);
    	accRenderer.setLineWidth(2);
    	accRenderer.setDisplayChartValues(true);
       	accRenderer.setDisplayChartValuesDistance(10);
    //	accRenderer.setPointStyle(PointStyle.TRIANGLE);
    //	accRenderer.setStroke(BasicStroke.DOTTED); 
    	
    	// Creating a XYMultipleSeriesRenderer to customize the whole chart
        multiRenderer = new XYMultipleSeriesRenderer();
    	
    	multiRenderer.setChartTitle("Patient Reading Graph on "+selecteday);
    	multiRenderer.setXTitle("Time");
    	multiRenderer.setYTitle("Readings obtained from device");
    	
    	/***
    	* Customizing graphs
    	*/
    	
    	//setting text size of the title
    	multiRenderer.setChartTitleTextSize(25);
    	//setting text size of the axis title
    	multiRenderer.setAxisTitleTextSize(23);
    	//setting text size of the graph lable
    	multiRenderer.setLabelsTextSize(20);
    	//setting zoom buttons visiblity
    	multiRenderer.setZoomButtonsVisible(false);
    	//setting pan enablity which uses graph to move on both axis
    	multiRenderer.setPanEnabled(true, true);
    	
    	//setting click false on graph
    	multiRenderer.setClickEnabled(false);
    	//setting zoom to false on both axis
    	multiRenderer.setZoomEnabled(true, true);
    	//setting lines to display on y axis
    	multiRenderer.setShowGridY(false);
    	//setting lines to display on x axis
    	multiRenderer.setShowGridX(false);
    	//setting legend to fit the screen size
    	multiRenderer.setFitLegend(true);
    	//setting displaying line on grid
    	multiRenderer.setShowGrid(true);
    	//setting zoom to false
    	multiRenderer.setZoomEnabled(true);
    	//setting external zoom functions to false
    	multiRenderer.setExternalZoomEnabled(false);
    	//setting displaying lines on graph to be formatted(like using graphics)
    	multiRenderer.setAntialiasing(true);
    	//setting to in scroll to false
    	multiRenderer.setInScroll(false);
    	//setting to set legend height of the graph
    	multiRenderer.setLegendHeight(200);
    	multiRenderer.setLegendTextSize(25);
    	multiRenderer.setXLabelsPadding(8);
    	
    	//setting y axis label to align
    	multiRenderer.setYLabelsAlign(Align.LEFT);
    	//setting text style
    	multiRenderer.setTextTypeface("sans_serif", Typeface.NORMAL);
    	//Setting background color of the graph to transparent
    	multiRenderer.setBackgroundColor(Color.TRANSPARENT);
    	//Setting margin color of the graph to transparent
    	//multiRenderer.setMarginsColor(getResources().getColor(R.color.transparent_background));
    	multiRenderer.setApplyBackgroundColor(true);
    	multiRenderer.setScale(2f);
    	//setting x axis point size
    	multiRenderer.setPointSize(4f);
    	//setting the margin size for the graph in the order top, left, bottom, right
    	multiRenderer.setMargins(new int[]{30, 30, 30, 0});
    	
    	multiRenderer.setXLabelsAlign(Align.CENTER);
    	//setting no of values to display in y axis
    	multiRenderer.setYLabels(10);
    	// setting y axis max value, Since i'm using static values inside the graph so i'm setting y max value to 4000.
    	// if you use dynamic values then get the max y value and set here
    	multiRenderer.setYAxisMax(highestval);
    	//setting used to move the graph on xaxiz to .5 to the right
    	multiRenderer.setXAxisMin(0);
    	//setting used to move the graph on xaxiz to .5 to the right
    	
    	//setting bar size or space between two bars
    	multiRenderer.setBarSpacing(0.3);
    	
    	if(acctime.size() <= 10){
    		multiRenderer.setXAxisMax(acctime.size());
    		multiRenderer.setXLabelsAngle(360);
    		multiRenderer.setXLabels(RESULT_OK);
    		for(int i=0; i< acctime.size();i++){
        		multiRenderer.addXTextLabel(i,acctime.get(i));
        		}
    		int size = acctime.size();
        	double con = (int)size;
        	double[] i ={0.0,con,0.0,highestval+10.0};
        	multiRenderer.setZoomLimits(i);
        	multiRenderer.setPanLimits(i);
    	}else if(acctime.size() > 10 && acctime.size() < 20){
    		multiRenderer.setXAxisMax(acctime.size());
    		multiRenderer.setXLabelsAngle(315);
    		multiRenderer.setXLabels(RESULT_OK);
    		for(int i=0; i< acctime.size();i++){
        		multiRenderer.addXTextLabel(i,acctime.get(i));
        		}
    		int size = acctime.size();
        	double con = (int)size;
        	double[] i ={0.0,con,0.0,highestval+10.0};
        	multiRenderer.setZoomLimits(i);
        	multiRenderer.setPanLimits(i);
    	}else if(acctime.size() >= 20){
    		multiRenderer.setXAxisMax(20);
    		multiRenderer.setXLabelsAngle(315);
    		zoomenabler = true;
    		multiRenderer.setXLabels(RESULT_OK);
    		multiRenderer.addXTextLabel(0,acctime.get(0));
    		multiRenderer.addXTextLabel(acctime.size()/2,acctime.get(acctime.size()/2));
    		multiRenderer.addXTextLabel(acctime.size()-1,acctime.get(acctime.size()-1));
    		int size = acctime.size();
        	double con = (int)size;
        	double[] i ={0.0,con,0.0,highestval+10.0};
        	multiRenderer.setZoomLimits(i);
        	multiRenderer.setPanLimits(i);
    	}
    	int size = acctime.size();
    	double con = (int)size;
    	double[] i ={0.0,con,0.0,highestval};
    //	multiRenderer.setZoomLimits(i);
    	//multiRenderer.setPanLimits(i);
    //	multiRenderer.setZoomInLimitY(highestval+10.0);
    //	multiRenderer.setZoomInLimitX(con+10.0);
    	
    	// Note: The order of adding dataseries to dataset and renderers to multipleRenderer
    	// should be same
    	/*	
      */multiRenderer.addSeriesRenderer(accRenderer);
    	LinearLayout chartContainer = (LinearLayout) findViewById(R.id.drawgraph);
    	chartContainer.removeAllViews();
    	mChart = ChartFactory.getBarChartView(Accgraph.this, dataset, multiRenderer,Type.DEFAULT);
    	chartContainer.addView(mChart);
    	
    	((GraphicalView) mChart).addZoomListener(new ZoomListener() {
         	  public void zoomApplied(ZoomEvent e) {
         		  if(zoomenabler == true){
         			  double start = multiRenderer.getXAxisMin();
         			  double stop = multiRenderer.getXAxisMax();
         			  multiRenderer.setXLabels(RESULT_OK);
		         	  List<Double> labels = MathHelper.getLabels(start, stop, 15);
         	  
		         	/*  for (Double label : labels) {
		         		  int i = label.intValue();
		         		  multiRenderer.addXTextLabel(label, acctime.get(i));
		         	  }*/
		         	 for(int i=0; i< acctime.size();i++){
		         		multiRenderer.addXTextLabel(i,acctime.get(i));
		         		}
		         	 zoomenabler=false;
         		  }
         	  }
         	  public void zoomReset() {
         		 showToast("hi");
         	  }
         	}, true, true);
    	
    	 ((GraphicalView) mChart).addPanListener(new PanListener() {
    	        @Override
    	        public void panApplied() {
    	           // showToast("hi");
    	        }
    	    });
    	
	}
	
	public String initialdisplay(){
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyyyy");
    	String currentDateandTime = sdf.format(new Date());
    	currentDateandTime = currentDateandTime.substring(0,2)+"/"+currentDateandTime.substring(2,4)+"/"+currentDateandTime.substring(4,currentDateandTime.length());
    	return currentDateandTime;
	}
	
	public String date(){
		SimpleDateFormat sdf = new SimpleDateFormat("ddMMyy");
    	String currentDateandTime = sdf.format(new Date());
    	return currentDateandTime;
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
	}
	
	public void showToast(String message) {
	 	   Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
	 	}

}

package com.tetrahedrontech.icbustrackernewversion.cards;


import com.tetrahedrontech.icbustrackernewversion.R;
import com.tetrahedrontech.icbustrackernewversion.API.coreAPI;

import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.NumberPicker;
import android.widget.Toast;
import it.gmariotti.cardslib.library.internal.CardExpand;

public class routeListDetailCardExpand extends CardExpand implements NumberPicker.OnValueChangeListener{
	
	//the route name the user wants to set alarm
	private String routeName="";
	//the current prediction time of the route
	private String min="";
	//the stop id
	private int stopId=-1;
	//the alert time the user sets
	private int alertTime=-1;
	//upperBound is used to do the logical determination
	private int upperBound=-1;
	private coreAPI api=new coreAPI();
	//-1=no error, 0=times up, 1=lose bus prediction, 2=Internet Problem
	private int errorCode=-1;
	
	public routeListDetailCardExpand(Context context) {
        super(context, R.layout.stop_detail_expand_layout);
    }
	
	@Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        if (view == null) return;

        //Retrieve button elements
        Button button=(Button) view.findViewById(R.id.alarm);
        button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showNumberPicker();
			}
		});
    }
	
	public void setContent(String routeName, String min, int stopId){
		this.routeName=routeName;
		this.min=min;
		this.stopId=stopId;
	}
	
	//this method shows a number picker dialog
	private void showNumberPicker(){
		Log.i("mytag","time:before"+Integer.toString(alertTime));
		final Dialog d = new Dialog(getContext());
        d.setTitle("Remind me when the bus is");
        d.setContentView(R.layout.number_picker_layout);
        
        //it has 2 button, cancel and set
        Button cancel = (Button) d.findViewById(R.id.cancel_btn);
        Button set = (Button) d.findViewById(R.id.set_btn);
        final NumberPicker np = (NumberPicker) d.findViewById(R.id.number_picker);
        
        //set the maxValue and minValue of the number picker
        np.setMaxValue(Math.max(0,Integer.valueOf(min)-1));
        np.setMinValue(0);
        if (alertTime != -1){
        	np.setValue(alertTime);
        }
        np.setWrapSelectorWheel(true);
        np.setOnValueChangedListener(this);
        
        //define what to do when the cancel is clicked
        //here, just dismiss the dialog
        cancel.setOnClickListener(new OnClickListener()
        {
         @Override
         public void onClick(View v) {
             d.dismiss();
          }    
         });
        
        //define what to do when the set is clicked
        //here, we need to set the alartTime, set up the alarm and dismiss the dialog
        set.setOnClickListener(new OnClickListener()
        {
         @Override
         public void onClick(View v) {
        	 alertTime=np.getValue();
        	 Toast.makeText(getContext(), "Alarm set for "+routeName+" when it is "+Integer.toString(alertTime)+" minutes away!", Toast.LENGTH_SHORT).show();
        	 setUpAlarm();
             d.dismiss();
          }    
         });
      d.show();
	}
	
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
    }
	
	//thie method set up an alarm for user. It will alarm the user when the time is up
	private void setUpAlarm(){
		//Log.i("mytag","alertTime: "+String.valueOf(alertTime));
		
		//the initial value of upperBound is current bus prediction time
		upperBound=Integer.valueOf(min);
		
		//start a thread to continuously track bus prediction time
		final alarmOperation getData=new alarmOperation();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		    getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, "");
		else
		    getData.execute("");
		
		//new alarmOperation().execute("");
	}
	
	//this class continuously track the bus prediction time
	private class alarmOperation extends AsyncTask<String, Void, String> {
		
		//this method get the bus prediction time from api
		@Override
        protected String doInBackground(String... params) {
			while(errorCode==-1){
				
				//check if the device is connected to the Internet
				if(!isOnline()){
					errorCode=2;
					break;
				}
				
				//download prediction data and check if there is an event:times up, lose bus or disconnect from
				//the Internet
				String lines=api.busPrediction(stopId);
				checkTerminate(lines);
				
				//if no event, sleep 1 second and check again
				if(errorCode==-1){
				try {
					Thread.currentThread().sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				}
			}
			//Log.i("mytag","get out of while loop");
			return "";
		}
		
		//this method shows message to users when an event occurs: times up or error
		@Override
		protected void onPostExecute(String result){
			//Log.i("mytag","in onPostExecute");
			switch(errorCode){
			case 0:
					timeUp();
					//Toast.makeText(getContext(), "times up", Toast.LENGTH_LONG).show();
					break;
			case 1:Toast.makeText(getContext(), "sorry, lose the bus", Toast.LENGTH_LONG).show();
					break;
			case 2:Toast.makeText(getContext(), "Internet problem", Toast.LENGTH_LONG).show();
					break;
			}
			
			//init the parameters and be ready for next alarm setting
			alertTime=-1;
			upperBound=-1;
			errorCode=-1;
		}
	}
	
	//this method check bus prediction time and do the logical determination to see if an event occurs
	//if there is an event, it will change the errorCode
	private void checkTerminate(String lines){
		
		//if there is no bus predictions -> lose bus
		if (lines.length()==0){
			errorCode=1;
		}
		
		String line[]=lines.split(";");
		
		//check bus predictions in reversed order
		for (int i=line.length-1;i>=0;i--){
			//Log.i("mytag",line[i]);
			String singlePre[]=line[i].split(",");
			//Log.i("mytag",String.valueOf(singlePre[0].equals(routeName)));
			//Log.i("mytag",String.valueOf(Integer.valueOf(singlePre[1])<=upperBound));
			
			//if we found the bus the user want to track
			if(singlePre[0].equals(routeName) && Integer.valueOf(singlePre[1])<=upperBound){
				//Log.i("mytag","in first if");
				
				//if the arriving time is less than or equal to the alerTime -> times up
				if(Integer.valueOf(singlePre[1])<=alertTime){
					//Log.i("mytag","in second if");
					errorCode=0;
					return;
				}
				//still need more time
				else{
					//Log.i("mytag","in second else");
					upperBound=Integer.valueOf(line[i].split(",")[1]);
					return;
				}
			}
		}
		//lose buses
		errorCode=1;
	}
	
	//this methods checks if the device is connected to the Internet and can receive data
	private boolean isOnline(){
		ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting() && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
			return true;}
		return false;
	}
	
	//this method deals with the event --time up
	private void timeUp(){
		//get mode settings
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(getContext());
		String alarmMode=sharedPref.getString("alarm_mode", "auto");
		Log.i("mytag","time up");
		Log.i("mytag","mode in settings: "+alarmMode);
		
		//get current device ringer mode
		AudioManager myAudioManager = (AudioManager) getContext().getSystemService(Context.AUDIO_SERVICE);
		int ringer = myAudioManager.getRingerMode();
		
		//logic
		if ((alarmMode.equals("Auto") && ringer == myAudioManager.RINGER_MODE_NORMAL) || (alarmMode.equals("Sound"))){
			ringAlarm(0);
		}
		else if ((alarmMode.equals("Auto") && ringer == myAudioManager.RINGER_MODE_VIBRATE) || alarmMode.equals("Vibrate")){
			ringAlarm(1);
		}
		else if ((alarmMode.equals("Auto") && ringer == myAudioManager.RINGER_MODE_SILENT) || alarmMode.equals("Silence")){
			ringAlarm(2);
		}
	}
	
	//ring alarm
	//0=sound, 1=vibrate, 2=silence
	private void ringAlarm(int mode){
		Log.i("mytag",String.valueOf(mode));
		switch (mode){
		case 0 : {
			Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
			Ringtone r = RingtoneManager.getRingtone(getContext(), notification);
			r.play();
			break;
		}
		case 1 : {
			Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
			v.vibrate(1000);
			break;
		}
		case 2 : {
			break;
		}
		}
	}
			
		
}

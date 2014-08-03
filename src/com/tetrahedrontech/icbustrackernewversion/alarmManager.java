package com.tetrahedrontech.icbustrackernewversion;

import java.util.ArrayList;

import com.tetrahedrontech.icbustrackernewversion.API.coreAPI;

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
import android.widget.Toast;

public class alarmManager {
	private Context context;
	private coreAPI api= new coreAPI();
	private ArrayList<alarmOperation> alarms = new ArrayList<alarmOperation>();
	private static alarmManager instance=null;
	
	
	private alarmManager(Context context){
		this.context=context;
	}
	
	public static alarmManager getInstance(Context context){
		if (instance ==null){
			instance=new alarmManager(context);
		}
		return instance;
	}
	
	public void addAlarm(int stopId, String routeName, String upperBound, int alartTime){
		//start a thread to continuously track bus prediction time
		final alarmOperation getData=new alarmOperation();
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
			getData.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, String.valueOf(stopId),routeName,upperBound, String.valueOf(alartTime));
		else
			getData.execute(String.valueOf(stopId),routeName,upperBound, String.valueOf(alartTime));
		alarms.add(getData);
	}
	
	

		//this class continuously track the bus prediction time
		private class alarmOperation extends AsyncTask<String, Void, String> {
			//-1=no error, 0=times up, 1=lose bus prediction, 2=Internet Problem
			private int errorCode=-1;
			private String routeName;
			private int stopId;
			//upperBound is used to do the logical determination
			private int upperBound;
			//the alert time the user sets
			private int alertTime;
			
			//this method get the bus prediction time from api
			@Override
	        protected String doInBackground(String... params) {
				stopId=Integer.valueOf(params[0]);
				routeName=params[1];
				upperBound=Integer.valueOf(params[2]);
				alertTime=Integer.valueOf(params[3]);
				
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
						timeUp(routeName, alertTime);
						//Toast.makeText(getContext(), "times up", Toast.LENGTH_LONG).show();
						break;
				case 1:Toast.makeText(context, "sorry, lose the bus", Toast.LENGTH_LONG).show();
						break;
				case 2:Toast.makeText(context, "Internet problem", Toast.LENGTH_LONG).show();
						break;
				}
				/*
				//init the parameters and be ready for next alarm setting
				alertTime=-1;
				upperBound=-1;
				errorCode=-1;*/
				alarms.remove(this);
			}
			
			//this method check bus prediction time and do the logical determination to see if an event occurs
			//if there is an event, it will change the errorCode
			private void checkTerminate(String lines){
				
				//if there is no bus predictions -> lose bus
				if (lines.length()==0){
					errorCode=1;
				}
				
				String line[]=lines.split("\\?");
				
				//check bus predictions in reversed order
				for (int i=line.length-1;i>=0;i--){
					//Log.i("mytag",line[i]);
					String singlePre[]=line[i].split(";");
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
							upperBound=Integer.valueOf(line[i].split(";")[1]);
							return;
						}
					}
				}
				//lose buses
				errorCode=1;
			}
		}
		
		
		
		//this methods checks if the device is connected to the Internet and can receive data
		private boolean isOnline(){
			ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = cm.getActiveNetworkInfo();
			if (netInfo != null && netInfo.isConnectedOrConnecting() && cm.getActiveNetworkInfo().isAvailable() && cm.getActiveNetworkInfo().isConnected()) {
				return true;}
			return false;
		}
		
		//this method deals with the event --time up
		private void timeUp(String routeName, int alertTime){
			//get mode settings
			SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
			String alarmMode=sharedPref.getString("alarm_mode", "auto");
			Log.i("mytag","time up");
			Log.i("mytag","mode in settings: "+alarmMode);
			
			//get current device ringer mode
			AudioManager myAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
			int ringer = myAudioManager.getRingerMode();
			
			//logic
			if ((alarmMode.equals("Auto") && ringer == myAudioManager.RINGER_MODE_NORMAL) || (alarmMode.equals("Sound"))){
				ringAlarm(0,routeName, alertTime);
			}
			else if ((alarmMode.equals("Auto") && ringer == myAudioManager.RINGER_MODE_VIBRATE) || alarmMode.equals("Vibrate")){
				ringAlarm(1,routeName, alertTime);
			}
			else if ((alarmMode.equals("Auto") && ringer == myAudioManager.RINGER_MODE_SILENT) || alarmMode.equals("Silence")){
				ringAlarm(2,routeName, alertTime);
			}
		}
		
		//ring alarm
		//0=sound, 1=vibrate, 2=silence
		private void ringAlarm(int mode,String routeName, int alertTime){
			Log.i("mytag",String.valueOf(mode));
			switch (mode){
			case 0 : {
				Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
				Ringtone r = RingtoneManager.getRingtone(context, notification);
				r.play();
				
				break;
			}
			case 1 : {
				Vibrator v = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
				v.vibrate(3000);
				break;
			}
			case 2 : {
				break;
			}
			}
			Toast.makeText(context, "The bus \""+routeName+"is "+alertTime+" minutes away! Get ready!", Toast.LENGTH_LONG).show();
		}
}

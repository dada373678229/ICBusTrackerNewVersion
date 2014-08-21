package com.tetrahedrontech.bongocity.cards;


import com.tetrahedrontech.bongocity.R;
import com.tetrahedrontech.bongocity.alarmManager;

import android.app.Dialog;
import android.content.Context;
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
	
	private alarmManager am = alarmManager.getInstance(getContext());
	
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
		//Log.i("mytag","time:before"+Integer.toString(alertTime));
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
        	 //setUpAlarm();
        	 am.addAlarm(stopId, routeName, min, alertTime);
             d.dismiss();
          }    
         });
      d.show();
	}
	
	public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
    }
		
}

package com.tetrahedrontech.bongocity.cards;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.tetrahedrontech.bongocity.R;
import com.tetrahedrontech.bongocity.StopsDetailActivity;

import it.gmariotti.cardslib.library.internal.Card;

public class nearMeCard extends Card{
	//the view of stopId, stopName and stop distance
		protected TextView stopIdView;
	    protected TextView stopNameView;
	    protected TextView stopDistanceView;
	    
	    //the value of stopId, stopName and stop distance
	    private String stopId;
	    private String stopName;
	    private String stopDistance;
	    
	    private Context context;
	    
	    //constructor, use custom stop_list_card layout
		public nearMeCard(Context context) {
			super(context, R.layout.route_list_detail_card_inner_layout);
			this.context=context;
			init();
	    }
		
		//init the card, set what will it do on click
		private void init(){
	        //No Header
	        //Set a OnClickListener listener
	        setOnClickListener(new OnCardClickListener() {
				@Override
				public void onClick(Card card, View view) {
					Intent i = new Intent(context,StopsDetailActivity.class);
					i.putExtra("stopTitle", card.getId());
					context.startActivity(i);
					((Activity) getContext()).overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
					
				}
	        });
	    }
		
		//this method sets the data to display on the card
		@Override
	    public void setupInnerViewElements(ViewGroup parent, View view) {

	        //get two views
	        stopIdView = (TextView) parent.findViewById(R.id.route_list_view_detail_routeName);
	        stopNameView=(TextView) parent.findViewById(R.id.route_list_view_detail_routeDirection);
	        stopDistanceView=(TextView) parent.findViewById(R.id.route_list_view_detail_routeTime);
	        
	        //if they are not empty, assign them values
	        if (stopId!=null && stopName!=null && stopDistance != null)
	            stopIdView.setText(stopId);
	        	stopNameView.setText(stopName);
	        	stopDistanceView.setText(stopDistance);
	    }
		
		//this methods receive data from outside of the class
		public void setContent(String stopId, String stopName, String stopDistance){
			this.stopId=stopId;
			this.stopName=stopName;
			this.stopDistance=stopDistance;
		}
		
		public String getDistance(){
			return stopDistance;
		}
}

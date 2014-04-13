package com.tetrahedrontech.icbustrackernewversion.cards;

import com.tetrahedrontech.icbustrackernewversion.R;
import com.tetrahedrontech.icbustrackernewversion.StopsDetailActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;


import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;

//This class is for displaying the stop list, under the stop tab
//stopListCard has two components and it looks like this:
//  ***********************************
//  * 1051(stopId)                    *
//  * Communication Center(stopName)  *
//  ***********************************
public class stopListCard extends Card{
	//the view of stopId and stopName
	protected TextView stopIdView;
    protected TextView stopNameView;
    
    //the value of stopId and stopName
    private String stopId;
    private String stopName;
    
    //constructor, use custom stop_list_card layout
	public stopListCard(Context context) {
		super(context, R.layout.stop_list_card);
		init();
    }
	
	//init the card, set what will it do on click
	private void init(){
        //No Header
        //Set a OnClickListener listener
        setOnClickListener(new OnCardClickListener() {
			@Override
			public void onClick(Card card, View view) {
				Intent i = new Intent(getContext(),StopsDetailActivity.class);
				i.putExtra("stopTitle", card.getId());
				getContext().startActivity(i);
				((Activity) getContext()).overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
			}
        });
    }
	
	//this method sets the data to display on the card
	@Override
    public void setupInnerViewElements(ViewGroup parent, View view) {

        //get two views
        stopIdView = (TextView) parent.findViewById(R.id.stop_list_view_stopId);
        stopNameView=(TextView) parent.findViewById(R.id.stop_list_view_stopName);
        
        //if they are not empty, assign them values
        if (stopId!=null && stopName!=null)
            stopIdView.setText(stopId);
        	stopNameView.setText(stopName);
    }
	
	//this methods receive data from outside of the class
	public void setContent(String stopId, String stopName){
		this.stopId=stopId;
		this.stopName=stopName;
	}
}

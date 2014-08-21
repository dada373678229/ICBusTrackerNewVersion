package com.tetrahedrontech.bongocity.cards;

import com.tetrahedrontech.bongocity.R;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


import it.gmariotti.cardslib.library.internal.Card;

//This class is for displaying the route list with predictions on the card, 
//it is under the route detail tab, after user click a specific route in the STOP list
//routeListDetailCard has three components and it looks like this:
//  *************************************************
//  * red route(routeName)                          *
//  * clockwise(routeDirection)   10mins(routeTime) *
//  *************************************************
public class routeListDetailCard extends Card{
	//the view of three components
	protected TextView routeNameView;
    protected TextView routeDirectionView;
    protected TextView routeTimeView;
    //the value of three components
    private String routeName;
    private String routeDirection;
    private String routeTime;

    //constructor, use custom route_list_detail_card layout
	public routeListDetailCard(Context context) {
		super(context,R.layout.route_list_detail_card_inner_layout);
        init();
    }
	
	//init the card, set what will it do on click
	private void init(){
        //No Header
        //Set a OnClickListener listener
        setOnClickListener(new OnCardClickListener() {
			@Override
			public void onClick(Card card, View view) {
				//Toast.makeText(getContext(), "Route "+card.getId()+" clicked", Toast.LENGTH_SHORT).show();
			}
        });
    }
	
	//this method sets the data to display on the card
	@Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        //get views of three component
        routeNameView = (TextView) parent.findViewById(R.id.route_list_view_detail_routeName);
        routeDirectionView=(TextView) parent.findViewById(R.id.route_list_view_detail_routeDirection);
        routeTimeView = (TextView) parent.findViewById(R.id.route_list_view_detail_routeTime);

        //if the values of them are not empty, assign views values
        if (routeName!=null && routeDirection!=null && routeTime!=null)
            routeNameView.setText(routeName);
        	routeDirectionView.setText(routeDirection);
        	routeTimeView.setText(routeTime);
    }
	
	//this methods receive data from outside of the class
	public void setContent(String routeName, String routeDirection, String routeTime){
		this.routeName=routeName;
		this.routeDirection=routeDirection;
		this.routeTime=routeTime;
	}
}

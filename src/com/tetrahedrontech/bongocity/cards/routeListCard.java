package com.tetrahedrontech.bongocity.cards;


import com.tetrahedrontech.bongocity.R;
import com.tetrahedrontech.bongocity.RoutesDetailActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import it.gmariotti.cardslib.library.internal.Card;

//This class is for displaying the route list, under the route tab
//routeListCard has only one component and it looks like this:
//  ***********************************
//  * 		red route(routeName)      *
//  ***********************************
public class routeListCard extends Card{
	//the view of routeName
	protected TextView routeNameView;
    //the value of routeName
    private String routeName;
    //color border view
    private ImageView colorBorderView;
    private String color;

    //constructor, use custom route_list_card layout
	public routeListCard(Context context) {
		super(context, R.layout.route_list_card);
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
				//call routeDetailActivity by intent
				Intent i = new Intent(getContext(), RoutesDetailActivity.class);
				i.putExtra("route", card.getId());
				getContext().startActivity(i);
				((Activity) getContext()).overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
			}
        });
    }
	
	//this method sets the data to display on the card
	@Override
    public void setupInnerViewElements(ViewGroup parent, View view) {
        //get views
        routeNameView = (TextView) parent.findViewById(R.id.route_list_view_routeName);
        colorBorderView = (ImageView) parent.findViewById(R.id.route_list_color_border);

        //if the routeNmae is not empty, assign view the value
        if (routeName!=null){
            routeNameView.setText(routeName);
        }
        
        if (color.equals("red")){
        	colorBorderView.setBackgroundResource(R.drawable.rectangle_red);
        }
        else if (color.equals("blue")){
        	colorBorderView.setBackgroundResource(R.drawable.rectangle_blue);
        }
        else{
        	colorBorderView.setBackgroundResource(R.drawable.rectangle_yellow);
        }
    }
	
	//this methods receive data from outside of the class
	public void setContent(String routeName, String color){
		this.routeName=routeName;
		this.color=color;
	}
}

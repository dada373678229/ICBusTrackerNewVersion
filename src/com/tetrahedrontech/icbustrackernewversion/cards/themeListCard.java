package com.tetrahedrontech.icbustrackernewversion.cards;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.Card.OnCardClickListener;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.tetrahedrontech.icbustrackernewversion.HomeActivity;
import com.tetrahedrontech.icbustrackernewversion.R;
import com.tetrahedrontech.icbustrackernewversion.ThemeFragment;

public class themeListCard extends Card{
		//the view of themeName
		protected TextView themeView;
	    //the value of themeName
	    private String themeName;
	    
	    private Context context;

	    //constructor, use custom route_list_card layout
		public themeListCard(Context context) {
			super(context, R.layout.route_list_card);
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
					//Toast.makeText(getContext(), "Theme "+ThemeFragment.themeNames[Integer.valueOf(card.getId())]+" is set!", Toast.LENGTH_SHORT).show();
					SharedPreferences settings=context.getSharedPreferences("themeSettings",0);
					SharedPreferences.Editor editor=settings.edit();
					editor.clear();
					editor.putString("theme", card.getId());
					editor.commit();
					
					HomeActivity.theme=Integer.valueOf(card.getId());
					ColorDrawable cd=new ColorDrawable(Color.parseColor(HomeActivity.actionBarColors[Integer.valueOf(card.getId())]));
					((Activity) getContext()).getActionBar().setBackgroundDrawable(cd);
				}
	        });
	    }
		
		//this method sets the data to display on the card
		@Override
	    public void setupInnerViewElements(ViewGroup parent, View view) {
	        //get the routeName view
	        themeView = (TextView) parent.findViewById(R.id.route_list_view_routeName);

	        //if the routeNmae is not empty, assign view the value
	        if (themeName!=null)
	            themeView.setText(themeName);
	    }
		
		//this methods receive data from outside of the class
		public void setContent(String themeName){
			this.themeName=themeName;
		}
}

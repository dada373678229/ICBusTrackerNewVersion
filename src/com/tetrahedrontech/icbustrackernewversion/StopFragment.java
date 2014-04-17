package com.tetrahedrontech.icbustrackernewversion;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.tetrahedrontech.icbustrackernewversion.cards.stopListCard;
import com.tetrahedrontech.icbustrackernewversion.cards.themeListCard;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView.OnQueryTextListener;

public class StopFragment extends Fragment{
	
	//this arraylist contains all stops
		private ArrayList<stopObject> stops=new ArrayList<stopObject>();
		
		private int[] pressedCardBackground=new int[]{R.drawable.card_selector_light_blue,R.drawable.card_selector_light_purple,R.drawable.card_selector_light_green};
		
		ArrayList<Card> cards = new ArrayList<Card>();
		CardArrayAdapter mCardArrayAdapter;
		
		private int theme;

		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	  
	        View rootView = inflater.inflate(R.layout.fragment_stop, container, false);
	        
	        ActionBar actionBar = getActivity().getActionBar();
			actionBar.setTitle("Stops");
			
			SharedPreferences settings=getActivity().getSharedPreferences(themeListCard.PREFS_NAME,0);
			theme=Integer.valueOf(settings.getString("theme", "0"));
			
			//"cards" contains cards, each card is a stop
			cards =setListItem();
			
			return rootView;
		}
		
		@Override
	    public void onActivityCreated(Bundle savedInstanceState){
	    	super.onActivityCreated(savedInstanceState);
	    	mCardArrayAdapter = new CardArrayAdapter(getActivity(),cards);
			CardListView listView = (CardListView) getActivity().findViewById(R.id.stopListView);
	        if (listView!=null){
	            listView.setAdapter(mCardArrayAdapter);
	        }
	    }
		
		//this method find all stops, convert them into cards and return an arraylist of them
		private ArrayList<Card> setListItem(){
			ArrayList<Card> result=new ArrayList<Card>();
			try{
				//open file and read stops
				AssetManager am=getActivity().getAssets();
				InputStream in = am.open("allStops.txt");
				InputStreamReader isr = new InputStreamReader(in);
				BufferedReader br= new BufferedReader(isr);
				String line = br.readLine();
				String data[];
				//get a single line and analyze stop infomation
				while (line != null){
					Card temp=new stopListCard(getActivity());
					data=line.split(",");
					((stopListCard) temp).setContent(data[0],data[1]);
					String stopTitle=data[0]+","+data[1];
					temp.setId(stopTitle);
					temp.setBackgroundResourceId(pressedCardBackground[theme]);
					result.add(temp);
					stops.add(new stopObject(data[0],data[1]));
					line=br.readLine();
				}
			}
			catch (Exception e){}
			return result;
		}
}

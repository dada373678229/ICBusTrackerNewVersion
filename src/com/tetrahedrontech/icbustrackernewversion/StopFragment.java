package com.tetrahedrontech.icbustrackernewversion;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
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

		ArrayList<Card> cards = new ArrayList<Card>();
		CardArrayAdapter mCardArrayAdapter;

		@Override
	    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
	  
	        View rootView = inflater.inflate(R.layout.fragment_stop, container, false);
			
			//"cards" contains cards, each card is a stop
			cards =setListItem();
			
			return rootView;
		}
		
		@Override
	    public void onActivityCreated(Bundle savedInstanceState){
	    	super.onActivityCreated(savedInstanceState);
	    	mCardArrayAdapter = new CardArrayAdapter(getActivity(),cards);
			CardListView listView = (CardListView) getActivity().findViewById(R.id.stopListView);
			AnimationAdapter animCardArrayAdapter = new AlphaInAnimationAdapter(mCardArrayAdapter);
	        animCardArrayAdapter.setAbsListView(listView);
	        if (listView!=null){
	            listView.setExternalAdapter(animCardArrayAdapter,mCardArrayAdapter);
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
					temp.setBackgroundResourceId(HomeActivity.pressedCardBackground[HomeActivity.theme]);
					result.add(temp);
					stops.add(new stopObject(data[0],data[1]));
					line=br.readLine();
				}
			}
			catch (Exception e){}
			return result;
		}
		
		//search stops according to user's input, and returns an arraylist of cards for adapter
		private ArrayList<Card> search(String newText){
			ArrayList<Card> result=new ArrayList<Card>();
			
			//scan all the stops
			for (int i=0;i<stops.size();i++){
				String stopId=stops.get(i).getStopId();
				String stopName=stops.get(i).getStopName();
				//if stopId contains query or stopName contains query, create a card of this stop and put it into arraylist
				if(stopId.toLowerCase().contains(newText.toLowerCase()) | stopName.toLowerCase().contains(newText.toLowerCase())){
					Card temp=new stopListCard(getActivity());
					((stopListCard) temp).setContent(stopId,stopName);
					String stopTitle=stopId+","+stopName;
					temp.setId(stopTitle);
					result.add(temp);
				}
			}
			return result;
		}
		
		public void searchUpdate(String newText) {
			//update the arraylist for adapter, clear the data in the adapter, add new data and notify change
			cards=search(newText);
			mCardArrayAdapter.clear();
			mCardArrayAdapter.addAll(cards);
			mCardArrayAdapter.notifyDataSetChanged();
		}
}

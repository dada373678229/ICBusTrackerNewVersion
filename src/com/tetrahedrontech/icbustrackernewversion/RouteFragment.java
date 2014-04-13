package com.tetrahedrontech.icbustrackernewversion;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;


import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;


import com.tetrahedrontech.icbustrackernewversion.cards.routeListCard;
import com.tetrahedrontech.icbustrackernewversion.cards.routeListDetailCard;

import android.app.ActionBar;
import android.app.Fragment;
import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class RouteFragment extends Fragment{
	
	private ArrayList<Card> routeListCoralville=new ArrayList<Card>();
	private ArrayList<Card> routeListIC=new ArrayList<Card>();
	private ArrayList<Card> routeListCambus=new ArrayList<Card>();
	private ArrayList<Card> routeListAll=new ArrayList<Card>();
	//routeAgencies contains items shown in the drop down menu on action bar
	private String[] routeAgencies=new String[]{"Show All","Cambus","Iowa-City","Coralville"};
	
	public RouteFragment(){}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_route, container, false);
        
        ActionBar actionBar = getActivity().getActionBar();
		actionBar.setTitle("routes");
        
        initRouteList();
		setList(routeListAll);
          
        return rootView;
    }
    
  //this method set the route list for three different agencies and store them in arraylists
  	private void initRouteList(){
  		try{
  			//fetch data from allRoutes.txt
  			AssetManager am=getActivity().getAssets();
  			InputStream in = am.open("allRoutes.txt");
  			InputStreamReader isr = new InputStreamReader(in);
  			BufferedReader br= new BufferedReader(isr);
  			String line = br.readLine();
  			String data[];
  			//read data line by line
  			while (line != null){
  				Card temp=new routeListCard(getActivity());
  				data=line.split(",");
  				((routeListCard) temp).setContent(data[0]);
  				temp.setId(data[0]);
  				//find different agencies and put them into corresponding arraylists
  				if (data[2].equals("coralville")){
  					//temp.setBackgroundResourceId(R.drawable.card_selector_blue);
  					routeListCoralville.add(temp);
  				}
  				else if(data[2].equals("iowa-city")){
  					//temp.setBackgroundResourceId(R.drawable.card_selector_red);
  					routeListIC.add(temp);
  				}
  				else{
  					//temp.setBackgroundResourceId(R.drawable.card_selector_yellow);
  					routeListCambus.add(temp);
  				}
  				routeListAll.add(temp);
  				line=br.readLine();
  			}
  		}
  		catch (Exception e){}
  	}
  	
  //this method set the card lists using arraylists created before
  	private void setList(ArrayList<Card> routeList){
  		CardArrayAdapter mCardArrayAdapter = new CardArrayAdapter(getActivity(),routeList);
  		CardListView listView = (CardListView) getActivity().findViewById(R.id.routeListView);
        if (listView!=null){
            listView.setAdapter(mCardArrayAdapter);
        }
  	}
}

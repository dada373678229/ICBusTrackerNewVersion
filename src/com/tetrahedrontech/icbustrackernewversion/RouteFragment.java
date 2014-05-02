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
import com.tetrahedrontech.icbustrackernewversion.cards.routeListCard;
import com.tetrahedrontech.icbustrackernewversion.cards.routeListDetailCard;
import com.tetrahedrontech.icbustrackernewversion.cards.themeListCard;

import android.app.ActionBar;
import android.app.Fragment;
import android.app.ActionBar.OnNavigationListener;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

public class RouteFragment extends Fragment{
	
	private ArrayList<Card> routeListCoralville=new ArrayList<Card>();
	private ArrayList<Card> routeListIC=new ArrayList<Card>();
	private ArrayList<Card> routeListCambus=new ArrayList<Card>();
	private ArrayList<Card> routeListAll=new ArrayList<Card>();
	//routeAgencies contains items shown in the drop down menu on action bar
	private String[] routeAgencies=new String[]{"Show All","Cambus","Iowa-City","Coralville"};
	
	private int[] pressedCardBackground=new int[]{R.drawable.card_selector_light_blue,R.drawable.card_selector_light_purple,R.drawable.card_selector_light_green};
	
	private int theme;
	
	public RouteFragment(){}
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_route, container, false);
		
		SharedPreferences settings=getActivity().getSharedPreferences(themeListCard.PREFS_NAME,0);
		theme=Integer.valueOf(settings.getString("theme", "0"));
		
		//setActionBar();
		initRouteList();
        return rootView;
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState){
    	super.onActivityCreated(savedInstanceState);
		
    	setList(routeListAll);
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
  				temp.setId(data[1]+","+data[2]+","+data[0]);
  				temp.setBackgroundResourceId(pressedCardBackground[theme]);
  				
  				//find different agencies and put them into corresponding arraylists
  				if (data[2].equals("coralville")){
  					routeListCoralville.add(temp);
  				}
  				else if(data[2].equals("iowa-city")){
  					routeListIC.add(temp);
  				}
  				else{
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
  		AnimationAdapter animCardArrayAdapter = new AlphaInAnimationAdapter(mCardArrayAdapter);
        animCardArrayAdapter.setAbsListView(listView);
        if (listView!=null){
            listView.setExternalAdapter(animCardArrayAdapter,mCardArrayAdapter);
        }
  	}
  	
  //this method set the drop down navigation list on action bar
  	private void setActionBar(){
  		ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_spinner_dropdown_item, routeAgencies);
  		getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
  		ActionBar.OnNavigationListener navigationListener = new OnNavigationListener() {
              @Override
              public boolean onNavigationItemSelected(int itemPosition, long itemId) {
              	if (routeAgencies[itemPosition]=="Show All"){
              		setList(routeListAll);
              	}
              	else if (routeAgencies[itemPosition]=="Iowa-City"){
              		setList(routeListIC);
              	}
              	else if (routeAgencies[itemPosition]=="Coralville"){
              		setList(routeListCoralville);
              	}
              	else{
              		setList(routeListCambus);
              	}
                  
                  return false;
              }
          };
          getActivity().getActionBar().setListNavigationCallbacks(adapter, navigationListener);
  	}
}

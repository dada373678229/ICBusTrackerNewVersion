package com.tetrahedrontech.icbustrackernewversion;

import it.gmariotti.cardslib.library.internal.Card;
import it.gmariotti.cardslib.library.internal.CardArrayAdapter;
import it.gmariotti.cardslib.library.view.CardListView;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.tetrahedrontech.icbustrackernewversion.cards.nearMeCard;
import com.tetrahedrontech.icbustrackernewversion.cards.nearMeCardComparator;
import com.tetrahedrontech.icbustrackernewversion.cards.routeListDetailCard;
import com.tetrahedrontech.icbustrackernewversion.cards.stopListCard;
import com.tetrahedrontech.icbustrackernewversion.cards.themeListCard;

import android.app.Activity;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class NearMeActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener{
	private GoogleMap map;
	private int theme;
	private String[] actionBarColors=new String[]{"#99CCFF","#FFBFFF","#99FFCC"};
	
	private LocationClient mLocationClient;
	private Location currentLocation;
	
	private int[] pressedCardBackground=new int[]{R.drawable.card_selector_light_blue,R.drawable.card_selector_light_purple,R.drawable.card_selector_light_green};
	
	ArrayList<Card> NearStops = new ArrayList<Card>();
	CardArrayAdapter mCardArrayAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_near_me);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
		
		//set action bar background color
		SharedPreferences settings=getSharedPreferences(themeListCard.PREFS_NAME,0);
		theme=Integer.valueOf(settings.getString("theme", "0"));
		ColorDrawable cd=new ColorDrawable(Color.parseColor(actionBarColors[theme]));
		getActionBar().setBackgroundDrawable(cd);
		getActionBar().setTitle("Near me");
		
		initMap();
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		mLocationClient.connect();
		//mLocationClient = new LocationClient(this, this, this);
        //mLocationClient.connect();
		
	}
	
	@Override
	protected void onStop(){
		super.onStop();
		mLocationClient.disconnect();
	}
	
	private void initMap(){
		if (map == null) {
            //link map with fragment1
            map =((MapFragment) getFragmentManager().findFragmentById(R.id.near_me_map)).getMap();
            if (map==null){
            	setContentView(R.layout.error_layout);
            	TextView t = (TextView) this.findViewById(R.id.stop_detail_textView);
            	t.setText("Sorry, Google Play service is not available on your phone!");
            	return;
            }
        }
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        
        mLocationClient = new LocationClient(this, this, this);
        mLocationClient.connect();
        
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		currentLocation=mLocationClient.getLastLocation();
		LatLng latlng=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15);
		map.animateCamera(cameraUpdate);
		NearStops=setCards();
		
		mCardArrayAdapter = new CardArrayAdapter(this,NearStops);
		CardListView listView = (CardListView) findViewById(R.id.nearMeListView);
		//AnimationAdapter animCardArrayAdapter = new AlphaInAnimationAdapter(mCardArrayAdapter);
        //animCardArrayAdapter.setAbsListView(listView);
        if (listView!=null){
            listView.setAdapter(mCardArrayAdapter);
        }
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLocationChanged(Location arg0) {
		//currentLocation=arg0;
		//LatLng latlng=new LatLng(arg0.getLatitude(),arg0.getLongitude());
		//CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15);
		//map.animateCamera(cameraUpdate);
	}
	
	private ArrayList<Card> setCards(){
		ArrayList<Card> result = new ArrayList<Card>();
		try{
			//open file and read stops
			AssetManager am=getAssets();
			InputStream in = am.open("allStops.txt");
			InputStreamReader isr = new InputStreamReader(in);
			BufferedReader br= new BufferedReader(isr);
			String line = br.readLine();
			String data[];
			
			Location stopLoc=new Location("compare");
			int distance;
			
			//get a single line and analyze stop infomation
			while (line != null){
				nearMeCard temp=new nearMeCard(this);
				data=line.split(",");
				
				stopLoc.setLatitude(Double.valueOf(data[2]));
				stopLoc.setLongitude(Double.valueOf(data[3]));
				distance=(int) (currentLocation.distanceTo(stopLoc)*3.28084);
				
				if (distance<=1700){
					String stopTitle=data[0]+","+data[1];
					temp.setId(stopTitle);
					temp.setContent(data[0],data[1],String.valueOf(distance)+" ft");
					temp.setBackgroundResourceId(pressedCardBackground[theme]);
					result.add(temp);
				}
				line=br.readLine();
			}
		}
		catch (Exception e){}
		//sort the result using custom comparator
		Collections.sort(result, new nearMeCardComparator());
		return result;
	}

	
	
	
}

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
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.nhaarman.listviewanimations.swinginadapters.AnimationAdapter;
import com.nhaarman.listviewanimations.swinginadapters.prepared.AlphaInAnimationAdapter;
import com.tetrahedrontech.icbustrackernewversion.cards.nearMeCard;
import com.tetrahedrontech.icbustrackernewversion.cards.nearMeCardComparator;
import com.tetrahedrontech.icbustrackernewversion.cards.routeListDetailCard;
import com.tetrahedrontech.icbustrackernewversion.cards.stopListCard;
import com.tetrahedrontech.icbustrackernewversion.cards.themeListCard;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

public class NearMeActivity extends Activity{// implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, LocationListener{
	private GoogleMap map;
	private int theme;
	private String[] actionBarColors=new String[]{"#99CCFF","#FFBFFF","#99FFCC"};
	
	private Location currentLocation;
	
	private int[] pressedCardBackground=new int[]{R.drawable.card_selector_light_blue,R.drawable.card_selector_light_purple,R.drawable.card_selector_light_green};
	
	ArrayList<Card> nearStops = new ArrayList<Card>();
	CardArrayAdapter mCardArrayAdapter;
	
	ArrayList<Marker> markersOnMap = new ArrayList<Marker>();
	
	//error code: -1=normal, 0=failed to find a map, 1=Location service not available
	private int errorCode=-1;
	
	private Context context;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_near_me);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
		context=this;
		
		//set action bar background color
		SharedPreferences settings=getSharedPreferences(themeListCard.PREFS_NAME,0);
		theme=Integer.valueOf(settings.getString("theme", "0"));
		ColorDrawable cd=new ColorDrawable(Color.parseColor(actionBarColors[theme]));
		getActionBar().setBackgroundDrawable(cd);
		getActionBar().setTitle("Near me");
		
		Log.i("mytag", String.valueOf(servicesConnected()));

		initMap();

		LocationManager locationManager = (LocationManager) this.getSystemService(this.LOCATION_SERVICE);
		if (locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER) || locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
			LocationListener locationListener = new LocationListener() {
			    public void onLocationChanged(Location location) {
			    	currentLocation=location;
			    	setUpThings();
			    }

			    public void onStatusChanged(String provider, int status, Bundle extras) {}

			    public void onProviderEnabled(String provider) {}

			    public void onProviderDisabled(String provider) {}
			  };
			locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 15000, 20, locationListener);
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 10, locationListener);
		}
		else{
			errorCode=1;
			errorHandler();
		}
		
		//set up listener to response to click on info window
        //here, we need to go to the stop detail page that user clicked
        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
            	//if the marker clicked is not a bus marker
            	if (!marker.getTitle().equals("BUS")){
            		Intent i = new Intent(context,StopsDetailActivity.class);
            		i.putExtra("stopTitle", marker.getTitle()+","+marker.getSnippet());
            		context.startActivity(i);
            		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
            	}       
            }
        });
	}
	
	//// Check that Google Play services is available
	private boolean servicesConnected() {
        // Check that Google Play services is available
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        // If Google Play services is available
        if (ConnectionResult.SUCCESS == resultCode) {
        	return true;
        }
        else{
        	return false;
        }
	}
	
	private void initMap(){
		if (map == null) {
            //link map with fragment
            map =((MapFragment) getFragmentManager().findFragmentById(R.id.near_me_map)).getMap();
            //if still didn't get map, call errorHandler()
            if (map==null){
            	errorCode=0;
            	errorHandler();
            	return;
            }
        }
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        map.setMyLocationEnabled(true);
        
	}
	
	private ArrayList<Card> setCardsAndMarkers(){
		ArrayList<Card> result = new ArrayList<Card>();
		
		//remove current markers
		for (int i=0; i<markersOnMap.size();i++){
			markersOnMap.get(i).remove();
		}
		markersOnMap.clear();
		
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
					//add card to the arraylist
					String stopTitle=data[0]+","+data[1];
					temp.setId(stopTitle);
					temp.setContent(data[0],data[1],String.valueOf(distance)+" ft");
					temp.setBackgroundResourceId(pressedCardBackground[theme]);
					result.add(temp);
					
					//add markers on the map
					markersOnMap.add(map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(data[2]), Double.parseDouble(data[3]))).title(data[0]).snippet(data[1]).icon(BitmapDescriptorFactory.defaultMarker(200)).alpha(0.7f)));
				}
				line=br.readLine();
			}
		}
		catch (Exception e){}
		//sort the result using custom comparator
		Collections.sort(result, new nearMeCardComparator());
		return result;
	}
	
	//set up camera position and card list view
	private void setUpThings(){
		LatLng latlng=new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude());
		CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latlng, 15);
		map.animateCamera(cameraUpdate);
		nearStops=setCardsAndMarkers();
		mCardArrayAdapter = new CardArrayAdapter(this,nearStops);
		CardListView listView = (CardListView) findViewById(R.id.nearMeListView);
        if (listView!=null){
            listView.setAdapter(mCardArrayAdapter);
        }
	}

	//display error msg according to errorCode
	private void errorHandler(){
		TextView t;
		switch (errorCode){
		case -1:
			return;
		case 0:
			setContentView(R.layout.error_layout);
	    	t = (TextView) this.findViewById(R.id.stop_detail_textView);
	    	t.setText("Sorry, Google Map service is not available on your phone!");
	    	break;
		case 1:
			setContentView(R.layout.error_layout);
	    	t = (TextView) this.findViewById(R.id.stop_detail_textView);
	    	t.setText("Location Services is not available!\nPlease check your location settings!");
	    	break;
	    default:
	    	break;
		}
	}
	
	//animation when back button is pressed
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}
}

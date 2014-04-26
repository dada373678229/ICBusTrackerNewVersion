package com.tetrahedrontech.icbustrackernewversion;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.tetrahedrontech.icbustrackernewversion.cards.themeListCard;

import android.app.Activity;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.location.Location;
import android.os.Bundle;
import android.widget.TextView;

public class NearMeActivity extends Activity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener{
	private GoogleMap map;
	private int theme;
	private String[] actionBarColors=new String[]{"#99CCFF","#FFBFFF","#99FFCC"};
	
	private LocationClient mLocationClient;
	private Location currentLocation;
	
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
		
		initMap();
		
	}
	
	@Override
	protected void onStart(){
		super.onStart();
		mLocationClient.connect();
		
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
		map.addMarker(new MarkerOptions().position(new LatLng(currentLocation.getLatitude(),currentLocation.getLongitude())));
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
}

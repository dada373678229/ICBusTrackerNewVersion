package com.tetrahedrontech.icbustrackernewversion;

import java.io.BufferedReader;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnCameraChangeListener;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tetrahedrontech.icbustrackernewversion.API.coreAPI;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.widget.TextView;
import android.os.Build;

public class RoutesDetailActivity extends Activity {

	//camera boundary
	private LatLngBounds mapBound;
	private GoogleMap map;
	
	private String routeName;
	private String routeAgency;
	private ArrayList<Marker> busLocationMarkers=new ArrayList<Marker>();
	
	//******************************************
	coreAPI api=new coreAPI();
	private Context context;
	BusLocationMarkerThread getBusLocation;
	
			//this class is to show bus marker on the map, continuously
			private class BusLocationMarkerThread extends AsyncTask<String, String, String> {
				
				//this method fetches bus location data and update it every second
				//Only Main Thread can update UI, so we have to put UI-changing-method in onProgressUpdate.
				@Override
		        protected String doInBackground(String... params) {
					while (true){
						if (isCancelled()) {
							break;}
						
						String line=api.busLocations(params[0], params[1]);
						if (line.length() != 0){
							publishProgress(new String[]{line});
						}
					
						try {
							Thread.currentThread().sleep(5000);
						} catch (InterruptedException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					}
					return "";
				}
				
				//this method is called after publishProgress is called, the parameter is the same as the
				//the parameter of publishProgress.
				//this method creates bus markers on the map
				@Override
				protected void onProgressUpdate(String... result){
					//before adding new bus location markers, remove old markers and clear the arraylist
					for (int i=0;i<busLocationMarkers.size();i++){
						busLocationMarkers.get(i).remove();
					}
					busLocationMarkers.clear();
					
					//to keep track of all bus location markers, store it in an arraylist
					String[] temp=result[0].split(";");
					for (int i=0; i<temp.length;i++){
						String[] temp1=temp[i].split(",");
						LatLng busLocation=new LatLng(Float.parseFloat(temp1[1]),Float.parseFloat(temp1[2]));
						busLocationMarkers.add(map.addMarker(new MarkerOptions().anchor((float)0.5, (float)0.5).flat(true).title("BUS").snippet(temp1[0]).position(busLocation).icon(BitmapDescriptorFactory.fromAsset("busIcon.png")).rotation(Integer.parseInt(temp1[3]))));
					}
				}
				/*
				@Override
		        protected void onPostExecute(String result) {
					//Log.i("mytag","onPostExecute");
		        }*/
			}
	
	//*******************************************
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routes_detail);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
		
		//get the route name and route agency from intent
		routeName=((String) getIntent().getExtras().get("route")).split(",")[0];
		routeAgency=((String) getIntent().getExtras().get("route")).split(",")[1];
		
		initMap(routeName);
		context=this;
		 
	}
	
	@Override
	protected void onResume(){
		super.onResume();
		
		//start tracking bus locations only when there is a map
		if (map != null){
			getBusLocation=new BusLocationMarkerThread();
			if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
				getBusLocation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{routeAgency,routeName});
			else
				getBusLocation.execute(new String[]{routeAgency,routeName});
		}
	}
	
	//cancel bus location track when pause/stop/destroy
	@Override
    protected void onDestroy() {
        super.onDestroy();
        if (map != null && (!getBusLocation.isCancelled())){
        	getBusLocation.cancel(true);
        }
        
    }
	
	@Override
    protected void onPause() {
        super.onPause();
        if (map != null && (!getBusLocation.isCancelled())){
        	getBusLocation.cancel(true);
        }
    }
	
	@Override
	protected void onStop(){
		super.onStop();
		if (map != null && (!getBusLocation.isCancelled())){
        	getBusLocation.cancel(true);
        }
	}
	
	//animation when back button is pressed
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}
	
	//this method initiates the map fragment
	public void initMap(String route){
		try{
	        if (map == null) {
	            //link map with fragment1
	            map =((MapFragment) getFragmentManager().findFragmentById(R.id.routeDetailMapFragment)).getMap();
	            //map=null, show error msg
	            if (map==null){
	            	setContentView(R.layout.error_layout);
	            	TextView t = (TextView) this.findViewById(R.id.stop_detail_textView);
	            	t.setText("Sorry, Google Play service is not available on your phone!");
	            	return;
	            }
	        }
	        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	        
	        //read stop information and add stop markers
	        AssetManager am = this.getAssets();
	        InputStream is = am.open("routeInfo/stops/"+route+".txt");
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String line = br.readLine();
	        
	        //rawData contains stopId, stopTitle, stopLat, stopLng
	        String[] rawData;
	        while (line != null){
	            rawData=line.split(",");
	            //add marker
	            map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(rawData[2]), Double.parseDouble(rawData[3]))).title(rawData[0]).snippet(rawData[1]).icon(BitmapDescriptorFactory.defaultMarker(200)).alpha(0.7f));
	            line=br.readLine();
	        }
	        is.close();
	        isr.close();
	        br.close();
	        
	        //open route path file
	        is = am.open("routeInfo/path/"+route+".txt");
	        isr=new InputStreamReader(is);
	        br=new BufferedReader(isr);
	        line = br.readLine();
	        
	        //get map boundary, (the first line of the txt file)
	        String[] mapBoundTemp=line.split(",");
	        mapBound = new LatLngBounds(new LatLng(Double.valueOf(mapBoundTemp[0]), Double.valueOf(mapBoundTemp[1])), new LatLng(Double.valueOf(mapBoundTemp[2]), Double.valueOf(mapBoundTemp[3])));
	        line=br.readLine();
	        
	        //add path points to an arraylist, which will be passed to addMarkers(markers)
	        ArrayList<LatLng> markers = new ArrayList<LatLng>();
	        while (line != null){
	        	//blocks are separated by ";"
	        	//when we reach a ";", draw current line segment, and clear current arraylist
	        	if (line.equals(";")){
	    	        addMarkers(markers);
	    	        markers.clear();
	        	}
	        	//if it is in the same block, keep adding point into arraylist
	        	else{
	        		rawData=line.split(",");
	            	markers.add(new LatLng(Double.parseDouble(rawData[0]),Double.parseDouble(rawData[1])));
	        	}
            	
	            line=br.readLine();
	        }
	        addMarkers(markers);
	        is.close();            
	        isr.close();
	        br.close();
	        
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
	            
	        //set camera position using mapBounds with padding 40
	        map.setOnCameraChangeListener(new OnCameraChangeListener() {
	            @Override
	            public void onCameraChange(CameraPosition arg0) {
	                // Move camera.
	            	map.moveCamera(CameraUpdateFactory.newLatLngBounds(mapBound, 40));
	                // Remove listener to prevent position reset on camera move.
	                map.setOnCameraChangeListener(null);
	            }
	        });
	        
	        map.setMyLocationEnabled(true);
		}
		catch (Exception e){
			Log.i("mytag","shouldn't be here");
		}
	}
	
	//drawer line segment using given points stored in the arraylist
	private void addMarkers(ArrayList<LatLng> markers){
		PolylineOptions routeOptions = new PolylineOptions();
		for (int i=0; i<markers.size(); i++){
			routeOptions.add(markers.get(i));
		}
		routeOptions.color(Color.CYAN);
        map.addPolyline(routeOptions);
	}
}

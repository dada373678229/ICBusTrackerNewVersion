package com.tetrahedrontech.icbustrackernewversion;

import it.gmariotti.cardslib.library.internal.CardArrayAdapter;

import it.gmariotti.cardslib.library.view.CardListView;

import java.io.BufferedReader;



import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnInfoWindowClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.tetrahedrontech.icbustrackernewversion.API.coreAPI;
//import com.tetrahedron.ICBusTracker.R;

import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Toast;
import android.os.Build;

public class RoutesDetailActivity extends Activity {

	//camera bound
	private final LatLngBounds redBound = new LatLngBounds(new LatLng(41.6569098, -91.5541481), new LatLng(41.6768599, -91.53203));
	private GoogleMap map;
	
	//******************************************
	coreAPI api=new coreAPI();
	Marker busLocationMarker;
	private Context context;
	final BusLocationMarkerThread getBusLocation=new BusLocationMarkerThread();
	
			//this class is to show bus marker on the map, continuously
			private class BusLocationMarkerThread extends AsyncTask<String, String, String> {
				
				//this method fetches bus location data and update it every second
				//Only Main Thread can update UI, so we have to put UI-changing-method in onProgressUpdate.
				@Override
		        protected String doInBackground(String... params) {
					while (true){
					String line=api.busLocations(params[0], params[1]);
					if (line.length() != 0){
						publishProgress(new String[]{line});
					}
					
					try {
						Thread.currentThread().sleep(1000);
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}}
				}
				
				//this method is called after publishProgress is called, the parameter is the same as the
				//the parameter of publishProgress.
				//this method creates bus markers on the map
				@Override
				protected void onProgressUpdate(String... result){
					String[] temp=result[0].split(";");
					for (int i=0; i<temp.length;i++){
						String[] temp1=temp[i].split(",");
						LatLng busLocation=new LatLng(Float.parseFloat(temp1[1]),Float.parseFloat(temp1[2]));
						map.addMarker(new MarkerOptions().anchor((float)0.5, (float)0.5).flat(true).title("BUS "+temp1[0]).position(busLocation).icon(BitmapDescriptorFactory.fromAsset("busIcon.png")).rotation(Integer.parseInt(temp1[3])));
					}
				}
				
				@Override
		        protected void onPostExecute(String result) {
					//Log.i("mytag","onPostExecute");
		        }
			}
	
	//*******************************************
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_routes_detail);
		overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
		
		String route=(String) getIntent().getExtras().get("route");
		//Toast.makeText(this, route, Toast.LENGTH_SHORT).show();
		initMap("red");
		context=this;
		
		//start tracking bus locations
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB)
		    getBusLocation.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR, new String[]{"uiowa","red"});
		else
		    getBusLocation.execute(new String[]{"uiowa","red"});
		
		 
	}
	
	@Override
    protected void onDestroy() {
        super.onDestroy();
    }
	
	@Override
    protected void onPause() {
		getBusLocation.cancel(true);
        super.onPause();
    }
	
	//animation when back button is pressed
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slide_in_from_left, R.anim.slide_out_to_right);
	}
	
	//this method initiates the map fragment
	public void initMap(String route){
		String file;
		try{
	        if (map == null) {
	            //link map with fragment1
	            map =((MapFragment) getFragmentManager().findFragmentById(R.id.routeDetailMapFragment)).getMap();
	        }
	        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	        
	        //get time to determine which map we are going to use
	        Calendar c = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	        String strTime = sdf.format(c.getTime());
	        String[] time=strTime.split(":");
	        
	        if ((Integer.parseInt(time[0])==19 && Integer.parseInt(time[1])>=25) || (Integer.parseInt(time[0])>19)){
	        	file=route+"Night.txt";
	        }
	        else{
	        	file=route+"Day.txt";
	        }
	        
	        //read stop information
	        AssetManager am = this.getAssets();
	        InputStream is = am.open("routeInfo/stops/"+file);
	        InputStreamReader isr = new InputStreamReader(is);
	        BufferedReader br = new BufferedReader(isr);
	        String line = br.readLine();
	        //rawData contains stopId, stopTitle, stopLat, stopLng
	        String[] rawData;
	        while (line != null){
	            rawData=line.split(",");
	            String markerStopId=rawData[0];
	            //add marker
	            map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(rawData[2]), Double.parseDouble(rawData[3]))).title(rawData[0]).snippet(rawData[1]).icon(BitmapDescriptorFactory.defaultMarker(200)).alpha(0.7f));
	            line=br.readLine();
	        }
	        is.close();
	        isr.close();
	        br.close();
	        
	        
	        //draw polyline for route path
	        is = am.open("routeInfo/path/"+file);
	        isr=new InputStreamReader(is);
	        br=new BufferedReader(isr);
	        line = br.readLine();
	        PolylineOptions routeOptions = new PolylineOptions();
	        while (line != null){
            	rawData=line.split(",");
            	routeOptions.add(new LatLng(Double.parseDouble(rawData[0]),Double.parseDouble(rawData[1])));
	            line=br.readLine();
	        }
	        routeOptions.color(Color.CYAN);
	        Polyline polyLine=map.addPolyline(routeOptions);
	        is.close();            
	        isr.close();
	        br.close();
	        
	        //set up listener to response to click on info window
	        //here, we need to go to the stop detail page that user clicked
	        map.setOnInfoWindowClickListener(new OnInfoWindowClickListener() {
	            @Override
	            public void onInfoWindowClick(Marker marker) {
	            	
	               Intent i = new Intent(context,StopsDetailActivity.class);
	               i.putExtra("stopTitle", marker.getTitle()+","+marker.getSnippet());
	               context.startActivity(i);
	               overridePendingTransition(R.anim.slide_in_from_right, R.anim.slide_out_to_left);
	               
	            }
	        });
	       
	        //for test uses
	        //map.addMarker(new MarkerOptions().position(center).icon(BitmapDescriptorFactory.fromAsset("busIcon.png")).flat(true).rotation(88));
	        /*
	        is=am.open("MoreredRoutePoints1.txt");
	        isr=new InputStreamReader(is);
	        br=new BufferedReader(isr);
	        line=br.readLine();
	        while(line!=null){
	          	rawData=line.split(",");
	           	map.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(rawData[1]), Double.parseDouble(rawData[2]))).title(rawData[0]).icon(BitmapDescriptorFactory.fromAsset("reddot.png")).alpha(0.7f));
	           	line=br.readLine();           
	        }*/
	            
	        //set camera position
	        //CameraPosition cameraPosition = new CameraPosition.Builder().target(center).zoom(13).build();
	        //map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
	        //map.animateCamera(CameraUpdateFactory.newLatLngZoom(redBound.getCenter(),13));
	        map.moveCamera(CameraUpdateFactory.newLatLngZoom(redBound.getCenter(), 14));
	        map.setMyLocationEnabled(true);
	        //map.setTrafficEnabled(true);
		}
		catch (Exception e){}
	}
}

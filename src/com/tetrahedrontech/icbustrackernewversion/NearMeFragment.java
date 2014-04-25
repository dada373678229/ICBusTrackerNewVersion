package com.tetrahedrontech.icbustrackernewversion;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.SupportMapFragment;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class NearMeFragment extends Fragment {
	private MapFragment mapFragment;
	private GoogleMapOptions mapOptions;
	private GoogleMap map;
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.fragment_near_me, container, false);
		
		return rootView;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		if (map == null) {
            //link map with fragment1
            map =((MapFragment) getFragmentManager().findFragmentById(R.id.near_me_map)).getMap();
        }
        map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
	}
}

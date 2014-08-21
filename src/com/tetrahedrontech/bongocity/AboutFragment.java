package com.tetrahedrontech.bongocity;

import android.app.Fragment;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class AboutFragment extends Fragment{

	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
  
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);
		
        return rootView;
    }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		TextView t = (TextView) getActivity().findViewById(R.id.about_us_text);
		t.setMovementMethod(new ScrollingMovementMethod());
	}
}

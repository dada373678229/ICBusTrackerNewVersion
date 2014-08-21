package com.tetrahedrontech.bongocity;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;

public class SettingsFragment extends PreferenceFragment implements OnSharedPreferenceChangeListener{
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.fragment_settings);
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		if (key.equals("auto_refresh")) {
            SwitchPreference connectionPref = (SwitchPreference) findPreference(key);
            if (sharedPreferences.getBoolean("alarm", false)){
            	connectionPref.setChecked(false);
            }
        }
		if (key.equals("alarm")){
			SwitchPreference connectionPref = (SwitchPreference) findPreference(key);
			if (sharedPreferences.getBoolean("auto_refresh", true)){
            	connectionPref.setChecked(false);
            }
		}
	}
	
	@Override
	public void onResume() {
	    super.onResume();
	    getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
	}
	
	@Override
	public void onPause() {
	    super.onPause();
	    getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
	}
}

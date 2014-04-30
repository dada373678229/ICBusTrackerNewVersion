package com.tetrahedrontech.icbustrackernewversion;

import com.tetrahedrontech.icbustrackernewversion.cards.themeListCard;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.SwitchPreference;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;

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
            if (getPreferenceManager().getSharedPreferences().getBoolean("alarm", false)){
            	connectionPref.setChecked(false);
            }
        }
		if (key.equals("alarm")){
			SwitchPreference connectionPref = (SwitchPreference) findPreference(key);
			if (getPreferenceManager().getSharedPreferences().getBoolean("auto_refresh", true)){
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

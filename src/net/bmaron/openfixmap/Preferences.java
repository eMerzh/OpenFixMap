package net.bmaron.openfixmap;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity 
	implements OnSharedPreferenceChangeListener {

	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.main_preferences_menu);

		togglePreferences();
		PreferenceManager.getDefaultSharedPreferences(getApplication()).registerOnSharedPreferenceChangeListener(this);
		PreferenceManager.setDefaultValues(this, R.layout.main_preferences_menu, false);
	}
	
	protected void togglePreferences()
	{
		String [] values = MultiSelectListPreference.parseStoredValue(
				PreferenceManager.getDefaultSharedPreferences(getApplication()).getString("checkers", ""));
		
		List<String> list;
		if(values == null)
			list = new ArrayList<String>();
		else
			list = new ArrayList<String>(Arrays.asList(values));
		
		Preference somePreference;
// @TODO: Re-enable MapDust parser
//		somePreference = findPreference("pl_errors_mapdust");
//		if(list.contains("MapDust")) {
//			somePreference.setEnabled(true);
//		} else {
//			somePreference.setEnabled(false);
//		}
		
		somePreference = findPreference("pl_errors_keepright");
		if(list.contains("KeepRight")) {
			somePreference.setEnabled(true);
		} else {
			somePreference.setEnabled(false);
		}
		somePreference = findPreference("pl_errors_osmose");
		if(list.contains("Osmose")) {
			somePreference.setEnabled(true);
		} else {
			somePreference.setEnabled(false);
		}
	}

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
			String key) {	
		if(key.equals("checkers")) {		
			togglePreferences();
		}
	}
}

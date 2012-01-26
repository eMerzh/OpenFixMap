package net.bmaron.openfixmap;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {

	 protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.main_preferences_menu);
		/*
		PlatformManager plManager = PlatformManager.getInstance();
        PreferenceCategory targetCategory = (PreferenceCategory)findPreference("platforms_pref");*/
	}
}

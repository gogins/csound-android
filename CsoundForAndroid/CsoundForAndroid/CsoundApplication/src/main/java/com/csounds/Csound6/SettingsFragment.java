package com.csounds.Csound6;

import android.os.Bundle;

import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {
    public static final String KEY_AUDIO_DRIVER_PREFERENCE = "audioDriver";
    public ListPreference audioDriverPreference;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
         // Indicate here the XML resource you created above that holds the preferences
        setPreferencesFromResource(R.xml.settings, rootKey);
        audioDriverPreference = (ListPreference)  getPreferenceManager().findPreference("audioDriver");
        audioDriverPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                return true;
                // your code here
            }
        });
    }
}

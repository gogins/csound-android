package com.csounds.Csound6;

import android.app.FragmentTransaction;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.preference.ListPreference;
import android.preference.PreferenceActivity;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends androidx.appcompat.app.AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layut);

        // our settings activity must come here

        Fragment fragment = new SettingsHolder();
        // this fragment must be from android.app.Fragment,
        // if you use support fragment, it will not work

        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            // when saved instance state is null, that means
            // activity is created for the first time, so basically
            // add the fragment to activity if and only if activity is new
            // when activity rotates, do nothing

            transaction.add(R.id.settings_holder, fragment, "settings_screen");
        }
        transaction.commit();
    }

    // below inner class is a fragment, which must be called in the main activity
    public static class SettingsHolder extends PreferenceFragmentCompat {
        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            // here we should call settings ui
            addPreferencesFromResource(R.xml.settings);

        }

        @Override
        public void onCreatePreferences(@Nullable Bundle savedInstanceState, @Nullable String rootKey) {
            super.onCreate(savedInstanceState);
            // here we should call settings ui
            addPreferencesFromResource(R.xml.settings);

        }
    }
}



class SettingsActivityx extends PreferenceActivity implements SharedPreferences.OnSharedPreferenceChangeListener{
    public static final String KEY_AUDIO_DRIVER_PREFERENCE = "audioDriver";
    public ListPreference audioDriverPreference;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.settings);
        //audioDriverPreference = (ListPreference)findPreference(KEY_AUDIO_DRIVER_PREFERENCE);

    }
    @Override
    protected void onResume() {
        super.onResume();
        // Setup the initial values
        // Set up a listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Unregister the listener whenever a key changes
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(KEY_AUDIO_DRIVER_PREFERENCE)) {
            audioDriverPreference.setSummary("Selected driver is " + audioDriverPreference.getEntry().toString());
        }
    }
}

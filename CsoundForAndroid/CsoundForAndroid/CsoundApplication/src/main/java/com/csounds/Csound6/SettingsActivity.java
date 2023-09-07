package com.csounds.Csound6;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsActivity extends androidx.appcompat.app.AppCompatActivity {
    @SuppressLint("ResourceType")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (savedInstanceState == null) {
            SettingsFragment settingsFragment = new SettingsFragment();
            transaction.replace(android.R.id.content, settingsFragment);
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
            ///super.onCreate(savedInstanceState);
            // here we should call settings ui
            addPreferencesFromResource(R.xml.settings);

        }
    }
}

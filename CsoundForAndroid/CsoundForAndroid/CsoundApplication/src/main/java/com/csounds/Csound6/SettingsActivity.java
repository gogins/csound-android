package com.csounds.Csound6;

import static com.csounds.Csound6.CsoundAppActivity.REQUEST_CODE_PICK_HTML_DIR;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;
import androidx.preference.Preference;
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
/*
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
            Preference pickHtmlDirectoryPref = findPreference("pick_html_directory");
            if (pickHtmlDirectoryPref != null) {
                pickHtmlDirectoryPref.setOnPreferenceClickListener(preference -> {
                    Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
                    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                    startActivityForResult(intent, REQUEST_CODE_PICK_HTML_DIR);
                    return true;
                });
            }
        }
    }
 */
}

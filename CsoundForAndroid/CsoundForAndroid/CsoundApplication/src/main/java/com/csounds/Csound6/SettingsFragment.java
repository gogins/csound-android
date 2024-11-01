package com.csounds.Csound6;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsFragment extends PreferenceFragmentCompat {
    private ActivityResultLauncher<Intent> directoryPickerLauncher;
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);
        directoryPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri directoryUri = result.getData().getData();
                    if (directoryUri != null) {
                        requireContext().getContentResolver().takePersistableUriPermission(
                            directoryUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(requireContext());
                        prefs.edit().putString("html_directory_uri", directoryUri.toString()).apply();
                    }
                }
            }
        );
        Preference pickHtmlDirectoryPref = findPreference("pick_html_directory");
        Log.d("SettingsFragment", "Fragment context: " + requireContext());
        Log.d("SettingsFragment", "Activity context: " + getActivity());
        if (pickHtmlDirectoryPref != null) {
            pickHtmlDirectoryPref.setOnPreferenceClickListener(preference -> {
                openDirectoryPicker();
                return true;
            });
        }
    }
    private void openDirectoryPicker() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
        directoryPickerLauncher.launch(intent);
    }
}

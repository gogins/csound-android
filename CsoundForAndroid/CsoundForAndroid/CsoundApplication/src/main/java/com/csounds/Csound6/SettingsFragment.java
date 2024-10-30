package com.csounds.Csound6;

import static com.csounds.Csound6.CsoundAppActivity.REQUEST_CODE_PICK_HTML_DIR;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.preference.ListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    private ActivityResultLauncher<Intent> directoryPickerLauncher;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.settings, rootKey);

        // Initialize the ActivityResultLauncher
        directoryPickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri directoryUri = result.getData().getData();
                    if (directoryUri != null) {
                        requireContext().getContentResolver().takePersistableUriPermission(
                            directoryUri, Intent.FLAG_GRANT_READ_URI_PERMISSION | Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                        SharedPreferences prefs = requireContext().getSharedPreferences("AppPreferences", Context.MODE_PRIVATE);
                        prefs.edit().putString("html_directory_uri", directoryUri.toString()).apply();
                    }
                }
            }
        );

        // Set up the preference click listener
        Preference pickHtmlDirectoryPref = findPreference("pick_html_directory");
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

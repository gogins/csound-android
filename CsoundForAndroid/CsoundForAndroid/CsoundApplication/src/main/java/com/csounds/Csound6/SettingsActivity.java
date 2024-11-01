package com.csounds.Csound6;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentTransaction;

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
}

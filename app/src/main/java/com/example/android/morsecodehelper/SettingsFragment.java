package com.example.android.morsecodehelper;

import android.os.Bundle;
import android.widget.SeekBar;

import androidx.preference.PreferenceFragmentCompat;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.root_preferences);
        setPreferencesFromResource(R.xml.root_preferences, rootKey);
    }
}
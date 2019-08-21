package com.example.taufiq.preference;

import android.os.Bundle;
import android.support.v7.preference.PreferenceFragmentCompat;

/**
 * Created By Taufiq on 8/20/2019.
 */
public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle bundle, String s) {
        // this method will create preference screen in this file.
        addPreferencesFromResource(R.xml.pref_visualizer);
    }
}

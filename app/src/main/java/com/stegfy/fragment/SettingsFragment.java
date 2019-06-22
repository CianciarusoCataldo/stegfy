package com.stegfy.fragment;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.ListPreference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.view.MenuItem;
import android.view.View;

import com.stegfy.R;
import com.stegfy.activity.SettingsActivity;

public class SettingsFragment extends PreferenceFragment {

    SharedPreferences sharedPref;
    ListPreference hashingPreference;
    ListPreference encryptionPreference;
    CheckBoxPreference animations;

    private String animationsPref;
    private String hashingPref;
    private String encryptionPref;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        Context context = getActivity().getApplicationContext();
        sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
        PreferenceManager.setDefaultValues(context, R.xml.preferences, false);

        final SharedPreferences.Editor editor = sharedPref.edit();
        hashingPref = getString(R.string.list_prefs_key_hashing);
        encryptionPref = getString(R.string.list_prefs_key_encryption);
        animationsPref = getString(R.string.list_prefs_animations);

        hashingPreference = (ListPreference) findPreference(hashingPref);
        encryptionPreference = (ListPreference) findPreference(encryptionPref);
        //encryptionPreference.setTitle("Encryption method:");

        animations = (CheckBoxPreference) findPreference(animationsPref);

        animations.setOnPreferenceChangeListener((preference, newValue) -> {
            editor.putBoolean(animationsPref, (Boolean) newValue);
            SettingsActivity.is_animations_enabled = (boolean) newValue;
            return editor.commit();
        });

        hashingPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            editor.putString(hashingPref, (String) newValue);
            return editor.commit();
        });

        encryptionPreference.setOnPreferenceChangeListener((preference, newValue) -> {
            editor.putString(encryptionPref, (String) newValue);
            return editor.commit();
        });
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            getActivity().onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

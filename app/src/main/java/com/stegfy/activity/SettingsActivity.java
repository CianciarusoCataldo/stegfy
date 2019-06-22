package com.stegfy.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.stegfy.fragment.SettingsFragment;

/**
 * This activity handle the setting screen of the app. There are some static values,
 * red from preferences file at startup, for a quick access.
 *
 * @author Cataldo Cianciaruso
 */
public class SettingsActivity extends AppCompatActivity {

    public static boolean is_animations_enabled = true;
    public static boolean pause = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction()
                .replace(android.R.id.content, new SettingsFragment())
                .commit();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

}

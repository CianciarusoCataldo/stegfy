package com.stegfy.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.AsyncLayoutInflater;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RadioButton;

import com.stegfy.R;
import com.stegfy.fragment.AboutDialogFragment;
import com.stegfy.fragment.FragmentDecode;
import com.stegfy.fragment.FragmentEncode;
import com.stegfy.utils.compress.Coders;

import java.util.Objects;

import es.dmoral.toasty.Toasty;

import static com.stegfy.Constants.TAB_DECODE_TITLE;
import static com.stegfy.Constants.TAB_ENCODE_TITLE;
import static com.stegfy.Constants.TAG_HOME;

/**
 * Define the structure of the main activity of the app. Its background is setted by theme,
 * so the user see it immediately at startup, like a splash screen.
 * It contains two tabs ("code" and "encode", for every task), and a drawer, for quick access.
 *
 * @author Cataldo Cianciaruso
 */
public class MainActivity extends RuntimePermissionsActivity {

    private static final int REQUEST_PERMISSIONS = 20;
    public static int navItemIndex = 0;
    public static String CURRENT_TAG = TAG_HOME;
    private NavigationView navigationView;
    private DrawerLayout drawer;


    @SuppressLint("InflateParams")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_layout);

        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);

        AsyncTask.execute(() -> {
            Toasty.Config.getInstance().tintIcon(true).apply(); // required
            SettingsActivity.is_animations_enabled = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean(getString(R.string.list_prefs_animations), true);
            if (savedInstanceState == null) {
                navItemIndex = 0;
                CURRENT_TAG = TAG_HOME;
            }
        });

        new AsyncLayoutInflater(this).inflate(R.layout.activity_main, null, (view, i, viewGroup) ->
                {
                    getAllPermissions();
                    AsyncTask.execute(() -> initViews(view));
                }
        );
    }


    /**
     * Initialize the views of the activity layout, and setup other components.
     *
     * @param view : the main view, inflated from the activity layout
     */
    public void initViews(View view) {
        ProgressBar progress = findViewById(R.id.startupLoading);
        progress.setVisibility(View.VISIBLE);
        ViewPager viewPager = view.findViewById(R.id.viewpager);
        View appbarlayout = view.findViewById(R.id.app_bar);
        Toolbar toolbar = appbarlayout.findViewById(R.id.toolbar);
        TabLayout tabLayout = appbarlayout.findViewById(R.id.tabs);
        navigationView = view.findViewById(R.id.nav_view);
        setSupportActionBar(toolbar);
        drawer = view.findViewById(R.id.drawer_layout);

        runOnUiThread(() -> {

            tabLayout.setupWithViewPager(viewPager);
            viewPager.setAdapter(new ViewPagerAdapter(getSupportFragmentManager()));
            Objects.requireNonNull(tabLayout.getTabAt(0)).setIcon(R.drawable.lock);
            Objects.requireNonNull(tabLayout.getTabAt(1)).setIcon(R.drawable.open);
            navigationView.getMenu().getItem(navItemIndex).setChecked(true);
            viewPager.setCurrentItem(0);

        });

        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);

        navigationView.setNavigationItemSelectedListener(menuItem -> {

            switch (menuItem.getItemId()) {

                case R.id.nav_settings:
                    Intent intentSettings = new Intent(MainActivity.this, SettingsActivity.class);
                    SettingsActivity.pause = true;
                    startActivity(intentSettings);
                    return true;

                case R.id.nav_about_us:
                    FragmentManager fm = getSupportFragmentManager();
                    AboutDialogFragment dialog = AboutDialogFragment.newInstance();
                    dialog.show(fm, "Stegify");
                    return true;

                default:
                    navItemIndex = 0;
            }
            menuItem.setChecked(true);
            return true;
        });

        ActionBarDrawerToggle actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.openDrawer, R.string.closeDrawer) {

            @Override
            public void onDrawerClosed(View drawerView) {
                super.onDrawerClosed(drawerView);
            }

            @Override
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        drawer.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();

        runOnUiThread(() -> {
            progress.setVisibility(View.GONE);
            setContentView(view);
        });
    }

    /**
     * Request all necessaary permissions to user
     */
    private void getAllPermissions() {
        String[] permissionsNeeded = new String[]{
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.CAMERA,
                Manifest.permission.ACCESS_WIFI_STATE,
                Manifest.permission.ACCESS_NETWORK_STATE,
                Manifest.permission.CHANGE_NETWORK_STATE,
                Manifest.permission.CHANGE_WIFI_STATE,
                Manifest.permission.INTERNET};

        MainActivity.super.requestAppPermissions(permissionsNeeded,
                R.string.runtime_permissions_txt,
                REQUEST_PERMISSIONS);
    }

    /**
     * Handle the radio-buttons action in {@link R.layout#fragment_decode} layout.
     * They are used to set the text decompression algorithm to use during decoding.
     *
     * @param view : selected radio-button
     */
    public void onRadioButtonClicked(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch (view.getId()) {
            case R.id.noneDec:
                if (checked)
                    FragmentDecode.choiceDec = Coders.Algoritms.NONE;
                break;

            case R.id.lzwDec:
                if (checked)
                    FragmentDecode.choiceDec = Coders.Algoritms.LZW;
                break;
            case R.id.arithmeticDec:
                if (checked)
                    FragmentDecode.choiceDec = Coders.Algoritms.ARITM;
                break;
            case R.id.huffmanDec:
                if (checked)
                    FragmentDecode.choiceDec = Coders.Algoritms.HUFFMAN;
                break;
        }
    }


    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawers();
            return;
        }

        super.onBackPressed();
    }


    @Override
    public void onPermissionsGranted(final int requestCode) {
        Log.println(Log.INFO, "Permissions:", "All permissions granted.");
    }

    class ViewPagerAdapter extends FragmentPagerAdapter {

        private Fragment[] mFragmentList = {new FragmentEncode(), new FragmentDecode()};
        private String[] mFragmentTitleList = {TAB_ENCODE_TITLE, TAB_DECODE_TITLE};

        private ViewPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            try {
                return mFragmentList[position];
            } catch (ArrayIndexOutOfBoundsException ex) {
                return mFragmentList[0];
            }
        }

        @Override
        public int getCount() {
            return mFragmentList.length;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList[position];
        }
    }
}

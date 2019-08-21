package com.example.taufiq.preference;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.taufiq.preference.audio_visual.AudioInputReader;
import com.example.taufiq.preference.audio_visual.VisualizerView;

public class MainActivity extends AppCompatActivity implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final int MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE = 88;
    private VisualizerView mVisualizerView;
    private AudioInputReader mAudioInputReader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mVisualizerView = findViewById(R.id.activity_visualizer);
        setupPreferences();
        setupPermissions();


    }

    private void setupPreferences() {


        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);

        mVisualizerView.setShowBass(preferences.getBoolean(getString(R.string.show_bass_key),
                getResources().getBoolean(R.bool.pref_show_bass_default)));


        mVisualizerView.setShowMid(preferences.getBoolean(getString(R.string.show_mid_range_key),
                getResources().getBoolean(R.bool.pref_show_mid_range_default)));

        mVisualizerView.setShowTreble(preferences.getBoolean(getString(R.string.show_treble_key),
                getResources().getBoolean(R.bool.pref_show_treble_default)));

        mVisualizerView.setMinSizeScale(1);

        loadColorFromPreference(preferences);

        //register the listner
        preferences.registerOnSharedPreferenceChangeListener(this);
    }


    private void loadColorFromPreference(SharedPreferences preferences) {
        mVisualizerView.setColor(preferences.getString(getString(R.string.pref_color_key),
                getString(R.string.pref_color_red_value)));
    }

    /**
     * Below this point is code you do not need to modify; it deals with permissions
     * and starting/cleaning up the AudioInputReader
     **/

    /**
     * onPause Cleanup audio stream
     **/
    @Override
    protected void onPause() {
        super.onPause();
        if (mAudioInputReader != null) {
            mAudioInputReader.shutdown(isFinishing());
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mAudioInputReader != null) {
            mAudioInputReader.restart();
        }
    }

    /**
     * App Permissions for Audio
     **/
    private void setupPermissions() {
        // If we don't have the record audio permission...
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            // And if we're on SDK M or later...
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Ask again, nicely, for the permissions.
                String[] permissionsWeNeed = new String[]{Manifest.permission.RECORD_AUDIO};
                requestPermissions(permissionsWeNeed, MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE);
            }
        } else {
            // Otherwise, permissions were granted and we are ready to go!
            mAudioInputReader = new AudioInputReader(mVisualizerView, this);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSION_RECORD_AUDIO_REQUEST_CODE: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // The permission was granted! Start up the visualizer!
                    mAudioInputReader = new AudioInputReader(mVisualizerView, this);

                } else {
                    Toast.makeText(this, "Permission for audio not granted. Visualizer can't run.", Toast.LENGTH_LONG).show();
                    finish();
                    // The permission was denied, so we can show a message why we can't run the app
                    // and then close the app.
                }
            }
            // Other permissions could go down here

        }
    }


    /**
     * method to make menu
     * that inflate menu resource
     *
     * @param menu
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_item, menu);
        return true;
    }

    /**
     * method to make menu can be click and
     * do some actions
     *
     * @param item
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.settings_id) {
            Context context = this;
            Class settingAct = SettingsActivity.class;
            Intent intent = new Intent(context, settingAct);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        // set show bass, and by default, bass is shown!
        if (key.equals(getString(R.string.show_bass_key))) {
            mVisualizerView.setShowBass(sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.pref_show_bass_default)));

        } else if (key.equals(getString(R.string.show_mid_range_key))) {
            mVisualizerView.setShowMid(sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.pref_show_mid_range_default)));

        } else if (key.equals(getString(R.string.show_treble_key))) {
            mVisualizerView.setShowTreble(sharedPreferences.getBoolean(key,
                    getResources().getBoolean(R.bool.pref_show_treble_default)));
        } else if (key.equals(getString(R.string.pref_color_key))) {
            loadColorFromPreference(sharedPreferences);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        PreferenceManager.getDefaultSharedPreferences(this)
                .unregisterOnSharedPreferenceChangeListener(this);
    }
}

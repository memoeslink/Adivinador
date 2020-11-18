package com.app.memoeslink.adivinador;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.takisoft.preferencex.PreferenceFragmentCompat;

/**
 * Created by Memoeslink on 10/08/2017.
 */

public class SettingsActivity extends CommonActivity implements TextToSpeech.OnInitListener {
    private boolean speechAvailable;
    private AlertDialog dialog;
    private AudioManager audioManager;
    private TextToSpeech tts;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;
    private FortuneTeller methods;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        tts = new TextToSpeech(this, this);
        methods = new FortuneTeller(SettingsActivity.this);

        //Define dialog to notify about application restart
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(getString(R.string.alert_app_restart_title));
        builder.setMessage(getString(R.string.alert_app_restart_message));
        builder.setIcon(R.drawable.door);
        builder.setNeutralButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss());
        builder.setOnDismissListener(arg0 -> Methods.restartApplication(SettingsActivity.this));
        dialog = builder.create();

        //Set listeners
        listener = (prefs, key) -> {
            if (key.equals("preference_activeScreen"))
                Methods.setScreenVisibility(SettingsActivity.this, defaultPreferences.getBoolean("preference_activeScreen"));

            if (key.equals("preference_adsEnabled")) {
                if (defaultPreferences.getBoolean("preference_adsEnabled"))
                    preferences.putBoolean("temp_restartAds", true);
            }

            if (key.equals("preference_fortuneTellerAspect"))
                preferences.putBoolean("temp_changeFortuneTeller", true);

            if (key.equals("preference_language") || key.equals("preference_theme")) {
                try {
                    SettingsActivity.this.runOnUiThread(() -> dialog.show());
                } catch (Exception e) {
                    new Handler().postDelayed(() -> Methods.restartApplication(SettingsActivity.this), 500);
                }
            }

            if (key.equals("preference_saveNames")) {
                if (!defaultPreferences.getBoolean("preference_saveNames", true))
                    preferences.remove("nameList");
            }

            if (key.equals("preference_saveEnquiries")) {
                if (!defaultPreferences.getBoolean("preference_saveEnquiries", true))
                    preferences.remove("enquiryList");
            }

            if (key.equals("preference_seed"))
                methods.setRandomizer();

            if (key.equals("preference_stickHeader"))
                preferences.putBoolean("temp_restartActivity", true);
        };
        defaultPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        Methods.setScreenVisibility(SettingsActivity.this, defaultPreferences.getBoolean("preference_activeScreen"));
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (defaultPreferences.getBoolean("preference_audioEnabled"))
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    else
                        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (defaultPreferences.getBoolean("preference_audioEnabled"))
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                    else
                        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS)
            speechAvailable = Methods.setTTS(tts, defaultPreferences.getString("preference_language", "es"));
        else {
            speechAvailable = false;
            Methods.showSimpleToast(SettingsActivity.this, getString(R.string.toast_unavailable_voices));
        }
    }

    public static class MyPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.default_preferences, rootKey);
        }
    }
}

package com.app.memoeslink.adivinador.activity;

import android.content.SharedPreferences;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.view.KeyEvent;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;

import com.app.memoeslink.adivinador.R;
import com.app.memoeslink.adivinador.Screen;
import com.app.memoeslink.adivinador.preference.Preference;
import com.app.memoeslink.adivinador.preference.PreferenceHandler;
import com.takisoft.preferencex.PreferenceFragmentCompat;

public class SettingsActivity extends CommonActivity implements TextToSpeech.OnInitListener {
    private AlertDialog dialog;
    private AudioManager audioManager;
    private TextToSpeech tts;
    private SharedPreferences.OnSharedPreferenceChangeListener listener; //Declared as global to avoid destruction by JVM Garbage Collector

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportFragmentManager().beginTransaction().replace(android.R.id.content, new CustomPreferenceFragment()).commit();
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        tts = new TextToSpeech(this, this);

        AlertDialog.Builder builder = new AlertDialog.Builder(SettingsActivity.this);
        builder.setTitle(getString(R.string.alert_app_restart_title));
        builder.setMessage(getString(R.string.alert_app_restart_message));
        builder.setIcon(R.drawable.exit);
        builder.setNeutralButton(getString(R.string.ok), (dialog, which) -> dialog.dismiss());
        builder.setOnDismissListener(arg0 -> restartApplication());
        dialog = builder.create();

        //Set listeners
        listener = (prefs, key) -> {
            if (key.equals(Preference.SETTING_ACTIVE_SCREEN.getTag()))
                Screen.setContinuance(SettingsActivity.this, PreferenceHandler.getBoolean(Preference.SETTING_ACTIVE_SCREEN));

            if (key.equals(Preference.SETTING_ADS_ENABLED.getTag()) && PreferenceHandler.getBoolean(Preference.SETTING_ADS_ENABLED))
                PreferenceHandler.put(Preference.TEMP_RESTART_ACTIVITY, true);

            if (key.equals(Preference.SETTING_FORTUNE_TELLER_ASPECT.getTag()))
                PreferenceHandler.put(Preference.TEMP_CHANGE_FORTUNE_TELLER, true);

            if (key.equals(Preference.SETTING_LANGUAGE.getTag()) || key.equals(Preference.SETTING_THEME.getTag()) || key.equals(Preference.SETTING_SEED.getTag())) {
                try {
                    SettingsActivity.this.runOnUiThread(() -> dialog.show());
                } catch (Exception e) {
                    new Handler(Looper.getMainLooper()).postDelayed(this::restartApplication, 500);
                }
            }

            if (key.equals(Preference.SETTING_SAVE_NAMES.getTag()) && !PreferenceHandler.getBoolean(Preference.SETTING_SAVE_NAMES, true))
                PreferenceHandler.remove(Preference.DATA_STORED_NAMES);

            if (key.equals(Preference.SETTING_SAVE_ENQUIRIES.getTag()) && !PreferenceHandler.getBoolean(Preference.SETTING_SAVE_ENQUIRIES, true))
                PreferenceHandler.remove(Preference.DATA_STORED_PEOPLE);
        };
        PreferenceHandler.changeDefaultPreferencesListener(listener);
    }

    @Override
    public void onResume() {
        super.onResume();
        Screen.setContinuance(SettingsActivity.this, PreferenceHandler.getBoolean(Preference.SETTING_ACTIVE_SCREEN));
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
                    if (PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED))
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    else
                        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED))
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
            speechAvailable = isTTSAvailable(tts, PreferenceHandler.getString(Preference.SETTING_LANGUAGE, "es"));
        else {
            speechAvailable = false;
            showToast(getString(R.string.toast_voice_unavailability));
        }
    }

    public static class CustomPreferenceFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferencesFix(@Nullable Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.default_preferences, rootKey);
        }
    }
}

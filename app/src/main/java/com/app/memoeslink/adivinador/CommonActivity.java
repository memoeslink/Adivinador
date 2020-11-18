package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.media.AudioManager;
import android.os.Build;
import android.os.Bundle;
import android.os.LocaleList;
import android.speech.tts.TextToSpeech;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Memoeslink on 10/08/2017.
 */

public class CommonActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    private AudioManager audioManager;
    protected boolean available = false;
    protected String lastText = "";
    HashMap<String, String> map;
    Bundle bundle;
    TextToSpeech tts;
    SharedPreferencesHelper preferences;
    SharedPreferencesHelper defaultPreferences;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(Methods.getTheme(this));
        super.onCreate(savedInstanceState);
        preferences = new SharedPreferencesHelper(this, SharedPreferencesHelper.PREFERENCES);
        defaultPreferences = new SharedPreferencesHelper(this);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "UniqueID");
        bundle = new Bundle();
        bundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        tts = new TextToSpeech(this, this);
        setCustomActionBar(); //Set ActionBar aspect
    }

    @Override
    public void onResume() {
        super.onResume();
        Methods.setScreenVisibility(CommonActivity.this, defaultPreferences.getBoolean("preference_activeScreen"));
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
            available = Methods.setTTS(tts, defaultPreferences.getString("preference_language", "es"));
        else {
            available = false;
            Methods.showSimpleToast(CommonActivity.this, getString(R.string.toast_unavailable_voices));
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        context = wrap(context);
        super.attachBaseContext(context);
    }

    @SuppressWarnings("deprecation")
    void talk(String text) {
        if (available && defaultPreferences.getBoolean("preference_audioEnabled") && defaultPreferences.getBoolean("preference_voiceEnabled")) {
            lastText = text;

            if (tts.isSpeaking())
                tts.stop();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, "UniqueID");
            else
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
        }
    }

    protected void setCustomActionBar() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //Set ActionBar params
        View v = inflater.inflate(R.layout.actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_VERTICAL);
        params.gravity = Gravity.CENTER_VERTICAL;

        //Customize ActionBar
        ActionBar actionBar;

        if ((actionBar = getSupportActionBar()) != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setIcon(android.R.color.transparent);
            actionBar.setCustomView(v, params);
        }
    }

    public ContextWrapper wrap(Context context) {
        SharedPreferencesHelper preferences = new SharedPreferencesHelper(context);
        String language = LanguageHelper.getLanguage(preferences);

        if (language != null && !language.isEmpty()) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Resources res = context.getResources();
            Configuration config = res.getConfiguration();

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                config.setLocale(locale);
                LocaleList localeList = new LocaleList(locale);
                LocaleList.setDefault(localeList);
                config.setLocales(localeList);
                context = context.createConfigurationContext(config);
            } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                config.setLocale(locale);
                context = context.createConfigurationContext(config);
            } else {
                config.locale = locale;
                res.updateConfiguration(config, res.getDisplayMetrics());
            }
        }
        return new ContextWrapper(context);
    }
}

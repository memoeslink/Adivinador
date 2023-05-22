package com.app.memoeslink.adivinador;

import android.content.Context;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;

import com.app.memoeslink.adivinador.preference.Preference;
import com.app.memoeslink.adivinador.preference.PreferenceHandler;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class Speech implements TextToSpeech.OnInitListener {
    private TextToSpeech tts;
    private Bundle bundle;
    private boolean available = false;
    private static Speech instance;
    private static final Locale[] LOCALES;

    static {
        LOCALES = Locale.getAvailableLocales();
    }

    private Speech(Context context) {
        bundle = new Bundle();
        bundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        tts = new TextToSpeech(context, this);
    }

    public static synchronized Speech getInstance(Context context) {
        if (instance == null) instance = new Speech(context);
        return instance;
    }

    public boolean isAvailable() {
        return available;
    }

    public boolean isSpeaking() {
        return tts.isSpeaking();
    }

    public final boolean isTTSAvailable(TextToSpeech tts, String language) {
        if (tts != null) {
            List<String> regionNames = new ArrayList<>();

            for (Locale l : LOCALES) {
                regionNames.add(l.toString());
            }
            Collections.shuffle(regionNames);

            int position = 0;
            boolean anyAvailable = false;

            for (int tries = regionNames.size(); tries > 0; tries--) {
                int result = tts.setLanguage(new Locale(language, regionNames.get(position)));

                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    anyAvailable = true;
                    tries = 0;
                }
                position++;
            }
            return anyAvailable;
        }
        return false;
    }

    public final boolean speak(String text) {
        if (!available) return false;
        suppress();

        if (PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED) && PreferenceHandler.getBoolean(Preference.SETTING_VOICE_ENABLED) &&
                BaseApplication.Companion.getForeground()) {
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, "UniqueID");
            return true;
        }
        return false;
    }

    public final boolean suppress() {
        if (tts.isSpeaking()) {
            tts.stop();
            return true;
        }
        return false;
    }

    public final void destroy() {
        tts.stop();
        tts.shutdown();
        tts = null;
        instance = null;
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS)
            available = isTTSAvailable(tts, PreferenceHandler.getString(Preference.SETTING_LANGUAGE, "en"));
        else available = false;
    }
}

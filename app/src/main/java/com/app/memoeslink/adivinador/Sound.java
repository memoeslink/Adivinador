package com.app.memoeslink.adivinador;

import android.content.Context;
import android.media.MediaPlayer;

import androidx.annotation.RawRes;

import com.app.memoeslink.adivinador.preference.Preference;
import com.app.memoeslink.adivinador.preference.PreferenceHandler;

public class Sound {
    private static MediaPlayer mediaPlayer;

    public static void play(Context context, @RawRes int id) {
        if (PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED) &&
                PreferenceHandler.getBoolean(Preference.SETTING_SOUNDS_ENABLED) &&
                BaseApplication.Companion.getForeground()) {
            try {
                mediaPlayer = MediaPlayer.create(context, id);
                mediaPlayer.setOnCompletionListener(mp -> stop());
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception ignored) {
            }
        }
    }

    public static void stop() {
        if (mediaPlayer == null) return;

        try {
            mediaPlayer.reset();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception ignored) {
        }
    }
}

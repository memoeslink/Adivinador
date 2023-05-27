package com.app.memoeslink.adivinador;

import android.content.Context;
import android.media.MediaPlayer;

import com.app.memoeslink.adivinador.preference.Preference;
import com.app.memoeslink.adivinador.preference.PreferenceHandler;

import java.io.IOException;

public class Sound {
    private static MediaPlayer mediaPlayer;

    public static boolean play(Context context, String soundResource) {
        if (BaseApplication.Companion.getForeground() && PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED) && PreferenceHandler.getBoolean(Preference.SETTING_SOUNDS_ENABLED)) {
            if (soundResource != null) {
                try {
                    int soundId = context.getResources().getIdentifier(soundResource, "raw", context.getPackageName());
                    mediaPlayer = MediaPlayer.create(context, soundId);

                    try {
                        mediaPlayer.prepare();
                    } catch (IllegalStateException | IOException e) {
                        e.printStackTrace();
                    }

                    mediaPlayer.setOnCompletionListener(mp -> {
                        mp.reset();
                        mp.release();
                    });
                    mediaPlayer.start();
                    return true;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                }
            }
        }
        return false;
    }

    public static void stop() {
        try {
            mediaPlayer.reset();
            mediaPlayer.prepare();
            mediaPlayer.stop();
            mediaPlayer.release();
            mediaPlayer = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

package com.app.memoeslink.adivinador;

import android.content.Context;
import android.media.MediaPlayer;

import com.app.memoeslink.adivinador.preference.Preference;
import com.app.memoeslink.adivinador.preference.PreferenceHandler;

import org.memoeslink.StringHelper;

public class Sound {
    private static MediaPlayer mediaPlayer;

    public static boolean play(Context context, String soundResource) {
        if (BaseApplication.Companion.getForeground() && PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED) && PreferenceHandler.getBoolean(Preference.SETTING_SOUNDS_ENABLED)) {
            if (StringHelper.isNotNullOrEmpty(soundResource)) {
                try {
                    int soundId = context.getResources().getIdentifier(soundResource, "raw", context.getPackageName());
                    mediaPlayer = MediaPlayer.create(context, soundId);

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

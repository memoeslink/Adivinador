package com.app.memoeslink.adivinador;

import android.content.Context;
import android.media.MediaPlayer;

import com.memoeslink.helper.SharedPreferencesHelper;

import java.io.IOException;

public class Sound {
    private static MediaPlayer mediaPlayer;

    public static boolean play(Context context, String soundResource) {
        SharedPreferencesHelper preferences = new SharedPreferencesHelper(context);

        if (preferences.getBoolean(Preference.SETTING_AUDIO_ENABLED.getTag()) && preferences.getBoolean(Preference.SETTING_SOUNDS_ENABLED.getTag())) {
            if (soundResource != null) {
                try {
                    int soundID = context.getResources().getIdentifier(soundResource, "raw", context.getPackageName());
                    mediaPlayer = MediaPlayer.create(context, soundID);

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
                    return false;
                } catch (Exception e) {
                    System.out.println(e.getMessage());
                    return false;
                }
            } else
                return false;
        } else
            return false;
    }
}

package com.app.memoeslink.adivinador;

import android.content.Context;

import com.app.memoeslink.adivinador.preference.Preference;
import com.app.memoeslink.adivinador.preference.PreferenceHandler;
import com.memoeslink.generator.common.StringHelper;

import java.util.Locale;

public class LanguageHelper {
    public static final String[] PERMITTED_LANGUAGES = {"en", "es"};

    public static String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getLanguage() {
        String language;

        if (!PreferenceHandler.has(Preference.SETTING_LANGUAGE)) {
            language = getDeviceLanguage();

            if (!StringHelper.equalsAny(language, PERMITTED_LANGUAGES))
                language = "en";
            PreferenceHandler.put(Preference.SETTING_LANGUAGE, language);
        } else
            language = PreferenceHandler.getString(Preference.SETTING_LANGUAGE, "en");
        return language;
    }

    public static Locale getLocale(Context context) {
        return context.getResources().getConfiguration().getLocales().get(0);
    }
}

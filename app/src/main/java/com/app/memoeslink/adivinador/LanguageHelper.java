package com.app.memoeslink.adivinador;

import android.content.Context;

import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.helper.SharedPreferencesHelper;

import java.util.Locale;

public class LanguageHelper {
    public static final String[] PERMITTED_LANGUAGES = {"en", "es"};

    public static String getDeviceLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getLanguage(Context context) {
        SharedPreferencesHelper preferences = new SharedPreferencesHelper(context);
        String language;

        if (!preferences.contains("preference_language")) {
            language = getDeviceLanguage();

            if (!StringHelper.equalsAny(language, PERMITTED_LANGUAGES))
                language = "en";
            preferences.putString("preference_language", language);
        } else
            language = preferences.getString("preference_language", "en");
        return language;
    }

    public static Locale getLocale(Context context) {
        return context.getResources().getConfiguration().getLocales().get(0);
    }
}

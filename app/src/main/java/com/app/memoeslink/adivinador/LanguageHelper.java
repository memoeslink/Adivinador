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

        if (!preferences.contains(Preference.SETTING_LANGUAGE.getName())) {
            language = getDeviceLanguage();

            if (!StringHelper.equalsAny(language, PERMITTED_LANGUAGES))
                language = "en";
            preferences.put(Preference.SETTING_LANGUAGE.getName(), language);
        } else
            language = preferences.getString(Preference.SETTING_LANGUAGE.getName(), "en");
        return language;
    }

    public static Locale getLocale(Context context) {
        return context.getResources().getConfiguration().getLocales().get(0);
    }
}

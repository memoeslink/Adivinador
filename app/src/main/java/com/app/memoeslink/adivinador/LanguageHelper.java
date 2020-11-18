package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.res.Resources;

import org.apache.commons.lang3.StringUtils;

import java.util.Locale;

public class LanguageHelper {
    @SuppressWarnings("deprecation")
    public static String getSystemLanguage() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N)
            return Resources.getSystem().getConfiguration().getLocales().get(0).getLanguage();
        else
            return Resources.getSystem().getConfiguration().locale.getLanguage();
    }

    public static String getDefaultLanguage() {
        return Locale.getDefault().getLanguage();
    }

    public static String getLanguage(Context context) {
        SharedPreferencesHelper preferences = new SharedPreferencesHelper(context);
        String language;

        if (!preferences.contains("preference_language")) {
            language = getDefaultLanguage();

            if (!StringUtils.equalsAny(language, Locale.ENGLISH.getLanguage(), "es"))
                language = Locale.ENGLISH.getLanguage();
            preferences.putString("preference_language", language);
        } else
            language = preferences.getString("preference_language", "es");
        return language;
    }
}

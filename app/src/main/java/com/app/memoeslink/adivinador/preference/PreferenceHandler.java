package com.app.memoeslink.adivinador.preference;

import android.content.Context;
import android.content.SharedPreferences;

import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.helper.SharedPreferencesHelper;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class PreferenceHandler {
    private static SharedPreferencesHelper preferences;
    private static SharedPreferencesHelper defaultPreferences;

    private PreferenceHandler() {
    }

    public static void init(Context context) {
        if (preferences == null)
            preferences = SharedPreferencesHelper.Companion.getPreferencesHelper(context);

        if (defaultPreferences == null) defaultPreferences = new SharedPreferencesHelper(context);
        deleteOldPreferences();
    }

    public static boolean has(Preference preference) {
        if (isNullOrNone(preference)) return false;
        return getPreferences(preference).contains(preference.getTag());
    }

    public static boolean put(Preference preference, Object value) {
        if (isNullOrNone(preference)) return false;

        if (!preference.getClassType().isInstance(value)) return false;
        return getPreferences(preference).put(preference.getTag(), value);
    }

    public static void save(Preference preference, Object value) {
        if (isNullOrNone(preference)) return;

        if (!preference.getClassType().isInstance(value)) return;
        getPreferences(preference).save(preference.getTag(), value);
    }

    public static boolean getBoolean(Preference preference) {
        return getBoolean(preference, SharedPreferencesHelper.getDEFAULT_BOOLEAN());
    }

    public static boolean getBoolean(Preference preference, boolean defaultValue) {
        if (!isValid(preference, Boolean.class)) return defaultValue;
        return getPreferences(preference).getBoolean(preference.getTag(), defaultValue);
    }

    public static Boolean getBooleanOrNull(Preference preference) {
        if (!isValid(preference, Boolean.class)) return null;
        return getPreferences(preference).getBooleanOrNull(preference.getTag());
    }

    public static float getFloat(Preference preference) {
        return getFloat(preference, SharedPreferencesHelper.getDEFAULT_FLOAT());
    }

    public static float getFloat(Preference preference, float defaultValue) {
        if (!isValid(preference, Float.class)) return defaultValue;
        return getPreferences(preference).getFloat(preference.getTag(), defaultValue);
    }

    public static Float getFloatOrNull(Preference preference) {
        if (!isValid(preference, Float.class)) return null;
        return getPreferences(preference).getFloatOrNull(preference.getTag());
    }

    public static int getInt(Preference preference) {
        return getInt(preference, SharedPreferencesHelper.getDEFAULT_INTEGER());
    }

    public static int getInt(Preference preference, int defaultValue) {
        if (!isValid(preference, Integer.class)) return defaultValue;
        return getPreferences(preference).getInt(preference.getTag(), defaultValue);
    }

    public static Integer getIntOrNull(Preference preference) {
        if (!isValid(preference, Integer.class)) return null;
        return getPreferences(preference).getIntOrNull(preference.getTag());
    }

    public static long getLong(Preference preference) {
        return getLong(preference, SharedPreferencesHelper.getDEFAULT_LONG());
    }

    public static long getLong(Preference preference, long defaultValue) {
        if (!isValid(preference, Long.class)) return defaultValue;
        return getPreferences(preference).getLong(preference.getTag(), defaultValue);
    }

    public static Long getLongOrNull(Preference preference) {
        if (!isValid(preference, Long.class)) return null;
        return getPreferences(preference).getLongOrNull(preference.getTag());
    }

    public static String getString(Preference preference) {
        return getString(preference, SharedPreferencesHelper.getDEFAULT_STRING());
    }

    public static String getString(Preference preference, String defaultValue) {
        if (!isValid(preference, String.class)) return defaultValue;
        return getPreferences(preference).getString(preference.getTag(), defaultValue);
    }

    public static String getStringOrNull(Preference preference) {
        if (!isValid(preference, String.class)) return null;
        return getPreferences(preference).getStringOrNull(preference.getTag());
    }

    public static int getStringAsInt(Preference preference) {
        return getStringAsInt(preference, SharedPreferencesHelper.getDEFAULT_INTEGER());
    }

    public static int getStringAsInt(Preference preference, int defaultValue) {
        if (!isValid(preference, String.class)) return defaultValue;
        return getPreferences(preference).getStringAsInt(preference.getTag(), defaultValue);
    }

    public static Integer getStringAsIntOrNull(Preference preference) {
        if (!isValid(preference, String.class)) return null;
        return getPreferences(preference).getStringAsIntOrNull(preference.getTag());
    }

    public static Set<String> getStringSet(Preference preference) {
        return getStringSet(preference, SharedPreferencesHelper.getDEFAULT_STRING_SET());
    }

    public static Set<String> getStringSet(Preference preference, Set<String> defaultValue) {
        if (!isValid(preference, Set.class)) return defaultValue;
        return getPreferences(preference).getStringSet(preference.getTag(), defaultValue);
    }

    public static Set<String> getStringSetOrNull(Preference preference) {
        if (!isValid(preference, Set.class)) return null;
        return getPreferences(preference).getStringSetOrNull(preference.getTag());
    }

    public static boolean remove(Preference preference) {
        if (isNullOrNone(preference)) return false;
        return getPreferences(preference).remove(preference.getTag());
    }

    public static void changePreferencesListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        preferences.unregisterOnSharedPreferenceChangeListener(listener);
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    public static void changeDefaultPreferencesListener(SharedPreferences.OnSharedPreferenceChangeListener listener) {
        defaultPreferences.unregisterOnSharedPreferenceChangeListener(listener);
        defaultPreferences.registerOnSharedPreferenceChangeListener(listener);
    }

    private static boolean isNullOrNone(Preference preference) {
        return preference == null || preference == Preference.NONE;
    }

    private static boolean isValid(Preference preference, Class<?> classType) {
        classType = classType != null ? classType : Object.class;
        return !isNullOrNone(preference) && preference.getClassType() == classType;
    }

    private static SharedPreferencesHelper getPreferences(Preference preference) {
        if (preference != null && StringHelper.startsWith(preference.toString(), "SETTING_"))
            return defaultPreferences;
        return preferences;
    }

    public static void deleteOldPreferences() {
        List<String> preferenceNames = Stream.of(Preference.values()).map(Preference::getTag).collect(Collectors.toList());

        for (Map.Entry<String, ?> entry : defaultPreferences.getAll().entrySet()) {
            if (!preferenceNames.contains(entry.getKey())) {
                if (remove(Preference.get(entry.getKey())))
                    System.out.println("Old system preference '" + entry.getKey() + "' was removed.");
            }
        }

        for (Map.Entry<String, ?> entry : preferences.getAll().entrySet()) {
            if (!preferenceNames.contains(entry.getKey()))
                if (remove(Preference.get(entry.getKey())))
                    System.out.println("Old preference '" + entry.getKey() + "' was removed.");
        }
        System.out.println("Preferences validation has finished.");
    }

    public static Context getContext() {
        return preferences.getBaseContext();
    }
}
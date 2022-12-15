package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.SharedPreferences;

import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.helper.SharedPreferencesHelper;

import java.util.Collections;
import java.util.Set;

public class PreferenceHandler {
    private static final boolean DEFAULT_BOOLEAN = false;
    private static final float DEFAULT_FLOAT = 0.0F;
    private static final int DEFAULT_INTEGER = 0;
    private static final long DEFAULT_LONG = 0L;
    private static final String DEFAULT_STRING = "";
    private static final Set<String> DEFAULT_STRING_SET = Collections.emptySet();
    private static SharedPreferencesHelper preferences;
    private static SharedPreferencesHelper defaultPreferences;

    private PreferenceHandler() {
    }

    public static void init(Context context) {
        if (preferences == null)
            preferences = SharedPreferencesHelper.Companion.getPreferencesHelper(context);

        if (defaultPreferences == null) defaultPreferences = new SharedPreferencesHelper(context);
    }

    public static boolean has(Preference preference) {
        if (isNullOrNone(preference)) return false;
        return getPreferences(preference).contains(preference.getTag());
    }

    public static boolean put(Preference preference, Object value) {
        if (isNullOrNone(preference)) return false;

        if (!preference.getClassType().isInstance(value)) return false;

        switch (preference) {
            case TEMP_NAME:
            case DATA_STORED_PEOPLE:
                return preferences.putString(preference.getTag(), (String) value);
            case TEMP_GENDER:
            case TEMP_YEAR_OF_BIRTH:
            case TEMP_MONTH_OF_BIRTH:
            case TEMP_DAY_OF_BIRTH:
            case SYSTEM_REVISED_DATABASE_VERSION:
                return preferences.putInt(preference.getTag(), (Integer) value);
            case TEMP_ANONYMOUS:
            case TEMP_BUSY:
            case TEMP_CHANGE_FORTUNE_TELLER:
            case TEMP_RESTART_ACTIVITY:
            case TEMP_RESTART_ADS:
                return preferences.putBoolean(preference.getTag(), (Boolean) value);
            case DATA_STORED_NAMES:
                return preferences.putStringSet(preference.getTag(), (Set<String>) value);
            case SETTING_GREETINGS_ENABLED:
            case SETTING_OPINIONS_ENABLED:
            case SETTING_PHRASES_ENABLED:
            case SETTING_AUDIO_ENABLED:
            case SETTING_SOUNDS_ENABLED:
            case SETTING_VOICE_ENABLED:
            case SETTING_HIDE_DRAWER:
            case SETTING_STICK_HEADER:
            case SETTING_PARTICLES_ENABLED:
            case SETTING_ADS_ENABLED:
            case SETTING_ACTIVE_SCREEN:
            case SETTING_KEEP_FORM:
            case SETTING_SAVE_NAMES:
            case SETTING_SAVE_ENQUIRIES:
                return defaultPreferences.putBoolean(preference.getTag(), (Boolean) value);
            case SETTING_SEED:
            case SETTING_UPDATE_TIME:
            case SETTING_FORTUNE_TELLER_ASPECT:
            case SETTING_REFRESH_TIME:
            case SETTING_TEXT_TYPE:
            case SETTING_LANGUAGE:
            case SETTING_THEME:
                return defaultPreferences.putString(preference.getTag(), (String) value);
            default:
                return false;
        }
    }

    public static void save(Preference preference, Object value) {
        if (isNullOrNone(preference)) return;

        if (!preference.getClassType().isInstance(value)) return;

        switch (preference) {
            case TEMP_NAME:
            case DATA_STORED_PEOPLE:
                preferences.saveString(preference.getTag(), (String) value);
                break;
            case TEMP_GENDER:
            case TEMP_YEAR_OF_BIRTH:
            case TEMP_MONTH_OF_BIRTH:
            case TEMP_DAY_OF_BIRTH:
            case SYSTEM_REVISED_DATABASE_VERSION:
                preferences.saveInt(preference.getTag(), (Integer) value);
                break;
            case TEMP_ANONYMOUS:
            case TEMP_BUSY:
            case TEMP_CHANGE_FORTUNE_TELLER:
            case TEMP_RESTART_ACTIVITY:
            case TEMP_RESTART_ADS:
                preferences.saveBoolean(preference.getTag(), (Boolean) value);
                break;
            case DATA_STORED_NAMES:
                preferences.saveStringSet(preference.getTag(), (Set<String>) value);
                break;
            case SETTING_GREETINGS_ENABLED:
            case SETTING_OPINIONS_ENABLED:
            case SETTING_PHRASES_ENABLED:
            case SETTING_AUDIO_ENABLED:
            case SETTING_SOUNDS_ENABLED:
            case SETTING_VOICE_ENABLED:
            case SETTING_HIDE_DRAWER:
            case SETTING_STICK_HEADER:
            case SETTING_PARTICLES_ENABLED:
            case SETTING_ADS_ENABLED:
            case SETTING_ACTIVE_SCREEN:
            case SETTING_KEEP_FORM:
            case SETTING_SAVE_NAMES:
            case SETTING_SAVE_ENQUIRIES:
                defaultPreferences.saveBoolean(preference.getTag(), (Boolean) value);
                break;
            case SETTING_SEED:
            case SETTING_UPDATE_TIME:
            case SETTING_FORTUNE_TELLER_ASPECT:
            case SETTING_REFRESH_TIME:
            case SETTING_TEXT_TYPE:
            case SETTING_LANGUAGE:
            case SETTING_THEME:
                defaultPreferences.saveString(preference.getTag(), (String) value);
                break;
        }
    }

    public static boolean getBoolean(Preference preference) {
        return getBoolean(preference, DEFAULT_BOOLEAN);
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
        return getFloat(preference, DEFAULT_FLOAT);
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
        return getInt(preference, DEFAULT_INTEGER);
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
        return getLong(preference, DEFAULT_LONG);
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
        return getString(preference, DEFAULT_STRING);
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
        return getStringAsInt(preference, DEFAULT_INTEGER);
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
        return getStringSet(preference, DEFAULT_STRING_SET);
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
}
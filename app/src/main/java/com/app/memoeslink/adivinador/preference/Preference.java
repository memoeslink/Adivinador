package com.app.memoeslink.adivinador.preference;

import java.util.HashMap;
import java.util.Set;

public enum Preference {
    NONE("", String.class),
    TEMP_NAME("temp_name", String.class),
    TEMP_GENDER("temp_gender", Integer.class),
    TEMP_YEAR_OF_BIRTH("temp_yearOfBirth", Integer.class),
    TEMP_MONTH_OF_BIRTH("temp_monthOfBirth", Integer.class),
    TEMP_DAY_OF_BIRTH("temp_dayOfBirth", Integer.class),
    TEMP_YEAR_OF_ENQUIRY("temp_yearOfEnquiry", Integer.class),
    TEMP_MONTH_OF_ENQUIRY("temp_monthOfEnquiry", Integer.class),
    TEMP_DAY_OF_ENQUIRY("temp_dayOfEnquiry", Integer.class),
    TEMP_ANONYMOUS("temp_anonymous", Boolean.class),
    TEMP_BUSY("temp_busy", Boolean.class),
    TEMP_CHANGE_FORTUNE_TELLER("temp_changeFortuneTeller", Boolean.class),
    TEMP_RESTART_ACTIVITY("temp_restartActivity", Boolean.class),
    TEMP_RESTART_ADS("temp_restartAds", Boolean.class),
    DATA_STORED_PEOPLE("data_storedPeople", String.class),
    DATA_STORED_NAMES("data_storedNames", Set.class),
    SYSTEM_REVISED_DATABASE_VERSION("revisedDatabaseVersion", Integer.class),
    SETTING_GREETINGS_ENABLED("setting_greetingsEnabled", Boolean.class),
    SETTING_OPINIONS_ENABLED("setting_opinionsEnabled", Boolean.class),
    SETTING_PHRASES_ENABLED("setting_phrasesEnabled", Boolean.class),
    SETTING_SEED("setting_seed", String.class),
    SETTING_UPDATE_TIME("setting_updateTime", String.class),
    SETTING_FORTUNE_TELLER_ASPECT("setting_fortuneTellerAspect", String.class),
    SETTING_REFRESH_TIME("setting_refreshTime", String.class),
    SETTING_AUDIO_ENABLED("setting_audioEnabled", Boolean.class),
    SETTING_SOUNDS_ENABLED("setting_soundsEnabled", Boolean.class),
    SETTING_VOICE_ENABLED("setting_voiceEnabled", Boolean.class),
    SETTING_TEXT_TYPE("setting_textType", String.class),
    SETTING_BACKGROUND_READING_ENABLED("setting_backgroundReadingEnabled", Boolean.class),
    SETTING_HIDE_DRAWER("setting_hideDrawer", Boolean.class),
    SETTING_PARTICLES_ENABLED("setting_particlesEnabled", Boolean.class),
    SETTING_ADS_ENABLED("setting_adsEnabled", Boolean.class),
    SETTING_LANGUAGE("setting_language", String.class),
    SETTING_PROFANITY_FILTER_ENABLED("setting_profanityFilterEnabled", Boolean.class),
    SETTING_THEME("setting_theme", String.class),
    SETTING_ACTIVE_SCREEN("setting_activeScreen", Boolean.class),
    SETTING_KEEP_FORM("setting_keepForm", Boolean.class),
    SETTING_SAVE_NAMES("setting_saveNames", Boolean.class),
    SETTING_SAVE_ENQUIRIES("setting_saveEnquiries", Boolean.class);

    private final String tag;
    private final Class<?> classType;
    private static final HashMap<String, Preference> LOOKUP = new HashMap<>();

    static {
        for (Preference preference : Preference.values()) {
            LOOKUP.put(preference.getTag(), preference);
        }
    }

    private Preference(String tag, Class<?> classType) {
        this.tag = tag;
        this.classType = classType;
    }

    public String getTag() {
        return tag;
    }

    public Class<?> getClassType() {
        return classType;
    }

    public static Preference get(String name) {
        return LOOKUP.getOrDefault(name, NONE);
    }
}

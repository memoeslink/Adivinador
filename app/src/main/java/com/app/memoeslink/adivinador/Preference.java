package com.app.memoeslink.adivinador;

import java.util.HashMap;

public enum Preference {
    NONE(""),
    TEMP_NAME("temp_name"),
    TEMP_GENDER("temp_gender"),
    TEMP_YEAR_OF_BIRTH("temp_yearOfBirth"),
    TEMP_MONTH_OF_BIRTH("temp_monthOfBirth"),
    TEMP_DAY_OF_BIRTH("temp_dayOfBirth"),
    TEMP_ANONYMOUS("temp_anonymous"),
    TEMP_BUSY("temp_busy"),
    TEMP_CHANGE_FORTUNE_TELLER("temp_changeFortuneTeller"),
    TEMP_RESTART_ACTIVITY("temp_restartActivity"),
    TEMP_RESTART_ADS("temp_restartAds"),
    DATA_STORED_PEOPLE("data_storedPeople"),
    DATA_STORED_NAMES("data_storedNames"),
    SYSTEM_REVISED_DATABASE_VERSION("revisedDatabaseVersion"),
    SETTING_GREETINGS_ENABLED("setting_greetingsEnabled"),
    SETTING_OPINIONS_ENABLED("setting_opinionsEnabled"),
    SETTING_PHRASES_ENABLED("setting_phrasesEnabled"),
    SETTING_SEED("setting_seed"),
    SETTING_UPDATE_TIME("setting_updateTime"),
    SETTING_FORTUNE_TELLER_ASPECT("setting_fortuneTellerAspect"),
    SETTING_REFRESH_TIME("setting_refreshTime"),
    SETTING_AUDIO_ENABLED("setting_audioEnabled"),
    SETTING_SOUNDS_ENABLED("setting_soundsEnabled"),
    SETTING_VOICE_ENABLED("setting_voiceEnabled"),
    SETTING_TEXT_TYPE("setting_textType"),
    SETTING_HIDE_DRAWER("setting_hideDrawer"),
    SETTING_STICK_HEADER("setting_stickHeader"),
    SETTING_PARTICLES_ENABLED("setting_particlesEnabled"),
    SETTING_ADS_ENABLED("setting_adsEnabled"),
    SETTING_LANGUAGE("setting_language"),
    SETTING_THEME("setting_theme"),
    SETTING_ACTIVE_SCREEN("setting_activeScreen"),
    SETTING_KEEP_FORM("setting_keepForm"),
    SETTING_SAVE_NAMES("setting_saveNames"),
    SETTING_SAVE_ENQUIRIES("setting_saveEnquiries");

    private final String tag;
    private static final HashMap<String, Preference> LOOKUP = new HashMap<>();

    static {
        for (Preference preference : Preference.values()) {
            LOOKUP.put(preference.getTag(), preference);
        }
    }

    private Preference(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public static Preference get(String name) {
        return LOOKUP.getOrDefault(name, NONE);
    }
}

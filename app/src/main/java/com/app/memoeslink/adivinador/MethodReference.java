package com.app.memoeslink.adivinador;

import java.util.HashMap;

public enum MethodReference {
    NONE(""),
    NAME("getName"),
    USERNAME("getUsername"),
    SECRET_NAME("getSecretName"),
    ALT_SUMMARY("getAltSummary"),
    NOUN("getNoun"),
    NOUN_WITH_ARTICLE("getNounWithArticle"),
    NOUN_WITH_INDEFINITE_ARTICLE("getNounWithIndefiniteArticle"),
    NOUN_WITH_ANY_ARTICLE("getNounWithAnyArticle"),
    DATE("getDate"),
    TIME("getTime"),
    PERCENTAGE("getPercentage"),
    DECIMAL_PERCENTAGE("getDecimalPercentage"),
    HEX_COLOR("getHexColor"),
    DEFAULT_COLOR("getDefaultColor"),
    DEVICE_INFO("getDeviceInfo"),
    CONTACT_NAME("getContactName"),
    SUGGESTED_NAME("getSuggestedName"),
    FORMATTED_NAME("getFormattedName"),
    SIMPLE_GREETING("getSimpleGreeting"),
    CURRENT_DAY_OF_WEEK("getCurrentDayOfWeek"),
    CURRENT_DATE("getCurrentDate"),
    CURRENT_TIME("getCurrentTime");

    private final String name;
    private static final HashMap<String, MethodReference> LOOKUP = new HashMap<>();

    static {
        for (MethodReference methodReference : MethodReference.values()) {
            LOOKUP.put(methodReference.getName(), methodReference);
        }
    }

    private MethodReference(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public static MethodReference get(String name) {
        return LOOKUP.getOrDefault(name, NONE);
    }
}

package com.app.memoeslink.adivinador;

import java.util.HashMap;

public enum MethodReference {
    NONE(""),
    PERSON("getPerson"),
    ANONYMOUS_PERSON("getAnonymousPerson"),
    USERNAME("getUsername"),
    SECRET_NAME("getSecretName"),
    NOUN("getNoun"),
    NOUN_WITH_ARTICLE("getNounWithArticle"),
    DATE("getDate"),
    TIME("getTime"),
    PERCENTAGE("getPercentage"),
    DEFAULT_COLOR("getDefaultColor"),
    COLOR("getColorStr"),
    DEVICE_USER("getDeviceUser"),
    FORMATTED_NAME("getFormattedName");

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

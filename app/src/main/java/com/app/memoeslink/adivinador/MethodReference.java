package com.app.memoeslink.adivinador;

import java.util.HashMap;

public enum MethodReference {
    NONE(""),
    NAME("getName"),
    USERNAME("getUsername"),
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
    INDIVIDUAL("getIndividual"),
    FORMATTED_INDIVIDUAL("getFormattedIndividual"),
    ACTOR("getActor"),
    FORMATTED_ACTOR("getFormattedActor"),
    AGREEMENT("getAgreement"),
    AMAZEMENT("getAmazement"),
    APOLOGY("getApology"),
    APPRECIATION("getAppreciation"),
    CONGRATULATION("getCongratulation"),
    DISAGREEMENT("getDisagreement"),
    DOUBT("getDoubt"),
    FAREWELL("getFarewell"),
    GREETING("getGreeting"),
    INITIATION_QUESTION("getInitiationQuestion"),
    INQUIRY_QUESTION("getInquiryQuestion"),
    SHORT_ANSWER("getShortAnswer"),
    WELCOME("getWelcome");

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

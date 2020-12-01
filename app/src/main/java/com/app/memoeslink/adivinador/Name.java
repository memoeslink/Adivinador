package com.app.memoeslink.adivinador;

public enum Name {
    EMPTY,
    TEST_CASE,
    NAME,
    FORENAME,
    SURNAME,
    COMPOUND_SURNAME,
    DOUBLE_BARRELLED_SURNAME,
    INTERNATIONAL_NAME,
    INTERNATIONAL_FORENAME,
    INTERNATIONAL_SURNAME,
    ENGLISH_NAME,
    ENGLISH_DOUBLE_BARRELLED_NAME,
    ENGLISH_FORENAME,
    ENGLISH_SURNAME,
    SPANISH_NAME,
    SPANISH_COMPOUND_NAME,
    SPANISH_FORENAME,
    SPANISH_GIVEN_NAME,
    SPANISH_SURNAME,
    JAPANESE_NAME,
    MEXICAN_NAME,
    RUSSIAN_NAME,
    GENERATED_PATTERN_NAME,
    GENERATED_NATURAL_NAME,
    GENERATED_DEFINED_NAME,
    GENERATED_FREQUENCY_NAME,
    GENERATED_ADJUSTED_NAME,
    ARABIC_NAME,
    FRENCH_NAME,
    GERMAN_NAME,
    INDIAN_NAME,
    ITALIAN_NAME,
    PORTUGUESE_NAME,
    SUPPORTED_NAME;

    private String subtype;

    Name() {
        subtype = null;
    }

    public String getSubtype() {
        return subtype;
    }

    public void setSubtype(String subtype) {
        this.subtype = subtype;
    }
}

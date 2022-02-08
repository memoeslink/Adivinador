package com.app.memoeslink.adivinador;

import android.content.Context;

public enum ZodiacSign {
    UNKNOWN(0x2049, "#00FFFFFF"),
    AQUARIUS(0x2652, "#40E0D0"),
    PISCES(0x2653, "#800080"),
    ARIES(0x2648, "#FF0000"),
    TAURUS(0x2649, "#008000"),
    GEMINI(0x264A, "#FFFF00"),
    CANCER(0x264B, "#FFFFFF"),
    LEO(0x264C, "#FFA500"),
    VIRGO(0x264D, "#C76C2E"),
    LIBRA(0x264E, "#FFC0CB"),
    SCORPIO(0x264F, "#FF0000"),
    SAGITTARIUS(0x2650, "#0000FF"),
    CAPRICORN(0x2651, "#000000");

    private final int emojiUtf8;
    private final String hexColor;

    private ZodiacSign(int emojiUtf8, String hexColor) {
        this.emojiUtf8 = emojiUtf8;
        this.hexColor = hexColor;
    }

    public String getEmoji() {
        return new String(Character.toChars(emojiUtf8));
    }

    public String getHexColor() {
        return hexColor;
    }

    public String getName(Context context) {
        return context.getResources().getStringArray(R.array.zodiac_sign)[this.ordinal()];
    }

    public String getAstrologicalHouse(Context context) {
        return context.getResources().getStringArray(R.array.astrological_house)[this.ordinal()];
    }

    public String getRuler(Context context) {
        return context.getResources().getStringArray(R.array.ruler)[this.ordinal()];
    }

    public String getElement(Context context) {
        return context.getResources().getStringArray(R.array.element)[this.ordinal()];
    }

    public String getColor(Context context) {
        return context.getResources().getStringArray(R.array.color)[this.ordinal()];
    }

    public String getNumbers(Context context) {
        return context.getResources().getStringArray(R.array.numbers)[this.ordinal()];
    }

    public String getCompatibility(Context context) {
        return context.getResources().getStringArray(R.array.compatibility)[this.ordinal()];
    }

    public String getIncompatibility(Context context) {
        return context.getResources().getStringArray(R.array.incompatibility)[this.ordinal()];
    }
}

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

    public String getPersonalityDescription(Context context) {
        return context.getResources().getStringArray(R.array.personality_description)[this.ordinal()];
    }

    public String getCompatibility(Context context) {
        return context.getResources().getStringArray(R.array.compatibility)[this.ordinal()];
    }

    public String getIncompatibility(Context context) {
        return context.getResources().getStringArray(R.array.incompatibility)[this.ordinal()];
    }

    public static ZodiacSign get(int month, int day) {
        switch (month) {
            case 1 -> {
                if (day >= 1 && day <= 31)
                    return day >= 20 ? ZodiacSign.AQUARIUS : ZodiacSign.CAPRICORN;
            }
            case 2 -> {
                if (day >= 1 && day <= 29)
                    return day >= 19 ? ZodiacSign.PISCES : ZodiacSign.AQUARIUS;
            }
            case 3 -> {
                if (day >= 1 && day <= 31)
                    return day >= 21 ? ZodiacSign.ARIES : ZodiacSign.PISCES;
            }
            case 4 -> {
                if (day >= 1 && day <= 30)
                    return day >= 21 ? ZodiacSign.TAURUS : ZodiacSign.ARIES;
            }
            case 5 -> {
                if (day >= 1 && day <= 31)
                    return day >= 22 ? ZodiacSign.GEMINI : ZodiacSign.TAURUS;
            }
            case 6 -> {
                if (day >= 1 && day <= 30)
                    return day >= 21 ? ZodiacSign.CANCER : ZodiacSign.GEMINI;
            }
            case 7 -> {
                if (day >= 1 && day <= 31)
                    return day >= 23 ? ZodiacSign.LEO : ZodiacSign.CANCER;
            }
            case 8 -> {
                if (day >= 1 && day <= 31)
                    return day >= 23 ? ZodiacSign.VIRGO : ZodiacSign.LEO;
            }
            case 9 -> {
                if (day >= 1 && day <= 30)
                    return day >= 23 ? ZodiacSign.LIBRA : ZodiacSign.VIRGO;
            }
            case 10 -> {
                if (day >= 1 && day <= 31)
                    return day >= 23 ? ZodiacSign.SCORPIO : ZodiacSign.LIBRA;
            }
            case 11 -> {
                if (day >= 1 && day <= 30)
                    return day >= 22 ? ZodiacSign.SAGITTARIUS : ZodiacSign.SCORPIO;
            }
            case 12 -> {
                if (day >= 1 && day <= 31)
                    return day >= 22 ? ZodiacSign.CAPRICORN : ZodiacSign.SAGITTARIUS;
            }
        }
        return ZodiacSign.UNKNOWN;
    }
}

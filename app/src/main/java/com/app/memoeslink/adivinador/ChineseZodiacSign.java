package com.app.memoeslink.adivinador;

import android.content.Context;

public enum ChineseZodiacSign {
    UNKNOWN,
    MONKEY,
    ROOSTER,
    DOG,
    PIG,
    RAT,
    OX,
    TIGER,
    RABBIT,
    DRAGON,
    SNAKE,
    HORSE,
    GOAT;

    public String getName(Context context) {
        return context.getResources().getStringArray(R.array.zodiac_sign)[this.ordinal()];
    }

    public static ChineseZodiacSign get(int year) {
        return ChineseZodiacSign.values()[year % 12 + 1];
    }
}

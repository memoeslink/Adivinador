package com.app.memoeslink.adivinador;

import android.content.Context;

public enum WalterBergZodiacSign {
    UNKNOWN(0x2049),
    AQUARIUS(0x2652),
    PISCES(0x2653),
    ARIES(0x2648),
    TAURUS(0x2649),
    GEMINI(0x264A),
    CANCER(0x264B),
    LEO(0x264C),
    VIRGO(0x264D),
    LIBRA(0x264E),
    SCORPIO(0x264F),
    OPHIUCHUS(0x26CE),
    SAGITTARIUS(0x2650),
    CAPRICORN(0x2651);

    private final int emojiUtf8;

    private WalterBergZodiacSign(int emojiUtf8) {
        this.emojiUtf8 = emojiUtf8;
    }

    public String getEmoji() {
        return new String(Character.toChars(emojiUtf8));
    }

    public String getName(Context context) {
        return context.getResources().getStringArray(R.array.walter_berg_zodiac_sign)[this.ordinal()];
    }

    public static WalterBergZodiacSign get(int month, int day) {
        switch (month) {
            case 1:
                if (day >= 1 && day <= 31)
                    return day >= 19 ? WalterBergZodiacSign.CAPRICORN : WalterBergZodiacSign.SAGITTARIUS;
                break;
            case 2:
                if (day >= 1 && day <= 29)
                    return day >= 16 ? WalterBergZodiacSign.AQUARIUS : WalterBergZodiacSign.CAPRICORN;
                break;
            case 3:
                if (day >= 1 && day <= 31)
                    return day >= 12 ? WalterBergZodiacSign.PISCES : WalterBergZodiacSign.AQUARIUS;
                break;
            case 4:
                if (day >= 1 && day <= 30)
                    return day >= 19 ? WalterBergZodiacSign.ARIES : WalterBergZodiacSign.PISCES;
                break;
            case 5:
                if (day >= 1 && day <= 31)
                    return day >= 14 ? WalterBergZodiacSign.TAURUS : WalterBergZodiacSign.ARIES;
                break;
            case 6:
                if (day >= 1 && day <= 30)
                    return day >= 20 ? WalterBergZodiacSign.GEMINI : WalterBergZodiacSign.TAURUS;
                break;
            case 7:
                if (day >= 1 && day <= 31)
                    return day >= 21 ? WalterBergZodiacSign.CANCER : WalterBergZodiacSign.GEMINI;
                break;
            case 8:
                if (day >= 1 && day <= 31)
                    return day >= 10 ? WalterBergZodiacSign.LEO : WalterBergZodiacSign.CANCER;
                break;
            case 9:
                if (day >= 1 && day <= 30)
                    return day >= 16 ? WalterBergZodiacSign.VIRGO : WalterBergZodiacSign.LEO;
                break;
            case 10:
                if (day >= 1 && day <= 31)
                    return day >= 31 ? WalterBergZodiacSign.LIBRA : WalterBergZodiacSign.VIRGO;
                break;
            case 11:
                if (day >= 1 && day <= 30)
                    return day >= 30 ? WalterBergZodiacSign.OPHIUCHUS : day >= 23 ? WalterBergZodiacSign.SCORPIO : WalterBergZodiacSign.LIBRA;
                break;
            case 12:
                if (day >= 1 && day <= 31)
                    return day >= 18 ? WalterBergZodiacSign.SAGITTARIUS : WalterBergZodiacSign.OPHIUCHUS;
                break;
        }
        return WalterBergZodiacSign.UNKNOWN;
    }
}

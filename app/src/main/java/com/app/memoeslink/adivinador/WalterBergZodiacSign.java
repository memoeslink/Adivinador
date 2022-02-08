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
}

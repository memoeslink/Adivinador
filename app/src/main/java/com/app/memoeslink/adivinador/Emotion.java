package com.app.memoeslink.adivinador;

import android.content.Context;

public enum Emotion {
    FEAR(0x1F631),
    JOY(0x1F601),
    ANGER(0x1F620),
    SADNESS(0x1F622),
    LOVE(0x1F60D),
    DISGUST(0x1F61D),
    SURPRISE(0x1F62E),
    SHAME(0x1F633),
    SATISFACTION(0x1F60C),
    REMORSE(0x1F614);

    private final int emojiUtf8;

    private Emotion(int emojiUtf8) {
        this.emojiUtf8 = emojiUtf8;
    }

    public String getEmoji() {
        return new String(Character.toChars(emojiUtf8));
    }

    public String getName(Context context) {
        return context.getResources().getStringArray(R.array.emotion)[this.ordinal()];
    }
}

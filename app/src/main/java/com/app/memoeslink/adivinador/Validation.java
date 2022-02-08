package com.app.memoeslink.adivinador;

import android.util.Patterns;

public interface Validation {

    default boolean isEmailAddress(CharSequence email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    default boolean isUrl(CharSequence url) {
        return Patterns.WEB_URL.matcher(url).matches();
    }
}

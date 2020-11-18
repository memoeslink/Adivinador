package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.ContextWrapper;

public class BaseWrapper extends ContextWrapper {
    SharedPreferencesHelper preferences;
    SharedPreferencesHelper defaultPreferences;

    public BaseWrapper(Context context) {
        super(context);
        preferences = new SharedPreferencesHelper(this, SharedPreferencesHelper.PREFERENCES);
        defaultPreferences = new SharedPreferencesHelper(this);
    }
}

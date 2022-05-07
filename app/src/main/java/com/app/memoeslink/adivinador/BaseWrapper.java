package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.ContextWrapper;

import com.memoeslink.helper.SharedPreferencesHelper;

abstract class BaseWrapper extends ContextWrapper {
    protected SharedPreferencesHelper preferences;
    protected SharedPreferencesHelper defaultPreferences;

    public BaseWrapper(Context context) {
        super(context);
        preferences = SharedPreferencesHelper.Companion.getPreferencesHelper(context);
        defaultPreferences = new SharedPreferencesHelper(context);
    }
}

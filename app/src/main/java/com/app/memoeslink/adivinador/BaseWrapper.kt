package com.app.memoeslink.adivinador

import android.content.Context
import android.content.ContextWrapper
import com.memoeslink.helper.SharedPreferencesHelper
import com.memoeslink.helper.SharedPreferencesHelper.Companion.getPreferencesHelper

open class BaseWrapper(@JvmField protected var context: Context): ContextWrapper(context) {
    @JvmField
    protected var preferences: SharedPreferencesHelper = getPreferencesHelper(context)

    @JvmField
    protected var defaultPreferences: SharedPreferencesHelper = SharedPreferencesHelper(context)
}
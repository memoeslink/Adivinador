package com.app.memoeslink.adivinador

import android.content.Context
import android.content.ContextWrapper
import com.memoeslink.helper.SharedPreferencesHelper
import com.memoeslink.helper.SharedPreferencesHelper.Companion.getPreferencesHelper

abstract class BaseWrapper(@JvmField protected var context: Context) : ContextWrapper(context) {
    @JvmField
    protected var preferences: SharedPreferencesHelper = getPreferencesHelper(context)

    @JvmField
    protected var defaultPreferences: SharedPreferencesHelper = SharedPreferencesHelper(context)

    open fun deleteTemp() {
        preferences.remove("temp_changeFortuneTeller")
        preferences.remove("temp_busy")
        preferences.remove("temp_enquiryDate")
        preferences.remove("temp_restartActivity")

        if (!defaultPreferences.getBoolean("preference_keepForm")) clearForm()
    }

    open fun clearForm() {
        preferences.remove("temp_name")
        preferences.remove("temp_gender")
        preferences.remove("temp_date_year")
        preferences.remove("temp_date_month")
        preferences.remove("temp_date_day")
        preferences.remove("temp_anonymous")
    }
}
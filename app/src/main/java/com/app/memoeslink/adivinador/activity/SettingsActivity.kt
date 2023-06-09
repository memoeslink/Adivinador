package com.app.memoeslink.adivinador.activity

import android.content.DialogInterface
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AlertDialog
import com.app.memoeslink.adivinador.R
import com.app.memoeslink.adivinador.Screen
import com.app.memoeslink.adivinador.Speech
import com.app.memoeslink.adivinador.preference.Preference
import com.app.memoeslink.adivinador.preference.PreferenceHandler
import com.takisoft.preferencex.PreferenceFragmentCompat

class SettingsActivity : CommonActivity() {
    private var dialog: AlertDialog? = null
    private var listener: OnSharedPreferenceChangeListener? =
        null // Declared as global to avoid destruction by JVM Garbage Collector

    public override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager.beginTransaction()
            .replace(android.R.id.content, CustomPreferenceFragment()).commit()
        val builder = AlertDialog.Builder(this@SettingsActivity)
        builder.setTitle(getString(R.string.alert_app_restart_title))
        builder.setMessage(getString(R.string.alert_app_restart_message))
        builder.setIcon(R.drawable.exit)
        builder.setNeutralButton(getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
        builder.setOnDismissListener { restartApplication() }
        dialog = builder.create()

        // Set listeners
        listener = OnSharedPreferenceChangeListener { _: SharedPreferences?, key: String? ->
            if (key == Preference.SETTING_AUDIO_ENABLED.tag && !PreferenceHandler.getBoolean(
                    Preference.SETTING_AUDIO_ENABLED
                ) || key == Preference.SETTING_VOICE_ENABLED.tag && !PreferenceHandler.getBoolean(
                    Preference.SETTING_VOICE_ENABLED
                )
            ) Speech.getInstance(this@SettingsActivity).suppress()

            if (key == Preference.SETTING_ACTIVE_SCREEN.tag) Screen.setContinuance(
                this@SettingsActivity,
                PreferenceHandler.getBoolean(Preference.SETTING_ACTIVE_SCREEN)
            )

            if (key == Preference.SETTING_ADS_ENABLED.tag) PreferenceHandler.put(
                Preference.TEMP_RESTART_ACTIVITY, true
            )

            if (key == Preference.SETTING_FORTUNE_TELLER_ASPECT.tag) PreferenceHandler.put(
                Preference.TEMP_CHANGE_FORTUNE_TELLER, true
            )

            if (key == Preference.SETTING_LANGUAGE.tag || key == Preference.SETTING_THEME.tag || key == Preference.SETTING_SEED.tag) {
                try {
                    runOnUiThread { dialog?.show() }
                } catch (e: Exception) {
                    Handler(Looper.getMainLooper()).postDelayed({ restartApplication() }, 500)
                }
            }

            if (key == Preference.SETTING_SAVE_NAMES.tag && !PreferenceHandler.getBoolean(
                    Preference.SETTING_SAVE_NAMES, true
                )
            ) PreferenceHandler.remove(Preference.DATA_STORED_NAMES)

            if (key == Preference.SETTING_SAVE_ENQUIRIES.tag && !PreferenceHandler.getBoolean(
                    Preference.SETTING_SAVE_ENQUIRIES, true
                )
            ) PreferenceHandler.remove(Preference.DATA_STORED_PEOPLE)
        }
        PreferenceHandler.changeDefaultPreferencesListener(listener)
    }

    public override fun onResume() {
        super.onResume()
        Screen.setContinuance(
            this@SettingsActivity, PreferenceHandler.getBoolean(Preference.SETTING_ACTIVE_SCREEN)
        )
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    class CustomPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferencesFix(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.default_preferences, rootKey)
        }
    }
}
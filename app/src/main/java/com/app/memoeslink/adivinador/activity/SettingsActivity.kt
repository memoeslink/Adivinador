package com.app.memoeslink.adivinador.activity

import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.preference.PreferenceFragmentCompat
import com.app.memoeslink.adivinador.R
import com.app.memoeslink.adivinador.Speech
import com.app.memoeslink.adivinador.extensions.setContinuance
import com.app.memoeslink.adivinador.preference.Preference
import com.app.memoeslink.adivinador.preference.PreferenceHandler

class SettingsActivity : CommonActivity() {
    private lateinit var dialog: AlertDialog
    private lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener // Declared as global to avoid destruction by JVM Garbage Collector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportFragmentManager
            .beginTransaction()
            .replace(androidx.appcompat.R.id.content, CustomPreferenceFragment())
            .commit()

        dialog = AlertDialog.Builder(this@SettingsActivity)
            .setTitle(getString(R.string.alert_app_restart_title))
            .setMessage(getString(R.string.alert_app_restart_message)).setIcon(R.drawable.exit)
            .setNeutralButton(getString(R.string.ok)) { dialog: DialogInterface, _: Int -> dialog.dismiss() }
            .setOnDismissListener { restartApplication() }.create()

        listener = SharedPreferences.OnSharedPreferenceChangeListener { _, key ->
            when (key) {
                Preference.SETTING_AUDIO_ENABLED.tag, Preference.SETTING_VOICE_ENABLED.tag -> {
                    if (!PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED) || !PreferenceHandler.getBoolean(
                            Preference.SETTING_VOICE_ENABLED
                        )
                    ) Speech.getInstance(this@SettingsActivity).suppress()
                }

                Preference.SETTING_ACTIVE_SCREEN.tag -> this@SettingsActivity.setContinuance(
                    PreferenceHandler.getBoolean(Preference.SETTING_ACTIVE_SCREEN)
                )

                Preference.SETTING_ADS_ENABLED.tag -> PreferenceHandler.put(
                    Preference.TEMP_RESTART_ACTIVITY, true
                )

                Preference.SETTING_FORTUNE_TELLER_ASPECT.tag -> PreferenceHandler.put(
                    Preference.TEMP_CHANGE_FORTUNE_TELLER, true
                )

                Preference.SETTING_LANGUAGE.tag, Preference.SETTING_THEME.tag, Preference.SETTING_SEED.tag -> {
                    runOnUiThread {
                        dialog.show()
                    }
                }

                Preference.SETTING_SAVE_NAMES.tag -> {
                    if (!PreferenceHandler.getBoolean(Preference.SETTING_SAVE_NAMES)) PreferenceHandler.remove(
                        Preference.DATA_STORED_NAMES
                    )
                }

                Preference.SETTING_SAVE_ENQUIRIES.tag -> {
                    if (!PreferenceHandler.getBoolean(Preference.SETTING_SAVE_ENQUIRIES)) PreferenceHandler.remove(
                        Preference.DATA_STORED_PEOPLE
                    )
                }
            }
        }
        PreferenceHandler.changeDefaultPreferencesListener(listener)
    }

    override fun onResume() {
        super.onResume()
        this@SettingsActivity.setContinuance(PreferenceHandler.getBoolean(Preference.SETTING_ACTIVE_SCREEN))
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }

    class CustomPreferenceFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.default_preferences, rootKey)
        }
    }
}

package com.app.memoeslink.adivinador.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.media.AudioManager
import android.os.Bundle
import android.os.LocaleList
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.app.memoeslink.adivinador.ActivityState
import com.app.memoeslink.adivinador.ActivityStatus
import com.app.memoeslink.adivinador.BaseApplication
import com.app.memoeslink.adivinador.LanguageHelper
import com.app.memoeslink.adivinador.R
import com.app.memoeslink.adivinador.ResourceExplorer
import com.app.memoeslink.adivinador.Sound
import com.app.memoeslink.adivinador.Speech
import com.app.memoeslink.adivinador.extensions.setContinuance
import com.app.memoeslink.adivinador.preference.Preference
import com.app.memoeslink.adivinador.preference.PreferenceHandler
import org.memoeslink.StringHelper
import java.util.Locale

open class CommonActivity : AppCompatActivity() {
    private lateinit var audioManager: AudioManager
    protected lateinit var resourceExplorer: ResourceExplorer
    protected var activityState = ActivityState.LAUNCHED
    protected var status = ActivityStatus()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(themeId)
        super.onCreate(savedInstanceState)
        activityState = ActivityState.CREATED
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        resourceExplorer = ResourceExplorer(this@CommonActivity)
        setCustomActionBar() // Set ActionBar aspect
    }

    override fun onStart() {
        super.onStart()
        activityState = ActivityState.STARTED
    }

    override fun onResume() {
        super.onResume()
        activityState = ActivityState.RESUMED
        this@CommonActivity.setContinuance(PreferenceHandler.getBoolean(Preference.SETTING_ACTIVE_SCREEN))

        if (!PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED) || !PreferenceHandler.getBoolean(
                Preference.SETTING_VOICE_ENABLED
            )
        ) Speech.getInstance(this@CommonActivity).suppress()
    }

    override fun onPostResume() {
        super.onPostResume()
        activityState = ActivityState.POST_RESUMED
        status.initialized = true
    }

    override fun onPause() {
        super.onPause()
        activityState = ActivityState.PAUSED
    }

    override fun onStop() {
        super.onStop()
        activityState = ActivityState.STOPPED
    }

    override fun onDestroy() {
        super.onDestroy()
        activityState = ActivityState.DESTROYED
    }

    override fun dispatchKeyEvent(event: KeyEvent): Boolean {
        return when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    audioManager.adjustStreamVolume(
                        if (PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED)) AudioManager.STREAM_MUSIC else AudioManager.STREAM_RING,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_SHOW_UI
                    )
                }
                true
            }

            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    audioManager.adjustStreamVolume(
                        if (PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED)) AudioManager.STREAM_MUSIC else AudioManager.STREAM_RING,
                        AudioManager.ADJUST_LOWER,
                        AudioManager.FLAG_SHOW_UI
                    )
                }
                true
            }

            else -> super.dispatchKeyEvent(event)
        }
    }

    override fun attachBaseContext(context: Context) {
        var currentContext = context
        val language = LanguageHelper.getLanguage()

        if (StringHelper.isNotNullOrEmpty(language)) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val res = currentContext.resources
            val config = res.configuration
            config.setLocale(locale)
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            currentContext = currentContext.createConfigurationContext(config)
        }
        super.attachBaseContext(currentContext)
    }

    protected fun setCustomActionBar() {
        val inflater: LayoutInflater = this@CommonActivity.layoutInflater
        val v = inflater.inflate(R.layout.actionbar, null)
        val params = ActionBar.LayoutParams(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT,
            Gravity.CENTER_VERTICAL
        )
        supportActionBar?.apply {
            setDisplayShowCustomEnabled(true)
            setDisplayShowTitleEnabled(false)
            setDisplayUseLogoEnabled(false)
            setIcon(android.R.color.transparent)
            setCustomView(v, params)
        }
    }

    protected fun showToast(text: String?, quick: Boolean = false) {
        if (!BaseApplication.foreground || text.isNullOrBlank()) return

        if (activityState != ActivityState.PAUSED && activityState != ActivityState.STOPPED) {
            if (toast != null) toast?.cancel()

            toast = Toast.makeText(
                this@CommonActivity, text, if (quick) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
            )
            Sound.play(this@CommonActivity, R.raw.computer_chimes)
            toast?.show()
        }
    }

    protected fun copyTextToClipboard(text: String?) {
        if (StringHelper.isNullOrEmpty(text)) {
            showToast(getString(R.string.toast_clipboard_error))
            return
        }
        val clipboard = getSystemService(CLIPBOARD_SERVICE) as ClipboardManager
        val clipData = ClipData.newPlainText(null, text)
        clipboard.setPrimaryClip(clipData)
        showToast(getString(R.string.toast_clipboard_success))
    }

    protected fun restartApplication() {
        val i = packageManager.getLaunchIntentForPackage(packageName)
        val mainIntent = Intent.makeRestartActivityTask(i?.component)
        startActivity(mainIntent)
        Runtime.getRuntime().exit(0)
    }

    companion object {
        private var toast: Toast? = null

        init {
            AppCompatDelegate.setCompatVectorFromResourcesEnabled(true)
        }

        private val themeId: Int
            get() = when (PreferenceHandler.getStringAsInt(Preference.SETTING_THEME)) {
                1 -> R.style.BlackTheme
                2 -> R.style.GrayTheme
                3 -> R.style.SlateGrayTheme
                else -> R.style.DefaultTheme
            }
    }
}
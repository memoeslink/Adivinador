package com.app.memoeslink.adivinador.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.media.AudioManager
import android.os.Bundle
import android.os.LocaleList
import android.view.Gravity
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
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
import com.app.memoeslink.adivinador.Screen
import com.app.memoeslink.adivinador.Sound
import com.app.memoeslink.adivinador.Speech
import com.app.memoeslink.adivinador.preference.Preference
import com.app.memoeslink.adivinador.preference.PreferenceHandler
import com.memoeslink.generator.common.StringHelper
import java.util.Locale

open class CommonActivity : AppCompatActivity() {
    private var audioManager: AudioManager? = null
    protected lateinit var resourceExplorer: ResourceExplorer
    protected var activityState = ActivityState.LAUNCHED
    protected var status = ActivityStatus()

    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(themeId)
        super.onCreate(savedInstanceState)
        activityState = ActivityState.CREATED
        resourceExplorer = ResourceExplorer(this@CommonActivity)
        audioManager = getSystemService(AUDIO_SERVICE) as AudioManager
        setCustomActionBar() //Set ActionBar aspect
    }

    override fun onStart() {
        super.onStart()
        activityState = ActivityState.STARTED
    }

    override fun onResume() {
        super.onResume()
        activityState = ActivityState.RESUMED
        Screen.setContinuance(
            this@CommonActivity, PreferenceHandler.getBoolean(Preference.SETTING_ACTIVE_SCREEN)
        )

        //Stop TTS if it is disabled and continues talking
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
                    audioManager?.adjustStreamVolume(
                        if (PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED)) AudioManager.STREAM_MUSIC else AudioManager.STREAM_RING,
                        AudioManager.ADJUST_RAISE,
                        AudioManager.FLAG_SHOW_UI
                    )
                }
                true
            }

            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (event.action == KeyEvent.ACTION_DOWN) {
                    audioManager?.adjustStreamVolume(
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
        var context = context
        val language = LanguageHelper.getLanguage()

        if (StringHelper.isNotNullOrEmpty(language)) {
            val locale = Locale(language)
            Locale.setDefault(locale)
            val res = context.resources
            val config = res.configuration
            config.setLocale(locale)
            val localeList = LocaleList(locale)
            LocaleList.setDefault(localeList)
            config.setLocales(localeList)
            context = context.createConfigurationContext(config)
        }
        super.attachBaseContext(context)
    }

    protected fun setCustomActionBar() {
        val inflater =
            this.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater //Set ActionBar params
        val v = inflater.inflate(R.layout.actionbar, null)
        val params = ActionBar.LayoutParams(
            ActionBar.LayoutParams.MATCH_PARENT,
            ActionBar.LayoutParams.MATCH_PARENT,
            Gravity.CENTER_VERTICAL
        )
        params.gravity = Gravity.CENTER_VERTICAL

        //Customize ActionBar
        val actionBar = supportActionBar

        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true)
            actionBar.setDisplayShowTitleEnabled(false)
            actionBar.setDisplayUseLogoEnabled(false)
            actionBar.setIcon(android.R.color.transparent)
            actionBar.setCustomView(v, params)
        }
    }

    protected fun isViewVisible(view: View?): Boolean {
        if (view == null || !view.isShown) return false
        val rect = Rect()
        return view.getGlobalVisibleRect(rect) && view.height == rect.height() && view.width == rect.width()
    }

    protected fun fadeAndShowView(v: View) {
        v.clearAnimation()
        val alphaAnimation = AlphaAnimation(1.0f, 0.0f)
        alphaAnimation.duration = 350
        alphaAnimation.repeatCount = 1
        alphaAnimation.repeatMode = Animation.REVERSE
        v.startAnimation(alphaAnimation)
    }

    protected fun showToast(text: String?, quick: Boolean = false) {
        if (!BaseApplication.foreground || text.isNullOrBlank()) return

        if (activityState != ActivityState.PAUSED && activityState != ActivityState.STOPPED) {
            if (toast != null) toast?.cancel()

            toast = Toast.makeText(
                this@CommonActivity, text, if (quick) Toast.LENGTH_SHORT else Toast.LENGTH_LONG
            )
            Sound.play(this@CommonActivity, "computer_chimes")
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
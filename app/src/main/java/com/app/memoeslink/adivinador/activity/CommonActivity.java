package com.app.memoeslink.adivinador.activity;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Rect;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.LocaleList;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.Toast;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.app.memoeslink.adivinador.ActivityState;
import com.app.memoeslink.adivinador.BaseApplication;
import com.app.memoeslink.adivinador.LanguageHelper;
import com.app.memoeslink.adivinador.R;
import com.app.memoeslink.adivinador.ResourceExplorer;
import com.app.memoeslink.adivinador.Screen;
import com.app.memoeslink.adivinador.Sound;
import com.app.memoeslink.adivinador.Speech;
import com.app.memoeslink.adivinador.preference.Preference;
import com.app.memoeslink.adivinador.preference.PreferenceHandler;
import com.memoeslink.generator.common.StringHelper;

import java.util.Locale;

public class CommonActivity extends AppCompatActivity {
    protected static Toast toast;
    protected ResourceExplorer resourceExplorer;
    protected AudioManager audioManager;
    protected ActivityState activityState = ActivityState.LAUNCHED;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getThemeId());
        super.onCreate(savedInstanceState);
        activityState = ActivityState.CREATED;
        resourceExplorer = new ResourceExplorer(CommonActivity.this);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        setCustomActionBar(); //Set ActionBar aspect
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityState = ActivityState.STARTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityState = ActivityState.RESUMED;
        Screen.setContinuance(CommonActivity.this, PreferenceHandler.getBoolean(Preference.SETTING_ACTIVE_SCREEN));

        //Stop TTS if it is disabled and continues talking
        if (!PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED) || !PreferenceHandler.getBoolean(Preference.SETTING_VOICE_ENABLED))
            Speech.getInstance(CommonActivity.this).suppress();
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityState = ActivityState.PAUSED;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityState = ActivityState.STOPPED;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityState = ActivityState.DESTROYED;
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED))
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    else
                        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED))
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                    else
                        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_LOWER, AudioManager.FLAG_SHOW_UI);
                }
                return true;
            default:
                return super.dispatchKeyEvent(event);
        }
    }

    @Override
    protected final void attachBaseContext(Context context) {
        context = wrap(context);
        super.attachBaseContext(context);
    }

    protected final void setCustomActionBar() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //Set ActionBar params
        View v = inflater.inflate(R.layout.actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_VERTICAL);
        params.gravity = Gravity.CENTER_VERTICAL;

        //Customize ActionBar
        ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setIcon(android.R.color.transparent);
            actionBar.setCustomView(v, params);
        }
    }

    protected final ContextWrapper wrap(Context context) {
        String language = LanguageHelper.getLanguage();

        if (StringHelper.isNotNullOrEmpty(language)) {
            Locale locale = new Locale(language);
            Locale.setDefault(locale);
            Resources res = context.getResources();
            Configuration config = res.getConfiguration();
            config.setLocale(locale);
            LocaleList localeList = new LocaleList(locale);
            LocaleList.setDefault(localeList);
            config.setLocales(localeList);
            context = context.createConfigurationContext(config);
        }
        return new ContextWrapper(context);
    }

    protected final boolean isViewVisible(View view) {
        if (view == null || !view.isShown()) return false;
        Rect rect = new Rect();
        return view.getGlobalVisibleRect(rect) && view.getHeight() == rect.height() && view.getWidth() == rect.width();
    }

    protected final void fadeAndShowView(View v) {
        v.clearAnimation();
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0F, 0.0F);
        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        v.startAnimation(alphaAnimation);
    }

    private void showToast(String text, boolean quick) {
        if (!BaseApplication.Companion.getForeground()) return;

        if (toast != null) toast.cancel();

        if (activityState != ActivityState.PAUSED && activityState != ActivityState.STOPPED) {
            toast = Toast.makeText(CommonActivity.this, text, quick ? Toast.LENGTH_SHORT : Toast.LENGTH_LONG);
            Sound.play(CommonActivity.this, "computer_chimes");
            toast.show();
        }
    }

    protected final void showToast(String text) {
        showToast(text, false);
    }

    protected final void showQuickToast(String text) {
        showToast(text, true);
    }

    protected final void copyTextToClipboard(String text) {
        if (StringHelper.isNullOrEmpty(text)) {
            showToast(getString(R.string.toast_clipboard_error));
            return;
        }
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, text);
        clipboard.setPrimaryClip(clipData);
        showToast(getString(R.string.toast_clipboard_success));
    }

    protected final int getThemeId() {
        return switch (PreferenceHandler.getStringAsInt(Preference.SETTING_THEME)) {
            case 1 -> R.style.BlackTheme;
            case 2 -> R.style.GrayTheme;
            case 3 -> R.style.SlateGrayTheme;
            default -> R.style.DefaultTheme;
        };
    }

    protected final void restartApplication() {
        Intent i = getPackageManager().getLaunchIntentForPackage(getPackageName());
        Intent mainIntent = Intent.makeRestartActivityTask(i.getComponent());
        startActivity(mainIntent);
        Runtime.getRuntime().exit(0);
    }
}

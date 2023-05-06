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
import android.speech.tts.TextToSpeech;
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

import com.app.memoeslink.adivinador.ActivityStatus;
import com.app.memoeslink.adivinador.LanguageHelper;
import com.app.memoeslink.adivinador.R;
import com.app.memoeslink.adivinador.ResourceExplorer;
import com.app.memoeslink.adivinador.Screen;
import com.app.memoeslink.adivinador.Sound;
import com.app.memoeslink.adivinador.preference.Preference;
import com.app.memoeslink.adivinador.preference.PreferenceHandler;
import com.memoeslink.generator.common.StringHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CommonActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    protected static Toast toast;
    protected static Locale[] locales;
    protected boolean speechAvailable;
    protected Bundle bundle;
    protected TextToSpeech tts;
    protected ResourceExplorer resourceExplorer;
    protected AudioManager audioManager;
    protected List<String> names = new ArrayList<>();
    protected ActivityStatus activityStatus = ActivityStatus.LAUNCHED;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        locales = Locale.getAvailableLocales();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getThemeId());
        super.onCreate(savedInstanceState);
        activityStatus = ActivityStatus.CREATED;
        resourceExplorer = new ResourceExplorer(CommonActivity.this);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        bundle = new Bundle();
        bundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        tts = new TextToSpeech(this, this);
        setCustomActionBar(); //Set ActionBar aspect
    }

    @Override
    protected void onStart() {
        super.onStart();
        activityStatus = ActivityStatus.STARTED;
    }

    @Override
    protected void onResume() {
        super.onResume();
        activityStatus = ActivityStatus.RESUMED;
        Screen.setContinuance(CommonActivity.this, PreferenceHandler.getBoolean(Preference.SETTING_ACTIVE_SCREEN));
    }

    @Override
    protected void onPause() {
        super.onPause();
        activityStatus = ActivityStatus.PAUSED;
    }

    @Override
    protected void onStop() {
        super.onStop();
        activityStatus = ActivityStatus.STOPPED;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        activityStatus = ActivityStatus.DESTROYED;
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
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS)
            speechAvailable = isTTSAvailable(tts, PreferenceHandler.getString(Preference.SETTING_LANGUAGE, "en"));
        else {
            speechAvailable = false;
            showToast(getString(R.string.toast_voice_unavailability));
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
        if (view == null || !view.isShown())
            return false;
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
        if (toast != null)
            toast.cancel();

        if (activityStatus != ActivityStatus.PAUSED && activityStatus != ActivityStatus.STOPPED) {
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
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, text);
        clipboard.setPrimaryClip(clipData);
        showToast(getString(R.string.toast_clipboard));
    }

    protected final boolean isTTSAvailable(TextToSpeech tts, String language) {
        if (tts != null) {
            List<String> regionNames = new ArrayList<>();

            for (Locale l : locales) {
                regionNames.add(l.toString());
            }
            Collections.shuffle(regionNames);

            int position = 0;
            boolean anyAvailable = false;

            for (int tries = regionNames.size(); tries > 0; tries--) {
                int result = tts.setLanguage(new Locale(language, regionNames.get(position)));

                if (result != TextToSpeech.LANG_MISSING_DATA && result != TextToSpeech.LANG_NOT_SUPPORTED) {
                    anyAvailable = true;
                    tries = 0;
                }
                position++;
            }
            return anyAvailable;
        }
        return false;
    }

    protected final void read(String text) {
        if (speechAvailable) {
            if (tts.isSpeaking()) tts.stop();

            if (activityStatus == ActivityStatus.RESUMED &&
                    PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED) &&
                    PreferenceHandler.getBoolean(Preference.SETTING_VOICE_ENABLED))
                tts.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, "UniqueID");
        }
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

package com.app.memoeslink.adivinador;

import android.app.AlarmManager;
import android.app.PendingIntent;
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
import android.text.Spanned;
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

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializer;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.helper.SharedPreferencesHelper;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

public class CommonActivity extends AppCompatActivity implements TextToSpeech.OnInitListener {
    protected static Toast toast;
    protected static Locale[] locales;

    static {
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        locales = Locale.getAvailableLocales();
    }

    protected boolean speechAvailable;
    protected Bundle bundle;
    protected TextToSpeech tts;
    protected SharedPreferencesHelper preferences;
    protected SharedPreferencesHelper defaultPreferences;
    protected List<Person> people = new ArrayList<>();
    protected List<String> names = new ArrayList<>();
    protected AudioManager audioManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(getThemeId());
        super.onCreate(savedInstanceState);
        preferences = SharedPreferencesHelper.Companion.getPreferencesHelper(CommonActivity.this);
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        bundle = new Bundle();
        bundle.putString(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "");
        tts = new TextToSpeech(this, this);
        setCustomActionBar(); //Set ActionBar aspect
    }

    @Override
    public void onResume() {
        super.onResume();
        Screen.setContinuance(CommonActivity.this, defaultPreferences.getBoolean("preference_activeScreen"));
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        int action = event.getAction();
        int keyCode = event.getKeyCode();

        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (defaultPreferences.getBoolean("preference_audioEnabled"))
                        audioManager.adjustStreamVolume(AudioManager.STREAM_MUSIC, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                    else
                        audioManager.adjustStreamVolume(AudioManager.STREAM_RING, AudioManager.ADJUST_RAISE, AudioManager.FLAG_SHOW_UI);
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                if (action == KeyEvent.ACTION_DOWN) {
                    if (defaultPreferences.getBoolean("preference_audioEnabled"))
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
            speechAvailable = isTTSAvailable(tts, defaultPreferences.getString("preference_language", "en"));
        else {
            speechAvailable = false;
            showSimpleToast(CommonActivity.this, getString(R.string.toast_voice_unavailability));
        }
    }

    @Override
    protected void attachBaseContext(Context context) {
        context = wrap(context);
        super.attachBaseContext(context);
    }

    protected void setCustomActionBar() {
        LayoutInflater inflater = (LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE); //Set ActionBar params
        View v = inflater.inflate(R.layout.actionbar, null);
        ActionBar.LayoutParams params = new ActionBar.LayoutParams(
                ActionBar.LayoutParams.MATCH_PARENT,
                ActionBar.LayoutParams.MATCH_PARENT,
                Gravity.CENTER_VERTICAL);
        params.gravity = Gravity.CENTER_VERTICAL;

        //Customize ActionBar
        ActionBar actionBar;

        if ((actionBar = getSupportActionBar()) != null) {
            actionBar.setDisplayShowCustomEnabled(true);
            actionBar.setDisplayShowTitleEnabled(false);
            actionBar.setDisplayUseLogoEnabled(false);
            actionBar.setIcon(android.R.color.transparent);
            actionBar.setCustomView(v, params);
        }
    }

    protected ContextWrapper wrap(Context context) {
        SharedPreferencesHelper preferences = new SharedPreferencesHelper(context);
        String language = LanguageHelper.getLanguage(preferences);

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

    protected boolean isViewVisible(View view) {
        if (view == null)
            return false;
        if (!view.isShown())
            return false;
        Rect rect = new Rect();
        return view.getGlobalVisibleRect(rect) && view.getHeight() == rect.height() && view.getWidth() == rect.width();
    }

    protected void vanishAndMaterializeView(View v) {
        v.clearAnimation();
        AlphaAnimation alphaAnimation = new AlphaAnimation(1.0F, 0.0F);
        alphaAnimation.setDuration(350);
        alphaAnimation.setRepeatCount(1);
        alphaAnimation.setRepeatMode(Animation.REVERSE);
        v.startAnimation(alphaAnimation);
    }

    protected void cancelToast() {
        if (toast != null && toast.getView().isShown()) {
            toast.cancel();
            toast = null;
        }
    }

    protected void showSimpleToast(Context context, String text) {
        cancelToast();
        toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        Sound.play(context, "computer_chimes");
        toast.show();
    }

    protected void showFormattedToast(Context context, Spanned spanned) {
        cancelToast();
        toast = Toast.makeText(context, spanned, Toast.LENGTH_SHORT);
        Sound.play(context, "computer_chimes");
        toast.show();
    }

    protected void copyTextToClipboard(String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText(null, text);
        clipboard.setPrimaryClip(clipData);
        showSimpleToast(this, getString(R.string.toast_clipboard));
    }

    protected boolean isTTSAvailable(TextToSpeech tts, String language) {
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
        } else
            return false;
    }

    protected final void talk(String text) {
        if (speechAvailable && defaultPreferences.getBoolean("preference_audioEnabled") && defaultPreferences.getBoolean("preference_voiceEnabled")) {
            if (tts.isSpeaking())
                tts.stop();
            tts.speak(text, TextToSpeech.QUEUE_FLUSH, bundle, "UniqueID");
        }
    }

    protected Person getPreferencesPerson() {
        Person person = new Person.PersonBuilder()
                .setGender(Gender.get(preferences.getInt("temp_gender")))
                .setBirthdate(LocalDate.of(
                        preferences.getInt("temp_date_year", 1900),
                        preferences.getInt("temp_date_month", 1),
                        preferences.getInt("temp_date_day", 1)
                ))
                .setAttribute("requested")
                .build();

        if (preferences.getBoolean("temp_anonymous")) {
            person.setUsername(preferences.getString("temp_name"));
            person.addAttribute("anonymous");
        } else
            person.setFullName(preferences.getString("temp_name"));
        return person;
    }

    protected boolean isNoPersonTempStored() {
        return (!preferences.contains("temp_name")
                && !preferences.contains("temp_gender")
                && !preferences.contains("temp_date_year")
                && !preferences.contains("temp_date_month")
                && !preferences.contains("temp_date_day")
        ) || isPersonNotTempStored(getPreferencesPerson());
    }

    protected boolean isPersonNotTempStored(Person person) {
        List<Person> people = Arrays.asList(getPreferencesPerson());
        return isPersonDistinct(person, people);
    }

    protected boolean isPersonDistinct(Person person) {
        return isPersonDistinct(person, people);
    }

    private boolean isPersonDistinct(Person person, List<Person> people) {
        if (person == null || people == null)
            return false;

        for (Person existingPerson : people) {
            if (existingPerson != null
                    && person.getDescriptor().equals(existingPerson.getDescriptor())
                    && person.getBirthdate().equals(existingPerson.getBirthdate())
                    && person.getGender().equals(existingPerson.getGender()))
                return false;
        }
        return true;
    }

    protected boolean savePerson(Person person) {
        if (defaultPreferences.getBoolean("preference_saveEnquiries", true) && isPersonDistinct(person)) {
            if (people.size() >= 100)
                people.remove(0);
            people.add(person);
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, (JsonSerializer<LocalDate>) (src, typeOfSrc, context) -> {
                return new JsonPrimitive(src.format(DateTimeFormatter.ISO_LOCAL_DATE)); // "yyyy-mm-dd"
            }).create();
            String json = gson.toJson(people);
            preferences.putStringSafely("peopleList", json);
            return true;
        }
        return false;
    }

    protected int getThemeId() {
        defaultPreferences = new SharedPreferencesHelper(CommonActivity.this);

        switch (defaultPreferences.getStringAsInt("preference_theme")) {
            case 1:
                return R.style.BlackTheme;
            case 2:
                return R.style.GrayTheme;
            case 3:
                return R.style.SlateGrayTheme;
            case 0:
            default:
                return R.style.DefaultTheme;
        }
    }

    protected void restartApplication() {
        Intent i = new Intent(this, MainActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 9999, i, PendingIntent.FLAG_CANCEL_CURRENT);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC, System.currentTimeMillis() + 500, pendingIntent);
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(0);
    }
}

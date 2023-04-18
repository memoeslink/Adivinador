package com.app.memoeslink.adivinador.activity;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.UtteranceProgressListener;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.multidex.BuildConfig;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.app.memoeslink.adivinador.ActivityStatus;
import com.app.memoeslink.adivinador.AdUnitId;
import com.app.memoeslink.adivinador.Divination;
import com.app.memoeslink.adivinador.FortuneTeller;
import com.app.memoeslink.adivinador.Prediction;
import com.app.memoeslink.adivinador.PredictionHistory;
import com.app.memoeslink.adivinador.R;
import com.app.memoeslink.adivinador.Screen;
import com.app.memoeslink.adivinador.Sound;
import com.app.memoeslink.adivinador.SpannerHelper;
import com.app.memoeslink.adivinador.preference.Preference;
import com.app.memoeslink.adivinador.preference.PreferenceHandler;
import com.app.memoeslink.adivinador.preference.PreferenceUtils;
import com.easyandroidanimations.library.BounceAnimation;
import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettiSource;
import com.github.jinatonic.confetti.ConfettoGenerator;
import com.github.jinatonic.confetti.Utils;
import com.github.jinatonic.confetti.confetto.BitmapConfetto;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.google.android.gms.common.util.ArrayUtils;
import com.lelloman.identicon.view.GithubIdenticonView;
import com.memoeslink.common.Randomizer;
import com.memoeslink.generator.common.DateTimeGetter;
import com.memoeslink.generator.common.DateTimeHelper;
import com.memoeslink.generator.common.GeneratorManager;
import com.memoeslink.generator.common.LongHelper;
import com.memoeslink.generator.common.NameType;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.manager.Device;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

import me.zhanghai.android.materialprogressbar.CircularProgressDrawable;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivity extends MenuActivity {
    private static final int[][] PARTICLE_COLORS = {{Color.BLUE, Color.argb(255, 0, 128, 255), Color.argb(255, 51, 153, 255), Color.argb(255, 0, 192, 199), Color.argb(125, 0, 128, 255), Color.argb(125, 51, 153, 255), Color.argb(125, 0, 192, 199)}, {Color.YELLOW, Color.argb(255, 251, 255, 147), Color.argb(255, 224, 228, 124), Color.argb(255, 155, 215, 93), Color.argb(255, 120, 168, 71), Color.argb(125, 251, 255, 147), Color.argb(125, 224, 228, 124), Color.argb(125, 155, 215, 93), Color.argb(125, 120, 168, 71)}};
    private static boolean forceEffects = true;
    private SwipeRefreshLayout srlRefresher;
    private RelativeLayout rlAdContainer;
    private LinearLayout llConfetti;
    private LinearLayout llDataEntryHolder;
    private LinearLayout llReloadHolder;
    private LinearLayout llInquiryHolder;
    private LinearLayout llSelectorHolder;
    private LinearLayout llClearHolder;
    private GithubIdenticonView vPersonImage;
    private AppCompatImageView ivFortuneTeller;
    private AppCompatImageView ivSaveLogo;
    private AppCompatAutoCompleteTextView atvInitialName;
    private AppCompatAutoCompleteTextView atvFinalName;
    private TextView tvPick;
    private TextView tvDataEntry;
    private TextView tvReload;
    private TextView tvInquiry;
    private TextView tvSelector;
    private TextView tvClear;
    private TextView tvPhrase;
    private TextView tvPersonInfo;
    private TextView tvPrediction;
    private TextView tvBinder;
    private TextView tvCompatibility;
    private TextView tvTextCopy;
    private EditText tvNameBox;
    private AppCompatSpinner spnNameType;
    private DatePickerDialog dpdInquiryDate;
    private MaterialProgressBar pbWait;
    private Button btDataEntry;
    private Button btTextCopy;
    private View vMain;
    private View vCompatibility;
    private View vNameGenerator;
    private AdView adView;
    private InterstitialAd interstitialAd;
    private AdRequest adRequest;
    private ConfettiManager confettiManager;
    private AlertDialog compatibilityDialog = null;
    private AlertDialog nameGeneratorDialog = null;
    private Dialog dialog = null;
    private boolean timerEnabled = false;
    private boolean measured = false;
    private boolean adAdded = false;
    private boolean started = false;
    private boolean obtaining = false;
    private Boolean adPaused = null;
    private int width = 0;
    private int height = 0;
    private int originInX = 0;
    private int originInY = 0;
    private int seconds = 0;
    private int updateSeconds = 0;
    private int resourceSeconds = 0;
    private int adSeconds = 0;
    private int frequency = 20;
    private int refreshFrequency = 60;
    private long measuredTimes = 0L;
    private long confettiThrown = 0L;
    private Timer timer;
    private Divination divination;
    private FortuneTeller fortuneTeller;
    private Device device;
    private Randomizer r;
    private PredictionHistory predictionHistory;
    private PredictionHistory backupPredictions;
    private List<Person> people = new ArrayList<>();
    private SharedPreferences.OnSharedPreferenceChangeListener listener; //Declared as global to avoid destruction by JVM Garbage Collector

    private static void onInitializationComplete(InitializationStatus initializationStatus) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, MainActivity::onInitializationComplete);
        srlRefresher = findViewById(R.id.main_refresh_layout);
        rlAdContainer = findViewById(R.id.ad_container);
        llConfetti = findViewById(R.id.main_confetti_layout);
        llDataEntryHolder = findViewById(R.id.main_data_entry_holder);
        llReloadHolder = findViewById(R.id.main_reload_holder);
        llInquiryHolder = findViewById(R.id.main_inquiry_holder);
        llSelectorHolder = findViewById(R.id.main_selector_holder);
        llClearHolder = findViewById(R.id.main_clear_holder);
        vPersonImage = findViewById(R.id.main_person_image);
        ivFortuneTeller = findViewById(R.id.main_fortune_teller);
        ivSaveLogo = findViewById(R.id.main_save);
        tvPick = findViewById(R.id.main_pick);
        tvPick.setText(SpannerHelper.fromHtml(getString(R.string.link, getString(R.string.pick))));
        tvDataEntry = findViewById(R.id.main_data_entry);
        tvDataEntry.setText(SpannerHelper.fromHtml(getString(R.string.link, getString(R.string.data_entry))));
        tvReload = findViewById(R.id.main_reload);
        tvReload.setText(SpannerHelper.fromHtml(getString(R.string.link, getString(R.string.reload))));
        tvInquiry = findViewById(R.id.main_inquiry);
        tvInquiry.setText(SpannerHelper.fromHtml(getString(R.string.link, getString(R.string.inquiry, "…"))));
        tvSelector = findViewById(R.id.main_selector);
        tvSelector.setText(SpannerHelper.fromHtml(getString(R.string.link, getString(R.string.selector))));
        tvClear = findViewById(R.id.main_clear);
        tvClear.setText(SpannerHelper.fromHtml(getString(R.string.link, getString(R.string.clear))));
        tvPhrase = findViewById(R.id.main_fortune_teller_phrase);
        tvPersonInfo = findViewById(R.id.main_person);
        tvPrediction = findViewById(R.id.main_prediction);
        btDataEntry = findViewById(R.id.main_edit_button);
        btTextCopy = findViewById(R.id.main_copy_button);
        vMain = findViewById(R.id.main_view);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        vCompatibility = inflater.inflate(R.layout.dialog_compatibility, null);
        atvInitialName = vCompatibility.findViewById(R.id.dialog_name_field);
        atvFinalName = vCompatibility.findViewById(R.id.dialog_other_name_field);
        tvBinder = vCompatibility.findViewById(R.id.dialog_binder_text);
        tvBinder.setText(SpannerHelper.fromHtml(getString(R.string.link, getString(R.string.binder))));
        tvCompatibility = vCompatibility.findViewById(R.id.dialog_text);
        pbWait = vCompatibility.findViewById(R.id.dialog_progress);
        vNameGenerator = inflater.inflate(R.layout.dialog_name_generation, null);
        spnNameType = vNameGenerator.findViewById(R.id.dialog_spinner);
        tvNameBox = vNameGenerator.findViewById(R.id.dialog_generated_name);
        tvTextCopy = vNameGenerator.findViewById(R.id.dialog_copy_text);
        tvTextCopy.setText(SpannerHelper.fromHtml(getString(R.string.link, getString(R.string.action_copy))));
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

        //Initialize resources
        divination = new Divination(MainActivity.this);
        fortuneTeller = new FortuneTeller(MainActivity.this);
        device = new Device(MainActivity.this);
        r = new Randomizer();
        predictionHistory = new PredictionHistory();
        backupPredictions = new PredictionHistory();

        //Request ads
        List<String> testDevices = new ArrayList<>();
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR);
        testDevices.add(device.getAndroidId());
        RequestConfiguration requestConfiguration = new RequestConfiguration.Builder().build();

        if (BuildConfig.DEBUG)
            requestConfiguration = new RequestConfiguration.Builder()
                    .setTestDeviceIds(testDevices)
                    .build();
        MobileAds.setRequestConfiguration(requestConfiguration);
        adRequest = new AdRequest.Builder().build();

        if (BuildConfig.DEBUG && !adRequest.isTestDevice(MainActivity.this))
            System.out.println("This device will not show test ads.");

        //Preload prediction
        predictionHistory.add(getPredictionData(PreferenceUtils.isEnquiryFormEntered()));

        //Set empty prediction
        tvPrediction.setText(SpannerHelper.fromHtml(divination.getEmptyPrediction().getFormattedContent(MainActivity.this)));

        //Get a greeting
        if (PreferenceHandler.getBoolean(Preference.SETTING_GREETINGS_ENABLED))
            tvPhrase.setText(SpannerHelper.fromHtml(fortuneTeller.greet()));
        else
            tvPhrase.setText("…");

        //Change drawable for fortune teller
        ivFortuneTeller.setImageResource(fortuneTeller.getRandomAppearance());

        //Delete temporary preferences
        PreferenceUtils.deleteTemp();

        //Set adapters
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.name_type)) {
            private final int[] positions = {0, 1, 2};

            @Override
            public boolean isEnabled(int position) {
                return !ArrayUtils.contains(positions, position);
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                view.setTag(position);
                view.setAlpha(isEnabled(position) ? 1.0F : 0.35F);
                return view;
            }
        };
        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnNameType.setAdapter(nameAdapter);
        spnNameType.setSelection(3);

        //Set alert dialogs
        ContextThemeWrapper wrapper = new ContextThemeWrapper(MainActivity.this, R.style.CustomDialog);
        AlertDialog.Builder compatibilityBuilder = new AlertDialog.Builder(wrapper);
        compatibilityBuilder.setTitle(R.string.compatibility);
        compatibilityBuilder.setNeutralButton(R.string.action_close, (dialog, which) -> this.compatibilityDialog.dismiss());
        compatibilityBuilder.setView(vCompatibility);
        this.compatibilityDialog = compatibilityBuilder.create();

        AlertDialog.Builder nameGeneratorBuilder = new AlertDialog.Builder(wrapper);
        nameGeneratorBuilder.setTitle(R.string.name_generation);
        nameGeneratorBuilder.setPositiveButton(R.string.action_generate, null);
        nameGeneratorBuilder.setNegativeButton(R.string.action_close, null);
        nameGeneratorBuilder.setView(vNameGenerator);
        nameGeneratorDialog = nameGeneratorBuilder.create();

        //Set listeners
        srlRefresher.setOnRefreshListener(() -> {
            refreshPrediction();
            srlRefresher.setRefreshing(false);
        });

        navigationView.setNavigationItemSelectedListener(item -> {
            if (PreferenceHandler.getBoolean(Preference.SETTING_HIDE_DRAWER, true))
                closeDrawer();

            switch (item.getItemId()) {
                case R.id.nav_data_entry:
                    Intent i = new Intent(MainActivity.this, InputActivity.class);
                    startActivity(i);
                    break;
                case R.id.nav_reload:
                    refreshPrediction();
                    break;
                case R.id.nav_save:
                    ivSaveLogo.performClick();
                    break;
                case R.id.nav_inquiry:
                    llInquiryHolder.performClick();
                    break;
                case R.id.nav_selector:
                    llSelectorHolder.performClick();
                    break;
                case R.id.nav_clear:
                    llClearHolder.performClick();
                    break;
                case R.id.nav_compatibility:
                    this.compatibilityDialog.show();
                    this.compatibilityDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    break;
                case R.id.nav_name_generation:
                    nameGeneratorDialog.show();
                    break;
            }
            return false;
        });

        LocalDate date = DateTimeHelper.getCurrentDate();

        dpdInquiryDate = new DatePickerDialog(MainActivity.this, (datePicker, i, i1, i2) -> {
            PreferenceHandler.put(Preference.TEMP_YEAR_OF_ENQUIRY, datePicker.getYear());
            PreferenceHandler.put(Preference.TEMP_MONTH_OF_ENQUIRY, datePicker.getMonth() + 1);
            PreferenceHandler.put(Preference.TEMP_DAY_OF_ENQUIRY, datePicker.getDayOfMonth());
            refreshPrediction();
        }, date.getYear(), date.getMonthValue() - 1, date.getDayOfMonth());

        tvPick.setOnClickListener(view -> {
            if (!dpdInquiryDate.isShowing()) {
                LocalDate pickedDate = LocalDate.parse(PreferenceUtils.getEnquiryDate());
                dpdInquiryDate.updateDate(pickedDate.getYear(), pickedDate.getMonthValue() - 1, pickedDate.getDayOfMonth());
                dpdInquiryDate.show();
            }
        });

        spnNameType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                getName();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        ivFortuneTeller.setOnClickListener(view -> {
            if (PreferenceHandler.getStringAsInt(Preference.SETTING_FORTUNE_TELLER_ASPECT, 1) != 0) {
                Sound.play(MainActivity.this, "jump");
                new BounceAnimation(ivFortuneTeller)
                        .setBounceDistance(7)
                        .setNumOfBounces(1)
                        .setDuration(150)
                        .animate();
            }
        });

        ivSaveLogo.setOnClickListener(v -> {
            if (PreferenceUtils.savePerson(predictionHistory.getLatest().getPerson())) {
                showToast(getString(R.string.toast_inquiry_saved));
                refreshUiUponEnquirySaved();
            } else
                showToast(getString(R.string.toast_inquiry_not_saved));
        });

        llDataEntryHolder.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, InputActivity.class);
            startActivity(i);
        });

        llReloadHolder.setOnClickListener(view -> refreshPrediction());

        llInquiryHolder.setOnClickListener(view -> {
            setFormPerson(people.get(0));
        });

        llSelectorHolder.setOnClickListener(view -> {
            if (dialog != null && !PreferenceHandler.getBoolean(Preference.TEMP_BUSY))
                dialog.show();
        });

        llClearHolder.setOnClickListener(view -> {
            PreferenceUtils.clearForm(); //Delete form data
            refreshPrediction();
        });

        btDataEntry.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, InputActivity.class);
            startActivity(i);
        });

        btTextCopy.setOnClickListener(view -> {
            if (!predictionHistory.isEmpty())
                copyTextToClipboard(predictionHistory.getLatest().getContent(MainActivity.this));
        });

        this.compatibilityDialog.setOnShowListener(dialog -> calculateCompatibility());

        nameGeneratorDialog.setOnShowListener(dialog -> {
            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> getName());
        });

        atvInitialName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateCompatibility();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        atvFinalName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                calculateCompatibility();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tvBinder.setOnClickListener(v -> atvInitialName.setText(predictionHistory.getLatest().getPerson().getDescriptor()));

        tvNameBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (StringHelper.isNullOrEmpty(s.toString()))
                    tvTextCopy.setVisibility(View.GONE);
                else
                    tvTextCopy.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        tvTextCopy.setOnClickListener(v -> copyTextToClipboard(tvNameBox.getText().toString()));

        listener = (prefs, key) -> {
            if (key.equals(Preference.DATA_STORED_PEOPLE.getTag())) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
                //recreate();
                updateInquirySelector();
            }

            if (key.equals(Preference.DATA_STORED_NAMES.getTag()))
                updateNameSuggestions(); //Update name suggestions for compatibility
        };
        PreferenceHandler.changePreferencesListener(listener);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateNameSuggestions(); //Update name suggestions for compatibility

        //Request permissions
        ActivityResultLauncher<String[]> launcher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            if (isGranted.containsValue(false))
                showToast(getString(R.string.denied_contact_permission));
        });
        boolean permissionsGranted = true;
        String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION};

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED)
                permissionsGranted = false;
        }

        if (!permissionsGranted)
            launcher.launch(permissions);

        //Show full-screen ads
        showInterstitialAd();
    }

    @Override
    public void onResume() {
        super.onResume();
        Long seed = LongHelper.getSeed(PreferenceHandler.getString(Preference.SETTING_SEED));

        if (seed != null && fortuneTeller.getSeed() != null && seed.equals(fortuneTeller.getSeed()))
            fortuneTeller = new FortuneTeller(MainActivity.this);

        //Restart Activity if required
        if (PreferenceHandler.has(Preference.TEMP_RESTART_ACTIVITY)) {
            forceEffects = true;
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        PreferenceHandler.remove(Preference.TEMP_RESTART_ACTIVITY);

        //Stop TTS if it is disabled and continues talking
        if (speechAvailable && (!PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED) || !PreferenceHandler.getBoolean(Preference.SETTING_VOICE_ENABLED)) && tts.isSpeaking())
            tts.stop();

        //Show, avoid, or hide ads
        prepareAd(false);

        //Update selectable people list
        updateInquirySelector();

        //Get a prediction
        refreshPrediction();

        //Change drawable if the 'fortune teller aspect' preference was changed
        if (PreferenceHandler.has(Preference.TEMP_CHANGE_FORTUNE_TELLER)) {
            ivFortuneTeller.setImageResource(fortuneTeller.getRandomAppearance());
            PreferenceHandler.remove(Preference.TEMP_CHANGE_FORTUNE_TELLER);
        }

        if (!timerEnabled) {
            timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    frequency = PreferenceHandler.getStringAsInt(Preference.SETTING_REFRESH_TIME, 20);
                    refreshFrequency = PreferenceHandler.getStringAsInt(Preference.SETTING_UPDATE_TIME, 60);

                    if (resourceSeconds >= 1800)
                        resourceSeconds = 0;
                    else
                        resourceSeconds++;

                    if (seconds >= frequency && frequency != 0) {
                        MainActivity.this.runOnUiThread(() -> {
                            if (activityStatus == ActivityStatus.RESUMED)
                                fadeAndShowView(tvPhrase); //Fade out and fade in the fortune teller's text.

                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                if (activityStatus == ActivityStatus.RESUMED) //Play random sound
                                    Sound.play(MainActivity.this, resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.sound_names));

                                //Change drawable for the fortune teller
                                ivFortuneTeller.setImageResource(fortuneTeller.getRandomAppearance());

                                //Get random text from the fortune teller
                                tvPhrase.setText(SpannerHelper.fromHtml(fortuneTeller.talk()));

                                //Read the text
                                read(tvPhrase.getText().toString());
                            }, 350);
                        });
                        seconds = 0;
                    } else {
                        if (seconds < 120)
                            seconds++;
                    }

                    if (updateSeconds >= refreshFrequency) { //Get another prediction if current is auto-generated, or refresh user-entered enquiry at day start
                        refreshPrediction();
                        updateSeconds = 0;
                    } else {
                        if (updateSeconds != 0 && updateSeconds % 10 == 0) {
                            if (!started && !backupPredictions.isFull()) {
                                new Thread(() -> {
                                    started = true;
                                    backupPredictions.add(getPredictionData(false));
                                    started = false;
                                }).start();
                            }
                        }
                        updateSeconds++;
                    }

                    if (adSeconds >= 3600) {
                        showInterstitialAd();
                        adSeconds = 0;
                    } else
                        adSeconds++;
                }
            }, 0, 1000);
            timerEnabled = true;
        }
        prepareAnimation(); //Measure confetti layout; create confetti and throw it; animate ImageView
    }

    @Override
    public void onPause() {
        super.onPause();
        stopConfetti(); //Finish any confetti animation
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroy() {
        if (timer != null) {
            timer.cancel();
            timer.purge();
            timer = null;
        }

        if (tts != null) {
            tts.stop();
            tts.shutdown();
            tts = null;
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (!closeDrawer()) {
            new AlertDialog.Builder(MainActivity.this)
                    .setTitle(R.string.alert_exit_title)
                    .setMessage(R.string.alert_exit_message)
                    .setNegativeButton(R.string.action_cancel, null)
                    .setPositiveButton(R.string.action_exit, (arg0, arg1) -> {
                        MainActivity.super.onBackPressed();
                        System.exit(0); //Try to stop current threads
                    }).create().show();
        }
    }

    @Override
    public void onConfigurationChanged(Configuration configuration) {
        super.onConfigurationChanged(configuration);
        prepareAnimation();
        prepareAd(true);
    }

    @Override
    public void onInit(int i) {
        super.onInit(i);

        if (speechAvailable) {
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                }

                @Override
                public void onError(String utteranceId) {
                }

                @Override
                public void onStart(String utteranceId) {
                }
            });
        }
    }

    private void prepareAd(boolean restarted) {
        if (adPaused == null || !adPaused) {
            if (PreferenceHandler.getBoolean(Preference.SETTING_ADS_ENABLED, true)) {
                if (restarted || !adAdded || PreferenceHandler.getBoolean(Preference.TEMP_RESTART_ADS))
                    destroyAd();

                if (!adAdded) {
                    adView = new AdView(MainActivity.this);
                    adView.setAdSize(AdSize.BANNER);
                    adView.setAdUnitId(AdUnitId.getBannerId());

                    //Set observer to get ad view height
                    ViewTreeObserver viewTreeObserver = adView.getViewTreeObserver();
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            try {
                                ViewGroup.LayoutParams params = vMain.getLayoutParams();
                                params.height = adView.getMeasuredHeight();
                                vMain.requestLayout();
                                adView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                            } catch (Exception ignored) {
                            }
                        }
                    });

                    adView.setAdListener(new AdListener() {
                        public void onAdLoaded() {
                            if (!adAdded) {
                                RelativeLayout.LayoutParams params; //Define ProgressBar
                                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                                ProgressBar progressBar = new ProgressBar(MainActivity.this, null, android.R.attr.progressBarStyleSmall);
                                progressBar.setIndeterminate(true);
                                progressBar.setIndeterminateDrawable(new CircularProgressDrawable(1, MainActivity.this));
                                rlAdContainer.addView(progressBar, params);

                                // Define AdView
                                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                                rlAdContainer.addView(adView, params);
                                rlAdContainer.setVisibility(View.VISIBLE);

                                //Define button to close ad
                                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                AppCompatImageView imageView = new AppCompatImageView(MainActivity.this);
                                imageView.setAlpha(0.8F);
                                imageView.setImageResource(R.drawable.close);
                                rlAdContainer.addView(imageView, params);

                                //Set listener
                                imageView.setOnClickListener(view -> destroyAd());
                                adAdded = true;
                            }
                        }

                        public void onAdFailedToLoad(LoadAdError error) {
                            System.out.println("The ad couldn't be loaded: " + error.getMessage());
                            destroyAd();
                        }
                    });

                    try {
                        adView.loadAd(adRequest);
                    } catch (Exception e) {
                        destroyAd();
                    }
                }
            } else
                destroyAd();
        } else
            adPaused = false;
    }

    private void destroyAd() {
        if (adView != null) {
            ViewGroup.LayoutParams params = vMain.getLayoutParams();
            params.height = 0;
            //vMain.requestLayout();
            //vMain.getLayoutParams().height = 0; This is slower!
            adView.destroy();
            adView.destroyDrawingCache();
            rlAdContainer.setVisibility(View.GONE);
            rlAdContainer.removeAllViews();
            adView = null;
            PreferenceHandler.remove(Preference.TEMP_RESTART_ADS);
            adAdded = false;
        }
    }

    private void showInterstitialAd() {
        if (PreferenceHandler.getBoolean(Preference.SETTING_ADS_ENABLED, true)) {
            r.bindSeed(LongHelper.getSeed(DateTimeGetter.getCurrentDateTime() + System.getProperty("line.separator") + device.getAndroidId()));

            if (r.getInt(20) == 0) {
                if (adPaused == null)
                    adPaused = true;

                MainActivity.this.runOnUiThread(() -> {
                    if (adView != null && adView.getVisibility() == View.VISIBLE)
                        destroyAd();

                    InterstitialAd.load(this, AdUnitId.getInterstitialId(), adRequest, new InterstitialAdLoadCallback() {
                        @Override
                        public void onAdLoaded(InterstitialAd ad) {
                            super.onAdLoaded(interstitialAd);
                            interstitialAd = ad;
                            interstitialAd.show(MainActivity.this);
                        }

                        @Override
                        public void onAdFailedToLoad(LoadAdError error) {
                            System.out.println("The ad couldn't be loaded: " + error.getMessage());
                            prepareAd(false); //Show, avoid, or hide ads@Override
                            tvSelector.invalidate(); //Force view to be redrawn
                        }
                    });
                });
            }
            r.unbindSeed();
        }
    }

    private void prepareAnimation() {
        Screen.lockScreenOrientation(MainActivity.this); //Lock orientation
        stopConfetti(); //Finish previous confetti animation;

        //Redraw view
        llConfetti.requestLayout();
        llConfetti.invalidate();

        //Wait for measurement to be done
        int[] size = getScreenSize();
        originInX = size[0] / 2;
        originInY = size[1] / 2;
        measured = false;
        waitTasks();

        if (measuredTimes == 0L)
            llConfetti.post(this::measureLayout); //Measure view after layout is completely loaded
        else
            new Handler(Looper.getMainLooper()).postDelayed(this::measureLayout, 1000); //Measure view after a while

        if (measuredTimes < Long.MAX_VALUE)
            measuredTimes++;
    }

    private void waitTasks() {
        if (!measured) {
            if (activityStatus == ActivityStatus.RESUMED || forceEffects)
                new Handler(Looper.getMainLooper()).postDelayed(this::waitTasks, 500);
        } else {
            if (activityStatus == ActivityStatus.RESUMED || forceEffects) {
                if (PreferenceHandler.getBoolean(Preference.SETTING_PARTICLES_ENABLED, true) && originInX != 0 && originInY != 0)
                    throwConfetti(originInX, originInY); //Start confetti animation

                if (PreferenceHandler.getStringAsInt(Preference.SETTING_FORTUNE_TELLER_ASPECT, 1) != 0) {
                    Sound.play(MainActivity.this, "jump");
                    new BounceAnimation(ivFortuneTeller)
                            .setBounceDistance(20)
                            .setNumOfBounces(1)
                            .setDuration(500)
                            .animate();
                }
                Screen.unlockScreenOrientation(MainActivity.this); //Unlock orientation
                forceEffects = false;
            }
        }
    }

    private void measureLayout() {
        width = llConfetti.getWidth();
        height = llConfetti.getHeight();
        originInX = width / 2;
        originInY = height / 2;
        measured = true;
        System.out.println("Confetti layout measures: " + width + "x" + height + " [Confetti origin in: " + originInX + " axis X; " + originInY + " axis Y]");
    }

    private void throwConfetti(int x, int y) {
        final List<Bitmap> allPossibleConfetti = Utils.generateConfettiBitmaps(PreferenceHandler.getStringAsInt(Preference.SETTING_FORTUNE_TELLER_ASPECT) == 0 ? PARTICLE_COLORS[0] : PARTICLE_COLORS[1], 10 /* size */);
        final int numConfetti = allPossibleConfetti.size();

        ConfettoGenerator confettoGenerator = random -> {
            final Bitmap bitmap = allPossibleConfetti.get(random.nextInt(numConfetti));
            return new BitmapConfetto(bitmap);
        };

        confettiManager = new ConfettiManager(this, confettoGenerator, new ConfettiSource(x, y), llConfetti)
                .setEmissionDuration(ConfettiManager.INFINITE_DURATION)
                .setEmissionRate(30)
                .setVelocityX(0, 360)
                .setVelocityY(0, 360)
                .setRotationalVelocity(180, 180)
                .setRotationalAcceleration(360, 180)
                .setInitialRotation(180, 180)
                .setTargetRotationalVelocity(360)
                .enableFadeOut(Utils.getDefaultAlphaInterpolator())
                .animate();

        if (confettiThrown < Long.MAX_VALUE)
            confettiThrown++;
    }

    private void stopConfetti() {
        if (confettiManager != null) {
            confettiManager.terminate();
            confettiManager = null;
        }
    }

    private void updateNameSuggestions() {
        List<String> nameList = null;

        if (PreferenceHandler.has(Preference.DATA_STORED_NAMES) && PreferenceHandler.getStringSet(Preference.DATA_STORED_NAMES).size() > 0)
            nameList = new ArrayList<>(PreferenceHandler.getStringSet(Preference.DATA_STORED_NAMES));

        if (nameList != null && nameList.size() > 0) {
            String[] names = nameList.toArray(new String[0]);
            atvInitialName.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, names));
            atvFinalName.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, names));
        }
    }

    private void updateInquirySelector() {
        people = PreferenceUtils.getStoredPeople();
        List<String> items = new ArrayList<>();

        for (Person person : people) {
            items.add(person.getDescriptor() + " (" + person.getGender().getName(MainActivity.this, 4) + "), " + person.getBirthdate());
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setNegativeButton(R.string.action_cancel, (dialogInterface, i) -> dialog.dismiss());

        ListView listView = new ListView(MainActivity.this);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, items);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener((parent, view, position, id) -> {
            setFormPerson(people.get(position));
            dialog.dismiss();
        });
        builder.setView(listView);
        dialog = builder.create();
    }

    private void setFormPerson(Person person) {
        if (!PreferenceUtils.isPersonTempStored(person)) {
            PreferenceUtils.saveFormPerson(person);
            refreshPrediction();
        }
    }

    private void refreshPrediction() {
        tvPick.setEnabled(false);
        updateSeconds = 0;
        llConfetti.animate() //Fade particles layout out
                .alpha(0.0F)
                .setDuration(200)
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationCancel(Animator animation) {
                        super.onAnimationEnd(animation);
                        vMain.clearAnimation();
                        vMain.setVisibility(View.INVISIBLE);
                    }
                });
        Sound.play(MainActivity.this, "wind");

        if (!PreferenceHandler.getBoolean(Preference.TEMP_BUSY)) {
            new Thread(() -> {
                PreferenceHandler.put(Preference.TEMP_BUSY, true);
                String pickedDate = DateTimeHelper.toIso8601Date(dpdInquiryDate.getDatePicker().getYear(),
                        dpdInquiryDate.getDatePicker().getMonth() + 1,
                        dpdInquiryDate.getDatePicker().getDayOfMonth()
                );
                boolean retrieved = false;

                if (!PreferenceUtils.isEnquiryFormEntered()) {
                    while (!backupPredictions.isEmpty()) {
                        Prediction prediction = backupPredictions.dispenseOldest();

                        if (prediction.getDate().equals(pickedDate)) {
                            predictionHistory.add(prediction);
                            retrieved = true;
                            break;
                        }
                    }

                    if (!retrieved)
                        predictionHistory.add(getPredictionData(false));
                } else predictionHistory.add(getPredictionData(true));

                MainActivity.this.runOnUiThread(() -> {
                    refreshHolders();
                    refreshNavigationView();
                    refreshSaveButton();
                    vPersonImage.setHash(predictionHistory.getLatest().getPerson().getSummary().hashCode());

                    tvPersonInfo.setText(SpannerHelper.fromHtml(getString(R.string.person_data,
                            predictionHistory.getLatest().getPerson().getDescription(),
                            predictionHistory.getLatest().getPerson().getGender().getName(MainActivity.this, 1),
                            DateTimeHelper.toIso8601Date(predictionHistory.getLatest().getPerson().getBirthdate())
                    )));
                    setLinksToText(tvPrediction, predictionHistory.getLatest().getFormattedContent(MainActivity.this));

                    if (activityStatus == ActivityStatus.RESUMED && !isViewVisible(tvPersonInfo))
                        showQuickToast(predictionHistory.getLatest().getPerson().getDescriptor());

                    //Read the text
                    read(tvPersonInfo.getText().toString() + ". " + predictionHistory.getLatest().getContent(MainActivity.this));
                    tvPick.setEnabled(true);
                    llConfetti.animate().alpha(1.0f).setDuration(200); //Fade particles layout in
                });
                PreferenceHandler.remove(Preference.TEMP_BUSY);
            }).start();
        }
    }

    private void refreshHolders() {
        if (people.size() == 0) {
            llInquiryHolder.setVisibility(View.GONE);
            llSelectorHolder.setVisibility(View.GONE);
        } else if (people.size() == 1) {
            tvInquiry.setText(SpannerHelper.fromHtml(getString(R.string.link, getString(R.string.inquiry, people.get(0).getDescriptor()))));

            if (PreferenceUtils.hasPersonTempStored())
                llInquiryHolder.setVisibility(View.GONE);
            else
                llInquiryHolder.setVisibility(View.VISIBLE);
            llSelectorHolder.setVisibility(View.GONE);
        } else {
            llInquiryHolder.setVisibility(View.GONE);
            llSelectorHolder.setVisibility(View.VISIBLE);
        }

        if (PreferenceUtils.hasPersonTempStored()) {
            llDataEntryHolder.setVisibility(View.GONE);
            llReloadHolder.setVisibility(View.GONE);
            llClearHolder.setVisibility(View.VISIBLE);
        } else {
            llDataEntryHolder.setVisibility(View.VISIBLE);
            llReloadHolder.setVisibility(View.VISIBLE);
            llClearHolder.setVisibility(View.GONE);
        }
    }

    private void refreshNavigationView() {
        if (PreferenceUtils.isEnquiryFormEntered()) {
            navigationView.getMenu().findItem(R.id.nav_data_entry).setEnabled(false);
            navigationView.getMenu().findItem(R.id.nav_data_entry).getIcon().setAlpha(125);
            navigationView.getMenu().findItem(R.id.nav_reload).setEnabled(false);
            navigationView.getMenu().findItem(R.id.nav_reload).getIcon().setAlpha(125);
            navigationView.getMenu().findItem(R.id.nav_save).setEnabled(false);
            navigationView.getMenu().findItem(R.id.nav_save).getIcon().setAlpha(125);
            navigationView.getMenu().findItem(R.id.nav_clear).setEnabled(true);
            navigationView.getMenu().findItem(R.id.nav_clear).getIcon().setAlpha(255);
        } else {
            navigationView.getMenu().findItem(R.id.nav_data_entry).setEnabled(true);
            navigationView.getMenu().findItem(R.id.nav_data_entry).getIcon().setAlpha(255);
            navigationView.getMenu().findItem(R.id.nav_reload).setEnabled(true);
            navigationView.getMenu().findItem(R.id.nav_reload).getIcon().setAlpha(255);
            navigationView.getMenu().findItem(R.id.nav_save).setEnabled(true);
            navigationView.getMenu().findItem(R.id.nav_save).getIcon().setAlpha(255);
            navigationView.getMenu().findItem(R.id.nav_clear).setEnabled(false);
            navigationView.getMenu().findItem(R.id.nav_clear).getIcon().setAlpha(125);
        }

        if (people.size() == 0) {
            navigationView.getMenu().findItem(R.id.nav_inquiry).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_inquiry).setEnabled(false);
            navigationView.getMenu().findItem(R.id.nav_inquiry).getIcon().setAlpha(125);
            navigationView.getMenu().findItem(R.id.nav_selector).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_selector).setEnabled(false);
            navigationView.getMenu().findItem(R.id.nav_selector).getIcon().setAlpha(125);
        } else if (people.size() == 1) {
            navigationView.getMenu().findItem(R.id.nav_inquiry).setTitle(getString(R.string.inquiry, people.get(0).getDescriptor()));

            if (PreferenceUtils.hasPersonTempStored()) {
                navigationView.getMenu().findItem(R.id.nav_inquiry).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_inquiry).setEnabled(false);
                navigationView.getMenu().findItem(R.id.nav_inquiry).getIcon().setAlpha(125);
            } else {
                navigationView.getMenu().findItem(R.id.nav_inquiry).setVisible(true);
                navigationView.getMenu().findItem(R.id.nav_inquiry).setEnabled(true);
                navigationView.getMenu().findItem(R.id.nav_inquiry).getIcon().setAlpha(255);
            }
            navigationView.getMenu().findItem(R.id.nav_selector).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_selector).setEnabled(false);
            navigationView.getMenu().findItem(R.id.nav_selector).getIcon().setAlpha(125);
        } else {
            navigationView.getMenu().findItem(R.id.nav_inquiry).setVisible(false);
            navigationView.getMenu().findItem(R.id.nav_inquiry).setEnabled(false);
            navigationView.getMenu().findItem(R.id.nav_inquiry).getIcon().setAlpha(125);
            navigationView.getMenu().findItem(R.id.nav_selector).setVisible(true);
            navigationView.getMenu().findItem(R.id.nav_selector).setEnabled(true);
            navigationView.getMenu().findItem(R.id.nav_selector).getIcon().setAlpha(255);
        }
    }

    private void refreshSaveButton() {
        if (PreferenceUtils.isEnquiryFormEntered()) {
            ivSaveLogo.setVisibility(View.GONE);
            ivSaveLogo.setEnabled(false);
            ivSaveLogo.setClickable(false);
        } else {
            ivSaveLogo.setVisibility(View.VISIBLE);
            ivSaveLogo.setEnabled(true);
            ivSaveLogo.setClickable(true);
        }
    }

    private void refreshUiUponEnquirySaved() {
        tvPick.setEnabled(false);
        updateInquirySelector();
        setFormPerson(people.get(people.size() - 1));
        tvPick.setEnabled(true);
    }

    public Prediction getPredictionData(boolean formEntered) {
        Prediction prediction;
        String enquiryDate = PreferenceUtils.getEnquiryDate();

        if (formEntered) {
            Person person = PreferenceUtils.getFormPerson();
            person.addAttribute("entered");
            prediction = divination.getPrediction(person, enquiryDate);
        } else
            prediction = divination.getPrediction(enquiryDate);
        return prediction;
    }

    private void setLinksToText(TextView textView, String s) {
        CharSequence sequence = SpannerHelper.fromHtml(s);
        SpannableStringBuilder sb = new SpannableStringBuilder(sequence);
        URLSpan[] urls = sb.getSpans(0, sequence.length(), URLSpan.class);

        for (URLSpan span : urls) {
            makeLinkClickable(sb, span);
        }
        textView.setText(sb, TextView.BufferType.SPANNABLE);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void makeLinkClickable(SpannableStringBuilder sb, final URLSpan span) {
        int start = sb.getSpanStart(span);
        int end = sb.getSpanEnd(span);
        int flags = sb.getSpanFlags(span);
        sb.setSpan(new ClickableSpan() {
            @Override
            public void onClick(View widget) {
                String link = StringHelper.substringAfterLast(span.getURL(), "/");

                if (link.equals("prediction")) {
                    Sound.play(MainActivity.this, "crack");
                    setLinksToText(tvPrediction, StringHelper.replaceBetweenZeroWidthSpaces(predictionHistory.getLatest().getFormattedContent(MainActivity.this), predictionHistory.getLatest().getComponents().get("fortuneCookie")));
                }
            }
        }, start, end, flags);
        sb.removeSpan(span);
    }

    private void calculateCompatibility() {
        pbWait.setMax(100);
        pbWait.setProgress(0);
        String initialName = StringHelper.trimToNull(atvInitialName.getText().toString());
        String finalName = StringHelper.trimToNull(atvFinalName.getText().toString());

        if (initialName != null && finalName != null) {
            if (initialName.equalsIgnoreCase(finalName)) {
                tvCompatibility.setText(SpannerHelper.fromHtml(getString(R.string.compatibility_result, "<font color=\"#6666FF\">" + 100 + "%</font>")));
                pbWait.setProgress(100);
            } else {
                String formattedText, tempName;

                if (initialName.compareTo(finalName) < 0) {
                    tempName = initialName;
                    initialName = finalName;
                    finalName = tempName;
                }
                long seed = LongHelper.getSeed(initialName + System.getProperty("line.separator") + finalName);
                r.bindSeed(seed);
                int compatibilityPoints;

                if ((compatibilityPoints = r.getInt(101)) == 0)
                    formattedText = "<font color=\"#7F79D1\">" + compatibilityPoints + "%</font>";
                else if (compatibilityPoints < 25)
                    formattedText = "<font color=\"#F94C4C\">" + compatibilityPoints + "%</font>";
                else if (compatibilityPoints < 50)
                    formattedText = "<font color=\"#FFA500\">" + compatibilityPoints + "%</font>";
                else if (compatibilityPoints < 75)
                    formattedText = "<font color=\"#F0EF2E\">" + compatibilityPoints + "%</font>";
                else if (compatibilityPoints < 100)
                    formattedText = "<font color=\"#2FCC2F\">" + compatibilityPoints + "%</font>";
                else
                    formattedText = "<font color=\"#6666FF\">" + compatibilityPoints + "%</font>";
                r.unbindSeed();
                tvCompatibility.setText(SpannerHelper.fromHtml(getString(R.string.compatibility_result, formattedText)));
                pbWait.setProgress(compatibilityPoints);
            }
        } else {
            tvCompatibility.setText(SpannerHelper.fromHtml(getString(R.string.compatibility_result, "<font color=\"#C0FF2B\">?</font>")));
            pbWait.setProgress(0);
        }
    }

    private void getName() {
        if (!obtaining) {
            obtaining = true;
            spnNameType.setEnabled(false);
            NameType nameType = NameType.values()[spnNameType.getSelectedItemPosition()];
            tvNameBox.setText(new GeneratorManager(new Locale("xx")).getNameGenerator().getNameOrRetry(nameType));
            spnNameType.setEnabled(true);
            obtaining = false;
        }
    }

    private int[] getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        System.out.println("Screen size: " + point.x + "x" + point.y);
        return new int[]{point.x, point.y};
    }
}

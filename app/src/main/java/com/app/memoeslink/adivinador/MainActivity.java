package com.app.memoeslink.adivinador;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
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
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;
import androidx.multidex.BuildConfig;

import com.easyandroidanimations.library.BounceAnimation;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;
import com.google.gson.reflect.TypeToken;
import com.memoeslink.generator.common.DateTimeHelper;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.GeneratorManager;
import com.memoeslink.generator.common.LongHelper;
import com.memoeslink.generator.common.NameType;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.Randomizer;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.common.TextProcessor;
import com.memoeslink.generator.common.ZeroWidthChar;
import com.networknt.schema.JsonSchema;
import com.networknt.schema.JsonSchemaFactory;
import com.networknt.schema.SpecVersion;
import com.networknt.schema.ValidationMessage;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import java.io.IOException;
import java.lang.reflect.Type;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import me.zhanghai.android.materialprogressbar.CircularProgressDrawable;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivity extends MenuActivity {
    private static final int[][] PARTICLE_COLORS = {{Color.BLUE, Color.argb(255, 0, 128, 255), Color.argb(255, 51, 153, 255), Color.argb(255, 0, 192, 199), Color.argb(125, 0, 128, 255), Color.argb(125, 51, 153, 255), Color.argb(125, 0, 192, 199)}, {Color.YELLOW, Color.argb(255, 251, 255, 147), Color.argb(255, 224, 228, 124), Color.argb(255, 155, 215, 93), Color.argb(255, 120, 168, 71), Color.argb(125, 251, 255, 147), Color.argb(125, 224, 228, 124), Color.argb(125, 155, 215, 93), Color.argb(125, 120, 168, 71)}};
    private static boolean forceEffects = true;
    private RelativeLayout rlAdContainer;
    private RelativeLayout rlHeader;
    private LinearLayout llConfetti;
    private LinearLayout llDataEntryHolder;
    private LinearLayout llReloadHolder;
    private LinearLayout llInquiryHolder;
    private LinearLayout llSelectorHolder;
    private LinearLayout llClearHolder;
    private AppCompatImageView ivFortuneTeller;
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
    private AppCompatSpinner spnDateType;
    private AppCompatSpinner spnNameType;
    private DatePickerDialog dpdEnquiryDate;
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
    private boolean recent = true;
    private boolean timerEnabled = false;
    private boolean measured = false;
    private boolean active = true;
    private boolean adAdded = false;
    private boolean busy = false;
    private boolean started = false;
    private boolean pending = false;
    private boolean obtaining = false;
    private Boolean adPaused = null;
    private int startingReading = -1;
    private int reading = -1;
    private int textType = 0;
    private int lastModified = 0;
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
    private String enquiryDate = "";
    private String currentName = "";
    private String currentSummary = "";
    private String lastDate = "";
    private long measuredTimes = 0L;
    private long confettiThrown = 0L;
    private Timer timer;
    private Methods methods;
    private Methods seededMethods;
    private Randomizer r;
    private FortuneTeller fortuneTeller;
    private Prediction prediction = null;
    private Prediction backupPrediction = null;
    private Hardware hardware;
    private SharedPreferences.OnSharedPreferenceChangeListener listener; //Declared as global to avoid destruction by JVM Garbage Collector

    private static void onInitializationComplete(InitializationStatus initializationStatus) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, MainActivity::onInitializationComplete);
        rlAdContainer = findViewById(R.id.ad_container);
        rlHeader = findViewById(R.id.main_header);
        llConfetti = findViewById(R.id.main_confetti_layout);
        llDataEntryHolder = findViewById(R.id.main_data_entry_holder);
        llReloadHolder = findViewById(R.id.main_reload_holder);
        llInquiryHolder = findViewById(R.id.main_inquiry_holder);
        llSelectorHolder = findViewById(R.id.main_selector_holder);
        llClearHolder = findViewById(R.id.main_clear_holder);
        ivFortuneTeller = findViewById(R.id.main_fortune_teller);
        ivFortuneTeller = findViewById(R.id.main_fortune_teller);
        spnDateType = findViewById(R.id.main_date_selector);
        tvPick = findViewById(R.id.main_pick);
        tvPick.setText(TextFormatter.fromHtml(getString(R.string.link, getString(R.string.pick))));
        tvDataEntry = findViewById(R.id.main_data_entry);
        tvDataEntry.setText(TextFormatter.fromHtml(getString(R.string.link, getString(R.string.data_entry))));
        tvReload = findViewById(R.id.main_reload);
        tvReload.setText(TextFormatter.fromHtml(getString(R.string.link, getString(R.string.reload))));
        tvInquiry = findViewById(R.id.main_inquiry);
        tvInquiry.setText(TextFormatter.fromHtml(getString(R.string.link, getString(R.string.inquiry, "…"))));
        tvSelector = findViewById(R.id.main_selector);
        tvSelector.setText(TextFormatter.fromHtml(getString(R.string.link, getString(R.string.selector))));
        tvClear = findViewById(R.id.main_clear);
        tvClear.setText(TextFormatter.fromHtml(getString(R.string.link, getString(R.string.clear))));
        tvPhrase = findViewById(R.id.main_fortune_teller_phrase);
        tvPersonInfo = findViewById(R.id.main_person);
        tvPersonInfo.setSelected(true);
        tvPrediction = findViewById(R.id.main_prediction);
        btDataEntry = findViewById(R.id.main_edit_button);
        btTextCopy = findViewById(R.id.main_copy_button);
        vMain = findViewById(R.id.main_view);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        vCompatibility = inflater.inflate(R.layout.dialog_compatibility, null);
        atvInitialName = vCompatibility.findViewById(R.id.dialog_name_field);
        atvFinalName = vCompatibility.findViewById(R.id.dialog_other_name_field);
        tvBinder = vCompatibility.findViewById(R.id.dialog_binder_text);
        tvBinder.setText(TextFormatter.fromHtml(getString(R.string.link, getString(R.string.binder))));
        tvCompatibility = vCompatibility.findViewById(R.id.dialog_text);
        pbWait = vCompatibility.findViewById(R.id.dialog_progress);
        vNameGenerator = inflater.inflate(R.layout.dialog_name_generation, null);
        spnNameType = vNameGenerator.findViewById(R.id.dialog_spinner);
        tvNameBox = vNameGenerator.findViewById(R.id.dialog_generated_name);
        tvTextCopy = vNameGenerator.findViewById(R.id.dialog_copy_text);
        tvTextCopy.setText(TextFormatter.fromHtml(getString(R.string.link, getString(R.string.action_copy))));
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

        //Load methods
        methods = new Methods(MainActivity.this);
        seededMethods = new Methods(MainActivity.this);
        r = new Randomizer();
        fortuneTeller = new FortuneTeller(MainActivity.this);
        hardware = new Hardware(MainActivity.this);

        //Request ads
        List<String> testDevices = new ArrayList<>();
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR);
        testDevices.add(hardware.getTestDeviceId());
        RequestConfiguration requestConfiguration = new RequestConfiguration.Builder().build();

        if (BuildConfig.DEBUG)
            requestConfiguration = new RequestConfiguration.Builder()
                    .setTestDeviceIds(testDevices)
                    .build();
        MobileAds.setRequestConfiguration(requestConfiguration);
        adRequest = new AdRequest.Builder().build();

        if (BuildConfig.DEBUG && !adRequest.isTestDevice(MainActivity.this))
            System.out.println("This device will not show test ads.");

        //Get a greeting, if enabled
        if (defaultPreferences.getBoolean("preference_greetingsEnabled", true))
            tvPhrase.setText(TextFormatter.fromHtml(fortuneTeller.greet()));
        else
            tvPhrase.setText(fortuneTeller.comment());

        //Change drawable for fortune teller
        ivFortuneTeller.setImageResource(fortuneTeller.getRandomAppearance());

        //Delete temporary preferences
        methods.deleteTemp();

        //Set adapters
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.date_options));
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnDateType.setAdapter(adapter);
        adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, getResources().getStringArray(R.array.name_type)) {
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
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spnNameType.setAdapter(adapter);
        spnNameType.setSelection(3);

        //Build dialogs
        ContextThemeWrapper wrapper = new ContextThemeWrapper(MainActivity.this, R.style.CustomDialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
        builder.setTitle(R.string.compatibility);
        builder.setNeutralButton(R.string.action_close, (dialog, which) -> compatibilityDialog.dismiss());
        builder.setView(vCompatibility);
        compatibilityDialog = builder.create();

        builder = new AlertDialog.Builder(wrapper);
        builder.setTitle(R.string.name_generation);
        builder.setPositiveButton(R.string.action_generate, null);
        builder.setNegativeButton(R.string.action_close, null);
        builder.setView(vNameGenerator);
        nameGeneratorDialog = builder.create();

        //Set listeners
        navigationView.setNavigationItemSelectedListener(item -> {
            if (defaultPreferences.getBoolean("preference_hideDrawer", true))
                closeDrawer();

            switch (item.getItemId()) {
                case R.id.nav_data_entry:
                    Intent i = new Intent(MainActivity.this, InputActivity.class);
                    startActivity(i);
                    break;
                case R.id.nav_reload:
                    reloadPrediction(false, false);
                    break;
                case R.id.nav_save:
                    tvPersonInfo.performClick();
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
                    compatibilityDialog.show();
                    compatibilityDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    break;
                case R.id.nav_name_generation:
                    nameGeneratorDialog.show();
                    break;
            }
            return false;
        });

        DatePickerDialog.OnDateSetListener dateSetListener = (dialog, year, monthOfYear, dayOfMonth) -> {
            enquiryDate = DateTimeHelper.getStrDate(year, monthOfYear + 1, dayOfMonth);
            preferences.putString("temp_enquiryDate", enquiryDate);

            if (!enquiryDate.equals(DateTimeHelper.getStrCurrentDate()))
                reloadPrediction(llReloadHolder.getVisibility() != View.VISIBLE, false);
        };
        enquiryDate = DateTimeHelper.getStrCurrentDate();

        tvPick.setOnClickListener(view -> {
            if (!dpdEnquiryDate.isVisible()) {
                LocalDate currentDate = LocalDate.now();
                dpdEnquiryDate = DatePickerDialog.newInstance(dateSetListener, currentDate.getYear(), currentDate.getMonthValue() - 1, currentDate.getDayOfMonth());
                dpdEnquiryDate.vibrate(true);

                if (!dpdEnquiryDate.isAdded() && !dpdEnquiryDate.isVisible()) {
                    try {
                        dpdEnquiryDate.show(getSupportFragmentManager(), "PICKER_TAG");
                        dpdEnquiryDate = null;
                    } catch (Exception ignored) {
                    }
                }
            }
        });

        spnDateType.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (parent != null && parent.getChildAt(0) != null)
                    ((TextView) parent.getChildAt(0)).setTextColor(Color.YELLOW);

                switch (position) {
                    case 1:
                        tvPick.setVisibility(View.VISIBLE);
                        break;
                    case 0:
                    default:
                        tvPick.setVisibility(View.GONE);
                        break;
                }
                enquiryDate = DateTimeHelper.getStrCurrentDate();
                preferences.putString("temp_enquiryDate", enquiryDate);

                if (!enquiryDate.equals(DateTimeHelper.getStrCurrentDate()))
                    reloadPrediction(llReloadHolder.getVisibility() != View.VISIBLE, false);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
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
            if (defaultPreferences.getStringAsInt("preference_fortuneTellerAspect", 1) != 0) {
                Sound.play(MainActivity.this, "jump");
                new BounceAnimation(ivFortuneTeller)
                        .setBounceDistance(7)
                        .setNumOfBounces(1)
                        .setDuration(150)
                        .animate();
            }
        });

        tvPersonInfo.setOnClickListener(v -> {
            if (people != null && savePerson(prediction.getPerson())) {
                showSimpleToast(MainActivity.this, getString(R.string.toast_enquiry_saved));

                if (isPredictionReloaded(people.get(people.size() - 1), true)) {
                    llInquiryHolder.setVisibility(View.GONE);
                    navigationView.getMenu().findItem(R.id.nav_inquiry).setEnabled(false);
                    navigationView.getMenu().findItem(R.id.nav_inquiry).getIcon().setAlpha(125);
                }
            } else
                showSimpleToast(MainActivity.this, getString(R.string.toast_enquiry_not_saved));
        });

        llDataEntryHolder.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, InputActivity.class);
            startActivity(i);
        });

        llReloadHolder.setOnClickListener(view -> reloadPrediction(false, false));

        llInquiryHolder.setOnClickListener(view -> {
            if (!busy) {
                if (isPredictionReloaded(people.get(0), false)) {
                    llInquiryHolder.setEnabled(false);
                    llInquiryHolder.setAlpha(0.7F);
                    navigationView.getMenu().findItem(R.id.nav_inquiry).setEnabled(false);
                    navigationView.getMenu().findItem(R.id.nav_inquiry).getIcon().setAlpha(125);
                }
            }
        });

        llSelectorHolder.setOnClickListener(view -> {
            if (dialog != null && !busy)
                dialog.show();
        });

        llClearHolder.setOnClickListener(view -> {
            methods.clearForm(); //Delete form data
            reloadPrediction(false, false);
        });

        btDataEntry.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, InputActivity.class);
            startActivity(i);
        });

        btTextCopy.setOnClickListener(view -> {
            if (prediction != null)
                copyTextToClipboard(prediction.getContent());
        });

        compatibilityDialog.setOnShowListener(dialog -> calculateCompatibility());

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

        tvBinder.setOnClickListener(v -> atvInitialName.setText(currentName));

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

        //Set listener to SharedPreferences
        listener = (prefs, key) -> {
            if (key.equals("peopleList")) {
                if (dialog != null && dialog.isShowing()) {
                    dialog.dismiss();
                    dialog = null;
                }
                //recreate();
                setDialog(); //Define Dialog
            }

            if (key.equals("nameList"))
                setAdapter(); //Get stored names
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onStart() {
        super.onStart();
        setAdapter(); //Get stored names

        //Request permissions
        ActivityResultLauncher<String[]> launcher = registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(), isGranted -> {
            if (isGranted.containsValue(false))
                showSimpleToast(MainActivity.this, getString(R.string.denied_contact_permission));
        });
        boolean permissionsGranted = true;
        String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION};

        for (String permission : permissions) {
            if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED)
                permissionsGranted = false;
        }

        if (!permissionsGranted)
            launcher.launch(new String[]{Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION});

        //Show full-screen ads
        showInterstitialAd();
    }

    @Override
    public void onResume() {
        super.onResume();
        active = true;
        pending = false;
        textType = defaultPreferences.getStringAsInt("preference_textType");
        Long seed = LongHelper.getSeed(preferences.getString("preference_seed"));

        if (seed != null && fortuneTeller.r.getSeed() != null && seed.equals(fortuneTeller.r.getSeed()))
            fortuneTeller = new FortuneTeller(MainActivity.this);

        //Restart Activity if required
        if (preferences.contains("temp_restartActivity")) {
            forceEffects = true;
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        preferences.remove("temp_restartActivity");

        if (defaultPreferences.getBoolean("preference_stickHeader"))
            rlHeader.setTag("sticky-hasTransparency-nonConstant");
        else
            rlHeader.setTag(null);

        //Stop TTS if it is disabled and continues talking
        if (speechAvailable && (defaultPreferences.getBoolean("preference_audioEnabled") || defaultPreferences.getBoolean("preference_voiceEnabled"))) {
            if (tts.isSpeaking())
                tts.stop();
        }

        //Show, avoid, or hide ads
        prepareAd(false);

        //Get a prediction for the fortune teller to display
        if (recent || (isDataValid() && (!getPreferencesPerson().getSummary().equals(currentSummary))) || (!isDataValid() && lastModified == 1)) {
            getPrediction();
            recent = false;
        }

        //Change drawable if the 'fortune teller aspect' preference was changed
        if (preferences.contains("temp_changeFortuneTeller")) {
            ivFortuneTeller.setImageResource(fortuneTeller.getRandomAppearance());
            preferences.remove("temp_changeFortuneTeller");
        }

        //Define Dialog
        setDialog();

        if (!timerEnabled) {
            timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    frequency = defaultPreferences.getStringAsInt("preference_refreshTime", 20);
                    refreshFrequency = defaultPreferences.getStringAsInt("preference_updateTime", 60);

                    if (resourceSeconds >= 1800)
                        resourceSeconds = 0;
                    else
                        resourceSeconds++;

                    if (seconds >= frequency && frequency != 0) {
                        runOnUiThread(() -> {
                            if (active)
                                vanishAndMaterializeView(tvPhrase); //Fade out and fade in the fortune teller's text.

                            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                                if (active) //Play sound, if active and enabled
                                    Sound.play(MainActivity.this, methods.getStrFromStrArrayRes(R.array.sound_names));

                                //Change drawable for the fortune teller
                                ivFortuneTeller.setImageResource(fortuneTeller.getRandomAppearance());

                                //Get random text for the fortune teller to show
                                List<String> phraseTypes = new ArrayList<>();

                                if (defaultPreferences.getBoolean("preference_greetingsEnabled", true)) {
                                    phraseTypes.add("greetings");
                                    phraseTypes.add("greetings");
                                }

                                if (defaultPreferences.getBoolean("preference_opinionsEnabled", true)) {
                                    phraseTypes.add("opinions");
                                    phraseTypes.add("opinions");
                                }

                                if (defaultPreferences.getBoolean("preference_phrasesEnabled", true)) {
                                    phraseTypes.add("phrases");
                                    phraseTypes.add("conversation");
                                }

                                if (phraseTypes.size() > 0) {
                                    int index = methods.r.getInt(phraseTypes.size());

                                    switch (phraseTypes.get(index)) {
                                        case "greetings":
                                            tvPhrase.setText(TextFormatter.fromHtml(fortuneTeller.greet()));
                                            break;
                                        case "opinions":
                                            tvPhrase.setText(TextFormatter.fromHtml(fortuneTeller.talkAboutSomeone()));
                                            break;
                                        case "phrases":
                                            tvPhrase.setText(TextFormatter.fromHtml(fortuneTeller.talk()));
                                            break;
                                        case "conversation":
                                            tvPhrase.setText(TextFormatter.fromHtml(fortuneTeller.talk(r.getInt(2, 3))));
                                            break;
                                        default:
                                            tvPhrase.setText("?");
                                            break;
                                    }
                                } else
                                    tvPhrase.setText(fortuneTeller.comment());

                                //Talk; if active, enabled and possible
                                if (active && (reading == -1 || reading == 0) && (textType == 0 || textType == 2)) {
                                    startingReading = 0;
                                    talk(tvPhrase.getText().toString());
                                }
                            }, 350);
                        });
                        seconds = 0;
                    } else {
                        if (seconds < 120)
                            seconds++;
                    }

                    if (updateSeconds >= refreshFrequency) {
                        if (lastModified != 1) //Get a prediction for the fortune teller to display, if possible
                            getPrediction();
                        else {
                            if (!lastDate.equals(DateTimeHelper.getStrCurrentDate()))
                                getPrediction();
                            else
                                updateSeconds = 0;
                        }
                    } else {
                        if (updateSeconds >= refreshFrequency / 2) {
                            if (!started && backupPrediction != null) {
                                started = true;

                                new Thread(() -> {
                                    backupPrediction = getPredictionData(false);
                                    started = false;
                                }).start();
                            }
                        }

                        if (updateSeconds < 600)
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
        active = false;
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
                    reading = -1;

                    //Talk; if active, enabled and possible
                    if (active && pending && !tvPersonInfo.getText().toString().isEmpty() && prediction != null && (textType == 1 || textType == 2)) {
                        startingReading = 1;
                        talk(tvPersonInfo.getText().toString() + ". " + prediction.getContent());
                        pending = false;
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    if (active) //Try to talk again, if active
                        talk(tvPersonInfo.getText().toString() + ". " + prediction.getContent());
                }

                @Override
                public void onStart(String utteranceId) {
                    reading = startingReading;
                }
            });
        }
    }

    private void prepareAd(boolean restarted) {
        if (adPaused == null || !adPaused) {
            if (defaultPreferences.getBoolean("preference_adsEnabled", true)) {
                if (restarted || !adAdded || defaultPreferences.getBoolean("temp_restartAds"))
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
                                imageView.setImageResource(R.drawable.cancel);
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
            //view.requestLayout();
            //view.getLayoutParams().height = 0; This is slower!
            adView.destroy();
            adView.destroyDrawingCache();
            rlAdContainer.setVisibility(View.GONE);
            rlAdContainer.removeAllViews();
            adView = null;
            defaultPreferences.remove("temp_restartAds");
            adAdded = false;
        }
    }

    private void showInterstitialAd() {
        if (defaultPreferences.getBoolean("preference_adsEnabled", true)) {
            methods.bindSeed(LongHelper.getSeed(DateTimeHelper.getStrCurrentDateTime() + System.getProperty("line.separator") + hardware.getDeviceId()));

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
            methods.unbindSeed();
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
            if (active || forceEffects)
                new Handler(Looper.getMainLooper()).postDelayed(this::waitTasks, 500);
        } else {
            if (active || forceEffects) {
                if (defaultPreferences.getBoolean("preference_particlesEnabled", true) && originInX != 0 && originInY != 0)
                    throwConfetti(originInX, originInY); //Start confetti animation

                if (defaultPreferences.getStringAsInt("preference_fortuneTellerAspect", 1) != 0) {
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
        final List<Bitmap> allPossibleConfetti = Utils.generateConfettiBitmaps(defaultPreferences.getStringAsInt("preference_fortuneTellerAspect") == 0 ? PARTICLE_COLORS[0] : PARTICLE_COLORS[1], 10 /* size */);
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

    private void setAdapter() {
        List<String> nameList = null;

        if (preferences.contains("nameList") && preferences.getStringSet("nameList", null).size() > 0)
            nameList = new ArrayList(preferences.getStringSet("nameList"));

        if (nameList != null && nameList.size() > 0) {
            String[] names = nameList.toArray(new String[0]);
            atvInitialName.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, names));
            atvFinalName.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, names));
        }
    }

    private void setDialog() {
        llInquiryHolder.setVisibility(View.GONE);
        tvInquiry.setText(TextFormatter.fromHtml(getString(R.string.link, getString(R.string.inquiry, "…"))));
        navigationView.getMenu().findItem(R.id.nav_inquiry).setTitle(getString(R.string.inquiry, "…")); //Changes to text won't be reflected until the Drawer item is updated
        navigationView.getMenu().findItem(R.id.nav_inquiry).setEnabled(false);
        navigationView.getMenu().findItem(R.id.nav_inquiry).getIcon().setAlpha(125);
        llSelectorHolder.setVisibility(View.GONE);
        navigationView.getMenu().findItem(R.id.nav_selector).setEnabled(false);
        navigationView.getMenu().findItem(R.id.nav_selector).getIcon().setAlpha(125);

        //Get stored enquiries
        if (StringHelper.isNotNullOrEmpty(preferences.getString("peopleList"))) {
            Gson gson = new GsonBuilder().registerTypeAdapter(LocalDate.class, (JsonDeserializer<LocalDate>) (json, typeOfT, context) -> LocalDate.parse(json.getAsJsonPrimitive().getAsString())).create();
            String json = preferences.getString("peopleList");
            Type type = new TypeToken<ArrayList<Person>>() {
            }.getType();

            //Validate JSON content
            boolean valid = false;

            validation:
            {
                if (json == null)
                    break validation;

                try {
                    JsonParser parser = new ObjectMapper().getFactory().createParser(json);

                    while (parser.nextToken() != null) {
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    break validation;
                }

                try {
                    JsonSchemaFactory factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V4);
                    JsonSchema schema = factory.getSchema(methods.getRawRes(R.raw.schema));
                    JsonNode node = new ObjectMapper().readTree(json);
                    Set<ValidationMessage> errors = schema.validate(node);

                    if (errors.size() > 1)
                        break validation;
                } catch (Exception e) {
                    break validation;
                }

                try {
                    people = gson.fromJson(json, type);
                } catch (Exception e) {
                    break validation;
                }
                valid = true;
            }

            if (!valid) {
                preferences.remove("peopleList");
                people.clear();
            }
        } else
            people.clear();

        //Set inquiry list
        if (people == null) {
        } else if (people.size() == 1) {
            tvInquiry.setText(TextFormatter.fromHtml(getString(R.string.link, getString(R.string.inquiry, people.get(0).getDescriptor()))));
            navigationView.getMenu().findItem(R.id.nav_inquiry).setTitle(getString(R.string.inquiry, people.get(0).getDescriptor())); //Changes to text won't be reflected until the Drawer item is updated

            if (isNoPersonTempStored()) {
                llInquiryHolder.setVisibility(View.VISIBLE);
                navigationView.getMenu().findItem(R.id.nav_inquiry).setEnabled(true);
                navigationView.getMenu().findItem(R.id.nav_inquiry).getIcon().setAlpha(255);
            }
        } else if (people.size() > 1) {
            List<String> items = new ArrayList<>();

            for (Person person : people) {
                items.add(person.getDescriptor() + " " + "(" + methods.getGenderName(person.getGender(), 3) + ")," + " " + person.getBirthdate());
            }

            //Define Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.alert_select_enquiry_title);
            builder.setNegativeButton(R.string.action_cancel, (dialogInterface, i) -> dialog.dismiss());
            final ListView listView = new ListView(MainActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, items);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                isPredictionReloaded(people.get(position), false);
                dialog.dismiss();
            });
            builder.setView(listView);
            dialog = builder.create();
            llSelectorHolder.setVisibility(View.VISIBLE);
            navigationView.getMenu().findItem(R.id.nav_selector).setEnabled(true);
            navigationView.getMenu().findItem(R.id.nav_selector).getIcon().setAlpha(255);
        }
    }

    private boolean isPredictionReloaded(Person person, boolean mute) {
        if (people.size() > 0 && isPersonNotTempStored(person)) {
            preferences.putString("temp_name", person.getDescriptor());
            preferences.putInt("temp_gender", person.getGender().getValue());
            preferences.putInt("temp_date_year", person.getBirthdate().getYear());
            preferences.putInt("temp_date_month", person.getBirthdate().getMonthValue());
            preferences.putInt("temp_date_day", person.getBirthdate().getDayOfMonth());
            preferences.putBoolean("temp_anonymous", person.hasAttribute("anonymous"));
            reloadPrediction(true, mute);
            return true;
        }
        return false;
    }

    private boolean isDataValid() {
        Object[] fields = new Object[]{
                preferences.getStringOrNull("temp_name"),
                preferences.getIntOrNull("temp_gender"),
                preferences.getIntOrNull("temp_date_year"),
                preferences.getIntOrNull("temp_date_month"),
                preferences.getIntOrNull("temp_date_day")
        };

        for (Object field : fields) {
            if (field == null)
                return false;
        }
        return true;
    }

    private void reloadPrediction(final boolean formCompleted, final boolean mute) {
        if (!busy) {
            busy = true;
            llConfetti.animate() //Fade particles layout out
                    .alpha(0.0f)
                    .setDuration(200)
                    .setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationCancel(Animator animation) {
                            super.onAnimationEnd(animation);
                            vMain.clearAnimation();
                            vMain.setVisibility(View.INVISIBLE);
                        }
                    });
            preferences.putBoolean("temp_busy", true);
            spnDateType.setEnabled(false);
            spnDateType.setClickable(false);
            tvPick.setEnabled(false);

            if (!mute)
                Sound.play(MainActivity.this, "wind");

            new Thread(() -> {
                backupPrediction = getPredictionData(formCompleted);
                getPrediction(); //Get a prediction for the fortune teller to display
            }).start();
        }
    }

    private void getPrediction() {
        MainActivity.this.runOnUiThread(() -> {
            updateSeconds = 0;
            boolean formCompleted = isDataValid();

            if (!llInquiryHolder.isEnabled()) {
                llInquiryHolder.setVisibility(View.GONE);
                llInquiryHolder.setAlpha(1.0F);
                llInquiryHolder.setEnabled(true);
            }

            if (formCompleted) {
                tvPersonInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                tvPersonInfo.setEnabled(false);
                tvPersonInfo.setClickable(false);
                navigationView.getMenu().findItem(R.id.nav_save).setEnabled(false);
                navigationView.getMenu().findItem(R.id.nav_save).getIcon().setAlpha(125);
                llDataEntryHolder.setEnabled(false);
                llDataEntryHolder.setVisibility(View.GONE);
                navigationView.getMenu().findItem(R.id.nav_data_entry).setEnabled(false);
                navigationView.getMenu().findItem(R.id.nav_data_entry).getIcon().setAlpha(125);
                llReloadHolder.setEnabled(false);
                llReloadHolder.setVisibility(View.GONE);
                navigationView.getMenu().findItem(R.id.nav_reload).setEnabled(false);
                navigationView.getMenu().findItem(R.id.nav_reload).getIcon().setAlpha(125);
                llClearHolder.setEnabled(true);
                llClearHolder.setVisibility(View.VISIBLE);
                navigationView.getMenu().findItem(R.id.nav_clear).setEnabled(true);
                navigationView.getMenu().findItem(R.id.nav_clear).getIcon().setAlpha(255);
            } else {
                tvPersonInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.save, 0, 0, 0);
                tvPersonInfo.setEnabled(true);
                tvPersonInfo.setClickable(true);
                navigationView.getMenu().findItem(R.id.nav_save).setEnabled(true);
                navigationView.getMenu().findItem(R.id.nav_save).getIcon().setAlpha(255);
                llDataEntryHolder.setEnabled(true);
                llDataEntryHolder.setVisibility(View.VISIBLE);
                navigationView.getMenu().findItem(R.id.nav_data_entry).setEnabled(true);
                navigationView.getMenu().findItem(R.id.nav_data_entry).getIcon().setAlpha(255);
                llReloadHolder.setEnabled(true);
                llReloadHolder.setVisibility(View.VISIBLE);
                navigationView.getMenu().findItem(R.id.nav_reload).setEnabled(true);
                navigationView.getMenu().findItem(R.id.nav_reload).getIcon().setAlpha(255);
                llClearHolder.setEnabled(false);
                llClearHolder.setVisibility(View.GONE);
                navigationView.getMenu().findItem(R.id.nav_clear).setEnabled(false);
                navigationView.getMenu().findItem(R.id.nav_clear).getIcon().setAlpha(125);
            }

            if (backupPrediction != null) {
                prediction = backupPrediction;
                backupPrediction = null;
            } else
                prediction = getPredictionData(formCompleted);

            //Show predictionView
            tvPersonInfo.setText(TextFormatter.fromHtml(getString(R.string.person_data,
                    enquiryDate.equals(DateTimeHelper.getStrCurrentDate()) ? getString(R.string.today) : enquiryDate,
                    methods.formatDescriptor(prediction.getPerson()),
                    methods.getGenderName(prediction.getPerson().getGender(), 2),
                    DateTimeHelper.getStrDate(prediction.getPerson().getBirthdate())
            )));
            setLinksToText(tvPrediction, prediction.getFormattedContent());

            if (people.size() == 1 && isNoPersonTempStored()) {
                llInquiryHolder.setVisibility(View.VISIBLE);
                navigationView.getMenu().findItem(R.id.nav_inquiry).setEnabled(true);
                navigationView.getMenu().findItem(R.id.nav_inquiry).getIcon().setAlpha(255);
            }
            preferences.remove("temp_busy");

            //Show person's name, if active and possible
            if (active && !isViewVisible(tvPersonInfo))
                showFormattedToast(MainActivity.this, TextFormatter.fromHtml(methods.formatDescriptor(prediction.getPerson())));

            //Talk; if active, enabled and possible
            if (recent)
                pending = true;
            else if (active && !tvPersonInfo.getText().toString().isEmpty() && prediction != null && (textType == 1 || textType == 2)) {
                if (reading == -1 || reading == 1) {
                    startingReading = 1;
                    talk(tvPersonInfo.getText().toString() + ". " + prediction.getContent());
                } else
                    pending = true;
            }
            spnDateType.setClickable(true);
            spnDateType.setEnabled(true);
            tvPick.setEnabled(true);
            busy = false;

            llConfetti.animate().alpha(1.0f).setDuration(200); //Fade particles layout in
        });
    }

    public Prediction getPredictionData(boolean formCompleted) {
        Prediction prediction = new Prediction();
        lastDate = DateTimeHelper.getStrCurrentDate();
        currentSummary = "";
        Person person;
        String name;

        if (formCompleted) {
            lastModified = 1;
            person = getPreferencesPerson();
            person.addAttribute("entered");
            name = person.getDescriptor();
            currentSummary = person.getSummary();
        } else {
            lastModified = 2;

            if (seededMethods.r.getInt(3) == 0) {
                person = seededMethods.getAnonymousPerson();
                name = person.getUsername();
            } else {
                person = seededMethods.getPerson();
                name = person.getFullName();
            }
        }
        prediction.setPerson(person);
        currentName = name;
        int birthdateYear = person.getBirthdate().getYear();
        int birthdateMonth = person.getBirthdate().getMonthValue();
        int birthdateDay = person.getBirthdate().getDayOfMonth();
        String birthdate = DateTimeHelper.getStrDate(birthdateYear, birthdateMonth, birthdateDay);
        Gender gender = person.getGender();

        //Get zodiac information
        ZodiacSign zodiacSign = methods.getZodiacSign(birthdateMonth, birthdateDay);
        WalterBergZodiacSign walterBergZodiacSign = methods.getWalterBergZodiacSign(birthdateMonth, birthdateDay);

        //Set seed
        String dailySeed = name + System.getProperty("line.separator") + enquiryDate + System.getProperty("line.separator") + birthdate + System.getProperty("line.separator") + gender.getGlyph();
        String uniqueSeed = name + System.getProperty("line.separator") + birthdate + System.getProperty("line.separator") + gender;
        long seed = LongHelper.getSeed(dailySeed);
        long personalSeed = LongHelper.getSeed(uniqueSeed);

        //Define HashMap
        HashMap<String, String> divination = new HashMap<>();

        //Get predictionView information
        seededMethods.bindSeed(seed);
        divination.put("link", String.format("<a href='links/prediction'>%s</a>", getString(R.string.prediction_action)));
        divination.put("fortuneCookie", seededMethods.getFortuneCookie());
        divination.put("gibberish", "<font color=#ECFE5B>" + seededMethods.formatText(TextProcessor.turnIntoGibberish(divination.get("fortuneCookie")), "i,tt") + "</font>");
        divination.put("divination", seededMethods.generateDivination());
        divination.put("fortuneNumbers", android.text.TextUtils.join(", ", seededMethods.r.getIntegers(5, 100, true)));
        divination.put("emotions", seededMethods.getEmotions());
        divination.put("bestTime", seededMethods.getTime());
        divination.put("worstTime", seededMethods.getTime());
        divination.put("characteristic", StringHelper.capitalizeFirst(seededMethods.getAbstractNoun()));
        divination.put("chainOfEvents", seededMethods.getChainOfEvents(person));
        divination.put("influence", seededMethods.formatName(seededMethods.getPerson()));

        //Get zodiac information
        divination.put("zodiacSign", zodiacSign.getName(MainActivity.this) + " " + zodiacSign.getEmoji());
        divination.put("astrologicalHouse", zodiacSign.getAstrologicalHouse(MainActivity.this));
        divination.put("ruler", zodiacSign.getRuler(MainActivity.this));
        divination.put("element", zodiacSign.getElement(MainActivity.this));
        divination.put("signColor", getString(R.string.zodiac_color, zodiacSign.getColor(MainActivity.this), zodiacSign.getHexColor()));
        divination.put("signNumbers", zodiacSign.getNumbers(MainActivity.this));
        divination.put("compatibility", zodiacSign.getCompatibility(MainActivity.this));
        divination.put("incompatibility", zodiacSign.getIncompatibility(MainActivity.this));
        divination.put("walterBergZodiacSign", walterBergZodiacSign.getName(MainActivity.this) + " " + walterBergZodiacSign.getEmoji());
        divination.put("chineseZodiacSign", methods.getChineseZodiacSign(birthdateYear));

        //Get identity information
        seededMethods.bindSeed(personalSeed);
        divination.put("color", seededMethods.getColor(uniqueSeed));
        divination.put("animal", StringHelper.capitalizeFirst(seededMethods.getStrFromStrArrayRes(R.array.animal)));
        divination.put("psychologicalType", seededMethods.getStrFromStrArrayRes(R.array.psychological_type));
        divination.put("secretName", seededMethods.formatName(seededMethods.getSecretName()));
        divination.put("demonicName", seededMethods.formatName(TextProcessor.demonize(name, seededMethods.getSecretName())));
        divination.put("previousName", seededMethods.formatName(seededMethods.getPerson()));
        divination.put("futureName", seededMethods.formatName(seededMethods.getPerson()));
        divination.put("recommendedUsername", seededMethods.formatUsername(seededMethods.getUsername()));
        divination.put("digit", String.valueOf(seededMethods.r.getInt(10)));
        divination.put("uniqueNumbers", android.text.TextUtils.join(", ", seededMethods.r.getIntegers(3, 1000, true)));
        divination.put("uniqueIdentifier", UUID.nameUUIDFromBytes(uniqueSeed.getBytes()).toString());
        divination.put("daysBetweenDates", String.valueOf(DateTimeHelper.getDifferenceInDays(birthdate, enquiryDate)));
        divination.put("timeBetweenDates", seededMethods.getDateDifference(birthdate, enquiryDate));
        seededMethods.unbindSeed();

        String content = getString(R.string.prediction,
                "",
                divination.get("fortuneCookie"),
                divination.get("divination"),
                divination.get("fortuneNumbers"),
                divination.get("emotions"),
                divination.get("worstTime"),
                divination.get("bestTime"),
                divination.get("characteristic"),
                divination.get("chainOfEvents"),
                divination.get("influence"),
                divination.get("zodiacSign"),
                divination.get("astrologicalHouse"),
                divination.get("ruler"),
                divination.get("element"),
                zodiacSign.getColor(MainActivity.this),
                divination.get("signNumbers"),
                divination.get("compatibility"),
                divination.get("incompatibility"),
                divination.get("walterBergZodiacSign"),
                divination.get("chineseZodiacSign"),
                "#FFFFFF", divination.get("color"),
                divination.get("animal"),
                divination.get("psychologicalType"),
                divination.get("secretName"),
                divination.get("demonicName"),
                divination.get("previousName"),
                divination.get("futureName"),
                divination.get("recommendedUsername"),
                Integer.valueOf(divination.get("digit")),
                divination.get("uniqueNumbers"),
                divination.get("uniqueIdentifier"),
                divination.get("daysBetweenDates"),
                divination.get("timeBetweenDates")
        );
        content = TextFormatter.fromHtml(content).toString();
        content = getString(R.string.enquiry_information, enquiryDate, name, methods.getGenderName(gender, 2), birthdate) +
                System.getProperty("line.separator") + System.getProperty("line.separator") +
                content;
        prediction.setContent(content);

        String formattedContent = getString(R.string.prediction,
                ZeroWidthChar.ZERO_WIDTH_SPACE.getCharacter() + divination.get("link"),
                divination.get("gibberish") + ZeroWidthChar.ZERO_WIDTH_SPACE.getCharacter(),
                divination.get("divination"),
                divination.get("fortuneNumbers"),
                divination.get("emotions"),
                divination.get("worstTime"),
                divination.get("bestTime"),
                divination.get("characteristic"),
                divination.get("chainOfEvents"),
                divination.get("influence"),
                divination.get("zodiacSign"),
                divination.get("astrologicalHouse"),
                divination.get("ruler"),
                divination.get("element"),
                divination.get("signColor"),
                divination.get("signNumbers"),
                divination.get("compatibility"),
                divination.get("incompatibility"),
                divination.get("walterBergZodiacSign"),
                divination.get("chineseZodiacSign"),
                divination.get("color"), "⬛&#xFE0E;",
                divination.get("animal"),
                divination.get("psychologicalType"),
                divination.get("secretName"),
                divination.get("demonicName"),
                divination.get("previousName"),
                divination.get("futureName"),
                divination.get("recommendedUsername"),
                Integer.valueOf(divination.get("digit")),
                divination.get("uniqueNumbers"),
                divination.get("uniqueIdentifier"),
                divination.get("daysBetweenDates"),
                divination.get("timeBetweenDates")
        );
        prediction.setFormattedContent(formattedContent);
        prediction.setFortuneCookie(divination.get("fortuneCookie"));
        prediction.setUnrevealedFortuneCookie(divination.get("gibberish"));
        return prediction;
    }

    private void setLinksToText(TextView textView, String s) {
        CharSequence sequence = TextFormatter.fromHtml(s);
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

        ClickableSpan clickable = new ClickableSpan() {
            @Override
            public void onClick(@NonNull View widget) {
                String link = StringHelper.substringAfterLast(span.getURL(), "/");

                if (link.equals("prediction")) {
                    Sound.play(MainActivity.this, "crack");
                    setLinksToText(tvPrediction, StringHelper.replaceBetweenZeroWidthSpaces(prediction.getFormattedContent(), prediction.getFortuneCookie()));
                }
            }
        };
        sb.setSpan(clickable, start, end, flags);
        sb.removeSpan(span);
    }

    private void calculateCompatibility() {
        pbWait.setMax(100);
        pbWait.setProgress(0);
        String initialName = StringHelper.trimToNull(atvInitialName.getText().toString());
        String finalName = StringHelper.trimToNull(atvFinalName.getText().toString());

        if (initialName != null && finalName != null) {
            if (initialName.equalsIgnoreCase(finalName)) {
                tvCompatibility.setText(TextFormatter.fromHtml(getString(R.string.compatibility_result, "<font color=#6666FF>" + 100 + "%</font>")));
                pbWait.setProgress(100);
            } else {
                String formattedText, tempName;
                int compatibilityPoints;

                if (initialName.compareTo(finalName) < 0) {
                    tempName = initialName;
                    initialName = finalName;
                    finalName = tempName;
                }
                long seed = LongHelper.getSeed(initialName + System.getProperty("line.separator") + finalName);
                r.bindSeed(seed);

                if ((compatibilityPoints = r.getInt(101)) == 0)
                    formattedText = "<font color=#7F79D1>" + compatibilityPoints + "%</font>";
                else if (compatibilityPoints < 25)
                    formattedText = "<font color=#F94C4C>" + compatibilityPoints + "%</font>";
                else if (compatibilityPoints < 50)
                    formattedText = "<font color=#FFA500>" + compatibilityPoints + "%</font>";
                else if (compatibilityPoints < 75)
                    formattedText = "<font color=#F0EF2E>" + compatibilityPoints + "%</font>";
                else if (compatibilityPoints < 100)
                    formattedText = "<font color=#2FCC2F>" + compatibilityPoints + "%</font>";
                else
                    formattedText = "<font color=#6666FF>" + compatibilityPoints + "%</font>";
                r.unbindSeed();
                tvCompatibility.setText(TextFormatter.fromHtml(getString(R.string.compatibility_result, formattedText)));
                pbWait.setProgress(compatibilityPoints);
            }
        } else {
            tvCompatibility.setText(TextFormatter.fromHtml(getString(R.string.compatibility_result, "<font color=#C0FF2B>?</font>")));
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

package com.app.memoeslink.adivinador;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.view.ContextThemeWrapper;
import androidx.appcompat.widget.AppCompatAutoCompleteTextView;
import androidx.appcompat.widget.AppCompatImageView;
import androidx.appcompat.widget.AppCompatSpinner;
import androidx.core.content.ContextCompat;

import com.easyandroidanimations.library.BounceAnimation;
import com.easyandroidanimations.library.FadeInAnimation;
import com.easyandroidanimations.library.FadeOutAnimation;
import com.github.jinatonic.confetti.ConfettiManager;
import com.github.jinatonic.confetti.ConfettiSource;
import com.github.jinatonic.confetti.ConfettoGenerator;
import com.github.jinatonic.confetti.Utils;
import com.github.jinatonic.confetti.confetto.BitmapConfetto;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.initialization.InitializationStatus;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;
import java.util.UUID;

import me.zhanghai.android.materialprogressbar.CircularProgressDrawable;
import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

public class MainActivity extends MenuActivity {
    private static final String[] sounds = {"blip1", "blip2", "bum", "chime_notification", "counter", "level_up", "notification", "notification_sound", "waterdrop"};
    private static final int PERMISSION_REQUEST = 1;
    private RelativeLayout adContainer;
    private RelativeLayout header;
    private LinearLayout confettiLayout;
    private LinearLayout dataEntryHolder;
    private LinearLayout reloadHolder;
    private LinearLayout inquiryHolder;
    private LinearLayout selectorHolder;
    private LinearLayout clearHolder;
    private AppCompatImageView fortuneTellerAspect;
    private AppCompatAutoCompleteTextView initialName;
    private AppCompatAutoCompleteTextView finalName;
    private TextView pick;
    private TextView dataEntry;
    private TextView reload;
    private TextView inquiry;
    private TextView selector;
    private TextView clear;
    private TextView phrase;
    private TextView personInfo;
    private TextView predictionView;
    private TextView binder;
    private TextView compatibility;
    private TextView copy;
    private EditText nameBox;
    private AppCompatSpinner dateSelector;
    private AppCompatSpinner nameTypeSelector;
    private DatePickerDialog date;
    private MaterialProgressBar progressBar;
    private Button button;
    private Button copyButton;
    private View view;
    private View compatibilityView;
    private View nameGenerationView;
    private AdView adView;
    private InterstitialAd interstitialAd;
    private AdRequest adRequest;
    private ConfettiManager confettiManager;
    private AlertDialog compatibilityDialog = null;
    private AlertDialog nameGenerationDialog = null;
    private Dialog dialog = null;
    private boolean current = true;
    private boolean same = false;
    private boolean shown = false;
    private boolean recent = true;
    private boolean timerEnabled = false;
    private boolean measured = false;
    private boolean active = true;
    private boolean adAdded = false;
    private boolean busy = false;
    private boolean started = false;
    private boolean enquiryAvailable = false;
    private boolean pending = false;
    private boolean obtaining = false;
    private static boolean forceEffects = true;
    private Boolean adPaused = null;
    private short startingReading = -1;
    private short reading = -1;
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
    private int[] currentDate = {-1, -1, -1};
    private static int[][] particleColors = new int[][]{{Color.BLUE, Color.argb(255, 0, 128, 255), Color.argb(255, 51, 153, 255), Color.argb(255, 0, 192, 199), Color.argb(125, 0, 128, 255), Color.argb(125, 51, 153, 255), Color.argb(125, 0, 192, 199)}, {Color.YELLOW, Color.argb(255, 251, 255, 147), Color.argb(255, 224, 228, 124), Color.argb(255, 155, 215, 93), Color.argb(255, 120, 168, 71), Color.argb(125, 251, 255, 147), Color.argb(125, 224, 228, 124), Color.argb(125, 155, 215, 93), Color.argb(125, 120, 168, 71)}};
    private String formattedDate = "";
    private String currentName = "";
    private String fakeIdentifier = "";
    private String lastDate = "";
    private String unrevealedFortuneCookie = "";
    private String fortuneCookie = "";
    private String initialNameText = "";
    private String finalNameText = "";
    private String[] sexArray;
    private String[] dateOptions;
    private String[] nameTypeOptions;
    private Object[] data = null;
    private Object[] backupData = null;
    private ArrayList<Enquiry> enquiryList;
    private Long measuredTimes = 0L;
    private Long confettiThrown = 0L;
    private Timer timer;
    private Methods methods;
    private Methods unseededMethods;
    private Methods seededMethods;
    private Randomizer randomizer;
    private Enum designationEnum = NameEnum.EMPTY;
    private FortuneTeller fortuneTeller;
    private Enquiry enquiry;
    private Person backupPerson = null;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    private static void onInitializationComplete(InitializationStatus initializationStatus) {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MobileAds.initialize(this, MainActivity::onInitializationComplete);
        tts = new TextToSpeech(this, this);
        preferences = getSharedPreferences(Methods.PREFERENCES, Activity.MODE_PRIVATE);
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId("ca-app-pub-9370416997367495/9592695166");
        adContainer = findViewById(R.id.ad_container);
        header = findViewById(R.id.main_header);
        confettiLayout = findViewById(R.id.main_confetti_layout);
        dataEntryHolder = findViewById(R.id.main_data_entry_holder);
        reloadHolder = findViewById(R.id.main_reload_holder);
        inquiryHolder = findViewById(R.id.main_inquiry_holder);
        selectorHolder = findViewById(R.id.main_selector_holder);
        clearHolder = findViewById(R.id.main_clear_holder);
        fortuneTellerAspect = findViewById(R.id.main_fortune_teller);
        fortuneTellerAspect = findViewById(R.id.main_fortune_teller);
        dateSelector = findViewById(R.id.main_date_selector);
        pick = findViewById(R.id.main_pick);
        pick.setText(Methods.fromHtml(String.format(getString(R.string.link), getString(R.string.pick))));
        dataEntry = findViewById(R.id.main_data_entry);
        dataEntry.setText(Methods.fromHtml(String.format(getString(R.string.link), getString(R.string.data_entry))));
        reload = findViewById(R.id.main_reload);
        reload.setText(Methods.fromHtml(String.format(getString(R.string.link), getString(R.string.reload))));
        inquiry = findViewById(R.id.main_inquiry);
        inquiry.setText(Methods.fromHtml(String.format(getString(R.string.link), String.format(getString(R.string.inquiry), "…"))));
        selector = findViewById(R.id.main_selector);
        selector.setText(Methods.fromHtml(String.format(getString(R.string.link), getString(R.string.selector))));
        clear = findViewById(R.id.main_clear);
        clear.setText(Methods.fromHtml(String.format(getString(R.string.link), getString(R.string.clear))));
        phrase = findViewById(R.id.main_fortune_teller_phrase);
        personInfo = findViewById(R.id.main_person);
        personInfo.setSelected(true);
        predictionView = findViewById(R.id.main_prediction);
        button = findViewById(R.id.main_edit_button);
        copyButton = findViewById(R.id.main_copy_button);
        view = findViewById(R.id.main_view);
        LayoutInflater inflater = MainActivity.this.getLayoutInflater();
        compatibilityView = inflater.inflate(R.layout.dialog_compatibility, null);
        initialName = compatibilityView.findViewById(R.id.dialog_name_field);
        finalName = compatibilityView.findViewById(R.id.dialog_other_name_field);
        binder = compatibilityView.findViewById(R.id.dialog_binder_text);
        binder.setText(Methods.fromHtml(String.format(getString(R.string.link), getString(R.string.binder))));
        compatibility = compatibilityView.findViewById(R.id.dialog_text);
        progressBar = compatibilityView.findViewById(R.id.dialog_progress);
        nameGenerationView = inflater.inflate(R.layout.dialog_name_generation, null);
        nameTypeSelector = nameGenerationView.findViewById(R.id.dialog_spinner);
        nameBox = nameGenerationView.findViewById(R.id.dialog_generated_name);
        copy = nameGenerationView.findViewById(R.id.dialog_copy_text);
        copy.setText(Methods.fromHtml(String.format(getString(R.string.link), getString(R.string.action_copy))));
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.navigation_view);
        navigationView.setItemIconTintList(null);

        //Initialize arrays
        sexArray = new String[]{getString(R.string.input_sex_indefinite), getString(R.string.input_sex_male), getString(R.string.input_sex_female)};
        dateOptions = getResources().getStringArray(R.array.date_options);
        nameTypeOptions = getResources().getStringArray(R.array.name_type_options);

        //Load methods
        methods = new Methods(MainActivity.this);
        unseededMethods = new Methods(MainActivity.this);
        seededMethods = new Methods(MainActivity.this);
        randomizer = new Randomizer();
        fortuneTeller = new FortuneTeller(MainActivity.this);

        //Request ads
        List<String> testDevices = new ArrayList<>();
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR);
        testDevices.add(methods.getTestDeviceId());
        RequestConfiguration requestConfiguration = new RequestConfiguration.Builder()
                .setTestDeviceIds(testDevices)
                .build();
        MobileAds.setRequestConfiguration(requestConfiguration);
        adRequest = new AdRequest.Builder().build();

        //Get a greeting, if enabled
        if (!defaultPreferences.getBoolean("preference_greetingsEnabled", true))
            phrase.setText(fortuneTeller.comment());
        else
            phrase.setText(Methods.fromHtml(fortuneTeller.greet()));

        //Change drawable for fortune teller
        fortuneTellerAspect.setImageResource(fortuneTeller.getRandomAppearance());

        //Delete temporary preferences
        methods.deleteTemp();

        //Set adapters
        ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_spinner_item, dateOptions);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        dateSelector.setAdapter(adapter);
        adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_spinner_item, nameTypeOptions) {
            private final int[] positions = {0, 1, NameEnum.values().length - 1};

            @Override
            public boolean isEnabled(int position) {
                return !ArrayUtils.contains(positions, position);
            }

            @Override
            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                View view = super.getDropDownView(position, convertView, parent);
                view.setTag(position);
                view.setAlpha(isEnabled(position) ? 1.0f : 0.35f);
                return view;
            }
        };
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        nameTypeSelector.setAdapter(adapter);
        nameTypeSelector.setSelection(2);

        //Build dialogs
        ContextThemeWrapper wrapper = new ContextThemeWrapper(MainActivity.this, R.style.CustomDialog);
        AlertDialog.Builder builder = new AlertDialog.Builder(wrapper);
        builder.setTitle(R.string.compatibility);
        builder.setNeutralButton(R.string.action_close, (dialog, which) -> compatibilityDialog.dismiss());
        builder.setView(compatibilityView);
        compatibilityDialog = builder.create();

        builder = new AlertDialog.Builder(wrapper);
        builder.setTitle(R.string.name_generation);
        builder.setPositiveButton(R.string.action_generate, null);
        builder.setNegativeButton(R.string.action_close, null);
        builder.setView(nameGenerationView);
        nameGenerationDialog = builder.create();

        //Get current date for DatePickerDialog
        currentDate = Methods.getCurrentDate();

        //Set listeners
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                interstitialAd.show();
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                System.out.println("The ad couldn't be loaded: " + errorCode);
                prepareAd(false); //Show, avoid, or hide ads
            }

            @Override
            public void onAdClosed() {
                prepareAd(false); //Show, avoid, or hide ads
                selector.invalidate(); //Force view to be redrawn
            }
        });

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
                    personInfo.performClick();
                    break;
                case R.id.nav_inquiry:
                    inquiryHolder.performClick();
                    break;
                case R.id.nav_selector:
                    selectorHolder.performClick();
                    break;
                case R.id.nav_clear:
                    clearHolder.performClick();
                    break;
                case R.id.nav_compatibility:
                    compatibilityDialog.show();
                    compatibilityDialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    break;
                case R.id.nav_name_generation:
                    nameGenerationDialog.show();
                    break;
            }
            return false;
        });

        pick.setOnClickListener(view -> {
            if (!shown) {
                date = DatePickerDialog.newInstance(dateSetListener, currentDate[0], currentDate[1], currentDate[2]);
                date.vibrate(true);

                date.setOnDismissListener(dialogInterface -> shown = false);

                if (!date.isAdded() && !date.isVisible()) {
                    try {
                        date.show(getSupportFragmentManager(), "PICKER_TAG");
                        shown = true;
                        date = null;
                    } catch (Exception ignored) {
                    }
                }
            }
        });

        dateSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (adapterView != null && adapterView.getChildAt(0) != null)
                    ((TextView) adapterView.getChildAt(0)).setTextColor(Color.YELLOW);

                switch (i) {
                    case 0:
                        pick.setVisibility(View.GONE);
                        current = true;
                        break;
                    case 1:
                        pick.setVisibility(View.VISIBLE);
                        current = false;
                        break;
                    default:
                        break;
                }

                if (!formattedDate.isEmpty() && !Methods.getFormattedDate(currentDate[0], currentDate[1] + 1, currentDate[2]).equals(methods.getDate())) {
                    backupData = null;
                    same = true;
                    reloadPrediction(reloadHolder.getVisibility() != View.VISIBLE, false);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            }
        });

        nameTypeSelector.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                List<Enum> designationTypes = new ArrayList<>();
                designationTypes.addAll(Arrays.asList(NameEnum.values()));
                designationTypes.addAll(Arrays.asList(PseudonymEnum.values()));
                designationEnum = designationTypes.get(position);
                obtainDesignation(); //Generate a random designation (name or pseudonym)
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });

        fortuneTellerAspect.setOnClickListener(view -> {
            if (Integer.parseInt(defaultPreferences.getString("preference_fortuneTellerAspect", "1")) != 0) {
                methods.playSound("jump");
                new BounceAnimation(fortuneTellerAspect)
                        .setBounceDistance(7)
                        .setNumOfBounces(1)
                        .setDuration(150)
                        .animate();
            }
        });

        personInfo.setOnClickListener(v -> {
            if (enquiryList != null) {
                if (methods.saveEnquiry(enquiryList, enquiry)) {
                    Methods.showSimpleToast(MainActivity.this, getString(R.string.toast_enquiry_saved));

                    if (getDataFromList(enquiryList.get(enquiryList.size() - 1), true)) {
                        inquiryHolder.setVisibility(View.GONE);
                        navigationView.getMenu().findItem(R.id.nav_inquiry).setEnabled(false);
                        navigationView.getMenu().findItem(R.id.nav_inquiry).getIcon().setAlpha(125);
                    }
                } else
                    Methods.showSimpleToast(MainActivity.this, getString(R.string.toast_enquiry_not_saved));
            }
        });

        dataEntryHolder.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, InputActivity.class);
            startActivity(i);
        });

        reloadHolder.setOnClickListener(view -> reloadPrediction(false, false));

        inquiryHolder.setOnClickListener(view -> {
            if (!busy) {
                if (getDataFromList(enquiryList.get(0), false)) {
                    inquiryHolder.setEnabled(false);
                    inquiryHolder.setAlpha(0.7F);
                    navigationView.getMenu().findItem(R.id.nav_inquiry).setEnabled(false);
                    navigationView.getMenu().findItem(R.id.nav_inquiry).getIcon().setAlpha(125);
                }
            }
        });

        selectorHolder.setOnClickListener(view -> {
            if (dialog != null && !busy)
                dialog.show();
        });

        clearHolder.setOnClickListener(view -> {
            methods.clearForm(); //Delete form data
            reloadPrediction(false, false);
        });

        button.setOnClickListener(view -> {
            Intent i = new Intent(MainActivity.this, InputActivity.class);
            startActivity(i);
        });

        copyButton.setOnClickListener(view -> {
            if (data != null && data.length >= 5)
                methods.copyTextToClipboard(data[1].toString());
        });

        compatibilityDialog.setOnShowListener(dialog -> calculateCompatibility());

        nameGenerationDialog.setOnShowListener(dialog -> {
            Button button = ((AlertDialog) dialog).getButton(AlertDialog.BUTTON_POSITIVE);
            button.setOnClickListener(view -> {
                obtainDesignation(); //Generate a random designation (name or pseudonym)
            });
        });

        initialName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                initialNameText = s.toString().trim();
                calculateCompatibility();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        finalName.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                finalNameText = s.toString().trim();
                calculateCompatibility();
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        binder.setOnClickListener(v -> initialName.setText(currentName));

        nameBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (StringUtils.isEmpty(s))
                    copy.setVisibility(View.GONE);
                else
                    copy.setVisibility(View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        copy.setOnClickListener(v -> methods.copyTextToClipboard(nameBox.getText().toString()));

        //Set listener to SharedPreferences
        listener = (prefs, key) -> {
            if (key.equals("enquiryList")) {
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            boolean permissionsGranted = true;
            String[] permissions = {Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_FINE_LOCATION};

            for (String permission : permissions) {
                if (ContextCompat.checkSelfPermission(MainActivity.this, permission) != PackageManager.PERMISSION_GRANTED)
                    permissionsGranted = false;
            }

            if (!permissionsGranted)
                requestPermissions(permissions, PERMISSION_REQUEST);
        }

        //Show full-screen ads
        showInterstitialAd();
    }

    @Override
    public void onResume() {
        super.onResume();
        active = true;
        pending = false;
        textType = Integer.parseInt(defaultPreferences.getString("preference_textType", "0"));
        enquiryList = new ArrayList<>();

        //Restart Activity if required
        if (preferences.contains("temp_restartActivity")) {
            forceEffects = true;
            Intent intent = getIntent();
            finish();
            startActivity(intent);
        }
        preferences.edit().remove("temp_restartActivity").commit();

        if (!defaultPreferences.getBoolean("preference_stickHeader", false))
            header.setTag(null);
        else
            header.setTag("sticky-hasTransparency-nonConstant");

        //Stop TTS if it is disabled and continues talking
        if (available && (defaultPreferences.getBoolean("preference_audioEnabled", false) || defaultPreferences.getBoolean("preference_voiceEnabled", false))) {
            if (tts.isSpeaking())
                tts.stop();
        }

        //Show, avoid, or hide ads
        prepareAd(false);

        //Get a prediction for the fortune teller to display
        if (recent || (verifyFields() && (!getFakeIdentifier().equals(fakeIdentifier))) || (!verifyFields() && lastModified == 1)) {
            getPrediction();
            recent = false;
        }

        //Change drawable if the 'fortune teller aspect' preference was changed
        if (preferences.contains("temp_changeFortuneTeller")) {
            fortuneTellerAspect.setImageResource(fortuneTeller.getRandomAppearance());
            preferences.edit().remove("temp_changeFortuneTeller").commit();
        }

        //Define Dialog
        setDialog();

        if (!timerEnabled) {
            timer = new Timer();

            timer.schedule(new TimerTask() {
                @Override
                public void run() {
                    frequency = Integer.parseInt(defaultPreferences.getString("preference_refreshTime", "20"));
                    refreshFrequency = Integer.parseInt(defaultPreferences.getString("preference_updateTime", "60"));

                    if (resourceSeconds >= 1800) {
                        Methods.language = Methods.getDefaultLanguage();
                        Methods.networkCountry = methods.getNetworkCountry();
                        Methods.locales = Locale.getAvailableLocales();
                        Methods.supportedNames = seededMethods.getPermittedNames(Methods.supportedNames, false);
                        resourceSeconds = 0;
                    } else
                        resourceSeconds++;

                    if (seconds >= frequency && frequency != 0) {
                        runOnUiThread(() -> {
                            if (active)
                                methods.vanishAndMaterialize(phrase); //Fade out and fade in the fortune teller's text.

                            new Handler().postDelayed(() -> {
                                int soundPosition = unseededMethods.getRandomizer().getInt(sounds.length, 0);

                                //Play sound, if active and enabled
                                if (active)
                                    methods.playSound(sounds[soundPosition]);

                                //Change drawable for the fortune teller
                                fortuneTellerAspect.setImageResource(fortuneTeller.getRandomAppearance());

                                //Get random text for the fortune teller to show
                                List<String> phraseList = new ArrayList<>();

                                if (defaultPreferences.getBoolean("preference_greetingsEnabled", true))
                                    phraseList.add("greetings");

                                if (defaultPreferences.getBoolean("preference_opinionsEnabled", true))
                                    phraseList.add("opinions");

                                if (defaultPreferences.getBoolean("preference_phrasesEnabled", true))
                                    phraseList.add("phrases");

                                if (phraseList.size() > 0) {
                                    int index = methods.getRandomizer().getInt(phraseList.size(), 0);

                                    switch (phraseList.get(index)) {
                                        case "greetings":
                                            phrase.setText(Methods.fromHtml(fortuneTeller.greet()));
                                            break;
                                        case "opinions":
                                            phrase.setText(Methods.fromHtml(fortuneTeller.talkAboutSomeone()));
                                            break;
                                        case "phrases":
                                            phrase.setText(Methods.fromHtml(fortuneTeller.talk()));
                                            break;
                                        default:
                                            phrase.setText("?");
                                            break;
                                    }
                                } else
                                    phrase.setText(fortuneTeller.comment());

                                //Talk; if active, enabled and possible
                                if (active && (reading == (short) -1 || reading == (short) 0) && (textType == 0 || textType == 2)) {
                                    startingReading = (short) 0;
                                    talk(phrase.getText().toString());
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
                            if (!lastDate.equals(methods.getDate()))
                                getPrediction();
                            else
                                updateSeconds = 0;
                        }
                    } else {
                        if (updateSeconds >= refreshFrequency / 2 && updateSeconds < refreshFrequency) {
                            if (!started && backupData != null) {
                                started = true;

                                new Thread(() -> {
                                    retrievePredictionData(true, false);
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
        if (closeDrawer()) {
        } else {
            new AlertDialog.Builder(this)
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
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_REQUEST: {
                if (grantResults != null && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                    methods.getContactNames();
                else
                    Methods.showSimpleToast(MainActivity.this, getString(R.string.denied_contact_permission));
            }
        }
    }

    @Override
    public void onInit(int i) {
        super.onInit(i);

        if (available) {
            tts.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                @Override
                public void onDone(String utteranceId) {
                    reading = -1;

                    //Talk; if active, enabled and possible
                    if (active && pending && !personInfo.getText().toString().isEmpty() && data != null && data.length >= 5 && (textType == 1 || textType == 2)) {
                        startingReading = (short) 1;
                        talk(personInfo.getText().toString() + "." + " " + data[1].toString());
                        pending = false;
                    }
                }

                @Override
                public void onError(String utteranceId) {
                    if (active) //Try to talk again, if active
                        talk(personInfo.getText().toString() + "." + " " + data[1].toString());
                }

                @Override
                public void onStart(String utteranceId) {
                    reading = startingReading;
                }
            });
        }
    }

    DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePickerDialog dialog, int year, int monthOfYear, int dayOfMonth) {
            currentDate = new int[]{year, monthOfYear, dayOfMonth};

            if (!formattedDate.isEmpty() && !formattedDate.equals(Methods.getFormattedDate(currentDate[0], currentDate[1] + 1, currentDate[2]))) {
                backupData = null;
                same = true;
                reloadPrediction(reloadHolder.getVisibility() != View.VISIBLE, false);
            }
        }
    };

    private void prepareAd(boolean restarted) {
        if (adPaused == null || (adPaused != null && !adPaused)) {
            if (defaultPreferences.getBoolean("preference_adsEnabled", true)) {
                if (restarted || !adAdded || defaultPreferences.getBoolean("temp_restartAds", false))
                    destroyAd();

                if (!adAdded) {
                    adView = new AdView(MainActivity.this);
                    adView.setAdSize(AdSize.BANNER);
                    adView.setAdUnitId("ca-app-pub-9370416997367495/4952493068");

                    //Set observer to get ad view height
                    ViewTreeObserver viewTreeObserver = adView.getViewTreeObserver();
                    viewTreeObserver.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                        @Override
                        public void onGlobalLayout() {
                            try {
                                ViewGroup.LayoutParams params = view.getLayoutParams();
                                params.height = adView.getMeasuredHeight();
                                view.requestLayout();
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
                                adContainer.addView(progressBar, params);

                                // Define AdView
                                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                params.addRule(RelativeLayout.CENTER_IN_PARENT);
                                adContainer.addView(adView, params);
                                adContainer.setVisibility(View.VISIBLE);

                                //Define button to close ad
                                params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                                params.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                params.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
                                AppCompatImageView imageView = new AppCompatImageView(MainActivity.this);
                                imageView.setAlpha(0.8F);
                                imageView.setImageResource(R.drawable.cancel);
                                adContainer.addView(imageView, params);

                                //Set listener
                                imageView.setOnClickListener(view -> destroyAd());
                                adAdded = true;
                            }
                        }

                        public void onAdFailedToLoad(int errorCode) {
                            System.out.println("The ad couldn't be loaded: " + errorCode);
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
            ViewGroup.LayoutParams params = view.getLayoutParams();
            params.height = 0;
            //view.requestLayout();
            //view.getLayoutParams().height = 0; This is slower!
            adView.destroy();
            adView.destroyDrawingCache();
            adContainer.setVisibility(View.GONE);
            adContainer.removeAllViews();
            adView = null;
            defaultPreferences.edit().remove("temp_restartAds");
            adAdded = false;
        }
    }

    private void showInterstitialAd() {
        if (defaultPreferences.getBoolean("preference_adsEnabled", true)) {
            methods.getRandomizer().bindSeed(Methods.getSeed(Methods.getCurrentDateTime() + System.getProperty("line.separator") + methods.getDeviceId()));

            if (randomizer.getInt(20, 0) == 0) {
                if (adPaused == null)
                    adPaused = true;

                MainActivity.this.runOnUiThread(() -> {
                    if (adView != null && adView.getVisibility() == View.VISIBLE)
                        destroyAd();
                    interstitialAd.loadAd(adRequest);
                });
            }
            methods.getRandomizer().bindSeed(null);
        }
    }

    private void prepareAnimation() {
        Methods.lockScreenOrientation(MainActivity.this); //Lock orientation
        stopConfetti(); //Finish previous confetti animation;

        //Redraw view
        confettiLayout.requestLayout();
        confettiLayout.invalidate();

        //Wait for measurement to be done
        int[] size = getScreenSize();
        originInX = size[0] / 2;
        originInY = size[1] / 2;
        measured = false;
        waitTasks();

        if (measuredTimes == 0L)
            confettiLayout.post(this::measureLayout); //Measure view after layout is completely loaded
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

                if (Integer.parseInt(defaultPreferences.getString("preference_fortuneTellerAspect", "1")) != 0) {
                    methods.playSound("jump");
                    new BounceAnimation(fortuneTellerAspect)
                            .setBounceDistance(20)
                            .setNumOfBounces(1)
                            .setDuration(500)
                            .animate();
                }
                Methods.unlockScreenOrientation(MainActivity.this); //Unlock orientation
                forceEffects = false;
            }
        }
    }

    private void measureLayout() {
        width = confettiLayout.getWidth();
        height = confettiLayout.getHeight();
        originInX = width / 2;
        originInY = height / 2;
        measured = true;
        System.out.println("Confetti layout measures: " + width + "x" + height + " [Confetti origin in: " + originInX + " axis X; " + originInY + " axis Y]");
    }

    private void throwConfetti(int x, int y) {
        final List<Bitmap> allPossibleConfetti = Utils.generateConfettiBitmaps(Integer.parseInt(defaultPreferences.getString("preference_fortuneTellerAspect", "0")) == 0 ? particleColors[0] : particleColors[1], 10 /* size */);
        final int numConfetti = allPossibleConfetti.size();

        ConfettoGenerator confettoGenerator = random -> {
            final Bitmap bitmap = allPossibleConfetti.get(random.nextInt(numConfetti));
            return new BitmapConfetto(bitmap);
        };

        confettiManager = new ConfettiManager(this, confettoGenerator, new ConfettiSource(x, y), confettiLayout)
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
        ArrayList<String> nameList = null;

        if (preferences.contains("nameList") && preferences.getStringSet("nameList", null).size() > 0)
            nameList = new ArrayList(preferences.getStringSet("nameList", null));

        if (nameList != null && nameList.size() > 0) {
            String[] names = nameList.toArray(new String[0]);
            initialName.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, names));
            finalName.setAdapter(new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_dropdown_item_1line, names));
        }
    }

    private void setDialog() {
        enquiryAvailable = false;
        inquiryHolder.setVisibility(View.GONE);
        inquiry.setText(Methods.fromHtml(String.format(getString(R.string.link), String.format(getString(R.string.inquiry), "…"))));
        navigationView.getMenu().findItem(R.id.nav_inquiry).setTitle(String.format(getString(R.string.inquiry), "…")); //Changes to text won't be reflected until the Drawer item is updated
        navigationView.getMenu().findItem(R.id.nav_inquiry).setEnabled(false);
        navigationView.getMenu().findItem(R.id.nav_inquiry).getIcon().setAlpha(125);
        selectorHolder.setVisibility(View.GONE);
        navigationView.getMenu().findItem(R.id.nav_selector).setEnabled(false);
        navigationView.getMenu().findItem(R.id.nav_selector).getIcon().setAlpha(125);

        if (preferences.getString("enquiryList", null) != null) { //Get enquiries
            Gson gson = new Gson();
            String json = preferences.getString("enquiryList", null);
            Type type = new TypeToken<ArrayList<Enquiry>>() {
            }.getType();
            enquiryList = gson.fromJson(json, type);
        }

        //Set inquiry list
        if (enquiryList != null && enquiryList.size() > 1) {
            List<String> items = new ArrayList<>();

            for (int n = 0; n < enquiryList.size(); n++) {
                String date = enquiryList.get(n).getYear() + "/" + String.format("%02d", enquiryList.get(n).getMonth() + 1) + "/" + String.format("%02d", enquiryList.get(n).getDay());
                items.add(enquiryList.get(n).getName() + " " + "(" + sexArray[enquiryList.get(n).getSex()].charAt(0) + ")," + " " + date);
            }

            //Define Dialog
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(R.string.alert_select_enquiry_title);
            builder.setNegativeButton(R.string.action_cancel, (dialogInterface, i) -> dialog.dismiss());
            final ListView listView = new ListView(MainActivity.this);
            ArrayAdapter<String> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, items);
            listView.setAdapter(adapter);

            listView.setOnItemClickListener((parent, view, position, id) -> {
                getDataFromList(enquiryList.get(position), false);
                dialog.dismiss();
            });
            builder.setView(listView);
            dialog = builder.create();
            selectorHolder.setVisibility(View.VISIBLE);
            navigationView.getMenu().findItem(R.id.nav_selector).setEnabled(true);
            navigationView.getMenu().findItem(R.id.nav_selector).getIcon().setAlpha(255);
        } else if (enquiryList != null && enquiryList.size() == 1) {
            Boolean temp;
            enquiryAvailable = true;
            inquiry.setText(Methods.fromHtml(String.format(getString(R.string.link), String.format(getString(R.string.inquiry), enquiryList.get(0).getName()))));
            navigationView.getMenu().findItem(R.id.nav_inquiry).setTitle(String.format(getString(R.string.inquiry), enquiryList.get(0).getName())); //Changes to text won't be reflected until the Drawer item is updated

            if (enquiryList != null && enquiryList.size() > 0 && (temp = isDataDifferent(enquiryList.get(0))) != null && temp) {
                inquiryHolder.setVisibility(View.VISIBLE);
                navigationView.getMenu().findItem(R.id.nav_inquiry).setEnabled(true);
                navigationView.getMenu().findItem(R.id.nav_inquiry).getIcon().setAlpha(255);
            }
        }
    }

    private Boolean isDataDifferent(Enquiry enquiry) {
        if (enquiry != null) {
            Object[] item = new Object[]{
                    enquiry.getName(),
                    enquiry.getSex(),
                    enquiry.getYear(),
                    enquiry.getMonth(),
                    enquiry.getDay()
            };

            Object[] object = new Object[]{
                    preferences.getString("temp_name", ""),
                    preferences.getInt("temp_sex", Methods.DEFAULT_SEX),
                    preferences.getInt("temp_date_year", Methods.DEFAULT_YEAR),
                    preferences.getInt("temp_date_month", Methods.DEFAULT_MONTH),
                    preferences.getInt("temp_date_day", Methods.DEFAULT_DAY)
            };
            return !Arrays.equals(item, object);
        } else return null;
    }

    private boolean getDataFromList(Enquiry enquiry, boolean mute) {
        Boolean temp;

        if (enquiryList != null && enquiryList.size() > 0 && (temp = isDataDifferent(enquiry)) != null && temp) {
            preferences.edit().putString("temp_name", enquiry.getName()).apply();
            preferences.edit().putString("temp_formatted_name", enquiry.getFormattedName()).apply();
            preferences.edit().putInt("temp_sex", enquiry.getSex()).apply();
            preferences.edit().putInt("temp_date_year", enquiry.getYear()).apply();
            preferences.edit().putInt("temp_date_month", enquiry.getMonth()).apply();
            preferences.edit().putInt("temp_date_day", enquiry.getDay()).apply();
            preferences.edit().putBoolean("temp_user", enquiry.isUser()).apply();
            preferences.edit().putBoolean("temp_anonymous", enquiry.isAnonymous() == null ? false : enquiry.isAnonymous()).apply();
            reloadPrediction(true, mute);
            return true;
        } else return false;
    }

    private boolean verifyFields() {
        boolean valid = true;
        Object[] fields = new Object[5];
        fields[0] = preferences.getString("temp_name", "");
        fields[1] = preferences.getInt("temp_sex", -1);
        fields[2] = preferences.getInt("temp_date_year", -1);
        fields[3] = preferences.getInt("temp_date_month", -1);
        fields[4] = preferences.getInt("temp_date_day", -1);

        for (int n = 0; n < fields.length; n++) {
            int dataType;

            if (fields[n].getClass() == Integer.class)
                dataType = 0;
            else if (fields[n].getClass() == String.class)
                dataType = 1;
            else
                dataType = -1;

            if (fields[n] == null)
                valid = false;
            else {
                if (dataType == 0 && (int) fields[n] == -1)
                    valid = false;

                if (dataType == 1 && fields[n].toString().equals(""))
                    valid = false;
            }
        }
        return valid;
    }

    private String getFakeIdentifier() {
        return preferences.getInt("temp_date_year", Methods.DEFAULT_YEAR) + "/" + String.format("%02d", (preferences.getInt("temp_date_month", Methods.DEFAULT_MONTH) + 1)) + "/" + String.format("%02d", preferences.getInt("temp_date_day", Methods.DEFAULT_DAY)) + System.getProperty("line.separator") + preferences.getInt("temp_sex", Methods.DEFAULT_SEX) + System.getProperty("line.separator") + preferences.getString("temp_name", "?");
    }

    private void reloadPrediction(final boolean formCompleted, final boolean mute) {
        if (!busy) {
            busy = true;
            new FadeOutAnimation(confettiLayout).setDuration(200).animate();
            preferences.edit().putBoolean("temp_busy", true).apply();
            dateSelector.setEnabled(false);
            dateSelector.setClickable(false);
            pick.setEnabled(false);

            if (!mute)
                methods.playSound("wind");

            new Thread(() -> {
                retrievePredictionData(true, formCompleted);
                getPrediction(); //Get a prediction for the fortune teller to display
            }).start();
        }
    }

    private void getPrediction() {
        MainActivity.this.runOnUiThread(() -> {
            updateSeconds = 0;
            boolean formCompleted = verifyFields();

            if (!inquiryHolder.isEnabled()) {
                inquiryHolder.setVisibility(View.GONE);
                inquiryHolder.setAlpha(1.0F);
                inquiryHolder.setEnabled(true);
            }

            if (formCompleted) {
                personInfo.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0);
                personInfo.setEnabled(false);
                personInfo.setClickable(false);
                navigationView.getMenu().findItem(R.id.nav_save).setEnabled(false);
                navigationView.getMenu().findItem(R.id.nav_save).getIcon().setAlpha(125);
                dataEntryHolder.setEnabled(false);
                dataEntryHolder.setVisibility(View.GONE);
                navigationView.getMenu().findItem(R.id.nav_data_entry).setEnabled(false);
                navigationView.getMenu().findItem(R.id.nav_data_entry).getIcon().setAlpha(125);
                reloadHolder.setEnabled(false);
                reloadHolder.setVisibility(View.GONE);
                navigationView.getMenu().findItem(R.id.nav_reload).setEnabled(false);
                navigationView.getMenu().findItem(R.id.nav_reload).getIcon().setAlpha(125);
                clearHolder.setEnabled(true);
                clearHolder.setVisibility(View.VISIBLE);
                navigationView.getMenu().findItem(R.id.nav_clear).setEnabled(true);
                navigationView.getMenu().findItem(R.id.nav_clear).getIcon().setAlpha(255);
            } else {
                personInfo.setCompoundDrawablesWithIntrinsicBounds(R.drawable.save, 0, 0, 0);
                personInfo.setEnabled(true);
                personInfo.setClickable(true);
                navigationView.getMenu().findItem(R.id.nav_save).setEnabled(true);
                navigationView.getMenu().findItem(R.id.nav_save).getIcon().setAlpha(255);
                dataEntryHolder.setEnabled(true);
                dataEntryHolder.setVisibility(View.VISIBLE);
                navigationView.getMenu().findItem(R.id.nav_data_entry).setEnabled(true);
                navigationView.getMenu().findItem(R.id.nav_data_entry).getIcon().setAlpha(255);
                reloadHolder.setEnabled(true);
                reloadHolder.setVisibility(View.VISIBLE);
                navigationView.getMenu().findItem(R.id.nav_reload).setEnabled(true);
                navigationView.getMenu().findItem(R.id.nav_reload).getIcon().setAlpha(255);
                clearHolder.setEnabled(false);
                clearHolder.setVisibility(View.GONE);
                navigationView.getMenu().findItem(R.id.nav_clear).setEnabled(false);
                navigationView.getMenu().findItem(R.id.nav_clear).getIcon().setAlpha(125);
            }

            if (backupData != null) {
                data = backupData;
                backupData = null;
            } else
                data = retrievePredictionData(false, formCompleted);

            //Display predictionView
            personInfo.setText(Methods.fromHtml(String.format(getString(R.string.person_data), formattedDate.equals(methods.getDate()) ? getString(R.string.today) : formattedDate, data[2].toString(), methods.swapFirstToLowercase(sexArray[(Integer) data[3]]), data[4].toString())));
            setTextAsLinkified(predictionView, data[0].toString());

            if (enquiryAvailable) {
                Boolean temp;

                if (enquiryList != null && enquiryList.size() > 0 && (temp = isDataDifferent(enquiryList.get(0))) != null && temp) {
                    inquiryHolder.setVisibility(View.VISIBLE);
                    navigationView.getMenu().findItem(R.id.nav_inquiry).setEnabled(true);
                    navigationView.getMenu().findItem(R.id.nav_inquiry).getIcon().setAlpha(255);
                }
            }
            preferences.edit().remove("temp_busy").apply();

            //Display name of the person, if active and possible
            if (active && !isVisible(personInfo))
                Methods.showFormattedToast(MainActivity.this, Methods.fromHtml(data[2].toString()));

            //Talk; if active, enabled and possible
            if (recent)
                pending = true;
            else if (active && !personInfo.getText().toString().isEmpty() && data != null && data.length >= 5 && (textType == 1 || textType == 2)) {
                if (reading == (short) -1 || reading == (short) 1) {
                    startingReading = (short) 1;
                    talk(personInfo.getText().toString() + "." + " " + data[1].toString());
                } else
                    pending = true;
            }
            dateSelector.setClickable(true);
            dateSelector.setEnabled(true);
            pick.setEnabled(true);
            busy = false;

            confettiLayout.post(() -> new FadeInAnimation(confettiLayout).setDuration(200).animate());
        });
    }

    public Object[] retrievePredictionData(boolean backup, boolean formCompleted) {
        Object[] data = new Object[5];
        lastDate = methods.getDate();
        int year, month, day;
        int sex;
        int zodiacIndex, newZodiacIndex;
        Boolean anonymous = null;
        boolean user = false;
        String name, simpleName, formattedName;
        String date;
        String prediction = null, clipboardPrediction = null;

        if (formCompleted) {
            lastModified = 1;
            fakeIdentifier = getFakeIdentifier();
            name = simpleName = preferences.getString("temp_name", "?");
            sex = preferences.getInt("temp_sex", Methods.DEFAULT_SEX);
            year = preferences.getInt("temp_date_year", Methods.DEFAULT_YEAR);
            month = preferences.getInt("temp_date_month", Methods.DEFAULT_MONTH) + 1;
            day = preferences.getInt("temp_date_day", Methods.DEFAULT_DAY);

            if (preferences.getBoolean("temp_user", true)) {
                formattedName = Methods.formatText(new String[]{name}, "", "b,i");
                user = true;
            } else
                formattedName = preferences.getString("temp_formatted_name", "?");
        } else {
            lastModified = 2;
            fakeIdentifier = "";
            Person person;

            if (same && backupPerson != null) {
                person = backupPerson;
                same = false;
            } else {
                person = seededMethods.getPerson();
                backupPerson = person;
            }

            if (anonymous = seededMethods.getRandomizer().getInt(3, 0) == 0) {
                name = person.getUsername();
                seededMethods.getRandomizer().bindSeed(Methods.getSeed(name)); //Set seed to Randomizer
                simpleName = seededMethods.getPerson().getSimpleName();
                formattedName = Methods.formatText(new String[]{name}, "", "b,tt");
            } else {
                simpleName = person.getForename() + (!person.getForename().isEmpty() && !person.getLastName().isEmpty() ? " " : "") + person.getLastName();
                name = simpleName + (person.getSuffix().isEmpty() ? "" : " " + person.getSuffix());
                formattedName = Methods.formatText(new String[]{person.getForename(), person.getLastName()}, person.getSuffix(), "b");
            }
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(person.getBirthdate());
            year = calendar.get(Calendar.YEAR);
            month = calendar.get(Calendar.MONTH) + 1;
            day = calendar.get(Calendar.DAY_OF_MONTH);
            sex = person.getSex() == -1 ? 0 : person.getSex();

            enquiry = new Enquiry(
                    name,
                    formattedName,
                    sex,
                    year,
                    calendar.get(Calendar.MONTH),
                    day,
                    false,
                    anonymous
            );
        }
        currentName = name;
        date = Methods.getFormattedDate(year, month, day);

        //Get zodiac information
        zodiacIndex = methods.getZodiacIndex(month, day);
        newZodiacIndex = methods.getNewZodiacIndex(month, day);

        //Get formatted inquiry date
        if (current)
            formattedDate = methods.getDate();
        else
            formattedDate = Methods.getFormattedDate(currentDate[0], currentDate[1] + 1, currentDate[2]);
        preferences.edit().putString("temp_enquiryDate", formattedDate).commit();

        //Set seed
        String dailySeed = name + System.getProperty("line.separator") + formattedDate + Methods.SEPARATOR[0] + date + System.getProperty("line.separator") + sex;
        String uniqueSeed = name + System.getProperty("line.separator") + date + System.getProperty("line.separator") + sex;
        long seed = Methods.getSeed(dailySeed);
        long personalSeed = Methods.getSeed(uniqueSeed);

        //Define HashMap
        HashMap<String, Object> hashMap = new HashMap();

        //Get predictionView information
        seededMethods.getRandomizer().bindSeed(seed); //Set seed to Randomizer
        Entity entity = seededMethods.getEntity();
        hashMap.put("fortuneCookie", seededMethods.getFortuneCookie());
        hashMap.put("gibberish", "<font color=#ECFE5B>" + Methods.formatText(new String[]{methods.generateGibberish(hashMap.get("fortuneCookie").toString().length())}, "", "i,tt") + "</font>");
        hashMap.put("divination", seededMethods.getDivination());
        hashMap.put("fortuneNumbers", android.text.TextUtils.join(", ", seededMethods.getRandomizer().getIntegers(5, 100, true)));
        hashMap.put("emotions", seededMethods.getEmotions());
        hashMap.put("characteristic", seededMethods.capitalizeFirst(seededMethods.getAbstractNoun()));
        hashMap.put("chainOfEvents", seededMethods.getChainOfEvents(formattedName, user));
        hashMap.put("influence", "<font color=" + seededMethods.getColorAsString() + ">" + Methods.formatText(new String[]{entity.getPrimitiveName()[0], entity.getPrimitiveName()[1]}, entity.getPrimitiveName()[2], "b") + "</font>");

        //Get zodiac information
        hashMap.put("zodiacSign", methods.getStringFromStringArray(R.array.zodiac_sign, zodiacIndex) + " " + methods.getEmojiByUnicode(methods.getIntFromIntArray(R.array.zodiac_signs, zodiacIndex)));
        hashMap.put("astrologicalHouse", methods.getStringFromStringArray(R.array.astrological_house, zodiacIndex));
        hashMap.put("ruler", methods.getStringFromStringArray(R.array.ruler, zodiacIndex));
        hashMap.put("element", methods.getStringFromStringArray(R.array.element, zodiacIndex));
        hashMap.put("signColor", String.format(getString(R.string.zodiac_color), methods.getStringFromStringArray(R.array.color, zodiacIndex), methods.getStringFromStringArray(R.array.color_values, zodiacIndex)));
        hashMap.put("signNumbers", methods.getStringFromStringArray(R.array.numbers, zodiacIndex));
        hashMap.put("compatibility", methods.getStringFromStringArray(R.array.compatibility, zodiacIndex));
        hashMap.put("incompatibility", methods.getStringFromStringArray(R.array.incompatibility, zodiacIndex));
        hashMap.put("newZodiacSign", methods.getStringFromStringArray(R.array.new_zodiac_sign, newZodiacIndex) + " " + methods.getEmojiByUnicode(methods.getIntFromIntArray(R.array.new_zodiac_signs, newZodiacIndex)));
        hashMap.put("chineseZodiacSign", methods.getChineseZodiacSign(year));

        //Get identity information
        seededMethods.getRandomizer().bindSeed(personalSeed); //Set seed to Randomizer
        formattedName = "<font color=" + seededMethods.getColorAsString() + ">" + formattedName + "</font>";
        Entity[] entities = new Entity[2];

        for (int i = 0; i < entities.length; i++) {
            entities[i] = seededMethods.getEntity();
        }
        hashMap.put("color", seededMethods.getColor(uniqueSeed));
        hashMap.put("animal", methods.capitalizeFirst(seededMethods.getStringFromStringArray(R.array.animal)));
        hashMap.put("psychologicalType", seededMethods.getStringFromStringArray(R.array.psychological_type));
        hashMap.put("secretName", "<font color=" + seededMethods.getColorAsString() + ">" + Methods.formatText(new String[]{methods.capitalizeFirst(seededMethods.generateName(seededMethods.getRandomizer().getInt(6, 1)))}, "", "b") + "</font>");
        hashMap.put("demonicName", "<font color=" + seededMethods.getColorAsString() + ">" + Methods.formatText(new String[]{methods.getDemonicName(simpleName)}, "", "b") + "</font>");
        hashMap.put("previousName", "<font color=" + seededMethods.getColorAsString() + ">" + Methods.formatText(new String[]{entities[0].getPrimitiveName()[0], entities[0].getPrimitiveName()[1]}, entities[0].getPrimitiveName()[2], "b") + "</font>");
        hashMap.put("futureName", "<font color=" + seededMethods.getColorAsString() + ">" + Methods.formatText(new String[]{entities[1].getPrimitiveName()[0], entities[1].getPrimitiveName()[1]}, entities[1].getPrimitiveName()[2], "b") + "</font>");
        hashMap.put("recommendedUsername", "<font color=" + seededMethods.getColorAsString() + ">" + Methods.formatText(new String[]{seededMethods.generateUsername()}, "", "b,tt") + "</font>");
        hashMap.put("digit", seededMethods.getRandomizer().getInt(10, 0));
        hashMap.put("uniqueNumbers", android.text.TextUtils.join(", ", seededMethods.getRandomizer().getIntegers(3, 1000, true)));
        hashMap.put("uniqueIdentifier", UUID.nameUUIDFromBytes(uniqueSeed.getBytes()).toString());
        hashMap.put("daysBetweenDates", Long.toString(Methods.getDifferenceInDays(date, formattedDate)));
        hashMap.put("timeBetweenDates", methods.getTimeBetweenDates(date, formattedDate));
        seededMethods.getRandomizer().bindSeed(null); //Remove seed to Randomizer

        prediction = String.format(getString(R.string.prediction),
                Methods.ZERO_WIDTH_SPACE + hashMap.get("gibberish").toString() + Methods.ZERO_WIDTH_SPACE,
                hashMap.get("divination").toString(),
                hashMap.get("fortuneNumbers").toString(),
                hashMap.get("emotions").toString(),
                hashMap.get("characteristic").toString(),
                hashMap.get("chainOfEvents").toString(),
                hashMap.get("influence").toString(),
                hashMap.get("zodiacSign").toString(),
                hashMap.get("astrologicalHouse").toString(),
                hashMap.get("ruler").toString(),
                hashMap.get("element").toString(),
                hashMap.get("signColor").toString(),
                hashMap.get("signNumbers").toString(),
                hashMap.get("compatibility").toString(),
                hashMap.get("incompatibility").toString(),
                hashMap.get("newZodiacSign").toString(),
                hashMap.get("chineseZodiacSign").toString(),
                hashMap.get("color").toString(), "⬛&#xFE0E;",
                hashMap.get("animal").toString(),
                hashMap.get("psychologicalType").toString(),
                hashMap.get("secretName").toString(),
                hashMap.get("demonicName").toString(),
                hashMap.get("previousName").toString(),
                hashMap.get("futureName").toString(),
                hashMap.get("recommendedUsername").toString(),
                (int) hashMap.get("digit"),
                hashMap.get("uniqueNumbers").toString(),
                hashMap.get("uniqueIdentifier").toString(),
                hashMap.get("daysBetweenDates").toString(),
                hashMap.get("timeBetweenDates").toString()
        );

        clipboardPrediction = String.format(getString(R.string.prediction),
                hashMap.get("fortuneCookie").toString(),
                hashMap.get("divination").toString(),
                hashMap.get("fortuneNumbers").toString(),
                hashMap.get("emotions").toString(),
                hashMap.get("characteristic").toString(),
                hashMap.get("chainOfEvents").toString(),
                hashMap.get("influence").toString(),
                hashMap.get("zodiacSign").toString(),
                hashMap.get("astrologicalHouse").toString(),
                hashMap.get("ruler").toString(),
                hashMap.get("element").toString(),
                methods.getStringFromStringArray(R.array.color, zodiacIndex),
                hashMap.get("signNumbers").toString(),
                hashMap.get("compatibility").toString(),
                hashMap.get("incompatibility").toString(),
                hashMap.get("newZodiacSign").toString(),
                hashMap.get("chineseZodiacSign").toString(),
                "#FFFFFF", hashMap.get("color").toString(),
                hashMap.get("animal").toString(),
                hashMap.get("psychologicalType").toString(),
                hashMap.get("secretName").toString(),
                hashMap.get("demonicName").toString(),
                hashMap.get("previousName").toString(),
                hashMap.get("futureName").toString(),
                hashMap.get("recommendedUsername").toString(),
                (int) hashMap.get("digit"),
                hashMap.get("uniqueNumbers").toString(),
                hashMap.get("uniqueIdentifier").toString(),
                hashMap.get("daysBetweenDates").toString(),
                hashMap.get("timeBetweenDates").toString()
        );
        fortuneCookie = hashMap.get("fortuneCookie").toString();
        unrevealedFortuneCookie = "<a href='links/prediction'>" + getString(R.string.prediction_action) + "</a>";
        prediction = prediction.replaceFirst("꘍.?", unrevealedFortuneCookie);
        clipboardPrediction = Methods.fromHtml(clipboardPrediction).toString();
        clipboardPrediction = clipboardPrediction.replaceAll("(?m)꘍^.*(?:\\r?\\n)?", "");
        clipboardPrediction = String.format(getString(R.string.enquiry_information), formattedDate, name, methods.swapFirstToLowercase(sexArray[sex]), date) + System.getProperty("line.separator") + System.getProperty("line.separator") + clipboardPrediction;
        data[0] = prediction;
        data[1] = clipboardPrediction;
        data[2] = formattedName;
        data[3] = sex;
        data[4] = date;

        if (backup)
            backupData = data;
        return data;
    }

    private void setTextAsLinkified(TextView textView, String s) {
        CharSequence sequence = Methods.fromHtml(s);
        SpannableStringBuilder stringBuilder = new SpannableStringBuilder(sequence);
        URLSpan[] urls = stringBuilder.getSpans(0, sequence.length(), URLSpan.class);

        for (URLSpan span : urls) {
            makeLinkClickable(stringBuilder, span);
        }
        textView.setText(stringBuilder, TextView.BufferType.SPANNABLE);
        textView.setClickable(true);
        textView.setMovementMethod(LinkMovementMethod.getInstance());
    }

    private void makeLinkClickable(SpannableStringBuilder stringBuilder, final URLSpan span) {
        int start = stringBuilder.getSpanStart(span);
        int end = stringBuilder.getSpanEnd(span);
        int flags = stringBuilder.getSpanFlags(span);

        ClickableSpan clickable = new ClickableSpan() {
            public void onClick(@NonNull View view) {
                String link = StringUtils.substringAfterLast(span.getURL(), "/");

                if (link.equals("prediction")) {
                    methods.playSound("crack");
                    setTextAsLinkified(predictionView, data[0].toString().replaceFirst(unrevealedFortuneCookie + "(\r\n|\r|\n)?\\s*(<br>)?" + Methods.ZERO_WIDTH_SPACE + ".*" + Methods.ZERO_WIDTH_SPACE, fortuneCookie));
                }
            }
        };
        stringBuilder.setSpan(clickable, start, end, flags);
        stringBuilder.removeSpan(span);
    }

    private void calculateCompatibility() {
        progressBar.setMax(100);
        progressBar.setProgress(0);

        if (!initialNameText.isEmpty() && !finalNameText.isEmpty()) {
            if (initialNameText.toLowerCase().equals(finalNameText.toLowerCase())) {
                compatibility.setText(Methods.fromHtml(String.format(getString(R.string.compatibility_result), "<font color=#6666FF>" + 100 + "%</font>")));
                progressBar.setProgress(100);
            } else {
                String formattedText, temp, tempName = initialNameText, tempOtherName = finalNameText;
                int compatibilityPoints;

                if (tempName.compareTo(tempOtherName) < 0) {
                    temp = tempName;
                    tempName = tempOtherName;
                    tempOtherName = temp;
                }
                long seed = Methods.getSeed(tempName + System.getProperty("line.separator") + tempOtherName);
                randomizer.bindSeed(seed);

                if ((compatibilityPoints = randomizer.getInt(101, 0)) == 0)
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
                randomizer.bindSeed(null);
                compatibility.setText(Methods.fromHtml(String.format(getString(R.string.compatibility_result), formattedText)));
                progressBar.setProgress(compatibilityPoints);
            }
        } else {
            compatibility.setText(Methods.fromHtml(String.format(getString(R.string.compatibility_result), "<font color=#C0FF2B>?</font>")));
            progressBar.setProgress(0);
        }
    }

    private void obtainDesignation() {
        if (!obtaining) {
            obtaining = true;
            nameTypeSelector.setEnabled(false);
            nameBox.setText(unseededMethods.getDesignation(designationEnum));
            nameTypeSelector.setEnabled(true);
            obtaining = false;
        }
    }

    private boolean isVisible(View view) {
        if (view == null)
            return false;
        if (!view.isShown())
            return false;
        Rect rect = new Rect();

        if (view.getGlobalVisibleRect(rect) && view.getHeight() == rect.height() && view.getWidth() == rect.width())
            return true;
        else
            return false;
    }

    private int[] getScreenSize() {
        Display display = getWindowManager().getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);
        System.out.println("Screen size: " + point.x + "x" + point.y);
        return new int[]{point.x, point.y};
    }
}

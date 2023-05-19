package com.app.memoeslink.adivinador.activity

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.app.DatePickerDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.Point
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.tts.UtteranceProgressListener
import android.text.Editable
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.multidex.BuildConfig
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.memoeslink.adivinador.ActivityState
import com.app.memoeslink.adivinador.ActivityStatus
import com.app.memoeslink.adivinador.CoupleCompatibility
import com.app.memoeslink.adivinador.Divination
import com.app.memoeslink.adivinador.FortuneTeller
import com.app.memoeslink.adivinador.Prediction
import com.app.memoeslink.adivinador.PredictionHistory
import com.app.memoeslink.adivinador.R
import com.app.memoeslink.adivinador.Screen
import com.app.memoeslink.adivinador.Sound
import com.app.memoeslink.adivinador.extensions.getCurrentWindowPoint
import com.app.memoeslink.adivinador.extensions.toClickableText
import com.app.memoeslink.adivinador.extensions.toHtmlText
import com.app.memoeslink.adivinador.extensions.toLinkedHtmlText
import com.app.memoeslink.adivinador.preference.Preference
import com.app.memoeslink.adivinador.preference.PreferenceHandler
import com.app.memoeslink.adivinador.preference.PreferenceUtils
import com.easyandroidanimations.library.BounceAnimation
import com.github.jinatonic.confetti.ConfettiManager
import com.github.jinatonic.confetti.ConfettiSource
import com.github.jinatonic.confetti.ConfettoGenerator
import com.github.jinatonic.confetti.Utils
import com.github.jinatonic.confetti.confetto.BitmapConfetto
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.lelloman.identicon.view.GithubIdenticonView
import com.memoeslink.common.Randomizer
import com.memoeslink.generator.common.DateTimeHelper
import com.memoeslink.generator.common.GeneratorManager
import com.memoeslink.generator.common.LongHelper
import com.memoeslink.generator.common.NameType
import com.memoeslink.generator.common.Person
import com.memoeslink.generator.common.StringHelper
import com.memoeslink.generator.common.TextFormatter
import com.memoeslink.manager.Device
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import me.zhanghai.android.materialprogressbar.MaterialProgressBar
import java.time.LocalDate
import java.util.Locale
import java.util.Random
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.system.exitProcess


class TempMainActivity : MenuActivity() {
    private val particleColors = arrayOf(
        intArrayOf(
            Color.BLUE,
            Color.argb(255, 0, 128, 255),
            Color.argb(255, 51, 153, 255),
            Color.argb(255, 0, 192, 199),
            Color.argb(125, 0, 128, 255),
            Color.argb(125, 51, 153, 255),
            Color.argb(125, 0, 192, 199)
        ), intArrayOf(
            Color.YELLOW,
            Color.argb(255, 251, 255, 147),
            Color.argb(255, 224, 228, 124),
            Color.argb(255, 155, 215, 93),
            Color.argb(255, 120, 168, 71),
            Color.argb(125, 251, 255, 147),
            Color.argb(125, 224, 228, 124),
            Color.argb(125, 155, 215, 93),
            Color.argb(125, 120, 168, 71)
        )
    )
    private var srlRefresher: SwipeRefreshLayout? = null
    private var rlAdContainer: RelativeLayout? = null
    private var llConfetti: LinearLayout? = null
    private var llDataEntryHolder: LinearLayout? = null
    private var llReloadHolder: LinearLayout? = null
    private var llInquiryHolder: LinearLayout? = null
    private var llSelectorHolder: LinearLayout? = null
    private var llClearHolder: LinearLayout? = null
    private var vPersonImage: GithubIdenticonView? = null
    private var ivFortuneTeller: AppCompatImageView? = null
    private var ivSaveLogo: AppCompatImageView? = null
    private var atvInitialName: AppCompatAutoCompleteTextView? = null
    private var atvFinalName: AppCompatAutoCompleteTextView? = null
    private var tvPick: TextView? = null
    private var tvDataEntry: TextView? = null
    private var tvReload: TextView? = null
    private var tvInquiry: TextView? = null
    private var tvSelector: TextView? = null
    private var tvClear: TextView? = null
    private var tvPhrase: TextView? = null
    private var tvPersonInfo: TextView? = null
    private var tvPrediction: TextView? = null
    private var tvBinder: TextView? = null
    private var tvCompatibility: TextView? = null
    private var tvTextCopy: TextView? = null
    private var tvNameBox: EditText? = null
    private var spnNameType: AppCompatSpinner? = null
    private var dpdInquiryDate: DatePickerDialog? = null
    private var pbWait: MaterialProgressBar? = null
    private var btDataEntry: Button? = null
    private var btTextCopy: Button? = null
    private var vMain: View? = null
    private var vCompatibility: View? = null
    private var vNameGenerator: View? = null
    private var adView: AdView? = null
    private var interstitialAd: InterstitialAd? = null
    private var adRequest: AdRequest? = null
    private var confettiManager: ConfettiManager? = null
    private var compatibilityDialog: AlertDialog? = null
    private var nameGeneratorDialog: AlertDialog? = null
    private var dialog: AlertDialog? = null
    private var timer: Timer? = null
    private var status: ActivityStatus? = ActivityStatus()
    private var fortuneTeller: FortuneTeller? = FortuneTeller(this@TempMainActivity)
    private var device: Device? = Device(this@TempMainActivity)
    private var r: Randomizer? = Randomizer()
    private var predictionHistory: PredictionHistory? = PredictionHistory()
    private var backupPredictions: PredictionHistory? = PredictionHistory()
    private var people: List<Person> = ArrayList()
    private var listener: OnSharedPreferenceChangeListener? =
        null //Declared as global to avoid destruction by JVM Garbage Collector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this@TempMainActivity)
        srlRefresher = findViewById(R.id.main_refresh_layout)
        rlAdContainer = findViewById(R.id.ad_container)
        llConfetti = findViewById(R.id.main_confetti_layout)
        llDataEntryHolder = findViewById(R.id.main_data_entry_holder)
        llReloadHolder = findViewById(R.id.main_reload_holder)
        llInquiryHolder = findViewById(R.id.main_inquiry_holder)
        llSelectorHolder = findViewById(R.id.main_selector_holder)
        llClearHolder = findViewById(R.id.main_clear_holder)
        vPersonImage = findViewById(R.id.main_person_image)
        ivFortuneTeller = findViewById(R.id.main_fortune_teller)
        ivSaveLogo = findViewById(R.id.main_save)
        tvPick = findViewById(R.id.main_pick)
        tvPick?.text = getString(R.string.pick).toLinkedHtmlText()
        tvDataEntry = findViewById(R.id.main_data_entry)
        tvDataEntry?.text = getString(R.string.data_entry).toLinkedHtmlText()
        tvReload = findViewById(R.id.main_reload)
        tvReload?.text = getString(R.string.reload).toLinkedHtmlText()
        tvInquiry = findViewById(R.id.main_inquiry)
        tvInquiry?.text = getString(R.string.inquiry, "…").toLinkedHtmlText()
        tvSelector = findViewById(R.id.main_selector)
        tvSelector?.text = getString(R.string.selector).toLinkedHtmlText()
        tvClear = findViewById(R.id.main_clear)
        tvClear?.text = getString(R.string.clear).toLinkedHtmlText()
        tvPhrase = findViewById(R.id.main_fortune_teller_phrase)
        tvPersonInfo = findViewById(R.id.main_person)
        tvPrediction = findViewById(R.id.main_prediction)
        btDataEntry = findViewById(R.id.main_edit_button)
        btTextCopy = findViewById(R.id.main_copy_button)
        vMain = findViewById(R.id.main_view)
        val inflater: LayoutInflater = this@TempMainActivity.layoutInflater
        vCompatibility = inflater.inflate(R.layout.dialog_compatibility, null)
        atvInitialName = vCompatibility?.findViewById(R.id.dialog_name_field)
        atvFinalName = vCompatibility?.findViewById(R.id.dialog_other_name_field)
        tvBinder = vCompatibility?.findViewById(R.id.dialog_binder_text)
        tvBinder?.text = getString(R.string.binder).toLinkedHtmlText()
        tvCompatibility = vCompatibility?.findViewById(R.id.dialog_text)
        pbWait = vCompatibility?.findViewById(R.id.dialog_progress)
        vNameGenerator = inflater.inflate(R.layout.dialog_name_generation, null)
        spnNameType = vNameGenerator?.findViewById(R.id.dialog_spinner)
        tvNameBox = vNameGenerator?.findViewById(R.id.dialog_generated_name)
        tvTextCopy = vNameGenerator?.findViewById(R.id.dialog_copy_text)
        tvTextCopy?.text = getString(R.string.action_copy).toLinkedHtmlText()
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        navigationView.itemIconTintList = null

        //Request ads
        val testDevices: MutableList<String> = java.util.ArrayList()
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
        testDevices.add(device?.getAndroidId() ?: "0".repeat(32))
        var requestConfiguration = RequestConfiguration.Builder().build()

        if (BuildConfig.DEBUG) requestConfiguration =
            RequestConfiguration.Builder().setTestDeviceIds(testDevices).build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        adRequest = AdRequest.Builder().build()

        adRequest?.isTestDevice(this@TempMainActivity)?.takeIf {
            !it && BuildConfig.DEBUG
        }?.let {
            println("This device will not show test ads.")
        }

        //Preload prediction
        predictionHistory?.add(generatePrediction(PreferenceUtils.isEnquiryFormEntered()))

        //Set empty prediction
        val emptyPrediction = Prediction.PredictionBuilder().build()
        tvPrediction?.text = emptyPrediction.getFormattedContent(this@TempMainActivity).toHtmlText()

        //Get a greeting
        if (PreferenceHandler.getBoolean(Preference.SETTING_GREETINGS_ENABLED)) tvPhrase?.text =
            fortuneTeller?.greet().toHtmlText()
        else tvPhrase?.text = "…"

        //Change drawable for fortune teller
        fortuneTeller?.randomAppearance?.let { ivFortuneTeller?.setImageResource(it) }

        //Delete temporary preferences
        PreferenceUtils.deleteTemp()

        //Initialize components
        val nameAdapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
            this@TempMainActivity,
            android.R.layout.simple_spinner_item,
            resources.getStringArray(R.array.name_type)
        ) {
            val disabledPositions = intArrayOf(0, 1, 2)

            override fun isEnabled(position: Int): Boolean {
                return !disabledPositions.contains(position)
            }

            override fun getDropDownView(
                position: Int, convertView: View?, parent: ViewGroup
            ): View? {
                val view = super.getDropDownView(position, convertView, parent)
                view.tag = position
                view.alpha = if (isEnabled(position)) 1.0f else 0.35f
                return view
            }
        }
        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spnNameType?.adapter = nameAdapter
        spnNameType?.setSelection(3)

        val wrapper = ContextThemeWrapper(this@TempMainActivity, R.style.CustomDialog)
        val compatibilityBuilder = AlertDialog.Builder(wrapper)
        compatibilityBuilder.setTitle(R.string.compatibility)
        compatibilityBuilder.setNeutralButton(R.string.action_close) { _, _ -> compatibilityDialog?.dismiss() }
        compatibilityBuilder.setView(vCompatibility)
        compatibilityDialog = compatibilityBuilder.create()

        val nameGeneratorBuilder = AlertDialog.Builder(wrapper)
        nameGeneratorBuilder.setTitle(R.string.name_generation)
        nameGeneratorBuilder.setPositiveButton(R.string.action_generate, null)
        nameGeneratorBuilder.setNegativeButton(R.string.action_close, null)
        nameGeneratorBuilder.setView(vNameGenerator)
        nameGeneratorDialog = nameGeneratorBuilder.create()

        nameGeneratorDialog?.setOnShowListener { dialog: DialogInterface ->
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener { displayGeneratedName() }
        }

        srlRefresher?.setOnRefreshListener {
            refreshPrediction()
            srlRefresher?.isRefreshing = false
        }

        navigationView.setNavigationItemSelectedListener { item: MenuItem ->
            if (PreferenceHandler.getBoolean(Preference.SETTING_HIDE_DRAWER, true)) closeDrawer()

            when (item.itemId) {
                R.id.nav_data_entry -> {
                    val i = Intent(this@TempMainActivity, InputActivity::class.java)
                    startActivity(i)
                }

                R.id.nav_reload -> refreshPrediction()
                R.id.nav_save -> ivSaveLogo?.performClick()
                R.id.nav_inquiry -> llInquiryHolder?.performClick()
                R.id.nav_selector -> llSelectorHolder?.performClick()
                R.id.nav_clear -> llClearHolder?.performClick()
                R.id.nav_compatibility -> {
                    compatibilityDialog?.show()
                    compatibilityDialog?.window?.setLayout(
                        ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
                    )
                }

                R.id.nav_name_generation -> nameGeneratorDialog?.show()
            }
            return@setNavigationItemSelectedListener false
        }

        DateTimeHelper.getCurrentDate().let {
            dpdInquiryDate = DatePickerDialog(
                this@TempMainActivity, { datePicker: DatePicker, _: Int, _: Int, _: Int ->
                    PreferenceHandler.put(Preference.TEMP_YEAR_OF_ENQUIRY, datePicker.year)
                    PreferenceHandler.put(Preference.TEMP_MONTH_OF_ENQUIRY, datePicker.month + 1)
                    PreferenceHandler.put(Preference.TEMP_DAY_OF_ENQUIRY, datePicker.dayOfMonth)
                    refreshPrediction()
                }, it.year, it.monthValue - 1, it.dayOfMonth
            )
        }

        tvPick?.setOnClickListener {
            dpdInquiryDate?.isShowing?.takeUnless { it }?.let {
                val pickedDate = LocalDate.parse(PreferenceUtils.getEnquiryDate())
                dpdInquiryDate?.updateDate(
                    pickedDate.year, pickedDate.monthValue - 1, pickedDate.dayOfMonth
                )
                dpdInquiryDate?.show()
            }
        }

        spnNameType?.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?, view: View, position: Int, id: Long
            ) {
                displayGeneratedName()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }
        }

        ivFortuneTeller?.setOnClickListener {
            PreferenceHandler.getStringAsInt(Preference.SETTING_FORTUNE_TELLER_ASPECT, 1)
                .takeUnless { it != 0 }.let {
                    Sound.play(this@TempMainActivity, "jump")
                    BounceAnimation(ivFortuneTeller).setBounceDistance(7f).setNumOfBounces(1)
                        .setDuration(150).animate()
                }
        }

        ivSaveLogo?.setOnClickListener {
            predictionHistory?.latest?.person?.let { person ->
                if (PreferenceUtils.savePerson(person)) {
                    showToast(getString(R.string.toast_inquiry_saved))
                    refreshUiUponEnquirySaved()
                } else showToast(getString(R.string.toast_inquiry_not_saved))
            }
        }

        llDataEntryHolder?.setOnClickListener {
            val i = Intent(this@TempMainActivity, InputActivity::class.java)
            startActivity(i)
        }

        llReloadHolder?.setOnClickListener { refreshPrediction() }

        llInquiryHolder?.setOnClickListener { setFormPerson(people[0]) }

        llSelectorHolder?.setOnClickListener {
            if (dialog != null && !PreferenceHandler.has(Preference.TEMP_BUSY)) dialog?.show()
        }

        llClearHolder?.setOnClickListener {
            PreferenceUtils.clearForm()
            refreshPrediction()
        }

        btDataEntry?.setOnClickListener {
            val i = Intent(this@TempMainActivity, InputActivity::class.java)
            startActivity(i)
        }

        btTextCopy?.setOnClickListener {
            predictionHistory?.takeUnless { !it.isEmpty }?.let { history ->
                copyTextToClipboard(history.latest?.getContent(this@TempMainActivity))
            }
        }

        compatibilityDialog?.setOnShowListener { displayCoupleCompatibility() }

        atvInitialName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                displayCoupleCompatibility()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        atvFinalName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                displayCoupleCompatibility()
            }

            override fun afterTextChanged(s: Editable) {}
        })

        tvBinder?.setOnClickListener {
            atvInitialName?.setText(predictionHistory?.latest?.person?.descriptor)
        }

        tvNameBox?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) tvTextCopy?.visibility = View.GONE
                else tvTextCopy?.visibility = View.VISIBLE
            }

            override fun afterTextChanged(s: Editable) {}
        })

        tvTextCopy?.setOnClickListener {
            copyTextToClipboard(tvNameBox?.text.toString())
        }

        listener = OnSharedPreferenceChangeListener { _: SharedPreferences?, key: String ->
            if (key == Preference.DATA_STORED_PEOPLE.tag) {
                dialog?.takeIf { it.isShowing }?.dismiss()
                dialog = null
                //recreate();
                updateInquirySelector()
            }

            if (key == Preference.DATA_STORED_NAMES.tag) updateNameSuggestions()
        }
        PreferenceHandler.changePreferencesListener(listener)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!closeDrawer()) AlertDialog.Builder(this@TempMainActivity)
                    .setTitle(R.string.alert_exit_title).setMessage(R.string.alert_exit_message)
                    .setNegativeButton(R.string.action_cancel, null)
                    .setPositiveButton(R.string.action_exit) { _, _ ->
                        exitProcess(0) //Try to stop current threads
                    }.create().show()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        updateNameSuggestions()

        //Request permissions
        val launcher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, Boolean> ->
                if (isGranted.containsValue(false)) showToast(getString(R.string.denied_permission))
            }
        var permissionsGranted = true
        val permissions =
            arrayOf(Manifest.permission.READ_CONTACTS, Manifest.permission.ACCESS_COARSE_LOCATION)

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this@TempMainActivity, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) permissionsGranted = false
        }

        if (!permissionsGranted) launcher.launch(permissions)

        //Show full-screen ads
        showInterstitialAd()
    }

    override fun onResume() {
        super.onResume()
        LongHelper.getSeed(PreferenceHandler.getString(Preference.SETTING_SEED))
            ?.takeIf { it == fortuneTeller?.seed }?.let {
                fortuneTeller = FortuneTeller(this@TempMainActivity)
            }

        //Restart Activity if required
        if (PreferenceHandler.has(Preference.TEMP_RESTART_ACTIVITY)) {
            status?.forceEffects = true
            val intent = intent
            finish()
            startActivity(intent)
        }
        PreferenceHandler.remove(Preference.TEMP_RESTART_ACTIVITY)

        //Stop TTS if it is disabled and continues talking
        if (speechAvailable && (!PreferenceHandler.getBoolean(Preference.SETTING_AUDIO_ENABLED) || !PreferenceHandler.getBoolean(
                Preference.SETTING_VOICE_ENABLED
            )) && tts.isSpeaking
        ) tts.stop()

        //Show, avoid, or hide ads
        prepareAd(false)

        //Update selectable people list
        updateInquirySelector()

        //Get a prediction
        refreshPrediction()

        //Change drawable if the 'fortune teller aspect' preference was changed
        if (PreferenceHandler.has(Preference.TEMP_CHANGE_FORTUNE_TELLER)) {
            fortuneTeller?.randomAppearance?.let { ivFortuneTeller?.setImageResource(it) }
            PreferenceHandler.remove(Preference.TEMP_CHANGE_FORTUNE_TELLER)
        }

        if (timer != null) {
            timer = fixedRateTimer("timer", false, 0L, 1000) {
                val frequency =
                    PreferenceHandler.getStringAsInt(Preference.SETTING_REFRESH_TIME, 20)
                val refreshFrequency =
                    PreferenceHandler.getStringAsInt(Preference.SETTING_UPDATE_TIME, 60)

                status?.seconds?.let { seconds ->
                    if (seconds >= frequency && frequency != 0) {
                        runOnUiThread {
                            //Fade out and fade in the fortune teller's text
                            if (activityState == ActivityState.RESUMED) fadeAndShowView(tvPhrase)

                            //Change drawable for the fortune teller
                            fortuneTeller?.randomAppearance?.let {
                                ivFortuneTeller?.setImageResource(
                                    it
                                )
                            }

                            //Get random text from the fortune teller
                            tvPhrase?.text = fortuneTeller?.talk().toHtmlText()

                            //Read the text
                            read(tvPhrase?.text.toString())
                        }
                        status?.seconds = 0
                    } else status?.seconds = seconds + 1
                }

                status?.updateSeconds?.let { seconds ->
                    if (seconds >= refreshFrequency) refreshPrediction()
                    else {
                        if (seconds != 0 && seconds % 10 == 0) {
                            backupPredictions?.takeIf { !it.isFull }.let {
                                lifecycleScope.launch {
                                    backupPredictions?.add(generatePrediction(false))
                                }
                            }
                        }
                        status?.updateSeconds = seconds + 1
                    }
                }

                status?.resourceSeconds?.let { seconds ->
                    if (seconds >= 1800) status?.resourceSeconds = 0
                    else status?.resourceSeconds = seconds + 1
                }

                status?.adSeconds?.let { seconds ->
                    if (seconds >= 3600) {
                        showInterstitialAd()
                        status?.adSeconds = 0
                    } else status?.adSeconds = seconds + 1
                }
            }
        }
        prepareAnimation()
    }

    override fun onPause() {
        super.onPause()
        stopConfetti() //Finish any confetti animation
    }

    override fun onDestroy() {
        timer?.let {
            it.cancel()
            it.purge()
            timer = null
        }

        tts?.let {
            it.stop()
            it.shutdown()
            tts = null
        }
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        prepareAnimation()
        prepareAd(true)
    }

    override fun onInit(i: Int) {
        super.onInit(i)

        if (speechAvailable) {
            tts.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                override fun onStart(utteranceId: String?) {}

                override fun onDone(utteranceId: String?) {}

                override fun onError(utteranceId: String?) {}
            })
        }
    }

    private fun prepareAd(restarted: Boolean) {
        status?.adPaused?.takeIf {
            !it && PreferenceHandler.getBoolean(Preference.SETTING_ADS_ENABLED, true)
        }?.let {

        }
    }

    private fun destroyAd() {
    }

    private fun showInterstitialAd() {
    }

    private fun prepareAnimation() {
        Screen.lockScreenOrientation(this@TempMainActivity) //Lock orientation
        stopConfetti() //Finish previous confetti animation;

        //Redraw view
        llConfetti?.requestLayout()
        llConfetti?.invalidate()

        //Wait for measurements to be done
        waitTasks()

        if (status?.measuredTimes == 0L) llConfetti?.post { performMeasurements() }
        else Handler(Looper.getMainLooper()).postDelayed({ performMeasurements() }, 1000)

        status?.measuredTimes?.takeIf { it < Long.MAX_VALUE }?.let {
            status?.measuredTimes = it + 1
        }
    }

    private fun waitTasks() {
        val forceEffects = status?.forceEffects ?: return
        val screenMeasured = status?.screenMeasured ?: return

        if (activityState == ActivityState.RESUMED || forceEffects) {
            if (screenMeasured) {
                status?.measurements?.let { measurements ->
                    val originInX: Int = measurements.getOrDefault("confettiOriginInX", 0)
                    val originInY: Int = measurements.getOrDefault("confettiOriginInY", 0)

                    if (PreferenceHandler.getBoolean(Preference.SETTING_PARTICLES_ENABLED) && originInX > 0 && originInY > 0) throwConfetti(
                        originInX, originInY
                    )
                }

                if (PreferenceHandler.getStringAsInt(
                        Preference.SETTING_FORTUNE_TELLER_ASPECT, 1
                    ) != 0
                ) {
                    Sound.play(this@TempMainActivity, "jump")
                    BounceAnimation(ivFortuneTeller).setBounceDistance(20f).setNumOfBounces(1)
                        .setDuration(500).animate()
                }
                Screen.unlockScreenOrientation(this@TempMainActivity) //Unlock orientation
                status?.forceEffects = false
            } else Handler(Looper.getMainLooper()).postDelayed({ waitTasks() }, 500)
        }
    }

    private fun performMeasurements() {
        status?.screenMeasured = false
        val point: Point = windowManager.getCurrentWindowPoint()
        status?.screenWidth = point.x
        status?.screenHeight = point.y
        status?.measurements?.put("confettiLayoutWidth", llConfetti?.width ?: 0)
        status?.measurements?.put("confettiLayoutHeight", llConfetti?.height ?: 0)
        status?.measurements?.put("confettiOriginInX", (llConfetti?.width ?: 0) / 2)
        status?.measurements?.put("confettiOriginInY", (llConfetti?.height ?: 0) / 2)
        status?.screenMeasured = true
        println(
            "Confetti layout measures: ${status?.measurements?.get("confettiLayoutWidth")}×${
                status?.measurements?.get(
                    "confettiLayoutHeight"
                )
            } [Confetti origin in: ${status?.measurements?.get("confettiOriginInX")} axis X; ${
                status?.measurements?.get(
                    "confettiOriginInY"
                )
            } axis Y]"
        )
    }

    private fun throwConfetti(originInX: Int, originInY: Int) {
        val allPossibleConfetti = Utils.generateConfettiBitmaps(
            particleColors[PreferenceHandler.getStringAsInt(Preference.SETTING_FORTUNE_TELLER_ASPECT)],
            10
        )
        val generator = ConfettoGenerator { random: Random ->
            val bitmap = allPossibleConfetti[random.nextInt(allPossibleConfetti.size)]
            BitmapConfetto(bitmap)
        }

        confettiManager = ConfettiManager(
            this@TempMainActivity, generator, ConfettiSource(originInX, originInY), llConfetti
        ).setEmissionDuration(ConfettiManager.INFINITE_DURATION).setEmissionRate(30f)
            .setVelocityX(0f, 360f).setVelocityY(0f, 360f).setRotationalVelocity(180f, 180f)
            .setRotationalAcceleration(360f, 180f).setInitialRotation(180, 180)
            .setTargetRotationalVelocity(360f).enableFadeOut(Utils.getDefaultAlphaInterpolator())
            .animate()

        status?.confettiThrown?.takeIf { it < Long.MAX_VALUE }?.let {
            status?.confettiThrown = it + 1
        }
    }

    private fun stopConfetti() {
        confettiManager?.let {
            it.terminate()
            confettiManager = null
        }
    }

    private fun updateNameSuggestions() {
        var names = listOf<String>()

        if (PreferenceHandler.has(Preference.DATA_STORED_NAMES) && PreferenceHandler.getStringSet(
                Preference.DATA_STORED_NAMES
            ).size > 0
        ) names = PreferenceHandler.getStringSet(Preference.DATA_STORED_NAMES).toList()

        names.takeIf { it.isNotEmpty() }?.let {
            atvInitialName?.setAdapter(
                ArrayAdapter(
                    this@TempMainActivity, android.R.layout.simple_spinner_dropdown_item, names
                )
            )
            atvFinalName?.setAdapter(
                ArrayAdapter(
                    this@TempMainActivity, android.R.layout.simple_spinner_dropdown_item, names
                )
            )
        }
    }

    private fun updateInquirySelector() {
        people = PreferenceUtils.getStoredPeople()
        val items: MutableList<String> = mutableListOf()

        for (person in people) {
            items.add(
                "${person.descriptor} + (${
                    person.gender.getName(
                        this@TempMainActivity, 4
                    )
                }) ${person.birthdate}"
            )
        }
        val builder = AlertDialog.Builder(this@TempMainActivity)
        builder.setNegativeButton(R.string.action_cancel) { _, _ -> dialog?.dismiss() }
        val listView = ListView(this@TempMainActivity)
        val adapter = ArrayAdapter<String>(
            this@TempMainActivity, android.R.layout.simple_list_item_1, android.R.id.text1, items
        )
        listView.adapter = adapter
        listView.onItemClickListener =
            OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                setFormPerson(people[position])
                dialog?.dismiss()
            }
        builder.setView(listView)
        dialog = builder.create()
    }

    private fun setFormPerson(person: Person) {
        if (!PreferenceUtils.isPersonTempStored(person)) {
            PreferenceUtils.saveFormPerson(person)
            refreshPrediction()
        }
    }

    private fun refreshPrediction() {
        if (PreferenceHandler.has(Preference.TEMP_BUSY)) return

        lifecycleScope.launch(Dispatchers.IO) {
            PreferenceHandler.put(Preference.TEMP_BUSY, true)
            Sound.play(this@TempMainActivity, "wind")

            this@TempMainActivity.runOnUiThread {
                tvPick?.isEnabled = false
                status?.updateSeconds = 0

                llConfetti?.animate()?.alpha(0.0f)?.setDuration(200)
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationCancel(animation: Animator) {
                            super.onAnimationEnd(animation)
                            vMain?.clearAnimation()
                            vMain?.visibility = View.INVISIBLE
                        }
                    }) //Fade particles layout out
            }

            val pickedDate = DateTimeHelper.toIso8601Date(
                dpdInquiryDate?.datePicker?.year ?: 2000,
                dpdInquiryDate?.datePicker?.month?.plus(1) ?: 1,
                dpdInquiryDate?.datePicker?.dayOfMonth ?: 1
            )
            val formEntered = PreferenceUtils.isEnquiryFormEntered()
            var preStored = false

            if (!formEntered) {
                backupPredictions?.takeIf { !it.isEmpty }?.let {
                    while (!it.isEmpty) {
                        val prediction = backupPredictions?.dispenseOldest()

                        if (prediction?.date == pickedDate) {
                            predictionHistory?.add(prediction)
                            preStored = true
                            break
                        }
                    }
                }
            }

            if (!preStored) predictionHistory?.add(generatePrediction(formEntered))

            this@TempMainActivity.runOnUiThread {
                refreshHolders()
                refreshNavigationView()
                refreshSaveButton()
                vPersonImage?.hash = predictionHistory?.latest?.person?.summary.hashCode()

                tvPersonInfo?.text = getString(
                    R.string.person_data,
                    predictionHistory?.latest?.person?.description,
                    predictionHistory?.latest?.person?.gender?.getName(
                        this@TempMainActivity, 1
                    ),
                    DateTimeHelper.toIso8601Date(predictionHistory?.latest?.person?.birthdate)
                ).toHtmlText()

                predictionHistory?.latest?.getFormattedContent(this@TempMainActivity)?.let {
                    tvPrediction?.setText(it.toClickableText(Pair("action") {
                        Sound.play(this@TempMainActivity, "crack")
                        tvPrediction?.let {
                            val replacement = StringHelper.replaceBetweenZeroWidthSpaces(
                                predictionHistory?.latest?.getFormattedContent(this@TempMainActivity),
                                predictionHistory?.latest?.components?.get("fortuneCookie")
                                    .orEmpty()
                            )
                            tvPrediction?.text = replacement
                        }
                    }), TextView.BufferType.SPANNABLE)
                    tvPrediction?.isClickable = true
                    tvPrediction?.movementMethod = LinkMovementMethod.getInstance()
                }

                if (activityState == ActivityState.RESUMED && !isViewVisible(tvPersonInfo)) showQuickToast(
                    predictionHistory?.latest?.person?.descriptor
                )

                //Read the text
                read(
                    tvPersonInfo?.text.toString() + ". " + predictionHistory?.latest?.getContent(
                        this@TempMainActivity
                    )
                )
                tvPick?.isEnabled = true
                llConfetti?.animate()?.alpha(1.0f)?.duration = 200 //Fade particles layout in
                PreferenceHandler.remove(Preference.TEMP_BUSY)
            }
        }
    }

    private fun refreshHolders() {
        when {
            people.isEmpty() -> {
                llInquiryHolder?.visibility = View.GONE
                llSelectorHolder?.visibility = View.GONE
            }

            people.size == 1 -> {
                tvInquiry?.text =
                    getString(R.string.inquiry, people[0].descriptor).toLinkedHtmlText()

                if (PreferenceUtils.hasPersonTempStored()) llInquiryHolder?.visibility = View.GONE
                else llInquiryHolder?.visibility = View.VISIBLE
                llSelectorHolder?.visibility = View.GONE
            }

            else -> {
                llInquiryHolder?.visibility = View.GONE
                llSelectorHolder?.visibility = View.VISIBLE
            }
        }

        PreferenceUtils.hasPersonTempStored().let {
            llDataEntryHolder?.visibility = if (it) View.GONE else View.VISIBLE
            llReloadHolder?.visibility = if (it) View.GONE else View.VISIBLE
            llClearHolder?.visibility = if (it) View.VISIBLE else View.GONE
        }
    }

    private fun refreshNavigationView() {
        PreferenceUtils.hasPersonTempStored().let {
            navigationView.menu.findItem(R.id.nav_data_entry).isEnabled = !it
            navigationView.menu.findItem(R.id.nav_data_entry).icon?.alpha = if (it) 125 else 255
            navigationView.menu.findItem(R.id.nav_reload).isEnabled = !it
            navigationView.menu.findItem(R.id.nav_reload).icon?.alpha = if (it) 125 else 255
            navigationView.menu.findItem(R.id.nav_save).isEnabled = !it
            navigationView.menu.findItem(R.id.nav_save).icon?.alpha = if (it) 125 else 255
            navigationView.menu.findItem(R.id.nav_clear).isEnabled = it
            navigationView.menu.findItem(R.id.nav_clear).icon?.alpha = if (it) 255 else 125
        }

        when {
            people.isEmpty() -> {
                navigationView.menu.findItem(R.id.nav_inquiry).isVisible = false
                navigationView.menu.findItem(R.id.nav_inquiry).isEnabled = false
                navigationView.menu.findItem(R.id.nav_inquiry).icon?.alpha = 125
                navigationView.menu.findItem(R.id.nav_selector).isVisible = false
                navigationView.menu.findItem(R.id.nav_selector).isEnabled = false
                navigationView.menu.findItem(R.id.nav_selector).icon?.alpha = 125
            }

            people.size == 1 -> {
                navigationView.menu.findItem(R.id.nav_inquiry).title =
                    getString(R.string.inquiry, people[0].descriptor)

                if (PreferenceUtils.hasPersonTempStored()) {
                    navigationView.menu.findItem(R.id.nav_inquiry).isVisible = true
                    navigationView.menu.findItem(R.id.nav_inquiry).isEnabled = false
                    navigationView.menu.findItem(R.id.nav_inquiry).icon?.alpha = 125
                } else {
                    navigationView.menu.findItem(R.id.nav_inquiry).isVisible = true
                    navigationView.menu.findItem(R.id.nav_inquiry).isEnabled = true
                    navigationView.menu.findItem(R.id.nav_inquiry).icon?.alpha = 255
                }
                navigationView.menu.findItem(R.id.nav_selector).isVisible = false
                navigationView.menu.findItem(R.id.nav_selector).isEnabled = false
                navigationView.menu.findItem(R.id.nav_selector).icon?.alpha = 125
            }

            else -> {
                navigationView.menu.findItem(R.id.nav_inquiry).isVisible = false
                navigationView.menu.findItem(R.id.nav_inquiry).isEnabled = false
                navigationView.menu.findItem(R.id.nav_inquiry).icon?.alpha = 125
                navigationView.menu.findItem(R.id.nav_selector).isVisible = true
                navigationView.menu.findItem(R.id.nav_selector).isEnabled = true
                navigationView.menu.findItem(R.id.nav_selector).icon?.alpha = 255
            }
        }
    }

    private fun refreshSaveButton() {
        PreferenceUtils.hasPersonTempStored().let {
            ivSaveLogo?.visibility = if (it) View.GONE else View.VISIBLE
            ivSaveLogo?.isEnabled = !it
            ivSaveLogo?.isClickable = !it
        }
    }

    private fun refreshUiUponEnquirySaved() {
        tvPick?.isEnabled = false
        updateInquirySelector()
        setFormPerson(people.last())
        tvPick?.isEnabled = true
    }

    private fun generatePrediction(formEntered: Boolean): Prediction {
        var prediction: Prediction

        status?.coroutineStarted?.takeIf { !it }.let {
            status?.coroutineStarted = true
            val enquiryDate = PreferenceUtils.getEnquiryDate()
            val person: Person

            if (formEntered) {
                person = PreferenceUtils.getFormPerson()
                person.addAttribute("entered")
            } else {
                person =
                    if (r?.getInt(3) == 0) resourceExplorer.generatorManager.personGenerator.anonymousPerson
                    else resourceExplorer.generatorManager.personGenerator.person
            }
            val divination = Divination(this@TempMainActivity, person, enquiryDate)
            prediction = divination.getPrediction(person)
            status?.coroutineStarted = false
        }
        return prediction
    }

    private fun displayCoupleCompatibility() {
        val initialName = atvInitialName?.text.toString().trim()
        val finalName = atvFinalName?.text.toString().trim()
        pbWait?.max = 100
        pbWait?.progress = 0

        if (initialName.isNotBlank() && finalName.isNotBlank()) {
            val compatibilityPoints = CoupleCompatibility.calculate(initialName, finalName)
            tvCompatibility?.text = getString(
                R.string.compatibility_result, TextFormatter.formatCapacity(compatibilityPoints)
            ).toHtmlText()
            pbWait?.progress = compatibilityPoints
        } else {
            tvCompatibility?.text = getString(
                R.string.compatibility_result, "<font color=\"#C0FF2B\">?</font>"
            ).toHtmlText()
            pbWait?.progress = 0
        }
    }

    private fun displayGeneratedName() {
        status?.nameInGeneration?.takeIf { !it }.let {
            status?.nameInGeneration = true
            spnNameType?.isEnabled = false
            val nameType = NameType.values()[spnNameType?.selectedItemPosition ?: 0]
            tvNameBox?.setText(GeneratorManager(Locale("xx")).nameGenerator.getNameOrRetry(nameType))
            spnNameType?.isEnabled = true
            status?.nameInGeneration = false
        }
    }
}
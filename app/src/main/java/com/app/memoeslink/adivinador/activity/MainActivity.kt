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
import android.text.Editable
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver.OnGlobalLayoutListener
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ProgressBar
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.IdRes
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.appcompat.widget.AppCompatImageView
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.content.ContextCompat
import androidx.core.text.toSpannable
import androidx.lifecycle.lifecycleScope
import androidx.multidex.BuildConfig
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.app.memoeslink.adivinador.ActivityState
import com.app.memoeslink.adivinador.AdUnitId
import com.app.memoeslink.adivinador.CoupleCompatibility
import com.app.memoeslink.adivinador.Database
import com.app.memoeslink.adivinador.Divination
import com.app.memoeslink.adivinador.FortuneTeller
import com.app.memoeslink.adivinador.Identity
import com.app.memoeslink.adivinador.Prediction
import com.app.memoeslink.adivinador.PredictionHistory
import com.app.memoeslink.adivinador.R
import com.app.memoeslink.adivinador.Screen
import com.app.memoeslink.adivinador.Sound
import com.app.memoeslink.adivinador.Speech
import com.app.memoeslink.adivinador.extensions.getCurrentWindowPoint
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
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import com.lelloman.identicon.view.GithubIdenticonView
import com.memoeslink.common.Randomizer
import com.memoeslink.generator.common.DateTimeHelper
import com.memoeslink.generator.common.GeneratorManager
import com.memoeslink.generator.common.LongHelper
import com.memoeslink.generator.common.NameType
import com.memoeslink.generator.common.Person
import com.memoeslink.generator.common.StringHelper
import com.memoeslink.generator.common.TextFormatter
import com.memoeslink.generator.common.ZeroWidthChar
import com.memoeslink.manager.Device
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.Locale
import java.util.Random
import java.util.Timer
import kotlin.concurrent.fixedRateTimer
import kotlin.system.exitProcess

class MainActivity : MenuActivity() {
    private val particleColors = mapOf(
        "legacy" to intArrayOf(
            Color.BLUE,
            Color.argb(255, 0, 128, 255),
            Color.argb(255, 51, 153, 255),
            Color.argb(255, 0, 192, 199),
            Color.argb(125, 0, 128, 255),
            Color.argb(125, 51, 153, 255),
            Color.argb(125, 0, 192, 199)
        ), "default" to intArrayOf(
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
    private var llAdContent: LinearLayout? = null
    private var givPersonImage: GithubIdenticonView? = null
    private var givDetailsPersonImage: GithubIdenticonView? = null
    private var ivFortuneTeller: AppCompatImageView? = null
    private var ivDetailsLogo: AppCompatImageView? = null
    private var ivSaveLogo: AppCompatImageView? = null
    private var ivAdDismiss: AppCompatImageView? = null
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
    private var tvNameBox: TextView? = null
    private var tvDetails: TextView? = null
    private var spnNameType: AppCompatSpinner? = null
    private var dpdInquiryDate: DatePickerDialog? = null
    private var pbWait: ProgressBar? = null
    private var btDataEntry: Button? = null
    private var btTextCopy: Button? = null
    private var vMain: View? = null
    private var vCompatibility: View? = null
    private var vNameGenerator: View? = null
    private var vDetails: View? = null
    private var adView: AdView? = null
    private var interstitialAd: InterstitialAd? = null
    private var adRequest: AdRequest? = null
    private var confettiManager: ConfettiManager? = null
    private var compatibilityDialog: AlertDialog? = null
    private var nameGeneratorDialog: AlertDialog? = null
    private var detailsDialog: AlertDialog? = null
    private var dialog: AlertDialog? = null
    private var timer: Timer? = null
    private var fortuneTeller: FortuneTeller? = null
    private var device: Device? = null
    private var r: Randomizer? = Randomizer()
    private var predictionHistory: PredictionHistory? = PredictionHistory()
    private var backupPredictions: PredictionHistory? = PredictionHistory()
    private var people: List<Person> = ArrayList()
    private var listener: OnSharedPreferenceChangeListener? =
        null // Declared as global to avoid destruction by JVM Garbage Collector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        MobileAds.initialize(this@MainActivity)
        srlRefresher = findViewById(R.id.main_refresh_layout)
        rlAdContainer = findViewById(R.id.ad_container)
        llConfetti = findViewById(R.id.main_confetti_layout)
        llDataEntryHolder = findViewById(R.id.main_data_entry_holder)
        llReloadHolder = findViewById(R.id.main_reload_holder)
        llInquiryHolder = findViewById(R.id.main_inquiry_holder)
        llSelectorHolder = findViewById(R.id.main_selector_holder)
        llClearHolder = findViewById(R.id.main_clear_holder)
        llAdContent = findViewById(R.id.ad_content)
        givPersonImage = findViewById(R.id.main_person_image)
        ivFortuneTeller = findViewById(R.id.main_fortune_teller)
        ivDetailsLogo = findViewById(R.id.main_details)
        ivSaveLogo = findViewById(R.id.main_save)
        ivAdDismiss = findViewById(R.id.ad_dismiss)
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
        val inflater: LayoutInflater = this@MainActivity.layoutInflater
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
        vDetails = inflater.inflate(R.layout.dialog_person_details, null)
        tvDetails = vDetails?.findViewById(R.id.dialog_details)
        givDetailsPersonImage = vDetails?.findViewById(R.id.dialog_person_image)
        drawerLayout = findViewById(R.id.drawer_layout)
        navigationView = findViewById(R.id.navigation_view)
        navigationView?.itemIconTintList = null

        fortuneTeller = FortuneTeller(this@MainActivity)
        device = Device(this@MainActivity)

        // Request ads
        val testDevices: MutableList<String> = java.util.ArrayList()
        testDevices.add(AdRequest.DEVICE_ID_EMULATOR)
        testDevices.add(device?.getAndroidId() ?: "0".repeat(32))
        var requestConfiguration = RequestConfiguration.Builder().build()

        if (BuildConfig.DEBUG) requestConfiguration =
            RequestConfiguration.Builder().setTestDeviceIds(testDevices).build()
        MobileAds.setRequestConfiguration(requestConfiguration)
        adRequest = AdRequest.Builder().build()

        adRequest?.isTestDevice(this@MainActivity)?.takeIf {
            !it && BuildConfig.DEBUG
        }?.let {
            println("This device will not show test ads.")
        }

        // Preload prediction
        backupPredictions?.add(generatePrediction(PreferenceUtils.isEnquiryFormEntered()))

        // Set empty prediction
        val emptyPrediction = Prediction.PredictionBuilder().build()
        tvPrediction?.text = emptyPrediction.getFormattedContent(this@MainActivity).toHtmlText()

        // Get a greeting
        if (PreferenceHandler.getBoolean(Preference.SETTING_GREETINGS_ENABLED)) tvPhrase?.text =
            fortuneTeller?.greet().toHtmlText()
        else tvPhrase?.text = "…"

        // Change drawable for fortune teller
        fortuneTeller?.randomAppearance?.let { ivFortuneTeller?.setImageResource(it) }

        // Delete temporary preferences
        PreferenceUtils.deleteTemp()

        // Initialize components
        val nameAdapter: ArrayAdapter<String> = object : ArrayAdapter<String>(
            this@MainActivity,
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

        val wrapper = ContextThemeWrapper(this@MainActivity, R.style.CustomDialog)
        val compatibilityBuilder = AlertDialog.Builder(wrapper)
        compatibilityBuilder.setTitle(R.string.compatibility)
        compatibilityBuilder.setNegativeButton(R.string.action_close) { _, _ -> compatibilityDialog?.dismiss() }
        compatibilityBuilder.setView(vCompatibility)
        compatibilityDialog = compatibilityBuilder.create()

        val nameGeneratorBuilder = AlertDialog.Builder(wrapper)
        nameGeneratorBuilder.setTitle(R.string.name_generation)
        nameGeneratorBuilder.setPositiveButton(R.string.action_generate, null)
        nameGeneratorBuilder.setNegativeButton(R.string.action_close) { _, _ -> nameGeneratorDialog?.dismiss() }
        nameGeneratorBuilder.setView(vNameGenerator)
        nameGeneratorDialog = nameGeneratorBuilder.create()

        nameGeneratorDialog?.setOnShowListener { dialog: DialogInterface ->
            val button = (dialog as AlertDialog).getButton(AlertDialog.BUTTON_POSITIVE)
            button.setOnClickListener { displayGeneratedName() }
        }

        val detailsBuilder = AlertDialog.Builder(wrapper)
        detailsBuilder.setTitle(getString(R.string.person_details))
        detailsBuilder.setNegativeButton(R.string.action_close) { _, _ -> detailsDialog?.dismiss() }
        detailsBuilder.setView(vDetails)
        detailsDialog = detailsBuilder.create()

        srlRefresher?.setOnRefreshListener {
            refreshPrediction()
            srlRefresher?.isRefreshing = false
        }

        navigationView?.setNavigationItemSelectedListener { item: MenuItem ->
            if (PreferenceHandler.getBoolean(Preference.SETTING_HIDE_DRAWER, true)) closeDrawer()

            when (item.itemId) {
                R.id.nav_data_entry -> {
                    val i = Intent(this@MainActivity, InputActivity::class.java)
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

        DateTimeHelper.getCurrentDate()?.let {
            dpdInquiryDate = DatePickerDialog(
                this@MainActivity, { datePicker: DatePicker, _: Int, _: Int, _: Int ->
                    PreferenceHandler.put(Preference.TEMP_YEAR_OF_ENQUIRY, datePicker.year)
                    PreferenceHandler.put(Preference.TEMP_MONTH_OF_ENQUIRY, datePicker.month + 1)
                    PreferenceHandler.put(Preference.TEMP_DAY_OF_ENQUIRY, datePicker.dayOfMonth)
                    backupPredictions?.clearHistory()
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
            if (PreferenceHandler.getStringAsInt(
                    Preference.SETTING_FORTUNE_TELLER_ASPECT, 1
                ) != 0
            ) {
                Sound.play(this@MainActivity, "jump")
                BounceAnimation(ivFortuneTeller).setBounceDistance(7f).setNumOfBounces(1)
                    .setDuration(150).animate()
            }
        }

        ivDetailsLogo?.setOnClickListener {
            it.isEnabled = false
            refreshDetailsDisplay()
            detailsDialog?.show()
            it.isEnabled = true
        }

        ivSaveLogo?.setOnClickListener {
            predictionHistory?.latest?.person?.let { person ->
                if (PreferenceUtils.savePerson(person)) {
                    showToast(getString(R.string.toast_inquiry_saved))
                    refreshUiUponEnquirySaved()
                } else showToast(getString(R.string.toast_inquiry_not_saved))
            }
        }

        ivAdDismiss?.setOnClickListener { destroyAd() }

        llDataEntryHolder?.setOnClickListener {
            val i = Intent(this@MainActivity, InputActivity::class.java)
            startActivity(i)
        }

        llReloadHolder?.setOnClickListener { refreshPrediction() }

        llInquiryHolder?.setOnClickListener { displayFormPerson(people[0]) }

        llSelectorHolder?.setOnClickListener {
            if (dialog != null && !PreferenceHandler.has(Preference.TEMP_BUSY)) dialog?.show()
        }

        llClearHolder?.setOnClickListener {
            PreferenceUtils.clearForm()
            refreshPrediction()
        }

        btDataEntry?.setOnClickListener {
            val i = Intent(this@MainActivity, InputActivity::class.java)
            startActivity(i)
        }

        btTextCopy?.setOnClickListener {
            predictionHistory?.takeUnless { it.isEmpty }?.let { history ->
                copyTextToClipboard(history.latest?.getContent(this@MainActivity))
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
                if (s.toString().trim().isEmpty()) tvTextCopy?.visibility = View.GONE
                else tvTextCopy?.visibility = View.VISIBLE
            }

            override fun afterTextChanged(s: Editable) {}
        })

        tvTextCopy?.setOnClickListener {
            copyTextToClipboard(tvNameBox?.text.toString())
        }

        listener = OnSharedPreferenceChangeListener { _: SharedPreferences?, key: String? ->
            if (key == Preference.DATA_STORED_PEOPLE.tag) {
                dialog?.takeIf { it.isShowing }?.dismiss()
                dialog = null
                //recreate()
                updateInquirySelector()
            }

            if (key == Preference.DATA_STORED_NAMES.tag) updateNameSuggestions()
        }
        PreferenceHandler.changePreferencesListener(listener)

        onBackPressedDispatcher.addCallback(this, object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (!closeDrawer()) AlertDialog.Builder(this@MainActivity)
                    .setTitle(R.string.alert_exit_title).setMessage(R.string.alert_exit_message)
                    .setNegativeButton(R.string.action_cancel, null)
                    .setPositiveButton(R.string.action_exit) { _, _ ->
                        exitProcess(0) // Try to stop current threads
                    }.create().show()
            }
        })
    }

    override fun onStart() {
        super.onStart()
        updateNameSuggestions()

        // Request permissions
        val launcher =
            registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { isGranted: Map<String, Boolean> ->
                if (isGranted.containsValue(false)) showToast(getString(R.string.denied_permission))
            }
        var permissionsGranted = true
        val permissions = arrayOf(
            Manifest.permission.INTERNET,
            Manifest.permission.READ_CONTACTS,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.CHANGE_NETWORK_STATE,
            Manifest.permission.ACCESS_WIFI_STATE,
            Manifest.permission.CHANGE_WIFI_STATE
        )

        for (permission in permissions) {
            if (ContextCompat.checkSelfPermission(
                    this@MainActivity, permission
                ) != PackageManager.PERMISSION_GRANTED
            ) permissionsGranted = false
        }

        if (!permissionsGranted) launcher.launch(permissions)
    }

    override fun onResume() {
        super.onResume()
        LongHelper.getSeed(PreferenceHandler.getString(Preference.SETTING_SEED))
            ?.takeIf { it != fortuneTeller?.seed }?.let {
                fortuneTeller = FortuneTeller(this@MainActivity)
            }

        // Restart Activity if required
        if (PreferenceHandler.has(Preference.TEMP_RESTART_ACTIVITY)) {
            val intent = intent
            finish()
            startActivity(intent)
        }
        PreferenceHandler.remove(Preference.TEMP_RESTART_ACTIVITY)

        // Show, avoid, or hide ads
        prepareAd(false)

        // Update selectable people list
        updateInquirySelector()

        // Get a prediction
        if (!status.initialized || (PreferenceUtils.hasPersonTempStored() && (PreferenceUtils.getFormPerson().summary != predictionHistory?.latest?.person?.summary || predictionHistory?.latest?.date != DateTimeHelper.getStrCurrentDate()))) refreshPrediction()

        // Change drawable if the 'fortune teller aspect' preference was changed
        if (PreferenceHandler.has(Preference.TEMP_CHANGE_FORTUNE_TELLER)) {
            fortuneTeller?.randomAppearance?.let { ivFortuneTeller?.setImageResource(it) }
            PreferenceHandler.remove(Preference.TEMP_CHANGE_FORTUNE_TELLER)
        }

        if (timer == null) {
            timer = fixedRateTimer("timer", false, 0L, 1000) {
                val elapsedSeconds = status.counters.getOrPut("elapsedSeconds") { 0L }
                val fortuneTellerRefresh = status.counters.getOrPut("fortuneTellerRefresh") { 0L }
                val predictionRefresh = status.counters.getOrPut("predictionRefresh") { 0L }
                val resourceReload = status.counters.getOrPut("resourceReload") { 0L }
                val frequency =
                    PreferenceHandler.getStringAsInt(Preference.SETTING_REFRESH_TIME, 20)
                val refreshFrequency =
                    PreferenceHandler.getStringAsInt(Preference.SETTING_UPDATE_TIME, 60)

                if (fortuneTellerRefresh >= frequency && frequency != 0) {
                    this@MainActivity.runOnUiThread {
                        // Fade out and fade in fortune-telling text
                        if (activityState in ActivityState.RESUMED..ActivityState.POST_RESUMED) tvPhrase?.let {
                            fadeAndShowView(
                                it
                            )
                        }

                        Handler(Looper.getMainLooper()).postDelayed({
                            // Change drawable for the fortune teller
                            fortuneTeller?.randomAppearance?.let {
                                ivFortuneTeller?.setImageResource(
                                    it
                                )
                            }

                            // Get random phrase from the fortune teller
                            val sounds = arrayOf("blip1", "blip2", "bum", "chime_notification", "counter", "level_up", "notification", "notification_sound", "water_drop")
                            Sound.play(this@MainActivity, r?.getElement(sounds))
                            tvPhrase?.text = fortuneTeller?.talk().toHtmlText()

                            // Read the text
                            if (PreferenceHandler.getStringAsInt(Preference.SETTING_TEXT_TYPE) == 0 || PreferenceHandler.getStringAsInt(
                                    Preference.SETTING_TEXT_TYPE
                                ) == 2
                            ) Speech.getInstance(this@MainActivity).speak(tvPhrase?.text.toString())
                        }, 350)
                    }
                    status.counters["fortuneTellerRefresh"] = 0L
                } else status.counters["fortuneTellerRefresh"] = fortuneTellerRefresh.inc()

                if (predictionRefresh >= refreshFrequency && (!PreferenceUtils.hasPersonTempStored() || PreferenceUtils.getFormPerson().summary != predictionHistory?.latest?.person?.summary || predictionHistory?.latest?.date != DateTimeHelper.getStrCurrentDate())) refreshPrediction()
                else {
                    if (predictionRefresh != 0L && predictionRefresh % 10 == 0L) {
                        backupPredictions?.takeIf { !it.isFull }?.let {
                            lifecycleScope.launch(Dispatchers.IO) {
                                backupPredictions?.add(generatePrediction(false))
                            }
                        }
                    }
                    status.counters["predictionRefresh"] = predictionRefresh.inc()
                }

                if (resourceReload >= 1800) status.counters["resourceReload"] = 0L
                else status.counters["resourceReload"] = resourceReload.inc()
                status.counters["elapsedSeconds"] = elapsedSeconds.inc()
            }
        }
        prepareAnimation()

        // Show full-screen ads
        device?.getAndroidId()?.let {
            val summarization =
                "$it${System.getProperty("line.separator")}${DateTimeHelper.getCurrentDateTime()}"
            val seed = LongHelper.getSeed(summarization)
            Randomizer(seed)
        }?.takeIf {
            it.getInt(50) == 0
        }?.let {
            showInterstitialAd()
        }
    }

    override fun onPause() {
        super.onPause()
        stopConfetti() // Finish any confetti animation
        adView?.pause()
    }

    override fun onDestroy() {
        timer?.let {
            it.cancel()
            it.purge()
            timer = null
        }
        adView?.destroy()
        super.onDestroy()
    }

    override fun onConfigurationChanged(newConfig: Configuration) {
        super.onConfigurationChanged(newConfig)
        prepareAnimation()
        prepareAd(true)
    }

    private fun prepareAd(restarted: Boolean) {
        if (PreferenceHandler.getBoolean(Preference.SETTING_ADS_ENABLED, true)) {
            if (status.adPaused) return

            if (status.adAdded || !restarted) destroyAd()

            if (!status.adAdded) {
                adView = AdView(this@MainActivity)
                adView?.setAdSize(AdSize.BANNER)
                adView?.adUnitId = AdUnitId.getBannerId()

                // Set observer to get ad view height
                adView?.viewTreeObserver?.addOnGlobalLayoutListener(object :
                    OnGlobalLayoutListener {
                    override fun onGlobalLayout() {
                        try {
                            val params = vMain?.layoutParams
                            params?.height = adView?.measuredHeight
                            vMain?.requestLayout()
                            adView?.viewTreeObserver?.removeOnGlobalLayoutListener(this)
                        } catch (ignored: Exception) {
                        }
                    }
                })

                adView?.adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        llAdContent?.addView(adView)
                        rlAdContainer?.visibility = View.VISIBLE
                        status.adAdded = true
                        println("The ad was loaded successfully.")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        println("The ad couldn't be loaded: " + error.message)
                        destroyAd()
                    }
                }

                try {
                    adRequest?.let { adView?.loadAd(it) }
                } catch (e: java.lang.Exception) {
                    destroyAd()
                }
            }
        }
    }

    private fun destroyAd() {
        adView?.takeIf { !PreferenceHandler.getBoolean(Preference.TEMP_RESTART_ADS) }?.let {
            PreferenceHandler.put(Preference.TEMP_RESTART_ADS, true)
            val params = vMain?.layoutParams
            params?.height = 0
            adView?.destroy()
            rlAdContainer?.visibility = View.GONE
            llAdContent?.removeAllViews()
            adView = null
            status.adAdded = false
            PreferenceHandler.remove(Preference.TEMP_RESTART_ADS)
        }
    }

    private fun showInterstitialAd() {
        if (PreferenceHandler.getBoolean(Preference.SETTING_ADS_ENABLED)) {
            this@MainActivity.runOnUiThread {
                if (adView != null && adView?.visibility == View.VISIBLE) destroyAd()

                adRequest?.let { adRequest ->
                    InterstitialAd.load(this,
                        AdUnitId.getInterstitialId(),
                        adRequest,
                        object : InterstitialAdLoadCallback() {
                            override fun onAdLoaded(ad: InterstitialAd) {
                                super.onAdLoaded(ad)
                                interstitialAd = ad
                                interstitialAd?.show(this@MainActivity)
                                println("The interstitial ad was loaded successfully.")
                            }

                            override fun onAdFailedToLoad(error: LoadAdError) {
                                println("The interstitial ad couldn't be loaded: " + error.message)
                                prepareAd(false) // Show, avoid, or hide ads@Override
                                tvSelector?.invalidate() // Force view to be redrawn
                            }
                        })
                }
            }
        }
    }

    private fun prepareAnimation() {
        Screen.lockScreenOrientation(this@MainActivity) // Lock orientation
        stopConfetti() // Finish previous confetti animation

        // Redraw view
        llConfetti?.requestLayout()
        llConfetti?.invalidate()

        // Measure screen and views
        if (status.measuredTimes == 0L) llConfetti?.post { performMeasurements() }
        else Handler(Looper.getMainLooper()).postDelayed({ performMeasurements() }, 1000)

        // Wait for measurements to be done
        waitTasks()
    }

    private fun waitTasks() {
        if (!status.screenMeasured || status.measurements.isEmpty()) {
            Handler(Looper.getMainLooper()).postDelayed(
                { waitTasks() }, 500
            )
            return
        }

        if (activityState in ActivityState.RESUMED..ActivityState.POST_RESUMED) {
            val originInX: Int = status.measurements.getOrDefault("confettiOriginInX", 0)
            val originInY: Int = status.measurements.getOrDefault("confettiOriginInY", 0)

            if (PreferenceHandler.getBoolean(Preference.SETTING_PARTICLES_ENABLED) && originInX > 0 && originInY > 0) throwConfetti(
                originInX, originInY
            )

            if (PreferenceHandler.getStringAsInt(
                    Preference.SETTING_FORTUNE_TELLER_ASPECT, 1
                ) != 0
            ) {
                Sound.play(this@MainActivity, "jump")
                BounceAnimation(ivFortuneTeller).setBounceDistance(20f).setNumOfBounces(1)
                    .setDuration(500).animate()
            }
            Screen.unlockScreenOrientation(this@MainActivity) // Unlock orientation
        }
    }

    private fun performMeasurements() {
        status.screenMeasured = false
        val point: Point = windowManager.getCurrentWindowPoint()
        status.screenWidth = point.x
        status.screenHeight = point.y
        status.measurements["confettiLayoutWidth"] = llConfetti?.width ?: 0
        status.measurements["confettiLayoutHeight"] = llConfetti?.height ?: 0
        status.measurements["confettiOriginInX"] = (llConfetti?.width ?: 0) / 2
        status.measurements["confettiOriginInY"] = (llConfetti?.height ?: 0) / 2
        status.screenMeasured = true

        if (status.measuredTimes < Long.MAX_VALUE) status.measuredTimes++
        println(
            "Confetti layout measures: ${status.measurements["confettiLayoutWidth"]}×${
                status.measurements["confettiLayoutHeight"]
            } [Confetti origin in: ${status.measurements["confettiOriginInX"]} axis X; ${
                status.measurements["confettiOriginInY"]
            } axis Y]"
        )
    }

    private fun throwConfetti(originInX: Int, originInY: Int) {
        val colors = if (PreferenceHandler.getStringAsInt(
                Preference.SETTING_FORTUNE_TELLER_ASPECT, 1
            ) == 0
        ) particleColors["legacy"]
        else particleColors["default"]
        val allPossibleConfetti = Utils.generateConfettiBitmaps(colors, 10)
        val generator = ConfettoGenerator { random: Random ->
            val bitmap = allPossibleConfetti[random.nextInt(allPossibleConfetti.size)]
            BitmapConfetto(bitmap)
        }

        confettiManager = ConfettiManager(
            this@MainActivity, generator, ConfettiSource(originInX, originInY), llConfetti
        ).setEmissionDuration(ConfettiManager.INFINITE_DURATION).setEmissionRate(30f)
            .setVelocityX(0f, 360f).setVelocityY(0f, 360f).setRotationalVelocity(180f, 180f)
            .setRotationalAcceleration(360f, 180f).setInitialRotation(180, 180)
            .setTargetRotationalVelocity(360f).enableFadeOut(Utils.getDefaultAlphaInterpolator())
            .animate()

        if (status.confettiThrown < Long.MAX_VALUE) status.confettiThrown++
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
                    this@MainActivity, android.R.layout.simple_spinner_dropdown_item, names
                )
            )
            atvFinalName?.setAdapter(
                ArrayAdapter(
                    this@MainActivity, android.R.layout.simple_spinner_dropdown_item, names
                )
            )
        }
    }

    private fun updateInquirySelector() {
        people = PreferenceUtils.getStoredPeople()
        val items: MutableList<String> = mutableListOf()

        for (person in people) {
            items.add(
                "${person.descriptor} (${
                    person.gender.getName(
                        this@MainActivity, 4
                    )
                }) ${person.birthdate}"
            )
        }
        val builder = AlertDialog.Builder(this@MainActivity)
        builder.setNegativeButton(R.string.action_cancel) { _, _ -> dialog?.dismiss() }
        val listView = ListView(this@MainActivity)
        val adapter = ArrayAdapter(
            this@MainActivity, android.R.layout.simple_list_item_1, android.R.id.text1, items
        )
        listView.adapter = adapter
        listView.onItemClickListener =
            OnItemClickListener { _: AdapterView<*>?, _: View?, position: Int, _: Long ->
                displayFormPerson(people[position])
                dialog?.dismiss()
            }
        builder.setView(listView)
        dialog = builder.create()
        refreshHolders()
    }

    private fun displayFormPerson(person: Person) {
        if (!PreferenceUtils.isPersonTempStored(person)) {
            PreferenceUtils.saveFormPerson(person)
            refreshPrediction()
        }
    }

    private fun refreshPrediction() {
        if (PreferenceHandler.has(Preference.TEMP_BUSY)) return

        lifecycleScope.launch(Dispatchers.IO) {
            PreferenceHandler.put(Preference.TEMP_BUSY, true)
            Sound.play(this@MainActivity, "ding")

            this@MainActivity.runOnUiThread {
                tvPick?.isEnabled = false
                status.counters["predictionRefresh"] = 0L

                llConfetti?.animate()?.alpha(0.0f)?.setDuration(200)
                    ?.setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationCancel(animation: Animator) {
                            super.onAnimationEnd(animation)
                            vMain?.clearAnimation()
                            vMain?.visibility = View.INVISIBLE
                        }
                    }) // Fade out particles layout
            }

            val pickedDate = DateTimeHelper.toIso8601Date(
                dpdInquiryDate?.datePicker?.year ?: 2000,
                dpdInquiryDate?.datePicker?.month?.plus(1) ?: 1,
                dpdInquiryDate?.datePicker?.dayOfMonth ?: 1
            )
            val formEntered = PreferenceUtils.isEnquiryFormEntered()
            var preStoredPrediction: Prediction = Prediction.PredictionBuilder().build()
            var retrieved = false

            predictionHistory?.takeIf { !formEntered || it.isEmpty }?.let {
                backupPredictions?.takeIf { !it.isEmpty }?.let { backups ->
                    if (backups.oldest.date != pickedDate) backups.clearHistory()

                    while (!backups.isEmpty) {
                        backups.dispenseOldest()
                            ?.takeIf { prediction -> !prediction.person.hasAttribute("empty") && prediction.date == pickedDate }
                            ?.let { prediction ->
                                preStoredPrediction = prediction
                                retrieved = true
                            }

                        if (retrieved) break
                    }
                }
            }

            if (retrieved) predictionHistory?.add(preStoredPrediction)
            else predictionHistory?.add(generatePrediction(formEntered))

            this@MainActivity.runOnUiThread {
                refreshHolders()
                refreshNavigationView()
                refreshSaveButton()
                givPersonImage?.hash = predictionHistory?.latest?.person?.summary.hashCode()

                tvPersonInfo?.text = getString(
                    R.string.person_data,
                    predictionHistory?.latest?.person?.description,
                    predictionHistory?.latest?.person?.gender?.getName(
                        this@MainActivity, 1
                    ),
                    DateTimeHelper.toIso8601Date(predictionHistory?.latest?.person?.birthdate)
                ).toHtmlText()

                predictionHistory?.latest?.getFormattedContent(this@MainActivity)?.let { content ->
                    tvPrediction?.text = content.toHtmlText().toSpannable().also { spannable ->
                        val clickableSpan: ClickableSpan = object : ClickableSpan() {
                            override fun onClick(textView: View) {
                                Sound.play(this@MainActivity, "crack")
                                tvPrediction?.let {
                                    val replacement = StringHelper.replaceBetweenZeroWidthSpaces(
                                        predictionHistory?.latest?.getFormattedContent(this@MainActivity),
                                        predictionHistory?.latest?.components?.get("fortuneCookie")
                                            .orEmpty()
                                    )
                                    tvPrediction?.text = replacement.toHtmlText()
                                }
                            }

                            override fun updateDrawState(ds: TextPaint) {
                                super.updateDrawState(ds)
                                ds.isUnderlineText = false
                            }
                        }
                        val start =
                            spannable.indexOfFirst { c -> c == ZeroWidthChar.ZERO_WIDTH_SPACE.character } + 1
                        val end = start.plus(getString(R.string.prediction_action).length)
                        spannable.setSpan(
                            clickableSpan, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }
                    tvPrediction?.isClickable = true
                    tvPrediction?.movementMethod = LinkMovementMethod.getInstance()
                }

                if (activityState in ActivityState.RESUMED..ActivityState.POST_RESUMED && !isViewVisible(
                        tvPersonInfo
                    )
                ) showToast(predictionHistory?.latest?.person?.descriptor, true)

                // Read the text
                if (PreferenceHandler.getStringAsInt(Preference.SETTING_TEXT_TYPE) == 1 || PreferenceHandler.getStringAsInt(
                        Preference.SETTING_TEXT_TYPE
                    ) == 2
                ) Speech.getInstance(this@MainActivity).speak(
                    tvPersonInfo?.text.toString() + ". " + predictionHistory?.latest?.getContent(
                        this@MainActivity
                    )
                )

                detailsDialog?.isShowing?.takeIf { it }?.let {
                    detailsDialog?.hide()
                    refreshDetailsDisplay()
                    detailsDialog?.show()
                }
                tvPick?.isEnabled = true
                llConfetti?.animate()?.alpha(1.0f)?.duration = 200 // Fade in particles layout
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

    private fun refreshNavigationViewItem(
        @IdRes itemId: Int, enabled: Boolean, visible: Boolean = true
    ) {
        navigationView?.menu?.findItem(itemId)?.isVisible = visible
        navigationView?.menu?.findItem(itemId)?.isEnabled = enabled
        navigationView?.menu?.findItem(itemId)?.icon?.alpha = if (enabled) 255 else 125
    }

    private fun refreshNavigationView() {
        PreferenceUtils.hasPersonTempStored().let {
            refreshNavigationViewItem(R.id.nav_data_entry, enabled = !it)
            refreshNavigationViewItem(R.id.nav_reload, enabled = !it)
            refreshNavigationViewItem(R.id.nav_save, enabled = !it)
            refreshNavigationViewItem(R.id.nav_clear, enabled = it)
        }
        navigationView?.menu?.findItem(R.id.nav_inquiry)?.title = "…"

        when {
            people.isEmpty() -> {
                refreshNavigationViewItem(R.id.nav_inquiry, enabled = false, visible = false)
                refreshNavigationViewItem(R.id.nav_selector, enabled = false, visible = false)
            }

            people.size == 1 -> {
                navigationView?.menu?.findItem(R.id.nav_inquiry)?.title =
                    getString(R.string.inquiry, people[0].descriptor)

                if (PreferenceUtils.hasPersonTempStored()) refreshNavigationViewItem(
                    R.id.nav_inquiry, enabled = false, visible = true
                )
                else refreshNavigationViewItem(R.id.nav_inquiry, enabled = true, visible = true)
                refreshNavigationViewItem(R.id.nav_selector, enabled = false, visible = false)
            }

            else -> {
                refreshNavigationViewItem(R.id.nav_inquiry, enabled = false, visible = false)
                refreshNavigationViewItem(R.id.nav_selector, enabled = true, visible = true)
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
        displayFormPerson(people.last())
        tvPick?.isEnabled = true
    }

    private fun generatePrediction(formEntered: Boolean): Prediction {
        var prediction: Prediction = Prediction.PredictionBuilder().build()

        if (!status.tasks.contains("generatePrediction")) {
            status.tasks.add("generatePrediction")
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
            val divination = Divination(this@MainActivity, person, enquiryDate)
            prediction = divination.prediction
            status.tasks.remove("generatePrediction")
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
        if (!status.tasks.contains("generateName")) {
            status.tasks.add("generateName")
            spnNameType?.isEnabled = false
            val nameType = NameType.values()[spnNameType?.selectedItemPosition ?: 0]
            Sound.play(this@MainActivity, "wind")
            tvNameBox?.text = GeneratorManager(Locale("xx")).nameGenerator.getNameOrRetry(nameType)
            spnNameType?.isEnabled = true
            status.tasks.remove("generateName")
        }
    }

    private fun refreshDetailsDisplay() {
        val person = predictionHistory?.latest?.person ?: Person.PersonBuilder().build()
        val identity = person?.let {
            givDetailsPersonImage?.hash = it.summary.hashCode()
            Identity(this@MainActivity, it)
        }
        tvDetails?.text = identity?.information?.withDefault { Database.DEFAULT_VALUE }?.let {
            getString(
                R.string.person_information,
                TextFormatter.formatDescriptor(person),
                it.getValue("zodiacSign"),
                it.getValue("astrologicalHouse"),
                it.getValue("ruler"),
                it.getValue("element"),
                it.getValue("signColor"),
                it.getValue("signNumbers"),
                it.getValue("compatibility"),
                it.getValue("incompatibility"),
                it.getValue("walterBergZodiacSign"),
                it.getValue("chineseZodiacSign"),
                it.getValue("animal"),
                it.getValue("psychologicalType"),
                it.getValue("secretName"),
                it.getValue("demonicName"),
                it.getValue("previousName"),
                it.getValue("futureName"),
                it.getValue("recommendedUsername"),
                it.getValue("uniqueColor"),
                it.getValue("daysBetweenDates"),
                it.getValue("timeBetweenDates")
            ).toHtmlText()
        }
    }
}
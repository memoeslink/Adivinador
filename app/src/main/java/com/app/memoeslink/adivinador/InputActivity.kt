package com.app.memoeslink.adivinador

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.reflect.TypeToken
import com.memoeslink.generator.common.DateTimeHelper
import com.memoeslink.generator.common.Gender
import com.memoeslink.generator.common.Person
import com.memoeslink.generator.common.StringHelper
import java.lang.reflect.Type
import java.time.LocalDate
import kotlin.math.abs

class InputActivity : CommonActivity() {
    private var tvName: AppCompatAutoCompleteTextView? = null
    private var rgGender: RadioGroup? = null
    private var dpBirthdate: CustomDatePicker? = null
    private var btBack: Button? = null
    private var methods: Methods? = null
    private var listener: OnSharedPreferenceChangeListener? =
            null //Declared as global to avoid destruction by JVM Garbage Collector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)
        btBack = findViewById(R.id.input_back_button)
        tvName = findViewById(R.id.input_name_field)
        rgGender = findViewById(R.id.input_gender_radio)
        dpBirthdate = findViewById(R.id.input_date_picker)
        methods = Methods(this@InputActivity)

        //Set max. and min. date
        val currentDate = DateTimeHelper.getCurrentDate()

        //Initialize values
        preferences.getStringOrNull("temp_name")?.takeUnless { name ->
            name.isBlank()
        }?.let { name ->
            tvName?.setText(name)
        }

        preferences.getIntOrNull("temp_gender")?.let { gender ->
            (rgGender?.getChildAt(gender) as RadioButton).isChecked = true
        } ?: preferences.putInt("temp_gender", Gender.NEUTRAL.value)

        preferences.getIntOrNull("temp_date_year")?.takeIf { year ->
            abs(currentDate.year - year) < 200
        }?.let { year ->
            currentDate.withYear(year)
        } ?: preferences.putInt("temp_date_year", currentDate.year)

        preferences.getIntOrNull("temp_date_month")?.let { month ->
            currentDate.withMonth(month)
        } ?: preferences.putInt("temp_date_month", currentDate.monthValue)

        preferences.getIntOrNull("temp_date_day")?.let { day ->
            currentDate.withDayOfMonth(day)
        } ?: preferences.putInt("temp_date_day", currentDate.dayOfMonth)

        //Disable form if app is retrieving prediction
        if (preferences.getBoolean("temp_busy")) toggleViews(false)

        //Set listeners
        tvName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var name: String? = s.toString()
                name = StringHelper.removeZeroWidthSpaces(name)
                name = TextFormatter.fromHtml(name).toString()

                if (StringHelper.isNotNullOrBlank(name)) preferences.putString("temp_name", name)
                else preferences.remove("temp_name")
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        rgGender?.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            val gender = when (checkedId) {
                R.id.input_gender_indefinite_option -> Gender.NEUTRAL
                R.id.input_gender_male_option -> Gender.MASCULINE
                R.id.input_gender_female_option -> Gender.FEMININE
                else -> Gender.NEUTRAL
            }
            preferences.putInt("temp_gender", gender.value)
        }

        dpBirthdate?.init(
                currentDate.year,
                currentDate.monthValue - 1,
                currentDate.dayOfMonth
        ) { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            preferences.putInt("temp_date_year", year)
            preferences.putInt("temp_date_month", month + 1)
            preferences.putInt("temp_date_day", dayOfMonth)
        }

        btBack?.setOnClickListener { finish() }

        listener = OnSharedPreferenceChangeListener { _: SharedPreferences?, key: String ->
            if (key == "temp_busy") toggleViews(true)
        }

        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    public override fun onStart() {
        super.onStart()

        //Get stored names
        preferences.getStringSet("nameList")?.toList()?.let { storedNames ->
            if (storedNames.isNotEmpty()) {
                names = storedNames
                tvName?.setAdapter(
                        ArrayAdapter(
                                this@InputActivity,
                                android.R.layout.simple_dropdown_item_1line,
                                names.toTypedArray()
                        )
                )
            }
        }

        //Get stored enquiries
        if (StringHelper.isNotNullOrBlank(preferences.getString("peopleList"))) {
            val gson = GsonBuilder().registerTypeAdapter(
                    LocalDate::class.java,
                    JsonDeserializer { json: JsonElement, _: Type?, _: JsonDeserializationContext? ->
                        LocalDate.parse(
                                json.asJsonPrimitive.asString
                        )
                    } as JsonDeserializer<LocalDate>).create()
            val json = preferences.getString("peopleList")
            val type = object : TypeToken<ArrayList<Person?>?>() {}.type
            people = gson.fromJson(json, type)
        }
    }

    public override fun onDestroy() {
        preferences.getStringOrNull("temp_name")?.takeUnless { name ->
            name.isBlank()
        }?.let { name ->
            if (defaultPreferences.getBoolean("preference_saveNames", true)
                    && !names.contains(name)
            ) {
                if (names.size >= 200) names.removeAt(0)
                names.add(name)
                val set: MutableSet<String> = HashSet()
                set.addAll(names)
                preferences.putStringSetSafely("nameList", set)
            }
            val person = preferencesPerson
            savePerson(person)
        }
        super.onDestroy()
    }

    private fun toggleViews(enabled: Boolean) {
        dpBirthdate?.isClickable = enabled
        dpBirthdate?.isEnabled = enabled
        tvName?.isClickable = enabled
        tvName?.isEnabled = enabled
        rgGender?.isClickable = enabled
        rgGender?.isEnabled = enabled
    }
}
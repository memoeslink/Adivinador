package com.app.memoeslink.adivinador.activity

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.*
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.app.memoeslink.adivinador.CustomDatePicker
import com.app.memoeslink.adivinador.Preference
import com.app.memoeslink.adivinador.R
import com.app.memoeslink.adivinador.SpannerHelper
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
    private var listener: OnSharedPreferenceChangeListener? =
        null //Declared as global to avoid destruction by JVM Garbage Collector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)
        btBack = findViewById(R.id.input_back_button)
        tvName = findViewById(R.id.input_name_field)
        rgGender = findViewById(R.id.input_gender_radio)
        dpBirthdate = findViewById(R.id.input_date_picker)

        //Set max. and min. date
        val currentDate = DateTimeHelper.getCurrentDate()

        //Initialize values
        preferences.getStringOrNull(Preference.TEMP_NAME.tag)?.takeUnless { name ->
            name.isBlank()
        }?.let { name ->
            tvName?.setText(name)
        }

        preferences.getIntOrNull(Preference.TEMP_GENDER.tag)?.let { gender ->
            (rgGender?.getChildAt(gender) as RadioButton).isChecked = true
        } ?: preferences.put(Preference.TEMP_GENDER.tag, Gender.NEUTRAL.value)

        preferences.getIntOrNull(Preference.TEMP_YEAR_OF_BIRTH.tag)?.takeIf { year ->
            abs(currentDate.year - year) < 200
        }?.let { year ->
            currentDate.withYear(year)
        } ?: preferences.put(Preference.TEMP_YEAR_OF_BIRTH.tag, currentDate.year)

        preferences.getIntOrNull(Preference.TEMP_MONTH_OF_BIRTH.tag)?.let { month ->
            currentDate.withMonth(month)
        } ?: preferences.put(
            Preference.TEMP_MONTH_OF_BIRTH.tag,
            currentDate.monthValue
        )

        preferences.getIntOrNull(Preference.TEMP_DAY_OF_BIRTH.tag)?.let { day ->
            currentDate.withDayOfMonth(day)
        } ?: preferences.put(Preference.TEMP_DAY_OF_BIRTH.tag, currentDate.dayOfMonth)

        //Disable form when a prediction is being retrieved
        if (preferences.getBoolean(Preference.TEMP_BUSY.tag)) toggleViews(false)

        //Set listeners
        tvName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var name: String? = s.toString()
                name = StringHelper.removeZeroWidthSpaces(name)
                name = SpannerHelper.fromHtml(name).toString()

                if (StringHelper.isNotNullOrBlank(name)) preferences.put(
                    Preference.TEMP_NAME.tag,
                    name
                )
                else preferences.remove(Preference.TEMP_NAME.tag)
            }

            override fun afterTextChanged(s: Editable) {
            }
        })

        rgGender?.setOnCheckedChangeListener { _: RadioGroup?, checkedId: Int ->
            val gender = when (checkedId) {
                R.id.input_gender_undefined_option -> Gender.NEUTRAL
                R.id.input_gender_male_option -> Gender.MASCULINE
                R.id.input_gender_female_option -> Gender.FEMININE
                else -> Gender.NEUTRAL
            }
            preferences.put(Preference.TEMP_GENDER.tag, gender.value)
        }

        dpBirthdate?.init(
            currentDate.year,
            currentDate.monthValue - 1,
            currentDate.dayOfMonth
        ) { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            preferences.put(Preference.TEMP_YEAR_OF_BIRTH.tag, year)
            preferences.put(Preference.TEMP_MONTH_OF_BIRTH.tag, month + 1)
            preferences.put(Preference.TEMP_DAY_OF_BIRTH.tag, dayOfMonth)
        }

        btBack?.setOnClickListener { finish() }

        listener = OnSharedPreferenceChangeListener { _: SharedPreferences?, key: String ->
            if (key == Preference.TEMP_BUSY.tag) toggleViews(true)
        }

        preferences.registerOnSharedPreferenceChangeListener(listener)
    }

    public override fun onStart() {
        super.onStart()

        //Get stored names
        preferences.getStringSet(Preference.DATA_STORED_NAMES.tag).toMutableList()
            .let { storedNames ->
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
        if (StringHelper.isNotNullOrBlank(preferences.getString(Preference.DATA_STORED_PEOPLE.tag))) {
            val gson = GsonBuilder().registerTypeAdapter(
                LocalDate::class.java,
                JsonDeserializer { json: JsonElement, _: Type?, _: JsonDeserializationContext? ->
                    LocalDate.parse(
                        json.asJsonPrimitive.asString
                    )
                } as JsonDeserializer<LocalDate>).create()
            val json = preferences.getString(Preference.DATA_STORED_PEOPLE.tag)
            val type = object : TypeToken<ArrayList<Person?>?>() {}.type
            people = gson.fromJson(json, type)
        }
    }

    public override fun onDestroy() {
        preferences.getStringOrNull(Preference.TEMP_NAME.tag)?.takeUnless { name ->
            name.isBlank()
        }?.let { name ->
            if (defaultPreferences.getBoolean(Preference.SETTING_SAVE_NAMES.tag, true)
                && !names.contains(name)
            ) {
                if (names.size >= 200) names.removeAt(0)
                names.add(name)
                val set: MutableSet<String> = HashSet()
                set.addAll(names)
                preferences.put(Preference.DATA_STORED_NAMES.tag, set)
            }
            val person = formPerson
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
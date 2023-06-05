package com.app.memoeslink.adivinador.activity

import android.content.SharedPreferences
import android.content.SharedPreferences.OnSharedPreferenceChangeListener
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.DatePicker
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import com.app.memoeslink.adivinador.CustomDatePicker
import com.app.memoeslink.adivinador.R
import com.app.memoeslink.adivinador.extensions.toHtmlText
import com.app.memoeslink.adivinador.preference.Preference
import com.app.memoeslink.adivinador.preference.PreferenceHandler
import com.app.memoeslink.adivinador.preference.PreferenceUtils
import com.memoeslink.generator.common.DateTimeHelper
import com.memoeslink.generator.common.Gender
import com.memoeslink.generator.common.StringHelper
import com.memoeslink.generator.common.Validation
import kotlin.math.abs

class InputActivity : CommonActivity() {
    private var tvName: AppCompatAutoCompleteTextView? = null
    private var rgGender: RadioGroup? = null
    private var dpBirthdate: CustomDatePicker? = null
    private var btBack: Button? = null
    private var listener: OnSharedPreferenceChangeListener? =
        null // Declared as global to avoid destruction by JVM Garbage Collector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_input)
        btBack = findViewById(R.id.input_back_button)
        tvName = findViewById(R.id.input_name_field)
        rgGender = findViewById(R.id.input_gender_radio)
        dpBirthdate = findViewById(R.id.input_date_picker)

        // Initialize values
        PreferenceHandler.getStringOrNull(
            Preference.TEMP_NAME
        )?.takeUnless { name ->
            name.isBlank()
        }?.let { name ->
            tvName?.setText(name)
        }

        PreferenceHandler.getIntOrNull(
            Preference.TEMP_GENDER
        )?.let { gender ->
            (rgGender?.getChildAt(gender.coerceAtLeast(0)) as RadioButton).isChecked = true
        } ?: PreferenceHandler.put(
            Preference.TEMP_GENDER, Gender.NEUTRAL.value
        )
        var currentDate = DateTimeHelper.getCurrentDate()

        PreferenceHandler.getIntOrNull(
            Preference.TEMP_YEAR_OF_BIRTH
        )?.takeIf { year ->
            currentDate.year >= 0 && abs(currentDate.year - year) < 200
        }?.let { year ->
            currentDate = currentDate.withYear(year)
        } ?: PreferenceHandler.put(
            Preference.TEMP_YEAR_OF_BIRTH, currentDate.year
        )

        PreferenceHandler.getIntOrNull(
            Preference.TEMP_MONTH_OF_BIRTH
        )?.takeIf { month ->
            month in 1..12
        }?.let { month ->
            currentDate = currentDate.withMonth(month)
        } ?: PreferenceHandler.put(
            Preference.TEMP_MONTH_OF_BIRTH, currentDate.monthValue
        )

        PreferenceHandler.getIntOrNull(
            Preference.TEMP_DAY_OF_BIRTH
        )?.takeIf { day ->
            Validation.isDate("${currentDate.year}-${currentDate.monthValue}-$day")
        }?.let { day ->
            currentDate = currentDate.withDayOfMonth(day)
        } ?: PreferenceHandler.put(
            Preference.TEMP_DAY_OF_BIRTH, currentDate.dayOfMonth
        )

        // Disable form while a prediction is being retrieved
        if (PreferenceHandler.getBoolean(Preference.TEMP_BUSY)) toggleViews(false)

        // Set listeners
        tvName?.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                var name: String? = s.toString()
                name = StringHelper.removeZeroWidthSpaces(name)
                name = name.toHtmlText().toString()

                if (name.isNotBlank()) PreferenceHandler.put(Preference.TEMP_NAME, name)
                else PreferenceHandler.remove(Preference.TEMP_NAME)
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
            PreferenceHandler.put(
                Preference.TEMP_GENDER, gender.value
            )
        }

        dpBirthdate?.init(
            currentDate.year, currentDate.monthValue - 1, currentDate.dayOfMonth
        ) { _: DatePicker?, year: Int, month: Int, dayOfMonth: Int ->
            PreferenceHandler.put(
                Preference.TEMP_YEAR_OF_BIRTH, year
            )
            PreferenceHandler.put(
                Preference.TEMP_MONTH_OF_BIRTH, month + 1
            )
            PreferenceHandler.put(
                Preference.TEMP_DAY_OF_BIRTH, dayOfMonth
            )
        }

        btBack?.setOnClickListener { finish() }

        listener = OnSharedPreferenceChangeListener { _: SharedPreferences?, key: String ->
            if (key == Preference.TEMP_BUSY.tag) toggleViews(true)
        }

        PreferenceHandler.changePreferencesListener(listener)
    }

    public override fun onStart() {
        super.onStart()

        // Get stored names
        PreferenceUtils.getStoredNames().takeIf { it.isNotEmpty() }?.let { storedNames ->
            tvName?.setAdapter(
                ArrayAdapter(
                    this@InputActivity,
                    android.R.layout.simple_dropdown_item_1line,
                    storedNames.toTypedArray()
                )
            )
        }
    }

    public override fun onDestroy() {
        PreferenceHandler.getStringOrNull(
            Preference.TEMP_NAME
        )?.takeUnless { name ->
            name.isBlank()
        }?.let { name ->
            PreferenceUtils.saveName(name)
            val person = PreferenceUtils.getFormPerson()
            PreferenceUtils.savePerson(person)
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
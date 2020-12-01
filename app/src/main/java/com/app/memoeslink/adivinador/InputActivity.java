package com.app.memoeslink.adivinador;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.widget.AppCompatAutoCompleteTextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.apache.commons.lang3.StringUtils;
import org.joda.time.LocalDate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class InputActivity extends CommonActivity {
    private AppCompatAutoCompleteTextView name;
    private RadioGroup sex;
    private CustomDatePicker date;
    private Button button;
    private SimpleDate currentDate;
    private ArrayList<String> names;
    private ArrayList<Enquiry> enquiries;
    private LocalDate maxDate;
    private LocalDate minDate;
    private Methods methods;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        button = findViewById(R.id.input_back_button);
        name = findViewById(R.id.input_name_field);
        sex = findViewById(R.id.input_sex_radio);
        date = findViewById(R.id.input_date_picker);
        names = new ArrayList<>();
        enquiries = new ArrayList<>();
        methods = new Methods(InputActivity.this);
        currentDate = Methods.getCurrentDate();

        //Set max. and min. date
        maxDate = new LocalDate(currentDate.getYear() + 200, currentDate.getMonth(), currentDate.getDay());
        date.setMaxDate(maxDate.toDate().getTime());

        minDate = new LocalDate(currentDate.getYear() - 200, currentDate.getMonth(), currentDate.getDay());
        date.setMinDate(minDate.toDate().getTime());

        //Initialize values
        if (preferences.contains("temp_name"))
            name.setText(preferences.getString("temp_name"));

        if (!preferences.contains("temp_sex"))
            preferences.putInt("temp_sex", Entity.DEFAULT_SEX);
        else
            ((RadioButton) sex.getChildAt(preferences.getInt("temp_sex", Entity.DEFAULT_SEX))).setChecked(true);

        if (preferences.contains("temp_date_year")) {
            if (Math.abs(Methods.getYear() - preferences.getInt("temp_date_year", SimpleDate.DEFAULT_YEAR)) >= 200)
                currentDate.setYear(Methods.getYear());
            else
                currentDate.setYear(preferences.getInt("temp_date_year", SimpleDate.DEFAULT_YEAR));
        } else
            preferences.putInt("temp_date_year", currentDate.getYear());

        if (preferences.contains("temp_date_month"))
            currentDate.setMonth(preferences.getInt("temp_date_month", SimpleDate.DEFAULT_MONTH));
        else
            preferences.putInt("temp_date_month", currentDate.getMonth());

        if (preferences.contains("temp_date_day"))
            currentDate.setDay(preferences.getInt("temp_date_day", SimpleDate.DEFAULT_DAY));
        else
            preferences.putInt("temp_date_day", currentDate.getDay());

        //Disable form if app is retrieving prediction
        if (preferences.getBoolean("temp_busy"))
            toggleViews(false);

        //Set listeners
        date.init(currentDate.getYear(), currentDate.getMonth() - 1, currentDate.getDay(), (datePicker, year, month, dayOfMonth) -> {
            preferences.putInt("temp_date_year", year);
            preferences.putInt("temp_date_month", month + 1);
            preferences.putInt("temp_date_day", dayOfMonth);
        });

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String s = charSequence.toString().trim();
                s = StringUtils.replace(s, "꘍", "");
                s = StringUtils.replace(s, Methods.ZERO_WIDTH_SPACE, "");
                s = Methods.fromHtml(s).toString();

                if (!s.isEmpty())
                    preferences.putString("temp_name", s);
                else
                    preferences.remove("temp_name");
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        sex.setOnCheckedChangeListener((group, checkedId) -> {
            int index;

            switch (checkedId) {
                case R.id.input_sex_indefinite_option:
                    index = 0;
                    break;
                case R.id.input_sex_male_option:
                    index = 1;
                    break;
                case R.id.input_sex_female_option:
                    index = 2;
                    break;
                default:
                    index = 0;
                    break;
            }
            preferences.putInt("temp_sex", index);
        });

        button.setOnClickListener(view -> finish());

        SharedPreferences.OnSharedPreferenceChangeListener listener = (prefs, key) -> {
            if (key.equals("temp_busy"))
                toggleViews(true);
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Get stored names
        if (preferences.getStringSet("nameList").size() > 0)
            names = new ArrayList(preferences.getStringSet("nameList"));

        if (names.size() > 0) {
            String[] names = this.names.toArray(new String[0]);
            name.setAdapter(new ArrayAdapter<>(InputActivity.this, android.R.layout.simple_dropdown_item_1line, names));
        }

        //Get stored enquiries
        if (StringUtils.isNotBlank(preferences.getString("enquiryList"))) {
            Gson gson = new Gson();
            String json = preferences.getString("enquiryList");
            Type type = new TypeToken<ArrayList<Enquiry>>() {
            }.getType();
            enquiries = gson.fromJson(json, type);
        }
    }

    @Override
    public void onDestroy() {
        String name = preferences.getString("temp_name", "");

        if (defaultPreferences.getBoolean("preference_saveNames", true)) {
            if (!name.isEmpty()) {
                if (!names.contains(name)) {
                    if (names.size() >= 200)
                        names.remove(0);
                    names.add(name);
                    Set<String> set = new HashSet<>();
                    set.addAll(names);
                    preferences.putStringSetSafely("nameList", set);
                }
            }
        }

        if (defaultPreferences.getBoolean("preference_saveEnquiries", true)) {
            if (!name.isEmpty()) {
                Person person = new Person(new Entity(
                        0,
                        preferences.getInt("temp_sex", Entity.DEFAULT_SEX),
                        preferences.getString("temp_name"),
                        "",
                        "",
                        Name.EMPTY
                ));
                person.setBirthdate(new SimpleDate(
                        preferences.getInt("temp_date_year", SimpleDate.DEFAULT_YEAR),
                        preferences.getInt("temp_date_month", SimpleDate.DEFAULT_MONTH),
                        preferences.getInt("temp_date_day", SimpleDate.DEFAULT_DAY)
                ));

                Enquiry enquiry = new Enquiry(
                        person,
                        true,
                        false
                );
                preferences.putString("temp_formatted_name", enquiry.getFormattedDescriptor());
                preferences.putBoolean("temp_user", true);
                preferences.putBoolean("temp_anonymous", false);
                methods.saveEnquiry(enquiries, enquiry);
            }
        }
        super.onDestroy();
    }

    private void toggleViews(boolean enabled) {
        date.setClickable(enabled);
        date.setEnabled(enabled);
        name.setClickable(enabled);
        name.setEnabled(enabled);
        sex.setClickable(enabled);
        sex.setEnabled(enabled);
    }
}

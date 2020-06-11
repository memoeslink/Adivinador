package com.app.memoeslink.adivinador;

import android.app.Activity;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

public class InputActivity extends CommonActivity {
    private AppCompatAutoCompleteTextView name;
    private RadioGroup sex;
    private CustomDatePicker date;
    private Button button;
    private int[] currentDate;
    private ArrayList<String> nameList;
    private ArrayList<Enquiry> enquiryList;
    private Calendar maxDate;
    private Calendar minDate;
    private Methods methods;
    private SharedPreferences.OnSharedPreferenceChangeListener listener;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_input);
        preferences = this.getSharedPreferences(Methods.PREFERENCES, Activity.MODE_PRIVATE);
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        button = findViewById(R.id.input_back_button);
        name = findViewById(R.id.input_name_field);
        sex = findViewById(R.id.input_sex_radio);
        date = findViewById(R.id.input_date_picker);
        nameList = new ArrayList<>();
        enquiryList = new ArrayList<>();
        methods = new Methods(InputActivity.this);
        currentDate = Methods.getCurrentDate();

        //Set max. and min. date
        maxDate = Calendar.getInstance();
        maxDate.set(Calendar.DAY_OF_MONTH, currentDate[2]);
        maxDate.set(Calendar.MONTH, currentDate[1]);
        maxDate.set(Calendar.YEAR, currentDate[0] + 200);
        date.setMaxDate(maxDate.getTimeInMillis());

        minDate = Calendar.getInstance();
        minDate.set(Calendar.DAY_OF_MONTH, currentDate[2]);
        minDate.set(Calendar.MONTH, currentDate[1]);
        minDate.set(Calendar.YEAR, currentDate[0] - 200);
        date.setMinDate(minDate.getTimeInMillis());

        //Initialize values
        if (preferences.contains("temp_name"))
            name.setText(preferences.getString("temp_name", ""));

        if (!preferences.contains("temp_sex"))
            preferences.edit().putInt("temp_sex", Methods.DEFAULT_SEX).commit();
        else
            ((RadioButton) sex.getChildAt(preferences.getInt("temp_sex", Methods.DEFAULT_SEX))).setChecked(true);

        if (preferences.contains("temp_date_year")) {
            if (Math.abs(Methods.getYear() - preferences.getInt("temp_date_year", Methods.DEFAULT_YEAR)) >= 200)
                currentDate[0] = Methods.getYear();
            else
                currentDate[0] = preferences.getInt("temp_date_year", Methods.DEFAULT_YEAR);
        } else
            preferences.edit().putInt("temp_date_year", currentDate[0]).commit();

        if (preferences.contains("temp_date_month"))
            currentDate[1] = preferences.getInt("temp_date_month", Methods.DEFAULT_MONTH);
        else
            preferences.edit().putInt("temp_date_month", currentDate[1]).commit();

        if (preferences.contains("temp_date_day"))
            currentDate[2] = preferences.getInt("temp_date_day", Methods.DEFAULT_DAY);
        else
            preferences.edit().putInt("temp_date_day", currentDate[2]).commit();

        //Disable form if app is retrieving prediction
        if (preferences.getBoolean("temp_busy", false))
            toggleViews(false);

        //Set listeners
        date.init(currentDate[0], currentDate[1], currentDate[2], (datePicker, year, month, dayOfMonth) -> {
            preferences.edit().putInt("temp_date_year", year).commit();
            preferences.edit().putInt("temp_date_month", month).commit();
            preferences.edit().putInt("temp_date_day", dayOfMonth).commit();
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
                    preferences.edit().putString("temp_name", s).commit();
                else
                    preferences.edit().remove("temp_name").commit();
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
            preferences.edit().putInt("temp_sex", index).commit();
        });

        button.setOnClickListener(view -> finish());

        listener = (prefs, key) -> {
            if (key.equals("temp_busy"))
                toggleViews(true);
        };
        preferences.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onStart() {
        super.onStart();

        //Get stored names
        if (preferences.contains("nameList") && preferences.getStringSet("nameList", null).size() > 0)
            nameList = new ArrayList(preferences.getStringSet("nameList", null));

        if (nameList.size() > 0) {
            String[] names = nameList.toArray(new String[0]);
            name.setAdapter(new ArrayAdapter<>(InputActivity.this, android.R.layout.simple_dropdown_item_1line, names));
        }

        //Get stored enquiries
        if (preferences.getString("enquiryList", null) != null) {
            Gson gson = new Gson();
            String json = preferences.getString("enquiryList", null);
            Type type = new TypeToken<ArrayList<Enquiry>>() {
            }.getType();
            enquiryList = gson.fromJson(json, type);
        }
    }

    @Override
    public void onDestroy() {
        String name = preferences.getString("temp_name", "");

        if (defaultPreferences.getBoolean("preference_saveNames", true)) {
            if (!name.isEmpty()) {
                if (!nameList.contains(name)) {
                    if (nameList.size() >= 200)
                        nameList.remove(0);
                    nameList.add(name);
                    Set<String> set = new HashSet<>();
                    set.addAll(nameList);
                    preferences.edit().putStringSet("nameList", set).apply();
                }
            }
        }

        if (defaultPreferences.getBoolean("preference_saveEnquiries", true)) {
            if (!name.isEmpty()) {
                Enquiry enquiry = new Enquiry(
                        preferences.getString("temp_name", ""),
                        "",
                        preferences.getInt("temp_sex", Methods.DEFAULT_SEX),
                        preferences.getInt("temp_date_year", Methods.DEFAULT_YEAR),
                        preferences.getInt("temp_date_month", Methods.DEFAULT_MONTH),
                        preferences.getInt("temp_date_day", Methods.DEFAULT_DAY),
                        true,
                        null
                );
                preferences.edit().putString("temp_formatted_name", "").apply();
                preferences.edit().putBoolean("temp_user", true).apply();
                preferences.edit().putBoolean("temp_anonymous", false).apply();
                methods.saveEnquiry(enquiryList, enquiry);
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

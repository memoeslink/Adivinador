package com.app.memoeslink.adivinador.finder;

import android.content.Context;

import com.app.memoeslink.adivinador.Preference;
import com.memoeslink.generator.common.Binder;
import com.memoeslink.generator.common.Constant;
import com.memoeslink.generator.common.Database;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.helper.SharedPreferencesHelper;

import java.util.ArrayList;
import java.util.List;

public class PreferenceFinder extends Binder {
    private final SharedPreferencesHelper preferences;

    public PreferenceFinder(Context context) {
        this(context, null);
    }

    public PreferenceFinder(Context context, Long seed) {
        super(context, seed);
        preferences = SharedPreferencesHelper.Companion.getPreferencesHelper(context);
    }

    private List<String> getSuggestedNames() {
        List<String> suggestedNames = new ArrayList<>(preferences.getStringSet(Preference.DATA_STORED_NAMES.getTag()));
        suggestedNames.add(Constant.DEVELOPER);
        return suggestedNames;
    }

    public String getSuggestedName() {
        List<String> suggestedNames = getSuggestedNames();
        String suggestedName;

        do {
            suggestedName = r.getItem(suggestedNames);
        } while (preferences.getString(Preference.TEMP_NAME.getTag()).equals(suggestedName) && suggestedNames.size() > 1);
        return StringHelper.defaultWhenBlank(suggestedName);
    }
}

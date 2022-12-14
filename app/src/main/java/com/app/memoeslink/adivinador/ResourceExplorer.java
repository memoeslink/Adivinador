package com.app.memoeslink.adivinador;

import android.content.Context;

import com.app.memoeslink.adivinador.finder.DatabaseFinder;
import com.app.memoeslink.adivinador.finder.PreferenceFinder;
import com.memoeslink.generator.common.Constant;
import com.memoeslink.generator.common.DateTimeGetter;
import com.memoeslink.generator.common.Device;
import com.memoeslink.generator.common.Explorer;
import com.memoeslink.generator.common.Form;
import com.memoeslink.generator.common.GeneratorManager;
import com.memoeslink.generator.common.IntegerHelper;
import com.memoeslink.generator.common.NameType;
import com.memoeslink.generator.common.PhraseType;
import com.memoeslink.generator.common.ResourceGetter;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.common.TextFormatter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ResourceExplorer extends Explorer {
    private static final List<String> NAME_SOURCES;
    private static final HashMap<MethodReference, String> METHOD_MAPPING = new HashMap<>();
    private final DatabaseFinder databaseFinder;
    private final PreferenceFinder preferenceFinder;
    private final MethodFinder methodFinder;
    private final GeneratorManager generatorManager;

    static {
        NAME_SOURCES = Collections.unmodifiableList(new ArrayList<>() {{
            addAll(new ArrayList<>(Collections.nCopies(15, "anonymous")));
            addAll(new ArrayList<>(Collections.nCopies(45, "common")));
            add("contact");
            add("contact");
            add("suggestion");
        }});
    }

    public ResourceExplorer(Context context) {
        this(context, null);
    }

    public ResourceExplorer(Context context, Long seed) {
        super(context, seed);
        databaseFinder = new DatabaseFinder(context, seed);
        preferenceFinder = new PreferenceFinder(context, seed);
        methodFinder = new MethodFinder();
        generatorManager = new GeneratorManager(Locale.getDefault(), seed);
    }

    public DatabaseFinder getDatabaseFinder() {
        return databaseFinder;
    }

    public PreferenceFinder getPreferenceFinder() {
        return preferenceFinder;
    }

    public MethodFinder getMethodFinder() {
        return methodFinder;
    }

    public GeneratorManager getGeneratorManager() {
        return generatorManager;
    }

    @Override
    public void bindSeed(Long seed) {
        super.bindSeed(seed);
        databaseFinder.bindSeed(seed);
        preferenceFinder.bindSeed(seed);
        generatorManager.setSeed(seed);
    }

    @Override
    public void unbindSeed() {
        super.unbindSeed();
        databaseFinder.unbindSeed();
        preferenceFinder.unbindSeed();
        generatorManager.setSeed(null);
    }

    public String findTableRowByName(String tableName) {
        return findTableRowByName(tableName, -1);
    }

    public String findTableRowByName(String tableName, int index) {
        if (index < 1)
            index = r.getInt(1, Database.getInstance(this).countTableRows(tableName));
        else
            index = IntegerHelper.defaultInt(index, 1, Database.getInstance(this).countTableRows(tableName));
        return Database.getInstance(this).selectFromTable(tableName, index);
    }

    public String findMethodByName(String methodName) {
        return StringHelper.defaultWhenEmpty(methodFinder.getMethodByName(methodName));
    }

    public String findMethodByRef(MethodReference reference) {
        return StringHelper.defaultWhenEmpty(methodFinder.getMethodByRef(reference));
    }

    private class MethodFinder {

        public String getMethodByName(String methodName) {
            return getMethodByRef(MethodReference.get(methodName));
        }

        public String getMethodByRef(MethodReference reference) {
            reference = reference != null ? reference : MethodReference.NONE;

            if (METHOD_MAPPING.isEmpty()) {
                METHOD_MAPPING.put(MethodReference.NONE, Database.DEFAULT_VALUE);
                METHOD_MAPPING.put(MethodReference.NAME, generatorManager.getNameGenerator().getName(NameType.FULL_NAME));
                METHOD_MAPPING.put(MethodReference.USERNAME, generatorManager.getNameGenerator().getUsername());
                METHOD_MAPPING.put(MethodReference.SECRET_NAME, generatorManager.getNameGenerator().getName(NameType.MALE_ITERATIVE_FORENAME));
                METHOD_MAPPING.put(MethodReference.NOUN, generatorManager.getNounGenerator().getNoun(Form.UNDEFINED));
                METHOD_MAPPING.put(MethodReference.NOUN_WITH_ARTICLE, generatorManager.getNounGenerator().getNounWithArticle(Form.UNDEFINED));
                METHOD_MAPPING.put(MethodReference.NOUN_WITH_INDEFINITE_ARTICLE, generatorManager.getNounGenerator().getNounWithArticle(Form.UNDEFINED));
                METHOD_MAPPING.put(MethodReference.NOUN_WITH_ANY_ARTICLE, generatorManager.getNounGenerator().getNounWithArticle());
                METHOD_MAPPING.put(MethodReference.DATE, generatorManager.getDateTimeGenerator().getStrDate());
                METHOD_MAPPING.put(MethodReference.TIME, generatorManager.getDateTimeGenerator().getStrTime());
                METHOD_MAPPING.put(MethodReference.PERCENTAGE, generatorManager.getStringGenerator().getPercentage());
                METHOD_MAPPING.put(MethodReference.DECIMAL_PERCENTAGE, generatorManager.getStringGenerator().getDecimalPercentage());
                METHOD_MAPPING.put(MethodReference.HEX_COLOR, generatorManager.getStringGenerator().getStrColor());
                METHOD_MAPPING.put(MethodReference.DEFAULT_COLOR, ResourceGetter.with(r).getString(Constant.DEFAULT_COLORS));
                METHOD_MAPPING.put(MethodReference.DEVICE_INFO, new Device(context).getInfo(r.getInt(1, 9)));
                METHOD_MAPPING.put(MethodReference.CONTACT_NAME, contactNameFinder.getContactName());
                METHOD_MAPPING.put(MethodReference.SUGGESTED_NAME, preferenceFinder.getSuggestedName());
                METHOD_MAPPING.put(MethodReference.FORMATTED_NAME, getFormattedName());
                METHOD_MAPPING.put(MethodReference.SIMPLE_GREETING, generatorManager.getPhraseGenerator().getPhrase(PhraseType.SIMPLE_GREETING));
                METHOD_MAPPING.put(MethodReference.CURRENT_DAY_OF_WEEK, DateTimeGetter.with(LanguageHelper.getLocale(context), r).getCurrentDayOfWeek());
                METHOD_MAPPING.put(MethodReference.CURRENT_DATE, DateTimeGetter.with(LanguageHelper.getLocale(context), r).getCurrentDate());
                METHOD_MAPPING.put(MethodReference.CURRENT_TIME, DateTimeGetter.with(LanguageHelper.getLocale(context), r).getCurrentTime());
            }
            return METHOD_MAPPING.get(reference);
        }

        private String getFormattedName() {
            switch (r.getItem(NAME_SOURCES)) {
                case "anonymous":
                    return TextFormatter.formatUsername(METHOD_MAPPING.get(MethodReference.USERNAME));
                case "common":
                    return TextFormatter.formatName(generatorManager.getPersonGenerator().getPerson().getAltSummary());
                case "contact":
                    return TextFormatter.formatContactName(METHOD_MAPPING.get(MethodReference.CONTACT_NAME));
                case "suggestion":
                    return TextFormatter.formatSuggestedName(METHOD_MAPPING.get(MethodReference.SUGGESTED_NAME));
                default:
                    return TextFormatter.formatText("?", "b,tt");
            }
        }
    }
}

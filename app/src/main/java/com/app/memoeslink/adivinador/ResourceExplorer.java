package com.app.memoeslink.adivinador;

import android.content.Context;

import androidx.appcompat.app.AppCompatActivity;

import com.app.memoeslink.adivinador.finder.DatabaseFinder;
import com.app.memoeslink.adivinador.preference.Preference;
import com.app.memoeslink.adivinador.preference.PreferenceHandler;
import com.memoeslink.generator.common.Constant;
import com.memoeslink.generator.common.Explorer;
import com.memoeslink.generator.common.Form;
import com.memoeslink.generator.common.GeneratorManager;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.PhraseType;
import com.memoeslink.generator.common.TextFormatter;
import com.memoeslink.manager.Device;
import com.memoeslink.manager.InformationType;

import org.memoeslink.IntegerHelper;
import org.memoeslink.StringHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.function.Supplier;

public class ResourceExplorer extends Explorer {
    private static final List<String> NAME_SOURCES;
    private static final HashMap<MethodReference, Supplier<String>> METHOD_MAPPING = new HashMap<>();
    private final DatabaseFinder databaseFinder;
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
        methodFinder = new MethodFinder();

        if (context instanceof AppCompatActivity)
            generatorManager = new GeneratorManager(LanguageHelper.getLocale(context), seed);
        else
            generatorManager = new GeneratorManager(Locale.getDefault(), seed);
    }

    public DatabaseFinder getDatabaseFinder() {
        return databaseFinder;
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
        generatorManager.setSeed(seed);
    }

    @Override
    public void unbindSeed() {
        super.unbindSeed();
        databaseFinder.unbindSeed();
        generatorManager.setSeed(null);
    }

    public String findTableRowByName(String tableName) {
        return findTableRowByName(tableName, -1);
    }

    public String findTableRowByName(String tableName, int index) {
        if (index < 1)
            index = r.getIntInRange(1, Database.getInstance(this).countTableRows(tableName));
        else
            index = IntegerHelper.defaultByRange(index, 1, Database.getInstance(this).countTableRows(tableName));
        return Database.getInstance(this).selectFromTable(tableName, index);
    }

    public String findMethodByName(String methodName) {
        return StringHelper.defaultOnEmpty(methodFinder.getMethodByName(methodName));
    }

    public String findMethodByRef(MethodReference reference) {
        return StringHelper.defaultOnEmpty(methodFinder.getMethodByRef(reference));
    }

    public String findPrefByTag(String tag) {
        Preference preference = Preference.get(tag);
        return PreferenceHandler.get(preference);
    }

    private class MethodFinder {

        public String getMethodByName(String methodName) {
            return getMethodByRef(MethodReference.get(methodName));
        }

        public String getMethodByRef(MethodReference reference) {
            reference = reference != null ? reference : MethodReference.NONE;

            if (METHOD_MAPPING.isEmpty()) {
                METHOD_MAPPING.put(MethodReference.NONE, () -> Database.DEFAULT_VALUE);
                METHOD_MAPPING.put(MethodReference.NAME, () -> generatorManager.getPersonGenerator().getPerson().getFullName());
                METHOD_MAPPING.put(MethodReference.USERNAME, () -> generatorManager.getPersonGenerator().getAnonymousPerson().getUsername());
                METHOD_MAPPING.put(MethodReference.NOUN, () -> generatorManager.getNounGenerator().getNoun(Form.UNDEFINED));
                METHOD_MAPPING.put(MethodReference.NOUN_WITH_ARTICLE, () -> generatorManager.getNounGenerator().getNounWithArticle(Form.UNDEFINED));
                METHOD_MAPPING.put(MethodReference.NOUN_WITH_INDEFINITE_ARTICLE, () -> generatorManager.getNounGenerator().getNounWithArticle(Form.UNDEFINED));
                METHOD_MAPPING.put(MethodReference.NOUN_WITH_ANY_ARTICLE, () -> generatorManager.getNounGenerator().getNounWithArticle());
                METHOD_MAPPING.put(MethodReference.DATE, () -> generatorManager.getDateTimeGenerator().getStrDate());
                METHOD_MAPPING.put(MethodReference.TIME, () -> generatorManager.getDateTimeGenerator().getStrTime());
                METHOD_MAPPING.put(MethodReference.PERCENTAGE, () -> generatorManager.getStringGenerator().getPercentage(false));
                METHOD_MAPPING.put(MethodReference.DECIMAL_PERCENTAGE, () -> generatorManager.getStringGenerator().getPercentage(true));
                METHOD_MAPPING.put(MethodReference.HEX_COLOR, () -> generatorManager.getStringGenerator().getHexColor());
                METHOD_MAPPING.put(MethodReference.DEFAULT_COLOR, () -> resourceFinder.getStrFromArray(Constant.DEFAULT_COLORS));
                METHOD_MAPPING.put(MethodReference.DEVICE_INFO, () -> new Device(context).getInfo(r.getElement(InformationType.getEntries())));
                METHOD_MAPPING.put(MethodReference.CONTACT_NAME, contactNameFinder::getContactName);
                METHOD_MAPPING.put(MethodReference.SUGGESTED_NAME, this::getSuggestedName);
                METHOD_MAPPING.put(MethodReference.INDIVIDUAL, this::getIndividual);
                METHOD_MAPPING.put(MethodReference.FORMATTED_INDIVIDUAL, this::getFormattedIndividual);
                METHOD_MAPPING.put(MethodReference.ACTOR, this::getActor);
                METHOD_MAPPING.put(MethodReference.FORMATTED_ACTOR, this::getFormattedActor);
                METHOD_MAPPING.put(MethodReference.AGREEMENT, () -> generatorManager.getPhraseGenerator().getPhrase(PhraseType.AGREEMENT));
                METHOD_MAPPING.put(MethodReference.AMAZEMENT, () -> generatorManager.getPhraseGenerator().getPhrase(PhraseType.AMAZEMENT));
                METHOD_MAPPING.put(MethodReference.APOLOGY, () -> generatorManager.getPhraseGenerator().getPhrase(PhraseType.APOLOGY));
                METHOD_MAPPING.put(MethodReference.APPRECIATION, () -> generatorManager.getPhraseGenerator().getPhrase(PhraseType.APPRECIATION));
                METHOD_MAPPING.put(MethodReference.CONGRATULATION, () -> generatorManager.getPhraseGenerator().getPhrase(PhraseType.CONGRATULATION));
                METHOD_MAPPING.put(MethodReference.DISAGREEMENT, () -> generatorManager.getPhraseGenerator().getPhrase(PhraseType.DISAGREEMENT));
                METHOD_MAPPING.put(MethodReference.DOUBT, () -> generatorManager.getPhraseGenerator().getPhrase(PhraseType.DOUBT));
                METHOD_MAPPING.put(MethodReference.FAREWELL, () -> generatorManager.getPhraseGenerator().getPhrase(PhraseType.FAREWELL));
                METHOD_MAPPING.put(MethodReference.GREETING, () -> generatorManager.getPhraseGenerator().getPhrase(PhraseType.GREETING));
                METHOD_MAPPING.put(MethodReference.INITIATION_QUESTION, () -> generatorManager.getPhraseGenerator().getPhrase(PhraseType.INITIATION_QUESTION));
                METHOD_MAPPING.put(MethodReference.INQUIRY_QUESTION, () -> generatorManager.getPhraseGenerator().getPhrase(PhraseType.INQUIRY_QUESTION));
                METHOD_MAPPING.put(MethodReference.SHORT_ANSWER, () -> generatorManager.getPhraseGenerator().getPhrase(PhraseType.SHORT_ANSWER));
                METHOD_MAPPING.put(MethodReference.WELCOME, () -> generatorManager.getPhraseGenerator().getPhrase(PhraseType.WELCOME));
            }
            return METHOD_MAPPING.getOrDefault(reference, () -> Database.DEFAULT_VALUE).get();
        }

        private String getSuggestedName() {
            List<String> suggestedNames = new ArrayList<>(PreferenceHandler.getStringSet(Preference.DATA_STORED_NAMES));
            suggestedNames.add(Constant.DEVELOPER);
            String suggestedName;

            do {
                suggestedName = r.getElement(suggestedNames);
            } while (PreferenceHandler.getString(Preference.TEMP_NAME).equals(suggestedName) && suggestedNames.size() > 1);
            return StringHelper.defaultIfBlank(suggestedName, Constant.DEVELOPER);
        }

        private Person getPerson() {
            return switch (r.getElement(NAME_SOURCES)) {
                case "anonymous" -> generatorManager.getPersonGenerator().getAnonymousPerson();
                case "common" -> generatorManager.getPersonGenerator().getPerson();
                case "contact" -> new Person.PersonBuilder()
                        .setFullName(contactNameFinder.getContactName())
                        .setAttribute("registered")
                        .build();
                case "suggestion" -> new Person.PersonBuilder()
                        .setFullName(getSuggestedName())
                        .setAttribute("suggested")
                        .build();
                default -> new Person.PersonBuilder().setAttribute("empty").build();
            };
        }

        private String getIndividual() {
            return getPerson().getDescriptor();
        }

        private String getFormattedIndividual() {
            return TextFormatter.formatPerson(getPerson());
        }

        private String getActor() {
            Person person = getPerson();
            return String.format("%s {actor:%s; id:@tempActor; grammatical-indicator:singular-%s; hidden}", person.getDescriptor(), person.getSha256(), person.getGender().toString().toLowerCase());
        }

        private String getFormattedActor() {
            Person person = getPerson();
            return String.format("%s {actor:%s; id:@tempActor; grammatical-indicator:singular-%s; hidden}", TextFormatter.formatPerson(person), person.getSha256(), person.getGender().toString().toLowerCase());
        }
    }
}

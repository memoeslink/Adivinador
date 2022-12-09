package com.app.memoeslink.adivinador;

import android.content.Context;

import com.app.memoeslink.adivinador.finder.DatabaseFinder;
import com.memoeslink.generator.common.Constant;
import com.memoeslink.generator.common.DateTimeGetter;
import com.memoeslink.generator.common.DateTimeHelper;
import com.memoeslink.generator.common.Device;
import com.memoeslink.generator.common.Explorer;
import com.memoeslink.generator.common.Form;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.GeneratorManager;
import com.memoeslink.generator.common.IntegerHelper;
import com.memoeslink.generator.common.NameType;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.ResourceGetter;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.common.TextFormatter;
import com.memoeslink.generator.common.finder.ResourceFinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ResourceExplorer extends Explorer {
    private static final List<String> PERSON_SOURCES;
    private final DatabaseFinder databaseFinder;
    private final ReflectionFinder reflectionFinder;

    static {
        PERSON_SOURCES = new ArrayList<>();
        PERSON_SOURCES.addAll(new ArrayList<>(Collections.nCopies(15, "anonymous")));
        PERSON_SOURCES.addAll(new ArrayList<>(Collections.nCopies(45, "common")));
        PERSON_SOURCES.add("contact");
        PERSON_SOURCES.add("contact");
        PERSON_SOURCES.add("suggestion");
    }

    public ResourceExplorer(Context context) {
        this(context, null);
    }

    public ResourceExplorer(Context context, Long seed) {
        super(context, seed);
        databaseFinder = new DatabaseFinder(context, seed);
        reflectionFinder = new ReflectionFinder(context, seed);
    }

    public DatabaseFinder getDatabaseFinder() {
        return databaseFinder;
    }

    public ReflectionFinder getReflectionFinder() {
        return reflectionFinder;
    }

    @Override
    public void bindSeed(Long seed) {
        super.bindSeed(seed);
        databaseFinder.bindSeed(seed);
        reflectionFinder.bindSeed(seed);
    }

    @Override
    public void unbindSeed() {
        super.unbindSeed();
        databaseFinder.unbindSeed();
        reflectionFinder.unbindSeed();
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

    public String findByReflection(String methodName) {
        String s = reflectionFinder.callMethod(methodName);

        if (s != null)
            return s;
        s = reflectionFinder.invokeMethod(methodName);

        if (s != null)
            return s;
        s = reflectionFinder.getMethodByName(methodName);

        if (StringHelper.isNotNullOrEmpty(s))
            return s;
        return "?";
    }

    private class ReflectionFinder {
        private final Locale locale;
        private final GeneratorManager generatorManager;

        public ReflectionFinder(Context context) {
            this(context, null);
        }

        public ReflectionFinder(Context context, Long seed) {
            locale = context.getResources().getConfiguration().getLocales().get(0);
            generatorManager = new GeneratorManager(locale, seed);
        }

        public GeneratorManager getGenerator() {
            return generatorManager;
        }

        public void bindSeed(Long seed) {
            generatorManager.setSeed(seed);
        }

        public void unbindSeed() {
            generatorManager.setSeed(null);
        }

        public String callMethod(String methodName) {
            String s = null;

            try {
                Class<?> reflectionFinderClass = Class.forName("com.memoeslink.generator.finder.ReflectionFinder");
                Method m = reflectionFinderClass.getDeclaredMethod(methodName);
                m.setAccessible(true);
                s = (String) m.invoke(new ReflectionFinder(context, r.getSeed()));
            } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                e.printStackTrace();
            }
            return s;
        }

        public String invokeMethod(String methodName) {
            String s = (String) AccessController.doPrivileged((PrivilegedAction) () -> {
                String r = null;

                try {
                    Class<ReflectionFinder> reflectionFinderClass = ReflectionFinder.class;
                    Constructor<ReflectionFinder> constructor = reflectionFinderClass.getConstructor(Context.class, Long.class);
                    Object object = constructor.newInstance(context, this.generatorManager.getSeed());
                    Method method = reflectionFinderClass.getDeclaredMethod(methodName);
                    method.setAccessible(true);
                    r = (String) method.invoke(object);
                } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                    e.printStackTrace();
                }
                return r;
            });
            return s;
        }

        public String getMethodByName(String methodName) {
            return getMethodByRef(MethodReference.get(methodName));
        }

        public String getMethodByRef(MethodReference reference) {
            reference = reference != null ? reference : MethodReference.NONE;

            switch (reference) {
                case PERSON:
                    return getPerson().getSummary();
                case ANONYMOUS_PERSON:
                    return getAnonymousPerson().getSummary();
                case USERNAME:
                    return getUsername();
                case SECRET_NAME:
                    return getSecretName();
                case NOUN:
                    return getNoun();
                case NOUN_WITH_ARTICLE:
                    return getNounWithArticle();
                case DATE:
                    return getDate();
                case TIME:
                    return getTime();
                case PERCENTAGE:
                    return getPercentage();
                case DEFAULT_COLOR:
                    return getDefaultColor();
                case COLOR:
                    return getColorStr();
                case DEVICE_INFO:
                    return getDeviceInfo();
                case FORMATTED_NAME:
                    return getFormattedName();
                case SIMPLE_GREETING:
                    return getSimpleGreeting();
                case CURRENT_DAY_OF_WEEK:
                    return getCurrentDayOfWeek();
                case CURRENT_DATE:
                    return getCurrentDate();
                case CURRENT_TIME:
                    return getCurrentTime();
                case NONE:
                default:
                    return ResourceFinder.RESOURCE_NOT_FOUND;
            }
        }

        public Person getPerson() {
            return generatorManager.getPersonGenerator().getPerson(r.getElement(Gender.values()));
        }

        public Person getAnonymousPerson() {
            return generatorManager.getPersonGenerator().getAnonymousPerson(r.getElement(Gender.values()));
        }

        public String getUsername() {
            return generatorManager.getNameGenerator().getUsername();
        }

        public String getSecretName() {
            return generatorManager.getNameGenerator().getName(NameType.MALE_ITERATIVE_FORENAME);
        }

        public String getNoun() {
            return generatorManager.getNounGenerator().getNoun(Form.UNDEFINED);
        }

        public String getNounWithArticle() {
            return generatorManager.getNounGenerator().getNounWithArticle();
        }

        public String getDate() {
            return generatorManager.getDateTimeGenerator().getStrDate();
        }

        public String getTime() {
            return generatorManager.getDateTimeGenerator().getStrTime();
        }

        public String getPercentage() {
            return generatorManager.getStringGenerator().getPercentage();
        }

        public String getDefaultColor() {
            return ResourceGetter.with(r).getString(Constant.DEFAULT_COLORS);
        }

        public String getColorStr() {
            return generatorManager.getStringGenerator().getStrColor();
        }

        public String getDeviceInfo() {
            int infoType = r.getInt(1, 10);
            return new Device(context).getInfo(infoType);
        }

        public String getFormattedName() {
            switch (r.getItem(PERSON_SOURCES)) {
                case "anonymous":
                    return TextFormatter.formatUsername(getUsername());
                case "common":
                    Person person = generatorManager.getPersonGenerator().getPerson();
                    String name = person.getFullName();

                    switch (person.getGender()) {
                        case MASCULINE:
                            name += "｢1｣";
                            break;
                        case FEMININE:
                            name += "｢2｣";
                            break;
                    }
                    return TextFormatter.formatName(name);
                case "contact":
                    return TextFormatter.formatContactName(contactNameFinder.getContactName());
                case "suggestion":
                    String suggestedName = preferenceFinder.getStringSetValueOrDefault(
                            Preference.DATA_STORED_NAMES.getTag(),
                            preferenceFinder.getString(Preference.TEMP_NAME.getTag()),
                            Constant.DEVELOPER
                    );
                    return TextFormatter.formatSuggestedName(suggestedName);
                default:
                    return TextFormatter.formatText("?", "b,tt");
            }
        }

        public String getSimpleGreeting() {
            int hour = DateTimeHelper.getCurrentTime().getHour();
            return resourceFinder.getStrFromStrArrayRes(com.memoeslink.generator.R.array.emojis, hour);
        }

        public String getCurrentDayOfWeek() {
            return android.text.format.DateFormat.format("EEEE", new Date()).toString();
        }

        public String getCurrentDate() {
            return DateTimeGetter.with(locale).getCurrentDate(0);
        }

        public String getCurrentTime() {
            return DateTimeGetter.with(locale).getCurrentTime(0);
        }
    }
}

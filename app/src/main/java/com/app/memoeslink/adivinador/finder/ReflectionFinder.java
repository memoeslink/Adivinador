package com.app.memoeslink.adivinador.finder;

import android.content.Context;

import com.app.memoeslink.adivinador.LanguageHelper;
import com.app.memoeslink.adivinador.MethodReference;
import com.app.memoeslink.adivinador.R;
import com.app.memoeslink.adivinador.TagProcessor;
import com.memoeslink.generator.common.Binder;
import com.memoeslink.generator.common.Constant;
import com.memoeslink.generator.common.DateTimeGetter;
import com.memoeslink.generator.common.DateTimeHelper;
import com.memoeslink.generator.common.Device;
import com.memoeslink.generator.common.Form;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.GeneratorManager;
import com.memoeslink.generator.common.NameType;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.ResourceGetter;
import com.memoeslink.generator.common.TextFormatter;
import com.memoeslink.generator.common.finder.ContactNameFinder;
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

public class ReflectionFinder extends Binder {
    private static final List<String> PERSON_SOURCES;
    private final GeneratorManager manager;
    private final ContactNameFinder contactNameFinder;
    private final PreferenceFinder preferenceFinder;

    static {
        PERSON_SOURCES = new ArrayList<>();
        PERSON_SOURCES.addAll(new ArrayList<>(Collections.nCopies(15, "anonymous")));
        PERSON_SOURCES.addAll(new ArrayList<>(Collections.nCopies(45, "common")));
        PERSON_SOURCES.add("contact");
        PERSON_SOURCES.add("contact");
        PERSON_SOURCES.add("suggestion");
    }

    public ReflectionFinder(Context context) {
        this(context, null);
    }

    public ReflectionFinder(Context context, Long seed) {
        super(context, seed);
        manager = new GeneratorManager(Locale.getDefault(), seed);
        contactNameFinder = new ContactNameFinder(context, seed);
        preferenceFinder = new PreferenceFinder(context, seed);
    }

    public String callMethod(String methodName) {
        String s = null;

        try {
            Class<?> reflectionFinderClass = Class.forName("com.app.memoeslink.adivinador.finder.ReflectionFinder");
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
                Object object = constructor.newInstance(context, this.r.getSeed());
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
            case DEVICE_USER:
                return getDeviceUser();
            case FORMATTED_NAME:
                return getFormattedName();
            case SIMPLE_GREETING:
                return getSimpleGreeting();
            case CURRENT_DAY_OF_WEEK:
                return getCurrentDayOfWeek();
            case NONE:
            default:
                return ResourceFinder.RESOURCE_NOT_FOUND;
        }
    }

    public Person getPerson() {
        return manager.getPersonGenerator().getPerson(r.getElement(Gender.values()));
    }

    public Person getAnonymousPerson() {
        return manager.getPersonGenerator().getAnonymousPerson(r.getElement(Gender.values()));
    }

    public String getUsername() {
        return manager.getNameGenerator().getUsername();
    }

    public String getSecretName() {
        return manager.getNameGenerator().getName(NameType.MALE_ITERATIVE_FORENAME);
    }

    public String getNoun() {
        return manager.getNounGenerator().getNoun(Form.UNDEFINED);
    }

    public String getNounWithArticle() {
        return r.getBoolean() ? manager.getNounGenerator().getNounWithArticle(Form.UNDEFINED) :
                manager.getNounGenerator().getNounWithIndefiniteArticle(Form.UNDEFINED);
    }

    public String getDate() {
        return manager.getDateTimeGenerator().getStrDate();
    }

    public String getTime() {
        return manager.getDateTimeGenerator().getStrTime();
    }

    public String getPercentage() {
        return manager.getStringGenerator().getPercentage();
    }

    public String getDefaultColor() {
        return ResourceGetter.with(r).getString(Constant.DEFAULT_COLORS);
    }

    public String getColorStr() {
        return manager.getStringGenerator().getStrColor();
    }

    public String getDeviceUser() {
        int infoType = r.getInt(1, 10);
        String deviceUser = getString(R.string.user, new Device(context).getInfo(infoType));
        return new TagProcessor(context).replaceTags(deviceUser).getText();
    }

    public String getFormattedName() {
        switch (r.getItem(PERSON_SOURCES)) {
            case "anonymous":
                return TextFormatter.formatUsername(getUsername());
            case "common":
                Person person = getPerson();
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
                return TextFormatter.formatSuggestedName(preferenceFinder.getSuggestedName());
            default:
                return TextFormatter.formatText("?", "b,tt");
        }
    }

    public String getSimpleGreeting() {
        int hour = DateTimeHelper.getCurrentTime().getHour();
        return getResources().getStringArray(R.array.simple_greetings)[hour];
    }

    public String getCurrentDayOfWeek() {
        return android.text.format.DateFormat.format("EEEE", new Date()).toString();
    }

    public String getCurrentDate() {
        return DateTimeGetter.with(LanguageHelper.getLocale(this)).getCurrentDate(r.getInt(1, 14));
    }

    public String getCurrentTime() {
        return DateTimeGetter.with(LanguageHelper.getLocale(this)).getCurrentTime(r.getInt(1, 11));
    }
}

package com.app.memoeslink.adivinador.finder;

import android.content.Context;

import com.app.memoeslink.adivinador.MethodReference;
import com.app.memoeslink.adivinador.R;
import com.memoeslink.generator.common.Binder;
import com.memoeslink.generator.common.Constant;
import com.memoeslink.generator.common.Device;
import com.memoeslink.generator.common.Form;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.GeneratorManager;
import com.memoeslink.generator.common.NameType;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.ResourceGetter;
import com.memoeslink.generator.common.finder.ResourceFinder;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Locale;

public class ReflectionFinder extends Binder {
    GeneratorManager manager;

    public ReflectionFinder(Context context) {
        this(context, null);
    }

    public ReflectionFinder(Context context, Long seed) {
        super(context, seed);
        manager = new GeneratorManager(Locale.getDefault(), seed);
    }

    public String callMethod(String methodName) {
        String s = null;

        try {
            Class<?> resourceFinderClass = Class.forName("com.app.memoeslink.adivinador.finder.ReflectionFinder");
            Method m = resourceFinderClass.getDeclaredMethod(methodName);
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
                Class<ReflectionFinder> resourceFinderClass = ReflectionFinder.class;
                Constructor<ReflectionFinder> constructor = resourceFinderClass.getConstructor(Context.class, Long.class);
                Object object = constructor.newInstance(context, this.r.getSeed());
                Method method = resourceFinderClass.getDeclaredMethod(methodName);
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
        return getString(R.string.user, new Device(context).getInfo(infoType));
    }
}

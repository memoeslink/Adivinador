package com.app.memoeslink.adivinador;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Build;
import android.provider.ContactsContract;

import androidx.annotation.ArrayRes;
import androidx.annotation.RawRes;
import androidx.annotation.StringRes;
import androidx.core.content.ContextCompat;

import com.memoeslink.common.Randomizer;
import com.memoeslink.generator.common.Constant;
import com.memoeslink.generator.common.Form;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.GeneratorManager;
import com.memoeslink.generator.common.IntegerHelper;
import com.memoeslink.generator.common.LongHelper;
import com.memoeslink.generator.common.NameType;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.common.TextProcessor;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class ResourceFinder extends BaseWrapper {
    public static final String RESOURCE_NOT_FOUND = "";
    private static final HashMap<Integer, String[]> RESOURCE_REGISTRY = new HashMap<>();
    private static List<String> contactNames = null;
    private static List<String> suggestedNames = null;
    private final Randomizer r;
    private final GeneratorManager manager;
    private final Hardware hardware;

    public ResourceFinder(Context context) {
        this(context, null);
    }

    public ResourceFinder(Context context, Long seed) {
        super(context);
        r = new Randomizer(seed);
        manager = new GeneratorManager(Locale.getDefault(), seed);
        hardware = new Hardware(context);

        if (contactNames == null && context instanceof Activity)
            contactNames = getContactNames();

        if (suggestedNames == null && context instanceof Activity)
            suggestedNames = getSuggestedNames();
    }

    public void bindSeed(Long seed) {
        r.bindSeed(seed);
        manager.setSeed(seed);
    }

    public void unbindSeed() {
        r.unbindSeed();
        manager.setSeed(null);
    }

    //================================================================================
    // Default methods
    //================================================================================

    public String getStrFromArray(String[] items) {
        if (items == null || items.length == 0)
            return RESOURCE_NOT_FOUND;
        return items[r.getInt(items.length)];
    }

    public String getStrFromList(List<String> items) {
        if (items == null || items.size() == 0)
            return RESOURCE_NOT_FOUND;
        return items.get(r.getInt(items.size()));
    }

    public char getChar(String s) {
        if (s != null && s.length() > 0)
            return s.charAt(r.getInt(s.length()));
        return '\0';
    }

    public String getEmojiByUnicode(int unicode) {
        return new String(Character.toChars(unicode));
    }

    public String getColor(String s) {
        if (StringHelper.isNullOrEmpty(s))
            return String.format("#%06X", (0xFFFFFF & Color.GRAY));
        Randomizer r = new Randomizer(LongHelper.getSeed(s));
        return String.format("#%06x", r.getInt(0xffffff + 1));
    }

    //================================================================================
    // Android Resources methods
    //================================================================================

    public String getRawRes(@RawRes int id) {
        if (isResource(id) && getResources().getResourceTypeName(id).equals("raw")) {
            try {
                InputStream is = getResources().openRawResource(id);

                byte[] b = new byte[is.available()];
                is.read(b);
                is.close();
                is = null;
                return new String(b);
            } catch (IOException ignored) {
            }
        }
        return RESOURCE_NOT_FOUND;
    }

    public String getRawResFromName(String s) {
        if (s != null && !s.isEmpty()) {
            try {
                int resourceId = getResources().getIdentifier(s, "raw", getPackageName());

                if (resourceId != 0)
                    return getRawRes(resourceId);
            } catch (Exception ignored) {
            }
        }
        return RESOURCE_NOT_FOUND;
    }

    public String getStrRes(@StringRes int id) {
        if (isResource(id) && getResources().getResourceTypeName(id).equals("string")) {
            try {
                return getString(id);
            } catch (Exception ignored) {
            }
        }
        return RESOURCE_NOT_FOUND;
    }

    public String getStrResByName(String s) {
        if (s != null && !s.isEmpty()) {
            try {
                int resourceId = getResources().getIdentifier(s, "string", getPackageName());

                if (resourceId != 0)
                    return getString(resourceId);
            } catch (Exception ignored) {
            }
        }
        return RESOURCE_NOT_FOUND;
    }

    public String[] getStrArrayRes(@ArrayRes int id) {
        if (isResource(id) && getResources().getResourceTypeName(id).equals("array")) {
            try {
                return getResources().getStringArray(id);
            } catch (Exception ignored) {
            }
        }
        return new String[]{};
    }

    public String[] getStrArrayResByName(String s) {
        if (s != null && !s.isEmpty()) {
            try {
                int resourceId = getResources().getIdentifier(s, "array", getPackageName());

                if (resourceId != 0)
                    return getResources().getStringArray(resourceId);
            } catch (Exception ignored) {
            }
        }
        return new String[]{};
    }

    public String getStrFromStrArrayRes(@ArrayRes int id) {
        int length = getArrayResLength(id);

        if (length == 0)
            return RESOURCE_NOT_FOUND;
        return getStrFromStrArrayRes(id, r.getInt(length));
    }

    public String getStrFromStrArrayRes(@ArrayRes int id, int index) {
        String[] items = getStrArrayRes(id);

        if (items.length == 0)
            return RESOURCE_NOT_FOUND;
        index = IntegerHelper.defaultIndex(index, items.length);
        return items[index];
    }

    public int[] getIntArrayRes(@ArrayRes int id) {
        if (isResource(id) && getResources().getResourceTypeName(id).equals("array")) {
            try {
                return getResources().getIntArray(id);
            } catch (Exception ignored) {
            }
        }
        return new int[]{};
    }

    public int[] getIntArrayResByName(String s) {
        if (s != null && !s.isEmpty()) {
            try {
                int resourceId = getResources().getIdentifier(s, "array", getPackageName());

                if (resourceId != 0)
                    return getResources().getIntArray(resourceId);
            } catch (Exception ignored) {
            }
        }
        return new int[]{};
    }

    public int getIntFromIntArrayRes(@ArrayRes int id) {
        int length = getArrayResLength(id);

        if (length == 0)
            return 0;
        return getIntFromIntArrayRes(id, r.getInt(length));
    }

    public int getIntFromIntArrayRes(@ArrayRes int id, int index) {
        int[] items = getIntArrayRes(id);

        if (items.length == 0)
            return 0;
        index = IntegerHelper.defaultIndex(index, items.length);
        return items[index];
    }

    public String[] getStrArrayFromSplitStrRes(@StringRes int id) {
        if (!RESOURCE_REGISTRY.containsKey(id)) {
            String s = getStrRes(id);
            String[] items = {};

            if (StringHelper.isNotNullOrBlank(s))
                items = s.split("¶[ ]*");
            RESOURCE_REGISTRY.put(id, items);
        }
        return RESOURCE_REGISTRY.get(id);
    }

    public String getStrFromSplitStrRes(@StringRes int id) {
        int length = getSplitStrResLength(id);

        if (length == 0)
            return RESOURCE_NOT_FOUND;
        return getStrFromSplitStrRes(id, r.getInt(length));
    }

    public String getStrFromSplitStrRes(@StringRes int id, int index) {
        String[] items = getStrArrayFromSplitStrRes(id);

        if (items.length == 0)
            return RESOURCE_NOT_FOUND;
        index = IntegerHelper.defaultIndex(index, items.length);
        return items[index];
    }

    public List<String> getStrListFromSplitStrRes(@StringRes int id) {
        return Arrays.asList(getStrArrayFromSplitStrRes(id));
    }

    public int getSplitStrResLength(@StringRes int id) {
        try {
            return getStrArrayFromSplitStrRes(id).length;
        } catch (Exception e) {
            return 0;
        }
    }

    public String getEmojis(int length) {
        length = IntegerHelper.defaultInt(length, 0, 999);
        StringBuilder sb = new StringBuilder();

        for (int n = 0; n < length; n++) {
            sb.append(getEmoji());
        }
        return sb.toString();
    }

    public String getEmoji() {
        return getEmojiByUnicode(getIntFromIntArrayRes(R.array.emojis));
    }

    public String getEmoji(int index) {
        return getEmojiByUnicode(getIntFromIntArrayRes(R.array.emojis, index));
    }

    public String getColorStr() {
        return getStrFromSplitStrRes(R.string.common_colors);
    }

    public String getColorStr(int index) {
        return getStrFromSplitStrRes(R.string.common_colors, index);
    }

    public String getColorStr(String s) {
        Randomizer r = new Randomizer(LongHelper.getSeed(s));
        int index = r.getInt(getSplitStrResLength(R.string.common_colors));
        return getStrFromSplitStrRes(R.string.common_colors, index);
    }

    public String getGenderName(Gender gender, int type) {
        gender = gender != null ? gender : Gender.UNDEFINED;
        type = IntegerHelper.defaultInt(type, 1, 3);

        switch (type) {
            case 1:
                return getStrArrayRes(R.array.genders)[gender.ordinal()];
            case 2:
                return StringHelper.uncapitalizeFirst(getStrArrayRes(R.array.genders)[gender.ordinal()]);
            case 3:
                return String.valueOf(StringHelper.getFirstChar(getStrArrayRes(R.array.genders)[gender.ordinal()]));
            default:
                return "";
        }
    }

    public int getArrayResLength(@ArrayRes int id) {
        try {
            return getResources().getStringArray(id).length;
        } catch (Exception ignored) {
        }

        try {
            return getResources().getIntArray(id).length;
        } catch (Exception ignored) {
        }
        return 0;
    }

    public int getStringResourceId(String s) {
        return getResources().getIdentifier(s, "string", getPackageName());
    }

    public int getArrayResourceId(String s) {
        return getResources().getIdentifier(s, "array", getPackageName());
    }

    public boolean isResource(int id) {
        try {
            return id != 0 && getResources().getResourceName(id) != null;
        } catch (Resources.NotFoundException ignore) {
        }
        return false;
    }

    //================================================================================
    // Reflection methods
    //================================================================================

    public String callMethod(String methodName) {
        String s = null;

        try {
            Class<?> resourceFinderClass = Class.forName("com.app.memoeslink.adivinador.ResourceFinder");
            Method m = resourceFinderClass.getDeclaredMethod(methodName);
            m.setAccessible(true);
            s = (String) m.invoke(new ResourceFinder(context, r.getSeed()));
        } catch (ClassNotFoundException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return s;
    }

    public String invokeMethod(String methodName) {
        String s = (String) AccessController.doPrivileged((PrivilegedAction) () -> {
            String r = null;

            try {
                Class<ResourceFinder> resourceFinderClass = ResourceFinder.class;
                Constructor<ResourceFinder> constructor = resourceFinderClass.getConstructor(Context.class, Long.class);
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

    //================================================================================
    // Database methods
    //================================================================================

    public String getAbstractNoun() {
        switch (getString(R.string.locale)) {
            case "en":
                return Database.getInstance(this).selectEnglishAbstractNoun(r.getInt(1, Database.getInstance(this).countEnglishAbstractNouns()));
            case "es":
                return Database.getInstance(this).selectSpanishAbstractNoun(r.getInt(1, Database.getInstance(this).countSpanishAbstractNouns()));
            default:
                return RESOURCE_NOT_FOUND;
        }
    }

    public String getAction() {
        switch (getString(R.string.locale)) {
            case "en":
                return Database.getInstance(this).selectEnglishAction(r.getInt(1, Database.getInstance(this).countEnglishActions()));
            case "es":
                return Database.getInstance(this).selectSpanishAction(r.getInt(1, Database.getInstance(this).countSpanishActions()));
            default:
                return RESOURCE_NOT_FOUND;
        }
    }

    public String getDivination() {
        switch (getString(R.string.locale)) {
            case "en":
                return Database.getInstance(this).selectEnglishPrediction(r.getInt(1, Database.getInstance(this).countEnglishPredictions()));
            case "es":
                return Database.getInstance(this).selectSpanishPrediction(r.getInt(1, Database.getInstance(this).countSpanishPredictions()));
            default:
                return RESOURCE_NOT_FOUND;
        }
    }

    public String getFortuneCookie() {
        switch (getString(R.string.locale)) {
            case "en":
                return Database.getInstance(this).selectEnglishFortuneCookie(r.getInt(1, Database.getInstance(this).countEnglishFortuneCookies()));
            case "es":
                return Database.getInstance(this).selectSpanishFortuneCookie(r.getInt(1, Database.getInstance(this).countSpanishFortuneCookies()));
            default:
                return RESOURCE_NOT_FOUND;
        }
    }

    public String getPhrase() {
        switch (getString(R.string.locale)) {
            case "en":
                return Database.getInstance(this).selectEnglishPhrase(r.getInt(1, Database.getInstance(this).countEnglishPhrases()));
            case "es":
                return Database.getInstance(this).selectSpanishPhrase(r.getInt(1, Database.getInstance(this).countSpanishPhrases()));
            default:
                return RESOURCE_NOT_FOUND;
        }
    }

    //================================================================================
    // Generator methods
    //================================================================================

    public Person getPerson() {
        return manager.getPersonGenerator().getPerson(r.getElement(Gender.values()));
    }

    public Person getAnonymousPerson() {
        return manager.getPersonGenerator().getAnonymousPerson(r.getElement(Gender.values()));
    }

    public String getMaleFullName() {
        return manager.getNameGenerator().getName(NameType.MALE_FULL_NAME);
    }

    public String getFemaleFullName() {
        return manager.getNameGenerator().getName(NameType.FEMALE_FULL_NAME);
    }

    public String getFullName() {
        return r.getBoolean() ? getMaleFullName() : getFemaleFullName();
    }

    public String getSecretName() {
        return manager.getNameGenerator().getName(NameType.MALE_ITERATIVE_FORENAME);
    }

    public String getUsername() {
        return manager.getNameGenerator().getUsername();
    }

    public String getNoun() {
        return manager.getNounGenerator().getNoun(Form.UNDEFINED);
    }

    public String getNounWithArticle() {
        switch (r.getInt(2)) {
            case 0:
                return manager.getNounGenerator().getNounWithArticle(Form.UNDEFINED);
            case 1:
                return manager.getNounGenerator().getNounWithIndefiniteArticle(Form.UNDEFINED);
            default:
                return getNoun();
        }
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

    //================================================================================
    // SharedPreferences methods
    //================================================================================

    public Gender getGender() {
        int genderValue = preferences.getInt("temp_gender", r.getInt(3));
        return Gender.get(genderValue);
    }

    private List<String> getSuggestedNames() {
        List<String> suggestedNames = new ArrayList(preferences.getStringSet("nameList"));
        suggestedNames.add(Constant.DEVELOPER);
        return suggestedNames;
    }

    public int getSuggestedNamesSize() {
        return suggestedNames == null ? 0 : suggestedNames.size();
    }

    public String getSuggestedName() {
        if (suggestedNames == null)
            suggestedNames = getSuggestedNames();

        if (suggestedNames.size() == 0)
            return RESOURCE_NOT_FOUND;
        String suggestedName;

        do {
            suggestedName = r.getItem(suggestedNames);
        } while (preferences.getString("temp_name").equals(suggestedName));
        return StringHelper.defaultWhenBlank(suggestedName);
    }

    //================================================================================
    // ContentResolver methods
    //================================================================================

    private List<String> getContactNames() {
        List<String> contacts = new ArrayList<>();
        boolean proceed = false;
        Cursor c = null;

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.READ_CONTACTS) == PackageManager.PERMISSION_GRANTED)
            proceed = true;

        try {
            if (proceed) {
                ContentResolver contentResolver = getContentResolver();
                c = contentResolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);

                if ((c != null ? c.getCount() : 0) > 0 && c.moveToFirst()) {
                    do {
                        int index = c.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                        contacts.add(c.getString(index));
                    } while (c.moveToNext());
                }
            }
        } catch (Exception ignored) {
        } finally {
            if (c != null)
                c.close();
        }
        return contacts;
    }

    public int getContactNamesSize() {
        return contactNames == null ? 0 : contactNames.size();
    }

    public String getContactName() {
        if (contactNames == null)
            contactNames = getContactNames();

        if (contactNames.size() == 0)
            return RESOURCE_NOT_FOUND;
        return StringHelper.trimToEmpty(r.getItem(contactNames));
    }

    //================================================================================
    // Hardware-related methods
    //================================================================================

    public String getDeviceUser() {
        switch (r.getInt(9)) {
            case 0:
                String name = hardware.getAndroidVersionName();

                if (StringHelper.isNullOrEmpty(name)) {
                    try {
                        name = getPackageManager().getPackageInfo(getPackageName(), 0).versionName;
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return getString(R.string.device_version_user, TextProcessor.genderifyStr(getString(R.string.default_user), getGender()).getText(), name + " " + "(" + Build.VERSION.RELEASE + ")");
            case 1:
                return getString(R.string.device_user, TextProcessor.genderifyStr(getString(R.string.default_user), getGender()).getText(), StringHelper.defaultWhenEmpty(Build.MANUFACTURER) + StringHelper.prependSpaceIfNotEmpty(Build.MODEL));
            case 2:
                return getString(R.string.device_user, TextProcessor.genderifyStr(getString(R.string.default_user), getGender()).getText(), StringHelper.defaultWhenEmpty(Build.BRAND) + StringHelper.prependSpaceIfNotEmpty(Build.MODEL));
            case 3:
                return getString(R.string.device_user, TextProcessor.genderifyStr(getString(R.string.default_user), getGender()).getText(), StringHelper.defaultWhenEmpty(Build.PRODUCT));
            case 4:
                return getString(R.string.android_device_user, TextProcessor.genderifyStr(getString(R.string.default_user), getGender()).getText(), hardware.getDeviceId());
            case 5:
                return getString(R.string.device_brand_user, TextProcessor.genderifyStr(getString(R.string.default_user), getGender()).getText(), StringHelper.defaultWhenEmpty(Build.BRAND));
            case 6:
                String networkName = hardware.getNetworkName();

                if (StringHelper.isNullOrEmpty(networkName))
                    return getString(R.string.network_disconnected_user);
                else if (networkName.equals("<unknown ssid>"))
                    return getString(R.string.network_unspecific_user);
                return getString(R.string.network_user, networkName);
            case 7:
                String networkOperator = hardware.getNetworkOperator();
                return StringHelper.isNullOrBlank(networkOperator) ? getString(R.string.network_operator_disconnected_user) :
                        getString(R.string.network_operator_user, networkOperator);
            case 8:
                String ipAddress = hardware.getLocalIpAddress();
                return StringHelper.isNullOrEmpty(ipAddress) ? getString(R.string.device_unspecific_user) :
                        getString(R.string.device_connected_user, ipAddress);
            default:
                return TextProcessor.genderifyStr(getString(R.string.default_user), getGender()).getText();
        }
    }
}

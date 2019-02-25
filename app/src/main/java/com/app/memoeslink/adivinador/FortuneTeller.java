package com.app.memoeslink.adivinador;

import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.SharedPreferences;
import android.content.res.TypedArray;
import android.preference.PreferenceManager;
import android.support.annotation.DrawableRes;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

import static com.app.memoeslink.adivinador.Methods.PREFERENCES;

/**
 * Created by Memoeslink on 04/04/2018.
 */

public class FortuneTeller extends ContextWrapper {
    private static Long seed = null;
    private SharedPreferences preferences;
    private SharedPreferences defaultPreferences;
    private Methods methods;
    private Randomizer randomizer;

    public FortuneTeller(Context context) {
        super(context);
        preferences = getSharedPreferences(PREFERENCES, Activity.MODE_PRIVATE);
        defaultPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        methods = new Methods(context);
        randomizer = new Randomizer(null);
        setRandomizer(); //Initialize Randomizer with or without seed
    }

    public void setRandomizer() {
        if (!defaultPreferences.getString("preference_seed", "").trim().isEmpty())
            seed = Methods.getSeed(defaultPreferences.getString("preference_seed", ""));
        else
            seed = null;
        methods.getRandomizer().bindSeed(seed);
        randomizer.bindSeed(seed);
    }

    public String comment() {
        return methods.getRandomEmoji((short) 0, (short) 0, 1) + " " + methods.getSplitString(R.string.default_comments);
    }

    public String greet(int... index) {
        String s;
        String extra = "";

        //Get an extra comment to add to greeting, if possible
        switch (randomizer.getInt(6, 0)) {
            case 0:
                extra = methods.getSubject(methods.getSex(), randomizer.getBoolean());
                break;
            case 1:
                extra = methods.getSubject(methods.getSex(), null);
                break;
            case 2:
                extra = methods.getInterjection();
                break;
            case 3:
                extra = methods.replaceTags(methods.getDeviceCommonUser()).getText();
                break;
        }

        //Give format to extra comment
        if (!extra.equals("")) {
            if (extra.contains("¬")) {
                extra = StringUtils.replace(extra, "¬", "");
                extra = "<i> " + extra + "</i>";
            } else
                extra = "<b><font color=" + methods.getColorAsString() + ">" + extra + "</font></b>";
        }
        extra = extra.equals("") ? extra : ("," + " " + extra);

        if (methods.verifyIntVararg(index))
            s = methods.getSplitString(R.string.greetings, index[0]).replace("§", extra);
        else {
            int typeOfGreeting = randomizer.getInt(6, 0);
            String emoji = methods.getRandomEmoji((short) 60, (short) 20, 2);

            if (typeOfGreeting == 0) {
                List<String> greetings;
                int timeOfDay = methods.getHourOfDay();

                if (timeOfDay >= 0 && timeOfDay < 12)
                    greetings = methods.getStringAsList(R.string.greetings_day);
                else if (timeOfDay >= 12 && timeOfDay < 19)
                    greetings = methods.getStringAsList(R.string.greetings_afternoon);
                else if (timeOfDay >= 19 && timeOfDay < 24)
                    greetings = methods.getStringAsList(R.string.greetings_night);
                else
                    greetings = methods.getStringAsList(R.string.greetings_default);
                s = greetings.get(randomizer.getInt(greetings.size(), 0)).replace("§", extra) + " " + methods.getSplitString(R.string.phrase_fortune_telling);
            } else if (typeOfGreeting == 1)
                s = methods.getSplitString(R.string.greetings_component) + " " + methods.getDayOfWeekName() + extra + Methods.FULL_STOP + " " + (emoji.isEmpty() ? "" : emoji + " ") + methods.getSplitString(R.string.phrase_fortune_telling);
            else if (typeOfGreeting == 2)
                s = methods.getSplitString(R.string.date_component) + " " + methods.getCurrentDate(randomizer.getInt(9, 0)) + extra + Methods.FULL_STOP + " " + (emoji.isEmpty() ? "" : emoji + " ") + methods.getSplitString(R.string.phrase_fortune_telling);
            else if (typeOfGreeting == 3)
                s = methods.getSplitString(R.string.time_component) + " " + methods.getCurrentTime(randomizer.getInt(4, 0)) + extra + Methods.FULL_STOP + " " + (emoji.isEmpty() ? "" : emoji + " ") + methods.getSplitString(R.string.phrase_fortune_telling);
            else
                s = methods.getSplitString(R.string.greetings).replace("§", extra) + " " + (emoji.isEmpty() ? "" : emoji + " ") + methods.getSplitString(R.string.phrase_fortune_telling);
        }
        s = StringUtils.replace(s, "..", ".");
        return String.format(getResources().getString(R.string.html_format), s);
    }

    public String talk() {
        String emoji = methods.getRandomEmoji((short) 70, (short) 14, 1);
        return methods.getPhrase() + (emoji.isEmpty() ? "" : " " + emoji);
    }

    public String talkAboutSomeone(int... index) {
        String s;
        String extra;
        boolean suggestionIncluded = false;
        boolean contactIncluded = false;

        if (methods.verifyIntVararg(index))
            s = methods.getStringFromList(methods.getOpinions(), index[0]);
        else
            s = methods.getOpinion();

        //Replace some characters to random names, and then give them format
        while (s.contains("##")) {
            ArrayList nameList = null;
            String contactName = null;
            String formattedContent;
            String color = methods.getColorAsString();

            if (randomizer.getBoolean()) {
                if (!suggestionIncluded && randomizer.getInt(50, 0) == 0) {
                    if (preferences.contains("nameList") && preferences.getStringSet("nameList", null).size() > 0)
                        nameList = new ArrayList(preferences.getStringSet("nameList", null));
                }
            } else {
                if (!contactIncluded && randomizer.getInt(25, 0) == 0) {
                    contactName = methods.getContactName(true);
                }
            }

            if (contactName != null && !contactName.trim().isEmpty()) {
                formattedContent = "<font color=" + color + ">" + contactName + "</font>";
                s = methods.replaceTags(s, -1).getText();
                contactIncluded = true;
            } else if (nameList != null && nameList.size() > 0) {
                String name;

                if (nameList.size() >= 2) {
                    name = nameList.get(randomizer.getInt(nameList.size(), 0)).toString();

                    while (preferences.getString("temp_name", "").equals(name)) {
                        name = nameList.get(randomizer.getInt(nameList.size(), 0)).toString();
                    }
                    name = name.isEmpty() ? "?" : name;
                    formattedContent = "<font color=" + color + ">" + methods.formatText(new String[]{name}, "", "b") + "</font>";
                    suggestionIncluded = true;
                } else
                    formattedContent = "<font color=" + color + ">" + methods.formatText(new String[]{getString(R.string.developer)}, "", "b,i") + "</font>";
                s = methods.replaceTags(s, -1).getText();
            } else {
                Person person = methods.getPerson();

                if (randomizer.getInt(4, 0) == 0)
                    formattedContent = "<font color=" + color + ">" + methods.formatText(new String[]{person.getUsername()}, "", "b,tt") + "</font>";
                else
                    formattedContent = (person.getTitleOfHonor().isEmpty() ? "" : "<i>" + person.getTitleOfHonor() + "</i>" + " ")
                            + "<font color=" + color + ">" + methods.formatText(new String[]{person.getForename(), person.getLastName()}, person.getSuffix(), "b")
                            + (person.getJapaneseHonorific().isEmpty() ? "" : person.getJapaneseHonorific())
                            + (person.getPostNominalLetters().isEmpty() ? "" : "," + " " + methods.formatText(new String[]{person.getPostNominalLetters()}, "", "b,i")) + "</font>";
                s = methods.replaceTags(s, person.getSex()).getText();
            }
            s = StringUtils.replaceOnce(s, "##", formattedContent);
        }
        s = StringUtils.replace(s, " a el ", " al ");
        s = methods.fixDoubleDot(s);

        //Add emoji, emoticon or nothing
        float probability = randomizer.getFloat();

        if (probability <= 0.25F) {
            extra = methods.getSplitString(R.string.emoticons);
            extra = "<b><font color=" + methods.getColorAsString() + ">" + extra + "</font></b>";
        } else if (probability <= 0.5F)
            extra = methods.getRandomEmoji((short) 60, (short) 20, 2);
        else
            extra = "";
        s = s + (extra.isEmpty() ? "" : " " + extra);
        return String.format(getResources().getString(R.string.html_format), s);
    }

    public @DrawableRes
    int getRandomAppearance() {
        int resId;
        int arrayId;
        int initialRes;

        switch (Integer.parseInt(defaultPreferences.getString("preference_fortuneTellerAspect", "1"))) {
            case 0:
                arrayId = R.array.crystal_ball;
                initialRes = R.drawable.ic_crystal_ball;
                break;
            case 1:
                arrayId = R.array.emoji_collection;
                initialRes = R.drawable.ic_emoji_angel;
                break;
            case 2:
                arrayId = R.array.emoticon_collection;
                initialRes = R.drawable.ic_emoticon_alien;
                break;
            case 3:
                arrayId = R.array.smiley_collection;
                initialRes = R.drawable.ic_smiley_3d_glasses;
                break;
            default:
                arrayId = R.array.crystal_ball;
                initialRes = R.drawable.ic_crystal_ball;
                break;
        }
        TypedArray images = getResources().obtainTypedArray(arrayId);
        int randomRes = (int) (randomizer.getDouble() * images.length());
        resId = images.getResourceId(randomRes, initialRes);
        images.recycle();
        return resId;
    }
}

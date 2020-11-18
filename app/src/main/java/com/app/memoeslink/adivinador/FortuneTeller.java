package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.res.TypedArray;

import androidx.annotation.DrawableRes;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Memoeslink on 04/04/2018.
 */

public class FortuneTeller extends BaseWrapper {
    private Long seed;
    private Methods methods;
    private Randomizer randomizer;

    public FortuneTeller(Context context) {
        super(context);
        methods = new Methods(context);
        randomizer = new Randomizer(null);
        setRandomizer(); //Initialize Randomizer with or without seed
    }

    public void setRandomizer() {
        if (!StringUtils.isBlank(defaultPreferences.getString("preference_seed")))
            seed = Methods.getSeed(defaultPreferences.getString("preference_seed"));
        else
            seed = null;
        methods.getRandomizer().bindSeed(seed);
        randomizer.bindSeed(seed);
    }

    public String comment() {
        return methods.getIdeogramSet(0, 0, 1) + " " + methods.getSplitString(R.string.default_comments);
    }

    public String greet() {
        return greet(-1);
    }

    public String greet(int index) {
        String s;
        String addendum = methods.replaceTags(methods.getStringFromStringArray(R.array.conversation_addendum)).getText();

        if (index >= 0)
            s = methods.getSplitString(R.string.greetings, index).replace("§", addendum);
        else {
            String ideogramSet = methods.getIdeogramSet(60, 20, 2);

            switch (randomizer.getInt(0, 6)) {
                case 0:
                    s = greetShortly(addendum) + " " + methods.getStringFromStringArray(R.array.phrase_augury);
                    break;
                case 1:
                    s = methods.getSplitString(R.string.greetings_component) + " " + methods.getDayOfWeekName() + (StringUtils.isNotBlank(addendum) ? ", " + addendum : "") + Methods.FULL_STOP + " " + (StringUtils.isNotBlank(ideogramSet) ? ideogramSet + " " : ideogramSet) + methods.getStringFromStringArray(R.array.phrase_augury);
                    break;
                case 2:
                    s = methods.getSplitString(R.string.date_component) + " " + methods.getCurrentDate(randomizer.getInt(0, 9)) + (StringUtils.isNotBlank(addendum) ? ", " + addendum : "") + Methods.FULL_STOP + " " + (StringUtils.isNotBlank(ideogramSet) ? ideogramSet + " " : ideogramSet) + methods.getStringFromStringArray(R.array.phrase_augury);
                    break;
                case 3:
                    s = methods.getSplitString(R.string.time_component) + " " + methods.getCurrentTime(randomizer.getInt(0, 4)) + (StringUtils.isNotBlank(addendum) ? ", " + addendum : "") + Methods.FULL_STOP + " " + (StringUtils.isNotBlank(ideogramSet) ? ideogramSet + " " : ideogramSet) + methods.getStringFromStringArray(R.array.phrase_augury);
                    break;
                default:
                    s = methods.getSplitString(R.string.greetings).replace("§", StringUtils.isNotBlank(addendum) ? ", " + addendum : "") + " " + (StringUtils.isNotBlank(ideogramSet) ? ideogramSet + " " : ideogramSet) + methods.getStringFromStringArray(R.array.phrase_augury);
                    break;
            }
        }
        s = StringUtils.replace(s, "..", ".");
        return getString(R.string.html_format, s);
    }

    public String greetShortly() {
        return greetShortly("");
    }

    public String greetShortly(String addendum) {
        addendum = StringUtils.defaultIfBlank(addendum, "");
        addendum = StringUtils.isNotEmpty(addendum) ? ", " + addendum : "";
        int timeOfDay = Methods.getHourOfDay();

        if (timeOfDay >= 0 && timeOfDay < 12)
            return methods.getSplitString(R.string.greetings_day).replace("§", addendum);
        else if (timeOfDay >= 12 && timeOfDay < 19)
            return methods.getSplitString(R.string.greetings_afternoon).replace("§", addendum);
        else if (timeOfDay >= 19 && timeOfDay < 24)
            return methods.getSplitString(R.string.greetings_night).replace("§", addendum);
        else
            return methods.getSplitString(R.string.greetings_default).replace("§", addendum);
    }

    public String talk() {
        String ideogramSet = methods.getIdeogramSet(70, 14, 1);
        return methods.getPhrase() + (ideogramSet.isEmpty() ? "" : " " + ideogramSet);
    }

    public String talk(int times) {
        String conversation = "";
        String ideogramSet = methods.getIdeogramSet(70, 14, 1);

        while (times > 0) {
            conversation += (StringUtils.isNotEmpty(conversation) ? " " : "") + methods.getPhrase();
            times--;
        }
        return StringUtils.stripToEmpty(conversation) + (ideogramSet.isEmpty() ? "" : " " + ideogramSet);
    }

    public String talkAboutSomeone() {
        return talkAboutSomeone(-1);
    }

    public String talkAboutSomeone(int index) {
        String s;
        String extra;

        if (index >= 0)
            s = methods.getStringFromStringArray(R.array.opinions, index);
        else
            s = methods.getStringFromStringArray(R.array.opinions);

        //Initialize person sources
        List<String> personSources = new ArrayList<>(Collections.nCopies(50, "random"));
        personSources.add("contact");
        personSources.add("contact");
        personSources.add("suggestion");

        //Replace some characters to random names, and then give them format
        while (s.contains("##")) {
            String name;
            String content;
            int sex = -1;

            switch (personSources.get(randomizer.getInt(0, personSources.size()))) {
                case "contact":
                    name = methods.getContactName(true);
                    content = "<font color=" + methods.getColorString() + ">" + name + "</font>";
                    personSources.remove("contact");
                    break;
                case "random":
                    Person person = methods.getPerson();
                    name = person.getFullName();
                    sex = person.getSex();

                    if (randomizer.getInt(4, 0) == 0)
                        content = "<font color=" + methods.getColorString() + ">" + person.getFormattedUsername() + "</font>";
                    else
                        content = (StringUtils.isNotBlank(person.getHonoraryTitle()) ? Methods.formatText(person.getHonoraryTitle(), "i") + " " : "")
                                + "<font color=" + methods.getColorString() + ">" + person.getFormattedFullName()
                                + (StringUtils.defaultIfBlank(person.getJapaneseHonorific(), ""))
                                + (StringUtils.isNotBlank(person.getPostNominalLetters()) ? ", " + Methods.formatText(person.getPostNominalLetters(), "b,i") : "")
                                + "</font>";
                    break;
                case "suggestion":
                    name = methods.getSuggestedName(true);
                    content = "<font color=" + methods.getColorString() + ">" + name + "</font>";
                    personSources.remove("suggestion");
                    break;
                default:
                    name = "?";
                    content = Methods.formatText("?", "b,tt");
                    break;
            }

            if (StringUtils.isEmpty(name))
                continue;
            s = methods.replaceTags(s, sex).getText();
            s = StringUtils.replaceOnce(s, "##", content);
        }
        s = StringUtils.replace(s, " a el ", " al ");
        s = methods.fixDoubleFullStop(s);

        //Add ideogram, emoticon or nothing
        float probability = randomizer.getFloat();

        if (probability <= 0.25F) {
            extra = methods.getSplitString(R.string.emoticons);
            extra = "<b><font color=" + methods.getColorString() + ">" + extra + "</font></b>";
        } else if (probability <= 0.5F)
            extra = methods.getIdeogramSet(60, 20, 2);
        else
            extra = "";
        s = s + (extra.isEmpty() ? "" : " " + extra);
        return getString(R.string.html_format, s);
    }

    public @DrawableRes
    int getRandomAppearance() {
        int resId;
        int arrayId;
        int initialRes;

        switch (defaultPreferences.getStringAsInt("preference_fortuneTellerAspect", 1)) {
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
            case 0:
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

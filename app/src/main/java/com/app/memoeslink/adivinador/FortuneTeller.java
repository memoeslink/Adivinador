package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.res.TypedArray;

import com.memoeslink.generator.common.DateTimeHelper;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.LongHelper;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.Randomizer;
import com.memoeslink.generator.common.StringHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FortuneTeller extends TextFormatter {

    public FortuneTeller(Context context) {
        super(context);

        if (StringHelper.isNotNullOrBlank(defaultPreferences.getString("preference_seed")))
            r = new Randomizer(LongHelper.getSeed(defaultPreferences.getString("preference_seed")));
        else
            r = new Randomizer();
    }

    public String comment() {
        return getEmojis(r.getInt(1, 4)) + " " + getStrFromSplitStrRes(R.string.default_comments);
    }

    public String greet() {
        return greet(-1);
    }

    public String greet(int index) {
        String s;
        String addendum = replaceTags(getStrFromStrArrayRes(R.array.conversation_addendum)).getText();

        if (index >= 0) {
            s = getStrFromSplitStrRes(R.string.greetings, index);
            s = StringHelper.replace(s, "§", addendum);
        } else {
            String ideogramSet = getEmojis(r.getInt(1, 4));

            switch (r.getInt(6)) {
                case 0:
                    s = greetShortly(addendum) + " " + getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                case 1:
                    s = getStrFromSplitStrRes(R.string.greetings_component) + " " + DateTimeHelper.getStrCurrentDayOfWeek() + StringHelper.prependIfNotEmpty(addendum, ", ") + ". " + StringHelper.appendSpaceIfNotEmpty(ideogramSet) + getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                case 2:
                    s = getStrFromSplitStrRes(R.string.date_component) + " " + DateTimeHelper.getStrCurrentDate(r.getInt(1, 11)) + StringHelper.prependIfNotEmpty(addendum, ", ") + ". " + StringHelper.appendSpaceIfNotEmpty(ideogramSet) + getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                case 3:
                    s = getStrFromSplitStrRes(R.string.time_component) + " " + DateTimeHelper.getStrCurrentTime(r.getInt(1, 8)) + StringHelper.prependIfNotEmpty(addendum, ", ") + ". " + StringHelper.appendSpaceIfNotEmpty(ideogramSet) + getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                default:
                    s = StringHelper.replaceOnce(getStrFromSplitStrRes(R.string.greetings), "§", StringHelper.prependIfNotEmpty(addendum, ", ")) + " " + StringHelper.appendSpaceIfNotEmpty(ideogramSet) + getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
            }
        }
        s = StringHelper.replace(s, "..", ".");
        return getString(R.string.html_format, s);
    }

    public String greetShortly() {
        return greetShortly("");
    }

    public String greetShortly(String addendum) {
        addendum = StringHelper.defaultIfBlank(addendum);
        addendum = StringHelper.prependIfNotEmpty(addendum, ", ");
        int hour = DateTimeHelper.getCurrentTime().getHour();

        if (hour >= 0 && hour < 12)
            return StringHelper.replace(getStrFromSplitStrRes(R.string.greetings_day), "§", addendum);
        else if (hour >= 12 && hour < 19)
            return StringHelper.replace(getStrFromSplitStrRes(R.string.greetings_afternoon), "§", addendum);
        else if (hour >= 19 && hour < 24)
            return StringHelper.replace(getStrFromSplitStrRes(R.string.greetings_night), "§", addendum);
        else
            return StringHelper.replace(getStrFromSplitStrRes(R.string.greetings_default), "§", addendum);
    }

    public String talk() {
        return talk(1);
    }

    public String talk(int times) {
        String conversation = "";
        String ideogramSet = getEmojis(r.getInt(1, 4));

        while (times > 0) {
            conversation = StringHelper.appendIfNotEmpty(conversation, " ") + getPhrase();
            times--;
        }
        return StringHelper.trimToEmpty(conversation) + StringHelper.prependSpaceIfNotEmpty(ideogramSet);
    }

    public String talkAboutSomeone() {
        return talkAboutSomeone(-1);
    }

    public String talkAboutSomeone(int index) {
        String s;
        String extra;

        if (index >= 0)
            s = getStrFromStrArrayRes(R.array.opinion, index);
        else
            s = getStrFromStrArrayRes(R.array.opinion);

        //Initialize person sources
        List<String> personSources = new ArrayList<>(Collections.nCopies(50, "random"));
        personSources.add("contact");
        personSources.add("contact");
        personSources.add("suggestion");

        //Replace some characters to random names, and then give them format
        while (s.contains("##")) {
            String name;
            String content;
            Gender gender = Gender.UNDEFINED;

            switch (r.getItem(personSources)) {
                case "contact":
                    name = getContactName();
                    content = formatContactName(name);
                    personSources.remove("contact");
                    break;
                case "random":
                    gender = Gender.values()[r.getInt(1, 3)];

                    if (r.getInt(4) == 0) {
                        name = getUsername();
                        content = formatUsername(name);
                    } else {
                        Person person = getPerson();
                        name = person.getFullName();
                        content = formatName(person);
                    }
                    break;
                case "suggestion":
                    name = getSuggestedName();
                    content = formatSuggestedName(name);
                    personSources.remove("suggestion");
                    break;
                default:
                    name = "?";
                    content = formatText("?", "b,tt");
                    break;
            }

            if (StringHelper.isNullOrEmpty(name))
                continue;
            s = replaceTags(s, gender).getText();
            s = StringHelper.replaceOnce(s, "##", content);
        }
        s = StringHelper.replace(s, " a el ", " al ");
        s = fixDoubleFullStop(s);

        //Add ideogram, emoticon or nothing
        float probability = r.getFloat();

        if (probability <= 0.25F) {
            extra = getStrFromSplitStrRes(R.string.emoticons);
            extra = "<b><font color=" + getColorStr() + ">" + extra + "</font></b>";
        } else if (probability <= 0.5F)
            extra = getEmojis(r.getInt(1, 4));
        else
            extra = "";
        s = s + StringHelper.prependSpaceIfNotEmpty(extra);
        return getString(R.string.html_format, s);
    }

    public int getRandomAppearance() {
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
        int randomRes = (int) (r.getDouble() * images.length());
        resId = images.getResourceId(randomRes, initialRes);
        images.recycle();
        return resId;
    }
}

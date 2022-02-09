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

public class FortuneTeller extends BaseWrapper {
    private final Randomizer r;
    private final ResourceFinder resourceFinder;
    private final TextFormatter textFormatter;

    public FortuneTeller(Context context) {
        super(context);
        r = new Randomizer();
        resourceFinder = new ResourceFinder(context);
        textFormatter = new TextFormatter(context);
        bindSeed();
    }

    public Long getSeed() {
        if (StringHelper.isNotNullOrBlank(defaultPreferences.getString("preference_seed")))
            return LongHelper.getSeed(defaultPreferences.getString("preference_seed"));
        return null;
    }

    private void bindSeed() {
        Long seed = getSeed();
        r.bindSeed(seed);

    }

    public String comment() {
        return resourceFinder.getEmojis(r.getInt(1, 4)) + " " + resourceFinder.getStrFromSplitStrRes(R.string.default_comments);
    }

    public String greet() {
        return greet(-1);
    }

    public String greet(int index) {
        String s;
        String addendum = textFormatter.replaceTags(resourceFinder.getStrFromStrArrayRes(R.array.conversation_addendum)).getText();

        if (index >= 0) {
            s = resourceFinder.getStrFromSplitStrRes(R.string.greetings, index);
            s = StringHelper.replace(s, "§", addendum);
        } else {
            String ideogramSet = resourceFinder.getEmojis(r.getInt(1, 4));

            switch (r.getInt(6)) {
                case 0:
                    s = greetShortly(addendum) + " " + resourceFinder.getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                case 1:
                    s = resourceFinder.getStrFromSplitStrRes(R.string.greetings_component) + " " + DateTimeHelper.getStrCurrentDayOfWeek() + StringHelper.prependIfNotEmpty(addendum, ", ") + ". " + StringHelper.appendSpaceIfNotEmpty(ideogramSet) + resourceFinder.getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                case 2:
                    s = resourceFinder.getStrFromSplitStrRes(R.string.date_component) + " " + DateTimeHelper.getStrCurrentDate(r.getInt(1, 11)) + StringHelper.prependIfNotEmpty(addendum, ", ") + ". " + StringHelper.appendSpaceIfNotEmpty(ideogramSet) + resourceFinder.getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                case 3:
                    s = resourceFinder.getStrFromSplitStrRes(R.string.time_component) + " " + DateTimeHelper.getStrCurrentTime(r.getInt(1, 8)) + StringHelper.prependIfNotEmpty(addendum, ", ") + ". " + StringHelper.appendSpaceIfNotEmpty(ideogramSet) + resourceFinder.getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                default:
                    s = StringHelper.replaceOnce(resourceFinder.getStrFromSplitStrRes(R.string.greetings), "§", StringHelper.prependIfNotEmpty(addendum, ", ")) + " " + StringHelper.appendSpaceIfNotEmpty(ideogramSet) + resourceFinder.getStrFromStrArrayRes(R.array.phrase_augury);
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
            return StringHelper.replace(resourceFinder.getStrFromSplitStrRes(R.string.greetings_day), "§", addendum);
        else if (hour >= 12 && hour < 19)
            return StringHelper.replace(resourceFinder.getStrFromSplitStrRes(R.string.greetings_afternoon), "§", addendum);
        else if (hour >= 19 && hour < 24)
            return StringHelper.replace(resourceFinder.getStrFromSplitStrRes(R.string.greetings_night), "§", addendum);
        else
            return StringHelper.replace(resourceFinder.getStrFromSplitStrRes(R.string.greetings_default), "§", addendum);
    }

    public String talk() {
        List<String> phraseTypes = new ArrayList<>();

        if (defaultPreferences.getBoolean("preference_greetingsEnabled", true)) {
            phraseTypes.add("greetings");
            phraseTypes.add("greetings");
        }

        if (defaultPreferences.getBoolean("preference_opinionsEnabled", true)) {
            phraseTypes.add("opinions");
            phraseTypes.add("opinions");
        }

        if (defaultPreferences.getBoolean("preference_phrasesEnabled", true)) {
            phraseTypes.add("phrases");
            phraseTypes.add("conversation");
        }

        if (phraseTypes.size() > 0) {
            int index = r.getInt(phraseTypes.size());

            switch (phraseTypes.get(index)) {
                case "greetings":
                    return greet();
                case "opinions":
                    return talkAboutSomeone();
                case "phrases":
                    return talkAboutSomething();
                case "conversation":
                    return talkAboutSomething(r.getInt(2, 3));
                default:
                    return "?";
            }
        }
        return comment();
    }

    public String talkAboutSomething() {
        return talkAboutSomething(1);
    }

    public String talkAboutSomething(int times) {
        String conversation = "";
        String ideogramSet = resourceFinder.getEmojis(r.getInt(1, 4));

        while (times > 0) {
            conversation = StringHelper.appendIfNotEmpty(conversation, " ") + resourceFinder.getPhrase();
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
            s = resourceFinder.getStrFromStrArrayRes(R.array.opinion, index);
        else
            s = resourceFinder.getStrFromStrArrayRes(R.array.opinion);

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
                    name = resourceFinder.getContactName();
                    content = textFormatter.formatContactName(name);
                    personSources.remove("contact");
                    break;
                case "random":
                    gender = Gender.values()[r.getInt(1, 3)];

                    if (r.getInt(4) == 0) {
                        name = resourceFinder.getUsername();
                        content = textFormatter.formatUsername(name);
                    } else {
                        Person person = resourceFinder.getPerson();
                        name = person.getFullName();
                        content = textFormatter.formatName(person);
                    }
                    break;
                case "suggestion":
                    name = resourceFinder.getSuggestedName();
                    content = textFormatter.formatSuggestedName(name);
                    personSources.remove("suggestion");
                    break;
                default:
                    name = "?";
                    content = textFormatter.formatText("?", "b,tt");
                    break;
            }

            if (StringHelper.isNullOrEmpty(name))
                continue;
            s = textFormatter.replaceTags(s, gender).getText();
            s = StringHelper.replaceOnce(s, "##", content);
        }
        s = StringHelper.replace(s, " a el ", " al ");
        s = textFormatter.fixDoubleFullStop(s);

        //Add ideogram, emoticon or nothing
        float probability = r.getFloat();

        if (probability <= 0.25F) {
            extra = resourceFinder.getStrFromSplitStrRes(R.string.emoticons);
            extra = "<b><font color=" + resourceFinder.getColorStr() + ">" + extra + "</font></b>";
        } else if (probability <= 0.5F)
            extra = resourceFinder.getEmojis(r.getInt(1, 4));
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

package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.res.TypedArray;

import com.memoeslink.common.Randomizer;
import com.memoeslink.generator.common.DateTimeGetter;
import com.memoeslink.generator.common.DateTimeHelper;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.LongHelper;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.common.TextFormatter;
import com.memoeslink.generator.common.TextProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FortuneTeller extends BaseWrapper {
    private final Randomizer r;
    private final ResourceExplorer resourceExplorer;
    private final TagProcessor tagProcessor;

    public FortuneTeller(Context context) {
        super(context);
        r = new Randomizer();
        resourceExplorer = new ResourceExplorer(context);
        tagProcessor = new TagProcessor(context);
        bindSeed();
    }

    public Long getSeed() {
        if (StringHelper.isNotNullOrBlank(defaultPreferences.getString(Preference.SETTING_SEED.getName())))
            return LongHelper.getSeed(defaultPreferences.getString(Preference.SETTING_SEED.getName()));
        return null;
    }

    private void bindSeed() {
        Long seed = getSeed();
        r.bindSeed(seed);
        resourceExplorer.bindSeed(seed);
        tagProcessor.bindSeed(seed);
    }

    public String greet() {
        return greet(-1);
    }

    public String greet(int index) {
        String s;
        String res = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.conversation_addendum);
        String addendum = tagProcessor.replaceTags(res).getText();

        if (index >= 0) {
            s = resourceExplorer.getResourceFinder().getStrFromSplitStrRes(R.string.greetings, index);
            s = StringHelper.replace(s, "§", addendum);
        } else {
            String ideogramSet = resourceExplorer.getEmojis(r.getInt(1, 4));

            switch (r.getInt(6)) {
                case 0:
                    s = greetShortly(addendum) + " " + resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                case 1:
                    s = resourceExplorer.getResourceFinder().getStrFromSplitStrRes(R.string.greetings_component) + " " + DateTimeGetter.with(LanguageHelper.getLocale(this)).getCurrentDayOfWeek() + StringHelper.prependIfNotEmpty(addendum, ", ") + ". " + StringHelper.appendSpaceIfNotEmpty(ideogramSet) + resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                case 2:
                    s = resourceExplorer.getResourceFinder().getStrFromSplitStrRes(R.string.date_component) + " " + DateTimeGetter.with(LanguageHelper.getLocale(this)).getCurrentDate(r.getInt(1, 14)) + StringHelper.prependIfNotEmpty(addendum, ", ") + ". " + StringHelper.appendSpaceIfNotEmpty(ideogramSet) + resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                case 3:
                    s = resourceExplorer.getResourceFinder().getStrFromSplitStrRes(R.string.time_component) + " " + DateTimeGetter.with(LanguageHelper.getLocale(this)).getCurrentTime(r.getInt(1, 11)) + StringHelper.prependIfNotEmpty(addendum, ", ") + ". " + StringHelper.appendSpaceIfNotEmpty(ideogramSet) + resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                default:
                    s = StringHelper.replaceOnce(resourceExplorer.getResourceFinder().getStrFromSplitStrRes(R.string.greetings), "§", StringHelper.prependIfNotEmpty(addendum, ", ")) + " " + StringHelper.appendSpaceIfNotEmpty(ideogramSet) + resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
            }
        }
        s = StringHelper.replace(s, "..", ".");
        return getString(R.string.html_format, s);
    }

    public String greetInProcess() {
        return "";
    }

    public String greetShortly() {
        return greetShortly("");
    }

    public String greetShortly(String addendum) {
        addendum = StringHelper.defaultIfBlank(addendum);
        addendum = StringHelper.prependIfNotEmpty(addendum, ", ");
        int hour = DateTimeHelper.getCurrentTime().getHour();

        if (hour >= 0 && hour < 12)
            return StringHelper.replace(resourceExplorer.getResourceFinder().getStrFromSplitStrRes(R.string.greetings_day), "§", addendum);
        else if (hour >= 12 && hour < 19)
            return StringHelper.replace(resourceExplorer.getResourceFinder().getStrFromSplitStrRes(R.string.greetings_afternoon), "§", addendum);
        else if (hour >= 19 && hour < 24)
            return StringHelper.replace(resourceExplorer.getResourceFinder().getStrFromSplitStrRes(R.string.greetings_night), "§", addendum);
        else
            return StringHelper.replace(resourceExplorer.getResourceFinder().getStrFromSplitStrRes(R.string.greetings_default), "§", addendum);
    }

    public String talk() {
        List<String> phraseTypes = new ArrayList<>();

        if (defaultPreferences.getBoolean(Preference.SETTING_GREETINGS_ENABLED.getName())) {
            phraseTypes.add("greetings");
            phraseTypes.add("greetings");
        }

        if (defaultPreferences.getBoolean(Preference.SETTING_OPINIONS_ENABLED.getName())) {
            phraseTypes.add("opinions");
            phraseTypes.add("opinions");
        }

        if (defaultPreferences.getBoolean(Preference.SETTING_PHRASES_ENABLED.getName())) {
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
            }
        }
        return "…";
    }

    public String talkAboutSomething() {
        return talkAboutSomething(1);
    }

    public String talkAboutSomething(int times) {
        String conversation = "";
        String ideogramSet = resourceExplorer.getEmojis(r.getInt(1, 4));

        while (times > 0) {
            conversation = StringHelper.appendIfNotEmpty(conversation, " ") + resourceExplorer.getDatabaseFinder().getPhrase();
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
            s = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.opinion, index);
        else
            s = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.opinion);

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
                    name = resourceExplorer.getContactNameFinder().getContactName();
                    content = TextFormatter.formatContactName(name);
                    personSources.remove("contact");
                    break;
                case "random":
                    gender = Gender.values()[r.getInt(1, 3)];

                    if (r.getInt(4) == 0) {
                        name = resourceExplorer.getReflectionFinder().getUsername();
                        content = TextFormatter.formatUsername(name);
                    } else {
                        Person person = resourceExplorer.getReflectionFinder().getPerson();
                        name = person.getFullName();
                        content = TextFormatter.formatName(person);
                    }
                    break;
                case "suggestion":
                    name = resourceExplorer.getPreferenceFinder().getSuggestedName();
                    content = TextFormatter.formatSuggestedName(name);
                    personSources.remove("suggestion");
                    break;
                default:
                    name = "?";
                    content = TextFormatter.formatText("?", "b,tt");
                    break;
            }

            if (StringHelper.isNullOrEmpty(name))
                continue;
            s = tagProcessor.replaceTags(s, gender, false).getText();
            s = StringHelper.replaceOnce(s, "##", content);
        }
        s = StringHelper.replace(s, " a el ", " al ");
        s = TextProcessor.removeDoubleFullStop(s);

        //Add ideogram, emoticon or nothing
        float probability = r.getFloat();

        if (probability <= 0.25F) {
            extra = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.emoticons);
            extra = "<b><font color=" + resourceExplorer.getReflectionFinder().getColorStr() + ">" + extra + "</font></b>";
        } else if (probability <= 0.5F)
            extra = resourceExplorer.getEmojis(r.getInt(1, 4));
        else
            extra = "";
        s = s + StringHelper.prependSpaceIfNotEmpty(extra);
        return getString(R.string.html_format, s);
    }

    public int getRandomAppearance() {
        int resId;
        int arrayId;
        int initialRes;

        switch (defaultPreferences.getStringAsInt(Preference.SETTING_FORTUNE_TELLER_ASPECT.getName(), 1)) {
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

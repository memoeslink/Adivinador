package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.res.TypedArray;

import com.memoeslink.common.Randomizer;
import com.memoeslink.generator.common.DateTimeGetter;
import com.memoeslink.generator.common.DateTimeHelper;
import com.memoeslink.generator.common.LongHelper;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.common.TextProcessor;

import java.util.ArrayList;
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
        if (StringHelper.isNotNullOrBlank(defaultPreferences.getString(Preference.SETTING_SEED.getTag())))
            return LongHelper.getSeed(defaultPreferences.getString(Preference.SETTING_SEED.getTag()));
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
            String pictogram = resourceExplorer.getEmojis(r.getInt(1, 4));

            switch (r.getInt(6)) {
                case 0:
                    s = greetShortly(addendum) + " " + resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                case 1:
                    s = resourceExplorer.getResourceFinder().getStrFromSplitStrRes(R.string.greetings_component) + " " + DateTimeGetter.with(LanguageHelper.getLocale(this)).getCurrentDayOfWeek() + StringHelper.prependIfNotEmpty(addendum, ", ") + ". " + StringHelper.appendSpaceIfNotEmpty(pictogram) + resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                case 2:
                    s = resourceExplorer.getResourceFinder().getStrFromSplitStrRes(R.string.date_component) + " " + DateTimeGetter.with(LanguageHelper.getLocale(this)).getCurrentDate(r.getInt(1, 14)) + StringHelper.prependIfNotEmpty(addendum, ", ") + ". " + StringHelper.appendSpaceIfNotEmpty(pictogram) + resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                case 3:
                    s = resourceExplorer.getResourceFinder().getStrFromSplitStrRes(R.string.time_component) + " " + DateTimeGetter.with(LanguageHelper.getLocale(this)).getCurrentTime(r.getInt(1, 11)) + StringHelper.prependIfNotEmpty(addendum, ", ") + ". " + StringHelper.appendSpaceIfNotEmpty(pictogram) + resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.phrase_augury);
                    break;
                default:
                    s = StringHelper.replaceOnce(resourceExplorer.getResourceFinder().getStrFromSplitStrRes(R.string.greetings), "§", StringHelper.prependIfNotEmpty(addendum, ", ")) + " " + StringHelper.appendSpaceIfNotEmpty(pictogram) + resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.phrase_augury);
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

        if (defaultPreferences.getBoolean(Preference.SETTING_GREETINGS_ENABLED.getTag()))
            phraseTypes.add("greetings");

        if (defaultPreferences.getBoolean(Preference.SETTING_OPINIONS_ENABLED.getTag()))
            phraseTypes.add("opinions");

        if (defaultPreferences.getBoolean(Preference.SETTING_PHRASES_ENABLED.getTag()))
            phraseTypes.add("phrases");

        if (phraseTypes.size() > 0) {
            switch (r.getItem(phraseTypes)) {
                case "greetings":
                    return greet();
                case "opinions":
                    return talkAboutSomeone();
                case "phrases":
                    return talkAboutSomething();
            }
        }
        return "…";
    }

    public String talkAboutSomething() {
        String pictogram = r.getBoolean() ? resourceExplorer.getPictogram() : "";
        String phrase = resourceExplorer.getDatabaseFinder().getPhrase();
        return StringHelper.trimToEmpty(phrase) + StringHelper.prependSpaceIfNotEmpty(pictogram);
    }

    public String talkAboutSomeone() {
        String s = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.opinion);
        s = tagProcessor.replaceTags(s).getText();
        s = StringHelper.replace(s, " a el ", " al ");
        s = TextProcessor.removeDoubleFullStop(s);

        String pictogram = r.getBoolean() ? resourceExplorer.getPictogram() : "";
        s = s + StringHelper.prependSpaceIfNotEmpty(pictogram);
        return getString(R.string.html_format, s);
    }

    public int getRandomAppearance() {
        int resId;
        int arrayId;
        int initialRes;

        switch (defaultPreferences.getStringAsInt(Preference.SETTING_FORTUNE_TELLER_ASPECT.getTag(), 1)) {
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

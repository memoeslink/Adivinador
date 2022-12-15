package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;

import com.app.memoeslink.adivinador.tagprocessor.TagProcessor;
import com.memoeslink.common.Randomizer;
import com.memoeslink.generator.common.LongHelper;
import com.memoeslink.generator.common.ResourceReference;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.common.TextProcessor;

import java.util.ArrayList;
import java.util.List;

public class FortuneTeller extends ContextWrapper {
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
        if (StringHelper.isNotNullOrBlank(PreferenceHandler.getString(Preference.SETTING_SEED)))
            return LongHelper.getSeed(PreferenceHandler.getString(Preference.SETTING_SEED));
        return null;
    }

    private void bindSeed() {
        Long seed = getSeed();
        r.bindSeed(seed);
        resourceExplorer.bindSeed(seed);
        tagProcessor.bindSeed(seed);
    }

    public String greet() {
        String s;
        String greeting = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.conversation_greeting);
        String auguryPhrase = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.phrase_augury);
        String pictogram = r.getBoolean() ? resourceExplorer.findByRef(ResourceReference.FORMATTED_PICTOGRAM) : "";
        s = tagProcessor.replaceTags(greeting, null, r.getBoolean()).getText() + StringHelper.prependSpaceIfNotEmpty(auguryPhrase) + StringHelper.prependIfNotEmpty(pictogram, "<br>");
        s = StringHelper.replace(s, "..", ".");
        return getString(R.string.html_format, s);
    }

    public String talk() {
        List<String> phraseTypes = new ArrayList<>();

        if (PreferenceHandler.getBoolean(Preference.SETTING_GREETINGS_ENABLED))
            phraseTypes.add("greetings");

        if (PreferenceHandler.getBoolean(Preference.SETTING_OPINIONS_ENABLED))
            phraseTypes.add("opinions");

        if (PreferenceHandler.getBoolean(Preference.SETTING_PHRASES_ENABLED))
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
        String pictogram = r.getBoolean() ? resourceExplorer.findByRef(ResourceReference.FORMATTED_PICTOGRAM) : "";
        String phrase = resourceExplorer.getDatabaseFinder().getPhrase();
        return StringHelper.trimToEmpty(phrase) + StringHelper.prependIfNotEmpty(pictogram, "<br>");
    }

    public String talkAboutSomeone() {
        String s = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.opinion);
        s = tagProcessor.replaceTags(s).getText();
        s = TextProcessor.removeDoubleFullStop(s);

        String pictogram = r.getBoolean() ? resourceExplorer.findByRef(ResourceReference.FORMATTED_PICTOGRAM) : "";
        s = s + StringHelper.prependIfNotEmpty(pictogram, "<br>");
        return getString(R.string.html_format, s);
    }

    public int getRandomAppearance() {
        int resId;
        int arrayId;
        int initialRes;

        switch (PreferenceHandler.getStringAsInt(Preference.SETTING_FORTUNE_TELLER_ASPECT, 1)) {
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

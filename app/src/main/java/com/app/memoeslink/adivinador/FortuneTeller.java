package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;

import com.app.memoeslink.adivinador.preference.Preference;
import com.app.memoeslink.adivinador.preference.PreferenceHandler;
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
        Long seed = getSeed();
        r = new Randomizer(seed);
        resourceExplorer = new ResourceExplorer(context, seed);
        tagProcessor = new TagProcessor(context, seed);
    }

    public Long getSeed() {
        if (StringHelper.isNotNullOrBlank(PreferenceHandler.getString(Preference.SETTING_SEED)))
            return LongHelper.getSeed(PreferenceHandler.getString(Preference.SETTING_SEED));
        return null;
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
            return switch (r.getElement(phraseTypes)) {
                case "greetings" -> greet();
                case "opinions" -> talkAboutSomeone();
                case "phrases" -> talkAboutSomething();
                default -> "…";
            };
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
            case 1 -> {
                arrayId = R.array.emoji_collection;
                initialRes = R.drawable.ic_emoji_angel;
            }
            case 2 -> {
                arrayId = R.array.pixel_collection;
                initialRes = R.drawable.ic_pixel_afraid;
            }
            case 3 -> {
                arrayId = R.array.smiley_collection;
                initialRes = R.drawable.ic_smiley_3d_glasses;
            }
            default -> {
                arrayId = R.array.crystal_ball;
                initialRes = R.drawable.ic_crystal_ball;
            }
        }
        TypedArray images = getResources().obtainTypedArray(arrayId);
        int randomRes = (int) (r.getDouble() * images.length());
        resId = images.getResourceId(randomRes, initialRes);
        images.recycle();
        return resId;
    }
}

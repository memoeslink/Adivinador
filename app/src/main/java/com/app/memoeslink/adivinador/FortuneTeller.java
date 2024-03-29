package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.ContextWrapper;
import android.content.res.TypedArray;

import com.app.memoeslink.adivinador.dialoguecreator.DialogueCreator;
import com.app.memoeslink.adivinador.dialoguecreator.DialogueCreatorFactory;
import com.app.memoeslink.adivinador.preference.Preference;
import com.app.memoeslink.adivinador.preference.PreferenceHandler;
import com.memoeslink.common.Randomizer;
import com.memoeslink.generator.common.Constant;
import com.memoeslink.generator.common.ResourceGetter;
import com.memoeslink.generator.common.ResourceReference;
import com.memoeslink.generator.common.TextFormatter;

import org.memoeslink.LongHelper;
import org.memoeslink.StringHelper;

import java.util.ArrayList;
import java.util.List;

public class FortuneTeller extends ContextWrapper {
    private final Randomizer r;
    private final ResourceExplorer explorer;
    private final DialogueCreator aiDialogueCreator;
    private final DialogueCreator legacyDialogueCreator;

    public FortuneTeller(Context context) {
        super(context);
        Long seed = getSeed();
        r = new Randomizer(seed);
        explorer = new ResourceExplorer(context, seed);
        aiDialogueCreator = DialogueCreatorFactory.getDialogueCreator("AI", context, getSeed());
        legacyDialogueCreator = DialogueCreatorFactory.getDialogueCreator("legacy", context, getSeed());
    }

    public Long getSeed() {
        if (StringHelper.isNotNullOrBlank(PreferenceHandler.getString(Preference.SETTING_SEED)))
            return LongHelper.getSeed(PreferenceHandler.getString(Preference.SETTING_SEED));
        return null;
    }

    public String talk() {
        List<String> phraseTypes = new ArrayList<>();

        if (PreferenceHandler.getBoolean(Preference.SETTING_GREETINGS_ENABLED))
            phraseTypes.add("greeting");

        if (PreferenceHandler.getBoolean(Preference.SETTING_OPINIONS_ENABLED))
            phraseTypes.add("opinion");

        if (PreferenceHandler.getBoolean(Preference.SETTING_PHRASES_ENABLED)) {
            phraseTypes.add("phrase");
            phraseTypes.add("dialogue");
        }
        return talk(r.getElement(phraseTypes));
    }

    public String talk(String dialogueType) {
        dialogueType = StringHelper.defaultIfNull(dialogueType);
        DialogueCreator dialogueCreator = r.getBoolean() ? aiDialogueCreator : legacyDialogueCreator;

        String dialogue = switch (dialogueType) {
            case "greeting" -> dialogueCreator.greet();
            case "opinion" -> dialogueCreator.talkAboutSomeone();
            case "phrase" -> dialogueCreator.talkAboutSomething();
            case "dialogue" -> dialogueCreator.chat();
            default -> "…";
        };
        dialogue = StringHelper.trimOrDefault(dialogue, "…");

        if (r.getBoolean()) {
            String pictogram = explorer.getResourceFinder().getResByRefId(ResourceReference.REACTION);
            pictogram = String.format("<font color=\"%s\">%s</font>", ResourceGetter.with(r).getString(Constant.DEFAULT_COLORS), TextFormatter.formatText(pictogram, "b"));
            dialogue = dialogue + StringHelper.prependIfNotEmpty(pictogram, "<br>");
            dialogue = getString(R.string.html_format, dialogue);
        }
        return dialogue;
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

package com.app.memoeslink.adivinador.dialoguecreator;

import android.content.Context;
import android.content.ContextWrapper;

import com.app.memoeslink.adivinador.R;
import com.app.memoeslink.adivinador.ResourceExplorer;
import com.app.memoeslink.adivinador.tagprocessor.TagProcessor;
import com.memoeslink.common.Randomizer;
import com.memoeslink.generator.common.ResourceReference;
import com.memoeslink.generator.common.TextProcessor;

import org.memoeslink.StringHelper;

public class LegacyDialogueCreator extends ContextWrapper implements DialogueCreator {
    private final Randomizer r;
    private final ResourceExplorer resourceExplorer;
    private final TagProcessor tagProcessor;

    public LegacyDialogueCreator(Context context, Long seed) {
        super(context);
        r = new Randomizer(seed);
        resourceExplorer = new ResourceExplorer(context, seed);
        tagProcessor = new TagProcessor(context, seed);
    }

    @Override
    public String greet() {
        String dialogue = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.conversation_greeting);
        String pictogram = r.getBoolean() ? resourceExplorer.findByRef(ResourceReference.FORMATTED_PICTOGRAM) : "";
        String s = tagProcessor.replaceTags(dialogue).getText() + StringHelper.prependIfNotEmpty(pictogram, "<br>");
        s = StringHelper.replace(s, "..", ".");
        return getString(R.string.html_format, s);
    }

    @Override
    public String talkAboutSomeone() {
        String dialogue = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.opinion);
        dialogue = tagProcessor.replaceTags(dialogue).getText();
        dialogue = TextProcessor.removeDoubleFullStop(dialogue);
        String pictogram = r.getBoolean() ? resourceExplorer.findByRef(ResourceReference.FORMATTED_PICTOGRAM) : "";
        dialogue = dialogue + StringHelper.prependIfNotEmpty(pictogram, "<br>");
        return getString(R.string.html_format, dialogue);
    }

    @Override
    public String talkAboutSomething() {
        String dialogue = resourceExplorer.getDatabaseFinder().getLegacyPhrase();
        String pictogram = r.getBoolean() ? resourceExplorer.findByRef(ResourceReference.FORMATTED_PICTOGRAM) : "";
        return StringHelper.trimToEmpty(dialogue) + StringHelper.prependIfNotEmpty(pictogram, "<br>");
    }

    @Override
    public String recite() {
        String dialogue = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.phrase_augury);
        String pictogram = r.getBoolean() ? resourceExplorer.findByRef(ResourceReference.FORMATTED_PICTOGRAM) : "";
        return StringHelper.trimToEmpty(dialogue) + StringHelper.prependIfNotEmpty(pictogram, "<br>");
    }
}

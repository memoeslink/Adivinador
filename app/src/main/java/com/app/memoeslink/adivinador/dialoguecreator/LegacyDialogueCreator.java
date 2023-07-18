package com.app.memoeslink.adivinador.dialoguecreator;

import android.content.Context;
import android.content.ContextWrapper;

import com.app.memoeslink.adivinador.R;
import com.app.memoeslink.adivinador.ResourceExplorer;
import com.app.memoeslink.adivinador.tagprocessor.TagProcessor;
import com.memoeslink.common.Randomizer;
import com.memoeslink.generator.common.TextProcessor;

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
        dialogue = tagProcessor.replaceTags(dialogue).getText();
        return TextProcessor.removeDoubleFullStop(dialogue);
    }

    @Override
    public String talkAboutSomeone() {
        String dialogue = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.opinion);
        dialogue = tagProcessor.replaceTags(dialogue).getText();
        return TextProcessor.removeDoubleFullStop(dialogue);
    }

    @Override
    public String talkAboutSomething() {
        return resourceExplorer.getDatabaseFinder().getLegacyPhrase();
    }

    @Override
    public String recite() {
        return resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.phrase_augury);
    }
}

package com.app.memoeslink.adivinador.dialoguecreator;

import android.content.Context;
import android.content.ContextWrapper;

import com.app.memoeslink.adivinador.MethodReference;
import com.app.memoeslink.adivinador.R;
import com.app.memoeslink.adivinador.ResourceExplorer;
import com.app.memoeslink.adivinador.preference.PreferenceUtils;
import com.app.memoeslink.adivinador.tagprocessor.Actor;
import com.app.memoeslink.adivinador.tagprocessor.TagProcessor;
import com.memoeslink.generator.common.GrammaticalNumber;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.TextProcessor;

public class LegacyDialogueCreator extends ContextWrapper implements DialogueCreator {
    private final ResourceExplorer explorer;
    private final TagProcessor tagProcessor;

    public LegacyDialogueCreator(Context context, Long seed) {
        super(context);
        explorer = new ResourceExplorer(context, seed);
        Person user = PreferenceUtils.getFormPerson();
        tagProcessor = new TagProcessor.NewTagProcessorBuilder(context)
                .setDefaultActor(new Actor(user.getDescriptor(), user.getGender(), GrammaticalNumber.SINGULAR))
                .setActor("actor1", new Actor(explorer.findMethodByRef(MethodReference.FORMATTED_INDIVIDUAL)))
                .setSeed(seed)
                .build();
    }

    @Override
    public String greet() {
        String dialogue = explorer.getResourceFinder().getStrFromStrArrayRes(R.array.conversation_greeting);
        dialogue = tagProcessor.replaceTags(dialogue).getText();
        return TextProcessor.removeDoubleFullStop(dialogue);
    }

    @Override
    public String talkAboutSomeone() {
        String dialogue = explorer.getResourceFinder().getStrFromStrArrayRes(R.array.opinion);
        dialogue = tagProcessor.replaceTags(dialogue).getText();
        return TextProcessor.removeDoubleFullStop(dialogue);
    }

    @Override
    public String talkAboutSomething() {
        return explorer.getDatabaseFinder().getLegacyPhrase();
    }

    @Override
    public String chat() {
        return explorer.getResourceFinder().getStrFromStrArrayRes(R.array.phrase_augury);
    }
}

package com.app.memoeslink.adivinador.dialoguecreator;

import android.content.Context;
import android.content.ContextWrapper;

import com.app.memoeslink.adivinador.R;
import com.app.memoeslink.adivinador.ResourceExplorer;

public class AiDialogueCreator extends ContextWrapper implements DialogueCreator {
    private final ResourceExplorer explorer;

    public AiDialogueCreator(Context context, Long seed) {
        super(context);
        explorer = new ResourceExplorer(context, seed);
    }

    @Override
    public String greet() {
        return explorer.getResourceFinder().getStrFromStrArrayRes(R.array.salutation);
    }

    @Override
    public String talkAboutSomeone() {
        return explorer.getDatabaseFinder().getOpinion();
    }

    @Override
    public String talkAboutSomething() {
        return explorer.getDatabaseFinder().getPhrase();
    }

    @Override
    public String chat() {
        return explorer.getDatabaseFinder().getDialogue();
    }
}

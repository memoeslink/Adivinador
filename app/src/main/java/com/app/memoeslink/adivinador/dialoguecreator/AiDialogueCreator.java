package com.app.memoeslink.adivinador.dialoguecreator;

import android.content.Context;
import android.content.ContextWrapper;

import com.app.memoeslink.adivinador.Database;
import com.app.memoeslink.adivinador.R;
import com.app.memoeslink.adivinador.ResourceExplorer;

public class AiDialogueCreator extends ContextWrapper implements DialogueCreator {
    private final ResourceExplorer resourceExplorer;

    public AiDialogueCreator(Context context, Long seed) {
        super(context);
        resourceExplorer = new ResourceExplorer(context, seed);
    }

    @Override
    public String greet() {
        return resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.salutation);
    }

    @Override
    public String talkAboutSomeone() {
        return Database.DEFAULT_VALUE;
    }

    @Override
    public String talkAboutSomething() {
        return resourceExplorer.getDatabaseFinder().getPhrase();
    }

    @Override
    public String recite() {
        return resourceExplorer.getDatabaseFinder().getRecitation();
    }
}

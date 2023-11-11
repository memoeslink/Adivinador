package com.app.memoeslink.adivinador.dialoguecreator;

import com.app.memoeslink.adivinador.Database;

public class DefaultDialogueCreator implements DialogueCreator {

    @Override
    public String greet() {
        return Database.DEFAULT_VALUE;
    }

    @Override
    public String talkAboutSomeone() {
        return Database.DEFAULT_VALUE;
    }

    @Override
    public String talkAboutSomething() {
        return Database.DEFAULT_VALUE;
    }

    @Override
    public String chat() {
        return Database.DEFAULT_VALUE;
    }
}

package com.app.memoeslink.adivinador.dialoguecreator;

import android.content.Context;

public class DialogueCreatorFactory {

    public DialogueCreator getDialogueCreator(String type, Context context, Long seed) {
        if (type == null) return new DefaultDialogueCreator();

        return switch (type) {
            case "AI" -> new AiDialogueCreator(context, seed);
            case "legacy" -> new LegacyDialogueCreator(context, seed);
            default -> new DefaultDialogueCreator();
        };
    }
}

package com.app.memoeslink.adivinador.tagprocessor;

import android.content.Context;
import android.content.ContextWrapper;

import com.app.memoeslink.adivinador.R;

public class GrammarTagProcessorFactory extends ContextWrapper {

    public GrammarTagProcessorFactory(Context context) {
        super(context);
    }

    public GrammarTagProcessor getGrammarTagProcessor() {
        return switch (getString(R.string.locale)) {
            case "en" -> new EnglishGrammarTagProcessor();
            case "es" -> new SpanishGrammarTagProcessor();
            default -> new DefaultGrammarTagProcessor();
        };
    }
}

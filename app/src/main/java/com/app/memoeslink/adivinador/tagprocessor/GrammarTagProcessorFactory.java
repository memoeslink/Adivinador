package com.app.memoeslink.adivinador.tagprocessor;

import android.content.Context;
import android.content.ContextWrapper;

import com.app.memoeslink.adivinador.R;

public class GrammarTagProcessorFactory extends ContextWrapper {

    public GrammarTagProcessorFactory(Context context) {
        super(context);
    }

    public GrammarTagProcessor createGrammarTagProcessor() {
        switch (getString(R.string.locale)) {
            case "en":
                return new EnglishGrammarTagProcessor();
            case "es":
                return new SpanishGrammarTagProcessor();
            default:
                return new DefaultGrammarTagProcessor();
        }
    }
}

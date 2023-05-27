package com.app.memoeslink.adivinador.textfilter;

import android.content.Context;
import android.content.ContextWrapper;

import com.app.memoeslink.adivinador.R;

public class TextFilterFactory extends ContextWrapper {

    public TextFilterFactory(Context context) {
        super(context);
    }

    public TextFilter createTextFilter() {
        return switch (getString(R.string.locale)) {
            case "en" -> new EnglishTextFilter();
            case "es" -> new SpanishTextFilter();
            default -> new DefaultTextFilter();
        };
    }
}

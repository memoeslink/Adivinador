package com.app.memoeslink.adivinador.finder;

import android.content.Context;

import com.app.memoeslink.adivinador.Database;
import com.app.memoeslink.adivinador.R;
import com.memoeslink.generator.common.Binder;
import com.memoeslink.generator.common.finder.ResourceFinder;

public class DatabaseFinder extends Binder {

    public DatabaseFinder(Context context) {
        this(context, null);
    }

    public DatabaseFinder(Context context, Long seed) {
        super(context, seed);
    }

    public String getAbstractNoun() {
        switch (getString(R.string.locale)) {
            case "en":
                return Database.getInstance(context).selectEnglishAbstractNoun(r.getInt(1, Database.getInstance(context).countEnglishAbstractNouns()));
            case "es":
                return Database.getInstance(context).selectSpanishAbstractNoun(r.getInt(1, Database.getInstance(context).countSpanishAbstractNouns()));
            default:
                return ResourceFinder.RESOURCE_NOT_FOUND;
        }
    }

    public String getDivination() {
        switch (getString(R.string.locale)) {
            case "en":
                return Database.getInstance(context).selectEnglishPrediction(r.getInt(1, Database.getInstance(context).countEnglishPredictions()));
            case "es":
                return Database.getInstance(context).selectSpanishPrediction(r.getInt(1, Database.getInstance(context).countSpanishPredictions()));
            default:
                return ResourceFinder.RESOURCE_NOT_FOUND;
        }
    }

    public String getFortuneCookie() {
        switch (getString(R.string.locale)) {
            case "en":
                return Database.getInstance(context).selectEnglishFortuneCookie(r.getInt(1, Database.getInstance(context).countEnglishFortuneCookies()));
            case "es":
                return Database.getInstance(context).selectSpanishFortuneCookie(r.getInt(1, Database.getInstance(context).countSpanishFortuneCookies()));
            default:
                return ResourceFinder.RESOURCE_NOT_FOUND;
        }
    }

    public String getPhrase() {
        switch (getString(R.string.locale)) {
            case "en":
                return Database.getInstance(context).selectEnglishPhrase(r.getInt(1, Database.getInstance(context).countEnglishPhrases()));
            case "es":
                return Database.getInstance(context).selectSpanishPhrase(r.getInt(1, Database.getInstance(context).countSpanishPhrases()));
            default:
                return ResourceFinder.RESOURCE_NOT_FOUND;
        }
    }
}

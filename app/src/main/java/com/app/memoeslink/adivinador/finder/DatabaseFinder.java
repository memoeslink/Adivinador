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
                return Database.getInstance(this).selectEnglishAbstractNoun(r.getInt(1, Database.getInstance(this).countEnglishAbstractNouns()));
            case "es":
                return Database.getInstance(this).selectSpanishAbstractNoun(r.getInt(1, Database.getInstance(this).countSpanishAbstractNouns()));
            default:
                return ResourceFinder.RESOURCE_NOT_FOUND;
        }
    }

    public String getAction() {
        switch (getString(R.string.locale)) {
            case "en":
                return Database.getInstance(this).selectEnglishAction(r.getInt(1, Database.getInstance(this).countEnglishActions()));
            case "es":
                return Database.getInstance(this).selectSpanishAction(r.getInt(1, Database.getInstance(this).countSpanishActions()));
            default:
                return ResourceFinder.RESOURCE_NOT_FOUND;
        }
    }

    public String getDivination() {
        switch (getString(R.string.locale)) {
            case "en":
                return Database.getInstance(this).selectEnglishPrediction(r.getInt(1, Database.getInstance(this).countEnglishPredictions()));
            case "es":
                return Database.getInstance(this).selectSpanishPrediction(r.getInt(1, Database.getInstance(this).countSpanishPredictions()));
            default:
                return ResourceFinder.RESOURCE_NOT_FOUND;
        }
    }

    public String getFortuneCookie() {
        switch (getString(R.string.locale)) {
            case "en":
                return Database.getInstance(this).selectEnglishFortuneCookie(r.getInt(1, Database.getInstance(this).countEnglishFortuneCookies()));
            case "es":
                return Database.getInstance(this).selectSpanishFortuneCookie(r.getInt(1, Database.getInstance(this).countSpanishFortuneCookies()));
            default:
                return ResourceFinder.RESOURCE_NOT_FOUND;
        }
    }

    public String getPhrase() {
        switch (getString(R.string.locale)) {
            case "en":
                return Database.getInstance(this).selectEnglishPhrase(r.getInt(1, Database.getInstance(this).countEnglishPhrases()));
            case "es":
                return Database.getInstance(this).selectSpanishPhrase(r.getInt(1, Database.getInstance(this).countSpanishPhrases()));
            default:
                return ResourceFinder.RESOURCE_NOT_FOUND;
        }
    }
}

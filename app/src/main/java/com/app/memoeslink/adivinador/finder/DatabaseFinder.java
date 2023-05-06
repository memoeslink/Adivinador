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
        return switch (getString(R.string.locale)) {
            case "en" ->
                    Database.getInstance(context).selectEnglishAbstractNoun(r.getInt(1, Database.getInstance(context).countEnglishAbstractNouns()));
            case "es" ->
                    Database.getInstance(context).selectSpanishAbstractNoun(r.getInt(1, Database.getInstance(context).countSpanishAbstractNouns()));
            default -> ResourceFinder.RESOURCE_NOT_FOUND;
        };
    }

    public String getDivination() {
        return switch (getString(R.string.locale)) {
            case "en" ->
                    Database.getInstance(context).selectEnglishPrediction(r.getInt(1, Database.getInstance(context).countEnglishPredictions()));
            case "es" ->
                    Database.getInstance(context).selectSpanishPrediction(r.getInt(1, Database.getInstance(context).countSpanishPredictions()));
            default -> ResourceFinder.RESOURCE_NOT_FOUND;
        };
    }

    public String getFortuneCookie() {
        return switch (getString(R.string.locale)) {
            case "en" ->
                    Database.getInstance(context).selectEnglishFortuneCookie(r.getInt(1, Database.getInstance(context).countEnglishFortuneCookies()));
            case "es" ->
                    Database.getInstance(context).selectSpanishFortuneCookie(r.getInt(1, Database.getInstance(context).countSpanishFortuneCookies()));
            default -> ResourceFinder.RESOURCE_NOT_FOUND;
        };
    }

    public String getPhrase() {
        return switch (getString(R.string.locale)) {
            case "en" ->
                    Database.getInstance(context).selectEnglishPhrase(r.getInt(1, Database.getInstance(context).countEnglishPhrases()));
            case "es" ->
                    Database.getInstance(context).selectSpanishPhrase(r.getInt(1, Database.getInstance(context).countSpanishPhrases()));
            default -> ResourceFinder.RESOURCE_NOT_FOUND;
        };
    }
}

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

    public String getDialogue() {
        return switch (getString(R.string.locale)) {
            case "en" ->
                    Database.getInstance(context).selectEnglishDialogue(r.getIntInRange(1, Database.getInstance(context).countEnglishDialogues()));
            case "es" ->
                    Database.getInstance(context).selectSpanishDialogues(r.getIntInRange(1, Database.getInstance(context).countSpanishDialogues()));
            default -> ResourceFinder.RESOURCE_NOT_FOUND;
        };
    }

    public String getFortuneCookie() {
        return switch (getString(R.string.locale)) {
            case "en" ->
                    Database.getInstance(context).selectEnglishFortuneCookie(r.getIntInRange(1, Database.getInstance(context).countEnglishFortuneCookies()));
            case "es" ->
                    Database.getInstance(context).selectSpanishFortuneCookie(r.getIntInRange(1, Database.getInstance(context).countSpanishFortuneCookies()));
            default -> ResourceFinder.RESOURCE_NOT_FOUND;
        };
    }

    public String getLegacyFortuneCookie() {
        return switch (getString(R.string.locale)) {
            case "en" ->
                    Database.getInstance(context).selectEnglishLegacyFortuneCookie(r.getIntInRange(1, Database.getInstance(context).countEnglishLegacyFortuneCookies()));
            case "es" ->
                    Database.getInstance(context).selectSpanishLegacyFortuneCookie(r.getIntInRange(1, Database.getInstance(context).countSpanishLegacyFortuneCookies()));
            default -> ResourceFinder.RESOURCE_NOT_FOUND;
        };
    }

    public String getLegacyPrediction() {
        return switch (getString(R.string.locale)) {
            case "en" ->
                    Database.getInstance(context).selectEnglishLegacyPredictions(r.getIntInRange(1, Database.getInstance(context).countEnglishLegacyPredictions()));
            case "es" ->
                    Database.getInstance(context).selectSpanishLegacyPredictions(r.getIntInRange(1, Database.getInstance(context).countSpanishLegacyPredictions()));
            default -> ResourceFinder.RESOURCE_NOT_FOUND;
        };
    }

    public String getLegacyPhrase() {
        return switch (getString(R.string.locale)) {
            case "en" ->
                    Database.getInstance(context).selectEnglishLegacyPhrases(r.getIntInRange(1, Database.getInstance(context).countEnglishLegacyPhrases()));
            case "es" ->
                    Database.getInstance(context).selectSpanishLegacyPhrases(r.getIntInRange(1, Database.getInstance(context).countSpanishLegacyPhrases()));
            default -> ResourceFinder.RESOURCE_NOT_FOUND;
        };
    }

    public String getOpinion() {
        return switch (getString(R.string.locale)) {
            case "en" ->
                    Database.getInstance(context).selectEnglishOpinion(r.getIntInRange(1, Database.getInstance(context).countEnglishOpinions()));
            case "es" ->
                    Database.getInstance(context).selectSpanishOpinion(r.getIntInRange(1, Database.getInstance(context).countSpanishOpinions()));
            default -> ResourceFinder.RESOURCE_NOT_FOUND;
        };
    }

    public String getPhrase() {
        return switch (getString(R.string.locale)) {
            case "en" ->
                    Database.getInstance(context).selectEnglishPhrase(r.getIntInRange(1, Database.getInstance(context).countEnglishPhrases()));
            case "es" ->
                    Database.getInstance(context).selectSpanishPhrase(r.getIntInRange(1, Database.getInstance(context).countSpanishPhrases()));
            default -> ResourceFinder.RESOURCE_NOT_FOUND;
        };
    }

    public String getPrediction() {
        return switch (getString(R.string.locale)) {
            case "en" ->
                    Database.getInstance(context).selectEnglishPrediction(r.getIntInRange(1, Database.getInstance(context).countEnglishPredictions()));
            case "es" ->
                    Database.getInstance(context).selectSpanishPrediction(r.getIntInRange(1, Database.getInstance(context).countSpanishPredictions()));
            default -> ResourceFinder.RESOURCE_NOT_FOUND;
        };
    }
}

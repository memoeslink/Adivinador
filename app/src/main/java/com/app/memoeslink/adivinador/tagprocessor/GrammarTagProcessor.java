package com.app.memoeslink.adivinador.tagprocessor;

public interface GrammarTagProcessor {
    ProcessedText replaceTags(String s);

    ProcessedText replaceTags(String s, int remainingMatches);
}

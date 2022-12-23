package com.app.memoeslink.adivinador.tagprocessor;

import com.memoeslink.generator.common.Gender;

public interface GrammarTagProcessor {
    ProcessedText replaceTags(String s);

    ProcessedText replaceTags(String s, int remainingMatches);

    ProcessedText replaceTags(String s, Gender gender, int remainingMatches);
}

package com.app.memoeslink.adivinador.tagprocessor;

import com.memoeslink.generator.common.Gender;

import java.util.regex.Pattern;

public class DefaultGrammarTagProcessor implements GrammarTagProcessor {
    public static final String GRAMMAR_TAG_REGEX = "";
    public static final Pattern GRAMMAR_TAG_PATTERN = Pattern.compile(GRAMMAR_TAG_REGEX);

    @Override
    public ProcessedText replaceTags(String s) {
        return replaceTags(s, Integer.MAX_VALUE);
    }

    @Override
    public ProcessedText replaceTags(String s, int remainingMatches) {
        return replaceTags(s, Gender.UNDEFINED, remainingMatches);
    }

    @Override
    public ProcessedText replaceTags(String s, Gender gender, int remainingMatches) {
        return new ProcessedText(s, 0, remainingMatches);
    }
}

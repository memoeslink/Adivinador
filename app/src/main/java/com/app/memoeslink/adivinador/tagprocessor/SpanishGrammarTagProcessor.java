package com.app.memoeslink.adivinador.tagprocessor;

import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.StringHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SpanishGrammarTagProcessor implements GrammarTagProcessor {
    public static final String GRAMMAR_TAG_REGEX = "(" + "(?<rule1>(?<rule1Start>^|\\s+)(?<rule1Text>\\{(?<rule1Contraction>[Aa]/al|[Dd]e/del)\\}(?<rule1Remainder>\\s+el)?)(?<rule1End>\\s+|$))" +
            //"| (?<rule2>\\{\\})" +
            //"| (?<rule3>\\{\\})" +
            ")";
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
        if (StringHelper.isNullOrEmpty(s))
            return new ProcessedText(s, 0, remainingMatches);
        gender = gender != null ? gender : Gender.UNDEFINED;
        Matcher matcher = GRAMMAR_TAG_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();
        int replacementCount = 0;

        while (remainingMatches > 0 && matcher.find()) {
            String replacement = matcher.group();

            if (matcher.group("rule1") != null) {
                String contraction = matcher.group("rule1Contraction");
                String preposition = StringHelper.substringBefore(contraction, "/");
                contraction = StringHelper.substringAfter(contraction, "/");

                if (matcher.group("rule1Remainder") != null) {
                    contraction = Character.isUpperCase(preposition.charAt(0)) ? StringHelper.capitalizeFirst(contraction) : contraction;
                    replacement = matcher.group("rule1Start") + contraction + matcher.group("rule1End");
                } else
                    replacement = matcher.group("rule1Start") + preposition + matcher.group("rule1End");
            }
            matcher.appendReplacement(sb, replacement);

            if (replacementCount < Integer.MAX_VALUE)
                replacementCount++;
            remainingMatches--;
        }
        matcher.appendTail(sb);
        return new ProcessedText(sb.toString(), replacementCount, remainingMatches);
    }
}

package com.app.memoeslink.adivinador.tagprocessor;

import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.english.IndefiniteArticle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnglishGrammarTagProcessor implements GrammarTagProcessor {
    public static final String GRAMMAR_TAG_REGEX = "(" + "(?<rule1>(?<rule1Tag>\\{(?<rule1Article>[Aa]/an)\\})(?<rule1Remainder>\\s+(?<rule1Word>\\b\\p{L}+\\b))?)" +
            "| (?<rule2>(?<rule2Start>^|\\s+)(?<rule2Tag>\\{(?<rule2PossessiveAdjective>[Hh]is/her)\\})(?<rule2End>\\s+|$))" +
            //"| (?<rule3>\\{\\})" +
            ")";
    public static final Pattern GRAMMAR_TAG_PATTERN = Pattern.compile(GRAMMAR_TAG_REGEX);

    @Override
    public ProcessedText replaceTags(String s) {
        return replaceTags(s, Integer.MAX_VALUE);
    }

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
                String article = matcher.group("rule1Article");

                if (matcher.group("rule1Remainder") != null) {
                    boolean uppercase = Character.isUpperCase(article.charAt(0));
                    article = IndefiniteArticle.get(matcher.group("rule1Word"));
                    article = uppercase ? StringHelper.capitalizeFirst(article) : article;
                }
                replacement = article + matcher.group("rule1Remainder");
            } else if (matcher.group("rule2") != null) {
                String possessiveAdjective = matcher.group("rule2PossessiveAdjective");

                switch (gender) {
                    case MASCULINE ->
                            possessiveAdjective = StringHelper.substringBefore(possessiveAdjective, "/");
                    case FEMININE -> {
                        boolean uppercase = Character.isUpperCase(possessiveAdjective.charAt(0));
                        possessiveAdjective = StringHelper.substringAfter(possessiveAdjective, "/");
                        possessiveAdjective = uppercase ? StringHelper.capitalizeFirst(possessiveAdjective) : possessiveAdjective;
                    }
                }
                replacement = matcher.group("rule2Start") + possessiveAdjective + matcher.group("rule2End");
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

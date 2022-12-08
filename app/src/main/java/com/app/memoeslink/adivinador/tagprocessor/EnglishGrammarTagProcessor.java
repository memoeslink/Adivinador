package com.app.memoeslink.adivinador.tagprocessor;

import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.english.IndefiniteArticle;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EnglishGrammarTagProcessor implements GrammarTagProcessor {
    public static final String GRAMMAR_TAG_REGEX = "(" + "(?<rule1>\\{(?<rule1Article>[Aa]/an)\\}(?<rule1Remainder>(\\s+)(?<rule1Word>\\b\\p{L}+\\b))?)" +
            //"| (?<rule2>\\{\\})" +
            //"| (?<rule3>\\{\\})" +
            ")";
    public static final Pattern GRAMMAR_TAG_PATTERN = Pattern.compile(GRAMMAR_TAG_REGEX);

    @Override
    public ProcessedText replaceTags(String s) {
        return replaceTags(s, Integer.MAX_VALUE);
    }

    public ProcessedText replaceTags(String s, int remainingMatches) {
        if (StringHelper.isNullOrEmpty(s))
            return new ProcessedText(s, 0, remainingMatches);
        Matcher matcher = GRAMMAR_TAG_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();
        int replacementCount = 0;

        while (remainingMatches > 0 && matcher.find()) {
            if (matcher.group("rule1") != null) {
                String article;

                if (matcher.group("rule1Remainder") != null) {
                    article = IndefiniteArticle.search(matcher.group("rule1Word"));
                    article = Character.isUpperCase(matcher.group("rule1Article").charAt(0)) ? StringHelper.capitalizeFirst(article) : article;
                } else
                    article = matcher.group("rule1Article");
                String replacement = StringHelper.replaceByIndex(matcher.group(), matcher.start("rule1Article"), matcher.end("rule1Article"), article);
                matcher.appendReplacement(sb, replacement);

                if (replacementCount < Integer.MAX_VALUE)
                    replacementCount++;
            }
            remainingMatches--;
        }
        matcher.appendTail(sb);
        return new ProcessedText(sb.toString(), replacementCount, remainingMatches);
    }
}

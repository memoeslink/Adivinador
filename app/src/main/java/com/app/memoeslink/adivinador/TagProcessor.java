package com.app.memoeslink.adivinador;

import android.content.Context;

import com.memoeslink.generator.common.Binder;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.Pair;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.common.TextComponent;
import com.memoeslink.generator.common.TextProcessor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagProcessor extends Binder {
    private static final String INTEGER_REGEX = "(-?[1-9]\\d*|0)";
    private static final String TAG_GENDER_REGEX = "(;\\s?gender:((user|⛌)|(random|⸮)|(default|＃)|" + INTEGER_REGEX + "))?";
    private static final String TAG_PLURAL_REGEX = "(;\\s?plural:(\\+?\\p{L}+))?";
    private static final Pattern GENDER_PATTERN = Pattern.compile("｢([0-2])｣");
    private static final String TAG_REGEX = "\\{((string|database):([\\w\\p{L}]+)(\\[!?[\\d]+\\])?" + TAG_GENDER_REGEX + "|method:[a-zA-Z0-9_$]+)\\}";
    private static final Pattern TAG_PATTERN = Pattern.compile(TAG_REGEX);
    private static final String WORD_TAG_REGEX = "\\{(" + TextProcessor.EXTENDED_WORD_REGEX + ")" + TAG_GENDER_REGEX + TAG_PLURAL_REGEX + "\\}";
    private static final Pattern WORD_TAG_PATTERN = Pattern.compile(WORD_TAG_REGEX);
    private static final String RANDOM_TAG_REGEX = "\\{rand:[^{}⸠⸡;]+(;[^{}⸠⸡;]+)*\\}";
    private static final Pattern RANDOM_TAG_PATTERN = Pattern.compile(RANDOM_TAG_REGEX);
    private final ResourceExplorer resourceExplorer;

    public TagProcessor(Context context) {
        this(context, null);
    }

    public TagProcessor(Context context, Long seed) {
        super(context, seed);
        resourceExplorer = new ResourceExplorer(context, seed);
    }

    public TextComponent replaceTags(String s) {
        return replaceTags(s, null, false);
    }

    public TextComponent replaceTags(String s, Gender predefinedGender, boolean plural) {
        if (StringHelper.isNullOrBlank(s))
            return new TextComponent();
        TextComponent component = new TextComponent();
        Gender gender = null;
        Gender defaultGender;
        boolean nullified = false;

        if (predefinedGender != null)
            defaultGender = predefinedGender;
        else
            defaultGender = r.getElement(Gender.values());

        //Verifies if the text contains curly brackets
        if (StringHelper.containsAny(s, "{", "}")) {
            int pairsOfBrackets = 0;
            int counter = 0;

            //Counts the possible pairs of curly brackets within the text
            for (int n = -1, length = s.length(); ++n < length; ) {
                boolean concluded = false;

                if (s.charAt(n) == '{')
                    counter++;
                else if (s.charAt(n) == '}') {
                    counter--;
                    concluded = true;
                }

                if (counter < 0) {
                    pairsOfBrackets = 0;
                    break;
                } else if (concluded)
                    pairsOfBrackets++;
            }
            Matcher matcher;

            //Replaces simple tags within the text, if there are any
            while (pairsOfBrackets > 0 && (matcher = TAG_PATTERN.matcher(s)).find()) {
                String replacement = "";
                String resourceName = StringHelper.startsWith(matcher.group(), "{method:") ? StringHelper.substringBetween(matcher.group(), ":", "}") : matcher.group(3);
                gender = getTrueGender(matcher.group(6), defaultGender);
                int index = -1;

                try {
                    Matcher indexMatcher = Pattern.compile("\\[!?\\d+\\]").matcher(matcher.group());

                    if (indexMatcher.find()) {
                        String match = StringHelper.removeAll(indexMatcher.group(), "[^\\d]");
                        index = Integer.valueOf(match);
                    }
                } catch (Exception e) {
                    index = -1;
                }

                if (StringHelper.startsWith(matcher.group(), "{string:"))
                    replacement = resourceExplorer.findResByName(resourceName, index);
                else if (StringHelper.startsWith(matcher.group(), "{database:"))
                    replacement = resourceExplorer.findTableRowByName(resourceName, index);
                else if (StringHelper.startsWith(matcher.group(), "{method:"))
                    replacement = resourceExplorer.findByReflection(resourceName);
                int openingIndex = StringHelper.indexOf(replacement, '{');
                int closingIndex;
                boolean empty = false;

                if (openingIndex >= 0 && (closingIndex = StringHelper.indexOf(replacement, '}')) >= 0 && openingIndex < closingIndex) {
                    TextComponent tempComponent;
                    tempComponent = replaceTags(replacement, defaultGender, plural);
                    replacement = tempComponent.getText();
                    gender = tempComponent.getHegemonicGender();
                    empty = replacement.isEmpty() || (tempComponent.isNullified() && StringHelper.isNullOrBlank(replacement));
                }

                if (empty)
                    s = StringHelper.removeFirst(s, "\\s*" + Pattern.quote(matcher.group()));
                else {
                    replacement = TextProcessor.genderifyStr(replacement, gender).getText();
                    s = StringHelper.replaceOnce(s, matcher.group(), replacement);
                }
                pairsOfBrackets--;
            }

            //Replaces ‘word’ tags within the text, if there are any
            while (pairsOfBrackets > 0 && (matcher = WORD_TAG_PATTERN.matcher(s)).find()) {
                String replacement = matcher.group(1);
                gender = getTrueGender(matcher.group(5), defaultGender);
                replacement = TextProcessor.genderifyStr(replacement, gender).getText();

                if (matcher.group(11) != null && plural) {
                    if (StringHelper.startsWith(matcher.group(11), '+'))
                        replacement = replacement + StringHelper.removeStart(matcher.group(11), "+");
                    else
                        replacement = matcher.group(11);
                }
                s = StringHelper.replaceOnce(s, matcher.group(), replacement);
                pairsOfBrackets--;
            }

            //Replaces ‘random’ tags within the text, if there are any
            while (pairsOfBrackets > 0 && (matcher = RANDOM_TAG_PATTERN.matcher(s)).find()) {
                String substring = StringHelper.removeStart(matcher.group(), "{rand:");
                substring = StringHelper.removeEnd(substring, "}");
                List<String> items = Arrays.asList(substring.split("\\s*;\\s*"));

                if (items.size() > 0) {
                    String replacement;
                    String regex;

                    if ((replacement = r.getItem(items)).trim().equals("∅")) {
                        replacement = "";
                        regex = "\\s*" + Pattern.quote(matcher.group());
                        nullified = true;
                    } else
                        regex = Pattern.quote(matcher.group());
                    s = StringHelper.replaceFirst(s, regex, replacement);
                    pairsOfBrackets--;
                }
            }
        }
        Pair<String, List<Gender>> pair = replaceDigitTags(s);

        if (pair.getSubValue() != null && pair.getSubValue().size() > 0 && !s.equals(pair.getSubKey()))
            gender = pair.getSubValue().get(pair.getSubValue().size() - 1);
        s = pair.getSubKey();

        //Replaces subtags within the text, if there are any
        if (StringHelper.containsAny(s, "⸠", "⸡")) {
            s = StringHelper.replaceEach(s, new String[]{"⸠", "⸡"}, new String[]{"{", "}"});
            s = replaceTags(s, gender != null ? gender : defaultGender, plural).getText();
        }
        component.setText(s);
        component.setHegemonicGender(gender);
        component.setNullified(nullified);
        return component;
    }

    private Pair<String, List<Gender>> replaceDigitTags(String s) {
        if (StringHelper.isNotNullOrEmpty(s) && StringHelper.containsAny(s, "｢", "｣")) {
            List<Gender> matches = new ArrayList<>();
            Matcher matcher = GENDER_PATTERN.matcher(s);

            while (matcher.find()) {
                matches.add(getTrueGender(matcher.group(1)));
            }
            return new Pair<>(StringHelper.removeAll(s, "｢[0-2]｣"), matches);
        }
        return new Pair<>(s, new ArrayList<>());
    }

    private Gender getTrueGender(String s) {
        return getTrueGender(s, Gender.UNDEFINED);
    }

    private Gender getTrueGender(String s, Gender defaultGender) {
        defaultGender = defaultGender != null ? defaultGender : Gender.UNDEFINED;

        if (StringHelper.isNullOrEmpty(s))
            return defaultGender;

        if (StringHelper.equalsAny(s, "user", "⛌"))
            return resourceExplorer.getPreferenceFinder().getGender();

        if (StringHelper.equalsAny(s, "random", "⸮"))
            return r.getBoolean() ? Gender.MASCULINE : Gender.FEMININE;

        if (StringHelper.equalsAny(s, "default", "＃"))
            return defaultGender;

        try {
            int genderValue = Integer.parseInt(s);
            return Gender.get(genderValue);
        } catch (NumberFormatException e) {
            return defaultGender;
        }
    }
}

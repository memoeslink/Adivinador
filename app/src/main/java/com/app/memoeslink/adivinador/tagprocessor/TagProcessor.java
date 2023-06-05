package com.app.memoeslink.adivinador.tagprocessor;

import android.content.Context;

import com.app.memoeslink.adivinador.ResourceExplorer;
import com.app.memoeslink.adivinador.preference.Preference;
import com.app.memoeslink.adivinador.preference.PreferenceHandler;
import com.memoeslink.generator.common.Binder;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.Pair;
import com.memoeslink.generator.common.ResourceReference;
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
    private static final String TAG_GENDER_REGEX = "(?<gender>;\\s?gender:(?<genderType>(user|⛌)|(random|⸮)|(default|＃)|" + INTEGER_REGEX + "|(undefined|masculine|feminine)))?";
    private static final String TAG_PLURAL_REGEX = "(?<plural>;\\s?plural:(?<pluralForm>\\+?\\p{L}+))?";
    private static final Pattern GENDER_PATTERN = Pattern.compile("｢([0-2])｣");
    private static final String TAG_REGEX = "\\{((?<resourceType>string|database):(?<resourceName>[\\w\\p{L}]+)(\\[!?[\\d]+\\])?" + TAG_GENDER_REGEX + "|(?<referenceType>method|reference):(?<referenceName>[a-zA-Z0-9_$]+))\\}";
    private static final Pattern TAG_PATTERN = Pattern.compile(TAG_REGEX);
    private static final String WORD_TAG_REGEX = "\\{(?<word>" + TextProcessor.EXTENDED_WORD_REGEX + ")" + TAG_GENDER_REGEX + TAG_PLURAL_REGEX + "\\}";
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

    @Override
    public void bindSeed(Long seed) {
        super.bindSeed(seed);
        resourceExplorer.bindSeed(seed);
    }

    @Override
    public void unbindSeed() {
        super.unbindSeed();
        resourceExplorer.unbindSeed();
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

        // Verify if the text contains curly brackets
        if (StringHelper.containsAny(s, "{", "}")) {
            // Get an approximate number of curly bracket pairs within the text
            int pairsOfBrackets = countPairsOfBrackets(s);

            // Replace simple tags within the text, if there are any
            Matcher matcher = TAG_PATTERN.matcher(s);
            StringBuffer sb;

            while (matcher.find()) {
                String replacement = "";
                String resourceName = matcher.group("referenceName") != null ? matcher.group("referenceName") : matcher.group("resourceName");
                gender = getTrueGender(matcher.group("genderType"), defaultGender);
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
                    replacement = resourceExplorer.findMethodByName(resourceName);
                else if (StringHelper.startsWith(matcher.group(), "{reference:"))
                    replacement = resourceExplorer.findByRef(ResourceReference.get(resourceName));
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

            // Replace ‘word’ tags within the text, if there are any
            matcher = WORD_TAG_PATTERN.matcher(s);
            sb = new StringBuffer();

            while (pairsOfBrackets > 0 && matcher.find()) {
                String replacement = matcher.group("word");
                gender = getTrueGender(matcher.group("genderType"), defaultGender);
                replacement = TextProcessor.genderifyStr(replacement, gender).getText();

                if (matcher.group("plural") != null && plural) {
                    if (StringHelper.startsWith(matcher.group("pluralForm"), '+'))
                        replacement = replacement + StringHelper.removeStart(matcher.group("pluralForm"), "+");
                    else
                        replacement = matcher.group("pluralForm");
                }
                matcher.appendReplacement(sb, replacement);
                pairsOfBrackets--;
            }
            matcher.appendTail(sb);
            s = sb.toString();

            // Replace ‘random’ tags within the text, if there are any
            matcher = RANDOM_TAG_PATTERN.matcher(s);

            while (pairsOfBrackets > 0 && matcher.find()) {
                String substring = StringHelper.removeStart(matcher.group(), "{rand:");
                substring = StringHelper.removeEnd(substring, "}");
                List<String> items = Arrays.asList(substring.split("\\s*;\\s*"));
                String replacement = r.getItem(items);

                if (StringHelper.isNullOrEmpty(replacement) || replacement.trim().equals("∅")) {
                    String regex = "\\s*" + Pattern.quote(matcher.group());
                    s = StringHelper.removeFirst(s, regex);
                    nullified = true;
                } else {
                    String regex = Pattern.quote(matcher.group());
                    s = StringHelper.replaceFirst(s, regex, replacement);
                }
                pairsOfBrackets--;
            }

            // Replace grammar tags within the text, if there are any
            GrammarTagProcessor grammarTagProcessor = new GrammarTagProcessorFactory(context).createGrammarTagProcessor();
            ProcessedText t = grammarTagProcessor.replaceTags(s, gender, pairsOfBrackets);
            s = t.getText();
            pairsOfBrackets = t.getRemainingMatches();
        }
        Pair<String, List<Gender>> pair = replaceDigitTags(s);

        if (pair.getSubValue() != null && pair.getSubValue().size() > 0 && !s.equals(pair.getSubKey()))
            gender = pair.getSubValue().get(pair.getSubValue().size() - 1);
        s = pair.getSubKey();
        gender = gender != null ? gender : defaultGender;

        // Replace subtags within the text, if there are any
        if (StringHelper.containsAny(s, "⸠", "⸡")) {
            s = StringHelper.replaceEach(s, new String[]{"⸠", "⸡"}, new String[]{"{", "}"});
            s = replaceTags(s, gender, plural).getText();
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
            return new Pair<>(StringHelper.removeAll(s, "\\s*｢[0-2]｣"), matches);
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

        if (StringHelper.equalsAny(s, "user", "⛌")) {
            int genderValue = PreferenceHandler.getInt(Preference.TEMP_GENDER, r.getInt(3));
            return Gender.get(genderValue);
        }

        if (StringHelper.equalsAny(s, "random", "⸮"))
            return r.getBoolean() ? Gender.MASCULINE : Gender.FEMININE;

        if (StringHelper.equalsAny(s, "default", "＃"))
            return defaultGender;

        switch (s) {
            case "undefined":
                return Gender.UNDEFINED;
            case "masculine":
                return Gender.MASCULINE;
            case "feminine":
                return Gender.FEMININE;
        }

        try {
            int genderValue = Integer.parseInt(s);
            return Gender.get(genderValue);
        } catch (NumberFormatException e) {
            return defaultGender;
        }
    }

    private int countPairsOfBrackets(String s) {
        if (StringHelper.isNullOrEmpty(s))
            return 0;
        int pairsOfBrackets = 0;
        int counter = 0;

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
        return pairsOfBrackets;
    }
}

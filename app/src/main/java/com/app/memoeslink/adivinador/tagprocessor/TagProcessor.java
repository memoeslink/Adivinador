package com.app.memoeslink.adivinador.tagprocessor;

import android.content.Context;

import com.app.memoeslink.adivinador.Database;
import com.app.memoeslink.adivinador.ResourceExplorer;
import com.app.memoeslink.adivinador.preference.Preference;
import com.app.memoeslink.adivinador.preference.PreferenceHandler;
import com.memoeslink.generator.common.Binder;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.GrammaticalNumber;
import com.memoeslink.generator.common.ResourceReference;
import com.memoeslink.generator.common.TextComponent;
import com.memoeslink.generator.common.TextProcessor;

import org.memoeslink.IntegerHelper;
import org.memoeslink.StringHelper;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import kotlin.Triple;

public class TagProcessor extends Binder {
    private static final String INTEGER_REGEX = "(-?[1-9]\\d*|0)";
    private static final String INDEX_ATTRIBUTE_REGEX = "(?<index>index:(?<specifiedIndex>" + INTEGER_REGEX + "))";
    private static final String GENDER_ATTRIBUTE_REGEX = "(?<gender>gender:(?<genderType>(user|⛌)|(random|⸮)|(default|＃)|" + INTEGER_REGEX + "|(undefined|masculine|feminine)))";
    private static final String PLURAL_ATTRIBUTE_REGEX = "(?<plural>plural:(?<pluralForm>\\+?\\p{L}+))";
    private static final String GENDER_REGEX = "｢(?<content>[0-2])｣";
    private static final Pattern GENDER_PATTERN = Pattern.compile("(?<startSpaces>\\s*)" + GENDER_REGEX + "(?<endSpaces>\\s*)");
    private static final String GRAMMATICAL_NUMBER_REGEX = "｢(?<content>[sSpPuU])｣";
    private static final Pattern GRAMMATICAL_NUMBER_PATTERN = Pattern.compile("(?<startSpaces>\\s*)" + GRAMMATICAL_NUMBER_REGEX + "(?<endSpaces>\\s*)");
    private static final String GENDER_AND_NUMBER_REGEX = "｢(?<content>[0-2sSpPuU])｣";
    private static final Pattern GENDER_AND_NUMBER_PATTERN = Pattern.compile("(?<startSpaces>\\s*)" + GENDER_AND_NUMBER_REGEX + "(?<endSpaces>\\s*)");
    private static final String TAG_REGEX = "\\{(?<resourceType>string|database|method|reference|preference):(?<resourceName>[a-zA-Z0-9_$.]+)(?<indexAttribute>;\\s?" + INDEX_ATTRIBUTE_REGEX + ")?(?<genderAttribute>;\\s?" + GENDER_ATTRIBUTE_REGEX + ")?\\}";
    private static final Pattern TAG_PATTERN = Pattern.compile(TAG_REGEX);
    private static final String WORD_TAG_REGEX = "\\{(?<word>" + TextProcessor.EXTENDED_WORD_REGEX + ")" + "(?<genderAttribute>;\\s?" + GENDER_ATTRIBUTE_REGEX + ")?" + "(?<pluralAttribute>;\\s?" + PLURAL_ATTRIBUTE_REGEX + ")?" + "\\}";
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
        return replaceTags(s, null, null);
    }

    public TextComponent replaceTags(String s, Gender defaultGender, GrammaticalNumber defaultGrammaticalNumber) {
        if (StringHelper.isNullOrBlank(s))
            return new TextComponent();
        defaultGender = defaultGender != null ? defaultGender : r.getEnum(Gender.class);
        defaultGrammaticalNumber = defaultGrammaticalNumber != null ? defaultGrammaticalNumber : r.getEnum(GrammaticalNumber.class);
        Gender gender = null;
        GrammaticalNumber grammaticalNumber = null;
        boolean nullified = false;
        TextComponent component = new TextComponent();

        // Verify if the text contains curly brackets
        if (StringHelper.containsAny(s, "{", "}")) {
            // Get an approximate number of pairs of curly brackets within the text
            int pairsOfBrackets = countPairsOfBrackets(s);

            // Replace simple tags within the text, if there are any
            Matcher matcher = TAG_PATTERN.matcher(s);
            StringBuffer sb;

            while (matcher.find()) {
                gender = getTrueGender(matcher.group("genderType"), defaultGender);
                String resourceType = matcher.group("resourceType");
                String resourceName = matcher.group("resourceName");
                int index = IntegerHelper.tryParse(matcher.group("resourceType"), -1);

                String replacement = switch (resourceType) {
                    case "string" -> resourceExplorer.findResByName(resourceName, index);
                    case "database" -> resourceExplorer.findTableRowByName(resourceName, index);
                    case "method" -> resourceExplorer.findMethodByName(resourceName);
                    case "reference" ->
                            resourceExplorer.findByRef(ResourceReference.get(resourceName));
                    case "preference" -> resourceExplorer.findPrefByTag(resourceName);
                    default -> Database.DEFAULT_VALUE;
                };
                int openingIndex = StringHelper.indexOf(replacement, '{');
                int closingIndex = StringHelper.indexOf(replacement, '}');
                boolean empty = false;

                if (openingIndex >= 0 && closingIndex >= 0 && openingIndex < closingIndex) {
                    TextComponent tempComponent;
                    tempComponent = replaceTags(replacement, defaultGender, defaultGrammaticalNumber);
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

                if (matcher.group("plural") != null && defaultGrammaticalNumber == GrammaticalNumber.PLURAL) {
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
                String[] parts = StringHelper.split(substring, "\\s*;\\s*");
                String replacement = r.getElement(parts);

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

        // Replace gender and number tags and get the hegemonic, or most recent, values
        Triple<String, Gender, GrammaticalNumber> result = replaceGrammarTags(s);

        if (!StringHelper.equals(s, result.getFirst())) {
            s = result.getFirst();

            if (result.getSecond() != null)
                gender = result.getSecond();

            if (result.getSecond() != null)
                grammaticalNumber = result.getThird();
        }
        gender = gender != null ? gender : defaultGender;
        grammaticalNumber = grammaticalNumber != null ? grammaticalNumber : defaultGrammaticalNumber;

        // Replace subtags within the text, if there are any
        if (StringHelper.containsAny(s, "⸠", "⸡")) {
            s = StringHelper.replaceEach(s, new String[]{"⸠", "⸡"}, new String[]{"{", "}"});
            s = replaceTags(s, gender, grammaticalNumber).getText();
        }
        component.setText(s);
        component.addGender(gender);
        component.addGrammaticalNumber(grammaticalNumber);
        component.setNullified(nullified);
        return component;
    }

    private Triple<String, Gender, GrammaticalNumber> replaceGrammarTags(String s) {
        Triple<String, Gender, GrammaticalNumber> result = new Triple<>(s, null, null);

        if (StringHelper.containsAny(s, "｢", "｣")) {
            Gender gender = null;
            GrammaticalNumber grammaticalNumber = null;
            Matcher matcher = GENDER_AND_NUMBER_PATTERN.matcher(s);
            StringBuffer sb = new StringBuffer();

            while (matcher.find()) {
                String content = matcher.group("content");

                if (content == null) continue;

                switch (content) {
                    case "0", "1", "2" -> {
                        int value = IntegerHelper.tryParse(content, -1);
                        gender = Gender.get(value);
                    }
                    case "s", "S" -> grammaticalNumber = GrammaticalNumber.SINGULAR;
                    case "p", "P" -> grammaticalNumber = GrammaticalNumber.PLURAL;
                    case "u", "U" -> grammaticalNumber = GrammaticalNumber.UNDEFINED;
                }
                matcher.appendReplacement(sb, StringHelper.defaultIfNull(matcher.group("endSpaces")));
            }
            matcher.appendTail(sb);
            result = new Triple<>(sb.toString(), gender, grammaticalNumber);
        }
        return result;
    }

    private Gender getTrueGender(String s) {
        return getTrueGender(s, Gender.UNDEFINED);
    }

    private Gender getTrueGender(String s, Gender defaultGender) {
        defaultGender = defaultGender != null ? defaultGender : Gender.UNDEFINED;

        if (StringHelper.isNullOrEmpty(s))
            return defaultGender;

        if (StringHelper.equalsAny(s, "user", "⛌")) {
            int genderValue = PreferenceHandler.getInt(Preference.TEMP_GENDER, -1);
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

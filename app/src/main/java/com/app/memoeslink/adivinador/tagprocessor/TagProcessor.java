package com.app.memoeslink.adivinador.tagprocessor;

import android.content.Context;

import com.app.memoeslink.adivinador.Database;
import com.app.memoeslink.adivinador.LanguageHelper;
import com.app.memoeslink.adivinador.ResourceExplorer;
import com.memoeslink.generator.common.Binder;
import com.memoeslink.generator.common.DateTimeGetter;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.GrammaticalNumber;
import com.memoeslink.generator.common.TextComponent;
import com.memoeslink.generator.common.TextProcessor;

import org.memoeslink.DateTimeHelper;
import org.memoeslink.IntegerHelper;
import org.memoeslink.StringHelper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TagProcessor extends Binder {
    public static final String REFERENCE_REGEX = "[a-zA-Z0-9_$.]+";
    public static final String INTEGER_REGEX = "(-?[1-9]\\d*|0)";
    public static final String GENDER_REGEX = "(any|undefined|neutral|masculine|feminine)";
    public static final String GRAMMATICAL_NUMBER_REGEX = "(any|undefined|singular|plural)";
    public static final String ACTOR_TAG_REGEX = "(?<actorTagStartSpaces>\\s*)" + "(?<!#)\\{" +
            "actor:" + "(?<actorTagName>" + TextProcessor.BASE_TEXT_REGEX + "+)" +
            "(;\\s?" + "(?<actorTagIdAttr>id:(?<actorTagId>" + REFERENCE_REGEX + "))" + ")" +
            "(;\\s?" + "(?<actorTagGramIndicatorAttr>grammatical-indicator:(?<actorTagGramIndicator>(" + GRAMMATICAL_NUMBER_REGEX + "|" + GENDER_REGEX + "|" + GRAMMATICAL_NUMBER_REGEX + "-" + GENDER_REGEX + ")))" + ")?" +
            "(;\\s?" + "(?<actorTagHiddenAttr>hidden)" + ")?" +
            "\\}" + "(?<actorTagEndSpaces>\\s*)";
    public static final Pattern ACTOR_TAG_PATTERN = Pattern.compile(ACTOR_TAG_REGEX);
    public static final String SIMPLE_TAG_REGEX = "(?<simpleTagStartSpaces>\\s*)" + "#\\{(?<simpleTagContent>" + REFERENCE_REGEX + ")?\\}" + "(?<simpleTagEndSpaces>\\s*)";
    public static final Pattern SIMPLE_TAG_PATTERN = Pattern.compile(SIMPLE_TAG_REGEX);
    public static final String RESOURCE_TAG_REGEX = "(?<resourceTagStartSpaces>\\s*)" + "(?<!#)\\{" +
            "(?<resourceTagType>string|database|compendium|method|reference|preference):(?<resourceTagName>" + REFERENCE_REGEX + ")" +
            "(;\\s?" + "(?<resourceTagIndexAttr>index:(?<resourceTagIndex>" + INTEGER_REGEX + "|[hms]))" + ")?" +
            "\\}" + "(?<resourceTagEndSpaces>\\s*)";
    public static final Pattern RESOURCE_TAG_PATTERN = Pattern.compile(RESOURCE_TAG_REGEX);
    public static final String WORD_TAG_REGEX = "(?<wordTagStartSpaces>\\s*)" + "(?<!#)\\{" +
            "(?<wordTagContent>" + TextProcessor.EXTENDED_TEXT_REGEX + ")" +
            "(;\\s?" + "(?<wordTagPluralFormAttr>plural-form:(?<wordTagPluralForm>\\+" + TextProcessor.BASE_TEXT_REGEX + "+|" + TextProcessor.BASE_TEXT_REGEX + "+\\+|" + TextProcessor.EXTENDED_TEXT_REGEX + "))" + ")?" +
            "(;\\s?" + "(?<wordTagInfluenceAttr>influence:(?<wordTagInfluence>" + REFERENCE_REGEX + "|\\?))" + ")?" +
            "\\}" + "(?<wordTagEndSpaces>\\s*)";
    public static final Pattern WORD_TAG_PATTERN = Pattern.compile(WORD_TAG_REGEX);
    public static final String DATE_TIME_TAG_REGEX = "(?<dateTimeTagStartSpaces>\\s*)" + "(?<!#)\\{" +
            "(?<dateTimeTagContent>" + "date-time" + "(:(?<dateTimeTagFormat>[\\w\\s\\-:./'\\[\\]]+))?" + "|" +
            "date" + "|" +
            "time" + ")" +
            "\\}" + "(?<dateTimeTagEndSpaces>\\s*)";
    public static final Pattern DATE_TIME_TAG_PATTERN = Pattern.compile(DATE_TIME_TAG_REGEX);
    public static final String RANDOM_TAG_OPTIONS_REGEX = "(" + "∅" + "|" +
            ACTOR_TAG_PATTERN + "|" +
            SIMPLE_TAG_PATTERN + "|" +
            RESOURCE_TAG_REGEX + "|" +
            WORD_TAG_REGEX + "|" +
            DATE_TIME_TAG_REGEX + "|" +
            TextProcessor.BASE_TEXT_REGEX + "+" +
            ")";
    public static final String RANDOM_TAG_REGEX = "(?<randomTagStartSpaces>\\s*)" + "(?<!#)\\{" + "" +
            "rand:" + "(?<randomTagOptions>(" + RANDOM_TAG_OPTIONS_REGEX + "(;|(?=\\}))" + ")+)" +
            "\\}" + "(?<randomTagEndSpaces>\\s*)";
    public static final Pattern RANDOM_TAG_PATTERN = Pattern.compile(RANDOM_TAG_REGEX);
    private final ActorRegistry actorRegistry;
    private final List<String> compendium;
    private final ResourceExplorer explorer;

    private TagProcessor(Context context, Long seed, ActorRegistry actorRegistry, List<String> compendium) {
        super(context, seed);
        this.actorRegistry = actorRegistry;
        this.compendium = compendium;
        this.explorer = new ResourceExplorer(context, seed);
    }

    public ActorRegistry getActorRegistry() {
        return actorRegistry;
    }

    public List<String> getCompendium() {
        return compendium;
    }

    public TextComponent replaceTags(String s) {
        return replaceTags(s, false);
    }

    private TextComponent replaceTags(String s, boolean nested) {
        if (StringHelper.isNullOrBlank(s))
            return new TextComponent();

        if (!StringHelper.containsAny(s, "{", "}"))
            return new TextComponent(s);
        int pairsOfBrackets = countPairsOfBrackets(s).getPairsOfTopLevelBrackets();

        if (pairsOfBrackets == 0)
            return new TextComponent(s);
        TextComponent component = new TextComponent();

        // Replace random tags within the text, if there are any
        ProcessedText t = replaceRandomTags(s, pairsOfBrackets);

        // Replace actor tags within the text, if there are any
        t = replaceActorTags(t.getText(), t.getRemainingMatches());

        // Replace simple tags within the text, if there are any
        t = replaceSimpleTags(t.getText(), t.getRemainingMatches());

        // Replace resource tags within the text, if there are any
        t = replaceResourceTags(t.getText(), t.getRemainingMatches());

        // Replace word tags within the text, if there are any
        t = replaceWordTags(t.getText(), t.getRemainingMatches(), nested ? DefaultHandling.NULL : DefaultHandling.ANY);

        // Replace date-time tags within the text, if there are any
        t = replaceDateTimeTags(t.getText(), t.getRemainingMatches());

        // Replace grammar tags within the text, if there are any
        GrammarTagProcessor grammarTagProcessor = new GrammarTagProcessorFactory(context).createGrammarTagProcessor();
        t = grammarTagProcessor.replaceTags(t.getText(), actorRegistry.getDefaultActor().getGender(), t.getRemainingMatches());
        component.setText(t.getText());
        pairsOfBrackets = t.getRemainingMatches();
        return component;
    }

    private BracketBalance countPairsOfBrackets(String s) {
        if (StringHelper.isNullOrEmpty(s))
            return new BracketBalance();
        int pairsOfBrackets = 0;
        int pairsOfTopLevelBrackets = 0;
        int counter = 0;

        for (char c : s.toCharArray()) {
            if (c == '{') {
                if (counter == 0) pairsOfTopLevelBrackets++;
                counter++;
            } else if (c == '}') {
                pairsOfBrackets++;
                counter--;
            }

            if (counter < 0) return new BracketBalance();
        }
        return new BracketBalance(pairsOfBrackets, pairsOfTopLevelBrackets, true);
    }

    public ProcessedText replaceRandomTags(String s) {
        return replaceRandomTags(s, Integer.MAX_VALUE);
    }

    public ProcessedText replaceRandomTags(String s, int remainingMatches) {
        Matcher matcher = RANDOM_TAG_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();
        int replacementCount = 0;

        while (remainingMatches > 0 && matcher.find()) {
            String substring = matcher.group("randomTagOptions");
            String[] parts = StringHelper.split(substring, "(?<!\\{[^\\{\\}]{1,9999})\\s*;\\s*(?![^\\{\\}]{1,9999}\\})");
            String replacement = r.getElement(parts);

            if (StringHelper.isNullOrEmpty(replacement) || replacement.trim().equals("∅"))
                replacement = matcher.group("randomTagEndSpaces");
            else {
                TextComponent tempComponent = this.replaceTags(replacement, true);
                replacement = matcher.group("randomTagStartSpaces") + tempComponent.getText() + matcher.group("randomTagEndSpaces");
            }
            matcher.appendReplacement(sb, replacement);

            if (replacementCount < Integer.MAX_VALUE)
                replacementCount++;
            remainingMatches--;
        }
        matcher.appendTail(sb);
        return new ProcessedText(sb.toString(), replacementCount, remainingMatches);
    }

    public ProcessedText replaceActorTags(String s) {
        return replaceActorTags(s, Integer.MAX_VALUE);
    }

    public ProcessedText replaceActorTags(String s, int remainingMatches) {
        Matcher matcher = ACTOR_TAG_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();
        int replacementCount = 0;

        while (remainingMatches > 0 && matcher.find()) {
            String actorName = matcher.group("actorTagName").trim();
            String actorId = matcher.group("actorTagId");
            String replacement;

            if (matcher.group("actorTagHiddenAttr") != null)
                replacement = matcher.group("actorTagEndSpaces");
            else
                replacement = matcher.group("actorTagStartSpaces") + actorName + matcher.group("actorTagEndSpaces");
            Actor actor = new Actor(actorName);

            if (matcher.group("actorTagGramIndicatorAttr") != null) {
                String grammaticalIndicator = matcher.group("actorTagGramIndicator");
                Gender gender = Gender.UNDEFINED;
                GrammaticalNumber grammaticalNumber = GrammaticalNumber.UNDEFINED;

                if (StringHelper.startsWith(grammaticalIndicator, "singular"))
                    grammaticalNumber = GrammaticalNumber.SINGULAR;
                else if (StringHelper.startsWith(grammaticalIndicator, "plural"))
                    grammaticalNumber = GrammaticalNumber.PLURAL;
                else if (StringHelper.startsWith(grammaticalIndicator, "any"))
                    grammaticalNumber = r.getBoolean() ? GrammaticalNumber.SINGULAR : GrammaticalNumber.PLURAL;

                if (StringHelper.endsWith(grammaticalIndicator, "masculine"))
                    gender = Gender.MASCULINE;
                else if (StringHelper.endsWith(grammaticalIndicator, "feminine"))
                    gender = Gender.FEMININE;
                else if (StringHelper.endsWith(grammaticalIndicator, "neutral"))
                    gender = Gender.NEUTRAL;
                else if (StringHelper.endsWith(grammaticalIndicator, "any"))
                    gender = r.getBoolean() ? Gender.MASCULINE : Gender.FEMININE;

                actor.setGender(gender);
                actor.setGrammaticalNumber(grammaticalNumber);
            }
            actorRegistry.put(actorId, actor, true);
            matcher.appendReplacement(sb, replacement);

            if (replacementCount < Integer.MAX_VALUE)
                replacementCount++;
            remainingMatches--;
        }
        matcher.appendTail(sb);
        return new ProcessedText(sb.toString(), replacementCount, remainingMatches);
    }

    public ProcessedText replaceSimpleTags(String s) {
        return replaceSimpleTags(s, Integer.MAX_VALUE);
    }

    public ProcessedText replaceSimpleTags(String s, int remainingMatches) {
        Matcher matcher = SIMPLE_TAG_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();
        int replacementCount = 0;

        while (remainingMatches > 0 && matcher.find()) {
            Actor actor = actorRegistry.getOrDefault(matcher.group("simpleTagContent"));
            String replacement = matcher.group("simpleTagStartSpaces") + actor.getDescriptor() + matcher.group("simpleTagEndSpaces");
            matcher.appendReplacement(sb, replacement);

            if (replacementCount < Integer.MAX_VALUE)
                replacementCount++;
            remainingMatches--;
        }
        matcher.appendTail(sb);
        return new ProcessedText(sb.toString(), replacementCount, remainingMatches);
    }

    public ProcessedText replaceResourceTags(String s) {
        return replaceResourceTags(s, Integer.MAX_VALUE);
    }

    public ProcessedText replaceResourceTags(String s, int remainingMatches) {
        Matcher matcher = RESOURCE_TAG_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();
        int replacementCount = 0;

        while (remainingMatches > 0 && matcher.find()) {
            String resourceType = matcher.group("resourceTagType");
            String resourceName = matcher.group("resourceTagName");
            String intendedIndex = StringHelper.defaultIfNull(matcher.group("resourceTagIndex"));
            int index;

            switch (intendedIndex) {
                case "h" -> index = DateTimeHelper.getCurrentTime().getHour();
                case "m" -> index = DateTimeHelper.getCurrentTime().getMinute();
                case "s" -> index = DateTimeHelper.getCurrentTime().getSecond();
                default -> index = IntegerHelper.tryParse(intendedIndex, -1);
            }

            String replacement = switch (resourceType) {
                case "string" -> explorer.getResourceFinder().getResByName(resourceName, index);
                case "database" -> explorer.findTableRowByName(resourceName, index);
                case "compendium" -> explorer.getResourceFinder().getStrFromList(compendium, index);
                case "method" -> explorer.findMethodByName(resourceName);
                case "reference" -> explorer.getResourceFinder().getResByName(resourceName);
                case "preference" -> explorer.findPrefByTag(resourceName);
                default -> Database.DEFAULT_VALUE;
            };
            int openingIndex = StringHelper.indexOf(replacement, '{');
            int closingIndex = StringHelper.indexOf(replacement, '}');

            if (openingIndex >= 0 && closingIndex >= 0 && openingIndex < closingIndex) {
                TextComponent tempComponent = this.replaceTags(replacement, true);
                replacement = tempComponent.getText();
            }

            if (StringHelper.isNullOrBlank(replacement))
                replacement = matcher.group("resourceTagEndSpaces");
            else
                replacement = matcher.group("resourceTagStartSpaces") + replacement + matcher.group("resourceTagEndSpaces");
            matcher.appendReplacement(sb, replacement);

            if (replacementCount < Integer.MAX_VALUE)
                replacementCount++;
            remainingMatches--;
        }
        matcher.appendTail(sb);
        return new ProcessedText(sb.toString(), replacementCount, remainingMatches);
    }

    public ProcessedText replaceWordTags(String s) {
        return replaceWordTags(s, Integer.MAX_VALUE);
    }

    public ProcessedText replaceWordTags(String s, int remainingMatches) {
        return replaceWordTags(s, remainingMatches, DefaultHandling.ANY);
    }

    private ProcessedText replaceWordTags(String s, int remainingMatches, DefaultHandling defaultHandling) {
        Matcher matcher = WORD_TAG_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();
        int replacementCount = 0;

        Actor inlineActor = new Actor();
        Gender gender = r.getBoolean() ? Gender.MASCULINE : Gender.FEMININE;
        GrammaticalNumber grammaticalNumber = r.getBoolean() ? GrammaticalNumber.SINGULAR : GrammaticalNumber.PLURAL;
        inlineActor.setGender(gender);
        inlineActor.setGrammaticalNumber(grammaticalNumber);

        while (remainingMatches > 0 && matcher.find()) {
            String replacement = matcher.group("wordTagContent");
            String actorId = matcher.group("wordTagInfluence");
            Actor actor = actorRegistry.getOrDefault(actorId, defaultHandling);

            if (actor != null) {
                String pluralForm = matcher.group("wordTagPluralForm");
                replacement = TextProcessor.genderifyStr(replacement, actor.getGender()).getText();

                if (pluralForm != null && actor.getGrammaticalNumber() == GrammaticalNumber.PLURAL) {
                    if (StringHelper.startsWith(pluralForm, '+'))
                        replacement = replacement + StringHelper.removeStart(pluralForm, "+");
                    else if (StringHelper.endsWith(pluralForm, '+'))
                        replacement = replacement + StringHelper.removeEnd(pluralForm, "+");
                    else
                        replacement = TextProcessor.genderifyStr(pluralForm, actor.getGender()).getText();
                }
                replacement = matcher.group("wordTagStartSpaces") + replacement + matcher.group("wordTagEndSpaces");
                matcher.appendReplacement(sb, replacement);

                if (replacementCount < Integer.MAX_VALUE)
                    replacementCount++;
            } else if (StringHelper.equals(actorId, "?")) {
                replacement = matcher.group("wordTagStartSpaces") + TextProcessor.genderifyStr(replacement, inlineActor.getGender()).getText() + matcher.group("wordTagEndSpaces");
                matcher.appendReplacement(sb, replacement);
            }
            remainingMatches--;
        }
        matcher.appendTail(sb);
        return new ProcessedText(sb.toString(), replacementCount, remainingMatches);
    }

    public ProcessedText replaceDateTimeTags(String s) {
        return replaceDateTimeTags(s, Integer.MAX_VALUE);
    }

    public ProcessedText replaceDateTimeTags(String s, int remainingMatches) {
        Matcher matcher = DATE_TIME_TAG_PATTERN.matcher(s);
        StringBuffer sb = new StringBuffer();
        int replacementCount = 0;

        while (remainingMatches > 0 && matcher.find()) {
            String dateTimeType = matcher.group("dateTimeTagContent");
            String replacement;

            switch (dateTimeType) {
                case "date-time" ->
                        replacement = DateTimeGetter.with(LanguageHelper.getLocale(context), r).getCurrentDateTime();
                case "date" ->
                        replacement = DateTimeGetter.with(LanguageHelper.getLocale(context), r).getCurrentDate();
                case "time" ->
                        replacement = DateTimeGetter.with(LanguageHelper.getLocale(context), r).getCurrentTime();
                default -> {
                    LocalDateTime dateTime = DateTimeHelper.getCurrentDateTime();
                    String dateTimeFormat = StringHelper.trimToEmpty(matcher.group("dateTimeTagFormat"));
                    replacement = DateTimeFormatter.ofPattern(dateTimeFormat, LanguageHelper.getLocale(context)).format(dateTime);
                }
            }
            replacement = matcher.group("dateTimeTagStartSpaces") + StringHelper.defaultWhenBlank(replacement) + matcher.group("dateTimeTagEndSpaces");
            matcher.appendReplacement(sb, replacement);

            if (replacementCount < Integer.MAX_VALUE)
                replacementCount++;
            remainingMatches--;
        }
        matcher.appendTail(sb);
        return new ProcessedText(sb.toString(), replacementCount, remainingMatches);
    }

    public static class NewTagProcessorBuilder {
        private Context context;
        private Long seed;
        private ActorRegistry actorRegistry = new ActorRegistry();
        private final List<String> compendium = new ArrayList<>();

        public NewTagProcessorBuilder(Context context) {
            this.context = context;
        }

        public NewTagProcessorBuilder setSeed(Long seed) {
            this.seed = seed;
            return this;
        }

        public NewTagProcessorBuilder setDefaultActor(Actor defaultActor) {
            defaultActor = defaultActor != null ? defaultActor : new Actor();
            actorRegistry.setDefaultActor(defaultActor);
            return this;
        }

        public NewTagProcessorBuilder setActor(String tag, Actor actor) {
            if (actor != null && StringHelper.isNotNullOrBlank(tag) && tag.matches("^" + REFERENCE_REGEX + "$"))
                this.actorRegistry.put(tag, actor, false);
            return this;
        }

        public NewTagProcessorBuilder setData(String text) {
            if (text != null) this.compendium.add(text);
            return this;
        }

        public NewTagProcessorBuilder setDuplicateHandling(DuplicateHandling duplicateHandling) {
            duplicateHandling = duplicateHandling != null ? duplicateHandling : DuplicateHandling.DO_NOTHING;
            this.actorRegistry.setDuplicateHandling(duplicateHandling);
            return this;
        }

        public TagProcessor build() {
            return new TagProcessor(context, seed, actorRegistry, compendium);
        }
    }

    private static class BracketBalance {
        private int pairsOfBrackets;
        private int pairsOfTopLevelBrackets;
        private boolean balanced;

        private BracketBalance() {
            pairsOfBrackets = 0;
            pairsOfTopLevelBrackets = 0;
            balanced = false;
        }

        private BracketBalance(int pairsOfBrackets, int pairsOfTopLevelBrackets, boolean balanced) {
            this.pairsOfBrackets = pairsOfBrackets;
            this.pairsOfTopLevelBrackets = pairsOfTopLevelBrackets;
            this.balanced = balanced;
        }

        public int getPairsOfBrackets() {
            return pairsOfBrackets;
        }

        public void setPairsOfBrackets(int pairsOfBrackets) {
            this.pairsOfBrackets = pairsOfBrackets;
        }

        public int getPairsOfTopLevelBrackets() {
            return pairsOfTopLevelBrackets;
        }

        public void setPairsOfTopLevelBrackets(int pairsOfTopLevelBrackets) {
            this.pairsOfTopLevelBrackets = pairsOfTopLevelBrackets;
        }

        public boolean isBalanced() {
            return balanced;
        }

        public void setBalanced(boolean balanced) {
            this.balanced = balanced;
        }
    }
}

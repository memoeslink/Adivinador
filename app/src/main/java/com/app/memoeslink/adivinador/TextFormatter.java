package com.app.memoeslink.adivinador;

import android.content.Context;
import android.text.Html;
import android.text.Spanned;

import com.memoeslink.generator.common.Constant;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.IntegerHelper;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.common.TextComponent;
import com.memoeslink.generator.common.TextProcessor;

import java.main.common.Randomizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextFormatter extends BaseWrapper implements Validation {
    private static final String INTEGER_REGEX = "(-?[1-9]\\d*|0)";
    private static final Pattern GENDER_PATTERN = Pattern.compile("｢([0-2])｣");
    private static final String TAG_REGEX = "\\{((string|database):([\\w\\p{L}]+)(\\[!?[\\d]+\\])?(\\s*⸻(⛌|⸮|" + INTEGER_REGEX + "))?|method:[a-zA-Z0-9_$]+)\\}";
    private static final Pattern TAG_PATTERN = Pattern.compile(TAG_REGEX);
    private static final String WORD_TAG_REGEX = "\\{(" + TextProcessor.EXTENDED_WORD_REGEX + ")(⸻(⛌|⸮|" + INTEGER_REGEX + "))?\\}";
    private static final Pattern WORD_TAG_PATTERN = Pattern.compile(WORD_TAG_REGEX);
    private static final String RANDOM_TAG_REGEX = "\\{rand:[^{}⸠⸡;]+(;[^{}⸠⸡;]+)*\\}";
    private static final Pattern RANDOM_TAG_PATTERN = Pattern.compile(RANDOM_TAG_REGEX);
    private static final String DOUBLE_FULL_STOP_REGEX = "\\.(\\s*</?\\w+>\\s*)*\\.";
    private static final Pattern DOUBLE_FULL_STOP_PATTERN = Pattern.compile(DOUBLE_FULL_STOP_REGEX);
    private static final String FORMAT_REGEX = "^((a|b|big|i|s|small|tt|u),)*(a|b|big|i|s|small|tt|u)$";
    private static final Pattern FORMAT_PATTERN = Pattern.compile(FORMAT_REGEX);
    private final Randomizer r;
    private final ResourceFinder resourceFinder;

    public TextFormatter(Context context) {
        this(context, null);
    }

    public TextFormatter(Context context, Long seed) {
        super(context);
        r = new Randomizer(seed);
        resourceFinder = new ResourceFinder(context, seed);
    }

    public static Spanned fromHtml(String html) {
        return Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
    }

    public void bindSeed(Long seed) {
        r.bindSeed(seed);
        resourceFinder.bindSeed(seed);
    }

    public void unbindSeed() {
        r.unbindSeed();
        resourceFinder.unbindSeed();
    }

    public TextComponent replaceTags(String s) {
        return replaceTags(s, null);
    }

    public TextComponent replaceTags(String s, Gender predefinedGender) {
        if (StringHelper.isNullOrBlank(s))
            return new TextComponent();
        TextComponent component = new TextComponent();
        Gender gender = null;
        Gender defaultGender;
        boolean defaulted = false;
        boolean nullified = false;

        if (defaulted = (predefinedGender != null))
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
                String resourceName = matcher.group().startsWith("{method:") ? StringHelper.substringBetween(matcher.group(), ":", "}") : matcher.group(3);
                String resourceType;
                gender = getTrueGender(matcher.group(6), defaultGender);
                Integer index = null;
                boolean empty = false;

                try {
                    Matcher indexMatcher = Pattern.compile("\\[!?\\d+\\]").matcher(matcher.group());

                    if (indexMatcher.find()) {
                        String match = StringHelper.removeAll(indexMatcher.group(), "[^\\d]");
                        index = Integer.valueOf(match);
                    }
                } catch (Exception e) {
                    index = -1;
                }

                if (StringHelper.startsWith(matcher.group(), "{string:")) {
                    int resourceId = resourceFinder.getArrayResourceId(resourceName);
                    resourceId = (resourceId == 0 ? resourceFinder.getStringResourceId(resourceName) : resourceId);

                    if (resourceId != 0) {
                        if ((resourceType = getResources().getResourceTypeName(resourceId)).equals("array"))
                            replacement = index != null && index >= 0 ? resourceFinder.getStrFromStrArrayRes(resourceId, index) : resourceFinder.getStrFromStrArrayRes(resourceId);
                        else if (resourceType.equals("string"))
                            replacement = index != null && index >= 0 ? resourceFinder.getStrFromSplitStrRes(resourceId, index) : resourceFinder.getStrFromSplitStrRes(resourceId);
                    }
                } else if (StringHelper.startsWith(matcher.group(), "{database:")) {
                    if (index == null || index < 0)
                        index = r.getInt(1, Database.getInstance(this).countTableRows(resourceName));
                    else
                        index = IntegerHelper.defaultIndex(index, Database.getInstance(this).countTableRows(resourceName));
                    replacement = Database.getInstance(this).selectFromTable(resourceName, index);
                } else if (StringHelper.startsWith(matcher.group(), "{method:")) {
                    if ((replacement = resourceFinder.callMethod(resourceName)) != null) {
                    } else if ((replacement = resourceFinder.invokeMethod(resourceName)) != null) {
                    } else
                        replacement = "?";
                }
                int openingIndex = StringHelper.indexOf(replacement, '{');
                int closingIndex;

                if (openingIndex >= 0 && (closingIndex = StringHelper.indexOf(replacement, '}')) >= 0 && openingIndex < closingIndex) {
                    TextComponent tempComponent;

                    if (defaulted)
                        tempComponent = replaceTags(replacement, defaultGender);
                    else
                        tempComponent = replaceTags(replacement);
                    replacement = tempComponent.getText();
                    gender = tempComponent.getHegemonicGender();
                    empty = replacement.isEmpty() || (tempComponent.isNullified() && StringHelper.isNullOrBlank(replacement));
                }

                if (empty)
                    s = StringHelper.removeFirst(s, "\\s*" + Pattern.quote(matcher.group()));
                else
                    s = StringHelper.replaceOnce(s, matcher.group(), TextProcessor.genderifyStr(replacement, gender).getText());
                pairsOfBrackets--;
            }

            //Replaces ‘word’ tags within the text, if there are any
            while (pairsOfBrackets > 0 && (matcher = WORD_TAG_PATTERN.matcher(s)).find()) {
                String replacement = matcher.group(1);
                gender = getTrueGender(matcher.group(5), defaultGender);
                replacement = TextProcessor.genderifyStr(replacement, gender).getText();
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

                    if ((replacement = resourceFinder.getStrFromList(items)).trim().equals("∅")) {
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
            s = replaceTags(s, gender != null ? gender : defaultGender).getText();
        }
        component.setText(s);
        component.setHegemonicGender(gender);
        component.setNullified(nullified);
        return component;
    }

    public Pair<String, List<Gender>> replaceDigitTags(String s) {
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

    public Gender getTrueGender(String s) {
        return getTrueGender(s, Gender.UNDEFINED);
    }

    public Gender getTrueGender(String s, Gender defaultGender) {
        defaultGender = defaultGender != null ? defaultGender : Gender.UNDEFINED;

        if (StringHelper.isNullOrEmpty(s))
            return defaultGender;

        if (s.equals("⛌"))
            return resourceFinder.getGender();

        if (s.equals("⸮"))
            return r.getBoolean() ? Gender.MASCULINE : Gender.FEMININE;

        try {
            int genderValue = Integer.parseInt(s);
            return Gender.get(genderValue);
        } catch (NumberFormatException e) {
            return defaultGender;
        }
    }

    public String fixDoubleFullStop(String s) {
        Matcher matcher = DOUBLE_FULL_STOP_PATTERN.matcher(s);
        List<Integer> positions = new ArrayList<>();

        while (matcher.find()) {
            positions.add(matcher.end());
        }

        if (positions.size() > 0) {
            for (int n = positions.size() - 1; n >= 0; n--) {
                char[] charArray = s.toCharArray();
                charArray[positions.get(n) - 1] = '\0';
                s = String.valueOf(charArray);
            }
        }
        return s;
    }

    public String formatText(String text, String format) {
        Matcher matcher = FORMAT_PATTERN.matcher(format);

        if (StringHelper.isNotNullOrBlank(text) && matcher.find()) {
            List<String> parts;

            if (format.contains(","))
                parts = Arrays.asList(format.split(Pattern.quote(",")));
            else {
                parts = new ArrayList<>();
                parts.add(format);
            }
            StringBuilder sb = new StringBuilder();
            sb.append(text);

            if (parts.contains("a"))
                sb.insert(0, "<b>").append("</a>");

            if (parts.contains("b"))
                sb.insert(0, "<b>").append("</b>");

            if (parts.contains("big"))
                sb.insert(0, "<big>").append("</big>");

            if (parts.contains("i"))
                sb.insert(0, "<i>").append("</i>");

            if (parts.contains("s"))
                sb.insert(0, "<s>").append("</s>");

            if (parts.contains("small"))
                sb.insert(0, "<small>").append("</small>");

            if (parts.contains("tt"))
                sb.insert(0, "<tt>").append("</tt>");

            if (parts.contains("u"))
                sb.insert(0, "<u>").append("</u>");
            text = sb.toString();
        }
        return text;
    }

    public String formatNumber(int number) {
        String formattedNumber;

        if (number == 0)
            formattedNumber = "<b><font color=#5E84EC>" + number + "</font></b>";
        else if (number < 0)
            formattedNumber = "<b><font color=#F94C4C>" + number + "</font></b>";
        else
            formattedNumber = "<b><font color=#2FCC2F>" + number + "</font></b>";
        return formattedNumber;
    }

    public String formatDescriptor(Person person) {
        if (person == null || StringHelper.isNullOrBlank(person.getDescriptor()))
            return "";
        String formattedDescriptor;

        if (person.hasAttribute("anonymous"))
            formattedDescriptor = formatUsername(person.getUsername());
        else
            formattedDescriptor = String.format("<font color=%s>%s</font>", resourceFinder.getColorStr(person.getSummary()), formatName(person.getDescriptor()));

        if (person.hasAttribute("requested"))
            formattedDescriptor = formatText(formattedDescriptor, "u");
        return formattedDescriptor;
    }

    public String formatName(String s) {
        if (StringHelper.isNullOrBlank(s))
            return s;
        return String.format("<font color=%s>%s</font>", resourceFinder.getColorStr(s), formatText(s, "b"));
    }

    public String formatName(Person person) {
        if (person == null || StringHelper.isNullOrBlank(person.getFullName()))
            return "";
        return String.format("%s<font color=%s>%s%s%s</font>",
                (StringHelper.isNotNullOrBlank(person.getOccupation()) ? formatText(person.getOccupation(), "i") + " " : ""),
                resourceFinder.getColorStr(person.getFullName()),
                formatText(person.getFullName(), "b"),
                StringHelper.defaultIfBlank(person.getJapaneseHonorific()),
                (StringHelper.isNotNullOrBlank(person.getPostNominalLetters()) ? ", " + formatText(person.getPostNominalLetters(), "b,i") : "")
        );
    }

    public String formatUsername(String s) {
        if (StringHelper.isNullOrBlank(s))
            return s;
        return String.format("<font color=%s>%s</font>", resourceFinder.getColorStr(s), formatText(s, "b,tt"));
    }

    public String formatContactName(String s) {
        if (StringHelper.isNullOrBlank(s))
            return s;

        if (isEmailAddress(s))
            return "<font color=#FFFFC6>" + s + "</font>";

        if (isUrl(s))
            return String.format("<font color=%s>%s</font>", resourceFinder.getColorStr(s), formatText(s, "s"));

        if (!StringHelper.containsSpace(s))
            return String.format("<font color=%s>%s</font>", resourceFinder.getColorStr(s), formatText(s, "b,tt"));
        return String.format("<font color=%s>%s</font>", resourceFinder.getColorStr(s), formatText(s, "b"));
    }

    public String formatSuggestedName(String s) {
        if (StringHelper.isNullOrBlank(s))
            return s;

        if (StringHelper.equalsIgnoreCase(s, Constant.DEVELOPER))
            return String.format("<font color=%s>%s</font>", resourceFinder.getColorStr(s), formatText(s, "b,i"));
        return String.format("<font color=%s>%s</font>", resourceFinder.getColorStr(s), formatText(s, "b"));
    }
}

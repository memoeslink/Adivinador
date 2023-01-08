package com.app.memoeslink.adivinador;

import android.content.Context;

import com.app.memoeslink.adivinador.tagprocessor.TagProcessor;
import com.memoeslink.generator.common.Binder;
import com.memoeslink.generator.common.CharHelper;
import com.memoeslink.generator.common.DateTimeHelper;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.IntegerHelper;
import com.memoeslink.generator.common.LongHelper;
import com.memoeslink.generator.common.Maker;
import com.memoeslink.generator.common.NameType;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.common.TextComponent;
import com.memoeslink.generator.common.TextFormatter;
import com.memoeslink.generator.common.TextProcessor;
import com.memoeslink.generator.common.ZeroWidthChar;

import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Divination extends Binder {
    public static final String[] COMMON_COLORS = {"#FEFF5B", "#6ABB6A", "#E55B5B", "#5B72E5", "#925BFF"};
    public static final Integer[] PROBABILITY_DISTRIBUTION = {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 5};
    private static final char separator;
    private final ResourceExplorer resourceExplorer;
    private final TagProcessor tagProcessor;

    static {
        separator = CharHelper.getFirstDisplayableGlyph('↓', '⬇', '⇣', '¦', '•');
    }

    public Divination(Context context) {
        this(context, null);
    }

    public Divination(Context context, Long seed) {
        super(context, seed);
        resourceExplorer = new ResourceExplorer(context, seed);
        tagProcessor = new TagProcessor(context, seed);
    }

    @Override
    public void bindSeed(Long seed) {
        super.bindSeed(seed);
        resourceExplorer.bindSeed(seed);
        tagProcessor.bindSeed(seed);
    }

    @Override
    public void unbindSeed() {
        super.unbindSeed();
        resourceExplorer.unbindSeed();
        tagProcessor.unbindSeed();
    }

    public Prediction getPrediction(String enquiryDate) {
        Person person;

        if (r.getInt(3) == 0)
            person = resourceExplorer.getGeneratorManager().getPersonGenerator().getAnonymousPerson();
        else
            person = resourceExplorer.getGeneratorManager().getPersonGenerator().getPerson();
        return getPrediction(person, enquiryDate);
    }

    public Prediction getEmptyPrediction() {
        Prediction prediction = new Prediction();

        String formattedContent = getString(R.string.prediction,
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "#FFFFFF", "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                "…",
                0,
                "…",
                "…",
                "…"
        );
        prediction.setFormattedContent(formattedContent);
        String content = formattedContent;
        content = SpannerHelper.fromHtml(content).toString();
        content = getString(R.string.inquiry_information, "…", "…", "…", "…") +
                System.getProperty("line.separator") + System.getProperty("line.separator") +
                content;
        prediction.setContent(content);
        return prediction;
    }

    public Prediction getPrediction(Person person, String enquiryDate) {
        Prediction prediction = new Prediction();
        prediction.setPerson(person);
        String name = person.getDescriptor();
        int birthdateYear = person.getBirthdate().getYear();
        int birthdateMonth = person.getBirthdate().getMonthValue();
        int birthdateDay = person.getBirthdate().getDayOfMonth();
        String birthdate = DateTimeHelper.toIso8601Date(birthdateYear, birthdateMonth, birthdateDay);
        Gender gender = person.getGender();

        //Get zodiac information
        ZodiacSign zodiacSign = getZodiacSign(birthdateMonth, birthdateDay);
        WalterBergZodiacSign walterBergZodiacSign = getWalterBergZodiacSign(birthdateMonth, birthdateDay);

        //Set seed
        String dailySeed = person.getSummary() + System.getProperty("line.separator") + enquiryDate;
        dailySeed = StringHelper.sha256(dailySeed);
        String uniqueSeed = person.getSha256();
        long seed = LongHelper.getSeed(dailySeed);
        long personalSeed = LongHelper.getSeed(uniqueSeed);

        //Define HashMap
        HashMap<String, String> divination = new HashMap<>();

        //Get predictionView information
        bindSeed(seed);
        divination.put("link", String.format("<a href='links/prediction'>%s</a>", getString(R.string.prediction_action)));
        divination.put("fortuneCookie", resourceExplorer.getDatabaseFinder().getFortuneCookie());
        divination.put("gibberish", "<font color=\"#ECFE5B\">" + TextFormatter.formatText(TextProcessor.turnIntoGibberish(divination.get("fortuneCookie")), "i") + "</font>");
        divination.put("divination", getDivination(enquiryDate));
        divination.put("fortuneNumbers", android.text.TextUtils.join(", ", r.getIntegers(5, 100, true)));
        divination.put("emotions", getEmotions());
        divination.put("bestTime", resourceExplorer.getGeneratorManager().getDateTimeGenerator().getStrTime());
        divination.put("worstTime", resourceExplorer.getGeneratorManager().getDateTimeGenerator().getStrTime());
        divination.put("characteristic", StringHelper.capitalizeFirst(resourceExplorer.getDatabaseFinder().getAbstractNoun()));
        divination.put("chainOfEvents", getChainOfEvents(person));
        divination.put("influence", TextFormatter.formatName(resourceExplorer.getGeneratorManager().getPersonGenerator().getPerson()));

        //Get zodiac information
        divination.put("zodiacSign", zodiacSign.getName(context) + " " + zodiacSign.getEmoji());
        divination.put("astrologicalHouse", zodiacSign.getAstrologicalHouse(context));
        divination.put("ruler", zodiacSign.getRuler(context));
        divination.put("element", zodiacSign.getElement(context));
        divination.put("signColor", getString(R.string.zodiac_color, zodiacSign.getColor(context), zodiacSign.getHexColor()));
        divination.put("signNumbers", zodiacSign.getNumbers(context));
        divination.put("compatibility", zodiacSign.getCompatibility(context));
        divination.put("incompatibility", zodiacSign.getIncompatibility(context));
        divination.put("walterBergZodiacSign", walterBergZodiacSign.getName(context) + " " + walterBergZodiacSign.getEmoji());
        divination.put("chineseZodiacSign", getChineseZodiacSign(birthdateYear));

        //Get identity information
        divination.put("color", Maker.getColor(uniqueSeed));
        divination.put("animal", StringHelper.capitalizeFirst(resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.animal)));
        divination.put("psychologicalType", resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.psychological_type));
        divination.put("secretName", TextFormatter.formatName(resourceExplorer.getGeneratorManager().getNameGenerator().getName(NameType.SECRET_NAME)));
        divination.put("demonicName", TextFormatter.formatName(TextProcessor.demonize(name, resourceExplorer.getGeneratorManager().getNameGenerator().getName(NameType.SECRET_NAME))));
        divination.put("previousName", TextFormatter.formatName(resourceExplorer.getGeneratorManager().getPersonGenerator().getPerson()));
        divination.put("futureName", TextFormatter.formatName(resourceExplorer.getGeneratorManager().getPersonGenerator().getPerson()));
        divination.put("recommendedUsername", TextFormatter.formatUsername(resourceExplorer.getGeneratorManager().getNameGenerator().getUsername()));
        divination.put("digit", String.valueOf(r.getInt(10)));
        divination.put("uniqueNumbers", android.text.TextUtils.join(", ", r.getIntegers(3, 1000, true)));
        divination.put("daysBetweenDates", String.valueOf(DateTimeHelper.getDifferenceInDays(birthdate, enquiryDate)));
        divination.put("timeBetweenDates", getDateDifference(birthdate, enquiryDate));
        unbindSeed();

        String content = getString(R.string.prediction,
                divination.get("fortuneCookie"),
                divination.get("divination"),
                divination.get("fortuneNumbers"),
                divination.get("emotions"),
                divination.get("worstTime"),
                divination.get("bestTime"),
                divination.get("characteristic"),
                divination.get("chainOfEvents"),
                divination.get("influence"),
                divination.get("zodiacSign"),
                divination.get("astrologicalHouse"),
                divination.get("ruler"),
                divination.get("element"),
                zodiacSign.getColor(context),
                divination.get("signNumbers"),
                divination.get("compatibility"),
                divination.get("incompatibility"),
                divination.get("walterBergZodiacSign"),
                divination.get("chineseZodiacSign"),
                "#FFFFFF", divination.get("color"),
                divination.get("animal"),
                divination.get("psychologicalType"),
                divination.get("secretName"),
                divination.get("demonicName"),
                divination.get("previousName"),
                divination.get("futureName"),
                divination.get("recommendedUsername"),
                Integer.valueOf(divination.get("digit")),
                divination.get("uniqueNumbers"),
                divination.get("daysBetweenDates"),
                divination.get("timeBetweenDates")
        );
        content = SpannerHelper.fromHtml(content).toString();
        content = getString(R.string.inquiry_information, enquiryDate, name, gender.getName(context, 1), birthdate) +
                System.getProperty("line.separator") + System.getProperty("line.separator") +
                content;
        prediction.setContent(content);

        String formattedContent = getString(R.string.prediction,
                ZeroWidthChar.ZERO_WIDTH_SPACE.getCharacter() + divination.get("link") + "<br>" +
                        divination.get("gibberish") + ZeroWidthChar.ZERO_WIDTH_SPACE.getCharacter(),
                divination.get("divination"),
                divination.get("fortuneNumbers"),
                divination.get("emotions"),
                divination.get("worstTime"),
                divination.get("bestTime"),
                divination.get("characteristic"),
                divination.get("chainOfEvents"),
                divination.get("influence"),
                divination.get("zodiacSign"),
                divination.get("astrologicalHouse"),
                divination.get("ruler"),
                divination.get("element"),
                divination.get("signColor"),
                divination.get("signNumbers"),
                divination.get("compatibility"),
                divination.get("incompatibility"),
                divination.get("walterBergZodiacSign"),
                divination.get("chineseZodiacSign"),
                divination.get("color"), "⬛&#xFE0E;",
                divination.get("animal"),
                divination.get("psychologicalType"),
                divination.get("secretName"),
                divination.get("demonicName"),
                divination.get("previousName"),
                divination.get("futureName"),
                divination.get("recommendedUsername"),
                Integer.valueOf(divination.get("digit")),
                divination.get("uniqueNumbers"),
                divination.get("daysBetweenDates"),
                divination.get("timeBetweenDates")
        );
        prediction.setFormattedContent(formattedContent);
        prediction.setFortuneCookie(divination.get("fortuneCookie"));
        prediction.setUnrevealedFortuneCookie(divination.get("gibberish"));
        prediction.getPerson().setDescription(TextFormatter.formatDescriptor(person));
        return prediction;
    }

    public String getDivination(String enquiryDate) {
        enquiryDate = StringHelper.defaultIfBlank(enquiryDate, DateTimeHelper.getStrCurrentDate());
        String divination;
        float probability = r.getFloat();
        Gender gender = Gender.UNDEFINED;

        if (probability <= 0.4F) {
            divination = resourceExplorer.getDatabaseFinder().getDivination();
            divination = tagProcessor.replaceTags(divination).getText();
        } else if (probability <= 0.8F) {
            boolean plural = false;
            boolean done = false;
            TextComponent component;
            String segment;

            //Get segments for the divination
            List<String> segments = new ArrayList<>();
            segments.add(resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination_start));
            segments.add(resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination_middle));
            segments.add(resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination_end));
            segments.add(resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination_cause));

            //Format start of divination
            segment = segments.get(0);
            int days = r.getGaussianInt(5, 7, 1);
            segment = StringHelper.replaceOnce(segment, "‽1", String.valueOf(days));
            segment = StringHelper.replaceOnce(segment, "‽2", DateTimeHelper.getStrDatePlusDays(enquiryDate, days));
            segments.set(0, segment);

            //Format middle of divination
            segment = segments.get(1);
            segment = (component = tagProcessor.replaceTags(segment)).getText();

            if (component.getHegemonicGender() != null && component.getHegemonicGender() != Gender.UNDEFINED)
                gender = component.getHegemonicGender();

            if (StringHelper.startsWithAny(segment, "tus ", "los ", "las "))
                plural = true;
            segments.set(1, segment);

            //Format end of divination
            segment = segments.get(2);
            segment = tagProcessor.replaceTags(segment, gender, plural).getText();
            segments.set(2, segment);

            //Format cause of divination
            segment = segments.get(3);

            if (!segment.isEmpty())
                segment = tagProcessor.replaceTags(segment).getText();
            segments.set(3, segment);

            //Make some replacements and truncate divination if needed
            for (int n = 0, size = segments.size(); n < size; n++) {
                if (done)
                    segments.set(n, "");
                else {
                    segment = segments.get(n);

                    if (StringHelper.endsWith(segment, '꘎')) {
                        segment = StringHelper.remove(segment, "꘎");
                        done = true;
                    }
                    segments.set(n, segment);
                }
            }
            segments.removeAll(Collections.singleton(""));
            divination = StringHelper.joinWithSpace(segments);
        } else
            divination = tagProcessor.replaceTags(resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination)).getText();
        return divination;
    }

    public String getEmotions() {
        String emotionDetails = "";
        HashMap<Emotion, Integer> emotionCount = new HashMap<>();

        for (Emotion emotion : Emotion.values()) {
            emotionCount.put(emotion, 0);
        }
        int points, remainingPoints = 100;

        while (remainingPoints > 0) {
            points = r.getInt(remainingPoints + 1);
            remainingPoints -= points;
            Emotion currentEmotion = r.getElement(Emotion.values());
            int currentCount = emotionCount.getOrDefault(currentEmotion, 0);
            emotionCount.put(currentEmotion, currentCount + points);
        }

        for (Map.Entry<Emotion, Integer> entry : emotionCount.entrySet()) {
            if (entry.getValue() > 0)
                emotionDetails = StringHelper.appendIfNotEmpty(emotionDetails, "<br>") +
                        String.format("%s %s (%d%%)",
                                entry.getKey().getName(context),
                                entry.getKey().getEmoji(),
                                entry.getValue());
        }
        return emotionDetails;
    }

    public String getChineseZodiacSign(int year) {
        return resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.chinese_zodiac_sign, year % 12);
    }

    public String getChainOfEvents(Person person) {
        StringBuilder chain = new StringBuilder();
        List<Person> people = new ArrayList<>();

        //Add unknown people
        char letter = 'A';

        for (int n = -1, limit = r.getElement(PROBABILITY_DISTRIBUTION); ++n < limit; ) {
            Gender gender = r.getBoolean() ? Gender.MASCULINE : Gender.FEMININE;
            String description = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.person, 0) + " " + String.format("<font color=\"%s\">%s</font>", COMMON_COLORS[n], TextFormatter.formatText(Character.toString(letter), "b", "i"));
            description += ", " + resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.probability) + " " + tagProcessor.replaceTags(resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.individual), gender, false).getText();
            people.add(new Person.PersonBuilder()
                    .setNickname(Character.toString(letter))
                    .setGender(gender)
                    .setDescription(description)
                    .setAttribute("nonspecific")
                    .setAttribute("unknown")
                    .build());
            letter++;
        }

        //Add identified people
        for (int n = -1, limit = r.getInt(3, 8); ++n < limit; ) {
            Person identifiedPerson;

            if (r.getInt(4) > 0) {
                identifiedPerson = resourceExplorer.getGeneratorManager().getPersonGenerator().getPerson();
                identifiedPerson.setDescription(TextFormatter.formatName(identifiedPerson.getFullName()));
            } else {
                identifiedPerson = resourceExplorer.getGeneratorManager().getPersonGenerator().getAnonymousPerson();
                identifiedPerson.setDescription(TextFormatter.formatUsername(identifiedPerson.getUsername()));
            }
            people.add(identifiedPerson);
        }

        //Add close people
        int arrayLength = resourceExplorer.getResourceFinder().getArrayResLength(R.array.person);
        int closePersonType = r.getInt(1, arrayLength - 1);
        int thirdPartyType = closePersonType <= 9 ? r.getInt(11, arrayLength - 11) : r.getInt(1, arrayLength - 1);
        Person closePerson = getClosePerson(person, closePersonType);
        Person thirdParty = getThirdParty(closePerson, thirdPartyType);
        people.add(thirdParty);
        people.add(closePerson);

        //Add oneself
        people.add(person);

        //Add gender glyph to people's descriptions
        for (Person currentPerson : people) {
            String description = currentPerson.getDescription();

            if ((currentPerson.hasAttribute("generated") || currentPerson.hasAttribute("nonspecific")) && StringHelper.isNotNullOrBlank(description)) {
                description += " (<font color=\"#B599FC\">" + TextFormatter.formatText(currentPerson.getGender().getGlyph(), "b") + "</font>)";
                currentPerson.setDescription(description);
            }
        }

        //Define chain links
        int totalKarma = 0;

        for (int n = 0; n < people.size() - 1; n++) {
            int karma = r.getInt(-10, 21);
            totalKarma = totalKarma + karma;
            String chainLink = getString(R.string.chain_link,
                    n + 1,
                    people.get(n).getDescription(),
                    people.get(n).hasAttribute("unknown") ? "," : "",
                    people.get(n + 1).getDescription(),
                    TextFormatter.formatNumber(karma),
                    TextFormatter.formatNumber(totalKarma));
            chainLink = tagProcessor.replaceTags(chainLink).getText();
            chain.append(chainLink);

            //Define link delimiter
            if (separator != '\u0000') //\u0000 is \0
                chain.append("<br><font color=\"#A0A8C7\">").append(separator).append("</font><br>");
            else
                chain.append("<br>");
        }
        float percentage = (float) totalKarma / ((people.size() - 1) * 10) * 100;
        String effect;

        if (percentage == 0)
            effect = getString(R.string.value_as_null);
        else if (percentage > 0)
            effect = getString(R.string.value_as_positive);
        else
            effect = getString(R.string.value_as_negative);
        chain.append(getString(R.string.chain_effect, person.getDescription(), effect, TextFormatter.formatPercentage(percentage)));
        return getString(R.string.html_format, chain.toString());
    }

    private Person getThirdParty(Person closePerson, int index) {
        TextComponent thirdParty = tagProcessor.replaceTags("{string:relationship[" + index + "]; gender:⸮}");
        thirdParty.setText(String.format(thirdParty.getText(), closePerson.getDescription()));
        return new Person.PersonBuilder()
                .setGender(thirdParty.getHegemonicGender())
                .setDescription(thirdParty.getText())
                .setAttribute("nonspecific")
                .build();
    }

    private Person getClosePerson(Person person, int index) {
        person.setDescription(TextFormatter.formatDescriptor(person));
        TextComponent closePerson = new TextComponent();
        int listSize = IntegerHelper.defaultInt(resourceExplorer.getContactNameFinder().getContactNamesSize(), 0, 1000);
        double probability = 0.1F + (0.5F / 1000 * listSize);

        if (person.hasAttribute("entered") && r.getFloat() <= probability && listSize > 0) {
            closePerson.setText(TextFormatter.formatContactName(resourceExplorer.getContactNameFinder().getContactName()));
            closePerson.setHegemonicGender(Gender.UNDEFINED);

            if (closePerson.getText().isEmpty())
                closePerson.setText("?");
        } else {
            closePerson = tagProcessor.replaceTags("{string:relationship[" + index + "]; gender:⸮}");
            closePerson.setText(String.format(closePerson.getText(), person.getDescription()));
        }
        return new Person.PersonBuilder()
                .setGender(closePerson.getHegemonicGender())
                .setDescription(closePerson.getText())
                .setAttribute("nonspecific")
                .build();
    }

    public String getDateDifference(String firstDate, String secondDate) {
        Period period = DateTimeHelper.getTimeBetweenDates(firstDate, secondDate);
        return getString(R.string.time_elapsed, period.getYears(), period.getMonths(), period.getDays());
    }

    public ZodiacSign getZodiacSign(int month, int day) {
        switch (month) {
            case 1:
                if (day >= 1 && day <= 31)
                    return day >= 20 ? ZodiacSign.AQUARIUS : ZodiacSign.CAPRICORN;
                break;
            case 2:
                if (day >= 1 && day <= 29)
                    return day >= 19 ? ZodiacSign.PISCES : ZodiacSign.AQUARIUS;
                break;
            case 3:
                if (day >= 1 && day <= 31)
                    return day >= 21 ? ZodiacSign.ARIES : ZodiacSign.PISCES;
                break;
            case 4:
                if (day >= 1 && day <= 30)
                    return day >= 21 ? ZodiacSign.TAURUS : ZodiacSign.ARIES;
                break;
            case 5:
                if (day >= 1 && day <= 31)
                    return day >= 22 ? ZodiacSign.GEMINI : ZodiacSign.TAURUS;
                break;
            case 6:
                if (day >= 1 && day <= 30)
                    return day >= 21 ? ZodiacSign.CANCER : ZodiacSign.GEMINI;
                break;
            case 7:
                if (day >= 1 && day <= 31)
                    return day >= 23 ? ZodiacSign.LEO : ZodiacSign.CANCER;
                break;
            case 8:
                if (day >= 1 && day <= 31)
                    return day >= 23 ? ZodiacSign.VIRGO : ZodiacSign.LEO;
                break;
            case 9:
                if (day >= 1 && day <= 30)
                    return day >= 23 ? ZodiacSign.LIBRA : ZodiacSign.VIRGO;
                break;
            case 10:
                if (day >= 1 && day <= 31)
                    return day >= 23 ? ZodiacSign.SCORPIO : ZodiacSign.LIBRA;
                break;
            case 11:
                if (day >= 1 && day <= 30)
                    return day >= 22 ? ZodiacSign.SAGITTARIUS : ZodiacSign.SCORPIO;
                break;
            case 12:
                if (day >= 1 && day <= 31)
                    return day >= 22 ? ZodiacSign.CAPRICORN : ZodiacSign.SAGITTARIUS;
                break;
        }
        return ZodiacSign.UNKNOWN;
    }

    public WalterBergZodiacSign getWalterBergZodiacSign(int month, int day) {
        switch (month) {
            case 1:
                if (day >= 1 && day <= 31)
                    return day >= 19 ? WalterBergZodiacSign.CAPRICORN : WalterBergZodiacSign.SAGITTARIUS;
                break;
            case 2:
                if (day >= 1 && day <= 29)
                    return day >= 16 ? WalterBergZodiacSign.AQUARIUS : WalterBergZodiacSign.CAPRICORN;
                break;
            case 3:
                if (day >= 1 && day <= 31)
                    return day >= 12 ? WalterBergZodiacSign.PISCES : WalterBergZodiacSign.AQUARIUS;
                break;
            case 4:
                if (day >= 1 && day <= 30)
                    return day >= 19 ? WalterBergZodiacSign.ARIES : WalterBergZodiacSign.PISCES;
                break;
            case 5:
                if (day >= 1 && day <= 31)
                    return day >= 14 ? WalterBergZodiacSign.TAURUS : WalterBergZodiacSign.ARIES;
                break;
            case 6:
                if (day >= 1 && day <= 30)
                    return day >= 20 ? WalterBergZodiacSign.GEMINI : WalterBergZodiacSign.TAURUS;
                break;
            case 7:
                if (day >= 1 && day <= 31)
                    return day >= 21 ? WalterBergZodiacSign.CANCER : WalterBergZodiacSign.GEMINI;
                break;
            case 8:
                if (day >= 1 && day <= 31)
                    return day >= 10 ? WalterBergZodiacSign.LEO : WalterBergZodiacSign.CANCER;
                break;
            case 9:
                if (day >= 1 && day <= 30)
                    return day >= 16 ? WalterBergZodiacSign.VIRGO : WalterBergZodiacSign.LEO;
                break;
            case 10:
                if (day >= 1 && day <= 31)
                    return day >= 31 ? WalterBergZodiacSign.LIBRA : WalterBergZodiacSign.VIRGO;
                break;
            case 11:
                if (day >= 1 && day <= 30)
                    return day >= 30 ? WalterBergZodiacSign.OPHIUCHUS : day >= 23 ? WalterBergZodiacSign.SCORPIO : WalterBergZodiacSign.LIBRA;
                break;
            case 12:
                if (day >= 1 && day <= 31)
                    return day >= 18 ? WalterBergZodiacSign.SAGITTARIUS : WalterBergZodiacSign.OPHIUCHUS;
                break;
        }
        return WalterBergZodiacSign.UNKNOWN;
    }
}

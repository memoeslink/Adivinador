package com.app.memoeslink.adivinador;

import android.content.Context;

import com.memoeslink.generator.common.Binder;
import com.memoeslink.generator.common.CharHelper;
import com.memoeslink.generator.common.DateTimeHelper;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.IntegerHelper;
import com.memoeslink.generator.common.LongHelper;
import com.memoeslink.generator.common.Maker;
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
        separator = CharHelper.getFirstDisplayableGlyph('↓', '⬇', '⇣', '•');
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
            person = resourceExplorer.getReflectionFinder().getAnonymousPerson();
        else
            person = resourceExplorer.getReflectionFinder().getPerson();
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
        content = getString(R.string.enquiry_information, "…", "…", "…", "…") +
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
        divination.put("gibberish", "<font color=#ECFE5B>" + TextFormatter.formatText(TextProcessor.turnIntoGibberish(divination.get("fortuneCookie")), "i,tt") + "</font>");
        divination.put("divination", generateDivination(enquiryDate));
        divination.put("fortuneNumbers", android.text.TextUtils.join(", ", r.getIntegers(5, 100, true)));
        divination.put("emotions", getEmotions());
        divination.put("bestTime", resourceExplorer.getReflectionFinder().getTime());
        divination.put("worstTime", resourceExplorer.getReflectionFinder().getTime());
        divination.put("characteristic", StringHelper.capitalizeFirst(resourceExplorer.getDatabaseFinder().getAbstractNoun()));
        divination.put("chainOfEvents", getChainOfEvents(person));
        divination.put("influence", TextFormatter.formatName(resourceExplorer.getReflectionFinder().getPerson()));

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
        divination.put("secretName", TextFormatter.formatName(resourceExplorer.getReflectionFinder().getSecretName()));
        divination.put("demonicName", TextFormatter.formatName(TextProcessor.demonize(name, resourceExplorer.getReflectionFinder().getSecretName())));
        divination.put("previousName", TextFormatter.formatName(resourceExplorer.getReflectionFinder().getPerson()));
        divination.put("futureName", TextFormatter.formatName(resourceExplorer.getReflectionFinder().getPerson()));
        divination.put("recommendedUsername", TextFormatter.formatUsername(resourceExplorer.getReflectionFinder().getUsername()));
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
        content = getString(R.string.enquiry_information, enquiryDate, name, resourceExplorer.findGenderName(gender, 1), birthdate) +
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

    public String generateDivination(String enquiryDate) {
        enquiryDate = StringHelper.defaultIfBlank(enquiryDate, DateTimeHelper.getStrCurrentDate());
        String divination = "?";
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

            //Define indices
            List<Integer> indices = new ArrayList<>();
            indices.add(r.getInt(15) > 0 ? r.getInt(1, resourceExplorer.getResourceFinder().getArrayResLength(R.array.divination_start) - 1) : 0);
            indices.add(r.getInt(resourceExplorer.getResourceFinder().getArrayResLength(R.array.divination_middle)));
            indices.add(r.getInt(resourceExplorer.getResourceFinder().getArrayResLength(R.array.divination_end) - (indices.get(1) > 0 ? 0 : 10)));
            indices.add(resourceExplorer.getResourceFinder().getArrayResLength(R.array.divination_cause) - 1 - indices.get(2) <= 1 ? 0 : r.getInt(resourceExplorer.getResourceFinder().getArrayResLength(R.array.divination_cause)));

            //Get segments for the divination
            List<String> segments = new ArrayList<>();
            segments.add(resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination_start, indices.get(0)));
            segments.add(resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination_middle, indices.get(1)));
            segments.add(resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination_end, indices.get(2)));
            segments.add(resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination_cause, indices.get(3)));

            //Format start of divination
            int days = r.getGaussianInt(5, 7, 1);
            segment = StringHelper.replaceOnce(segments.get(0), "‽1", String.valueOf(days));
            segment = StringHelper.replaceOnce(segment, "‽2", DateTimeHelper.getStrDatePlusDays(enquiryDate, days));
            segments.set(0, segment);

            //Format middle of divination
            segment = (component = tagProcessor.replaceTags(segments.get(1))).getText();

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

            if (!segment.isEmpty()) {
                segment = tagProcessor.replaceTags(segment).getText();

                if (segment.contains("%%"))
                    segment = StringHelper.replace(segment, "%%", tagProcessor.replaceTags("{string:unspecific_person; gender:⸮}").getText());
                segment = StringHelper.replace(segment, " a el ", " al ");
            }
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
        } else {
            divination = tagProcessor.replaceTags(resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination)).getText();
            divination = StringHelper.replace(divination, " de el ", " del ");
        }
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
            String description = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.person, 0) + " " + String.format("<font color=%s>%s</font>", COMMON_COLORS[n], TextFormatter.formatText(Character.toString(letter), "b,i,tt"));
            description += ", " + resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.probability) + " " + TextProcessor.genderifyStr(resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.individual), gender).getText();
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
                identifiedPerson = resourceExplorer.getReflectionFinder().getPerson();
                identifiedPerson.setDescription(TextFormatter.formatName(identifiedPerson.getFullName()));
            } else {
                identifiedPerson = resourceExplorer.getReflectionFinder().getAnonymousPerson();
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
                description += " (<font color=#B599FC>" + TextFormatter.formatText(currentPerson.getGender().getGlyph(), "b") + "</font>)";
                currentPerson.setDescription(description);
            }
        }

        //Define chain links
        int totalKarma = 0;

        for (int n = 0; n < people.size() - 1; n++) {
            int karma = r.getInt(-10, 21);
            totalKarma = totalKarma + karma;

            if (people.get(n).hasAttribute("unknown"))
                chain.append(getString(R.string.chain_link, n + 1, people.get(n).getDescription(), ",", ",", resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.uncertainty), people.get(n + 1).getDescription(), TextFormatter.formatNumber(karma), TextFormatter.formatNumber(totalKarma)));
            else
                chain.append(getString(R.string.chain_link, n + 1, people.get(n).getDescription(), "", "", "(" + resourceExplorer.getDatabaseFinder().getAction() + ")", people.get(n + 1).getDescription(), TextFormatter.formatNumber(karma), TextFormatter.formatNumber(totalKarma)));

            //Define link delimiter
            if (separator != '\0')
                chain.append("<br><font color=#A0A8C7>").append(separator).append("</font><br>");
            else
                chain.append("<br>");
        }
        float percentage = (float) totalKarma / (people.size() - 1) * 10;
        String effect = TextFormatter.formatPercentageWithText(percentage);
        chain.append(getString(R.string.chain_effect, effect, person.getDescription()));
        chain = new StringBuilder(StringHelper.replace(chain.toString(), " a el ", " al "));
        return getString(R.string.html_format, chain.toString());
    }

    public Person getThirdParty(Person person) {
        Person closePerson = getClosePerson(person);
        int index = r.getInt(1, resourceExplorer.getResourceFinder().getArrayResLength(R.array.person) - 1);
        return getThirdParty(closePerson, index);
    }

    private Person getThirdParty(Person closePerson, int index) {
        TextComponent thirdParty = tagProcessor.replaceTags("{string:person[" + index + "]; gender:⸮}");
        thirdParty.setText(StringHelper.replace(thirdParty.getText(), "%%", closePerson.getDescription()));
        thirdParty.setText(StringHelper.replace(thirdParty.getText(), " de el ", " del "));
        return new Person.PersonBuilder()
                .setGender(thirdParty.getHegemonicGender())
                .setDescription(thirdParty.getText())
                .setAttribute("nonspecific")
                .build();
    }

    public Person getClosePerson(Person person) {
        int index = r.getInt(1, resourceExplorer.getResourceFinder().getArrayResLength(R.array.person) - 1);
        return getClosePerson(person, index);
    }

    private Person getClosePerson(Person person, int index) {
        person.setDescription(TextFormatter.formatDescriptor(person));
        TextComponent closePerson = new TextComponent();
        int listSize = IntegerHelper.defaultInt(resourceExplorer.getContactNameFinder().getContactNamesSize(), 0, 1000);
        double probability = 0.1F + (0.5F / 1000 * listSize);

        if (person.hasAttribute("entered") && r.getFloat() <= probability && listSize > 0) {
            closePerson.setText(resourceExplorer.getContactNameFinder().getContactName());
            closePerson.setHegemonicGender(Gender.UNDEFINED);

            if (closePerson.getText().isEmpty())
                closePerson.setText("?");
        } else {
            closePerson = tagProcessor.replaceTags("{string:person[" + index + "]; gender:⸮}");
            closePerson.setText(StringHelper.replace(closePerson.getText(), "%%", person.getDescription()));
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
        if ((month == 1 && day >= 20) || (month == 2 && day <= 18))
            return ZodiacSign.AQUARIUS;
        else if ((month == 2 && day >= 19) || (month == 3 && day <= 20))
            return ZodiacSign.PISCES;
        else if ((month == 3 && day >= 21) || (month == 4 && day <= 20))
            return ZodiacSign.ARIES;
        else if ((month == 4 && day >= 21) || (month == 5 && day <= 21))
            return ZodiacSign.TAURUS;
        else if ((month == 5 && day >= 22) || (month == 6 && day <= 20))
            return ZodiacSign.GEMINI;
        else if ((month == 6 && day >= 21) || (month == 7 && day <= 22))
            return ZodiacSign.CANCER;
        else if ((month == 7 && day >= 23) || (month == 8 && day <= 22))
            return ZodiacSign.LEO;
        else if ((month == 8 && day >= 23) || (month == 9 && day <= 22))
            return ZodiacSign.VIRGO;
        else if ((month == 9 && day >= 23) || (month == 10 && day <= 22))
            return ZodiacSign.LIBRA;
        else if ((month == 10 && day >= 23) || (month == 11 && day <= 21))
            return ZodiacSign.SCORPIO;
        else if ((month == 11 && day >= 22) || (month == 12 && day <= 21))
            return ZodiacSign.SAGITTARIUS;
        else if ((month == 12 && day >= 22) || (month == 1 && day <= 19))
            return ZodiacSign.CAPRICORN;
        else
            return ZodiacSign.UNKNOWN;
    }

    public WalterBergZodiacSign getWalterBergZodiacSign(int month, int day) {
        if ((month == 2 && day >= 16) || (month == 3 && day <= 11))
            return WalterBergZodiacSign.AQUARIUS;
        else if ((month == 3 && day >= 12) || (month == 4 && day <= 18))
            return WalterBergZodiacSign.PISCES;
        else if ((month == 4 && day >= 19) || (month == 5 && day <= 13))
            return WalterBergZodiacSign.ARIES;
        else if ((month == 5 && day >= 14) || (month == 6 && day <= 20))
            return WalterBergZodiacSign.TAURUS;
        else if ((month == 6 && day >= 21) || (month == 7 && day <= 19))
            return WalterBergZodiacSign.GEMINI;
        else if ((month == 7 && day >= 20) || (month == 8 && day <= 19))
            return WalterBergZodiacSign.CANCER;
        else if ((month == 8 && day >= 20) || (month == 9 && day <= 15))
            return WalterBergZodiacSign.LEO;
        else if ((month == 9 && day >= 16) || (month == 10 && day <= 30))
            return WalterBergZodiacSign.VIRGO;
        else if ((month == 10 && day >= 31) || (month == 11 && day <= 22))
            return WalterBergZodiacSign.LIBRA;
        else if ((month == 11 && day >= 23) || (month == 11 && day <= 29))
            return WalterBergZodiacSign.SCORPIO;
        else if ((month == 11 && day >= 30) || (month == 12 && day <= 17))
            return WalterBergZodiacSign.OPHIUCHUS;
        else if ((month == 12 && day >= 18) || (month == 1 && day <= 8))
            return WalterBergZodiacSign.SAGITTARIUS;
        else if ((month == 1 && day >= 9) || (month == 2 && day <= 15))
            return WalterBergZodiacSign.CAPRICORN;
        else
            return WalterBergZodiacSign.UNKNOWN;
    }
}

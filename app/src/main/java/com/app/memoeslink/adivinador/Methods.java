package com.app.memoeslink.adivinador;

import android.content.Context;

import com.memoeslink.generator.common.CharHelper;
import com.memoeslink.generator.common.DateTimeHelper;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.IntegerHelper;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.Randomizer;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.common.TextComponent;
import com.memoeslink.generator.common.TextProcessor;

import java.time.Period;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Methods extends TextFormatter {
    public static final String[] COMMON_COLORS = {"#FEFF5B", "#6ABB6A", "#E55B5B", "#5B72E5", "#925BFF"};
    public static final Integer[] PROBABILITY_DISTRIBUTION = {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 5};

    public Methods(Context context) {
        super(context);
        r = new Randomizer();
    }

    public Methods(Context context, Long seed) {
        super(context, seed);
    }

    //================================================================================
    // Default methods
    //================================================================================

    public String generateDivination() {
        String divination = "?";
        float probability = r.getFloat();
        Gender gender = Gender.UNDEFINED;

        if (probability <= 0.4F) {
            divination = getDivination();
            divination = replaceTags(divination).getText();
        } else if (probability <= 0.8F) {
            int days = 0;
            boolean plural = false;
            boolean done = false;
            TextComponent component;
            String segment;

            //Define indices
            List<Integer> indices = new ArrayList<>();
            indices.add(r.getInt(15) > 0 ? r.getInt(1, getArrayResLength(R.array.divination_start) - 1) : 0);
            indices.add(r.getInt(getArrayResLength(R.array.divination_middle)));
            indices.add(r.getInt(getArrayResLength(R.array.divination_end) - (indices.get(1) > 0 ? 0 : 10)));
            indices.add(getArrayResLength(R.array.divination_cause) - 1 - indices.get(2) <= 1 ? 0 : r.getInt(getArrayResLength(R.array.divination_cause)));

            //Get segments for the divination
            List<String> segments = new ArrayList<>();
            segments.add(getStrFromStrArrayRes(R.array.divination_start, indices.get(0)));
            segments.add(getStrFromStrArrayRes(R.array.divination_middle, indices.get(1)));
            segments.add(getStrFromStrArrayRes(R.array.divination_end, indices.get(2)));
            segments.add(getStrFromStrArrayRes(R.array.divination_cause, indices.get(3)));

            //Format start of divination
            segment = StringHelper.replaceOnce(segments.get(0), "‽1", String.valueOf(days = r.getGaussianInt(5, 7, 1)));
            segment = StringHelper.replaceOnce(segment, "‽2", DateTimeHelper.getStrDatePlusDays(preferences.getString("temp_enquiryDate", DateTimeHelper.getStrCurrentDate()), days));
            segments.set(0, segment);
            preferences.remove("temp_enquiryDate");

            //Format middle of divination
            segment = (component = replaceTags(segments.get(1))).getText();

            if (component.getHegemonicGender() != null && component.getHegemonicGender() != Gender.UNDEFINED)
                gender = component.getHegemonicGender();

            if (StringHelper.startsWithAny(segment, "tus", "los", "las"))
                plural = true;
            segments.set(1, segment);

            //Format end of divination
            segment = segments.get(2);
            segment = StringHelper.replace(segment, "＃", String.valueOf(gender.getValue()));
            segment = replaceTags(segment).getText();

            if (getString(R.string.locale).equals("es"))
                segment = !plural ? StringHelper.removeAll(segment, "¦\\p{L}+¦") : StringHelper.remove(segment, "¦");
            segments.set(2, segment);

            //Format cause of divination
            segment = segments.get(3);

            if (!segment.isEmpty()) {
                segment = replaceTags(segment).getText();

                if (segment.contains("%%"))
                    segment = StringHelper.replace(segment, "%%", replaceTags("{string:unspecific_person⸻⸮}").getText());
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
            divination = replaceTags(getStrFromStrArrayRes(R.array.divination)).getText();
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
        return getStrFromStrArrayRes(R.array.chinese_zodiac_sign, year % 12);
    }

    public String getChainOfEvents(Person person) {
        StringBuilder chain = new StringBuilder();
        List<Person> people = new ArrayList<>();

        //Add unknown people
        char letter = 'A';

        for (int n = -1, limit = r.getElement(PROBABILITY_DISTRIBUTION); ++n < limit; ) {
            Gender gender = r.getBoolean() ? Gender.MASCULINE : Gender.FEMININE;
            String description = getStrFromStrArrayRes(R.array.person, 0) + " " + String.format("<font color=%s>%s</font>", COMMON_COLORS[n], formatText(Character.toString(letter), "b,i,tt"));
            description += ", " + getStrFromStrArrayRes(R.array.probability) + " " + TextProcessor.genderifyStr(getStrFromStrArrayRes(R.array.individual), gender).getText();
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
                identifiedPerson = getPerson();
                identifiedPerson.setDescription(formatName(identifiedPerson.getFullName()));
            } else {
                identifiedPerson = getAnonymousPerson();
                identifiedPerson.setDescription(formatUsername(identifiedPerson.getUsername()));
            }
            people.add(identifiedPerson);
        }

        //Add close people
        int arrayLength = getArrayResLength(R.array.person);
        int closePersonType = r.getInt(1, arrayLength - 1);
        int thirdPartyType = closePersonType <= 9 ? r.getInt(11, arrayLength - 11) : r.getInt(1, arrayLength - 1);
        Person closePerson = getClosePerson(person, closePersonType);
        Person thirdParty = getThirdParty(person, closePerson, thirdPartyType);
        people.add(thirdParty);
        people.add(closePerson);

        //Add oneself
        people.add(person);

        //Add gender glyph to people's descriptions
        for (Person currentPerson : people) {
            String description = currentPerson.getDescription();

            if ((currentPerson.hasAttribute("generated") || currentPerson.hasAttribute("nonspecific")) && StringHelper.isNotNullOrBlank(description)) {
                description += " (<font color=#B599FC>" + formatText(currentPerson.getGender().getGlyph(), "b") + "</font>)";
                currentPerson.setDescription(description);
            }
        }

        //Define chain links
        int totalKarma = 0;

        for (int n = 0; n < people.size() - 1; n++) {
            int karma = r.getInt(-10, 21);
            totalKarma = totalKarma + karma;

            if (people.get(n).hasAttribute("unknown"))
                chain.append(getString(R.string.chain_link, n + 1, people.get(n).getDescription(), ",", ",", getStrFromStrArrayRes(R.array.uncertainty), people.get(n + 1).getDescription(), formatNumber(karma), formatNumber(totalKarma)));
            else
                chain.append(getString(R.string.chain_link, n + 1, people.get(n).getDescription(), "", "", "(" + getAction() + ")", people.get(n + 1).getDescription(), formatNumber(karma), formatNumber(totalKarma)));

            //Define link delimiter
            if (CharHelper.isGlyphDisplayable('↓'))
                chain.append("<br><font color=#A0A8C7>↓</font><br>");
            else
                chain.append("<br>");
        }
        float percentage = (float) Math.abs(totalKarma) / (people.size() - 1) * 10;
        String effect;

        if (totalKarma == 0)
            effect = getString(R.string.chain_null_effect);
        else if (totalKarma < 0)
            effect = getString(R.string.chain_negative_effect);
        else
            effect = getString(R.string.chain_positive_effect);
        chain.append(getString(R.string.chain_effect, "<b><font color=#FFF870>" + (totalKarma == 0 ? effect : String.format("%.2f", percentage) + "% " + effect) + "</font></b>", person.getDescription()));
        chain = new StringBuilder(StringHelper.replace(chain.toString(), " a el ", " al "));
        return getString(R.string.html_format, chain.toString());
    }

    public Person getThirdParty(Person person) {
        Person closePerson = getClosePerson(person);
        int index = r.getInt(1, getArrayResLength(R.array.person) - 1);
        return getThirdParty(person, closePerson, index);
    }

    private Person getThirdParty(Person person, Person closePerson, int index) {
        TextComponent thirdParty = replaceTags("{string:person[" + index + "]⸻⸮}");
        thirdParty.setText(StringHelper.replace(thirdParty.getText(), "%%", closePerson.getDescription()));
        thirdParty.setText(StringHelper.replace(thirdParty.getText(), " de el ", " del "));
        return new Person.PersonBuilder()
                .setGender(thirdParty.getHegemonicGender())
                .setDescription(thirdParty.getText())
                .setAttribute("nonspecific")
                .build();
    }

    public Person getClosePerson(Person person) {
        int index = r.getInt(1, getArrayResLength(R.array.person) - 1);
        return getClosePerson(person, index);
    }

    private Person getClosePerson(Person person, int index) {
        person.setDescription(formatDescriptor(person));
        TextComponent closePerson = new TextComponent();
        int listSize = IntegerHelper.defaultInt(getContactNamesSize(), 0, 1000);
        double probability = 0.1F + (0.5F / 1000 * listSize);

        if (person.hasAttribute("entered") && r.getFloat() <= probability && listSize > 0) {
            closePerson.setText(getContactName());
            closePerson.setHegemonicGender(Gender.UNDEFINED);

            if (closePerson.getText().isEmpty())
                closePerson.setText("?");
        } else {
            closePerson = replaceTags("{string:person[" + index + "]⸻⸮}");
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

    //================================================================================
    // SharedPreferences methods
    //================================================================================

    public void deleteTemp() {
        preferences.remove("temp_changeFortuneTeller");
        preferences.remove("temp_busy");
        preferences.remove("temp_enquiryDate");
        preferences.remove("temp_restartActivity");

        if (!defaultPreferences.getBoolean("preference_keepForm"))
            clearForm();
    }

    public void clearForm() {
        preferences.remove("temp_name");
        preferences.remove("temp_gender");
        preferences.remove("temp_date_year");
        preferences.remove("temp_date_month");
        preferences.remove("temp_date_day");
        preferences.remove("temp_anonymous");
    }
}

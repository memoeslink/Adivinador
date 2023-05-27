package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.ContextWrapper;

import com.app.memoeslink.adivinador.tagprocessor.TagProcessor;
import com.memoeslink.common.Randomizer;
import com.memoeslink.generator.common.CharHelper;
import com.memoeslink.generator.common.DateTimeHelper;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.IntegerHelper;
import com.memoeslink.generator.common.LongHelper;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.StringHelper;
import com.memoeslink.generator.common.TextComponent;
import com.memoeslink.generator.common.TextFormatter;
import com.memoeslink.generator.common.TextProcessor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Divination extends ContextWrapper {
    public static final String[] COMMON_COLORS = {"#FEFF5B", "#6ABB6A", "#E55B5B", "#5B72E5", "#925BFF"};
    public static final Integer[] PROBABILITY_DISTRIBUTION = {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 5};
    private static final char separator;
    private final Person person;
    private final String date;
    private Randomizer r;
    private ResourceExplorer resourceExplorer;
    private TagProcessor tagProcessor;

    static {
        separator = CharHelper.getFirstDisplayableGlyph('↓', '⬇', '⇣', '¦', '•');
    }

    public Divination(Context context) {
        this(context, new Person.PersonBuilder().setAttribute("empty").build(), "");
    }

    public Divination(Context context, Person person, String date) {
        super(context);
        this.person = person;
        this.date = StringHelper.defaultIfBlank(date, DateTimeHelper.getStrCurrentDate());
    }

    public Prediction getPrediction(Person person) {
        person.addAttribute("queried");
        person.setDescription(TextFormatter.formatDescriptor(person));

        //Reset objects that rely on randomization
        resetRandomization();

        //Get prediction information
        String fortuneCookie;

        if (r.getBoolean())
            fortuneCookie = resourceExplorer.getDatabaseFinder().getLegacyFortuneCookie();
        else {
            fortuneCookie = StringHelper.quote(resourceExplorer.getDatabaseFinder().getFortuneCookie());
            fortuneCookie = TextFormatter.colorText(fortuneCookie, "aqua");
        }

        return new Prediction.PredictionBuilder().setPerson(person)
                .setDate(date)
                .setRetrievalDate(DateTimeHelper.getStrCurrentDate())
                .setComponent("fortuneCookie", fortuneCookie)
                .setComponent("gibberish", TextProcessor.turnIntoGibberish(fortuneCookie))
                .setComponent("divination", getDivination())
                .setComponent("emotions", getEmotions())
                .setComponent("characteristic", StringHelper.capitalizeFirst(resourceExplorer.getDatabaseFinder().getAbstractNoun()))
                .setComponent("chainOfEvents", getChainOfEvents(person))
                .build();
    }

    private String getDivination() {
        String divination;
        float probability = r.getFloat();

        if (probability <= 0.2F) {
            divination = resourceExplorer.getDatabaseFinder().getDivination();
            divination = TextFormatter.colorText(divination, "aqua");
        } else if (probability <= 0.4F) {
            divination = resourceExplorer.getDatabaseFinder().getLegacyDivination();
            divination = tagProcessor.replaceTags(divination).getText();
        } else if (probability <= 0.8F) {
            Gender gender = Gender.UNDEFINED;
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
            segment = StringHelper.replaceOnce(segment, "‽2", DateTimeHelper.getStrDatePlusDays(date, days));
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

    private String getEmotions() {
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
                                entry.getKey().getName(getBaseContext()),
                                entry.getKey().getEmoji(),
                                entry.getValue());
        }
        return emotionDetails;
    }

    private String getChainOfEvents(Person person) {
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

        //Append gender glyph in people's descriptions
        for (Person currentPerson : people) {
            String description = currentPerson.getDescription();

            if (!currentPerson.hasAttribute("queried") && (currentPerson.hasAttribute("generated") || currentPerson.hasAttribute("nonspecific")) && StringHelper.isNotNullOrBlank(description)) {
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
        TextComponent closePerson = new TextComponent();
        int listSize = IntegerHelper.defaultByRange(resourceExplorer.getContactNameFinder().getContactNamesSize(), 0, 1000);
        double probability = 0.1F + (0.5F / 1000 * listSize);

        if (r.getFloat() <= probability && listSize > 0 && !person.hasAttribute("generated") && person.hasAttribute("entered")) {
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

    private void resetRandomization() {
        String dailySeed = person.getSummary() + System.getProperty("line.separator") + date;
        long seed = LongHelper.getSeed(StringHelper.sha256(dailySeed));
        r = new Randomizer(seed);
        resourceExplorer = new ResourceExplorer(getBaseContext(), seed);
        tagProcessor = new TagProcessor(getBaseContext(), seed);
    }
}

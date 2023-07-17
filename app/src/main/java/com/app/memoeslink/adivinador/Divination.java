package com.app.memoeslink.adivinador;

import android.content.Context;
import android.content.ContextWrapper;

import com.app.memoeslink.adivinador.tagprocessor.TagProcessor;
import com.app.memoeslink.adivinador.textfilter.TextFilter;
import com.app.memoeslink.adivinador.textfilter.TextFilterFactory;
import com.memoeslink.common.Randomizer;
import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.GrammaticalNumber;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.PrintableCharUtils;
import com.memoeslink.generator.common.TextComponent;
import com.memoeslink.generator.common.TextFormatter;
import com.memoeslink.generator.common.TextProcessor;

import org.memoeslink.DateTimeHelper;
import org.memoeslink.IntegerHelper;
import org.memoeslink.LongHelper;
import org.memoeslink.StringHelper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Divination extends ContextWrapper {
    private static final Integer[] PROBABILITY_DISTRIBUTION = {0, 0, 0, 0, 0, 0, 1, 1, 1, 1, 1, 2, 2, 2, 2, 3, 3, 3, 4, 4, 5};
    private static final char SEPARATOR = PrintableCharUtils.getFirstDisplayable('↓', '⬇', '⇣', '¦', '•');
    private final Person person;
    private final String date;

    public Divination(Context context) {
        this(context, new Person.PersonBuilder().setAttribute("empty").build(), "");
    }

    public Divination(Context context, Person person, String date) {
        super(context);
        this.person = person != null ? person : new Person.PersonBuilder().build();
        this.person.addAttribute("queried");
        this.person.setDescription(TextFormatter.formatDescriptor(person));
        this.date = StringHelper.defaultIfBlank(date, DateTimeHelper.getStrCurrentDate());
    }

    public Prediction getPrediction() {
        String summarization = person.getSummary() + System.getProperty("line.separator") + date;
        Long seed = LongHelper.getSeed(StringHelper.sha256(summarization));
        ResourceExplorer resourceExplorer = new ResourceExplorer(getBaseContext(), seed);

        // Get prediction information
        String fortuneCookie = getFortuneCookie();

        return new Prediction.PredictionBuilder().setPerson(person)
                .setDate(date)
                .setRetrievalDate(DateTimeHelper.getStrCurrentDate())
                .setComponent("fortuneCookie", fortuneCookie)
                .setComponent("gibberish", TextProcessor.turnIntoGibberish(fortuneCookie))
                .setComponent("divination", getDivination())
                .setComponent("emotions", getEmotions())
                .setComponent("characteristic", StringHelper.capitalizeFirst(resourceExplorer.getResourceFinder().getStrFromArrayRes(R.array.abstract_noun)))
                .setComponent("chainOfEvents", getChainOfEvents(person))
                .build();
    }

    public String getFortuneCookie() {
        String fortuneCookie;
        String summarization = person.getSummary() + System.getProperty("line.separator") + date;
        Long seed = LongHelper.getSeed(StringHelper.sha256(summarization));
        Randomizer r = new Randomizer(seed);
        ResourceExplorer resourceExplorer = new ResourceExplorer(getBaseContext(), seed);

        if (r.getBoolean())
            fortuneCookie = resourceExplorer.getDatabaseFinder().getLegacyFortuneCookie();
        else {
            fortuneCookie = resourceExplorer.getDatabaseFinder().getFortuneCookie();
            fortuneCookie = StringHelper.quote(fortuneCookie);
            fortuneCookie = TextFormatter.formatBotMessage(fortuneCookie);
        }
        return fortuneCookie;
    }

    public String getDivination() {
        String divination = Database.DEFAULT_VALUE;
        String summarization = person.getSummary() + System.getProperty("line.separator") + date;
        Long seed = LongHelper.getSeed(StringHelper.sha256(summarization));
        Randomizer r = new Randomizer(seed);
        ResourceExplorer resourceExplorer = new ResourceExplorer(getBaseContext(), seed);
        TagProcessor tagProcessor = new TagProcessor(getBaseContext(), seed);

        switch (r.getInt(5)) {
            case 0 -> {
                divination = resourceExplorer.getDatabaseFinder().getLegacyPrediction();
                divination = tagProcessor.replaceTags(divination).getText();
            }
            case 1 -> {
                divination = resourceExplorer.getDatabaseFinder().getPrediction();
                divination = TextFormatter.formatBotMessage(divination);

                // Filter profanity
                TextFilter textFilter = new TextFilterFactory(getBaseContext()).createTextFilter();
                divination = textFilter.censor(divination);
            }
            case 2 ->
                    divination = tagProcessor.replaceTags(resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination)).getText();
            case 3, 4 -> {
                Gender gender = Gender.UNDEFINED;
                GrammaticalNumber grammaticalNumber = GrammaticalNumber.UNDEFINED;
                List<String> segments = new ArrayList<>();

                // Format start of divination
                String startSegment = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination_start);
                startSegment = tagProcessor.replaceTags(startSegment).getText();
                segments.add(startSegment);

                // Format middle of divination
                String middleSegment = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination_middle);
                TextComponent component = tagProcessor.replaceTags(middleSegment);
                middleSegment = component.getText();

                if (component.getHegemonicGender() != null && component.getHegemonicGender() != Gender.UNDEFINED)
                    gender = component.getHegemonicGender();

                if (component.getHegemonicGrammaticalNumber() != null && component.getHegemonicGrammaticalNumber() != GrammaticalNumber.UNDEFINED)
                    grammaticalNumber = component.getHegemonicGrammaticalNumber();
                segments.add(middleSegment);

                // Format end of divination
                String endSegment = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination_end);
                endSegment = tagProcessor.replaceTags(endSegment, gender, grammaticalNumber).getText();
                segments.add(endSegment);

                // Format cause of divination
                String causeSegment = resourceExplorer.getResourceFinder().getStrFromStrArrayRes(R.array.divination_cause);

                if (!causeSegment.isEmpty())
                    causeSegment = tagProcessor.replaceTags(causeSegment).getText();
                segments.add(causeSegment);

                segments.removeAll(Collections.singleton(""));
                divination = StringHelper.joinWithSpace(segments);
            }
        }
        return divination;
    }

    public String getEmotions() {
        String emotionDetails = "";
        HashMap<Emotion, Integer> emotionCount = new HashMap<>();
        String summarization = person.getSummary() + System.getProperty("line.separator") + date;
        Long seed = LongHelper.getSeed(StringHelper.sha256(summarization));
        Randomizer r = new Randomizer(seed);

        for (Emotion emotion : Emotion.values()) {
            emotionCount.put(emotion, 0);
        }
        int points, remainingPoints = 100;

        while (remainingPoints > 0) {
            points = r.getInt(remainingPoints + 1);
            remainingPoints -= points;
            Emotion currentEmotion = r.getEnum(Emotion.class);
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

    public String getChainOfEvents(Person person) {
        StringBuilder chain = new StringBuilder();
        List<Person> people = new ArrayList<>();
        String summarization = person.getSummary() + System.getProperty("line.separator") + date;
        Long seed = LongHelper.getSeed(StringHelper.sha256(summarization));
        Randomizer r = new Randomizer(seed);
        ResourceExplorer resourceExplorer = new ResourceExplorer(getBaseContext(), seed);
        TagProcessor tagProcessor = new TagProcessor(getBaseContext(), seed);

        // Add unknown people
        char letter = 'A';
        final String[] basicColors = {"#FEFF5B", "#6ABB6A", "#E55B5B", "#5B72E5", ">#925BFF"};

        for (int n = 0, limit = r.getElement(PROBABILITY_DISTRIBUTION); n < limit; n++) {
            Gender gender = r.getEnum(Gender.class);
            String formattedLetter = String.format("<font color=\"%s\">%s</font>", basicColors[n], TextFormatter.formatText(Character.toString(letter), "b", "i"));
            String description = String.format("{string:person; index:0} %s, {string:probability} {string:individual}", formattedLetter);
            description = tagProcessor.replaceTags(description, gender, GrammaticalNumber.SINGULAR).getText();
            people.add(new Person.PersonBuilder()
                    .setNickname(Character.toString(letter))
                    .setGender(gender)
                    .setDescription(description)
                    .setAttribute("nonspecific")
                    .setAttribute("unknown")
                    .build());
            letter++;
        }

        // Add identified people
        for (int n = 0, limit = r.getInt(3, 11); n < limit; n++) {
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

        // Add close people
        int arrayLength = resourceExplorer.getResourceFinder().getArrayResLength(R.array.person);
        int closePersonType = r.getInt(1, arrayLength);
        int thirdPartyType = closePersonType <= 9 ? r.getInt(11, arrayLength) : r.getInt(1, arrayLength);

        TextComponent tempComponent = new TextComponent();
        int listSize = IntegerHelper.defaultByRange(resourceExplorer.getContactNameFinder().getContactNamesSize(), 0, 1000);
        double probability = 0.1F + (0.5F / 1000 * listSize);

        if (r.getFloat() <= probability && listSize > 0 && !person.hasAttribute("generated") && person.hasAttribute("entered")) {
            tempComponent.setText(TextFormatter.formatContactName(resourceExplorer.getContactNameFinder().getContactName()));
            tempComponent.addGender(Gender.UNDEFINED);

            if (tempComponent.getText().isEmpty())
                tempComponent.setText("?");
        } else {
            tempComponent = tagProcessor.replaceTags("{string:relationship; index:" + closePersonType + "; gender:⸮}");
            tempComponent.setText(String.format(tempComponent.getText(), person.getDescription()));
        }
        Person closePerson = new Person.PersonBuilder()
                .setGender(tempComponent.getHegemonicGender())
                .setDescription(tempComponent.getText())
                .setAttribute("nonspecific")
                .build();

        tempComponent = tagProcessor.replaceTags("{string:relationship; index:" + thirdPartyType + "; gender:⸮}");
        tempComponent.setText(String.format(tempComponent.getText(), closePerson.getDescription()));
        Person thirdParty = new Person.PersonBuilder()
                .setGender(tempComponent.getHegemonicGender())
                .setDescription(tempComponent.getText())
                .setAttribute("nonspecific")
                .build();
        people.add(thirdParty);
        people.add(closePerson);

        // Add oneself
        people.add(person);

        // Append gender glyph in people's descriptions
        for (Person currentPerson : people) {
            String description = currentPerson.getDescription();

            if (!currentPerson.hasAttribute("queried") && (currentPerson.hasAttribute("generated") || currentPerson.hasAttribute("nonspecific")) && StringHelper.isNotNullOrBlank(description)) {
                description += " (<font color=\"#B599FC\">" + TextFormatter.formatText(currentPerson.getGender().getGlyph(), "b") + "</font>)";
                currentPerson.setDescription(description);
            }
        }

        // Define chain links
        int totalKarma = 0;

        for (int n = 0; n < people.size() - 1; n++) {
            int karma = r.getInt(-10, 11);
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

            // Define link delimiter
            if (SEPARATOR != '\u0000') // \u0000 is \0
                chain.append("<br><font color=\"#A0A8C7\">").append(SEPARATOR).append("</font><br>");
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
}

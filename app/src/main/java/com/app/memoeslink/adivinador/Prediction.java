package com.app.memoeslink.adivinador;

import android.content.Context;

import com.app.memoeslink.adivinador.extensions.StringExtensions;
import com.memoeslink.generator.common.DateTimeHelper;
import com.memoeslink.generator.common.Person;
import com.memoeslink.generator.common.ZeroWidthChar;

import java.util.HashMap;

public class Prediction {
    private Person person;
    private String date;
    private String retrievalDate;
    private HashMap<String, String> components;

    private Prediction() {
    }

    private Prediction(Person person, String date, String retrievalDate, HashMap<String, String> components) {
        this.person = person;
        this.date = date;
        this.retrievalDate = retrievalDate;
        this.components = components;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getRetrievalDate() {
        return retrievalDate;
    }

    public void setRetrievalDate(String retrievalDate) {
        this.retrievalDate = retrievalDate;
    }

    public HashMap<String, String> getComponents() {
        return components;
    }

    public void setComponents(HashMap<String, String> components) {
        this.components = components;
    }

    public String getContent(Context context) {
        String content = context.getString(R.string.prediction,
                date,
                components.getOrDefault("fortuneCookie", "…"),
                components.getOrDefault("divination", "…"),
                components.getOrDefault("emotions", "…"),
                components.getOrDefault("characteristic", "…"),
                components.getOrDefault("chainOfEvents", "…")
        );
        content = context.getString(R.string.inquiry_information, date, person.getDescriptor(), person.getGender().getName(context, 1), person.getBirthdate()) +
                System.getProperty("line.separator") + System.getProperty("line.separator") +
                content;
        return StringExtensions.toHtmlText(content).toString();
    }

    public String getFormattedContent(Context context) {
        return context.getString(R.string.prediction,
                date,
                (components.containsKey("gibberish") ?
                        (ZeroWidthChar.ZERO_WIDTH_SPACE.getCharacter() +
                                context.getString(R.string.prediction_action) + "<br>" +
                                "<font color=\"#ECFE5B\">" + components.getOrDefault("gibberish", "…") + "</font>" +
                                ZeroWidthChar.ZERO_WIDTH_SPACE.getCharacter()
                        ) : "…"
                ),
                components.getOrDefault("divination", "…"),
                components.getOrDefault("emotions", "…"),
                components.getOrDefault("characteristic", "…"),
                components.getOrDefault("chainOfEvents", "…")
        );
    }

    public static class PredictionBuilder {
        private Person person;
        private String date;
        private String retrievalDate;
        private final HashMap<String, String> components = new HashMap<>();

        public PredictionBuilder() {
        }

        public PredictionBuilder setPerson(Person person) {
            this.person = person;
            return this;
        }

        public PredictionBuilder setDate(String date) {
            this.date = date;
            return this;
        }

        public PredictionBuilder setRetrievalDate(String retrievalDate) {
            this.retrievalDate = retrievalDate;
            return this;
        }

        public PredictionBuilder setComponent(String key, String value) {
            this.components.put(key, value);
            return this;
        }

        public Prediction build() {
            if (this.person == null)
                this.person = new Person.PersonBuilder().setAttribute("empty").build();

            if (this.date == null)
                this.date = DateTimeHelper.getStrCurrentDate();

            if (this.retrievalDate == null)
                this.retrievalDate = DateTimeHelper.getStrCurrentDate();
            return new Prediction(this.person, this.date, this.retrievalDate, this.components);
        }
    }
}

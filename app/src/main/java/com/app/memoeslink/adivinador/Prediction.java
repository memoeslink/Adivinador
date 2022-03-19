package com.app.memoeslink.adivinador;

import com.memoeslink.generator.common.Person;

public class Prediction {
    private String date;
    private String retrievalDate;
    private String content;
    private String formattedContent;
    private String fortuneCookie;
    private String unrevealedFortuneCookie;
    private Person person;

    public Prediction() {
        date = "";
        retrievalDate = "";
        content = "";
        formattedContent = "";
        fortuneCookie = "";
        unrevealedFortuneCookie = "";
        person = new Person.PersonBuilder().build();
    }

    public Prediction(String date, String retrievalDate, String content, String formattedContent, String fortuneCookie, String unrevealedFortuneCookie, Person person) {
        this.date = date;
        this.retrievalDate = retrievalDate;
        this.content = content;
        this.formattedContent = formattedContent;
        this.fortuneCookie = fortuneCookie;
        this.unrevealedFortuneCookie = unrevealedFortuneCookie;
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

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getFormattedContent() {
        return formattedContent;
    }

    public void setFormattedContent(String formattedContent) {
        this.formattedContent = formattedContent;
    }

    public String getFortuneCookie() {
        return fortuneCookie;
    }

    public void setFortuneCookie(String fortuneCookie) {
        this.fortuneCookie = fortuneCookie;
    }

    public String getUnrevealedFortuneCookie() {
        return unrevealedFortuneCookie;
    }

    public void setUnrevealedFortuneCookie(String unrevealedFortuneCookie) {
        this.unrevealedFortuneCookie = unrevealedFortuneCookie;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }
}

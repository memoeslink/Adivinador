package com.app.memoeslink.adivinador;

public class Enquiry {
    private Person person;
    private boolean userRequested;
    private boolean anonymous;

    public Enquiry() {
        person = null;
        userRequested = true;
        anonymous = false;
    }

    public Enquiry(Person person, boolean userRequested, Boolean anonymous) {
        this.person = person;
        this.userRequested = userRequested;
        this.anonymous = anonymous;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public boolean isUserRequested() {
        return userRequested;
    }

    public void setUserRequested(boolean userRequested) {
        this.userRequested = userRequested;
    }

    public boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(boolean anonymous) {
        this.anonymous = anonymous;
    }

    public String getDescriptor() {
        return anonymous ? person.getUsername() : person.getFullName();
    }

    public String getFormattedDescriptor() {
        return anonymous ? person.getFormattedUsername() : person.getFormattedFullName();
    }
}

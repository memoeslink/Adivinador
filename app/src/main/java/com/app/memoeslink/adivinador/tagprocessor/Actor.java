package com.app.memoeslink.adivinador.tagprocessor;

import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.GrammaticalNumber;

import org.memoeslink.StringHelper;

import java.util.HashSet;
import java.util.Set;

public class Actor {
    private String descriptor;
    private Gender gender;
    private GrammaticalNumber grammaticalNumber;
    private Set<String> attributes = new HashSet<>();

    public Actor() {
        descriptor = "";
        gender = Gender.UNDEFINED;
        grammaticalNumber = GrammaticalNumber.UNDEFINED;
    }

    public Actor(String descriptor) {
        this(descriptor, Gender.UNDEFINED, GrammaticalNumber.UNDEFINED);
    }

    public Actor(String descriptor, Gender gender, GrammaticalNumber grammaticalNumber) {
        this.descriptor = descriptor;
        this.gender = gender;
        this.grammaticalNumber = grammaticalNumber;
    }

    public String getDescriptor() {
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public GrammaticalNumber getGrammaticalNumber() {
        return grammaticalNumber;
    }

    public void setGrammaticalNumber(GrammaticalNumber grammaticalNumber) {
        this.grammaticalNumber = grammaticalNumber;
    }

    public void setAttribute(String attribute) {
        if (StringHelper.isNotNullOrBlank(attribute))
            attributes.add(attribute);
    }

    public boolean hasAttribute(String attribute) {
        return attributes.contains(attribute);
    }
}

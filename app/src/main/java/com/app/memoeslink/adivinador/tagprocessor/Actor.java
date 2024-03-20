package com.app.memoeslink.adivinador.tagprocessor;

import com.memoeslink.generator.common.Gender;
import com.memoeslink.generator.common.GrammaticalNumber;

import org.memoeslink.StringHelper;

import java.util.HashSet;
import java.util.Set;

public class Actor {
    private Object descriptor;
    private Gender gender;
    private Gender trueGender;
    private GrammaticalNumber grammaticalNumber;
    private GrammaticalNumber trueGrammaticalNumber;
    private Set<String> attributes = new HashSet<>();

    public Actor() {
        descriptor = "";
        gender = Gender.UNDEFINED;
        trueGender = Gender.UNDEFINED;
        grammaticalNumber = GrammaticalNumber.UNDEFINED;
        trueGrammaticalNumber = GrammaticalNumber.UNDEFINED;
    }

    public Actor(Object descriptor) {
        this(descriptor, Gender.UNDEFINED, GrammaticalNumber.UNDEFINED);
    }

    public Actor(Object descriptor, Gender gender, GrammaticalNumber grammaticalNumber) {
        this(descriptor, gender, gender, grammaticalNumber, grammaticalNumber);
    }

    public Actor(Object descriptor, Gender gender, Gender trueGender, GrammaticalNumber grammaticalNumber, GrammaticalNumber trueGrammaticalNumber) {
        this.descriptor = descriptor;
        this.gender = gender;
        this.trueGender = gender;
        this.grammaticalNumber = grammaticalNumber;
        this.trueGrammaticalNumber = grammaticalNumber;
    }

    public Object getDescriptor() {
        if (descriptor instanceof String) return descriptor.toString();
        return descriptor;
    }

    public void setDescriptor(String descriptor) {
        this.descriptor = descriptor;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender != null ? gender : Gender.UNDEFINED;
    }

    public Gender getTrueGender() {
        return trueGender != null && trueGender != Gender.UNDEFINED ? trueGender : Gender.UNDEFINED;
    }

    public void setTrueGender(Gender trueGender) {
        this.trueGender = trueGender != null ? trueGender : Gender.UNDEFINED;
    }

    public GrammaticalNumber getGrammaticalNumber() {
        return grammaticalNumber;
    }

    public void setGrammaticalNumber(GrammaticalNumber grammaticalNumber) {
        this.grammaticalNumber = grammaticalNumber != null ? grammaticalNumber : GrammaticalNumber.UNDEFINED;
    }

    public GrammaticalNumber getTrueGrammaticalNumber() {
        return trueGrammaticalNumber != null && trueGrammaticalNumber != GrammaticalNumber.UNDEFINED ?
                trueGrammaticalNumber : grammaticalNumber;
    }

    public void setTrueGrammaticalNumber(GrammaticalNumber trueGrammaticalNumber) {
        this.trueGrammaticalNumber = trueGrammaticalNumber != null ? trueGrammaticalNumber : GrammaticalNumber.UNDEFINED;
    }

    public void setAttribute(String attribute) {
        if (StringHelper.isNotNullOrBlank(attribute))
            attributes.add(attribute);
    }

    public boolean hasAttribute(String attribute) {
        return attributes.contains(attribute);
    }
}

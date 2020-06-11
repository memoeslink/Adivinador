package com.app.memoeslink.adivinador;

import java.util.LinkedHashSet;
import java.util.Set;

public class TextComponent {
    private long id;
    private String uniqueId;
    private String tag;
    private String text;
    private Integer hegemonicSex;
    private boolean nullified;
    private Set<String> attributes = new LinkedHashSet<>();

    public TextComponent() {
        id = -1;
        uniqueId = null;
        tag = null;
        text = "";
        hegemonicSex = null;
        nullified = false;
    }

    public TextComponent(long id, String uniqueId, String tag, String text, Integer hegemonicSex, boolean nullified) {
        this.id = id;
        this.uniqueId = uniqueId;
        this.tag = tag;
        this.text = text;
        this.hegemonicSex = hegemonicSex;
        this.nullified = nullified;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public Integer getHegemonicSex() {
        return hegemonicSex;
    }

    public void setHegemonicSex(Integer hegemonicSex) {
        this.hegemonicSex = hegemonicSex;
    }

    public boolean isNullified() {
        return nullified;
    }

    public void setNullified(boolean nullified) {
        this.nullified = nullified;
    }

    public Set<String> getAttributes() {
        return attributes;
    }

    public void setAttributes(Set<String> attributes) {
        this.attributes = attributes;
    }
}
package com.app.memoeslink.adivinador.tagprocessor;

import java.util.HashSet;
import java.util.Set;

public class ProcessedText {
    private String text;
    private int replacementCount;
    private int remainingMatches;

    public ProcessedText() {
        text = "";
        replacementCount = 0;
        remainingMatches = 0;
    }

    public ProcessedText(String text, int replacementCount, int remainingMatches) {
        this.text = text;
        this.replacementCount = replacementCount;
        this.remainingMatches = remainingMatches;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public int getReplacementCount() {
        return replacementCount;
    }

    public void setReplacementCount(int replacementCount) {
        this.replacementCount = replacementCount;
    }

    public int getRemainingMatches() {
        return remainingMatches;
    }

    public void setRemainingMatches(int remainingMatches) {
        this.remainingMatches = remainingMatches;
    }
}

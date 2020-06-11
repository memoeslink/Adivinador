package com.app.memoeslink.adivinador;

import org.apache.commons.lang3.StringUtils;

public class SpanishNoun {
    private SpanishArticleEnum articleType;
    private String noun;

    public SpanishNoun() {
        articleType = SpanishArticleEnum.INDEFINITE;
        noun = "";
    }

    public SpanishNoun(SpanishArticleEnum articleType, String noun) {
        this.articleType = articleType;
        this.noun = noun;
    }

    public SpanishArticleEnum getArticleType() {
        return articleType;
    }

    public void setArticleType(SpanishArticleEnum articleType) {
        this.articleType = articleType;
    }

    public String getNoun() {
        return noun;
    }

    public void setNoun(String noun) {
        this.noun = noun;
    }

    public String getNounWithArticle() {
        return StringUtils.trimToEmpty(articleType.getArticle() + " " + noun);
    }

    public String getNounWithIndefiniteArticle() {
        return StringUtils.trimToEmpty(articleType.getIndefiniteArticle() + " " + noun);
    }
}

package com.app.memoeslink.adivinador.spanish;

import org.apache.commons.lang3.StringUtils;

public class Noun {
    private Article article;
    private String noun;

    public Noun() {
        article = Article.INDEFINITE;
        noun = "";
    }

    public Noun(Article article, String noun) {
        this.article = article;
        this.noun = noun;
    }

    public Article getArticle() {
        return article;
    }

    public void setArticle(Article article) {
        this.article = article;
    }

    public String getNoun() {
        return noun;
    }

    public void setNoun(String noun) {
        this.noun = noun;
    }

    public String getNounWithArticle() {
        return StringUtils.trimToEmpty(article.getArticle() + " " + noun);
    }

    public String getNounWithIndefiniteArticle() {
        return StringUtils.trimToEmpty(article.getIndefiniteArticle() + " " + noun);
    }
}

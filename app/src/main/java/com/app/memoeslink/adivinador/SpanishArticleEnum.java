package com.app.memoeslink.adivinador;

public enum SpanishArticleEnum {
    MASCULINE_SINGULAR("el", "un", 1, false),
    MASCULINE_PLURAL("los", "unos", 1, true),
    FEMININE_SINGULAR("la", "una", 2, false),
    FEMININE_PLURAL("las", "unas", 2, true),
    NEUTRAL("lo", "uno", 0, false),
    INDEFINITE("", "", 0, false);

    private String article;
    private String indefiniteArticle;
    private int sex;
    private boolean plural;

    SpanishArticleEnum(String article, String indefiniteArticle, int sex, boolean plural) {
        this.article = article;
        this.indefiniteArticle = indefiniteArticle;
        this.sex = sex;
        this.plural = plural;
    }

    public String getArticle() {
        return article;
    }

    public String getIndefiniteArticle() {
        return indefiniteArticle;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public boolean isPlural() {
        return plural;
    }
}

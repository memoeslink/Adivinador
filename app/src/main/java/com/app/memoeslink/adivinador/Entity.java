package com.app.memoeslink.adivinador;

import org.apache.commons.lang3.StringUtils;

/**
 * Created by Memoeslink on 02/09/2017.
 */

public class Entity {
    public static final int DEFAULT_SEX = 0;
    protected int id;
    protected int sex;
    protected String forename;
    protected String surname;
    protected String generationalTitle;
    protected NameEnum nameType;

    public Entity() {
        id = -1;
        sex = -1;
        forename = "";
        surname = "";
        generationalTitle = "";
        nameType = NameEnum.EMPTY;
    }

    public Entity(int id, int sex, String forename, String surname, String generationalTitle, NameEnum nameType) {
        this.id = id;
        this.sex = sex;
        this.forename = forename;
        this.surname = surname;
        this.generationalTitle = generationalTitle;
        this.nameType = nameType;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getForename() {
        return forename;
    }

    public void setForename(String forename) {
        this.forename = forename;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getGenerationalTitle() {
        return generationalTitle;
    }

    public void setGenerationalTitle(String generationalTitle) {
        this.generationalTitle = generationalTitle;
    }

    public NameEnum getNameType() {
        return nameType;
    }

    public void setNameType(NameEnum nameType) {
        this.nameType = nameType;
    }

    public String getSimpleName() {
        return StringUtils.trimToEmpty(forename + (StringUtils.isNotBlank(forename) && StringUtils.isNotBlank(surname) ? " " : "") + surname); //StringUtils.defaultIfBlank(forename, "") + (StringUtils.isBlank(forename) || StringUtils.isBlank(surname) ? "" : " ") + StringUtils.defaultIfBlank(surname, "");
    }

    public String getFullName() {
        return getSimpleName() + (StringUtils.isNotBlank(generationalTitle) ? " " + generationalTitle : "");
    }

    public String getFormattedSimpleName() {
        return Methods.formatText(getSimpleName(), "b");
    }

    public String getFormattedFullName() {
        return getFormattedSimpleName() + (StringUtils.isNotEmpty(generationalTitle) ? " " + Methods.formatText(generationalTitle, "b,u") : "");
    }
}

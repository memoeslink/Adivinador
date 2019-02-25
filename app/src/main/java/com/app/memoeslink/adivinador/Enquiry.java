package com.app.memoeslink.adivinador;

/**
 * Created by Memoeslink on 14/03/2018.
 */

public class Enquiry {
    private String name;
    private String formattedName;
    private int sex;
    private int year;
    private int month;
    private int day;
    private boolean user;
    private Boolean anonymous;

    public Enquiry() {
        name = "?";
        formattedName = "";
        sex = -1;
        year = 2000;
        month = 0;
        day = 1;
        user = true;
        anonymous = null;
    }

    public Enquiry(String name, String formattedName, int sex, int year, int month, int day, boolean user, Boolean anonymous) {
        this.name = name;
        this.formattedName = formattedName;
        this.sex = sex;
        this.year = year;
        this.month = month;
        this.day = day;
        this.user = user;
        this.anonymous = anonymous;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFormattedName() {
        return formattedName;
    }

    public void setFormattedName(String formattedName) {
        this.formattedName = formattedName;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public boolean isUser() {
        return user;
    }

    public void setUser(boolean user) {
        this.user = user;
    }

    public Boolean isAnonymous() {
        return anonymous;
    }

    public void setAnonymous(Boolean anonymous) {
        this.anonymous = anonymous;
    }
}

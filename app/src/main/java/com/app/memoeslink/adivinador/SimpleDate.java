package com.app.memoeslink.adivinador;

public class SimpleDate {
    public static final String UNKNOWN_DATE = "????/??/??";
    public static final int DEFAULT_YEAR = 2000;
    public static final int DEFAULT_MONTH = 1;
    public static final int DEFAULT_DAY = 1;
    private int year;
    private int month;
    private int day;

    public SimpleDate() {
        year = DEFAULT_YEAR;
        month = DEFAULT_MONTH;
        day = DEFAULT_DAY;
    }

    public SimpleDate(int year, int month, int day) {
        this.year = year;
        this.month = month;
        this.day = day;
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
}

package com.app.memoeslink.adivinador;

public class SimpleTime {
    public static final String UNKNOWN_TIME = "??:??:??";
    public static final int DEFAULT_HOUR = 0;
    public static final int DEFAULT_MINUTE = 0;
    public static final int DEFAULT_SECOND = 0;
    private int hour;
    private int minute;
    private int second;

    public SimpleTime() {
        hour = DEFAULT_HOUR;
        minute = DEFAULT_MINUTE;
        second = DEFAULT_SECOND;
    }

    public SimpleTime(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }
}

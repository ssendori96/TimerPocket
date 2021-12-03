package com.project.timerpocket;

public class ListData {
    String title;
    int hour;
    int minute;
    int second;

    public ListData(String title, int hour, int minute, int second) {
        this.title = title;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public String getTitle() {
        return title;
    }

    public int getHour() {
        return hour;
    }

    public int getMinute() {
        return minute;
    }

    public int getSecond() {
        return second;
    }
}

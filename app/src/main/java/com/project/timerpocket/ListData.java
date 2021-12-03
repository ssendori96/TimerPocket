package com.project.timerpocket;

public class ListData {
    String title;
    int hour;
    int minute;
    int second;
    int bookmark;

    public ListData(String title, int hour, int minute, int second, int bookmark) {
        this.title = title;
        this.hour = hour;
        this.minute = minute;
        this.second = second;
        this.bookmark = bookmark;
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

    public int getBookmark() {return bookmark;}
}

package com.example.reminderapp;

import java.sql.Time;
import java.util.Date;

class Event {

    private String title;
    private String date;
    private String time;
    private Integer prepTime;
    private String transport;
    private String location;

    Event(String tit, String d, String tim, Integer p, String tra, String l) {
        this.title = tit;
        this.date = d;
        this.time = tim;
        this.prepTime = p;
        this.transport = tra;
        this.location = l;
    }

    public Event() {}

    public void setTitle(String t) {
        this.title = t;
    }
    public void setDate(String d) {
        this.date = d;
    }
    public void setTime(String t) {
        this.time = t;
    }
    public void setPrepTime(Integer p) {
        this.prepTime = p;
    }
    public void setTransport(String t) {
        this.transport = t;
    }
    public void setLocation(String l) {
        this.location = l;
    }

    String getTitle() {
        return this.title;
    }
    String getDate() {
        return this.date;
    }
    String getTime() {
        return this.time;
    }
    Integer getPrepTime () {
        return this.prepTime;
    }
    String getTransport() {
        return this.transport;
    }
    String getLocation() {
        return this.location;
    }
}

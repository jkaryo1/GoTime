package com.example.reminderapp;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

class Event {

    private String title;
    private Calendar date;
    private Integer prepTime;
    private String transport;
    private String location;
    private static final String DATE_FORMAT = "MM/dd/yyyy hh:mm a";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    private static final String JUST_DATE_FORMAT = "MM/dd/yyyy";
    private static final SimpleDateFormat jdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    private static final String TIME_FORMAT = "hh:mm a";
    private static final SimpleDateFormat tf = new SimpleDateFormat(DATE_FORMAT, Locale.US);

    Event(String tit, String d, String tim, Integer p, String tra, String l) {
        try {
            this.title = tit;
            this.date = Calendar.getInstance();
            this.date.clear();
            this.date.setTime(sdf.parse(d + " " + tim));
            this.prepTime = p;
            this.transport = tra;
            this.location = l;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Event() {}

    public void setTitle(String t) {
        this.title = t;
    }
    public void setDate(String d) {
        this.date.set(Integer.parseInt(d.substring(6)), Integer.parseInt(d.substring(3,6)),
                Integer.parseInt(d.substring(0,3)), this.date.get(Calendar.HOUR), this.date.get(Calendar.MINUTE));
    }
    public void setTime(String t) {
        int hour = Integer.parseInt(t.substring(0,2));
        if (t.substring(6).equals("PM")) {
            hour += 12;
        }
        this.date.set(this.date.get(Calendar.YEAR), this.date.get(Calendar.MONTH),
                this.date.get(Calendar.DATE), hour, Integer.parseInt(t.substring(3,5)));
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
        return jdf.format(this.date);
    }
    String getTime() {
        return tf.format(this.date);
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

package com.example.reminderapp;

import android.util.Log;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

class Event {


    /*Integer Id used to represent the calendar in the database*/
    int id;
    /*Title of the event*/
    String title;
    /*DateTime for when the event actually starts*/
    Calendar date;
    /*Number of minutes the user needs to preapre*/
    Integer prepTime;
    /*String representing the transportation method*/
    String transport;
    /*Address or location title of the place*/
    String location;
    /*Id of the place from the google places api*/
    String placeID;
    /*id of the event in google calendar*/
    String gcalID;
    /*Calendar used to represent the date and time the user should depart for the event*/
    Calendar departTime;


    private static final String DATE_FORMAT = "MM/dd/yyyy hh:mm a";
    private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.US);
    private static final String JUST_DATE_FORMAT = "MM/dd/yyyy";
    private static final SimpleDateFormat jdf = new SimpleDateFormat(JUST_DATE_FORMAT, Locale.US);
    private static final String TIME_FORMAT = "h:mm a";
    private static final SimpleDateFormat tf = new SimpleDateFormat(TIME_FORMAT, Locale.US);



    Event(int i, String tit, long cal, Integer p, String tra, String l, String p_id, String g_id, long d) {
        try {
            this.id = i;
            this.title = tit;
            this.date = Calendar.getInstance();
            this.date.clear();
            this.date.setTimeInMillis(cal);
            this.prepTime = p;
            this.transport = tra;
            this.location = l;
            this.placeID = p_id;
            this.gcalID = g_id;
            this.departTime = Calendar.getInstance();
            this.departTime.clear();
            this.departTime.setTimeInMillis(d);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    Event(int i, String tit, Calendar cal, Integer p, String tra, String l, String p_id, String g_id, Calendar d) {
        this.id = i;
        this.title = tit;
        this.date = cal;
        this.prepTime = p;
        this.transport = tra;
        this.location = l;
        this.placeID = p_id;
        this.gcalID = g_id;
        this.departTime = d;
    }

    void setDate(String d) {
        this.date.set(Integer.parseInt(d.substring(6)), Integer.parseInt(d.substring(3,6)),
                Integer.parseInt(d.substring(0,3)), this.date.get(Calendar.HOUR), this.date.get(Calendar.MINUTE));
    }
    void setTime(String t) {
        int hour = Integer.parseInt(t.substring(0,2));
        if (t.substring(6).equals("PM")) {
            hour += 12;
        }
        this.date.set(this.date.get(Calendar.YEAR), this.date.get(Calendar.MONTH),
                this.date.get(Calendar.DATE), hour, Integer.parseInt(t.substring(3,5)));
    }

    String getDate() {
        return jdf.format(this.date.getTime());
    }
    String getTime() {
        return tf.format(this.date.getTime());
    }
}

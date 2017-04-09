package com.example.driversed;

class Lesson {

    private String date;
    private Double hours;
    private String lessonType;
    private String weather;
    private boolean day;

    Lesson(String d, Double h, String l, String w, boolean da) {
        this.date = d;
        this.hours = h;
        this.lessonType = l;
        this.weather = w;
        this.day = da;
    }

    public Lesson() {}

    public void setDate(String d) {
        this.date = d;
    }
    public void setHours(Double h) {
        this.hours = h;
    }
    public void setLessonType(String l) {
        this.lessonType = l;
    }
    public void setWeather(String w) {
        this.weather = w;
    }
    public void setDay(boolean d) {
        this.day = d;
    }

    String getDate() {
        return this.date;
    }
    Double getHours() {
        return this.hours;
    }
    String getLessonType() {
        return this.lessonType;
    }
    String getWeather() {
        return this.weather;
    }
    boolean getDay() {
        return this.day;
    }

}

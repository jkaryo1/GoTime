package com.example.reminderapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;

class DatabaseAdapter {

    private SQLiteDatabase database;
    private static DatabaseAdapter dbInstance = null;
    private DatabaseHelper dbHelper;

    private static int dbVersion = 1;

    private static final String EVENTS_TABLE = "events";
    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String DATE = "date";
    private static final String PREP_TIME = "prep_time";
    private static final String TRANSPORT = "transport";
    private static final String LOCATION = "location";
    private static final String PLACE_ID = "place_id";
    private static final String GCAL_ID = "gcal_id";
    private static final String[] EVENT_COLS = {ID, TITLE, DATE, PREP_TIME, TRANSPORT, LOCATION, PLACE_ID, GCAL_ID};

    // Enable getting a single instance of database adapter
    static synchronized DatabaseAdapter getInstance(Context c) {
        if (dbInstance == null) {
            dbInstance = new DatabaseAdapter(c.getApplicationContext());
        }
        return dbInstance;
    }

    // Constructor
    private DatabaseAdapter(Context c) {
        String DB_NAME = "events.db";
        this.dbHelper = new DatabaseHelper(c, DB_NAME, null, dbVersion);
    }

    // Open database
    void open() throws SQLiteException {
        try {
            this.database = this.dbHelper.getWritableDatabase();
        } catch (SQLiteException e) {
            this.database = this.dbHelper.getReadableDatabase();
        }
    }

    // Get new info into database
    public void clear() {
        this.dbHelper.onUpgrade(this.database, dbVersion, dbVersion + 1);
        dbVersion++;
    }

    // Update methods //
    long insertItem(Event event) {
        // Create new row
        ContentValues cValues = new ContentValues();
        // Add values to new row
        cValues.put(TITLE, event.title);
        cValues.put(DATE, event.date.getTimeInMillis());
        cValues.put(PREP_TIME, event.prepTime);
        cValues.put(TRANSPORT, event.transport);
        cValues.put(LOCATION, event.location);
        cValues.put(PLACE_ID, event.placeID);
        cValues.put(GCAL_ID, event.gcalID);
        // Add row to table
        return this.database.insert(EVENTS_TABLE, null, cValues);
    }

    // Removes a lesson from the database
    boolean removeItem(long e_id) {
        return this.database.delete(EVENTS_TABLE, ID + "="+ e_id, null) > 0;
    }

    // Updates a lesson in the database
    boolean updateLesson(long e_id, Event event) {
        // Create new row
        ContentValues cValues = new ContentValues();
        // Add values to new row
        cValues.put(TITLE, event.title);
        cValues.put(DATE, event.date.getTimeInMillis());
        cValues.put(PREP_TIME, event.prepTime);
        cValues.put(TRANSPORT, event.transport);
        cValues.put(LOCATION, event.location);
        cValues.put(PLACE_ID, event.placeID);
        cValues.put(GCAL_ID, event.gcalID);
        // Update row
        return this.database.update(EVENTS_TABLE, cValues, ID + "=" + e_id, null) > 0;
    }

    // Query method to get all lessons in table
    Cursor getAllItems() {
        return this.database.query(EVENTS_TABLE, EVENT_COLS, null, null, null, null, DATE);
    }

    // Database helper to create and update database
    private class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DB_CREATE = "CREATE TABLE " + EVENTS_TABLE + " (" + ID +
                " INTEGER PRIMARY KEY AUTOINCREMENT, " + TITLE + " TEXT, " + DATE + " LONG, " +
                PREP_TIME + " INT, " + TRANSPORT + " TEXT, " + LOCATION + " TEXT, " + PLACE_ID +
                " TEXT, " + GCAL_ID + " TEXT);";

        DatabaseHelper(Context c, String name, SQLiteDatabase.CursorFactory fct, int version) {
            super(c, name, fct, version);
        }

        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL(DB_CREATE);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS " + EVENTS_TABLE);
            onCreate(db);
        }
    }
}

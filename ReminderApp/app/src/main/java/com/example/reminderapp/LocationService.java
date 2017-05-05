package com.example.reminderapp;

import android.app.AlarmManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Calendar;


public class LocationService extends Service
{
    public static final String BROADCAST_ACTION = "com.example.reminderapp.LocationService.REQUEST_PROCESSED";
    public static final String BROADCAST_DELETE = "com.example.reminderapp.LocationService.DELETE_EVENT";
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    public LocationManager locationManager;
    public MyLocationListener listener;
    public Location previousBestLocation = null;
    private DatabaseAdapter dbAdapter;
    // The next event
    private Event nextEvent;
    // Time to travel to next event from current location in seconds
    private Integer travelTime;
    LocalBroadcastManager broadcaster;
    private Context context;

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String DATE = "date";
    private static final String PREP_TIME = "prep_time";
    private static final String TRANSPORT = "transport";
    private static final String LOCATION = "location";
    private static final String PLACE_ID = "place_id";
    private static final String GCAL_ID = "gcal_id";
    private static final String DEPART_TIME = "depart_time";
    private static final String MESSAGE = "MESSAGE";
    private static final String URL_BASE = "https://maps.googleapis.com/maps/api/directions/json?";
    private static final String API_KEY_URL = "&key=AIzaSyBtH-O0z7HEEjoTxdTnvU6KH2yJxnmmBRw";

    int counter = 0;

    Alarm alarm = new Alarm();

    @Override
    public void onCreate()
    {
        super.onCreate();
        this.dbAdapter = DatabaseAdapter.getInstance(getApplicationContext());
        this.dbAdapter.open();
        broadcaster = LocalBroadcastManager.getInstance(this);
        getNextEvent();
        this.context = getApplicationContext();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId)
    {
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        listener = new MyLocationListener();
        try {
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 10000, 0, listener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 10000, 0, listener);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent)
    {
        return null;
    }

    public void getNextEvent() {
        Cursor cursor = this.dbAdapter.getAllItems();
        if (cursor.moveToFirst()) {
            int idIndex = cursor.getColumnIndex(ID);
            int titleIndex = cursor.getColumnIndex(TITLE);
            int dateIndex = cursor.getColumnIndex(DATE);
            int prepTimeIndex = cursor.getColumnIndex(PREP_TIME);
            int transportIndex = cursor.getColumnIndex(TRANSPORT);
            int locationIndex = cursor.getColumnIndex(LOCATION);
            int placeIDIndex = cursor.getColumnIndex(PLACE_ID);
            int gcalIDIndex = cursor.getColumnIndex(GCAL_ID);
            int departIndex = cursor.getColumnIndex(DEPART_TIME);
            // Get components to create new lesson
            int id = cursor.getInt(idIndex);
            String title = cursor.getString(titleIndex);
            Calendar date = Calendar.getInstance();
            date.clear();
            date.setTimeInMillis(cursor.getLong(dateIndex));
            Integer prepTime = cursor.getInt(prepTimeIndex);
            String transport = cursor.getString(transportIndex);
            String location = cursor.getString(locationIndex);
            String placeID = cursor.getString(placeIDIndex);
            String gcalID = cursor.getString(gcalIDIndex);
            Calendar departTime = Calendar.getInstance();
            departTime.clear();
            departTime.setTimeInMillis(cursor.getLong(departIndex));
            // Create event and add to array
            nextEvent = new Event(id, title, date, prepTime, transport, location, placeID, gcalID, departTime);
        } else {
            nextEvent = null;
        }
        cursor.close();
    }

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            // A new location is always better than no location
            return true;
        }

        // Check whether the new location fix is newer or older
        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        // If it's been more than two minutes since the current location, use the new location
        // because the user has likely moved
        if (isSignificantlyNewer) {
            return true;
            // If the new location is more than two minutes older, it must be worse
        } else if (isSignificantlyOlder) {
            return false;
        }

        // Check whether the new location fix is more or less accurate
        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        // Check if the old and new location are from the same provider
        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        // Determine location quality using a combination of timeliness and accuracy
        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Override
    public void onDestroy() {
        // handler.removeCallbacks(sendUpdatesToUI);
        super.onDestroy();
        Log.v("STOP_SERVICE", "DONE");
        locationManager.removeUpdates(listener);
    }

    public static Thread performOnBackgroundThread(final Runnable runnable) {
        final Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    runnable.run();
                } finally {

                }
            }
        };
        t.start();
        return t;
    }

    /**
     * Updates travel time
     */
    public void updateTime() {
        if (nextEvent != null) {
            String origin = "origin=" + previousBestLocation.getLatitude() + "," + previousBestLocation.getLongitude();
            String destination = "destination=place_id:" + nextEvent.placeID;
            String mode = "mode=" + nextEvent.transport.toLowerCase();
            String query = URL_BASE + origin + "&" + destination + "&" + mode + "&" + API_KEY_URL;
            new DirectionsDownload().execute(query);
        } else {
            Intent intent = new Intent(BROADCAST_ACTION);
            intent.putExtra(MESSAGE, "No events");
            broadcaster.sendBroadcast(intent);
        }
    }

    public void sendTime() {
        Intent intent = new Intent(BROADCAST_ACTION);
        if (travelTime != null) {
            AlarmManager mgrAlarm = (AlarmManager) context.getSystemService(ALARM_SERVICE);

            Calendar currTime = Calendar.getInstance();
            long calTime = currTime.getTimeInMillis() / 1000;
            long eventTime = nextEvent.date.getTimeInMillis() / 1000;
            long prepTime = 60 * nextEvent.prepTime;
            String message = "";
            long timeDiff = eventTime - calTime - travelTime - prepTime;
            if (timeDiff > 0) {
                message += "Get ready in: ";
                alarm.cancelAlarm(this);
                alarm.setAlarm(this, 2, nextEvent.title, (int) timeDiff);
            } else if ((timeDiff += prepTime) > 0) {
                alarm.cancelAlarm(this);
                alarm.setAlarm(this,1,nextEvent.title, (int) timeDiff);
                message += "Leave in: ";
            } else if ((timeDiff += travelTime) > 0) {
                alarm.cancelAlarm(this);
                alarm.setAlarm(this,0,nextEvent.title, (int) timeDiff);
                message += "Time until event: ";
            } else {
                Intent deleteIntent = new Intent(BROADCAST_DELETE);
                dbAdapter.removeItem(nextEvent.id);
                getNextEvent();
                broadcaster.sendBroadcast(deleteIntent);
            }
            if (timeDiff >= 0) {
                String openColor = "<font color='#";
                //noinspection ResourceType
                String color = getResources().getString(R.color.colorGreen).substring(3);
                String closeColor = "'>";
                int time = (int) timeDiff;
                int numberOfDays;
                int numberOfHours;
                int numberOfMinutes;
                numberOfDays = time / 86400;
                numberOfHours = (time % 86400 ) / 3600 ;
                numberOfMinutes = ((time % 86400 ) % 3600 ) / 60;

                String timeString = "";
                if (numberOfDays > 0) {
                    String days = "day";
                    if (numberOfDays != 1) {
                        days += "s";
                    }
                    timeString += numberOfDays + days;
                }
                if (numberOfHours > 0) {
                    if (timeString.length() > 0) {
                        timeString += ", ";
                    }
                    String hours = "hour";
                    if (numberOfHours != 1) {
                        hours += "s";
                    }
                    timeString += numberOfHours + hours;
                }
                if (numberOfMinutes > 0) {
                    if (timeString.length() > 0) {
                        timeString += ", ";
                    }
                    String minutes = "minute";
                    if (numberOfMinutes != 1) {
                        minutes += "s";
                    }
                    timeString += numberOfMinutes + minutes;
                }
                String finish = "</font>";
                message += openColor + color + closeColor + timeString + finish;
                intent.putExtra(MESSAGE, message);
            } else {
                intent.putExtra(MESSAGE, getResources().getString(R.string.calculating));
            }
            broadcaster.sendBroadcast(intent);
        }
    }

    private class MyLocationListener implements LocationListener
    {
        public void onLocationChanged(final Location loc)
        {
            Log.i("***********************", "Location changed");
            if(isBetterLocation(loc, previousBestLocation)) {
                previousBestLocation = loc;
                getNextEvent();
                updateTime();
            }
        }

        public void onProviderDisabled(String provider)
        {
//            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }

        public void onProviderEnabled(String provider)
        {
//            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
        }

        public void onStatusChanged(String provider, int status, Bundle extras)
        {

        }
    }

    private class DirectionsDownload extends AsyncTask<String, Integer, Integer> {

        String urlStr = "https://maps.googleapis.com/maps/api/directions/json?origin=place_id:ChIJNwXfIuAEyIkRMlSZouZry18&destination=place_id:ChIJRVY_etDX3IARGYLVpoq7f68&mode=DRIVING&key=AIzaSyBtH-O0z7HEEjoTxdTnvU6KH2yJxnmmBRw";

        @Override
        protected Integer doInBackground(String... urls) {
            try {
                URL url = new URL(urls[0]);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                StringBuffer response = new StringBuffer();

                int responseCode = conn.getResponseCode();
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

                    String inputLine;

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }

                    in.close();
                } else {
                    Log.d("ERROR","Cannot get JSON");
                }

                JSONObject json = new JSONObject(response.toString());
                JSONObject route1 = json.getJSONArray("routes").getJSONObject(0);
                JSONObject leg1 = route1.getJSONArray("legs").getJSONObject(0);
                JSONObject duration = leg1.getJSONObject("duration");

                return duration.getInt("value");

            } catch (Exception e) {
                e.printStackTrace();
            }


            return -1;
        }

        @Override
        protected void onPostExecute(Integer result) {
//            nextEvent.setText(result.toString());
            travelTime = result;
            sendTime();
        }
    }



}
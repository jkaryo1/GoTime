package com.example.reminderapp;

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

    @Override
    public void onCreate()
    {
        super.onCreate();
        this.dbAdapter = DatabaseAdapter.getInstance(getApplicationContext());
        this.dbAdapter.open();
        broadcaster = LocalBroadcastManager.getInstance(this);
        getNextEvent();
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
        Log.d("OUTSIDE_UPDATE", "outside");
        if (nextEvent != null) {
            Log.d("INSIDE_UPDATE", "inside");
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
        Log.d("OUTSIDE_SEND", "outside send");
        if (travelTime != null) {
            Log.d("INSIDE_SEND", "inside send");
            Calendar currTime = Calendar.getInstance();
            long calTime = currTime.getTimeInMillis() / 1000;
            long eventTime = nextEvent.date.getTimeInMillis() / 1000;
            long prepTime = 60 * nextEvent.prepTime;
            String message = "";
            long timeDiff = eventTime - calTime - travelTime - prepTime;
            if (timeDiff > 0) {
                message += "Get ready in: ";
            } else if ((timeDiff += prepTime) > 0) {
                message += "Leave in: ";
            } else if ((timeDiff += travelTime) > 0) {
                message += "Time until event: ";
            } else {
                dbAdapter.removeItem(nextEvent.id);
                getNextEvent();
            }
            String openColor = "<font color='#";
            //noinspection ResourceType
            String color = getResources().getString(R.color.colorGreen).substring(3);
            String closeColor = "'>";
            String time = (int)(timeDiff / 60) + " minutes";
            String finish = "</font>";
            message += openColor + color + closeColor + time + finish;
            Log.d("MESSAGE", message);
            intent.putExtra(MESSAGE, message);
        } else {
            intent.putExtra(MESSAGE, "No events");
        }
        broadcaster.sendBroadcast(intent);
    }

    private class MyLocationListener implements LocationListener
    {
        public void onLocationChanged(final Location loc)
        {
            Log.i("***********************", "Location changed");
            if(isBetterLocation(loc, previousBestLocation)) {
                previousBestLocation = loc;
                Log.d("LISTENER", "listener");
                getNextEvent();
                updateTime();
            }
        }

        public void onProviderDisabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Disabled", Toast.LENGTH_SHORT ).show();
        }

        public void onProviderEnabled(String provider)
        {
            Toast.makeText( getApplicationContext(), "Gps Enabled", Toast.LENGTH_SHORT).show();
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
                Log.d("DURATION_TIME", String.valueOf(duration.getInt("value")));

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
            Log.d("POST", String.valueOf(travelTime));
            sendTime();
        }
    }



}
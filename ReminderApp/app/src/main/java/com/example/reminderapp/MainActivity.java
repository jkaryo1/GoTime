package com.example.reminderapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.BufferedReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ArrayList<Object> eventArrayList;
//    private String[] titles = {"Event 1", "Event 2", "Event 3", "Event 4", "Event 5", "Event 6", "Event 7", "Event 8"};
//    private String[] dates = {"04/10/2017", "04/11/2017", "04/11/2017", "04/12/2017", "04/12/2017", "04/12/2017", "04/14/2017", "04/18/2017"};
//    private String[] times = {"02:23 PM", "11:38 AM", "01:47 PM", "09:10 AM", "12:02 PM", "03:52 PM", "10:20 AM", "05:35 PM"};
//    private Integer[] prepTimes = {15, 5, 20, 10, 30, 35, 5, 15};
//    private String[] transports = {"Driving", "Walking", "Driving", "Biking", "Walking", "Biking", "Driving", "Biking"};
//    private String[] locations = {"Location 1", "Location 2", "Location 3", "Location 4", "Location 5", "Location 6", "Location 7", "Location 8"};
    private RecyclerView recyclerView;
    private SearchView searchView;
    private EventListAdapter adapter;
    private DatabaseAdapter dbAdapter;
    private TextView nextEvent;
    private BroadcastReceiver receiver;
    private BroadcastReceiver deleteReceiver;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Intent servIntent = new Intent(getApplicationContext(), LocationService.class);
        getApplicationContext().startService(servIntent);

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String s = intent.getStringExtra(MESSAGE);
                Log.d("MAIN", s);
                nextEvent.setText(fromHtml(s));
            }
        };

        deleteReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                updateArray();
            }
        };

        /*Set up Toolbar, hide default title*/
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        this.dbAdapter = DatabaseAdapter.getInstance(getApplicationContext());
        this.dbAdapter.open();

//        this.searchView = (SearchView) findViewById(R.id.event_search);
        this.nextEvent = (TextView) findViewById(R.id.next_event_time);

        this.searchView = (SearchView) findViewById(R.id.event_search);
        this.searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        this.eventArrayList = new ArrayList<>();
        this.recyclerView = (RecyclerView) findViewById(R.id.event_recycler_view);
//        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
//        this.recyclerView.addItemDecoration(divider);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));


        this.adapter = new EventListAdapter(this.eventArrayList, this);
        recyclerView.setAdapter(this.adapter);
        registerForContextMenu(recyclerView);
    }

    // Update array when fragment comes back into display
    @Override
    public void onResume() {
        this.nextEvent.setText(getResources().getString(R.string.calculating));
        updateArray();
        int i = 0;
        while (i < this.eventArrayList.size()) {
            Event e = (Event) this.eventArrayList.get(i);
            if (i == 0 || !e.getDate().equals(((Event) this.eventArrayList.get(i - 1)).getDate())) {
                this.eventArrayList.add(i, e.getDate());
                i++;
            }
            i++;
        }
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        LocalBroadcastManager.getInstance(this).registerReceiver(deleteReceiver, new IntentFilter(LocationService.BROADCAST_DELETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(receiver, new IntentFilter(LocationService.BROADCAST_ACTION));
    }

    @Override
    protected void onStop() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(deleteReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(receiver);
        super.onStop();
    }

    // Updates the array of displayed lessons
    public void updateArray() {
        // Set cursor at head of results from query to get all lessons
        Cursor cursor = this.dbAdapter.getAllItems();
        this.eventArrayList.clear();
        if (cursor.moveToFirst()) {
            do {
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
                Event result = new Event(id, title, date, prepTime, transport, location, placeID, gcalID, departTime);
                this.eventArrayList.add(result);
            } while (cursor.moveToNext());
        }
        cursor.close();
        // Tell adapter to update info
        this.adapter.notifyDataSetChanged();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem item = menu.findItem(R.id.delete_event_button);
        item.setVisible(false);
        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        MenuItem newEvent = menu.findItem(R.id.new_event_button);
        Drawable plus = ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_input_add, null);
        if (plus != null) {
            plus.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
        }
        newEvent.setIcon(plus);

        MenuItem settingsButton = menu.findItem(R.id.action_settings);
        Drawable gear = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_icon, null);
        if (gear != null) {
            gear.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
        }
        settingsButton.setIcon(gear);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.action_settings:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.transition.enter, R.transition.stack);
                return true;

            case R.id.new_event_button:
                intent = new Intent(this, EventActivity.class);
                startActivity(intent);
                overridePendingTransition(R.transition.enter, R.transition.stack);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    //From Rackney on StackOverflow
    @SuppressWarnings("deprecation")
    public static Spanned fromHtml(String html){
        Spanned result;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
            result = Html.fromHtml(html,Html.FROM_HTML_MODE_LEGACY);
        } else {
            result = Html.fromHtml(html);
        }
        return result;
    }



}

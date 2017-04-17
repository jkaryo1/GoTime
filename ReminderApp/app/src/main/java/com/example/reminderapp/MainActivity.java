package com.example.reminderapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
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
import android.widget.TextView;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private ArrayList<Object> eventArrayList;
    private String[] titles = {"Event 1", "Event 2", "Event 3", "Event 4", "Event 5", "Event 6", "Event 7", "Event 8"};
    private String[] dates = {"04/10/2017", "04/11/2017", "04/11/2017", "04/12/2017", "04/12/2017", "04/12/2017", "04/14/2017", "04/18/2017"};
    private String[] times = {"02:23 PM", "11:38 AM", "01:47 PM", "09:10 AM", "12:02 PM", "03:52 PM", "10:20 AM", "05:35 PM"};
    private Integer[] prepTimes = {15, 5, 20, 10, 30, 35, 5, 15};
    private String[] transports = {"Driving", "Walking", "Driving", "Biking", "Walking", "Biking", "Driving", "Biking"};
    private String[] locations = {"Location 1", "Location 2", "Location 3", "Location 4", "Location 5", "Location 6", "Location 7", "Location 8"};
    private RecyclerView recyclerView;
    private SearchView searchView;
    private EventListAdapter adapter;
    private TextView nextEvent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Set up Toolbar, hide default title*/
        this.toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

//        this.searchView = (SearchView) findViewById(R.id.event_search);
        this.nextEvent = (TextView) findViewById(R.id.next_event_time);
        String base = "Get ready in: ";
        String openColor = "<font color='#";
        //noinspection ResourceType
        String color = getResources().getString(R.color.colorGreen).substring(3);
        String closeColor = "'>";
        String time = "15 minutes";
        String finish = "</font>";
        String full = base + openColor + color + closeColor + time + finish;
        this.nextEvent.setText(fromHtml(full));

        this.searchView = (SearchView) findViewById(R.id.event_search);
        this.searchView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchView.setIconified(false);
            }
        });

        this.eventArrayList = new ArrayList<>();
        int numDays = 0;
        for(int i = 0; i < titles.length; i++){
            Event e = new Event(this.titles[i], this.dates[i], this.times[i], this.prepTimes[i],
                    this.transports[i], this.locations[i]);
            if (i == 0 || !e.getDate().equals(((Event) this.eventArrayList.get(i + numDays - 1)).getDate())) {
                this.eventArrayList.add(i + numDays, e.getDate());
                numDays++;
            }
            this.eventArrayList.add(e);
        }
        this.recyclerView = (RecyclerView) findViewById(R.id.event_recycler_view);
//        DividerItemDecoration divider = new DividerItemDecoration(recyclerView.getContext(), LinearLayoutManager.VERTICAL);
//        this.recyclerView.addItemDecoration(divider);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        updateUI();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
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

//        MenuItem settings = menu.findItem(R.id.action_settings);
        Drawable gear = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_settings_icon, null);
        if (gear != null) {
            gear.setColorFilter(ContextCompat.getColor(MainActivity.this, R.color.white), PorterDuff.Mode.SRC_IN);
        }
//        settings.setIcon(gear);

        toolbar.setNavigationIcon(gear);


        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;

        switch (item.getItemId()) {
            case android.R.id.home:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                overridePendingTransition(R.transition.unstack, R.transition.exit);
                return true;

            case R.id.new_event_button:
                intent = new Intent(this, AddEventActivity.class);
                startActivity(intent);
                overridePendingTransition(R.transition.enter, R.transition.stack);
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    private void updateUI(){
        this.adapter = new EventListAdapter(this.eventArrayList, getApplicationContext());
        recyclerView.setAdapter(this.adapter);
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

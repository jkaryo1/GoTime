package com.example.reminderapp;

import android.content.Intent;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /*Set up Toolbar, hide default title*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }
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
        Drawable plus = ResourcesCompat.getDrawable(getResources(), R.drawable.ic_plus, null);
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

}

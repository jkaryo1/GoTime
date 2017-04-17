package com.example.reminderapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView title;
    private EditText defaultPrepTime;
    private RelativeLayout alarmLayout;
    private RelativeLayout transportLayout;
    public SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

         /*Set up Toolbar, hide default title*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        /*Change Toolbar title to match activity*/
        title = (TextView) findViewById(R.id.title);
        title.setText(R.string.action_settings);

        this.defaultPrepTime = (EditText) findViewById(R.id.defaultPrepTime);
        this.alarmLayout = (RelativeLayout) findViewById(R.id.alarm_spinner_view);
        this.transportLayout = (RelativeLayout) findViewById(R.id.transport_spinner_view);


        this.defaultPrepTime.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        this.transportLayout.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        this.alarmLayout.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);


        /*Setting upp the SharedPreferences object*/
        Context context = SettingsActivity.this;
        this.sharedPref = context.getSharedPreferences(
                getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.enter, R.transition.stack);
    }

    public void onSave() {
//        Spinner s = (Spinner) findViewById(R.id.alarmTypeSpinner);
//        Spinner t = (Spinner) findViewById(R.id.defaultTransportMethod);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.prep_time), this.defaultPrepTime.getText().toString() + " minutes");
//        editor.putString(getString(R.string.alarm_type), s.getSelectedItem().toString());
//        editor.putString(getString(R.string.transport_method), t.getSelectedItem().toString());
        editor.commit();
    }

    /**
    public void onResume() {
        EditText e = (EditText) findViewById(R.id.defaultPrepTime);
        Spinner s = (Spinner) findViewById(R.id.alarmType);
        Spinner t = (Spinner) findViewById(R.id.defaultTransportMethod);
        e.setText(R.string.prep_time);
        String alarm = getString(R.string.alarm_type);
        String transport = getString(R.string.transport_method);
        switch (alarm) {
            case "Alarm Sound #1" :
                s.setSelection(0);
                break;
            case "Alarm Sound #2" :
                s.setSelection(1);
                break;
            case "Alarm Sound #3" :
                s.setSelection(2);
                break;
            default:
                s.setSelection(0);
                break;
        }
        switch (transport) {
            case "Walking":
                t.setSelection(0);
                break;
            case "Biking":
                t.setSelection(1);
                break;
            case "Driving":
                t.setSelection(2);
                break;
            default:
                t.setSelection(0);
                break;
        }
    }*/
}

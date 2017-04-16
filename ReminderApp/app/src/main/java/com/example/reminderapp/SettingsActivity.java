package com.example.reminderapp;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView title;
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
        EditText e = (EditText) findViewById(R.id.defaultPrepTime);
        Spinner s = (Spinner) findViewById(R.id.alarmType);
        Spinner t = (Spinner) findViewById(R.id.defaultTransportMethod);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.prep_time), e.getText().toString() + " minutes");
        editor.putString(getString(R.string.alarm_type), s.getSelectedItem().toString());
        editor.putString(getString(R.string.transport_method), t.getSelectedItem().toString());
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

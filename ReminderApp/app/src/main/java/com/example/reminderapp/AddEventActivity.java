package com.example.reminderapp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

public class AddEventActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView title;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

          /*Set up Toolbar, hide default title*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        /*Change Toolbar title to match activity*/
        title = (TextView) findViewById(R.id.title);
        title.setText(R.string.add_event);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.unstack, R.transition.exit);
    }
}

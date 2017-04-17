package com.example.reminderapp;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.ResourceBundle;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.res.Resources;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;

public class AddEventActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView title;
    private EditText dateView;
    private EditText timeView;
    private Button saveButton;
    private EditText titleInput;
    private EditText prepTimeInput;
    private EditText locationInput;
    private Spinner transportMethod;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        final Calendar myCalendar = Calendar.getInstance();

          /*Set up Toolbar, hide default title*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        /*Change Toolbar title to match activity*/
        title = (TextView) findViewById(R.id.title);
        title.setText(R.string.add_event);


        /*datePicker setup*/
        this.dateView = (EditText) findViewById(R.id.date);
        this.timeView = (EditText) findViewById(R.id.time);
        this.titleInput = (EditText) findViewById(R.id.title_input);
        this.prepTimeInput = (EditText) findViewById(R.id.prep_time);
        this.locationInput = (EditText) findViewById(R.id.loc_input);
        RelativeLayout transportLayout = (RelativeLayout) findViewById(R.id.transport_spinner_view);

        this.transportMethod = (Spinner) transportLayout.findViewById(R.id.transport_spinner);

        this.saveButton = (Button) findViewById(R.id.saveButton);


        this.saveButton.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

        this.titleInput.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        this.dateView.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        this.timeView.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        this.prepTimeInput.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        this.locationInput.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        transportLayout.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                myCalendar.set(Calendar.HOUR, hourOfDay);
                myCalendar.set(Calendar.MINUTE, minute);

                String myFormat = "hh:mm a"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                timeView.setText(sdf.format(myCalendar.getTime()));
            }

        };

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, monthOfYear);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                String myFormat = "MM/dd/yy"; //In which you need put here
                SimpleDateFormat sdf = new SimpleDateFormat(myFormat, Locale.US);
                dateView.setText(sdf.format(myCalendar.getTime()));
            }
        };

        this.dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new DatePickerDialog(dateView.getContext(), date, myCalendar.get(Calendar.YEAR),
                        myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();

            }
        });


        this.timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new TimePickerDialog(timeView.getContext(), time, myCalendar.get(Calendar.HOUR),
                        myCalendar.get(Calendar.MINUTE),false).show();

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.unstack, R.transition.exit);
    }
}

package com.example.reminderapp;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

public class EventActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView title;
    private EditText dateView;
    private EditText timeView;
    private Button saveButton;
    private Button cancelButton;
    private EditText titleInput;
    private EditText prepTimeInput;
    private EditText locationInput;
    private Spinner transportMethod;

    private boolean isExistingEvent;
    private Event event;
    private SharedPreferences sharedPref;
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        Context context = EventActivity.this;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        final Calendar myCalendar = Calendar.getInstance();

          /*Set up Toolbar, hide default title*/
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        /*Change Toolbar title to match activity*/
        title = (TextView) findViewById(R.id.title);

        /*datePicker setup*/
        this.dateView = (EditText) findViewById(R.id.date);
        this.timeView = (EditText) findViewById(R.id.time);
        this.titleInput = (EditText) findViewById(R.id.title_input);
        this.prepTimeInput = (EditText) findViewById(R.id.prep_time);
        this.locationInput = (EditText) findViewById(R.id.loc_input);
        RelativeLayout transportLayout = (RelativeLayout) findViewById(R.id.transport_spinner_view);

        this.transportMethod = (Spinner) transportLayout.findViewById(R.id.transport_spinner);

        this.saveButton = (Button) findViewById(R.id.save_button);
        this.cancelButton = (Button) findViewById(R.id.cancel_button);

        final Activity activity = this;


        this.saveButton.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

        this.titleInput.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        this.dateView.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        this.timeView.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        this.prepTimeInput.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        this.locationInput.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        transportLayout.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        setListeners(this.titleInput);
        setListeners(this.prepTimeInput);
        setListeners(this.locationInput);

        Intent intent = getIntent();
        this.isExistingEvent = intent.getBooleanExtra("EXISTING_EVENT", false);
        if (this.isExistingEvent) {
            title.setText(R.string.edit_event);
            int id = intent.getIntExtra("ID", -1);
            String title = intent.getStringExtra("TITLE");
            long dateMillis = intent.getLongExtra("DATE", 0);
            int prepTime = intent.getIntExtra("PREP_TIME", 0);
            String transport = intent.getStringExtra("TRANSPORT");
            String location = intent.getStringExtra("LOCATION");
            this.event = new Event(id, title, dateMillis, prepTime, transport, location);
            this.titleInput.setText(this.event.title);
            this.dateView.setText(this.event.getDate());
            this.timeView.setText(this.event.getTime());
            this.prepTimeInput.setText(String.valueOf(this.event.prepTime));
            this.locationInput.setText(this.event.location);
            switch (this.event.transport) {
                case "Driving":
                    this.transportMethod.setSelection(1);
                    break;
                case "Biking":
                    this.transportMethod.setSelection(2);
                    break;
                case "Walking": default:
                    this.transportMethod.setSelection(0);
                    break;
            }
        } else {
            int prepTime = sharedPref.getInt("PREP_TIME", 15);
            int transportType = sharedPref.getInt("TRANSPORT_TYPE", 0);
            title.setText(R.string.add_event);
            this.prepTimeInput.setText(String.valueOf(prepTime));
            this.transportMethod.setSelection(transportType);
        }

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


        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable icon = ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_menu_save, null);
                if (icon != null) {
                    icon.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                }
                // Create confirmation dialog
                // On confirm, save settings, update prefs, and create Toast
                if(isExistingEvent) {
                    new AlertDialog.Builder(activity).setTitle("Update Event")
                            .setMessage("Are you sure you want to update event?")
                            .setIcon(icon)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                    overridePendingTransition(R.transition.unstack, R.transition.exit);
                                    Toast.makeText(getApplicationContext(), "Event Updated", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    new AlertDialog.Builder(activity).setTitle("Add Event")
                            .setMessage("Are you sure you want to save event?")
                            .setIcon(icon)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                    overridePendingTransition(R.transition.unstack, R.transition.exit);
                                    Toast.makeText(getApplicationContext(), "Event Saved", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }

            }
        });

        this.cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable icon = ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_dialog_alert, null);
                if (icon != null) {
                    icon.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                }
                // Create confirmation dialog
                // On confirm, revert changes and create Toast
                if (isExistingEvent) {
                    new AlertDialog.Builder(activity).setTitle("Cancel Changes")
                            .setMessage("Are you sure you want to cancel changes?")
                            .setIcon(icon)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                    overridePendingTransition(R.transition.unstack, R.transition.exit);
                                    Toast.makeText(activity, "Changes Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                } else {
                    new AlertDialog.Builder(activity).setTitle("Cancel New Event")
                            .setMessage("Are you sure you want to cancel adding event?")
                            .setIcon(icon)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                    overridePendingTransition(R.transition.unstack, R.transition.exit);
                                    Toast.makeText(activity, "New Event Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(android.R.string.no, null).show();
                }

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        //Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        MenuItem plusItem = menu.findItem(R.id.new_event_button);
        MenuItem trashItem = menu.findItem(R.id.delete_event_button);
        MenuItem settingsItem = menu.findItem(R.id.action_settings);

        settingsItem.setVisible(false);
        plusItem.setVisible(false);
        if (this.isExistingEvent) {
            trashItem.setVisible(true);
        } else {
            trashItem.setVisible(false);
        }

        return true;
    }
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
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

            case R.id.delete_event_button:
                Drawable icon = ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_delete, null);
                if (icon != null) {
                    icon.setColorFilter(ContextCompat.getColor(this, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                }

                new AlertDialog.Builder(this).setTitle("Delete Event")
                        .setMessage("Are you sure you want to delete event?")
                        .setIcon(icon)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                                overridePendingTransition(R.transition.unstack, R.transition.exit);
                                Toast.makeText(getApplicationContext(), "Event Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void setListeners(final EditText et) {
        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setCursorVisible(true);
            }
        });
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    et.performClick();
                } else {
                    hideKeyboard(v, et);
                    et.clearFocus();
                }
            }
        });
        et.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (v.getId() == et.getId()) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard(v, et);
                    }
                }
                return false;
            }
        });
    }

    // Dismiss keyboard, control cursor behavior
    public void hideKeyboard(View v, EditText et) {
        et.setCursorVisible(false);
        et.clearFocus();

        final InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.unstack, R.transition.exit);
    }
}
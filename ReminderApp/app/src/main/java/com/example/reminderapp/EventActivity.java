package com.example.reminderapp;

import java.text.ParseException;
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
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.PlaceBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.location.places.ui.PlaceAutocompleteFragment;
import com.google.android.gms.location.places.ui.PlaceSelectionListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;

public class EventActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener {
    private GoogleMap mMap;
    private PlaceAutocompleteFragment autocompleteFragment;
    private Toolbar toolbar;
    private TextView title;
    private EditText dateView;
    private EditText timeView;
    private Button saveButton;
    private Button cancelButton;
    private EditText titleInput;
    private EditText prepTimeInput;
    private String placeIdInput;
    private String placeNameInput;
    private Spinner transportMethod;

    private DatabaseAdapter dbAdapter;
    private GoogleApiClient mGoogleApiClient;

    private boolean isExistingEvent;
    private Event event;
    private SharedPreferences sharedPref;

    private static final String ID = "id";
    private static final String TITLE = "title";
    private static final String DATE = "date";
    private static final String PREP_TIME = "prep_time";
    private static final String TRANSPORT = "transport";
    private static final String LOCATION = "location";
    private static final String PLACE_ID = "place_id";
    private static final String GCAL_ID = "gcal_id";
    private static final String DEPART_TIME = "depart_time";

    private static final String TIME_FORMAT = "h:mm a"; //In which you need put here
    private static final SimpleDateFormat stf = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
    private static final String DATE_FORMAT = "MM/dd/yy"; //In which you need put here
    private static final SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);
        final Context context = EventActivity.this;

        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this,0, this)
                .addApi(Places.GEO_DATA_API)
                .addApi(Places.PLACE_DETECTION_API)
                .build();

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        WorkaroundMapFragment mapFragment = (WorkaroundMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        this.autocompleteFragment = (PlaceAutocompleteFragment) getFragmentManager().findFragmentById(R.id.place_autocomplete_fragment);

        this.dbAdapter = DatabaseAdapter.getInstance(context);
        this.dbAdapter.open();

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            /*TODO get rid of pin on map on hitting x*/
            @Override
            public void onPlaceSelected(Place place) {
                // TODO: Get info about the selected place.
                mMap.clear();
                LatLngBounds mapViewport = place.getViewport();
                mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mapViewport,0));
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));
                placeIdInput = place.getId();
                placeNameInput = place.getName().toString();
                Log.i("onplaceselcted", "Place: " + place.getName());
            }

            @Override
            public void onError(Status status) {
                // TODO: Handle the error.
                Log.i("onerror", "An error occurred: " + status);
            }
        });

        View af = this.autocompleteFragment.getView();
        if (af != null) {
            View clearButton = af.findViewById(R.id.place_autocomplete_clear_button);
            if (clearButton != null) {
                clearButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        autocompleteFragment.setText("");
                        mMap.clear();
                    }
                });
            }
            EditText locInput = (EditText) af.findViewById(R.id.place_autocomplete_search_input);
            locInput.setTextColor(ContextCompat.getColor(this, R.color.darkGray));
            locInput.setTextSize(18f);
        }

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
        final RelativeLayout transportLayout = (RelativeLayout) findViewById(R.id.transport_spinner_view);

        this.transportMethod = (Spinner) transportLayout.findViewById(R.id.transport_spinner);

        this.saveButton = (Button) findViewById(R.id.save_button);
        this.cancelButton = (Button) findViewById(R.id.cancel_button);

        final Activity activity = this;


        this.saveButton.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            this.autocompleteFragment.getView().setBackground(ContextCompat.getDrawable(this, R.drawable.edit_text_field));
        } else {
            //noinspection deprecation
            this.autocompleteFragment.getView().setBackgroundDrawable(ContextCompat.getDrawable(this, R.drawable.edit_text_field));
        }
        setListeners(this.titleInput);
        setListeners(this.prepTimeInput);

        Intent intent = getIntent();
        this.isExistingEvent = intent.getBooleanExtra("EXISTING_EVENT", false);
        if (this.isExistingEvent) {
            title.setText(R.string.edit_event);
            int id = intent.getIntExtra(ID, -1);
            String title = intent.getStringExtra(TITLE);
            long dateMillis = intent.getLongExtra(DATE, 0);
            int prepTime = intent.getIntExtra(PREP_TIME, 0);
            String transport = intent.getStringExtra(TRANSPORT);
            String location = intent.getStringExtra(LOCATION);
            String placeID = intent.getStringExtra(PLACE_ID);
            String gcalID = intent.getStringExtra(GCAL_ID);
            long departTime = intent.getLongExtra(DEPART_TIME, 0);
            this.placeNameInput = location;
            this.placeIdInput = placeID;
            this.event = new Event(id, title, dateMillis, prepTime, transport, location, placeID, gcalID, departTime);
            this.titleInput.setText(this.event.title);
            this.dateView.setText(this.event.getDate());
            this.timeView.setText(this.event.getTime());
            String prepText = prepTime + "";
            if (prepTime == 1) {
                prepText += " minute";
            } else {
                prepText += " minutes";
            }
            this.prepTimeInput.setText(prepText);
            this.autocompleteFragment.setText(this.event.location);




            switch (this.event.transport) {
                case "Driving":
                    this.transportMethod.setSelection(1);
                    break;
                case "Bicycling":
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
            String prepText = prepTime + "";
            if (prepTime == 1) {
                prepText += " minute";
            } else {
                prepText += " minutes";
            }
            this.prepTimeInput.setText(prepText);
            this.transportMethod.setSelection(transportType);
        }

        final TimePickerDialog.OnTimeSetListener time = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                Calendar myCal = Calendar.getInstance();

                myCal.set(Calendar.HOUR_OF_DAY, hourOfDay);
                myCal.set(Calendar.MINUTE, minute);

                String time = stf.format(myCal.getTime());
                timeView.setText(time);
            }

        };

        final DatePickerDialog.OnDateSetListener date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear,
                                  int dayOfMonth) {
                Calendar myCal = Calendar.getInstance();

                myCal.set(Calendar.YEAR, year);
                myCal.set(Calendar.MONTH, monthOfYear);
                myCal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                dateView.setText(sdf.format(myCal.getTime()));
            }
        };

        this.dateView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentFocus() != null) {
                    getCurrentFocus().clearFocus();
                }
                dateView.requestFocus();
                String dateString = dateView.getText().toString();
                if (dateString.length() == 0) {
                    new DatePickerDialog(dateView.getContext(), date, myCalendar.get(Calendar.YEAR),
                            myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH)).show();
                } else {
                    SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
                    Calendar pickerDate = Calendar.getInstance();
                    pickerDate.clear();
                    try {
                        pickerDate.setTime(dateFormat.parse(dateString));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    new DatePickerDialog(dateView.getContext(), date, pickerDate.get(Calendar.YEAR),
                            pickerDate.get(Calendar.MONTH), pickerDate.get(Calendar.DAY_OF_MONTH)).show();
                }
            }
        });


        this.timeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getCurrentFocus() != null) {
                    getCurrentFocus().clearFocus();
                }
                timeView.requestFocus();
                String timeString = timeView.getText().toString();
                if (timeString.length() == 0) {
                    new TimePickerDialog(dateView.getContext(), time, myCalendar.get(Calendar.HOUR_OF_DAY),
                            myCalendar.get(Calendar.MINUTE), false).show();
                } else {
                    SimpleDateFormat timeFormat = new SimpleDateFormat(TIME_FORMAT, Locale.getDefault());
                    Calendar date = Calendar.getInstance();
                    date.clear();
                    try {
                        date.setTime(timeFormat.parse(timeString));
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                    new TimePickerDialog(timeView.getContext(), time, date.get(Calendar.HOUR_OF_DAY),
                            date.get(Calendar.MINUTE), false).show();
                }
            }
        });


        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Ensure all fields are completed
                Boolean incomplete = titleInput.getText().toString().equals("");
                if (!incomplete) {
                    incomplete = dateView.getText().toString().equals("");
                    if (!incomplete) {
                        incomplete = timeView.getText().toString().equals("");
                        if (!incomplete) {
                            incomplete = prepTimeInput.getText().toString().equals("");
                            if (!incomplete) {
                                incomplete = placeIdInput == null || placeIdInput.equals("");
                            }
                        }
                    }
                }
                if (incomplete) {
                    Drawable badIcon = ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_dialog_alert, null);
                    if (badIcon != null) {
                        badIcon.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                    }
                    new AlertDialog.Builder(activity).setTitle("Form Incomplete")
                            .setMessage("Fill out all empty fields before continuing.")
                            .setIcon(badIcon)
                            .setNeutralButton(android.R.string.ok, null).show();
                } else {
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
                                .setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        updateEvent();
                                        finish();
                                        overridePendingTransition(R.transition.unstack, R.transition.exit);
                                        Toast.makeText(getApplicationContext(), "Event Updated", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null).show();
                    } else {
                        new AlertDialog.Builder(activity).setTitle("Add Event")
                                .setMessage("Are you sure you want to save event?")
                                .setIcon(icon)
                                .setPositiveButton(R.string.save, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        createEvent();
                                        finish();
                                        overridePendingTransition(R.transition.unstack, R.transition.exit);
                                        Toast.makeText(getApplicationContext(), "Event Saved", Toast.LENGTH_SHORT).show();
                                    }
                                })
                                .setNegativeButton(R.string.cancel, null).show();
                    }
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
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                    overridePendingTransition(R.transition.unstack, R.transition.exit);
                                    Toast.makeText(activity, "Changes Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(R.string.no, null).show();
                } else {
                    new AlertDialog.Builder(activity).setTitle("Cancel New Event")
                            .setMessage("Are you sure you want to cancel adding event?")
                            .setIcon(icon)
                            .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    finish();
                                    overridePendingTransition(R.transition.unstack, R.transition.exit);
                                    Toast.makeText(activity, "New Event Cancelled", Toast.LENGTH_SHORT).show();
                                }
                            })
                            .setNegativeButton(R.string.no, null).show();
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
                        .setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                deleteEvent();
                                finish();
                                overridePendingTransition(R.transition.unstack, R.transition.exit);
                                Toast.makeText(getApplicationContext(), "Event Deleted", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(R.string.cancel, null).show();
                return true;

        }

        return super.onOptionsItemSelected(item);
    }

    public void setListeners(final EditText et) {
        final Context context = EventActivity.this;

        et.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                et.setCursorVisible(true);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                    et.setBackground(ContextCompat.getDrawable(context, R.drawable.edit_text_highlighted));
                } else {
                    //noinspection deprecation
                    et.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_text_highlighted));
                }
            }
        });
        et.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        et.setBackground(ContextCompat.getDrawable(context, R.drawable.edit_text_highlighted));
                    } else {
                        //noinspection deprecation
                        et.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_text_highlighted));
                    }
                    if (et.getId() == R.id.prep_time) {
                        String text = et.getText().toString();
                        int spaceIndex = text.indexOf(" ");
                        if (spaceIndex != -1) {
                            text = text.substring(0, spaceIndex);
                            et.setText(text);
                        }
                    }
                    et.performClick();
                } else {
                    hideKeyboard(v, et);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        et.setBackground(ContextCompat.getDrawable(context, R.drawable.edit_text_field));
                    } else {
                        //noinspection deprecation
                        et.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_text_field));
                    }
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
        if (et.getId() == R.id.prep_time) {
            String text = et.getText().toString();
            int spaceIndex = text.indexOf(" ");
            if (spaceIndex == -1) {
                if (text.equals("1")) {
                    text += " minute";
                } else {
                    text += " minutes";
                }
                et.setText(text);
            }
        }

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

    /**
     * Deletes current event being viewed in the database.
     */
    public void deleteEvent(){
        this.dbAdapter.removeItem((long) this.event.id);
    }

    /**
     * Updates current even being viewed in the database.
     * Only called if it is an existingevent
     */
    public void updateEvent() {
        try {
            int id = this.event.id;
            String title = this.titleInput.getText().toString();
            String dateString = this.dateView.getText().toString() + " " + this.timeView.getText().toString();
            SimpleDateFormat combined = new SimpleDateFormat(DATE_FORMAT + " " + TIME_FORMAT, Locale.getDefault());
            Calendar date = Calendar.getInstance();
            date.clear();
            date.setTime(combined.parse(dateString));
            String prepTimeString = this.prepTimeInput.getText().toString();
            int spaceIndex = prepTimeString.indexOf(" ");
            int prepTime = Integer.parseInt(prepTimeString.substring(0, spaceIndex));
            String transport = this.transportMethod.getSelectedItem().toString();
            /*Need to catch case where this.placeIdInput is null*/


            Event newEvent = new Event(id, title, date, prepTime, transport, placeNameInput,placeIdInput,"nullgcalEvent",date);
            this.dbAdapter.updateLesson((long) id, newEvent);


        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    public void createEvent() {
        try {
            String title = this.titleInput.getText().toString();
            String dateString = this.dateView.getText().toString() + " " + this.timeView.getText().toString();
            SimpleDateFormat combined = new SimpleDateFormat(DATE_FORMAT + " " + TIME_FORMAT, Locale.getDefault());
            Calendar date = Calendar.getInstance();
            date.clear();
            date.setTime(combined.parse(dateString));
            String prepTimeString = this.prepTimeInput.getText().toString();
            int spaceIndex = prepTimeString.indexOf(" ");
            int prepTime = Integer.parseInt(prepTimeString.substring(0, spaceIndex));
            String transport = this.transportMethod.getSelectedItem().toString();
            /*Need to catch case where this.placeIdInput is null*/

            Event newEvent = new Event(-1, title, date, prepTime, transport, placeNameInput,placeIdInput,"nullgcalEvent",date);
            dbAdapter.insertItem(newEvent);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        final ScrollView scrollView = (ScrollView) findViewById(R.id.scroll_view); //parent scrollview in xml, give your scrollview id value

        ((WorkaroundMapFragment) getSupportFragmentManager().findFragmentById(R.id.map))
                .setListener(new WorkaroundMapFragment.OnTouchListener() {
                    @Override
                    public void onTouch() {
                        scrollView.requestDisallowInterceptTouchEvent(true);
                    }
                });


        if (this.isExistingEvent) {
            Places.GeoDataApi.getPlaceById(mGoogleApiClient, this.event.placeID).setResultCallback(new ResultCallback<PlaceBuffer>() {
                @Override
                public void onResult(@NonNull PlaceBuffer places) {
                    if (places.getStatus().isSuccess() && places.getCount() > 0) {
                        final Place place = places.get(0);

                        mMap.clear();
                        LatLngBounds mapViewport = place.getViewport();
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(mapViewport,0));
                        mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(place.getName().toString()));


                    } else {
                        Log.e("OnMapReady", "Place not found");
                    }
                    places.release();
                }
            });
        } else {
            LatLng lastLoc = new LatLng(39, -76);
            mMap.moveCamera(CameraUpdateFactory.newLatLng(lastLoc));
        }


    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e("EventActivity", "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        // TODO(Developer): Check error code and notify the user of error state and resolution.
        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
        EventActivity.this.finish();
    }
}

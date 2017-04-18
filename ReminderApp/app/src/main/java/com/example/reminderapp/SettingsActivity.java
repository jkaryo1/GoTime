package com.example.reminderapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private TextView title;
    private EditText defaultPrepTime;
    private RelativeLayout alarmLayout;
    private RelativeLayout transportLayout;
    private SharedPreferences sharedPref;
    private Button saveButton;
    private Button cancelButton;
    private Spinner alarmSpinner;
    private Spinner transportSpinner;

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

        final Activity activity = this;
        /*Change Toolbar title to match activity*/
        title = (TextView) findViewById(R.id.title);
        title.setText(R.string.action_settings);

        this.defaultPrepTime = (EditText) findViewById(R.id.default_prep_time);
        this.alarmLayout = (RelativeLayout) findViewById(R.id.alarm_spinner_view);
        this.transportLayout = (RelativeLayout) findViewById(R.id.transport_spinner_view);
        this.saveButton = (Button) findViewById(R.id.save_button);
        this.cancelButton = (Button) findViewById(R.id.cancel_button);
        this.defaultPrepTime.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        this.transportLayout.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        this.alarmLayout.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.gray), PorterDuff.Mode.SRC_ATOP);
        this.saveButton.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

        /*Setting upp the SharedPreferences object*/
        Context context = SettingsActivity.this;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        this.alarmSpinner = (Spinner) alarmLayout.findViewById(R.id.alarm_spinner);
        this.transportSpinner = (Spinner) transportLayout.findViewById(R.id.transport_spinner);

        autofill();

        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable icon = ResourcesCompat.getDrawable(getResources(), android.R.drawable.ic_menu_save, null);
                if (icon != null) {
                    icon.setColorFilter(ContextCompat.getColor(activity, R.color.colorPrimary), PorterDuff.Mode.SRC_IN);
                }
                // Create confirmation dialog
                // On confirm, save settings, update prefs, and create Toast
                new AlertDialog.Builder(activity).setTitle("Update Settings")
                        .setMessage("Are you sure you want to save changes?")
                        .setIcon(icon)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                SharedPreferences.Editor peditor = sharedPref.edit();
                                peditor.putInt("PREP_TIME", Integer.parseInt(defaultPrepTime.getText().toString()));
                                peditor.putInt("ALARM_TYPE", alarmSpinner.getSelectedItemPosition());
                                peditor.putInt("TRANSPORT_TYPE", transportSpinner.getSelectedItemPosition());
                                peditor.apply();
                                Toast.makeText(getApplicationContext(), "Settings updated", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
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
                new AlertDialog.Builder(activity).setTitle("Cancel Changes")
                        .setMessage("Are you sure you want to cancel changes?")
                        .setIcon(icon)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                autofill();
                                Toast.makeText(activity, "Changes cancelled", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton(android.R.string.no, null).show();
            }
        });

        this.defaultPrepTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                defaultPrepTime.setCursorVisible(true);
            }
        });
        this.defaultPrepTime.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    defaultPrepTime.performClick();
                } else {
                    hideKeyboard(v);
                }
            }
        });
        this.defaultPrepTime.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (v.getId() == defaultPrepTime.getId()) {
                    if (actionId == EditorInfo.IME_ACTION_DONE) {
                        hideKeyboard(v);
                    }
                }
                return false;
            }
        });
    }

    public void autofill() {
        int prepTime = this.sharedPref.getInt("PREP_TIME", 15);
        int alarmType = this.sharedPref.getInt("ALARM_TYPE", 0);
        int transportType = this.sharedPref.getInt("TRANSPORT_TYPE", 0);

        this.defaultPrepTime.setText(String.valueOf(prepTime));
        this.alarmSpinner.setSelection(alarmType);
        this.transportSpinner.setSelection(transportType);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.enter, R.transition.stack);
    }

    // Dismiss keyboard, control cursor behavior
    public void hideKeyboard(View v) {
        this.defaultPrepTime.setCursorVisible(false);
        this.defaultPrepTime.clearFocus();

        final InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}

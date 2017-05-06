package com.example.reminderapp;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.preference.PreferenceManager;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.res.ResourcesCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
        this.saveButton.getBackground().setColorFilter(ContextCompat.getColor(this,R.color.colorPrimary), PorterDuff.Mode.MULTIPLY);

        /*Setting upp the SharedPreferences object*/
        final Context context = SettingsActivity.this;
        this.sharedPref = PreferenceManager.getDefaultSharedPreferences(context);

        this.alarmSpinner = (Spinner) alarmLayout.findViewById(R.id.alarm_spinner);
        this.transportSpinner = (Spinner) transportLayout.findViewById(R.id.transport_spinner);

        autofill();

        this.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean incomplete = defaultPrepTime.getText().toString().equals("");
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
                    new AlertDialog.Builder(activity).setTitle("Update Settings")
                            .setMessage("Are you sure you want to save changes?")
                            .setIcon(icon)
                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    SharedPreferences.Editor peditor = sharedPref.edit();
                                    String prepTime = defaultPrepTime.getText().toString();
                                    int spaceIndex = prepTime.indexOf(" ");
                                    if (spaceIndex != -1) {
                                        prepTime = prepTime.substring(0, spaceIndex);
                                    }
                                    peditor.putInt("PREP_TIME", Integer.parseInt(prepTime));
                                    peditor.putInt("ALARM_TYPE", alarmSpinner.getSelectedItemPosition());
                                    peditor.putInt("TRANSPORT_TYPE", transportSpinner.getSelectedItemPosition());
                                    peditor.apply();
                                    finish();
                                    overridePendingTransition(R.transition.unstack, R.transition.exit);
                                    Toast.makeText(getApplicationContext(), "Settings updated", Toast.LENGTH_SHORT).show();
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
                new AlertDialog.Builder(activity).setTitle("Cancel Changes")
                        .setMessage("Are you sure you want to cancel changes?")
                        .setIcon(icon)
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                finish();
                                overridePendingTransition(R.transition.unstack, R.transition.exit);
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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        v.setBackground(ContextCompat.getDrawable(context, R.drawable.edit_text_highlighted));
                    } else {
                        //noinspection deprecation
                        v.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_text_highlighted));
                    }
                    String text = defaultPrepTime.getText().toString();
                    int spaceIndex = text.indexOf(" ");
                    if (spaceIndex != -1) {
                        text = text.substring(0, spaceIndex);
                        defaultPrepTime.setText(text);
                    }
                    v.performClick();
                } else {
                    hideKeyboard(v);
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                        v.setBackground(ContextCompat.getDrawable(context, R.drawable.edit_text_field));
                    } else {
                        //noinspection deprecation
                        v.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.edit_text_field));
                    }
                    v.clearFocus();
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
        String prepText = prepTime + "";
        if (prepTime == 1) {
            prepText += " minute";
        } else {
            prepText += " minutes";
        }
        this.defaultPrepTime.setText(prepText);
        this.alarmSpinner.setSelection(alarmType);
        this.transportSpinner.setSelection(transportType);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.transition.unstack, R.transition.exit);
    }

    // Dismiss keyboard, control cursor behavior
    public void hideKeyboard(View v) {
        this.defaultPrepTime.setCursorVisible(false);
        this.defaultPrepTime.clearFocus();
        String text = this.defaultPrepTime.getText().toString();
        int spaceIndex = text.indexOf(" ");
        if (spaceIndex == -1 && !text.equals("")) {
            if (text.equals("1")) {
                text += " minute";
            } else {
                text += " minutes";
            }
            this.defaultPrepTime.setText(text);
        }

        final InputMethodManager imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }
}

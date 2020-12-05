package com.mdinolfo.izzsulin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private static final String myPref = "izzulin_prefs";

    private int carbFactor;
    private int insulinFactor;
    private int bloodTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // debug: uncomment the below line to clear all SharePreferences keys
        //getSharedPreferences(myPref, 0).edit().clear().commit();

        // open Preferences the first time you run the app
        SharedPreferences sp = getSharedPreferences(myPref,0);

        if ( loadPreferences() == false ) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
        } else {
            loadPreferences();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        int id = item.getItemId();
        switch (id) {
            case R.id.preferences:
                intent = new Intent(this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.help:
                intent = new Intent(this, AboutActivity.class);
                startActivity(intent);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private boolean loadPreferences() {
        boolean error = false;

        SharedPreferences sp = getSharedPreferences(myPref,0);
        carbFactor = sp.getInt("carbFactor",0);
        insulinFactor = sp.getInt("insulinFactor",0);
        bloodTarget = sp.getInt("bloodTarget",0);

        return (carbFactor > 0 && insulinFactor > 0 && bloodTarget > 0);
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    private double round4(double d) {
        return Math.round(d * 10000.0) / 10000.0;
    }

    public static double insulinRounding(double totalInsulin, double bloodInsulin) {
        double r;

        if (Double.compare(bloodInsulin,0.0) <= 0)
            totalInsulin -= 0.1;

        return Math.round(totalInsulin * 2) / 2.0;
    }

    public void calculateInsulin(View view) {
        String message = "";
        double carbEaten = 0.0;
        double bloodSugar = 0.0;
        boolean ok = true;

        //
        // collect input
        //
        EditText editText = (EditText) findViewById(R.id.carbEaten);
        if ( !isEmpty(editText) )
            carbEaten = Double.parseDouble(editText.getText().toString());

        editText = (EditText) findViewById(R.id.bloodSugar);
        if ( !isEmpty(editText) )
            bloodSugar = Double.parseDouble(editText.getText().toString());

        //
        // preferences set?
        //
        if ( loadPreferences() == false ) {
            //TextView textView = findViewById(R.id.settingsErrorMessage);
            //textView.setText("All values must be filled in to continue.");
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            ok = false;
        }

        //
        // input error checking
        //
        if ( ok && Double.compare(bloodSugar,0.0) <= 0 ) {
            message = "Please fill in blood sugar at the minimum.";
            ok = false;
        }

        //
        // emergency action for low insulin
        //
        if (ok && Double.compare(bloodSugar,80.0) <= 0) {
            message = "Please raise blood sugar before giving insulin.";
            ok = false;
        }

        //
        // run calculation
        //
        if (ok) {
            double bloodInsulin = 0.0;
            double carbInsulin = round4(carbEaten / carbFactor );
            if ( Double.compare(bloodSugar,bloodTarget) > 0 )
                bloodInsulin = round4((bloodSugar - bloodTarget) / insulinFactor);
            double totalInsulin = round4(carbInsulin + bloodInsulin);
            double roundInsulin = insulinRounding(totalInsulin,bloodInsulin);
            message = "Recommended dose: " + Double.toString(roundInsulin) + " units\n\n";
            message += "Total insulin: " + Double.toString(totalInsulin) + "\n";
            message += "Food insulin: " + Double.toString(carbInsulin) + "\n";
            message += "Blood insulin: " + Double.toString(bloodInsulin) + "\n";
        }

        TextView textView = findViewById(R.id.results);
        textView.setText(message);
    }
}
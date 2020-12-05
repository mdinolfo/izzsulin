package com.mdinolfo.izzsulin;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class SettingsActivity extends AppCompatActivity {

    private static final String myPref = "izzulin_prefs";

    private int carbFactor;
    private int insulinFactor;
    private int bloodTarget;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        loadPreferences();
    }

    private void loadPreferences() {
        SharedPreferences sp = getSharedPreferences(myPref,0);
        carbFactor = sp.getInt("carbFactor",0);
        insulinFactor = sp.getInt("insulinFactor",0);
        bloodTarget = sp.getInt("bloodTarget",0);

        EditText editText = (EditText) findViewById(R.id.carbFactorSetting);
        if ( carbFactor > 0 )
            editText.setText(Integer.toString(carbFactor));

        editText = (EditText) findViewById(R.id.insulinFactorSetting);
        if ( insulinFactor > 0 )
            editText.setText(Integer.toString(insulinFactor));

        editText = (EditText) findViewById(R.id.bloodTargetSetting);
        if ( bloodTarget > 0 )
            editText.setText(Integer.toString(bloodTarget));
    }

    private boolean isEmpty(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }

    public void savePreferences(View view) {
        boolean changesMade = false;
        SharedPreferences.Editor editor = getSharedPreferences(myPref,0).edit();

        EditText editText = (EditText) findViewById(R.id.carbFactorSetting);
        if ( !isEmpty(editText) ) {
            int newCarbFactor = Integer.parseInt(editText.getText().toString());

            if ( carbFactor != newCarbFactor ) {
                editor.putInt("carbFactor", newCarbFactor);
                carbFactor = newCarbFactor;
                changesMade = true;
            }
        }

        editText = (EditText) findViewById(R.id.insulinFactorSetting);
        if ( !isEmpty(editText) ) {
            int newInsulinFactor = Integer.parseInt(editText.getText().toString());

            if ( insulinFactor != newInsulinFactor ) {
                editor.putInt("insulinFactor", newInsulinFactor);
                insulinFactor = newInsulinFactor;
                changesMade = true;
            }
        }

        editText = (EditText) findViewById(R.id.bloodTargetSetting);
        if ( !isEmpty(editText) ) {
            int newBloodTarget = Integer.parseInt(editText.getText().toString());

            if ( bloodTarget != newBloodTarget ) {
                editor.putInt("bloodTarget", newBloodTarget);
                bloodTarget = newBloodTarget;
                changesMade = true;
            }
        }

        if ( bloodTarget > 0 && insulinFactor > 0 && carbFactor > 0 ) {
            if (changesMade)
                editor.commit();

            Intent intent = getIntent();
            setResult(RESULT_OK, intent);
            finish();
        }
    }
}
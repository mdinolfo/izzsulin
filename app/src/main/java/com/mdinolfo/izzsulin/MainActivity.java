package com.mdinolfo.izzsulin;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
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
        double carbFactor = 0.0;
        double insulinFactor = 0.0;
        double carbEaten = 0.0;
        double bloodSugar = 0.0;
        boolean ok = true;

        //
        // collect input
        //
        EditText editText = (EditText) findViewById(R.id.carbFactor);
        if ( !isEmpty(editText) )
            carbFactor = Double.parseDouble(editText.getText().toString());

        editText = (EditText) findViewById(R.id.insulinFactor);
        if ( !isEmpty(editText) )
            insulinFactor = Double.parseDouble(editText.getText().toString());

        editText = (EditText) findViewById(R.id.carbEaten);
        if ( !isEmpty(editText) )
            carbEaten = Double.parseDouble(editText.getText().toString());

        editText = (EditText) findViewById(R.id.bloodSugar);
        if ( !isEmpty(editText) )
            bloodSugar = Double.parseDouble(editText.getText().toString());

        //
        // input error checking
        //
        if ( Double.compare(carbFactor,0.0) <= 0 || Double.compare(insulinFactor,0.0) <= 0 ||
             Double.compare(carbEaten,0.0) <= 0 || Double.compare(bloodSugar,0.0) <= 0 ) {
            message = "All of the values must be filled in.\n\nPlease try again.";
            ok = false;
        }

        //
        // emergency action for low insulin
        //
        if (ok && Double.compare(bloodSugar,80.0) <= 0) {
            message = "Please raise blood sugar before giving insulin.\n";
            ok = false;
        }

        //
        // run calculation
        //
        if (ok) {
            double bloodInsulin = 0.0;
            double carbInsulin = round4(carbEaten / carbFactor );
            if (Double.compare(bloodSugar,180.0) > 0)
                bloodInsulin = round4((bloodSugar - 150) / insulinFactor);
            double totalInsulin = round4(carbInsulin + bloodInsulin);
            double roundInsulin = insulinRounding(totalInsulin,bloodInsulin);
            message = "Recommended dose: " + Double.toString(roundInsulin) + " units\n\n";
            message += "Total insulin: " + Double.toString(totalInsulin) + "\n";
            message += "Food insulin: " + Double.toString(carbInsulin) + "\n";
            message += "Blood sugar insulin: " + Double.toString(bloodInsulin) + "\n";
        }

        TextView textView = findViewById(R.id.results);
        textView.setText(message);
    }
}
package com.example.myfirstapp;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private Button button;
    private Spinner categorySpinner, fromSpinner, toSpinner;
    private EditText editAmount;
    private TextView textResult;

    private static final double MPG_TO_KML = 0.425;
    private static final double GAL_TO_L   = 3.785;
    private static final double NMI_TO_KM  = 1.852;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        categorySpinner = findViewById(R.id.spinnerCategory);
        fromSpinner = findViewById(R.id.spinner2);
        toSpinner = findViewById(R.id.spinner1);
        editAmount = findViewById(R.id.editAmount);
        textResult = findViewById(R.id.textResult);

        String[] categories = {"Currency", "Fuel & Distance", "Temperature"};
        ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                categories
        );
        categoryAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        categorySpinner.setAdapter(categoryAdapter);

        final String[] currencyUnits = {"USD", "AUD", "EUR", "JPY", "GBP"};
        final String[] fuelDistUnits = {"mpg", "km/L", "gal", "L", "nmi", "km"};
        final String[] tempUnits = {"C", "F", "K"};


        categorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selected = categorySpinner.getSelectedItem().toString();

                String[] units;
                if (selected.equals("Currency")) {
                    units = currencyUnits;
                } else if (selected.equals("Fuel & Distance")) {
                    units = fuelDistUnits;
                } else {
                    units = tempUnits;
                }

                ArrayAdapter<String> unitAdapter = new ArrayAdapter<>(
                        MainActivity.this,
                        android.R.layout.simple_spinner_item,
                        units
                );
                unitAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                fromSpinner.setAdapter(unitAdapter);
                toSpinner.setAdapter(unitAdapter);

                fromSpinner.setSelection(0);
                toSpinner.setSelection(Math.min(1, units.length - 1));

                textResult.setText("");
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String amountStr = editAmount.getText().toString().trim();
                if (TextUtils.isEmpty(amountStr)) {
                    editAmount.setError("Enter amount");
                    return;
                }

                double value;
                try {
                    value = Double.parseDouble(amountStr);
                } catch (NumberFormatException e) {
                    editAmount.setError("Invalid number");
                    return;
                }

                String from = fromSpinner.getSelectedItem().toString();
                String to = toSpinner.getSelectedItem().toString();

                try {
                    double result = convert(from, to, value);
                    textResult.setText(String.format("Result: %.2f %s", result, to));
                } catch (IllegalArgumentException e) {
                    textResult.setText(e.getMessage());
                }
            }
        });
    }

    private double convert(String from, String to, double value) {

        if (from.equals(to)) return value;

        if (isCurrency(from) && isCurrency(to)) {
            double usd;
            switch (from) {
                case "USD": usd = value; break;
                case "AUD": usd = value / 1.55; break;
                case "EUR": usd = value / 0.92; break;
                case "JPY": usd = value / 148.50; break;
                case "GBP": usd = value / 0.78; break;
                default: throw new IllegalArgumentException("Unsupported currency: " + from);
            }

            switch (to) {
                case "USD": return usd;
                case "AUD": return usd * 1.55;
                case "EUR": return usd * 0.92;
                case "JPY": return usd * 148.50;
                case "GBP": return usd * 0.78;
                default: throw new IllegalArgumentException("Unsupported currency: " + to);
            }
        }

        if (from.equals("mpg") && to.equals("km/L")) return value * MPG_TO_KML;
        if (from.equals("km/L") && to.equals("mpg")) return value / MPG_TO_KML;

        if (from.equals("gal") && to.equals("L")) return value * GAL_TO_L;
        if (from.equals("L") && to.equals("gal")) return value / GAL_TO_L;

        if (from.equals("nmi") && to.equals("km")) return value * NMI_TO_KM;
        if (from.equals("km") && to.equals("nmi")) return value / NMI_TO_KM;

        if (from.equals("C") && to.equals("F")) return (value * 1.8) + 32;
        if (from.equals("F") && to.equals("C")) return (value - 32) / 1.8;
        if (from.equals("C") && to.equals("K")) return value + 273.15;
        if (from.equals("K") && to.equals("C")) return value - 273.15;

        throw new IllegalArgumentException("Invalid conversion: " + from + " → " + to);
    }

    private boolean isCurrency(String unit) {
        return unit.equals("USD") || unit.equals("AUD") || unit.equals("EUR")
                || unit.equals("JPY") || unit.equals("GBP");
    }
}
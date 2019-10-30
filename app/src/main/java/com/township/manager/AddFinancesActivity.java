package com.township.manager;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputLayout;

public class AddFinancesActivity extends AppCompatActivity {

    AutoCompleteTextView paymentModeACTV;

    TextInputLayout paymentModeTIL;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_finances);

        paymentModeTIL = findViewById(R.id.add_cheque_payment_mode_til);

        paymentModeACTV = findViewById(R.id.add_upi_payment_mode_dropdown);

        String[] paymentModes = new String[] {"Cheque", "UPI"};
        ArrayAdapter<String> paymentModeAdapter = new ArrayAdapter<>(
                AddFinancesActivity.this,
                R.layout.dropdown_menu_popup_item,
                paymentModes);
        paymentModeACTV.setAdapter(paymentModeAdapter);
    }
}

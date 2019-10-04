package com.township.manager;

import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import androidx.appcompat.app.AppCompatActivity;

public class AddSecurityActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_security);

        String[] COMPLAINT_FILTER = new String[]{"Gate 1", "Gate 2", "Gate 2"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        COMPLAINT_FILTER);

        AutoCompleteTextView editTextFilledExposedDropdown = findViewById(R.id.gate_number_textinput_child);
        editTextFilledExposedDropdown.setAdapter(adapter);
    }
}

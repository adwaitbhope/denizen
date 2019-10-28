package com.township.manager;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.DialogFragment;

public class AddSecurityActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_security);

        Button from_button = findViewById(R.id.add_security_personnel_shift_from_button);
        Button till_button = findViewById(R.id.add_security_personnel_shift_till_button);
        from_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerClass();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });
        till_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerClass();
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_security_personnel_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Security");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//        TextView textView = (TextView) findViewById(R.id.textView);
//        textView.setText("Hour: " + hourOfDay + " Minute: " + minute);
        Toast.makeText(this, "Disla ka?", Toast.LENGTH_SHORT).show();
    }
}

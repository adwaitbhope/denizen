package com.township.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;

public class ResidentBookingHistoryActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_booking_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.resident_booking_history_toolbar);
//        toolbar.setTitleTextColor(getColor(R.color.secondaryColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Booking History");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);


    }
}

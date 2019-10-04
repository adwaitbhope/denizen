package com.township.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

public class RegistrationSuccessfulActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_successful);

        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String heading = intent.getStringExtra("heading");
        String description = intent .getStringExtra("description");

        Toolbar toolbar = (Toolbar) findViewById(R.id.registration_successful_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(title);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ((TextView) findViewById(R.id.registration_status_heading)).setText(heading);
        ((TextView) findViewById(R.id.registration_status_description)).setText(description);
    }
}

package com.township.manager;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.google.android.material.button.MaterialButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RegisterComplaintActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_complaint);

        Toolbar toolbar = (Toolbar) findViewById(R.id.register_complaint_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register Complaint");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        MaterialButton registerComplaintButton = findViewById(R.id.register_complaint_button);
        registerComplaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // query to register a new complaint

            }
        });

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
}

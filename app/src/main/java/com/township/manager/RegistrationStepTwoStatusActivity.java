package com.township.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RegistrationStepTwoStatusActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_step_two_status);

        Toolbar toolbar = (Toolbar) findViewById(R.id.registration_step_two_status_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Application Status");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button proceedButton = findViewById(R.id.registration_step_two_status_proceed_button);
        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationStepTwoStatusActivity.this, RegistrationSocietyStepTwo.class);
                startActivity(intent);
            }
        });

    }
}

package com.township.manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class RegistrationStepsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_steps);

        Toolbar toolbar = (Toolbar) findViewById(R.id.registration_steps_toolbar);
//        toolbar.setTitleTextColor(getColor(R.color.secondaryColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Registration Steps");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Button registrationStepOne = (Button) findViewById(R.id.registration_step_one_button);
        registrationStepOne.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegistrationStepsActivity.this, RegistrationStepOne.class);
                startActivity(intent);
            }
        });

        Button registrationStepTwo = (Button) findViewById(R.id.registration_step_two_button);
        registrationStepTwo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationStepsActivity.this, RegistrationSocietyStepTwo.class);
                startActivity(intent);
            }
        });


    }
}
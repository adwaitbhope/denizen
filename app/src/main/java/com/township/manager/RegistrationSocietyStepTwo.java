package com.township.manager;

import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.LinearLayout;

public class RegistrationSocietyStepTwo extends AppCompatActivity {

    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_society_step_two);

        mSlideViewPager = (ViewPager) findViewById(R.id.viewPager);
        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);
    }
}

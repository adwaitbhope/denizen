package com.township.manager;

import android.net.Uri;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

public class RegistrationSocietyStepTwo extends AppCompatActivity implements RegistrationSocietyStepTwoWingDetailsFragment.OnFragmentInteractionListener, RegistrationSocietyStepTwoAmenitiesDetailsFragment.OnFragmentInteractionListener, RegistrationSocietyStepTwoAdminLoginDetailsFragment.OnFragmentInteractionListener {

    public Button login;
    private TextView[] mDots;
    private ViewPager mSlideViewPager;
    private LinearLayout mDotLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_society_step_two);

        SliderAdapter sliderAdapter = new SliderAdapter(getSupportFragmentManager());
        sliderAdapter.addFragment(new RegistrationSocietyStepTwoWingDetailsFragment());
        sliderAdapter.addFragment(new RegistrationSocietyStepTwoAmenitiesDetailsFragment());
        sliderAdapter.addFragment(new RegistrationSocietyStepTwoAdminLoginDetailsFragment());

        mSlideViewPager = (ViewPager) findViewById(R.id.slideViewPager);
        mSlideViewPager.setAdapter(sliderAdapter);
        mSlideViewPager.setOffscreenPageLimit(2);

        mDotLayout = (LinearLayout) findViewById(R.id.dotsLayout);
        addDotsIndicator(0);

        mSlideViewPager.addOnPageChangeListener(viewListener);

    }
    public void addDotsIndicator(int position){


        mDots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i < mDots.length; i++){

            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#9702;"));
            mDots[i].setTextSize(35);

            mDotLayout.addView(mDots[i]);
        }

        if(mDots.length > 0){
            mDots[position].setText(Html.fromHtml("&#8226;"));
            mDots[position].setTextColor((getResources().getColor(R.color.colorAccent)));
        }
    }
    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);

        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

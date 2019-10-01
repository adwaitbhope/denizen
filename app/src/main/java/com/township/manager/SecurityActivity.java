package com.township.manager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class SecurityActivity extends AppCompatActivity implements ComplaintsListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        FloatingActionButton button = findViewById(R.id.add_new_security);
        SliderAdapter sliderAdapter = new SliderAdapter(Objects.requireNonNull(this).getSupportFragmentManager());

        // TODO: Change the fragments that are loading here
        sliderAdapter.addFragment(new ComplaintsListFragment(), "Pending");
        sliderAdapter.addFragment(new ComplaintsListFragment(), "Resolved");

        ViewPager mSlideViewPager = (ViewPager) findViewById(R.id.security_view_pager);
        mSlideViewPager.setAdapter(sliderAdapter);
        mSlideViewPager.setOffscreenPageLimit(1);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.security_tab_layout);
        tabLayout.setupWithViewPager(mSlideViewPager);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SecurityActivity.this, RegisterComplaintActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

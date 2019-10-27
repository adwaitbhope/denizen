package com.township.manager;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

public class SecurityActivity extends AppCompatActivity implements SecurityDesksListFragment.OnFragmentInteractionListener,SecurityPersonnelListFragment.OnFragmentInteractionListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        SliderAdapter sliderAdapter = new SliderAdapter(Objects.requireNonNull(this).getSupportFragmentManager());

        sliderAdapter.addFragment(new SecurityPersonnelListFragment(), "Personnel");
        sliderAdapter.addFragment(new SecurityDesksListFragment(), "Desks");

        ViewPager mSlideViewPager = (ViewPager) findViewById(R.id.security_view_pager);
        mSlideViewPager.setAdapter(sliderAdapter);
        mSlideViewPager.setOffscreenPageLimit(1);

        Toolbar toolbar = (Toolbar) findViewById(R.id.security_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Security");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.security_tab_layout);
        tabLayout.setupWithViewPager(mSlideViewPager);

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
    public void onFragmentInteraction(Uri uri) {

    }
}

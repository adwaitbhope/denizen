package com.township.manager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;

public class AdminHomeScreenActivity extends AppCompatActivity
        implements ComplaintsListFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener, ComplaintsFragment.OnFragmentInteractionListener, RegistrationSocietyStepTwoAmenitiesDetailsFragment.OnFragmentInteractionListener, RegistrationSocietyStepTwoAdminLoginDetailsFragment.OnFragmentInteractionListener {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();
        int firstNameCol, desCol, lastNameCol;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);
        TextView adminName, adminDesignation;
        adminDesignation = header.findViewById(R.id.navheader_admin_home_screen_designation_textview);
        adminName = header.findViewById(R.id.navheader_admin_home_screen_name_textview);
        firstNameCol = cursor.getColumnIndexOrThrow("First_Name");
        lastNameCol = cursor.getColumnIndexOrThrow("Last_Name");
        desCol = cursor.getColumnIndexOrThrow("Designation");
        cursor.moveToFirst();
        adminDesignation.setText(cursor.getString(desCol));
        adminName.setText(cursor.getString(firstNameCol) + " " + cursor.getString(lastNameCol));

        BottomNavigationView bottomNavigationView = findViewById(R.id.admin_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction;

                switch (menuItem.getItemId()) {
                    case R.id.admin_complaints:
                        transaction = getSupportFragmentManager().beginTransaction();
                        transaction.replace(R.id.admin_home_screen_fragment_area, new ComplaintsFragment());
                        transaction.commit();
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_intercom_admin) {

        } else if (id == R.id.nav_maintenance_admin) {

        } else if (id == R.id.nav_visitor_history_admin) {

        } else if (id == R.id.nav_admin_info_admin) {

        } else if (id == R.id.nav_security_list_admin) {

        } else if (id == R.id.nav_vendors_admin) {

        } else if (id == R.id.nav_wing_details_admin) {

        } else if (id == R.id.nav_amenities_admin) {
            Intent intent = new
                    Intent(AdminHomeScreenActivity.this, AmenitiesAdminContainerActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout_admin) {
            LogOutDialog logOutDialog = new LogOutDialog();
            logOutDialog.show(getSupportFragmentManager(), "Logout");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

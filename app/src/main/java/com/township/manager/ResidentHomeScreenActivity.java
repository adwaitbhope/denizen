package com.township.manager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class ResidentHomeScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_resident_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DBManager dbManager = new DBManager(getApplicationContext());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        TextView residentFlatNo = header.findViewById(R.id.resident_home_nav_header_flat_no);
        TextView residentName = header.findViewById(R.id.resident_home_nav_header_name);

        ImageButton editProfile = header.findViewById(R.id.resident_home_nav_header_edit_profile_button);

        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResidentHomeScreenActivity.this, EditProfileActivity.class);
                startActivity(intent);
            }
        });

        int flatNoCol, firstNameCol, lastNameCol, wingNoCol;
        Cursor cursor = dbManager.getDataLogin();
        firstNameCol = cursor.getColumnIndexOrThrow("First_Name");
        lastNameCol = cursor.getColumnIndexOrThrow("Last_Name");
        flatNoCol = cursor.getColumnIndexOrThrow("Apartment");
        wingNoCol = cursor.getColumnIndexOrThrow("Wing");
        cursor.moveToFirst();

        residentName.setText(cursor.getString(firstNameCol) + " " + cursor.getString(lastNameCol));
        residentFlatNo.setText(cursor.getString(wingNoCol) + "/" +cursor.getString(flatNoCol));

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.resident_home_screen_fragment_area, new NoticeBoardFragment());
        transaction.commit();
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

        if (id == R.id.nav_intercom_resident) {

        } else if (id == R.id.nav_vendors_resident) {

        } else if (id == R.id.nav_admin_info_resident) {

        } else if (id == R.id.nav_complaints_resident) {

        } else if (id == R.id.nav_security_list_resident) {

        } else if (id == R.id.nav_logout_resident) {
            LogOutDialog logOutDialog = new LogOutDialog();
            logOutDialog.show(getSupportFragmentManager(), "Logout");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

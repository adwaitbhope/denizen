package com.township.manager;

import android.database.Cursor;
import android.os.Bundle;

import com.google.android.material.navigation.NavigationView;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

public class SecurityHomeScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DBManager dbManager=new DBManager(getApplicationContext());
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        int placeCol,firstNameCol,lastNameCol;
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header=navigationView.getHeaderView(0);
        TextView securityName,securityPlace;
        securityName=header.findViewById(R.id.navheader_security_home_screen_name_textview);
        securityPlace=header.findViewById(R.id.navheader_security_home_screen_deskplace_textview);
        Cursor cursor= dbManager.getDataLogin();
        firstNameCol=cursor.getColumnIndexOrThrow("First_Name");
        lastNameCol=cursor.getColumnIndexOrThrow("Last_Name");
        placeCol=cursor.getColumnIndexOrThrow("Designation");
        cursor.moveToFirst();
        securityPlace.setText(cursor.getString(placeCol));
        securityName.setText(cursor.getString(firstNameCol)+" "+cursor.getString(lastNameCol));
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

        if (id == R.id.nav_intercom_security) {
            // Handle the camera action
        } else if (id == R.id.nav_admin_info_security) {

        } else if (id == R.id.nav_vendors_security) {

        } else if (id == R.id.nav_visitor_history_security) {

        } else if (id == R.id.nav_logout_security) {
            LogOutDialog logOutDialog=new LogOutDialog();
            logOutDialog.show(getSupportFragmentManager(),"Logout");

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}

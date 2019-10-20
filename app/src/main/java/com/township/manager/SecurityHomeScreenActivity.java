package com.township.manager;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class SecurityHomeScreenActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, VisitorEntryFragment.OnFragmentInteractionListener {

    AppDatabase appDatabase;
    VisitorDao visitorDao;
    String username, password;

    Visitor[] visitorsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security_home_screen);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DBManager dbManager = new DBManager(getApplicationContext());
        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        int placeCol, firstNameCol, lastNameCol;

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        View header = navigationView.getHeaderView(0);

        TextView securityName, securityPlace;

        securityName = header.findViewById(R.id.navheader_security_home_screen_name_textview);
        securityPlace = header.findViewById(R.id.navheader_security_home_screen_deskplace_textview);

        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        firstNameCol = cursor.getColumnIndexOrThrow("First_Name");
        lastNameCol = cursor.getColumnIndexOrThrow("Last_Name");
        placeCol = cursor.getColumnIndexOrThrow("Designation");
        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));

        getVisitorHistoryFromServer();

        securityName.setText(cursor.getString(firstNameCol) + " " + cursor.getString(lastNameCol));
        securityPlace.setText(cursor.getString(placeCol));

        VisitorEntryFragment visitorEntryFragment = new VisitorEntryFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.security_home_screen_fragment_area, visitorEntryFragment);
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

        if (id == R.id.nav_intercom_security) {
            // Handle the camera action
        } else if (id == R.id.nav_admin_info_security) {

        } else if (id == R.id.nav_vendors_security) {

        } else if (id == R.id.nav_visitor_history_security) {
            Intent intent = new Intent(SecurityHomeScreenActivity.this, VisitorHistorySecurityContainerActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_logout_security) {
            LogOutDialog logOutDialog = new LogOutDialog();
            logOutDialog.show(getSupportFragmentManager(), "Logout");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void getVisitorHistoryFromServer() {
        RetrofitServerAPI retrofitServerAPI = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.getVisitorHistory(
                username,
                password,
                null
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String responseString = response.body().toString();
                try {
                    JSONArray responseArray = new JSONArray(responseString);
                    JSONObject loginData = responseArray.getJSONObject(0);

                    if (loginData.getInt("login_status") == 1) {
                        if (loginData.getInt("request_status") == 1) {

                            JSONArray visitorsResponseArray = responseArray.getJSONArray(1);
                            Gson gson = new Gson();
                            visitorsArray = new Visitor[visitorsResponseArray.length()];
                            for (int i = 0; i < visitorsResponseArray.length(); i++) {
                                Visitor visitor = gson.fromJson(visitorsResponseArray.getJSONObject(i).toString(), Visitor.class);
                                visitorsArray[i] = visitor;
                            }
                            new AddVisitorsToDatabase().execute();
                        }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
    }

    private class AddVisitorsToDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            visitorDao = appDatabase.visitorDao();
            visitorDao.deleteAll();
            visitorDao.insert(visitorsArray);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

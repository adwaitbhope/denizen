package com.township.manager;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

public class SecurityActivity extends AppCompatActivity implements SecurityDesksListFragment.OnFragmentInteractionListener, SecurityPersonnelListFragment.OnFragmentInteractionListener {


    String username, password;
    DBManager dbManager;
    Cursor cursor;
    SecurityDesksDao securityDesksDao;
    SecurityPersonnelDao securityPersonnelDao;
    AppDatabase appDatabase;
    SecurityPersonnel[] securityPersonnels;
    SecurityDesks[] securityDesks;
    SecurityPersonnelListFragment securityPersonnelListFragment;
    SecurityDesksListFragment securityDesksListFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_security);
        SliderAdapter sliderAdapter = new SliderAdapter(Objects.requireNonNull(this).getSupportFragmentManager());

        Toolbar toolbar = (Toolbar) findViewById(R.id.security_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Security Info");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        dbManager = new DBManager(getApplicationContext());
        cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));

        securityPersonnelListFragment = new SecurityPersonnelListFragment();
        securityDesksListFragment = new SecurityDesksListFragment();

        sliderAdapter.addFragment(securityPersonnelListFragment, "Personnel");
        sliderAdapter.addFragment(securityDesksListFragment, "Desks");

        getSecurityDesksFromServer();
        getSecurityPersonnelFromServer();

        ViewPager mSlideViewPager = (ViewPager) findViewById(R.id.security_view_pager);
        mSlideViewPager.setAdapter(sliderAdapter);
        mSlideViewPager.setOffscreenPageLimit(1);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.security_tab_layout);
        tabLayout.setupWithViewPager(mSlideViewPager);
        tabLayout.getTabAt(0).setIcon(R.drawable.ic_people_black_24dp);
        tabLayout.getTabAt(1).setIcon(R.drawable.ic_store_black_24dp);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (securityPersonnelListFragment.getContext() != null) {
//            securityPersonnelListFragment.updateRecyclerView();
            getSecurityPersonnelFromServer();
        }
        if (securityDesksListFragment.getContext() != null) {
//            securityDesksListFragment.updateRecyclerView();
            getSecurityDesksFromServer();
        }
    }

    private void getSecurityDesksFromServer() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.getSecurityDesks(
                username,
                password
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                assert response.body() != null;
                Log.d("deskresponse", response.body().toString());
                String responseString = response.body().getAsJsonArray().toString();
                try {
                    JSONArray jsonArray = new JSONArray(responseString);
                    JSONObject loginResponse = jsonArray.getJSONObject(0);
                    if (loginResponse.getInt("login_status") == 1) {
                        JSONArray jsonSecurityDesks = jsonArray.getJSONArray(1);
                        JSONObject jsonSecurityDeskObject;

                        ArrayList<SecurityDesks> securityDesksArrayList = new ArrayList<>();
                        Gson gson = new Gson();
                        SecurityDesks securityDesks;

                        for (int i = 0; i < jsonSecurityDesks.length(); i++) {
                            jsonSecurityDeskObject = jsonSecurityDesks.getJSONObject(i);
                            securityDesks = gson.fromJson(jsonSecurityDeskObject.toString(), SecurityDesks.class);
                            securityDesksArrayList.add(securityDesks);
                        }
                        addSecurityDesksToDatabase(securityDesksArrayList);
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

    private void addSecurityDesksToDatabase(final ArrayList<SecurityDesks> securityDesksArrayList) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                securityDesksDao = appDatabase.securityDesksDao();
                securityDesks = new SecurityDesks[securityDesksArrayList.size()];
                securityDesksArrayList.toArray(securityDesks);
                new SecurityDesksAsyncTask().execute();
            }
        }.start();
    }

    private void getSecurityPersonnelFromServer() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.getSecurityPersonnel(
                username,
                password
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                assert response.body() != null;
                Log.d("personnelresponse", response.body().toString());
                String responseString = response.body().getAsJsonArray().toString();
                try {
                    JSONArray jsonArray = new JSONArray(responseString);
                    JSONObject loginResponse = jsonArray.getJSONObject(0);

                    if (loginResponse.getInt("login_status") == 1) {
                        JSONArray jsonSecurityPersonnel = jsonArray.getJSONArray(1);
                        JSONObject jsonSecurityPersonnelObject;
                        SecurityPersonnel securityPersonnel;
                        ArrayList<SecurityPersonnel> securityPersonnelArrayList = new ArrayList<>();
                        Gson gson = new Gson();

                        for (int i = 0; i < jsonSecurityPersonnel.length(); i++) {
                            jsonSecurityPersonnelObject = jsonSecurityPersonnel.getJSONObject(i);
                            securityPersonnel = gson.fromJson(jsonSecurityPersonnelObject.toString(), SecurityPersonnel.class);
                            securityPersonnelArrayList.add(securityPersonnel);
                        }
                        addSecurityPersonnelToDatabase(securityPersonnelArrayList);
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

    private void addSecurityPersonnelToDatabase(final ArrayList<SecurityPersonnel> securityPersonnelArrayList) {
        new Thread() {
            @Override
            public void run() {
                super.run();
                securityPersonnelDao = appDatabase.securityPersonnelDao();
                securityPersonnels = new SecurityPersonnel[securityPersonnelArrayList.size()];
                securityPersonnelArrayList.toArray(securityPersonnels);
                new SecurityPersonnelAsyncTask().execute();

            }
        }.start();
    }

    private class SecurityPersonnelAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.securityPersonnelDao().deleteAll();
            securityPersonnelDao.insert(securityPersonnels);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            securityPersonnelListFragment.updateRecyclerView();
            super.onPostExecute(aVoid);
        }
    }

    private class SecurityDesksAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.securityDesksDao().deleteAll();
            securityDesksDao.insert(securityDesks);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            securityDesksListFragment.updateRecyclerView();
            super.onPostExecute(aVoid);
        }
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

    @Override
    public void updateUI() {
        onResume();
    }
}

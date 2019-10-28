package com.township.manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CalendarView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AmenityDetailsActivity extends AppCompatActivity {

    String username, password, amenityId;

    RecyclerView recyclerView;
    AmenitySlotsAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<AmenitySlot> dataset= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amenity_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.amenity_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Available slots");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        new Thread() {
            public void run() {
                DBManager dbManager = new DBManager(getApplicationContext());
                Cursor cursor = dbManager.getDataLogin();
                cursor.moveToFirst();
                username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
                password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
            }
        }.start();

        Intent intent = getIntent();
        amenityId = intent.getStringExtra("amenityId");

        CalendarView calendarView = findViewById(R.id.calendarView3);
        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView view, int year, int month, int dayOfMonth) {
                getSlotsFromServer(dayOfMonth, month + 1, year);
            }
        });

        recyclerView = findViewById(R.id.amenity_slots_recycler_view);
        adapter = new AmenitySlotsAdapter(dataset, this);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemViewCacheSize(15);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

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

    public void getSlotsFromServer(int day, int month, int year) {
        dataset.clear();
        adapter.notifyDataSetChanged();

        ((ProgressBar) findViewById(R.id.amenity_slots_progress_bar)).setVisibility(View.VISIBLE);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.getAmenitySlots(
                username,
                password,
                amenityId,
                day,
                month,
                year
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

                            ((ProgressBar) findViewById(R.id.amenity_slots_progress_bar)).setVisibility(View.GONE);

                            JSONArray amenitySlotsResponseArray = responseArray.getJSONArray(1);
                            Gson gson = new Gson();
                            for (int i = 0; i < amenitySlotsResponseArray.length(); i++) {
                                AmenitySlot slot = gson.fromJson(amenitySlotsResponseArray.getJSONObject(i).toString(), AmenitySlot.class);
                                dataset.add(slot);
                            }
                            adapter.notifyDataSetChanged();
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
}

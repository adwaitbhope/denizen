package com.township.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AmenitiesBookingHistoryActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    AmenitiesBookingHistoryAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    ArrayList<AmenityBooking> dataset = new ArrayList<>();
    ArrayList<AmenityBooking> temporaryDataset = new ArrayList<>();

    AppDatabase appDatabase;
    AmenityBookingDao bookingDao;

    String username, password;
    AmenityBooking[] amenityBookingsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amenities_booking_history);

        Toolbar toolbar = (Toolbar) findViewById(R.id.resident_booking_history_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Booking History");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));

        ((ProgressBar) findViewById(R.id.amenities_booking_history_progress_bar)).setVisibility(View.VISIBLE);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();
        bookingDao = appDatabase.amenityBookingDao();

        recyclerView = findViewById(R.id.amenities_booking_history_recycler_view);
        adapter = new AmenitiesBookingHistoryAdapter(dataset, this);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemViewCacheSize(15);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

        new GetBookingsAsyncTask().execute();

        getBookingHistoryFromServer();

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

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    public void getBookingHistoryFromServer() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.getAmenityBookingHistory(
                username,
                password,
                false
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

                            final JSONArray bookingResponseArray = responseArray.getJSONArray(1);
                            final Gson gson = new Gson();
                            amenityBookingsArray = new AmenityBooking[bookingResponseArray.length()];
                            new Thread() {
                                public void run() {
                                    for (int i = 0; i < bookingResponseArray.length(); i++) {
                                        try {
                                            AmenityBooking booking = gson.fromJson(bookingResponseArray.getJSONObject(i).toString(), AmenityBooking.class);

                                            amenityBookingsArray[i] = booking;
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                    appDatabase.amenityBookingDao().insert(amenityBookingsArray);
                                    new GetBookingsAsyncTask().execute();
                                }
                            }.start();

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

    private class GetBookingsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            temporaryDataset.clear();
            temporaryDataset.addAll(bookingDao.getAll());
            WingDao wingDao = appDatabase.wingDao();
            AmenityDao amenityDao = appDatabase.amenityDao();
            for (AmenityBooking booking : temporaryDataset) {
                booking.setWing(wingDao.getWingName(booking.getWing_id()));
                booking.setAmenity_name(amenityDao.getAmenityName(booking.getAmenity_id()));
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataset.clear();
            dataset.addAll(temporaryDataset);

            if (adapter != null) {
                ((ProgressBar) findViewById(R.id.amenities_booking_history_progress_bar)).setVisibility(View.GONE);
                adapter.notifyDataSetChanged();
            }
        }
    }
}

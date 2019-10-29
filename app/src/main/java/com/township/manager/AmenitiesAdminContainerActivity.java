package com.township.manager;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.township.manager.R.color.white;

public class AmenitiesAdminContainerActivity extends AppCompatActivity implements AmenitiesFragment.OnFragmentInteractionListener {

    String username, password;
    Amenity[] amenitiesArray;
    AppDatabase appDatabase;

    AmenitiesFragment amenitiesFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amenities_admin_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.amenities_admin_container_toolbar);
//        toolbar.setTitleTextColor(getColor(R.color.secondaryColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Amenities");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        amenitiesFragment = new AmenitiesFragment();

        getAmenitiesFromServer();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.amenities_admin_container_frame, amenitiesFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();

    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.amenities_action_bar_menu, menu);

        Drawable historydrawable = menu.findItem(R.id.action_booking_history_item).getIcon();
        historydrawable = DrawableCompat.wrap(historydrawable);
        DrawableCompat.setTint(historydrawable, ContextCompat.getColor(this, white));
        menu.findItem(R.id.action_booking_history_item).setIcon(historydrawable);


        Drawable listdrawable = menu.findItem(R.id.action_booking_list_item).getIcon();
        listdrawable = DrawableCompat.wrap(listdrawable);
        DrawableCompat.setTint(listdrawable, ContextCompat.getColor(this, white));
        menu.findItem(R.id.action_booking_list_item).setIcon(listdrawable);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_booking_history_item) {

            startActivity (new Intent(this, AmenitiesBookingHistoryActivity.class));
            return true;
        }
        if (id == R.id.action_booking_list_item) {

            startActivity (new Intent(this, AmenitiesOneTimePaymentsActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void getAmenitiesFromServer() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.getAmenities(
                username,
                password
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

                            JSONArray amenitiesResponseArray = responseArray.getJSONArray(1);
                            Gson gson = new Gson();
                            amenitiesArray = new Amenity[amenitiesResponseArray.length()];
                            for (int i = 0; i < amenitiesResponseArray.length(); i++) {
                                Amenity amenity = gson.fromJson(amenitiesResponseArray.getJSONObject(i).toString(), Amenity.class);
                                amenitiesArray[i] = amenity;
                            }
                            new AmenitiesAsyncTask().execute();
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

    private class AmenitiesAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.amenityDao().deleteAll();
            appDatabase.amenityDao().insert(amenitiesArray);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (amenitiesFragment.getContext() != null) {
                amenitiesFragment.updateRecyclerView();
            }
            super.onPostExecute(aVoid);
        }
    }

}



package com.township.manager;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.ThemedSpinnerAdapter;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ServiceVendorActivity extends AppCompatActivity implements PopupMenu.OnMenuItemClickListener, ServiceVendorFragment.OnFragmentInteractionListener {

//    TextView popUpMenu;


    ServiceVendorFragment serviceVendorFragment;
    String username, password;
    AppDatabase appDatabase;

    ServiceVendorDao serviceVendorDao;
    ServiceVendors[] serviceVendorsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_vendor);
//
//
//
//
        Toolbar toolbar = (Toolbar) findViewById(R.id.service_vendor_container_toolbar);
//        toolbar.setTitleTextColor(getColor(R.color.secondaryColor));
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Service Vendors");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        serviceVendorFragment = new ServiceVendorFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.service_vendors_container_frame, serviceVendorFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();


        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        int usernameCol, passwordCol;

        usernameCol = cursor.getColumnIndexOrThrow("Username");
        passwordCol = cursor.getColumnIndexOrThrow("Password");
        cursor.moveToFirst();

        username = cursor.getString(usernameCol);
        password = cursor.getString(passwordCol);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        getServiceVendorFromServer();

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

    private void getServiceVendorFromServer() {


        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.getServiceVendors(
                username,
                password
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                assert response.body() != null;
                Log.d("responseservicevendor", response.body().toString());
                String responseString = response.body().getAsJsonArray().toString();
                try {
                    JSONArray jsonArray = new JSONArray(responseString);
                    JSONObject loginResponse = jsonArray.getJSONObject(0);

                    if (loginResponse.getInt("login_status") == 1) {
                        JSONArray jsonServiceVendorsArray = jsonArray.getJSONArray(1);
                        JSONObject jsonServiceVendor;
                        ArrayList<ServiceVendors> serviceVendorsArrayList = new ArrayList<>();
                        ServiceVendors serviceVendors;
                        Gson gson = new Gson();

                        for (int i = 0; i < jsonServiceVendorsArray.length(); i++) {
                            jsonServiceVendor = jsonServiceVendorsArray.getJSONObject(i);
                            serviceVendors = gson.fromJson(jsonServiceVendor.toString(), ServiceVendors.class);
                            serviceVendorsArrayList.add(serviceVendors);
                            Log.d("i", String.valueOf(i));
                        }

                        addServiceVendorsToDatabase(serviceVendorsArrayList);


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

    private void addServiceVendorsToDatabase(final ArrayList<ServiceVendors> serviceVendorsArrayList) {

        new Thread() {
            @Override
            public void run() {
                serviceVendorDao = appDatabase.serviceVendorDao();
                serviceVendorsArray = new ServiceVendors[serviceVendorsArrayList.size()];

                serviceVendorsArrayList.toArray(serviceVendorsArray);
                new ServiceVendorsAsyncTask().execute();
            }
        }.start();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private class ServiceVendorsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            serviceVendorDao.deleteAll();
            serviceVendorDao.insert(serviceVendorsArray);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            serviceVendorFragment.updateRecyclerView();
            super.onPostExecute(aVoid);
        }
    }


}

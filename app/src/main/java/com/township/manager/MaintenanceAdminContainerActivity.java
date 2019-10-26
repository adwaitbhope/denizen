package com.township.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
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
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MaintenanceAdminContainerActivity extends AppCompatActivity implements MaintenanceFragment.OnFragmentInteractionListener {


    MaintenanceFragment maintenanceFragment;
    MaintenanceDao maintenanceDao;
    Maintenance[] maintenancesArray;
    AppDatabase appDatabase;
    String username, password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance_admin_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.maintenance_admin_container_toolbar);
        setSupportActionBar(toolbar);

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

        getMaintenanceFromServer();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        maintenanceFragment = new MaintenanceFragment();
        transaction.replace(R.id.maintenance_admin_container_frame, maintenanceFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
    }


    private void getMaintenanceFromServer() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.getMaintenance(
                username,
                password,
                null
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.d("maintenanceresponse", response.body().toString());
                String responseString = response.body().getAsJsonArray().toString();
                try {
                    JSONArray jsonArray = new JSONArray(responseString);
                    JSONObject loginResponse = jsonArray.getJSONObject(0);

                    if (loginResponse.getInt("login_status") == 1) {
                        JSONArray jsonMaintenancArray = jsonArray.getJSONArray(1);

                        JSONObject jsonMaintenance;

                        ArrayList<Maintenance> maintenances = new ArrayList<>();
                        Maintenance maintenance;
                        Gson gson = new Gson();

                        for (int i = 0; i < jsonMaintenancArray.length(); i++) {
                            jsonMaintenance = jsonMaintenancArray.getJSONObject(i);
                            maintenance = gson.fromJson(jsonMaintenance.toString(), Maintenance.class);

                            maintenances.add(maintenance);
                        }

                        addMaintenanceToDatabase(maintenances);
                    }

                } catch (JSONException e) {
                    Log.d("maintenanceerror", e.getMessage());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });

    }

    private void addMaintenanceToDatabase(final ArrayList<Maintenance> maintenances) {
        new Thread() {
            @Override
            public void run() {
                maintenanceDao = appDatabase.maintenanceDao();
                maintenancesArray = new Maintenance[maintenances.size()];
                maintenances.toArray(maintenancesArray);
                MaintenanceAsyncTask maintenanceAsyncTask = new MaintenanceAsyncTask();
                maintenanceAsyncTask.execute();
            }
        }.start();
    }

    private class MaintenanceAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.maintenanceDao().deleteAll();
            maintenanceDao.insert(maintenancesArray);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            maintenanceFragment.updateRecyclerView();
            super.onPostExecute(aVoid);
        }
    }


    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}

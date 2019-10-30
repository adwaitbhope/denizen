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
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.nbsp.materialfilepicker.ui.DirectoryAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import javax.sql.StatementEvent;

public class IntercomActivity extends AppCompatActivity {

    RecyclerView recyclerViewAdmins, recyclerViewApartments, recyclerViewSecurity;
    IntercomAdapter adapterAdmins, adapterSecurity, adapterApartments;
    RecyclerView.LayoutManager layoutManagerAdmins, layoutManagerSecurity, layoutManagerApartments;

    AppDatabase appDatabase;
    IntercomDao intercomDao;
    WingDao wingDao;

    String username, password;

    ArrayList<Intercom> intercoms = new ArrayList<>();

    ArrayList<Intercom> datasetAdmin = new ArrayList<>();
    ArrayList<Intercom> datasetApartment = new ArrayList<>();
    ArrayList<Intercom> datasetSecurity = new ArrayList<>();

    ArrayList<Intercom> temporaryDatasetAdmin = new ArrayList<>();
    ArrayList<Intercom> temporaryDatasetApartment = new ArrayList<>();
    ArrayList<Intercom> temporaryDatasetSecurity = new ArrayList<>();

    Intercom[] intercomsArray;

    AutoCompleteTextView wingFilledExposedDropdown;
    ArrayList<String> WINGS = new ArrayList<>();

    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intercom);

        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        int usernameCol, passwordCol;

        usernameCol = cursor.getColumnIndexOrThrow("Username");
        passwordCol = cursor.getColumnIndexOrThrow("Password");

        username = cursor.getString(usernameCol);
        password = cursor.getString(passwordCol);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();
        intercomDao = appDatabase.intercomDao();
        wingDao = appDatabase.wingDao();

        intializeAdminRecycler();
        intializeSecurityRecycler();
        intializeApartmentsRecyler();

        updateRecyclerView();

        getIntercomFromServer();

        new Thread() {
            @Override
            public void run() {
                wingDao = appDatabase.wingDao();
                WINGS.addAll(wingDao.getALLWingName());
            }
        }.start();

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        this,
                        R.layout.dropdown_menu_popup_item,
                        WINGS);

        wingFilledExposedDropdown = findViewById(R.id.intercom_wing_selector_exposed_dropdown);
        wingFilledExposedDropdown.setAdapter(adapter);
        wingFilledExposedDropdown.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int i, long l) {
                flag = 1;
                updateRecyclerView();
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.intercom_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Intercom");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecyclerView();
    }

    private void getIntercomFromServer() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.getIntercomDetails(
                username,
                password
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                Log.d("intercom response", response.body().toString());
                String responseString = response.body().getAsJsonArray().toString();
                try {
                    Log.d("intercom", responseString);
                    JSONArray jsonArray = new JSONArray(responseString);
                    JSONObject loginResponse = jsonArray.getJSONObject(0);

                    if (loginResponse.getInt("login_status") == 1) {
                        JSONArray jsonAdmin = jsonArray.getJSONArray(1);
                        JSONArray jsonSecurity = jsonArray.getJSONArray(2);
                        JSONArray jsonApartment = jsonArray.getJSONArray(3);

                        Intercom intercom;
                        Gson gson = new Gson();
                        JSONObject intercomObject;

                        for (int i = 0; i < jsonAdmin.length(); i++) {
                            intercomObject = jsonAdmin.getJSONObject(i);
                            intercom = gson.fromJson(intercomObject.toString(), Intercom.class);
                            intercoms.add(intercom);
                        }
                        for (int i = 0; i < jsonSecurity.length(); i++) {
                            intercomObject = jsonSecurity.getJSONObject(i);
                            intercom = gson.fromJson(intercomObject.toString(), Intercom.class);
                            intercoms.add(intercom);
                        }
                        for (int i = 0; i < jsonApartment.length(); i++) {
                            intercomObject = jsonApartment.getJSONObject(i);
                            intercom = gson.fromJson(intercomObject.toString(), Intercom.class);
                            intercoms.add(intercom);
                        }

                        addIntercomToDatabase(intercoms);
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

    private void addIntercomToDatabase(final ArrayList<Intercom> intercoms) {

        new Thread() {
            @Override
            public void run() {
                intercomDao = appDatabase.intercomDao();
                intercomsArray = new Intercom[intercoms.size()];

                intercoms.toArray(intercomsArray);
                IntercomAsyncTask intercomAsyncTask = new IntercomAsyncTask();
                intercomAsyncTask.execute();
            }
        }.start();
    }

    private class IntercomAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.intercomDao().deleteAll();
            appDatabase.intercomDao().insert(intercomsArray);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateRecyclerView();
            super.onPostExecute(aVoid);
        }
    }

    private void updateRecyclerView() {
        new IntercomAsyncTaskFromDatabase().execute();
    }

    private class IntercomAsyncTaskFromDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            WingDao wingDao = appDatabase.wingDao();

            temporaryDatasetAdmin.clear();
            temporaryDatasetAdmin.addAll(intercomDao.getAll("admin"));
            temporaryDatasetApartment.clear();

            String wingId;
            if (flag == 1) {
                wingId = wingDao.getWingId(wingFilledExposedDropdown.getText().toString());
                temporaryDatasetApartment.addAll(intercomDao.getAll("resident", wingId));
                for (Intercom intercom : temporaryDatasetApartment) {
                    intercom.setWing(wingDao.getWingName(intercom.getWing_id()));
                }
            }

            temporaryDatasetSecurity.clear();
            temporaryDatasetSecurity.addAll(intercomDao.getAll("security"));


            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            datasetAdmin.clear();
            datasetAdmin.addAll(temporaryDatasetAdmin);

            datasetApartment.clear();
            datasetApartment.addAll(temporaryDatasetApartment);

            datasetSecurity.clear();
            datasetSecurity.addAll(temporaryDatasetSecurity);

            if (adapterApartments != null) {
                adapterApartments.notifyDataSetChanged();
            }

            if (adapterAdmins != null) {
                adapterAdmins.notifyDataSetChanged();
            }

            if (adapterSecurity != null) {
                adapterSecurity.notifyDataSetChanged();
            }
        }
    }

    private void intializeApartmentsRecyler() {

        recyclerViewApartments = findViewById(R.id.recycler_intecom_apartments);
        adapterApartments = new IntercomAdapter(datasetApartment, this);

        layoutManagerApartments = new LinearLayoutManager(this);

        recyclerViewApartments.setLayoutManager(layoutManagerApartments);
        recyclerViewApartments.setAdapter(adapterApartments);

        recyclerViewApartments.setItemViewCacheSize(15);
        recyclerViewApartments.setDrawingCacheEnabled(true);
        recyclerViewApartments.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    private void intializeSecurityRecycler() {

        recyclerViewSecurity = findViewById(R.id.recycler_intecom_security);
        adapterSecurity = new IntercomAdapter(datasetSecurity, this);

        layoutManagerSecurity = new LinearLayoutManager(this);

        recyclerViewSecurity.setLayoutManager(layoutManagerSecurity);
        recyclerViewSecurity.setAdapter(adapterSecurity);

        recyclerViewSecurity.setItemViewCacheSize(15);
        recyclerViewSecurity.setDrawingCacheEnabled(true);
        recyclerViewSecurity.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }

    private void intializeAdminRecycler() {

        recyclerViewAdmins = findViewById(R.id.recycler_intecom_admins);
        adapterAdmins = new IntercomAdapter(datasetAdmin, this);

        layoutManagerAdmins = new LinearLayoutManager(this);

        recyclerViewAdmins.setLayoutManager(layoutManagerAdmins);
        recyclerViewAdmins.setAdapter(adapterAdmins);

        recyclerViewAdmins.setItemViewCacheSize(15);
        recyclerViewAdmins.setDrawingCacheEnabled(true);
        recyclerViewAdmins.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

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
}

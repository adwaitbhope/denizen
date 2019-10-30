package com.township.manager;

import androidx.annotation.Nullable;
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

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class AdminInfoActivity extends AppCompatActivity {

    FloatingActionButton floatingActionButton;

    RecyclerView recyclerView;
    AdminInfoAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    AppDatabase appDatabase;
    ArrayList<AdminInfo> dataset = new ArrayList<>();
    ArrayList<AdminInfo> temporaryDataset = new ArrayList<>();
    AdminInfoDao adminInfoDao;
    public static final int ADD_ADMIN_REQUEST = 69;
    public static final int ADD_ADMIN_RESULT = 70;
    String townshipId;
    String username, password;
    AdminInfo[] adminInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_info);
        floatingActionButton = findViewById(R.id.add_admin_floatingActionButton);

        Toolbar toolbar = (Toolbar) findViewById(R.id.admin_info_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Admin Info");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(AdminInfoActivity.this, AddAdminActivity.class), ADD_ADMIN_REQUEST);

            }
        });

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        adminInfoDao = appDatabase.adminInfoDao();

        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();
        int usernameCol, passwordCol;

        usernameCol = cursor.getColumnIndexOrThrow("Username");
        passwordCol = cursor.getColumnIndexOrThrow("Password");

        username = cursor.getString(usernameCol);
        password = cursor.getString(passwordCol);

        townshipId = cursor.getString(cursor.getColumnIndexOrThrow("TownshipId"));

        getAdminInfoFromServer();

        intializeRecyclerView();

        updateRecyclerView();
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

    private void getAdminInfoFromServer() {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.getAdmins(
                username,
                password
        );
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                assert response.body() != null;

                String responseString = response.body().getAsJsonArray().toString();
                try {
                    JSONArray jsonArray = new JSONArray(responseString);
                    JSONObject loginResponse = jsonArray.getJSONObject(0);

                    if (loginResponse.getInt("login_status") == 1) {
                        JSONArray jsonArrayAdminInfo = jsonArray.getJSONArray(1);
                        JSONObject jsonObjectAdminInfo;

                        ArrayList<AdminInfo> adminInfosArrayList = new ArrayList<>();
                        AdminInfo adminInfo;
                        Gson gson = new Gson();

                        for (int i = 0; i < jsonArrayAdminInfo.length(); i++) {
                            jsonObjectAdminInfo = jsonArrayAdminInfo.getJSONObject(i);
                            adminInfo = gson.fromJson(jsonObjectAdminInfo.toString(), AdminInfo.class);
                            adminInfosArrayList.add(adminInfo);
                        }
                        addAdminInfoToDatabase(adminInfosArrayList);
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

    private void addAdminInfoToDatabase(final ArrayList<AdminInfo> adminInfosArrayList) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                adminInfos = new AdminInfo[adminInfosArrayList.size()];
                adminInfosArrayList.toArray(adminInfos);

                AdminInfoAsyncTaskExecute adminInfoAsyncTaskExecute = new AdminInfoAsyncTaskExecute();
                adminInfoAsyncTaskExecute.execute();
            }
        }.start();
    }

    private class AdminInfoAsyncTaskExecute extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.adminInfoDao().deleteAll();
            ;
            adminInfoDao.insert(adminInfos);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            updateRecyclerView();
            super.onPostExecute(aVoid);
        }
    }


    private void intializeRecyclerView() {

        recyclerView = findViewById(R.id.admin_info_recycler_view);
        adapter = new AdminInfoAdapter(dataset, AdminInfoActivity.this);

        adapter.TOWNSHIP_ID = townshipId;
        layoutManager = new LinearLayoutManager(AdminInfoActivity.this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemViewCacheSize(15);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
    }


    private void updateRecyclerView() {
        new AdminInfoAsyncTask().execute();
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecyclerView();

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_ADMIN_REQUEST) {
            if (resultCode == ADD_ADMIN_RESULT) {
                updateRecyclerView();
                recyclerView.smoothScrollToPosition(0);
            }
        }
    }

    private class AdminInfoAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            temporaryDataset.clear();
            temporaryDataset.addAll(adminInfoDao.getAll());

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataset.clear();
            dataset.addAll(temporaryDataset);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
            }
        }
    }
}

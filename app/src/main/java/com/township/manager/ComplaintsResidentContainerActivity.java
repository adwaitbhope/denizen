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
import android.view.MenuItem;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class ComplaintsResidentContainerActivity extends AppCompatActivity implements ComplaintsFragment.OnFragmentInteractionListener, ComplaintsListFragment.OnFragmentInteractionListener {

    ComplaintDao complaintDao;
    Complaint[] complaintsArray;
    AppDatabase appDatabase;
    String username, password;
    ComplaintsFragment complaintsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complaints_resident_container);

        Toolbar toolbar = (Toolbar) findViewById(R.id.complaints_resident_container_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Complaints");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        int usernameCol, passwordCol;
        usernameCol = cursor.getColumnIndexOrThrow("Username");
        passwordCol = cursor.getColumnIndexOrThrow("Password");
        cursor.moveToFirst();

        username = cursor.getString(usernameCol);
        password = cursor.getString(passwordCol);

        getComplaintsFromServer();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        complaintsFragment = new ComplaintsFragment();
        transaction.replace(R.id.complaints_resident_container_frame, complaintsFragment);
        transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
        transaction.commit();
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

    public void getComplaintsFromServer() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.getComplaints(
                username,
                password,
                null,
                null
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

                        JSONArray jsonArrayComplaint;
                        JSONObject jsonObjectComplaint;
                        ArrayList<Complaint> complaints = new ArrayList<>();
                        Complaint complaint;
                        Gson gson = new Gson();
                        jsonArrayComplaint = jsonArray.getJSONArray(1);
                        Log.d("printres", responseString);
                        for (int i = 0; i < jsonArrayComplaint.length(); i++) {
                            jsonObjectComplaint = jsonArrayComplaint.getJSONObject(i);
                            complaint = gson.fromJson(jsonObjectComplaint.toString(), Complaint.class);

                            complaints.add(complaint);
                        }
                        addComplaintsToDatabase(complaints);
                    }

                } catch (JSONException jsonexcpetion) {
                    Toast.makeText(ComplaintsResidentContainerActivity.this, jsonexcpetion.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });

    }

    public void addComplaintsToDatabase(final ArrayList<Complaint> complaints) {
        new Thread() {
            public void run() {
                complaintDao = appDatabase.complaintDao();
                complaintsArray = new Complaint[complaints.size()];
                complaints.toArray(complaintsArray);

                ComplaintsAsyncTask complaintsAsyncTask = new ComplaintsAsyncTask();
                complaintsAsyncTask.execute();

            }
        }.start();

    }

    private class ComplaintsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.complaintDao().deleteAll();
            complaintDao.insert(complaintsArray);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            complaintsFragment.updateRecyclerView();
            super.onPostExecute(aVoid);
        }
    }

}

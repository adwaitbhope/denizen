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
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MembershipDetailsActivity extends AppCompatActivity {

    AppDatabase appDatabase;
    MembershipPaymentDao paymentDao;

    String username, password;

    ExtendedFloatingActionButton payMembershipButton;
    ArrayList<MembershipPayment> dataset = new ArrayList<>();
    ArrayList<MembershipPayment> temporaryDataset = new ArrayList<>();

    RecyclerView recyclerView;
    MembershipPaymentsAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_membership_details);

        Toolbar toolbar = (Toolbar) findViewById(R.id.membership_details_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Membership Details");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();
        paymentDao = appDatabase.membershipPaymentDao();

        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));

        recyclerView = findViewById(R.id.membership_payments_recycler_view);
        adapter = new MembershipPaymentsAdapter(dataset, this);

        payMembershipButton = findViewById(R.id.pay_membership_ex_fab);
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 10) {
                    payMembershipButton.hide();
                } else if (dy < 10) {
                    payMembershipButton.show();
                }
            }
        });

        getMembershipPaymentsFromServer();

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        recyclerView.setItemViewCacheSize(15);
        recyclerView.setDrawingCacheEnabled(true);
        recyclerView.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);

    }

    @Override
    public void onResume() {
        super.onResume();
        updateRecyclerView();
    }

    private void updateRecyclerView() {
        new GetPaymentsAsyncTask().execute();
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

    public void getMembershipPaymentsFromServer() {
        Call<JsonArray> call = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitServerAPI.class)
                .getMembershipPayments(
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

                            JSONArray paymentsResponseArray = responseArray.getJSONArray(1);
                            Gson gson = new Gson();
                            final MembershipPayment[] payments = new MembershipPayment[paymentsResponseArray.length()];
                            for (int i = 0; i < paymentsResponseArray.length(); i++) {
                                MembershipPayment payment = gson.fromJson(paymentsResponseArray.getJSONObject(i).toString(), MembershipPayment.class);
                                payments[i] = payment;
                            }
                            new Thread() {
                                public void run() {
                                    paymentDao.insert(payments);
                                    updateRecyclerView();
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

    private class GetPaymentsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            temporaryDataset.clear();
            temporaryDataset.addAll(paymentDao.getAll());
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            dataset.clear();
            dataset.addAll(temporaryDataset);
            if (adapter != null) {
//                ((ProgressBar) getView().findViewById(R.id.maintenance_list_progress_bar)).setVisibility(GONE);
                adapter.notifyDataSetChanged();
            }
        }
    }
}

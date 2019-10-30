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

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class MembershipDetailsActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    AppDatabase appDatabase;
    MembershipPaymentDao paymentDao;

    String username, password, type;

    ExtendedFloatingActionButton payMembershipButton;
    ArrayList<MembershipPayment> dataset = new ArrayList<>();
    ArrayList<MembershipPayment> temporaryDataset = new ArrayList<>();

    RecyclerView recyclerView;
    MembershipPaymentsAdapter adapter;
    RecyclerView.LayoutManager layoutManager;

    TextView membershipStatus;

    String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    String membershipAmount = "4000";

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
        type = cursor.getString(cursor.getColumnIndexOrThrow("Type"));

        recyclerView = findViewById(R.id.membership_payments_recycler_view);
        adapter = new MembershipPaymentsAdapter(dataset, this);

        membershipStatus = ((TextView) findViewById(R.id.membership_details_membership_status_text_view));

        payMembershipButton = findViewById(R.id.pay_membership_ex_fab);
        if (type.equals("resident")) {
            payMembershipButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    initiatePayment();
                }
            });

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

            checkMembershipStatus();
        } else {
            payMembershipButton.setVisibility(View.GONE);
            ((ProgressBar) findViewById(R.id.membership_details_progress_bar)).setVisibility(View.GONE);
            membershipStatus.setVisibility(View.GONE);
        }

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
        if (type.equals("resident")) {
            checkMembershipStatus();
        }
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

    public void initiatePayment() {
        final Paytm.PaytmOrder paytmOrder = new Paytm.PaytmOrder();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        paytmOrder.setTXN_AMOUNT(membershipAmount);
        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);
        Call<JsonArray> call = retrofitServerAPI.initiateAmenityMembershipPayment(
                username,
                password,
                membershipAmount,
                Paytm.CHANNEL_ID,
                Paytm.WEBSITE,
                Paytm.CALLBACK_URL,
                Paytm.INDUSTRY_TYPE_ID
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String jsonstring = response.body().getAsJsonArray().toString();
                try {
                    JSONArray jsonArray = new JSONArray(jsonstring);
                    JSONObject jsonObject = jsonArray.getJSONObject(1);

                    paytmOrder.setORDER_ID(jsonObject.getString("ORDER_ID"));
                    paytmOrder.setCUST_ID(jsonObject.getString("CUST_ID"));
                    paytmOrder.setCHECKSUMHASH(jsonObject.getString("CHECKSUMHASH"));

                    initializePayment(paytmOrder);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
    }

    private void initializePayment(Paytm.PaytmOrder paytmOrder) {

        PaytmPGService Service = PaytmPGService.getStagingService();

        //use this when using for production
        //PaytmPGService Service = PaytmPGService.getProductionService();

        //creating a hashmap and adding all the values required
        HashMap<String, String> paramMap = new HashMap<>();
        paramMap.put("MID", Paytm.MID);
        paramMap.put("CHANNEL_ID", Paytm.CHANNEL_ID);
        paramMap.put("WEBSITE", Paytm.WEBSITE);
        paramMap.put("INDUSTRY_TYPE_ID", Paytm.INDUSTRY_TYPE_ID);
        paramMap.put("ORDER_ID", paytmOrder.getORDER_ID());
        paramMap.put("CUST_ID", paytmOrder.getCUST_ID());
        paramMap.put("TXN_AMOUNT", paytmOrder.getTXN_AMOUNT());
        paramMap.put("CALLBACK_URL", Paytm.CALLBACK_URL + paytmOrder.getORDER_ID());
        paramMap.put("CHECKSUMHASH", paytmOrder.getCHECKSUMHASH());

        //creating a paytm order object using the hashmap
        com.paytm.pgsdk.PaytmOrder order = new com.paytm.pgsdk.PaytmOrder(paramMap);

        //intializing the paytm service
        Service.initialize(order, null);

        //finally starting the payment transaction
        Service.startPaymentTransaction(MembershipDetailsActivity.this, true, true, this);
    }

    private void verifyTransactionStatusFromServer(String orderId) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.verifyAmenityMembershipPayment(
                username,
                password,
                orderId
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String jsonstring = response.body().getAsJsonArray().toString();
                try {
                    JSONArray jsonArray = new JSONArray(jsonstring);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject.getString("STATUS").equals("TXN_SUCCESS")) {

                        String paymentData = jsonArray.getJSONObject(1).toString();
                        Gson gson = new Gson();
                        final MembershipPayment payment = gson.fromJson(paymentData, MembershipPayment.class);

                        new Thread() {
                            public void run() {
                                appDatabase.membershipPaymentDao().insert(payment);
                            }
                        }.start();

                        Intent intent = new Intent(MembershipDetailsActivity.this, RegistrationSuccessfulActivity.class);
                        intent.putExtra("title", "Payment status");
                        intent.putExtra("heading", "Membership availed");
                        intent.putExtra("description", "Transaction successful, your slot has been booked!\nYou can find the details in your booking history.");
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(MembershipDetailsActivity.this, RegistrationSuccessfulActivity.class);
                        intent.putExtra("title", "Payment status");
                        intent.putExtra("heading", "Membership not availed");
                        intent.putExtra("description", "Transaction was unsuccessful, please try again later.");
                        startActivity(intent);
                        finish();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("JsonException", e.toString());

                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
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

                            final JSONArray paymentsResponseArray = responseArray.getJSONArray(1);
                            final Gson gson = new Gson();
                            final MembershipPayment[] payments = new MembershipPayment[paymentsResponseArray.length()];

                            new Thread() {
                                public void run() {
                                    WingDao wingDao = appDatabase.wingDao();
                                    for (int i = 0; i < paymentsResponseArray.length(); i++) {
                                        MembershipPayment payment = null;
                                        try {
                                            payment = gson.fromJson(paymentsResponseArray.getJSONObject(i).toString(), MembershipPayment.class);
                                            payment.setWing(wingDao.getWingName(payment.getWing_id()));
                                            payments[i] = payment;
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
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

    @Override
    public void onTransactionResponse(Bundle inResponse) {
        JSONObject jsonObject = new JSONObject();
        Set<String> keys = inResponse.keySet();
        try {
            for (String key : keys) {
                jsonObject.put(key, JSONObject.wrap(inResponse.get(key)));
            }
            if (jsonObject.getString("STATUS").equals("TXN_SUCCESS")) {
                verifyTransactionStatusFromServer(jsonObject.getString("ORDERID"));
            } else {
                Toast.makeText(MembershipDetailsActivity.this, jsonObject.getString("STATUS"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.d("JsonException", e.toString());
        }
    }

    @Override
    public void networkNotAvailable() {

    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {

    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {

    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {

    }

    @Override
    public void onBackPressedCancelTransaction() {

    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {

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
            WingDao wingDao = appDatabase.wingDao();
            for (MembershipPayment payment : temporaryDataset) {
                payment.setWing(wingDao.getWingName(payment.getWing_id()));
            }
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

    private void checkMembershipStatus() {

        ((ProgressBar) findViewById(R.id.membership_details_progress_bar)).setVisibility(View.VISIBLE);
        membershipStatus.setVisibility(View.INVISIBLE);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);
        Call<JsonArray> call = retrofitServerAPI.checkMembershipStatus(
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
                            JSONObject membershipResponseObject = responseArray.getJSONObject(1);

                            Log.d("membership", membershipResponseObject.toString());

                            String first = "Your membership status is ";
                            String active = "<b><font color='#41C200'>active</font></b>";
                            String inactive = "<b><font color='#FF0000'>inactive</font></b>";
                            String activeNext = " and is valid until ";
                            String inactiveNext = ". You can avail membership for one year by paying ₹" + membershipAmount + ".";

                            if (membershipResponseObject.getBoolean("membership_status")) {
                                ((ProgressBar) findViewById(R.id.membership_details_progress_bar)).setVisibility(View.GONE);
                                membershipStatus.setVisibility(View.VISIBLE);
                                membershipStatus.setText(Html.fromHtml(first + active + activeNext + getFormattedDate(membershipResponseObject.getString("valid_thru_timestamp")) + "."));
                            } else {
                                payMembershipButton.setVisibility(View.VISIBLE);
                                ((ProgressBar) findViewById(R.id.membership_details_progress_bar)).setVisibility(View.GONE);
                                membershipStatus.setVisibility(View.VISIBLE);
                                membershipStatus.setText(Html.fromHtml(first + inactive + inactiveNext));
                            }

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

    public String getFormattedDate(String timestamp) {
        String day = timestamp.substring(8, 10);
        String digit = day.substring(1);

        if (digit.equals("1")) {
            day = day + "st";
        } else if (digit.equals("2")) {
            day = day + "nd";
        } else if (digit.equals("3")) {
            day = day + "rd";
        } else {
            day = day + "th";
        }

        String month = months[Integer.valueOf(timestamp.substring(5, 7)) - 1];

        String year = timestamp.substring(2, 4);

        return month + " " + day + ", '" + year;
    }


}

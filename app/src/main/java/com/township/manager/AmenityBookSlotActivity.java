package com.township.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Set;

public class AmenityBookSlotActivity extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    int hour, day, month, year;
    TextView details;
    MaterialButton confirmButton;

    String username, password, amenityId, amount;

    AppDatabase appDatabase;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_amenity_book_slot);

        Toolbar toolbar = (Toolbar) findViewById(R.id.amenity_book_slot_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Book slot");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));

        Intent intent = getIntent();
        amount = String.valueOf(intent.getIntExtra("amount", 0));

        String slotStart = intent.getStringExtra("slot_start");
        hour = Integer.valueOf(slotStart.substring(11, 13));
        day = Integer.valueOf(slotStart.substring(8, 10));
        month = Integer.valueOf(slotStart.substring(5, 7));
        year = Integer.valueOf(slotStart.substring(0, 4));

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        details = (TextView) findViewById(R.id.amenity_book_slot_amenity_free_details);
        confirmButton = (MaterialButton) findViewById(R.id.amenity_book_slot_confirm_button);

        Boolean freeForMembers = intent.getBooleanExtra("free_for_members", false);
        amenityId = intent.getStringExtra("amenity_id");

        if (freeForMembers) {
            checkMembershipStatus();
        } else {
            ((ProgressBar) findViewById(R.id.amenity_book_slot_progress_bar)).setVisibility(View.GONE);
            details.setVisibility(View.VISIBLE);
            details.setText("This amenity is needs to be booked by paying a one time fee of ₹" + amount + ".");

            confirmButton.setVisibility(View.VISIBLE);
            confirmButton.setText("Proceed to payment");
            confirmButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ((ProgressBar) findViewById(R.id.amenity_book_slot_progress_bar)).setVisibility(View.VISIBLE);
                    initiatePayment();
                }
            });
        }
    }

    private void checkMembershipStatus() {
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

                            if (membershipResponseObject.getBoolean("membership_status")) {
                                ((ProgressBar) findViewById(R.id.amenity_book_slot_progress_bar)).setVisibility(View.GONE);
                                details.setVisibility(View.VISIBLE);
                                details.setText("This amenity is free for members as well, and your membership status is active.\nYou can confirm your slot right away.");

                                confirmButton.setVisibility(View.VISIBLE);
                                confirmButton.setText("Confirm slot");
                                confirmButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        confirmBooking();
                                    }
                                });

                            } else {
                                ((ProgressBar) findViewById(R.id.amenity_book_slot_progress_bar)).setVisibility(View.GONE);
                                details.setVisibility(View.VISIBLE);
                                details.setText("This amenity is free for members, but your membership status is not active.\n\nYou can book your slots for free if you pay membership fees, or you can book it by paying a one time fee of ₹" + amount + ".");

                                confirmButton.setVisibility(View.VISIBLE);
                                confirmButton.setText("Proceed to payment");
                                confirmButton.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        ((ProgressBar) findViewById(R.id.amenity_book_slot_progress_bar)).setVisibility(View.VISIBLE);
                                        initiatePayment();
                                    }
                                });
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

    public void initiatePayment() {
        final Paytm.PaytmOrder paytmOrder = new Paytm.PaytmOrder();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        paytmOrder.setTXN_AMOUNT(amount);
        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);
        Call<JsonArray> call = retrofitServerAPI.intiateAmenityBookingPayment(
                username,
                password,
                amount,
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
        Service.startPaymentTransaction(AmenityBookSlotActivity.this, true, true, this);
    }

    public void confirmBooking() {
        ((ProgressBar) findViewById(R.id.amenity_book_slot_progress_bar)).setVisibility(View.VISIBLE);

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.bookAmenity(
                username,
                password,
                amenityId,
                day,
                month,
                year,
                hour
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

                            ((ProgressBar) findViewById(R.id.amenity_book_slot_progress_bar)).setVisibility(View.GONE);

                            JSONObject amenitySlotResponseObject = responseArray.getJSONObject(1);
                            Gson gson = new Gson();
                            final AmenityBooking booking = gson.fromJson(amenitySlotResponseObject.toString(), AmenityBooking.class);
                            new Thread() {
                                public void run() {
                                    appDatabase.amenityBookingDao().insert(booking);
                                }
                            }.start();
                            Intent intent = new Intent(AmenityBookSlotActivity.this, RegistrationSuccessfulActivity.class);
                            intent.putExtra("title", "Payment status");
                            intent.putExtra("heading", "Amenity slot booked");
                            intent.putExtra("description", "Transaction successful, your slot has been booked!\nYou can find the details in your booking history.");
                            startActivity(intent);
                            finish();
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
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
                Toast.makeText(AmenityBookSlotActivity.this, jsonObject.getString("STATUS"), Toast.LENGTH_SHORT).show();
            }

        } catch (JSONException e) {
            Log.d("JsonException", e.toString());
        }
    }


    private void verifyTransactionStatusFromServer(String orderId) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.verifyAmenityBookingPayment(
                username,
                password,
                orderId,
                amenityId,
                day,
                month,
                year,
                hour
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String jsonstring = response.body().getAsJsonArray().toString();
                try {
                    JSONArray jsonArray = new JSONArray(jsonstring);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject.getString("STATUS").equals("TXN_SUCCESS")) {

                        String bookingData = jsonArray.getJSONObject(1).toString();
                        Gson gson = new Gson();
                        final AmenityBooking booking = gson.fromJson(bookingData, AmenityBooking.class);

                        new Thread() {
                            public void run() {
                                appDatabase.amenityBookingDao().insert(booking);
                            }
                        }.start();

                        Intent intent = new Intent(AmenityBookSlotActivity.this, RegistrationSuccessfulActivity.class);
                        intent.putExtra("title", "Payment status");
                        intent.putExtra("heading", "Amenity slot booked");
                        intent.putExtra("description", "Transaction successful, your slot has been booked!\nYou can find the details in your booking history.");
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(AmenityBookSlotActivity.this, RegistrationSuccessfulActivity.class);
                        intent.putExtra("title", "Payment status");
                        intent.putExtra("heading", "Slot not booked");
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
}

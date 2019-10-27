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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.paytm.pgsdk.PaytmPGService;
import com.paytm.pgsdk.PaytmPaymentTransactionCallback;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Objects;
import java.util.Set;

public class PaymentForMaintenance extends AppCompatActivity implements PaytmPaymentTransactionCallback {

    MaterialButton payForMaintenance;
    TextInputLayout amountTIL;
    EditText amountEditText;
    String username, password;
    String amount;

    AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_for_maintenance);
        payForMaintenance = findViewById(R.id.pay_for_maintenance_button);
        amountEditText = findViewById(R.id.enter_maintenance_amount_textinput_child);
        amountTIL = findViewById(R.id.enter_maintenance_amount_textinput);

        handleError(amountTIL);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        Toolbar toolbar = (Toolbar) findViewById(R.id.maintenance_payment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Maintenance details");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        int usernameCol, passwordCol;
        usernameCol = cursor.getColumnIndexOrThrow("Username");
        passwordCol = cursor.getColumnIndexOrThrow("Password");
        cursor.moveToFirst();

        username = cursor.getString(usernameCol);
        password = cursor.getString(passwordCol);
        payForMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                payForMaintenance();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        ((ProgressBar) findViewById(R.id.maintenance_payment_progress_bar)).setVisibility(View.GONE);
        ((TextView) findViewById(R.id.maintenance_payment_status_textview)).setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
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

    private void payForMaintenance() {
        amount = amountEditText.getText().toString();
        if (amount.equals("")) {
            amountTIL.setErrorEnabled(true);
            amountTIL.setError("This field is required");
            amountTIL.setErrorIconDrawable(null);
            return;
        } else if (Integer.valueOf(amount) == 0) {
            amountTIL.setErrorEnabled(true);
            amountTIL.setError("Amount cannot be 0");
            amountTIL.setErrorIconDrawable(null);
            return;
        }

        try {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ProgressBar) findViewById(R.id.maintenance_payment_progress_bar)).setVisibility(View.VISIBLE);
        ((TextView) findViewById(R.id.maintenance_payment_status_textview)).setVisibility(View.VISIBLE);

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        final Paytm.PaytmOrder paytmOrder = new Paytm.PaytmOrder();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        paytmOrder.setTXN_AMOUNT(amount);
        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);
        Call<JsonArray> call = retrofitServerAPI.intiateMaintenancePayment(
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
                assert response.body() != null;
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
                    Log.d("paymaintenancne", e.toString());
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
        Service.startPaymentTransaction(PaymentForMaintenance.this, true, true, this);
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
                Toast.makeText(PaymentForMaintenance.this, jsonObject.getString("STATUS"), Toast.LENGTH_SHORT).show();
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

        Call<JsonArray> call = retrofitServerAPI.verifyMaintenancePayment(
                username,
                password,
                orderId
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                assert response.body() != null;
                String jsonstring = response.body().getAsJsonArray().toString();
                try {
                    JSONArray jsonArray = new JSONArray(jsonstring);
                    JSONObject jsonObject = jsonArray.getJSONObject(0);

                    if (jsonObject.getString("STATUS").equals("TXN_SUCCESS")) {

                        String maintenanceData = jsonArray.getJSONObject(1).toString();
                        Gson gson = new Gson();
                        final Maintenance maintenance = gson.fromJson(maintenanceData, Maintenance.class);

                        new Thread() {
                            public void run () {
                                MaintenanceDao maintenanceDao = appDatabase.maintenanceDao();
                                maintenanceDao.insert(maintenance);
                            }
                        }.start();

                        Intent intent = new Intent(PaymentForMaintenance.this, RegistrationSuccessfulActivity.class);
                        intent.putExtra("title", "Payment status");
                        intent.putExtra("heading", "Maintenance paid");
                        intent.putExtra("description", "Transaction successful, maintenance has been paid!");
                        startActivity(intent);
                        finish();
                    } else {
                        Intent intent = new Intent(PaymentForMaintenance.this, RegistrationSuccessfulActivity.class);
                        intent.putExtra("title", "Payment status");
                        intent.putExtra("heading", "Maintenance not paid");
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
            public void onFailure(Call<JsonArray> call, Throwable throwable) {
                Log.d("Verif. call failure", throwable.toString());
            }
        });
    }

    private void handleError(final TextInputLayout textInputLayout) {
        Objects.requireNonNull(textInputLayout.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    @Override
    public void networkNotAvailable() {
        Toast.makeText(PaymentForMaintenance.this, "Network error", Toast.LENGTH_LONG).show();
    }

    @Override
    public void clientAuthenticationFailed(String inErrorMessage) {
        Toast.makeText(PaymentForMaintenance.this, inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void someUIErrorOccurred(String inErrorMessage) {
        Toast.makeText(PaymentForMaintenance.this, inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onErrorLoadingWebPage(int iniErrorCode, String inErrorMessage, String inFailingUrl) {
        Toast.makeText(PaymentForMaintenance.this, inErrorMessage, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBackPressedCancelTransaction() {
        Toast.makeText(PaymentForMaintenance.this, "Transaction cancelled", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onTransactionCancel(String inErrorMessage, Bundle inResponse) {
        Toast.makeText(PaymentForMaintenance.this, inErrorMessage + inResponse.toString(), Toast.LENGTH_LONG).show();
    }
}

package com.township.manager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class RegistrationStepTwoStatusActivity extends AppCompatActivity {
    EditText applicationNumber, applicationEmail;
    Button checkStatus, proceedButton;
    TextView appplicationStatusHeading, nameKey, nameValue, phoneKey, phoneValue, addressKey, addressValue, statusKey, statusValue;
    TextInputLayout applicationNumberTil, applicationEmailTil;
    Boolean visibility = false;

    ProgressBar progressBar;
    String applicationId, email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration_step_two_status);

        Toolbar toolbar = (Toolbar) findViewById(R.id.registration_step_two_status_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Application Status");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        applicationNumber = findViewById(R.id.registration_step_two_status_application_number_edit_text);
        applicationEmail = findViewById(R.id.registration_step_two_status_email_edit_text);
        checkStatus = findViewById(R.id.registration_step_two_status_check_status_button);
        proceedButton = findViewById(R.id.registration_step_two_status_proceed_button);
        appplicationStatusHeading = findViewById(R.id.registration_status_application_status_heading_text_view);
        nameKey = findViewById(R.id.registration_status_name_key_tv);
        nameValue = findViewById(R.id.application_status_name_value_tv);
        phoneKey = findViewById(R.id.appliaction_status_phone_key_tv);
        phoneValue = findViewById(R.id.application_status_phone_value_tv);
        addressKey = findViewById(R.id.application_status_address_key_tv);
        addressValue = findViewById(R.id.application_status_address_value_tv);
        statusKey = findViewById(R.id.application_status_status_key_tv);
        statusValue = findViewById(R.id.application_status_status_value_tv);
        applicationEmailTil = findViewById(R.id.registration_step_two_status_email_til);
        applicationNumberTil = findViewById(R.id.registration_step_two_status_application_til);

        progressBar = findViewById(R.id.registration_check_status_progress_bar);

        proceedButton.setVisibility(View.GONE);
        if (!visibility) {
            invisible(appplicationStatusHeading);
            invisible(nameKey);
            invisible(nameValue);
            invisible(phoneKey);
            invisible(phoneValue);
            invisible(addressKey);
            invisible(addressValue);
            invisible(statusKey);
            invisible(statusValue);
        }

        checkStatus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkStatus();
            }
        });

        proceedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegistrationStepTwoStatusActivity.this, RegistrationSocietyStepTwoActivity.class);
                intent.putExtra("application_id", applicationId);
                startActivity(intent);
            }
        });

        error(applicationEmailTil);
        error(applicationNumberTil);

    }

    private void error(final TextInputLayout textInputLayout) {
        try {
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
        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    private void invisible(TextView textView) {
        textView.setVisibility(View.INVISIBLE);
    }

    private void visible(TextView textView) {
        textView.setVisibility(View.VISIBLE);
    }

    private void checkStatus() {

        progressBar.setVisibility(View.VISIBLE);

        invisible(appplicationStatusHeading);
        invisible(nameKey);
        invisible(nameValue);
        invisible(phoneKey);
        invisible(phoneValue);
        invisible(addressKey);
        invisible(addressValue);
        invisible(statusKey);
        invisible(statusValue);

        findViewById(R.id.registration_check_status_constraint_layout).requestFocus();

        try {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        applicationId = applicationNumber.getText().toString();
        email = applicationEmail.getText().toString();

        if (TextUtils.isEmpty(applicationId)) {
            applicationNumberTil.setError("Please enter your application number");
            applicationNumberTil.setErrorEnabled(true);
            applicationNumberTil.requestFocus();
            applicationNumberTil.setErrorIconDrawable(null);
            return;
        }

        if (TextUtils.isEmpty(email)) {
            applicationEmailTil.setError("Please enter your email");
            applicationEmailTil.setErrorEnabled(true);
            applicationEmailTil.requestFocus();
            applicationEmailTil.setErrorIconDrawable(null);
            return;
        }


        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.checkstatus(applicationId, email);

        call.enqueue(new Callback<JsonArray>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                progressBar.setVisibility(View.INVISIBLE);

                assert response.body() != null;
                String responseString = response.body().getAsJsonArray().toString();

                try {
                    JSONArray jsonArray = new JSONArray(responseString);

                    JSONObject authorizationData = jsonArray.getJSONObject(0);
                    if (authorizationData.getInt("request_status") == 0) {
                        Toast.makeText(RegistrationStepTwoStatusActivity.this, authorizationData.getString("status_description"), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    JSONObject jsonObject = jsonArray.getJSONObject(1);
                    nameValue.setText(jsonObject.getString("name"));
                    phoneValue.setText(jsonObject.getString("phone"));
                    addressValue.setText(jsonObject.getString("address"));
                    visibility = true;

                    if (jsonObject.getBoolean("verified")) {
                        statusValue.setText("Approved");
                        statusValue.setTextColor(Color.rgb(10, 190, 10));
                    } else {
                        statusValue.setText("Pending");
                        statusValue.setTextColor(Color.RED);
                    }

                    visible(appplicationStatusHeading);
                    visible(nameKey);
                    visible(nameValue);
                    visible(phoneKey);
                    visible(phoneValue);
                    visible(addressKey);
                    visible(addressValue);
                    visible(statusKey);
                    visible(statusValue);

                    if (jsonObject.getBoolean("verified")) {
                        proceedButton.setVisibility(View.VISIBLE);
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                    Log.d("JsonException", e.toString());
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable throwable) {
                Toast.makeText(RegistrationStepTwoStatusActivity.this, throwable.toString(), Toast.LENGTH_SHORT).show();

            }
        });

    }


}

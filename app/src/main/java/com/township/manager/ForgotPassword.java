package com.township.manager;

import android.app.ProgressDialog;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.squareup.moshi.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ForgotPassword extends AppCompatActivity {

    MaterialButton resetPasswordButton;
    TextInputLayout emailTIL;
    EditText emailEdit;
    ProgressDialog progressDialog;
    ColorStateList colorStateList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        Toolbar toolbar = (Toolbar) findViewById(R.id.forgot_password_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Forgot Password");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        progressDialog=new ProgressDialog(ForgotPassword.this);
        progressDialog.setMessage("Just a moment...");

        resetPasswordButton = findViewById(R.id.reset_password_button);
        emailEdit = findViewById(R.id.email_text_input_child);
        emailTIL = findViewById(R.id.email_text_input_layout);

        resetPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendResetPasswordEmail();
            }
        });

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_activated},
                new int[]{android.R.attr.state_enabled}
        };

        int[] colors = new int[]{
                Color.GREEN,
                Color.RED,
        };
        colorStateList = new ColorStateList(states, colors);

        errorDisable(emailTIL);

    }
    private void errorDisable(final TextInputLayout textInputLayout) {
        Objects.requireNonNull(textInputLayout.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
                emailTIL.setHelperTextEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }

    private void sendResetPasswordEmail() {
        final String email;
        email=emailEdit.getText().toString();

        if(error(email,emailTIL))
            return;

        progressDialog.show();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call=retrofitServerAPI.getForgotPassword(
                email
        );
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String responseString = response.body().toString();
                try {
                    JSONArray responseArray = new JSONArray(responseString);
                    JSONObject loginJson = responseArray.getJSONObject(0);
                    if (loginJson.getString("user_found").equals("1")) {
                        if (loginJson.getString("email_sent").equals("1")) {
                            Toast.makeText(ForgotPassword.this,"Email has been sent",Toast.LENGTH_SHORT).show();
                            emailEdit.setText("");
                            progressDialog.dismiss();
                        }
                    }
                    else{
                        emailTIL.setHelperTextEnabled(true);
                        emailTIL.setHelperText("User not found");
                        emailTIL.setHelperTextColor(colorStateList);
                        progressDialog.dismiss();
                    }
                }catch (JSONException e) {
                    progressDialog.dismiss();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                progressDialog.dismiss();

            }
        });

    }

    private boolean error(String string,TextInputLayout textInputLayout) {
        if (TextUtils.isEmpty(string)){
            textInputLayout.setError("This field is required");
            textInputLayout.setErrorEnabled(true);
            textInputLayout.requestFocus();
            textInputLayout.setErrorIconDrawable(null);
            return true;
        }

        return false;
    }
}

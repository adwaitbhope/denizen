package com.township.manager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.pusher.pushnotifications.BeamsCallback;
import com.pusher.pushnotifications.PushNotifications;
import com.pusher.pushnotifications.PusherCallbackError;
import com.pusher.pushnotifications.auth.AuthData;
import com.pusher.pushnotifications.auth.AuthDataGetter;
import com.pusher.pushnotifications.auth.BeamsTokenProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginScreenActivity extends AppCompatActivity {
    public com.google.android.material.button.MaterialButton forgotpasswordButton, registersociety, contactus, loginButton;

    public TextInputEditText usernameEditText, passwordEditText;
    public TextInputLayout usernameTextLayout, passwordTextLayout;

    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        dbManager = new DBManager(this);
        Cursor cursor = dbManager.getDataLogin();
        if (cursor.getCount() != 0) {
            int columntypeindex = cursor.getColumnIndexOrThrow("Type");
            cursor.moveToFirst();
            switch (cursor.getString(columntypeindex)) {
                case "admin":
                    startActivity(new Intent(LoginScreenActivity.this, AdminHomeScreenActivity.class));
                    finish();
                    break;
                case "security":
                    startActivity(new Intent(LoginScreenActivity.this, SecurityHomeScreenActivity.class));
                    finish();
                    break;
                case "resident":
                    startActivity(new Intent(LoginScreenActivity.this, ResidentHomeScreenActivity.class));
                    finish();
                    break;
            }

        }
        forgotpasswordButton = findViewById(R.id.login_screen_forgot_password_button);
        forgotpasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginScreenActivity.this, ForgotPassword.class));
            }
        });

        registersociety = findViewById(R.id.login_screen_register_button);
        registersociety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisterSocietyScreen();
            }
        });

        contactus = findViewById(R.id.login_screen_contact_us_button);
        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });

        usernameEditText = (TextInputEditText) findViewById(R.id.login_screen_username_edit_text);
        passwordEditText = (TextInputEditText) findViewById(R.id.login_screen_password_edit_text);
        usernameTextLayout = (TextInputLayout) findViewById(R.id.login_screen_username_til);
        passwordTextLayout = (TextInputLayout) findViewById(R.id.login_screen_password_til);
        usernameTextLayout.setErrorEnabled(false);
        passwordTextLayout.setErrorEnabled(false);
        loginButton = findViewById(R.id.login_screen_login_button);
        logIn();
        error(usernameTextLayout);
        error(passwordTextLayout);

    }

    public void uploadToS3() {
        new Thread() {
            public void run() {
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "ap-southeast-1:9dad92cc-b78c-43e3-925c-1b18b7f6eb9a", // Identity pool ID
                        Regions.AP_SOUTHEAST_1 // Region
                );

                AmazonS3 s3Client = new AmazonS3Client(credentialsProvider);

                s3Client.putObject("township-manager", "townships/dataset/maintenance", "Pay the maintenance amount");
            }
        }.start();
    }

    private void error(final TextInputLayout textInputLayout) {
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


    public void logIn() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    usernameTextLayout.setError("Please enter your username");
                    usernameTextLayout.setErrorEnabled(true);
                    usernameTextLayout.requestFocus();
                    usernameTextLayout.setErrorIconDrawable(null);
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordTextLayout.setError("Please enter your password");
                    passwordTextLayout.setErrorEnabled(true);
                    passwordTextLayout.requestFocus();
                    passwordTextLayout.setErrorIconDrawable(null);
                    return;
                }

                StringRequest stringRequest = new StringRequest(Request.Method.POST, getString(R.string.server_addr) + "/login/",
                        new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {

                                    JSONArray jsonArray = new JSONArray(response);
                                    JSONObject jsonObjectLogin = jsonArray.getJSONObject(0);
                                    User user = new User();
                                    user.setLogin(jsonObjectLogin.getInt("login"));


                                    if (user.getLogin() == 1) {
                                        JSONObject jsonObjectLoginInfo = jsonArray.getJSONObject(1);
                                        user.setLoginType(jsonObjectLoginInfo.getString("type"));
                                        user.setUserName(jsonObjectLoginInfo.getString("username"));
                                        user.setPassword(password);
                                        user.setFirstName(jsonObjectLoginInfo.getString("first_name"));
                                        user.setLastName(jsonObjectLoginInfo.getString("last_name"));
                                        user.setPhoneNumber(jsonObjectLoginInfo.getString("phone"));
                                        user.setEmail(jsonObjectLoginInfo.getString("email"));
                                        user.setProfileUpdated(jsonObjectLoginInfo.getBoolean("profile_updated"));
                                        user.setTownship(jsonObjectLoginInfo.getString("township"));

                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(DBManager.ColUsername, user.getUserName());
                                        contentValues.put(DBManager.ColPassword, user.getPassword());
                                        contentValues.put(DBManager.ColFirstName, user.getFirstName());
                                        contentValues.put(DBManager.ColLastName, user.getLastName());
                                        contentValues.put(DBManager.ColPhone, user.getPhoneNumber());
                                        contentValues.put(DBManager.ColEmail, user.getEmail());
                                        contentValues.put(DBManager.ColProfileUpdated, user.getProfileUpdated());
                                        contentValues.put(DBManager.ColTownship, user.getTownship());
                                        contentValues.put(DBManager.ColType, user.getLoginType());
                                        //   Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();

                                        PushNotifications.start(getApplicationContext(), "f464fd4f-7e2f-4f42-91cf-8a8ef1a67acb");

                                        BeamsTokenProvider tokenProvider = new BeamsTokenProvider(
                                                getString(R.string.server_addr) + "/beams/get_token/",
                                                new AuthDataGetter() {
                                                    @Override
                                                    public AuthData getAuthData() {
                                                        HashMap<String, String> headers = new HashMap<>();
                                                        headers.put("username", username);
                                                        headers.put("password", password);
                                                        HashMap<String, String> queryParams = new HashMap<>();
                                                        return new AuthData(
                                                                headers,
                                                                queryParams
                                                        );
                                                    }
                                                }
                                        );

                                        PushNotifications.setUserId(username, tokenProvider, new BeamsCallback<Void, PusherCallbackError>(){
                                            @Override
                                            public void onSuccess(Void... values) {
                                                Log.i("PusherBeams", "Successfully authenticated with Pusher Beams");
                                            }

                                            @Override
                                            public void onFailure(PusherCallbackError error) {
                                                Log.i("PusherBeams", "Pusher Beams authentication failed: " + error.getMessage());
                                            }
                                        });


                                        Log.d("response", response);
                                        switch (user.getLoginType()) {

                                            case "admin": {
                                                user.setDesignation(jsonObjectLoginInfo.getString("designation"));
                                                contentValues.put(DBManager.ColDesignation, user.getDesignation());
                                                long id = dbManager.Insert(contentValues);
                                                PushNotifications.addDeviceInterest(jsonObjectLoginInfo.getString("township_id") + "-admins");
                                                startActivity(new Intent(LoginScreenActivity.this, AdminHomeScreenActivity.class));
                                                finish();
                                                break;
                                            }
                                            case "security": {
                                                long id = dbManager.Insert(contentValues);
                                                startActivity(new Intent(LoginScreenActivity.this, SecurityHomeScreenActivity.class));
                                                PushNotifications.addDeviceInterest(jsonObjectLoginInfo.getString("township_id") + "-security");
                                                finish();
                                                break;
                                            }
                                            case "resident": {
                                                user.setWing(jsonObjectLoginInfo.getString("wing"));
                                                contentValues.put(DBManager.ColWing, user.getWing());
                                                user.setApartment(jsonObjectLoginInfo.getString("apartment"));
                                                PushNotifications.addDeviceInterest(jsonObjectLoginInfo.getString("township_id") + "-residents");
                                                PushNotifications.addDeviceInterest(jsonObjectLoginInfo.getString("township_id") + "-" + jsonObjectLoginInfo.getString("wing_id") + "-residents");
                                                contentValues.put(DBManager.ColApartment, user.getApartment());
                                                long id = dbManager.Insert(contentValues);

                                                startActivity(new Intent(LoginScreenActivity.this, ResidentHomeScreenActivity.class));
                                                finish();
                                                break;
                                            }

                                        }

                                    } else {
                                        Log.d("login", "failed");
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }) {
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String, String> params = new HashMap<>();
                        params.put("username", username);
                        params.put("password", password);
                        return params;
                    }
                };

                ((GlobalVariables) getApplication()).getQueue().add(stringRequest);

            }
        });

    }

    public void openDialog() {
//        ContactUsDialog exampleDialog = new ContactUsDialog();
//        exampleDialog.show(getSupportFragmentManager(), "example dialog");

        MaterialAlertDialogBuilder materialAlertDialogBuilder = new MaterialAlertDialogBuilder(this);
        materialAlertDialogBuilder.setTitle("Contact us")
                .setMessage("\nE-mail: support@denizen.io\n\nPhone: +91 94054 38914")
                .setPositiveButton("Close", null)
                .show();
    }

    public void openRegisterSocietyScreen() {
        Intent intent = new Intent(LoginScreenActivity.this, RegistrationStepsActivity.class);
        startActivity(intent);
    }
}

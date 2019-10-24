package com.township.manager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.google.gson.Gson;
import com.pusher.pushnotifications.BeamsCallback;
import com.pusher.pushnotifications.PushNotifications;
import com.pusher.pushnotifications.PusherAlreadyRegisteredAnotherUserIdException;
import com.pusher.pushnotifications.PusherCallbackError;
import com.pusher.pushnotifications.auth.AuthData;
import com.pusher.pushnotifications.auth.AuthDataGetter;
import com.pusher.pushnotifications.auth.BeamsTokenProvider;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class LoginScreenActivity extends AppCompatActivity {
    public com.google.android.material.button.MaterialButton forgotpasswordButton, registersociety, contactus, loginButton;

    public TextInputEditText usernameEditText, passwordEditText;
    public TextInputLayout usernameTextLayout, passwordTextLayout;

    DBManager dbManager;
    private AppDatabase appDatabase;

    Resident[] residentsArray;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

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

                try {
                    InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
                    manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                } catch (Exception e) {
                    e.printStackTrace();
                }

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

                                    Log.d("login response", jsonArray.toString());

                                    if (jsonObjectLogin.getInt("login") == 1) {

                                        JSONObject jsonObjectLoginInfo = jsonArray.getJSONObject(1);

                                        ContentValues contentValues = new ContentValues();
                                        contentValues.put(DBManager.ColUsername, jsonObjectLoginInfo.getString("username"));
                                        contentValues.put(DBManager.ColPassword, password);
                                        contentValues.put(DBManager.ColFirstName, jsonObjectLoginInfo.getString("first_name"));
                                        contentValues.put(DBManager.ColLastName, jsonObjectLoginInfo.getString("last_name"));
                                        contentValues.put(DBManager.ColPhone, jsonObjectLoginInfo.getString("phone"));
                                        contentValues.put(DBManager.ColEmail, jsonObjectLoginInfo.getString("email"));
                                        contentValues.put(DBManager.ColProfileUpdated, jsonObjectLoginInfo.getBoolean("profile_updated"));
                                        contentValues.put(DBManager.ColTownship, jsonObjectLoginInfo.getString("township"));
                                        contentValues.put(DBManager.ColType, jsonObjectLoginInfo.getString("type"));
                                        contentValues.put(DBManager.ColTownshipId, jsonObjectLoginInfo.getString("township_id"));

                                        JSONArray jsonWings = jsonArray.getJSONArray(2);
                                        Gson gson = new Gson();
                                        final Wing[] wings = new Wing[jsonWings.length()];
                                        ArrayList<Resident> residents = new ArrayList<>();
                                        for (int i = 0; i < jsonWings.length(); i++) {
                                            JSONObject jsonWing = jsonWings.getJSONObject(i);
                                            JSONArray jsonArrayWings = jsonWing.getJSONArray("apartments");
                                            wings[i] = gson.fromJson(jsonWing.toString(), Wing.class);
                                            for (int j = 0; j < jsonArrayWings.length(); j++) {
                                                Resident resident = gson.fromJson(jsonArrayWings.getJSONObject(j).toString(), Resident.class);
                                                resident.setWing_id(wings[i].getWing_id());
                                                residents.add(resident);
                                            }
                                        }

                                        residentsArray = new Resident[residents.size()];
                                        residentsArray = residents.toArray(residentsArray);

                                        new Thread() {
                                            public void run() {
                                                WingDao wingDao = appDatabase.wingDao();
                                                ResidentDao residentDao = appDatabase.residentDao();
                                                wingDao.insert(wings);
                                                residentDao.insert(residentsArray);
                                            }
                                        }.start();

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
                                        PushNotifications.start(getApplicationContext(), "f464fd4f-7e2f-4f42-91cf-8a8ef1a67acb");

                                        try {
                                            PushNotifications.setUserId(username, tokenProvider, new BeamsCallback<Void, PusherCallbackError>() {
                                                @Override
                                                public void onSuccess(Void... values) {
                                                    Log.i("PusherBeams", "Successfully authenticated with Pusher Beams");
                                                }

                                                @Override
                                                public void onFailure(PusherCallbackError error) {
                                                    Log.i("PusherBeams", "Pusher Beams authentication failed: " + error.getMessage());
                                                }
                                            });
                                        } catch (PusherAlreadyRegisteredAnotherUserIdException e) {
                                            PushNotifications.clearAllState();
                                            PushNotifications.stop();
                                            PushNotifications.start(getApplicationContext(), "f464fd4f-7e2f-4f42-91cf-8a8ef1a67acb");
                                            PushNotifications.setUserId(username, tokenProvider, new BeamsCallback<Void, PusherCallbackError>() {
                                                @Override
                                                public void onSuccess(Void... values) {
                                                    Log.i("PusherBeams", "Successfully authenticated with Pusher Beams");
                                                }

                                                @Override
                                                public void onFailure(PusherCallbackError error) {
                                                    Log.i("PusherBeams", "Pusher Beams authentication failed: " + error.getMessage());
                                                }
                                            });

                                        }

                                        Log.d("response", response);
                                        dbManager.deleteAll();

                                        switch (jsonObjectLoginInfo.getString("type")) {

                                            case "admin": {
                                                contentValues.put(DBManager.ColDesignation, jsonObjectLoginInfo.getString("designation"));
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
                                                contentValues.put(DBManager.ColWing, jsonObjectLoginInfo.getString("wing"));
                                                PushNotifications.addDeviceInterest(jsonObjectLoginInfo.getString("township_id") + "-residents");
                                                PushNotifications.addDeviceInterest(jsonObjectLoginInfo.getString("township_id") + "-" + jsonObjectLoginInfo.getString("wing_id") + "-residents");
                                                contentValues.put(DBManager.ColApartment, jsonObjectLoginInfo.getString("apartment"));
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

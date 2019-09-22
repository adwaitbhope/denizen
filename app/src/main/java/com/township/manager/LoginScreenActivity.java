package com.township.manager;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.lang.String;

public class LoginScreenActivity extends FragmentActivity {
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
        if (cursor.getCount()!=0) {
            int columntypeindex = cursor.getColumnIndexOrThrow("Type");
            //  Log.d("hikr","hello");
            cursor.moveToFirst();
            switch (cursor.getString(columntypeindex)){
                case "admin":
                    startActivity(new Intent(LoginScreenActivity.this,AdminHomeScreenActivity.class));
                    finish();
                    break;
                case "security":
                    startActivity(new Intent(LoginScreenActivity.this,SecurityHomeScreenActivity.class));
                    finish();
                    break;
                case "resident":
                    startActivity(new Intent(LoginScreenActivity.this,ResidentHomeScreenActivity.class));
                    finish();
                    break;
            }

        }
        forgotpasswordButton = findViewById(R.id.login_screen_forgot_password_button);
        forgotpasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginScreenActivity.this,ForgotPassword.class));
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


        usernameEditText = (TextInputEditText) findViewById(R.id.login_username_edittext);
        passwordEditText = (TextInputEditText) findViewById(R.id.login_password_edittext);
        usernameTextLayout = (TextInputLayout) findViewById(R.id.login_username_text_layout);
        passwordTextLayout = (TextInputLayout) findViewById(R.id.login_password_text_layout);
        usernameTextLayout.setErrorEnabled(false);
        passwordTextLayout.setErrorEnabled(false);
        loginButton = findViewById(R.id.login_screen_login_button);
        logIn();
    }


    public void logIn() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    usernameTextLayout.setError("Please enter your username.");
                    usernameTextLayout.setErrorEnabled(true);
                    usernameTextLayout.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordTextLayout.setError("Please enter your password.");
                    passwordTextLayout.setErrorEnabled(true);
                    passwordTextLayout.requestFocus();
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
                                            Log.d("response", response);
                                            switch (user.getLoginType()) {

                                                case "admin": {
                                                    user.setDesignation(jsonObjectLoginInfo.getString("designation"));
                                                    contentValues.put(DBManager.ColDesignation, user.getDesignation());
                                                    long id = dbManager.Insert(contentValues);
                                                    startActivity(new Intent(LoginScreenActivity.this, AdminHomeScreenActivity.class));
                                                    finish();
                                                    break;
                                                }
                                                case "security": {
                                                    long id = dbManager.Insert(contentValues);
                                                    startActivity(new Intent(LoginScreenActivity.this, SecurityHomeScreenActivity.class));
                                                    finish();
                                                    break;
                                                }
                                                case "resident": {
                                                    user.setWing(jsonObjectLoginInfo.getString("wing"));
                                                    contentValues.put(DBManager.ColWing, user.getWing());
                                                    long id = dbManager.Insert(contentValues);
                                                    user.setApartment(jsonObjectLoginInfo.getString("apartment"));
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
        ContactUsDialog exampleDialog = new ContactUsDialog();
        exampleDialog.show(getSupportFragmentManager(), "example dialog");
    }

    public void openRegisterSocietyScreen() {
        Intent intent = new Intent(LoginScreenActivity.this, RegistrationStepsActivity.class);
        startActivity(intent);
    }
}

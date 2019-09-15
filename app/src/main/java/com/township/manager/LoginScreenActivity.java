package com.township.manager;

import android.content.ContentValues;
import android.content.Intent;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentTransaction;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.lang.String;

public class LoginScreenActivity extends FragmentActivity {
    public Button forgotpassword, registersociety, contactus, loginButton;
    public EditText usernameEditText, passwordEditText;

    DBManager dbManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);
        dbManager = new DBManager(this);
        forgotpassword = findViewById(R.id.forgotpassword);
        forgotpassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openForgotPasswordDialogFragment();
            }
        });
        registersociety = findViewById(R.id.registersociety);
        registersociety.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openRegisterSocietyDialogFragment();

            }
        });
        contactus = findViewById(R.id.contactus);
        contactus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        usernameEditText = findViewById(R.id.loginscreen_username_edittext);
        passwordEditText = findViewById(R.id.loginscreen_password_edittext);
        loginButton = findViewById(R.id.loginscreen_login_button);
        logIn();
    }

    public void logIn() {

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String username = usernameEditText.getText().toString();
                final String password = passwordEditText.getText().toString();

                if (TextUtils.isEmpty(username)) {
                    usernameEditText.setError("Please enter your username");
                    usernameEditText.requestFocus();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    passwordEditText.setError("Please enter your password");
                    passwordEditText.requestFocus();
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
                                    JSONObject jsonObjectLoginInfo = jsonArray.getJSONObject(1);

                                    user.setLogin(jsonObjectLogin.getInt("login"));
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


                                    if (user.getLogin() == 1) {
                                        //   Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                                        Log.d("response", response);
                                        switch (user.getLoginType()) {

                                            case "admin": {
                                                user.setDesignation(jsonObjectLoginInfo.getString("designation"));
                                                contentValues.put(DBManager.ColDesignation, user.getDesignation());
                                                long id = dbManager.Insert(contentValues);
                                                startActivity(new Intent(LoginScreenActivity.this, AdminHomeScreenActivity.class));
                                                break;
                                            }
                                            case "security": {
                                                long id = dbManager.Insert(contentValues);
                                                startActivity(new Intent(LoginScreenActivity.this, SecurityHomeScreenActivity.class));
                                                break;
                                            }
                                            case "resident": {
                                                user.setWing(jsonObjectLoginInfo.getString("wing"));
                                                contentValues.put(DBManager.ColWing, user.getWing());
                                                long id = dbManager.Insert(contentValues);
                                                user.setApartment(jsonObjectLoginInfo.getString("apartment"));
                                                startActivity(new Intent(LoginScreenActivity.this, ResidentHomeScreenActivity.class));
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

    public void openForgotPasswordDialogFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new ForgotPasswordFragment();
        dialogFragment.show(ft, getString(R.string.dialog));
    }

    public void openRegisterSocietyDialogFragment() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
        DialogFragment dialogFragment = new RegistrationDialogFragment();
        dialogFragment.show(ft, getString(R.string.dialog));
    }
}

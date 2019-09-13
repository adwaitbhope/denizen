package com.township.manager;

import android.content.Intent;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginScreenActivity extends FragmentActivity {
    public Button forgotpassword,registersociety,contactus,loginButton;
    public EditText usernameEditText,passwordEditText;
    String url= "https://township-manager.herokuapp.com";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_screen);

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
        usernameEditText=findViewById(R.id.loginscreen_username_edittext);
        passwordEditText=findViewById(R.id.loginscreen_password_edittext);
        loginButton=findViewById(R.id.loginscreen_login_button);
        logIn();
    }
    public void logIn(){
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

                  StringRequest stringRequest = new StringRequest(Request.Method.POST,url+"/login/",
                          new Response.Listener<String>() {
                              @Override
                              public void onResponse(String response) {
                                  try {

                                      JSONArray jsonArray= new JSONArray(response);
                                      JSONObject jsonObject=jsonArray.getJSONObject(0);
                                      Log.d("Take",username+password);
                                      if(jsonObject.getInt("login")==1){
                                          Toast.makeText(getApplicationContext(),response,Toast.LENGTH_SHORT).show();
                                          Log.d("response",response);
                                          switch (jsonObject.getString("type")){

                                              case "admin":{
                                                  startActivity(new Intent(LoginScreenActivity.this,AdminHomeScreenActivity.class));
                                                  Toast.makeText(getApplicationContext(),"admin",Toast.LENGTH_SHORT).show();
                                                  break;
                                              }
                                              case "security":{
                                                  startActivity(new Intent(LoginScreenActivity.this,SecurityHomeScreenActivity.class));
                                                  Toast.makeText(getApplicationContext(),"security",Toast.LENGTH_SHORT).show();
                                                  break;
                                              }
                                              case "resident": {

                                                  startActivity(new Intent(LoginScreenActivity.this, ResidentHomeScreenActivity.class));
                                                  Toast.makeText(getApplicationContext(),"resident",Toast.LENGTH_SHORT).show();
                                                  break;
                                              }

                                          }

                                      }
                                      else{
                                          Log.d("login","failed");
                                      }
                                  } catch (JSONException e) {
                                      e.printStackTrace();
                                      Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
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

    public void openForgotPasswordDialogFragment(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        Fragment prev = getSupportFragmentManager().findFragmentByTag("dialog");
        if (prev != null) {
            ft.remove(prev);
        }
        ft.addToBackStack(null);
//        DialogFragment dialogFragment = new ForgotPasswordFragment();
//        dialogFragment.show(ft, getString(R.string.dialog));
    }
    public void openRegisterSocietyDialogFragment(){
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

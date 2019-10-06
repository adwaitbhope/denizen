package com.township.manager;

import android.database.Cursor;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;

import org.w3c.dom.Text;

import java.util.Objects;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;

import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;

public class RegisterComplaintActivity extends AppCompatActivity {

    TextInputLayout titleTil,descriptiontil;
    Button uploadComplaintPhotoButton;
    String title,description;
    DBManager dbManager;
    int usernameCol,passwordCol;
    String username,password;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_complaint);



        titleTil=findViewById(R.id.register_complaint_title_textinput);
        descriptiontil=findViewById(R.id.register_complaint_description_textinput);
        uploadComplaintPhotoButton=findViewById(R.id.register_complaint_upload_photos_button);
        title=titleTil.getEditText().getText().toString();
        description=descriptiontil.getEditText().getText().toString();

        Toolbar toolbar = (Toolbar) findViewById(R.id.register_complaint_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register Complaint");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbManager=new DBManager(getApplicationContext());
        Cursor cursor=dbManager.getDataLogin();
        usernameCol=cursor.getColumnIndexOrThrow("Username");
        passwordCol=cursor.getColumnIndexOrThrow("Password");
        cursor.moveToFirst();
        username=cursor.getString(usernameCol);
        password=cursor.getString(passwordCol);

        title=findViewById(R.id.register_complaint_description_textinput_child);
        description=findViewById(R.id.register_complaint_description_textinput_child);

        error(descriptiontil);
        error(titleTil);
        MaterialButton registerComplaintButton = findViewById(R.id.register_complaint_button);
        registerComplaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // query to register a new complaint
                if(checkError()){
                    return;
                }
                Retrofit.Builder builder=new Retrofit.Builder()
                        .baseUrl(getString(R.string.server_addr))
                        .addConverterFactory(GsonConverterFactory.create());

                Retrofit retrofit=builder.build();

                RetrofitServerAPI retrofitServerAPI=retrofit.create(RetrofitServerAPI.class);

                Call<JsonArray> call=retrofitServerAPI.addComplaint(username,password,title,description);
                call.enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {

                    }

                    @Override
                    public void onFailure(Call<JsonArray> call, Throwable t) {

                    }
                });

            }
        });

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

    private boolean checkError() {
        boolean error=false;
        if(TextUtils.isEmpty(title)){
            titleTil.setError("Please enter the title");
            titleTil.setErrorEnabled(true);
            titleTil.requestFocus();
            titleTil.setErrorIconDrawable(null);
            error=true;
        }
        if(TextUtils.isEmpty(description)){
            descriptiontil.setError("Please enter the title");
            descriptiontil.setErrorEnabled(true);
            descriptiontil.requestFocus();
            descriptiontil.setErrorIconDrawable(null);
            error=true;
        }

        return error;
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
}

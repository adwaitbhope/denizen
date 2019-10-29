package com.township.manager;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.squareup.moshi.Json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static com.township.manager.SecurityPersonnelListFragment.ADD_SECURITY_PERSONNEL_RESULT;

public class AddSecurityPersonnelActivity extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener {


    TextInputLayout securityPersonnelNameTIL,securityPersonnelPhoneTIL;
    TextView securityPersonnelTimingsFrom,securityPersonnelTimingsTill;
    EditText securityPersonnelName,securityPersonnelPhone;
    MaterialButton timingsFromButton,timingsTillButton,cancelButton,saveButton,uploadPhoto;
    String username, password;
    int hourFrom,hourTill;
    int flag=0;
    Intent intent;
    SecurityPersonnel securityPersonnel;
    AppDatabase appDatabase;
    SecurityPersonnelDao securityPersonnelDao;
    File file;
    Context context;
    Bitmap photo;
    String township_id;
    private static final int PERMISSIONS_REQUEST_CODE = 42;
    private static final int PICK_IMAGE = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_security_personnel);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();


        securityPersonnelName=findViewById(R.id.add_security_personnel_name_edit_text);
        securityPersonnelNameTIL=findViewById(R.id.add_security_personnel_name_til);
        securityPersonnelPhoneTIL=findViewById(R.id.add_security_personnel_phone_number_til);
        securityPersonnelPhone=findViewById(R.id.add_security_personnel_phone_number_edit_text);
        securityPersonnelTimingsFrom=findViewById(R.id.add_security_personnel_shift_timings_from_text_view);
        securityPersonnelTimingsTill=findViewById(R.id.add_security_personnel_shift_timings_till_text_view);
        cancelButton=findViewById(R.id.add_security_personnel_cancel_button);
        saveButton=findViewById(R.id.add_security_personnel_save_button);
        uploadPhoto=findViewById(R.id.add_security_personnel_upload_photo_button);


        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();
        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
        township_id = cursor.getString(cursor.getColumnIndexOrThrow("TownshipId"));


        timingsFromButton = findViewById(R.id.add_security_personnel_shift_from_button);
        timingsTillButton = findViewById(R.id.add_security_personnel_shift_till_button);

        timingsFromButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerClass();
                flag=1;
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        timingsTillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerClass();
                flag=2;
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        intent=getIntent();

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_security_personnel_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Security");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);


        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickImage();
            }
        });

        if(intent.getStringExtra("type").equals("edit")){
            securityPersonnelName.setText(intent.getStringExtra("name"));
            securityPersonnelPhone.setText(intent.getStringExtra("phone"));
            securityPersonnelTimingsTill.setText(intent.getStringExtra("till"));
            securityPersonnelTimingsFrom.setText(intent.getStringExtra("from"));
        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(intent.getStringExtra("type").equals("add")){
                    addSecurityPersonnelToServer();
                }
                else{
                    editSecurityPersonnelToServer();
                }
            }
        });

    }

    private void addSecurityPersonnelToServer() {
        String name,phone,from,till;
        name=securityPersonnelName.getText().toString();
        phone=securityPersonnelPhone.getText().toString();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call=retrofitServerAPI.addNewSecurityPersonnel(
                username,
                password,
                name,
                phone,
                hourFrom,
                hourTill
        );
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String responseString = response.body().toString();
                try {
                    JSONArray responseArray = new JSONArray(responseString);
                    JSONObject loginJson = responseArray.getJSONObject(0);
                    if (loginJson.getString("login_status").equals("1")) {
                        if (loginJson.getString("request_status").equals("1")) {
                            Gson gson = new Gson();
                            securityPersonnel = gson.fromJson(responseArray.getJSONObject(1).toString(), SecurityPersonnel.class);
                            uploadToS3();
                            new AddSecurityPersonnelAsyncTask().execute();
                        }
                    }

                }catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });

    }

    private class AddSecurityPersonnelAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            securityPersonnelDao = appDatabase.securityPersonnelDao();
            if(intent.getStringExtra("type").equals("edit")){
                securityPersonnelDao.deleteById(intent.getStringExtra("'id"));
            }
            securityPersonnelDao.insert(securityPersonnel);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setResult(ADD_SECURITY_PERSONNEL_RESULT);
            super.onPostExecute(aVoid);
        }
    }

    private void pickImage() {
        String readPermission = Manifest.permission.READ_EXTERNAL_STORAGE;
        String writePermission = Manifest.permission.WRITE_EXTERNAL_STORAGE;

        if (ContextCompat.checkSelfPermission(this, readPermission) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, writePermission) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, readPermission) || ActivityCompat.shouldShowRequestPermissionRationale(this, readPermission)) {
                showError();
            } else {
                ActivityCompat.requestPermissions(this, new String[]{readPermission, writePermission}, PERMISSIONS_REQUEST_CODE);
            }
        } else {
            Intent getIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(Intent.createChooser(getIntent, "Select a photo"), PICK_IMAGE);
        }
    }
    private void showError() {
        Toast.makeText(this, "Please allow external storage access", Toast.LENGTH_SHORT).show();
    }

    private void uploadToS3() {

        new Thread() {
            public void run() {
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "ap-southeast-1:9dad92cc-b78c-43e3-925c-1b18b7f6eb9a", // Identity pool ID
                        Regions.AP_SOUTHEAST_1 // Region
                );

                AmazonS3 s3Client = new AmazonS3Client(credentialsProvider);

                try {
                    if(file!=null) {
                        FileInputStream stream = new FileInputStream(file);
                        ObjectMetadata metadata = new ObjectMetadata();
                        metadata.setContentLength(file.length());
                        s3Client.putObject("township-manager", "townships/" + township_id + "/security/" + securityPersonnel.getPersonnel_id() + ".png", stream, metadata);
                    }
                    finish();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private void editSecurityPersonnelToServer(){
        String name,phone,from,till;
        name=securityPersonnelName.getText().toString();
        phone=securityPersonnelPhone.getText().toString();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call=retrofitServerAPI.editSecurityPersonnel(
                username,
                password,
                intent.getStringExtra("id"),
                name,
                phone,
                hourFrom,
                hourTill
        );
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String responseString = response.body().toString();
                try {
                    JSONArray responseArray = new JSONArray(responseString);
                    JSONObject loginJson = responseArray.getJSONObject(0);
                    if (loginJson.getString("login_status").equals("1")) {
                        if (loginJson.getString("request_status").equals("1")) {
                            Gson gson = new Gson();
                            securityPersonnel = gson.fromJson(responseArray.getJSONObject(1).toString(), SecurityPersonnel.class);
                            uploadToS3();
                            new AddSecurityPersonnelAsyncTask().execute();
                        }
                    }


                }catch (JSONException e) {
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
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//        TextView textView = (TextView) findViewById(R.id.textView);
//        textView.setText("Hour: " + hourOfDay + " Minute: " + minute);
        if(flag==1) {
            hourFrom = hourOfDay;
            securityPersonnelTimingsFrom.setText(hourFrom);
        }
        else if(flag==2) {
            hourTill = hourOfDay;
            securityPersonnelTimingsTill.setText(hourTill);
        }
    }
}

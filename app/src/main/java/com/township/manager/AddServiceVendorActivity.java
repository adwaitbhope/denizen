package com.township.manager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.township.manager.ServiceVendorFragment.ADD_SERVICE_VENODR_RESULT;

public class AddServiceVendorActivity extends AppCompatActivity {

    ImageView serviceVendorImage;
    MaterialButton saveVendor;
    TextInputLayout firstNameTIL,workTIL,phoneTIL,lastNameTIL;
    TextInputEditText firstName,lastName,phoneEditText,workEditText;
    String username, password;
    AppDatabase appDatabase;
    ServiceVendors serviceVendors;
    private static final int PERMISSIONS_REQUEST_CODE = 42;
    String township_id;
    ServiceVendorDao serviceVendorDao;
    Intent intent;

    private static final int PICK_IMAGE = 1;
    File file;
    Bitmap photo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_service_vendor);
        firstName=findViewById(R.id.service_vendor_first_name_child);
        firstNameTIL=findViewById(R.id.service_vendor_first_name);

        lastName=findViewById(R.id.service_vendor_last_name_child);
        lastNameTIL=findViewById(R.id.service_vendor_last_name);

        workEditText=findViewById(R.id.service_vendor_service_work_child);
        workTIL=findViewById(R.id.service_vendor_service_work);

        phoneEditText=findViewById(R.id.service_vendor_contact_no_child);
        phoneTIL=findViewById(R.id.service_vendor_contact_no);

        saveVendor=findViewById(R.id.save_service_vendor_button);

        serviceVendorImage=findViewById(R.id.service_vendor_profile_photo);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
        township_id = cursor.getString(cursor.getColumnIndexOrThrow("TownshipId"));


        Toolbar toolbar = (Toolbar) findViewById(R.id.add_service_vendor_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Vendor");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);


        intent=getIntent();
        if(intent.getStringExtra("request").equals("1")) {
            saveVendor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    addVendorToServer();
                }
            });
            serviceVendorImage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    pickImage();
                }
            });

        }
        else{

            final String url = "https://township-manager.s3.ap-south-1.amazonaws.com/townships/" +township_id+ "/servicevendors/" + intent.getStringExtra("id") + ".png";
            Picasso.get()
                    .load(url)
                    .into(serviceVendorImage);
            firstName.setText(intent.getStringExtra("first_name"));
            lastName.setText(intent.getStringExtra("last_name"));
            phoneEditText.setText(intent.getStringExtra("phone"));
            workEditText.setText(intent.getStringExtra("work"));
            saveVendor.setText("Edit Information");
            saveVendor.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    editVendorToServer();
                }
            });
        }

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

    private void editVendorToServer() {

        String firstname,lastname,phone,work;
        firstname=firstName.getText().toString();
        lastname=lastName.getText().toString();
        phone=phoneEditText.getText().toString();
        work=workEditText.getText().toString();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call=retrofitServerAPI.editServiceVendors(username,password,intent.getStringExtra("id"),firstname,lastname,phone,work);

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
                            serviceVendors = gson.fromJson(responseArray.getJSONObject(1).toString(), ServiceVendors.class);
                            new EditServiceVendorAsyncTask().execute();
                        }
                    }

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });


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

    public void uploadToS3() {
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
                        s3Client.putObject("township-manager", "townships/" + township_id + "/servicevendors/" + serviceVendors.getVendor_id() + ".png", stream, metadata);
                    }
                    finish();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();

                try {
                    photo = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                serviceVendorImage.setImageBitmap(photo);

                new Thread() {
                    public void run() {
                        file = getOutputMediaFile();
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            photo.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                            fileOutputStream.close();
                        } catch (FileNotFoundException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }.start();

            }

        }
    }
    public File getOutputMediaFile() {
        File root = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.app_name));
        if (!root.exists()) {
            root.mkdirs();
        }
        File filepath = new File(root.getPath() + File.separator + "abc.png");
        return filepath;
    }


    private void showError() {
        Toast.makeText(this, "Please allow external storage access", Toast.LENGTH_SHORT).show();
    }
    private void addVendorToServer() {

        String firstname,lastname,phone,work;
        firstname=firstName.getText().toString();
        lastname=lastName.getText().toString();
        phone=phoneEditText.getText().toString();
        work=workEditText.getText().toString();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);
        Log.d("phone",phone);
        Call<JsonArray> call=retrofitServerAPI.addServiceVendors(username,password,firstname,lastname,phone,work);

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
                            serviceVendors = gson.fromJson(responseArray.getJSONObject(1).toString(), ServiceVendors.class);
                            uploadToS3();

                            new AddServiceVendorAsyncTask().execute();
                        }
                    }

                }
                catch (JSONException e) {
                    e.printStackTrace();
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
    }
    private class AddServiceVendorAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            serviceVendorDao = appDatabase.serviceVendorDao();
            serviceVendorDao.insert(serviceVendors);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setResult(ADD_SERVICE_VENODR_RESULT);
//            finish();
            super.onPostExecute(aVoid);
        }
    }

    private class EditServiceVendorAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            serviceVendorDao = appDatabase.serviceVendorDao();
            serviceVendorDao.delete(intent.getStringExtra("id"));
            serviceVendorDao.insert(serviceVendors);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setResult(ADD_SERVICE_VENODR_RESULT);
//            finish();
            super.onPostExecute(aVoid);
        }
    }



}

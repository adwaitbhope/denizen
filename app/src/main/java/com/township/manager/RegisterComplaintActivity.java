package com.township.manager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
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

    TextInputLayout titleTil, descriptiontil;
    Button uploadComplaintPhotoButton;
    private String title, description;
    DBManager dbManager;
    int usernameCol, passwordCol;
    String username, password;
    TextInputEditText complaintTitle, complaintDescription;
    Bitmap compalintPhoto;
    private static final int PERMISSIONS_REQUEST_CODE = 42;
    private static final int PICK_IMAGE = 1;
    File file;
    String township_id;
    Complaint complaint;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_complaint);


        titleTil = findViewById(R.id.register_complaint_title_textinput);
        descriptiontil = findViewById(R.id.register_complaint_description_textinput);
        uploadComplaintPhotoButton = findViewById(R.id.register_complaint_upload_photos_button);

        complaintTitle = (TextInputEditText) findViewById(R.id.register_complaint_title_textinput_child);
        complaintDescription = (TextInputEditText) findViewById(R.id.register_complaint_description_textinput_child);


        Toolbar toolbar = (Toolbar) findViewById(R.id.register_complaint_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Register Complaint");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        usernameCol = cursor.getColumnIndexOrThrow("Username");
        passwordCol = cursor.getColumnIndexOrThrow("Password");

        cursor.moveToFirst();
        username = cursor.getString(usernameCol);
        password = cursor.getString(passwordCol);
        township_id = cursor.getString(cursor.getColumnIndexOrThrow("TownshipId"));
        Log.d("townshipid",township_id);



        error(descriptiontil);
        error(titleTil);

        uploadComplaintPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectPhoto();
            }
        });

        MaterialButton registerComplaintButton = findViewById(R.id.register_complaint_button);
        registerComplaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addComplaint();
            }
        });

    }

    private void selectPhoto() {

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE) {
            if (data != null) {
                Uri uri = data.getData();

                try {
                    compalintPhoto = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new Thread() {
                    public void run() {
                         file = getOutputMediaFile();
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            compalintPhoto.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
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

    public void addComplaint() {
        title = complaintTitle.getText().toString();
        description = complaintDescription.getText().toString();

        if (TextUtils.isEmpty(title)) {
            titleTil.setError("Please enter the title");
            titleTil.setErrorEnabled(true);
            titleTil.requestFocus();
            titleTil.setErrorIconDrawable(null);
            return;
        }
        if (TextUtils.isEmpty(description)) {
            descriptiontil.setError("Please enter the description");
            descriptiontil.setErrorEnabled(true);
            descriptiontil.requestFocus();
            descriptiontil.setErrorIconDrawable(null);
            return;
        }

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());

        Retrofit retrofit = builder.build();
        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);
        Call<JsonArray> call = retrofitServerAPI.addComplaint(username, password, title, description);
        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
              String responseString=response.body().toString();

                try {
                    JSONArray responseArray = new JSONArray(responseString);
                    JSONObject loginJson = responseArray.getJSONObject(0);
                    if (loginJson.getString("login_status").equals("1")) {
                        if (loginJson.getString("request_status").equals("1")) {
                            Gson gson=new Gson();
                            complaint=gson.fromJson(responseArray.getJSONObject(1).toString(),Complaint.class);
                            uploadComplaintPhotoToS3();
                            Toast.makeText(RegisterComplaintActivity.this,"Complaint Registered",Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                catch (JSONException e) {
                    Log.d("ermes",e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {
                Log.d("notsent", t.toString());
            }
        });

    }

    private void uploadComplaintPhotoToS3() {
        new Thread() {

            public void run() {
                Log.d("Infiide","infile");
                CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                        getApplicationContext(),
                        "ap-southeast-1:9dad92cc-b78c-43e3-925c-1b18b7f6eb9a", // Identity pool ID
                        Regions.AP_SOUTHEAST_1 // Region
                );

                AmazonS3 s3Client = new AmazonS3Client(credentialsProvider);

                try {
                    FileInputStream stream = new FileInputStream(file);
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(file.length());
                    Log.d("Infiie","infile");
                    s3Client.putObject("township-manager", "townships/" + township_id + "/complaints/" + complaint.getComplaint_id() + ".png", stream, metadata);
                    finish();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                    Log.d("erocomplaint",e.getMessage());
                }

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

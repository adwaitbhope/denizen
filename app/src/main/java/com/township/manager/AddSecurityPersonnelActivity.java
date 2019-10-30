package com.township.manager;

import android.Manifest;
import android.app.TimePickerDialog;
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
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.android.material.button.MaterialButton;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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


    TextInputLayout firstNameTIL, lastNameTIL, phoneTIL;
    EditText firstNameEditText, lastNameEditText, phoneEditText;
    TextView securityPersonnelTimingsFrom, securityPersonnelTimingsTill;
    MaterialButton timingsFromButton, timingsTillButton, cancelButton, saveButton, uploadPhoto;
    ImageView imageView;

    String username, password;
    String TOWNSHIP_ID;

    int hourFrom, hourTill;
    int minuteFrom, minuteTill;
    int flag = 0;

    Intent intent;

    SecurityPersonnel securityPersonnel;

    AppDatabase appDatabase;
    SecurityPersonnelDao securityPersonnelDao;

    File file;
    Context context;
    Bitmap photo;
    String township_id;

    String firstName, lastName, phone;

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


        firstNameEditText = findViewById(R.id.add_security_personnel_first_name_edit_text);
        firstNameTIL = findViewById(R.id.add_security_personnel_first_name_til);

        lastNameEditText = findViewById(R.id.add_security_personnel_last_name_edit_text);
        lastNameTIL = findViewById(R.id.add_security_personnel_last_name_til);

        phoneEditText = findViewById(R.id.add_security_personnel_phone_number_edit_text);
        phoneTIL = findViewById(R.id.add_security_personnel_phone_number_til);

        securityPersonnelTimingsFrom = findViewById(R.id.add_security_personnel_shift_timings_from_text_view);
        securityPersonnelTimingsTill = findViewById(R.id.add_security_personnel_shift_timings_till_text_view);

        imageView = ((ImageView) findViewById(R.id.add_security_personnel_photo_image_view));

        cancelButton = findViewById(R.id.add_security_personnel_cancel_button);
        saveButton = findViewById(R.id.add_security_personnel_save_button);
        uploadPhoto = findViewById(R.id.add_security_personnel_upload_photo_button);

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
                flag = 1;
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        timingsTillButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment timePicker = new TimePickerClass();
                flag = 2;
                timePicker.show(getSupportFragmentManager(), "time picker");
            }
        });

        intent = getIntent();

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

        if (intent.getStringExtra("type").equals("edit")) {
            firstNameEditText.setText(intent.getStringExtra("first_name"));
            lastNameEditText.setText(intent.getStringExtra("last_name"));
            phoneEditText.setText(intent.getStringExtra("phone"));
            String TOWNSHIP_ID = intent.getStringExtra("township_id");

            hourFrom = Integer.valueOf(intent.getStringExtra("shift_from").substring(0, 2));
            minuteFrom = Integer.valueOf(intent.getStringExtra("shift_from").substring(3, 5));

            hourTill = Integer.valueOf(intent.getStringExtra("shift_till").substring(0, 2));
            minuteTill = Integer.valueOf(intent.getStringExtra("shift_till").substring(3, 5));

            securityPersonnelTimingsFrom.setVisibility(View.VISIBLE);
            securityPersonnelTimingsFrom.setText(getFormattedTime(hourFrom, minuteFrom));
            securityPersonnelTimingsTill.setVisibility(View.VISIBLE);
            securityPersonnelTimingsTill.setText(getFormattedTime(hourTill, minuteTill));

            String id = intent.getStringExtra("'id");
            final String url = "https://township-manager.s3.ap-south-1.amazonaws.com/townships/" + TOWNSHIP_ID + "/security_personnel/" + intent.getStringExtra("id") + ".png";
            Picasso.get()
                    .load(url)
                    .into(imageView);

            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddSecurityPersonnelActivity.this, FullScreenImageViewActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            });

        }

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (intent.getStringExtra("type").equals("edit")) {
                    editSecurityPersonnelToServer();
                } else {
                    addSecurityPersonnelToServer();
                }
            }
        });

    }

    private void addSecurityPersonnelToServer() {
        firstName = firstNameEditText.getText().toString();
        lastName = lastNameEditText.getText().toString();
        phone = phoneEditText.getText().toString();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.addNewSecurityPersonnel(
                username,
                password,
                firstName,
                lastName,
                phone,
                hourFrom,
                minuteFrom,
                hourTill,
                minuteTill
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
                            ((ProgressBar) findViewById(R.id.add_security_personnel_image_progress_bar)).setVisibility(View.VISIBLE);
                            new UploadToS3().execute();
                            new AddSecurityPersonnelAsyncTask().execute();
                        }
                    }

                } catch (JSONException e) {
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
            if (intent.getStringExtra("type").equals("edit")) {
                securityPersonnelDao.deleteById(intent.getStringExtra("'id"));
            }
            securityPersonnelDao.insert(securityPersonnel);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setResult(ADD_SECURITY_PERSONNEL_RESULT);
            finish();
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

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           @NonNull String permissions[], @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CODE: {
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    Intent getIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(Intent.createChooser(getIntent, "Select a photo"), PICK_IMAGE);
                } else {
                    showError();
                }
            }
        }
    }

    private void showError() {
        Toast.makeText(this, "Please allow external storage access", Toast.LENGTH_SHORT).show();
    }

    public File getOutputMediaFile() {
        File root = new File(Environment.getExternalStorageDirectory(), getResources().getString(R.string.app_name));
        if (!root.exists()) {
            root.mkdirs();
        }
        File filepath = new File(root.getPath() + File.separator + "abc.png");
        return filepath;
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
                ((ImageView) findViewById(R.id.add_security_personnel_photo_image_view)).setImageBitmap(photo);

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

//                        if (intent.getStringExtra("type").equals("edit")) {
//                            ((ProgressBar) findViewById(R.id.add_security_personnel_image_progress_bar)).setVisibility(View.VISIBLE);
//                            new UploadToS3().execute();
//                        }
                    }
                }.start();

            }

        }
    }

    private class UploadToS3 extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                    getApplicationContext(),
                    "ap-southeast-1:9dad92cc-b78c-43e3-925c-1b18b7f6eb9a", // Identity pool ID
                    Regions.AP_SOUTHEAST_1 // Region
            );

            AmazonS3 s3Client = new AmazonS3Client(credentialsProvider);

            try {
                if (file != null) {
                    FileInputStream stream = new FileInputStream(file);
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(file.length());
                    s3Client.putObject("township-manager", "townships/" + township_id + "/security_personnel/" + securityPersonnel.getPersonnel_id() + ".png", stream, metadata);
                }
                finish();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ((ProgressBar) findViewById(R.id.add_security_personnel_image_progress_bar)).setVisibility(View.INVISIBLE);
            final String url = "https://township-manager.s3.ap-south-1.amazonaws.com/townships/" + TOWNSHIP_ID + "/security_personnel/" + securityPersonnel.getPersonnel_id() + ".png";
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(AddSecurityPersonnelActivity.this, FullScreenImageViewActivity.class);
                    intent.putExtra("url", url);
                    startActivity(intent);
                }
            });
            super.onPostExecute(aVoid);
        }
    }


    private void editSecurityPersonnelToServer() {
        firstName = firstNameEditText.getText().toString();
        lastName = lastNameEditText.getText().toString();
        phone = phoneEditText.getText().toString();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.editSecurityPersonnel(
                username,
                password,
                intent.getStringExtra("id"),
                firstName,
                lastName,
                phone,
                hourFrom,
                minuteFrom,
                hourTill,
                minuteTill
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
                            ((ProgressBar) findViewById(R.id.add_security_personnel_image_progress_bar)).setVisibility(View.VISIBLE);
                            new UploadToS3().execute();
                            new AddSecurityPersonnelAsyncTask().execute();
                        }
                    }

                } catch (JSONException e) {
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
    public void onTimeSet(TimePicker view, int hourOfDay, int minuteOfHour) {
        if (flag == 1) {
            hourFrom = hourOfDay;
            minuteFrom = minuteOfHour;
            securityPersonnelTimingsFrom.setVisibility(View.VISIBLE);
            securityPersonnelTimingsFrom.setText(getFormattedTime(hourOfDay, minuteOfHour));
        } else if (flag == 2) {
            hourTill = hourOfDay;
            minuteTill = minuteOfHour;
            securityPersonnelTimingsTill.setVisibility(View.VISIBLE);
            securityPersonnelTimingsTill.setText(getFormattedTime(hourOfDay, minuteOfHour));
        }
    }

    public String getFormattedTime(int hourOfDay, int minuteOfHour) {
        String hour = "12";
        String minute = "00";
        String period = "am";
        if (hourOfDay != 0) {
            if (hourOfDay > 12) {
                hourOfDay -= 12;
                period = "pm";
            }
            hour = String.valueOf(hourOfDay);
        }

        if (minuteOfHour != 0) {
            minute = String.valueOf(minuteOfHour);
        }

        return hour + ":" + minute + " " + period;
    }

}

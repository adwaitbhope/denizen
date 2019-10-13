package com.township.manager;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.nbsp.materialfilepicker.MaterialFilePicker;
import com.nbsp.materialfilepicker.ui.FilePickerActivity;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

import androidx.annotation.NonNull;
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

import static com.township.manager.NoticeBoardFragment.ADD_NOTICE_RESULT;

public class AddNoticeAdminActivity extends AppCompatActivity {

    String username, password;
    String title, description;
    String township_id;

    AppDatabase appDatabase;

    NoticeDao noticeDao;
    WingDao wingDao;
    NoticeWingDao noticeWingDao;

    Notice notice;
    NoticeWing[] noticeWingArray;

    ArrayList<Wing> wings;
    Map<String, Boolean> wing_selected;
    final ArrayList<Chip> chips = new ArrayList<>();

    private static final int FILTER_CHIP_ID = 6420;
    private static final int PERMISSIONS_REQUEST_CODE = 42;
    private static final int PICK_IMAGE = 1;

    File file;
    Context context;
    Bitmap photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_notice_admin);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_notice_admin_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Notice");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        context = this;
        wing_selected = new HashMap<>();
        new GetWingChipsFromDatabase().execute();

        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
        township_id = cursor.getString(cursor.getColumnIndexOrThrow("TownshipId"));

        MaterialButton addDocumentButton = findViewById(R.id.add_notice_upload_document_button);
        addDocumentButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        MaterialButton publishNoticeButton = findViewById(R.id.add_notice_publish_notice_button);
        publishNoticeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                publishNotice();
            }
        });

    }

    private void publishNotice() {

        title = ((TextInputEditText) findViewById(R.id.add_notice_title_edittext)).getText().toString();
        description = ((TextInputEditText) findViewById(R.id.add_notice_description_edittext)).getText().toString();
        final Map<String, String> wing_ids = new HashMap<>();

        int counter = 0;
        for (Map.Entry entry : wing_selected.entrySet()) {
            if ((Boolean) entry.getValue()) {
                wing_ids.put("wing_" + String.valueOf(counter) + "_id", (String) entry.getKey());
                counter++;
            }
        }

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.addNotice(
                username,
                password,
                title,
                description,
                String.valueOf(wing_ids.size()),
                wing_ids
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
                            notice = gson.fromJson(responseArray.getJSONObject(1).toString(), Notice.class);
                            uploadToS3();

                            JSONArray wingsArray = responseArray.getJSONArray(2);
                            noticeWingArray = new NoticeWing[wingsArray.length()];
                            NoticeWing noticeWing;

                            for (int i = 0; i < wingsArray.length(); i++) {
                                Wing wing = gson.fromJson(wingsArray.getJSONObject(i).toString(), Wing.class);
                                noticeWing = new NoticeWing();
                                noticeWing.setWing_id(wing.getWing_id());
                                noticeWing.setNotice_id(notice.getNotice_id());
                                noticeWingArray[i] = noticeWing;
                            }

                            new AddNoticeAsyncTask().execute();
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
                setResult(123);
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private class AddNoticeAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noticeDao = appDatabase.noticeDao();
            noticeWingDao = appDatabase.noticeWingsDao();
            noticeDao.insert(notice);
            noticeWingDao.insert(noticeWingArray);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setResult(ADD_NOTICE_RESULT);
//            finish();
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
                ((ImageView) findViewById(R.id.add_notice_preview_image)).setVisibility(View.VISIBLE);
                ((ImageView) findViewById(R.id.add_notice_preview_image)).setImageBitmap(photo);

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
                    FileInputStream stream = new FileInputStream(file);
                    ObjectMetadata metadata = new ObjectMetadata();
                    metadata.setContentLength(file.length());
                    s3Client.putObject("township-manager", "townships/" + township_id + "/notices/" + notice.getNotice_id() + ".png", stream, metadata);
                    finish();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

            }
        }.start();
    }

    private class GetWingChipsFromDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wingDao = appDatabase.wingDao();
            wings = (ArrayList<Wing>) wingDao.getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            final ChipGroup chipGroup = findViewById(R.id.add_notice_visibility_chip_group);
            Chip chip;

            chip = new Chip(context);
            chip.setText("Admins");
            chip.setChecked(true);
            chip.setChipDrawable(ChipDrawable.createFromResource(context, R.xml.chip));
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    buttonView.setChecked(!isChecked);
                }
            });
            chips.add(chip);

            chip = new Chip(context);
            chip.setText("Everyone");
            chip.setChipDrawable(ChipDrawable.createFromResource(context, R.xml.chip));
            chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    for (int i = 1; i < chips.size(); i++) {
                        chips.get(i).setChecked(isChecked);
                    }
                }
            });
            chips.add(chip);

            int counter = 1;
            for (Wing wing : wings) {
                chip = new Chip(context);
                chip.setId(FILTER_CHIP_ID + counter);
                counter++;
                chip.setChipDrawable(ChipDrawable.createFromResource(context, R.xml.chip));
                chip.setHint(wing.getWing_id());
                chip.setText("Wing " + wing.getName());
                chip.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        wing_selected.put(buttonView.getHint().toString(), isChecked);
                        manageChipVisibility();

                    }
                });
                chips.add(chip);
            }

            for (Chip c : chips) {
                chipGroup.addView(c);
            }

            super.onPostExecute(aVoid);
        }

    }

    public void manageChipVisibility() {
        boolean allChecked = true;
        for (int i = 2; i < chips.size(); i++) {
            if (!chips.get(i).isChecked()) {
                allChecked = false;
            }
        }
        chips.get(1).setChecked(allChecked);
    }

}

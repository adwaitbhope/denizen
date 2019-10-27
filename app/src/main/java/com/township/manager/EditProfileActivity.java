package com.township.manager;

import android.Manifest;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.pusher.pushnotifications.PushNotifications;
import com.squareup.moshi.JsonAdapter;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static com.township.manager.ProfileActivity.PROFILE_EDITED;
import static com.township.manager.ProfileActivity.PROFILE_NOT_EDITED;

public class EditProfileActivity extends AppCompatActivity {

    Cursor cursor;
    String org_username, org_password;
    String user_id, username, password;
    String firstName, lastName, designation, phone, email, type;
    String township_id;
    int profileUpdated;
    DBManager dbManager;

    ColorStateList colorStateList;
    boolean isUsernameValid = true;

    private static final int PERMISSIONS_REQUEST_CODE = 42;
    private static final int PICK_IMAGE = 1;

    File file;
    Bitmap photo;
    boolean profilePicUploading = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit profile");

        dbManager = new DBManager(getApplicationContext());
        cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        user_id = cursor.getString(cursor.getColumnIndexOrThrow("User_Id"));
        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
        org_username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        org_password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
        township_id = cursor.getString(cursor.getColumnIndexOrThrow("TownshipId"));
        firstName = cursor.getString(cursor.getColumnIndexOrThrow("First_Name"));
        lastName = cursor.getString(cursor.getColumnIndexOrThrow("Last_Name"));
        email = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
        phone = cursor.getString(cursor.getColumnIndexOrThrow("Phone"));
        type = cursor.getString(cursor.getColumnIndexOrThrow("Type"));
        profileUpdated = cursor.getInt(cursor.getColumnIndexOrThrow("Profile_Updated"));

        if (profileUpdated == 1) {
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        TextInputLayout[] layouts = new TextInputLayout[5];
        layouts[0] = ((TextInputLayout) findViewById(R.id.edit_profile_first_name_til));
        layouts[1] = ((TextInputLayout) findViewById(R.id.edit_profile_last_name_til));
        layouts[2] = ((TextInputLayout) findViewById(R.id.edit_profile_designation_til));
        layouts[3] = ((TextInputLayout) findViewById(R.id.edit_profile_email_til));
        layouts[4] = ((TextInputLayout) findViewById(R.id.edit_profile_phone_til));

        for (final TextInputLayout layout : layouts) {
            layout.getEditText().addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {
                    if (s.length() > 0) {
                        layout.setError(null);
                        layout.setErrorEnabled(false);
                    }
                }

                @Override
                public void afterTextChanged(Editable s) {

                }
            });
        }

        if (!type.equals("resident")) {
            designation = cursor.getString(cursor.getColumnIndexOrThrow("Designation"));
        }

        ImageView profilePic = ((ImageView) findViewById(R.id.edit_profile_photo));
        final String url = "https://township-manager.s3.ap-south-1.amazonaws.com/townships/" + township_id + "/user_profile_pics/" + user_id + ".png";
        Picasso.get()
                .load(url)
                .noFade()
                .placeholder(R.drawable.ic_man)
                .error(R.drawable.ic_man)
//                .networkPolicy(NetworkPolicy.NO_CACHE)
                .into(profilePic);

        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        ((TextInputEditText) findViewById(R.id.edit_profile_first_name_edit_text)).setText(firstName);
        ((TextInputEditText) findViewById(R.id.edit_profile_last_name_edit_text)).setText(lastName);
        ((TextInputEditText) findViewById(R.id.edit_profile_username_edit_text)).setText(username);
        ((TextInputEditText) findViewById(R.id.edit_profile_phone_edit_text)).setText(phone);
        ((TextInputEditText) findViewById(R.id.edit_profile_email_edit_text)).setText(email);

        if (!type.equals("resident")) {
            ((TextInputLayout) findViewById(R.id.edit_profile_designation_til)).setVisibility(View.VISIBLE);
            ((TextInputEditText) findViewById(R.id.edit_profile_designation_edit_text)).setText(designation);
        }

        if (phone == null) {
            ((TextInputEditText) findViewById(R.id.edit_profile_phone_edit_text)).setText("");
        }

        int[][] states = new int[][]{
                new int[]{android.R.attr.state_activated},
                new int[]{-android.R.attr.state_activated}
        };

        int[] colors = new int[]{
                Color.GREEN,
                Color.RED,
        };

        colorStateList = new ColorStateList(states, colors);
        setPasswordChecks();
        setUsernameChecks();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.edit_profile_app_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                showExitDialog();
                return true;

            case R.id.edit_profile_save_changes_button:
                showSaveChangesDialog();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void showExitDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Discard changes?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setResult(PROFILE_NOT_EDITED);
                        EditProfileActivity.this.finish();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void showSaveChangesDialog() {
        new MaterialAlertDialogBuilder(this)
                .setTitle("Save changes?")
                .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveChanges();
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void saveChanges() {
        try {
            InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ProgressBar) findViewById(R.id.edit_profile_progress_bar)).setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        firstName = ((TextInputEditText) findViewById(R.id.edit_profile_first_name_edit_text)).getText().toString();
        lastName = ((TextInputEditText) findViewById(R.id.edit_profile_last_name_edit_text)).getText().toString();
        username = ((TextInputEditText) findViewById(R.id.edit_profile_username_edit_text)).getText().toString();

        if (!arePasswordsValid()) {
            ((ProgressBar) findViewById(R.id.edit_profile_progress_bar)).setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return;
        }

        if (!isUsernameValid) {
            Toast.makeText(this, "Username is not valid", Toast.LENGTH_SHORT).show();
            ((ProgressBar) findViewById(R.id.edit_profile_progress_bar)).setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return;
        }

        phone = ((TextInputEditText) findViewById(R.id.edit_profile_phone_edit_text)).getText().toString();
        email = ((TextInputEditText) findViewById(R.id.edit_profile_email_edit_text)).getText().toString();
        if (!type.equals("resident")) {
            designation = ((TextInputEditText) findViewById(R.id.edit_profile_designation_edit_text)).getText().toString();
        } else {
            designation = null;
        }

        TextInputLayout[] layouts = new TextInputLayout[5];
        layouts[0] = ((TextInputLayout) findViewById(R.id.edit_profile_first_name_til));
        layouts[1] = ((TextInputLayout) findViewById(R.id.edit_profile_last_name_til));
        layouts[2] = ((TextInputLayout) findViewById(R.id.edit_profile_email_til));
        layouts[3] = ((TextInputLayout) findViewById(R.id.edit_profile_phone_til));
        layouts[4] = ((TextInputLayout) findViewById(R.id.edit_profile_username_til));

        for (TextInputLayout layout : layouts) {
            if (layout.getEditText().getText().toString().isEmpty()) {
                layout.setError("This field cannot be empty");
                layout.setErrorEnabled(true);
                layout.requestFocus();
                layout.setErrorIconDrawable(null);
                ((ProgressBar) findViewById(R.id.edit_profile_progress_bar)).setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                return;
            }
        }

        if (!type.equals("resident")) {
            TextInputLayout layout = ((TextInputLayout) findViewById(R.id.edit_profile_designation_til));
            if (layout.getEditText().getText().toString().isEmpty()) {
                layout.setError("This field cannot be empty");
                layout.setErrorEnabled(true);
                layout.requestFocus();
                layout.setErrorIconDrawable(null);
                ((ProgressBar) findViewById(R.id.edit_profile_progress_bar)).setVisibility(View.GONE);
                getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
                return;
            }
        }

        Call<JsonArray> call = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitServerAPI.class)
                .editProfile(
                        org_username,
                        org_password,
                        username,
                        password,
                        phone,
                        email,
                        designation,
                        firstName,
                        lastName
                );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String responseString = response.body().toString();
                try {
                    JSONArray responseArray = new JSONArray(responseString);
                    JSONObject loginInfo = responseArray.getJSONObject(0);
                    if (loginInfo.getInt("login_status") == 1) {
                        if (loginInfo.getInt("request_status") == 1) {
                            updateDatabaseAndFinish();
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

    public void setUsernameChecks() {

        final RetrofitServerAPI api = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(RetrofitServerAPI.class);

        final TextInputLayout usernameTIL = ((TextInputLayout) findViewById(R.id.edit_profile_username_til));
        TextInputEditText usernameEditText = ((TextInputEditText) findViewById(R.id.edit_profile_username_edit_text));
        usernameTIL.setHelperTextColor(colorStateList);
        usernameEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(final CharSequence s, int start, int before, int count) {

                if (s.length() > 0) {
                    usernameTIL.setError(null);
                    usernameTIL.setErrorEnabled(false);
                }

                if (s.toString().equals(org_username) || s.toString().equals("")) {
                    ((TextView) findViewById(R.id.edit_profile_username_helper_text)).setVisibility(View.GONE);
                    return;
                }

                Call<JsonArray> call = api.checkUsernameAvailability(s.toString());

                call.enqueue(new Callback<JsonArray>() {
                    @Override
                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                        try {
                            String responseString = response.body().toString();
                            JSONArray responseArray = new JSONArray(responseString);
                            JSONObject requestInfo = responseArray.getJSONObject(0);
                            if (requestInfo.getBoolean("username_available") && !s.equals("")) {
                                ((TextView) findViewById(R.id.edit_profile_username_helper_text)).setText("'" + s.toString() + "' is available");
                                ((TextView) findViewById(R.id.edit_profile_username_helper_text)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.edit_profile_username_helper_text)).setTextColor(Color.GREEN);
                                isUsernameValid = true;
                            } else if (!s.equals("")) {
                                ((TextView) findViewById(R.id.edit_profile_username_helper_text)).setVisibility(View.VISIBLE);
                                ((TextView) findViewById(R.id.edit_profile_username_helper_text)).setTextColor(Color.RED);
                                ((TextView) findViewById(R.id.edit_profile_username_helper_text)).setText("'" + s.toString() + "' is not available");
                                isUsernameValid = false;
                            } else {
                                ((TextView) findViewById(R.id.edit_profile_username_helper_text)).setVisibility(View.GONE);
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
            public void afterTextChanged(final Editable s) {


            }
        });
    }

    public void setPasswordChecks() {
        final TextInputLayout passwordTIL = ((TextInputLayout) findViewById(R.id.edit_profile_password_til));
        final TextInputEditText passwordEditText = ((TextInputEditText) findViewById(R.id.edit_profile_password_edit_text));
        passwordTIL.setHelperTextColor(colorStateList);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.length() == 0) {
                    passwordTIL.setHelperTextEnabled(false);
                } else if (s.length() < 6) {
                    passwordTIL.setHelperTextEnabled(true);
                    passwordTIL.setHelperText("Must be 6 characters or longer");
                } else {
                    passwordTIL.setHelperTextEnabled(false);
                }
            }
        });

        final TextInputLayout confirmPasswordTIL = ((TextInputLayout) findViewById(R.id.edit_profile_confirm_password_til));
        final TextInputEditText confirmPasswordEditText = ((TextInputEditText) findViewById(R.id.edit_profile_confirm_password_edit_text));
        confirmPasswordTIL.setHelperTextColor(colorStateList);
        confirmPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (s.toString().equals("")) {
                    confirmPasswordTIL.setHelperTextEnabled(false);
                } else if (s.toString().equals(passwordEditText.getText().toString())) {
                    confirmPasswordTIL.setHelperTextEnabled(true);
                    confirmPasswordTIL.setHelperText("Passwords match");
                } else {
                    confirmPasswordTIL.setHelperTextEnabled(true);
                    confirmPasswordTIL.setHelperText("Passwords do not match");
                }
            }
        });
    }

    private Boolean arePasswordsValid() {
        String password, passwordConfirm;

        password = ((TextInputEditText) findViewById(R.id.edit_profile_password_edit_text)).getText().toString();
        passwordConfirm = ((TextInputEditText) findViewById(R.id.edit_profile_confirm_password_edit_text)).getText().toString();

        if (password.equals("") && passwordConfirm.equals("")) {
            return true;
        }

        if (password.length() < 6) {
            Toast.makeText(this, "Check password length", Toast.LENGTH_SHORT).show();
            return false;
        }

        if (!password.equals(passwordConfirm)) {
            Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show();
            return false;
        }

        this.password = password;
        return true;
    }

    private void updateDatabaseAndFinish() {
        ((ProgressBar) findViewById(R.id.edit_profile_progress_bar)).setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        ContentValues contentValues = new ContentValues();
        contentValues.put(DBManager.ColUsername, username);
        contentValues.put(DBManager.ColPassword, password);
        contentValues.put(DBManager.ColFirstName, firstName);
        contentValues.put(DBManager.ColLastName, lastName);
        contentValues.put(DBManager.ColPhone, phone);
        contentValues.put(DBManager.ColEmail, email);
        contentValues.put(DBManager.ColProfileUpdated, 1);
        contentValues.put(DBManager.ColDesignation, designation);
        contentValues.put(DBManager.ColTownship, cursor.getString(cursor.getColumnIndexOrThrow(DBManager.ColTownship)));
        contentValues.put(DBManager.ColTownshipId, cursor.getString(cursor.getColumnIndexOrThrow(DBManager.ColTownshipId)));
        contentValues.put(DBManager.ColWing, cursor.getString(cursor.getColumnIndexOrThrow(DBManager.ColWing)));
        contentValues.put(DBManager.ColApartment, cursor.getString(cursor.getColumnIndexOrThrow(DBManager.ColApartment)));
        contentValues.put(DBManager.ColType, cursor.getString(cursor.getColumnIndexOrThrow(DBManager.ColType)));

        dbManager.deleteAll();
        dbManager.Insert(contentValues);

        setResult(PROFILE_EDITED);
        finish();
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

                CropImage.activity(uri)
                        .setAllowFlipping(false)
                        .setAllowRotation(false)
                        .setAspectRatio(1, 1)
                        .start(this);
            }

        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                Uri uri = result.getUri();

                try {
                    photo = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ((ImageView) findViewById(R.id.edit_profile_photo)).setImageBitmap(photo);
                ((ProgressBar) findViewById(R.id.edit_profile_upload_photo_progress_bar)).setVisibility(View.VISIBLE);

                new Thread() {
                    public void run() {
                        file = getOutputMediaFile();
                        try {
                            FileOutputStream fileOutputStream = new FileOutputStream(file);
                            photo.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                            fileOutputStream.close();
                            profilePicUploading = true;
                            new UploadToS3().execute();
                        } catch (FileNotFoundException e) {
                            profilePicUploading = false;
                            e.printStackTrace();
                        } catch (IOException e) {
                            profilePicUploading = false;
                            e.printStackTrace();
                        }
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
                FileInputStream stream = new FileInputStream(file);
                ObjectMetadata metadata = new ObjectMetadata();
                metadata.setContentLength(file.length());
                s3Client.putObject("township-manager", "townships/" + township_id + "/user_profile_pics/" + user_id + ".png", stream, metadata);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (AmazonClientException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            ((ProgressBar) findViewById(R.id.edit_profile_upload_photo_progress_bar)).setVisibility(View.GONE);
            profilePicUploading = false;
            super.onPostExecute(aVoid);
        }
    }

}

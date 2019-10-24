package com.township.manager;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.JsonArray;
import com.pusher.pushnotifications.PushNotifications;
import com.squareup.moshi.JsonAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import static com.township.manager.ProfileActivity.PROFILE_EDITED;
import static com.township.manager.ProfileActivity.PROFILE_NOT_EDITED;

public class EditProfileActivity extends AppCompatActivity {

    Cursor cursor;
    String org_username, org_password;
    String username, password, firstName, lastName, designation, phone, email, type;
    int profileUpdated;
    DBManager dbManager;

    ColorStateList colorStateList;
    boolean isUsernameValid = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Toolbar toolbar = (Toolbar) findViewById(R.id.edit_profile_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit profile");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        dbManager = new DBManager(getApplicationContext());
        cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
        org_username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        org_password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));
        firstName = cursor.getString(cursor.getColumnIndexOrThrow("First_Name"));
        lastName = cursor.getString(cursor.getColumnIndexOrThrow("Last_Name"));
        email = cursor.getString(cursor.getColumnIndexOrThrow("Email"));
        phone = cursor.getString(cursor.getColumnIndexOrThrow("Phone"));
        type = cursor.getString(cursor.getColumnIndexOrThrow("Type"));
        profileUpdated = cursor.getInt(cursor.getColumnIndexOrThrow("Profile_Updated"));

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

        ((TextInputEditText) findViewById(R.id.edit_profile_first_name_edit_text)).setText(firstName);
        ((TextInputEditText) findViewById(R.id.edit_profile_last_name_edit_text)).setText(lastName);
        ((TextInputEditText) findViewById(R.id.edit_profile_username_edit_text)).setText(username);
        ((TextInputEditText) findViewById(R.id.edit_profile_phone_edit_text)).setText(phone);
        ((TextInputEditText) findViewById(R.id.edit_profile_email_edit_text)).setText(email);
        if (!type.equals("resident")) {
            ((TextInputLayout) findViewById(R.id.edit_profile_designation_til)).setVisibility(View.VISIBLE);
            ((TextInputEditText) findViewById(R.id.edit_profile_designation_edit_text)).setText(designation);
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

        TextInputLayout[] layouts = new TextInputLayout[6];
        layouts[0] = ((TextInputLayout) findViewById(R.id.edit_profile_first_name_til));
        layouts[1] = ((TextInputLayout) findViewById(R.id.edit_profile_last_name_til));
        layouts[2] = ((TextInputLayout) findViewById(R.id.edit_profile_designation_til));
        layouts[3] = ((TextInputLayout) findViewById(R.id.edit_profile_email_til));
        layouts[4] = ((TextInputLayout) findViewById(R.id.edit_profile_phone_til));
        layouts[5] = ((TextInputLayout) findViewById(R.id.edit_profile_username_til));

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
                    usernameTIL.setEnabled(false);
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

}

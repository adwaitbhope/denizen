package com.township.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ProgressBar;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Objects;

import static com.township.manager.MaintenanceFragment.ADD_MAINTENANCE_RESULT;

public class AddMaintenanceActivity extends AppCompatActivity {

    AutoCompleteTextView wingACTV, apartmentACTV, paymentModeACTV;
    TextInputLayout amountTIL, wingTIL, apartmentTIL, paymentModeTIL, chequeNoTIL;
    TextInputEditText amountEditText, chequeNoEditText;

    AppDatabase appDatabase;
    MaintenanceDao maintenanceDao;

    Maintenance maintenance;

    String username, password, wingId, apartment, mode, amount, cheque_no = null;

    ArrayAdapter<String> apartmentsAdapter, wingsAdapter;

    String[] wings, wingIds;
    String[] apartments;

    ArrayList<Wing> wingsList;
    ArrayList<Resident> residentsList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_maintenance);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_maintenance_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add maintenance");
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        amountTIL = findViewById(R.id.add_maintenance_amount_til);
        paymentModeTIL = findViewById(R.id.add_maintenance_payment_mode_til);
        wingTIL = findViewById(R.id.add_maintenance_wing_til);
        apartmentTIL = findViewById(R.id.add_maintenance_apartment_til);
        chequeNoTIL = findViewById(R.id.add_maintenance_cheque_no_til);

        wingACTV = findViewById(R.id.add_maintenance_wing_dropdown);
        apartmentACTV = findViewById(R.id.add_maintenance_apartment_dropdown);
        paymentModeACTV = findViewById(R.id.add_maintenance_payment_mode_dropdown);
        amountEditText = findViewById(R.id.add_maintenance_amount_edittext);
        chequeNoEditText = findViewById(R.id.add_maintenance_cheque_no_edittext);

        handleError(amountTIL);
        handleError(chequeNoTIL);

        MaterialButton addMaintenanceButton = findViewById(R.id.add_maintenance_confirm_button);

        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        int usernameCol, passwordCol;

        usernameCol = cursor.getColumnIndexOrThrow("Username");
        passwordCol = cursor.getColumnIndexOrThrow("Password");
        cursor.moveToFirst();

        username = cursor.getString(usernameCol);
        password = cursor.getString(passwordCol);

        apartmentsAdapter = new ArrayAdapter<>(
                this,
                R.layout.dropdown_menu_popup_item,
                new String[]{"No apartments"});
        apartmentACTV.setClickable(false);
        apartmentACTV.setAdapter(apartmentsAdapter);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        new GetWingsFromDatabase().execute();

        String[] paymentModes = new String[] {"Cash", "Cheque"};
        ArrayAdapter<String> paymentModeAdapter = new ArrayAdapter<>(
                AddMaintenanceActivity.this,
                R.layout.dropdown_menu_popup_item,
                paymentModes);
        paymentModeACTV.setAdapter(paymentModeAdapter);
        paymentModeACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                paymentModeTIL.setErrorEnabled(false);
                mode = String.valueOf(position + 1);
                if (position == 1) {
                    chequeNoTIL.setVisibility(View.VISIBLE);
                } else {
                    chequeNoTIL.setVisibility(View.GONE);
                }
            }
        });

        addMaintenanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMaintenanceToServer();
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

    private void handleError(final TextInputLayout textInputLayout) {
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

    private void addMaintenanceToServer() {

        try {
            InputMethodManager manager = (InputMethodManager) this.getSystemService(INPUT_METHOD_SERVICE);
            manager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
            e.printStackTrace();
        }

        ((ProgressBar) findViewById(R.id.add_maintenance_progress_bar)).setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);

        if (!getInputsAndValidate()) {
            ((ProgressBar) findViewById(R.id.add_maintenance_progress_bar)).setVisibility(View.GONE);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
            return;
        }

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);
        Call<JsonArray> call = retrofitServerAPI.addMaintenance(
                username,
                password,
                wingId,
                apartment,
                amount,
                mode,
                cheque_no
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                assert response.body() != null;
                String responseString = response.body().toString();
                try {
                    JSONArray responseArray = new JSONArray(responseString);
                    JSONObject loginJson = responseArray.getJSONObject(0);
                    if (loginJson.getString("login_status").equals("1")) {
                        if (loginJson.getString("request_status").equals("1")) {
                            Gson gson = new Gson();
                            maintenance = gson.fromJson(responseArray.getJSONObject(1).toString(), Maintenance.class);
                            new AddMaintenanceAsyncTask().execute();
                        }
                    }
                } catch (JSONException e) {
                    Log.d("maintenance", e.getMessage());
                }

            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });
    }

    private boolean getInputsAndValidate() {
        if (wingId == null) {
            wingTIL.setErrorEnabled(true);
            wingTIL.setError("Required");
            wingTIL.setErrorIconDrawable(null);
            return false;
        }

        if (apartment == null) {
            apartmentTIL.setErrorEnabled(true);
            apartmentTIL.setError("This field is required");
            apartmentTIL.setErrorIconDrawable(null);
            return false;
        }

        amount = amountEditText.getText().toString();
        if (amount.equals("")) {
            amountTIL.setErrorEnabled(true);
            amountTIL.setError("This field is required");
            amountTIL.setErrorIconDrawable(null);
            return false;
        }

        if (mode == null) {
            paymentModeTIL.setErrorEnabled(true);
            paymentModeTIL.setError("This field is required");
            paymentModeTIL.setErrorIconDrawable(null);
            return false;
        }

        if (mode.equals("2")) {
            cheque_no = chequeNoEditText.getText().toString();
            if (!(cheque_no.length() == 6)) {
                chequeNoTIL.setErrorEnabled(true);
                chequeNoTIL.setError("Invalid cheque no.");
                chequeNoTIL.setErrorIconDrawable(null);
                return false;
            }
        }

        return true;
    }

    private class AddMaintenanceAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            maintenanceDao = appDatabase.maintenanceDao();
            maintenanceDao.insert(maintenance);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setResult(ADD_MAINTENANCE_RESULT);
            finish();
            super.onPostExecute(aVoid);
        }
    }

    private class GetWingsFromDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            wingsList = (ArrayList<Wing>) appDatabase.wingDao().getAll();
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            wings = new String[wingsList.size()];
            wingIds = new String[wingsList.size()];
            for (int i = 0; i < wingsList.size(); i++) {
                wings[i] = wingsList.get(i).getName();
                wingIds[i] = wingsList.get(i).getWing_id();
            }

            wingsAdapter = new ArrayAdapter<>(
                    AddMaintenanceActivity.this,
                    R.layout.dropdown_menu_popup_item,
                    wings);
            wingACTV.setAdapter(wingsAdapter);
            wingACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    wingTIL.setErrorEnabled(false);
                    wingId = wingIds[position];
                    new AddMaintenanceActivity.GetApartmentsFromDatabase().execute();
                }
            });

            super.onPostExecute(aVoid);
        }
    }

    private class GetApartmentsFromDatabase extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            residentsList = (ArrayList<Resident>) appDatabase.residentDao().getAllFromWing(wingId);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            apartments = new String[residentsList.size()];
            for (int i = 0; i < residentsList.size(); i++) {
                apartments[i] = residentsList.get(i).getApartment();
            }
            apartmentsAdapter = new ArrayAdapter<>(
                    AddMaintenanceActivity.this,
                    R.layout.dropdown_menu_popup_item,
                    apartments);
            apartmentACTV.setAdapter(apartmentsAdapter);
            apartmentACTV.setClickable(true);
            apartmentACTV.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    apartmentTIL.setErrorEnabled(false);
                    apartment = apartments[position];
                }
            });
            super.onPostExecute(aVoid);
        }
    }
}

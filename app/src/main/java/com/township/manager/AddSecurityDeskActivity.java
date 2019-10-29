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
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.township.manager.SecurityDesksListFragment.ADD_SECURITY_DESKS_RESULT;

public class AddSecurityDeskActivity extends AppCompatActivity {

    TextInputLayout securityDeskNameTIL,securityDeskPhoneTIL;
    EditText securityDeskName,securityDeskPhone;
    MaterialButton cancelButton,saveButton;
    String username, password;
    SecurityDesks securityDesks;
    SecurityDesksDao securityDesksDao;
    AppDatabase appDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        securityDeskNameTIL=findViewById(R.id.add_security_desk_name_name_til);
        securityDeskName=findViewById(R.id.add_security_desk_name_edit_text);
        securityDeskPhoneTIL=findViewById(R.id.add_security_desk_phone_number_til);
        securityDeskPhone=findViewById(R.id.add_security_desk_phone_number_edit_text);
        cancelButton=findViewById(R.id.add_security_desk_cancel_button);
        saveButton=findViewById(R.id.add_security_desk_save_button);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();
        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();
        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));

        setContentView(R.layout.activity_add_security_desk);
        Toolbar toolbar = (Toolbar) findViewById(R.id.add_security_desk_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Add Desk");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        addSecurityDesksToServer();
    }

    private void addSecurityDesksToServer() {
        String name,phone;
        name=securityDeskName.getText().toString();
        phone=securityDeskPhone.getText().toString();

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call=retrofitServerAPI.addNewSecurityDesks(
                username,
                password,
                name,
                phone
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
                            securityDesks=gson.fromJson(responseArray.getJSONObject(1).toString(), SecurityDesks.class);
                            new AddSecurityDeskAsyncTask().execute();
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
    private class AddSecurityDeskAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            securityDesksDao = appDatabase.securityDesksDao();
            securityDesksDao.insert(securityDesks);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setResult(ADD_SECURITY_DESKS_RESULT);
            super.onPostExecute(aVoid);
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
}

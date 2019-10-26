package com.township.manager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.township.manager.MaintenanceFragment.ADD_MAINTENANCE_RESULT;

public class AddMaintenanceActivity extends AppCompatActivity {

    AutoCompleteTextView wing,modeOfPayment;
    TextInputLayout dateInputLayout,amountInputLayout,flatInputLayout;
    TextInputEditText dateInputEditText,amountInputEditText,flatInputEditText;
    WingDao wingDao;
    AppDatabase appDatabase;
    String username, password;
    ArrayList<String> wingNameArray=new ArrayList<>();
    MaterialButton addMaintenanceButton;
    String wingId;
    MaintenanceDao maintenanceDao;
    Maintenance maintenance;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_maintenance);
        dateInputEditText=findViewById(R.id.paid_maintenance_on_child);
        amountInputEditText=findViewById(R.id.maintenance_amount_child);
        dateInputLayout=findViewById(R.id.paid_maintenance_on);
        amountInputLayout=findViewById(R.id.maintenance_amount);
        wing=findViewById(R.id.wing_text_input_exposed_dropdown);
        flatInputEditText=findViewById(R.id.flat_number_textinput_child);
        flatInputLayout=findViewById(R.id.flat_number_textinput);
        modeOfPayment=findViewById(R.id.mode_of_payment_text_input_filled_exposed_dropdown);
        addMaintenanceButton=findViewById(R.id.add_maintenance_button);


        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        int  usernameCol, passwordCol;

        usernameCol = cursor.getColumnIndexOrThrow("Username");
        passwordCol = cursor.getColumnIndexOrThrow("Password");
        cursor.moveToFirst();

        username = cursor.getString(usernameCol);
        password = cursor.getString(passwordCol);

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();



        new Thread(){
            @Override
            public void run() {
                wingDao=appDatabase.wingDao();
                wingNameArray.addAll(wingDao.getALLWingName());
            }
        }.start();

        ArrayAdapter<String> wingAdapter=new ArrayAdapter<>(getApplicationContext(),R.layout.dropdown_menu_popup_item,wingNameArray);
        wing.setAdapter(wingAdapter);

        String[] ModeOfPayment = new String[]{"Cheque", "Cash","Online"};

        ArrayAdapter<String> modeOfPaymentAdaper =
                new ArrayAdapter<>(
                        getApplicationContext(),
                        R.layout.dropdown_menu_popup_item,
                        ModeOfPayment);
        modeOfPayment.setAdapter(modeOfPaymentAdaper);

        addMaintenanceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addMaintenanceToServer();
            }
        });


    }

    private void addMaintenanceToServer() {

//        new Thread(){
//            @Override
//            public void run() {
//
//                Retrofit.Builder builder = new Retrofit.Builder()
//                        .baseUrl(getString(R.string.server_addr))
//                        .addConverterFactory(GsonConverterFactory.create());
//                Retrofit retrofit = builder.build();
//                wingId=wingDao.getWingId(wing.getText().toString());
//                RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);
//                Log.d("wingId",wingId);
//                Call<JsonArray> call = retrofitServerAPI.addMaintenance(
//                        username,
//                        password,
//                        wingId,
//                        flatInputEditText.getText().toString(),
//                        amountInputEditText.getText().toString(),
//                        modeOfPayment.getText().toString(),
//                        null
//                );
//
//                call.enqueue(new Callback<JsonArray>() {
//                    @Override
//                    public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
//                        assert response.body() != null;
//                        String responseString=response.body().toString();
//                        try{
//                            JSONArray responseArray=new JSONArray(responseString);
//                            JSONObject loginJson=responseArray.getJSONObject(0);
//                            if (loginJson.getString("login_status").equals("1")) {
//                                if (loginJson.getString("request_status").equals("1")) {
//                                    Gson gson = new Gson();
//                                    maintenance=gson.fromJson(responseArray.getJSONObject(1).toString(),Maintenance.class);
//                                    new AddMaintenanceAsyncTask().execute();
//                                }
//                            }
//                        }
//                        catch (JSONException e){
//                            Log.d("eraddmen",e.getMessage());
//                        }
//
//                    }
//
//                    @Override
//                    public void onFailure(Call<JsonArray> call, Throwable t) {
//
//                    }
//                });
//            }
//        }.start();

        //Log.d("wingID",wingId);
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
            super.onPostExecute(aVoid);
        }
    }
}

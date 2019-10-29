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
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import static com.township.manager.AdminInfoActivity.ADD_ADMIN_RESULT;

public class AddAdminActivity extends AppCompatActivity {

    AppDatabase appDatabase;
    EditText noOfAdmins;
    Button createAdminButton;
    String username,password;
    ArrayList<AdminInfo> adminInfos=new ArrayList<>();
    AdminInfo[] adminInfosArray;
    AdminInfoDao adminInfoDao;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_admin);
        noOfAdmins=findViewById(R.id.add_admin_no_of_admins_edit_text);
        createAdminButton=findViewById(R.id.add_admin_create_admins_button);

        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));

        createAdminButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addAdminsToServer();
            }
        });
    }

    private void addAdminsToServer() {
        String noofadmins;
        noofadmins=noOfAdmins.getText().toString();
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call=retrofitServerAPI.addNewAdmins(
                username,
                password,
                noofadmins
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

                            AdminInfo adminInfo;
                            Gson gson=new Gson();
                            JSONArray jsonAdminInfoArray=responseArray.getJSONArray(1);
                            JSONObject jsonObjectAdminInfo;

                            for(int i=0;i<jsonAdminInfoArray.length();i++){
                                jsonObjectAdminInfo=jsonAdminInfoArray.getJSONObject(i);
                                adminInfo=gson.fromJson(jsonObjectAdminInfo.toString(),AdminInfo.class);
                                adminInfos.add(adminInfo);
                            }
                            convertToArray();

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

    private void convertToArray() {
        new Thread(){
            @Override
            public void run() {
                super.run();
                adminInfosArray=new AdminInfo[adminInfos.size()];
                adminInfos.toArray(adminInfosArray);
                new AdminInfoAsynTask().execute();

            }
        }.start();
    }

    private class AdminInfoAsynTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {

            adminInfoDao=appDatabase.adminInfoDao();
            adminInfoDao.insert(adminInfosArray);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            setResult(ADD_ADMIN_RESULT);
//            finish();
            super.onPostExecute(aVoid);
        }
    }
}

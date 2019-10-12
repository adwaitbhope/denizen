package com.township.manager;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipDrawable;
import com.google.android.material.chip.ChipGroup;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddNoticeAdminActivity extends AppCompatActivity {

    String username, password;
    String title, description;

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

    Context context;

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
            finish();
            super.onPostExecute(aVoid);
        }
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

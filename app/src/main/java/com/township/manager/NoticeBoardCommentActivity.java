package com.township.manager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NoticeBoardCommentActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    CommentsAdapter adapter;
    LinearLayoutManager layoutManager;

    String notice_id, townshipId;
    ArrayList<Notice.Comment> dataset = new ArrayList<>();
    ArrayList<Notice.Comment> tempDataset;

    AppDatabase appDatabase;
    NoticeDao noticeDao;
    CommentDao commentDao;

    String username, password, firstName, lastName, wing, apartment, designation;
    Notice.Comment comment;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_board_comment);

        Toolbar toolbar = (Toolbar) findViewById(R.id.notice_board_comment_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        new Thread() {
            public void run() {
                getUserDataFromDatabase();
            }
        }.start();

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        townshipId = cursor.getString(cursor.getColumnIndexOrThrow("TownshipId"));

        Intent intent = getIntent();
        notice_id = intent.getStringExtra("notice_id");

        recyclerView = findViewById(R.id.notice_board_comments_recycler_view);
        adapter = new CommentsAdapter(dataset, this, townshipId);
        layoutManager = new LinearLayoutManager(this);
        layoutManager.setStackFromEnd(true);
        layoutManager.setReverseLayout(false);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.scrollToPosition(adapter.getItemCount() -1);

        recyclerView.addOnLayoutChangeListener(new View.OnLayoutChangeListener() {
            @Override
            public void onLayoutChange(View v, int left, int top, int right, int bottom, int oldLeft, int oldTop, int oldRight, int oldBottom) {
                if (bottom < oldBottom) {
                    recyclerView.smoothScrollToPosition(adapter.getItemCount());
                }
            }
        });

        final TextInputEditText addComment = findViewById(R.id.add_comment_edittext);
        addComment.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                final int DRAWABLE_LEFT = 0;
                final int DRAWABLE_TOP = 1;
                final int DRAWABLE_RIGHT = 2;
                final int DRAWABLE_BOTTOM = 3;

                if(event.getAction() == MotionEvent.ACTION_UP) {
                    if(event.getRawX() >= (addComment.getRight() - addComment.getCompoundDrawables()[DRAWABLE_RIGHT].getBounds().width())) {

                        try {
                            String comment = addComment.getText().toString();
                            if (!comment.equals("")) {
                                addComment.setText("");
                                sendCommentToServer(comment);
                            }
                        } catch (NullPointerException e) {
                            e.printStackTrace();
                        }
                        return true;
                    }
                }
                return false;
            }
        });

    }

    private void getUserDataFromDatabase() {
        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        int firstNameCol, lastNameCol, usernameCol, passwordCol, wingCol, apartmentCol, designationCol;
        firstNameCol = cursor.getColumnIndexOrThrow("First_Name");
        lastNameCol = cursor.getColumnIndexOrThrow("Last_Name");
        usernameCol = cursor.getColumnIndexOrThrow("Username");
        passwordCol = cursor.getColumnIndexOrThrow("Password");
        wingCol = cursor.getColumnIndexOrThrow("Wing");
        apartmentCol = cursor.getColumnIndexOrThrow("Apartment");
        designationCol = cursor.getColumnIndexOrThrow("Designation");

        username = cursor.getString(usernameCol);
        password = cursor.getString(passwordCol);
        firstName = cursor.getString(firstNameCol);
        lastName = cursor.getString(lastNameCol);
        wing = cursor.getString(wingCol);
        apartment = cursor.getString(apartmentCol);
        designation = cursor.getString(designationCol);
    }

    @Override
    protected void onResume() {
        super.onResume();
        new GetCommentsAsyncTask().execute();
    }

    private void sendCommentToServer(final String content) {
        comment = new Notice.Comment();
        comment.setContent(content);
        comment.setNotice_id(notice_id);
        comment.setPosted_by_first_name(firstName);
        comment.setPosted_by_last_name(lastName);
        comment.setPosted_by_user_id(username);
        comment.setPosted_by_wing(wing);
        comment.setPosted_by_apartment(apartment);
        comment.setPosted_by_designation(designation);
        comment.setTimestamp("now");

        dataset.add(comment);
        adapter.notifyItemInserted(dataset.size() -1);
        recyclerView.scrollToPosition(adapter.getItemCount() -1);
//        adapter.showSending((CommentsAdapter.ViewHolder) recyclerView.findViewHolderForLayoutPosition(-1));

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.addCommentToNotice(
                username,
                password,
                notice_id,
                content
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                String responseString = response.body().toString();
                try {
                    JSONArray responseArray = new JSONArray(responseString);
                    JSONObject responseJson = responseArray.getJSONObject(0);

                    if (responseJson.getInt("login_status") == 1) {
                        if (responseJson.getInt("request_status") == 1) {
                            Gson gson = new Gson();
                            comment = gson.fromJson(responseArray.getJSONObject(1).toString(), Notice.Comment.class);

                            dataset.set(dataset.size() -1, comment);
                            adapter.notifyItemChanged(dataset.size() -1);
//                            adapter.showSent(adapter.new ViewHolder(layoutManager.findViewByPosition(adapter.getItemCount() -1)));
                            recyclerView.scrollToPosition(adapter.getItemCount() -1);

                            new Thread() {
                                public void run() {
                                    commentDao = appDatabase.commentDao();
                                    commentDao.insert(comment);
                                }
                            }.start();
                        }
                    }
                    else {
                        Toast.makeText(NoticeBoardCommentActivity.this, "kahitari gandla", Toast.LENGTH_SHORT).show();
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

    private class GetCommentsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            noticeDao = appDatabase.noticeDao();
            dataset.clear();
            dataset.addAll(noticeDao.getComments(notice_id));
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (adapter != null) {
                adapter.notifyDataSetChanged();
                recyclerView.scrollToPosition(adapter.getItemCount() -1);
            }
        }
    }

}

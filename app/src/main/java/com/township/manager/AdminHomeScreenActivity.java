package com.township.manager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminHomeScreenActivity extends AppCompatActivity
        implements NoticeBoardFragment.OnFragmentInteractionListener, ComplaintsListFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener, ComplaintsFragment.OnFragmentInteractionListener {

    String username, password;
    NoticeBoardFragment noticeBoardFragment;
    ComplaintsFragment complaintsFragment;

    AppDatabase appDatabase;

    NoticeDao noticeDao;
    NoticeWingDao noticeWingDao;
    CommentDao commentDao;

    NoticeWing[] noticeWingArray;
    Notice[] noticesArray;
    Notice.Comment[] commentsArray;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        DBManager dbManager = new DBManager(getApplicationContext());
        Cursor cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        setContentView(R.layout.activity_admin_home_screen);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        TextView adminName, adminDesignation;

        adminDesignation = header.findViewById(R.id.navheader_admin_home_screen_designation_textview);
        adminName = header.findViewById(R.id.navheader_admin_home_screen_name_textview);

        int firstNameCol, desCol, lastNameCol, usernameCol, passwordCol;
        firstNameCol = cursor.getColumnIndexOrThrow("First_Name");
        lastNameCol = cursor.getColumnIndexOrThrow("Last_Name");
        desCol = cursor.getColumnIndexOrThrow("Designation");
        usernameCol = cursor.getColumnIndexOrThrow("Username");
        passwordCol = cursor.getColumnIndexOrThrow("Password");
        cursor.moveToFirst();

        username = cursor.getString(usernameCol);
        password = cursor.getString(passwordCol);

        adminDesignation.setText(cursor.getString(desCol));
        adminName.setText(cursor.getString(firstNameCol) + " " + cursor.getString(lastNameCol));


        noticeBoardFragment = new NoticeBoardFragment();
//        complaintsFragment = new ComplaintsFragment();

        getNoticesFromServer();

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.admin_home_screen_fragment_area, noticeBoardFragment);
        transaction.commit();

        BottomNavigationView bottomNavigationView = findViewById(R.id.admin_bottom_nav);
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction transaction;
                Fragment fragment;

                switch (menuItem.getItemId()) {

                    case R.id.admin_notice_board:
                        fragment = getSupportFragmentManager().findFragmentById(R.id.admin_home_screen_fragment_area);
                        if (!(fragment instanceof NoticeBoardFragment)) {
                            appBarLayout.setElevation(4);
                            transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.admin_home_screen_fragment_area, noticeBoardFragment);
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            transaction.commit();
                        }
                        return true;

                    case R.id.admin_complaints:
                        fragment = getSupportFragmentManager().findFragmentById(R.id.admin_home_screen_fragment_area);
                        if (!(fragment instanceof ComplaintsFragment)) {
                            appBarLayout.setElevation(0);
                            transaction = getSupportFragmentManager().beginTransaction();
                            complaintsFragment = new ComplaintsFragment();
                            transaction.replace(R.id.admin_home_screen_fragment_area, complaintsFragment);
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            transaction.commit();
                        }
                        return true;

                    case R.id.admin_group_chat:
//                        appBarLayout.setElevation(4);
                        return true;

                    case R.id.admin_finances:
//                        appBarLayout.setElevation(4);
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_intercom_admin) {

        } else if (id == R.id.nav_maintenance_admin) {

        } else if (id == R.id.nav_visitor_history_admin) {

        } else if (id == R.id.nav_admin_info_admin) {

        } else if (id == R.id.nav_security_list_admin) {
            Intent intent = new Intent(AdminHomeScreenActivity.this, SecurityActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_vendors_admin) {
            Intent intent=new Intent(AdminHomeScreenActivity.this,ServiceVendorActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_wing_details_admin) {

        } else if (id == R.id.nav_amenities_admin) {
            Intent intent = new
                    Intent(AdminHomeScreenActivity.this, AmenitiesAdminContainerActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout_admin) {
            LogOutDialog logOutDialog = new LogOutDialog();
            logOutDialog.show(getSupportFragmentManager(), "Logout");
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    public void getNoticesFromServer() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.getNotices(
                username,
                password,
                null
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                assert response.body() != null;
                Log.d("response", response.body().toString());
                String responseString = response.body().getAsJsonArray().toString();
                try {
                    JSONArray jsonArray = new JSONArray(responseString);
                    JSONObject loginResponse = jsonArray.getJSONObject(0);

                    if (loginResponse.getInt("login_status") == 1) {
                        JSONArray jsonNotices = jsonArray.getJSONArray(1);

                        JSONObject jsonNotice;
                        ArrayList<Notice> notices = new ArrayList<>();
                        Notice notice;
                        Notice.Comment comment;
                        Wing wing;
                        Gson gson = new Gson();

                        for (int i = 0; i < jsonNotices.length(); i++) {
                            jsonNotice = jsonNotices.getJSONObject(i);
                            notice = gson.fromJson(jsonNotice.toString(), Notice.class);

                            ArrayList<Wing> wings = new ArrayList<>();
                            JSONArray jsonWings = jsonNotice.getJSONArray("wings");
                            for (int j = 0; j < jsonWings.length(); j++) {
                                wing = gson.fromJson(jsonWings.getJSONObject(j).toString(), Wing.class);
                                wings.add(wing);
                            }
                            notice.setWings(wings);

                            ArrayList<Notice.Comment> comments = new ArrayList<>();
                            JSONArray jsonComments = jsonNotice.getJSONArray("comments");

                            for (int j = 0; j < jsonComments.length(); j++) {
                                comment = gson.fromJson(jsonComments.getString(j), Notice.Comment.class);
                                comment.setNotice_id(notice.getNotice_id());
                                comments.add(comment);
                            }
                            notice.setComments(comments);

                            notices.add(notice);
                        }

                        addNoticesToDatabase(notices);

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

    public void addNoticesToDatabase(final ArrayList<Notice> notices) {

        new Thread() {
            public void run() {
                noticeDao = appDatabase.noticeDao();
                noticeWingDao = appDatabase.noticeWingsDao();
                commentDao = appDatabase.commentDao();

                noticesArray = new Notice[notices.size()];

                int noticeWingsLength = 0, commentsLength = 0;
                for (Notice notice : notices) {
                    noticeWingsLength += notice.getWings().size();
                    commentsLength += notice.getComments().size();
                }

                noticeWingArray = new NoticeWing[noticeWingsLength];
                commentsArray = new Notice.Comment[commentsLength];

                ArrayList<NoticeWing> noticeWings = new ArrayList<>();
                ArrayList<Notice.Comment> comments = new ArrayList<>();
                NoticeWing noticeWing;

                for (Notice notice : notices) {
                    for (Wing wing : notice.getWings()) {
                        noticeWing = new NoticeWing();
                        noticeWing.setWing_id(wing.getWing_id());
                        noticeWing.setNotice_id(notice.getNotice_id());
                        noticeWings.add(noticeWing);
                    }
                    comments.addAll(notice.getComments());
                }

                notices.toArray(noticesArray);
                noticeWings.toArray(noticeWingArray);
                comments.toArray(commentsArray);

                NoticesAsyncTask asyncTask = new NoticesAsyncTask();
                asyncTask.execute();
            }
        }.start();

    }

    public void getComplaintsFromServer() {

    }

    private class NoticesAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.commentDao().deleteAll();
            appDatabase.noticeWingsDao().deleteAll();
            appDatabase.noticeDao().deleteAll();
            noticeDao.insert(noticesArray);
            noticeWingDao.insert(noticeWingArray);
            commentDao.insert(commentsArray);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            noticeBoardFragment.updateRecyclerView();
            super.onPostExecute(aVoid);
        }
    }

}

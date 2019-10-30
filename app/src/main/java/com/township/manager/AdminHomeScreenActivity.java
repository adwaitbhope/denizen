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
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.IntegerRes;
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
import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class AdminHomeScreenActivity extends AppCompatActivity
        implements NoticeBoardFragment.OnFragmentInteractionListener, ComplaintsListFragment.OnFragmentInteractionListener, NavigationView.OnNavigationItemSelectedListener, ComplaintsFragment.OnFragmentInteractionListener, FinancesFragment.OnFragmentInteractionListener {

    String username, password;
    NoticeBoardFragment noticeBoardFragment;
    ComplaintsFragment complaintsFragment;
    FinancesFragment financesFragment;

    AppDatabase appDatabase;

    NoticeDao noticeDao;
    NoticeWingDao noticeWingDao;
    CommentDao commentDao;

    NoticeWing[] noticeWingArray;
    Notice[] noticesArray;
    Notice.Comment[] commentsArray;

    ComplaintDao complaintDao;
    Complaint[] complaintsArray;

    DrawerLayout drawerLayout;

    DBManager dbManager;
    Cursor cursor;
    View headerView;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dbManager = new DBManager(getApplicationContext());
        cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        username = cursor.getString(cursor.getColumnIndexOrThrow("Username"));
        password = cursor.getString(cursor.getColumnIndexOrThrow("Password"));

        appDatabase = Room.databaseBuilder(getApplicationContext(),
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();

        setContentView(R.layout.activity_admin_home_screen);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        final AppBarLayout appBarLayout = (AppBarLayout) findViewById(R.id.appBarLayout);
        setSupportActionBar(toolbar);

        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        headerView = navigationView.getHeaderView(0);

        ImageButton editProfile = headerView.findViewById(R.id.admin_home_nav_header_edit_profile_button);
        editProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(AdminHomeScreenActivity.this, ProfileActivity.class);
                startActivity(intent);
            }
        });

        noticeBoardFragment = new NoticeBoardFragment();
        complaintsFragment = new ComplaintsFragment();

        getNoticesFromServer();
        getComplaintsFromServer();

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
                            transaction = getSupportFragmentManager().beginTransaction();
                            transaction.replace(R.id.admin_home_screen_fragment_area, noticeBoardFragment);
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            transaction.commit();
                            appBarLayout.setElevation(4);
                        }
                        return true;

                    case R.id.admin_complaints:
                        fragment = getSupportFragmentManager().findFragmentById(R.id.admin_home_screen_fragment_area);
                        if (!(fragment instanceof ComplaintsFragment)) {
                            appBarLayout.setElevation(0);
                            transaction = getSupportFragmentManager().beginTransaction();
                            complaintsFragment = new ComplaintsFragment();
                            transaction.replace(R.id.admin_home_screen_fragment_area, new ComplaintsFragment());
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            transaction.commit();
                        }
                        return true;

                    case R.id.admin_finances:
                        fragment = getSupportFragmentManager().findFragmentById(R.id.admin_home_screen_fragment_area);
                        if (!(fragment instanceof ComplaintsFragment)) {
                            appBarLayout.setElevation(0);
                            transaction = getSupportFragmentManager().beginTransaction();
                            financesFragment = new FinancesFragment();
                            transaction.replace(R.id.admin_home_screen_fragment_area, new FinancesFragment());
                            transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
                            transaction.commit();
//                        appBarLayout.setElevation(4);
                        }
                        return true;
                }
                return false;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    public void updateUI() {
        cursor = dbManager.getDataLogin();
        cursor.moveToFirst();

        int firstNameCol, desCol, lastNameCol;
        firstNameCol = cursor.getColumnIndexOrThrow("First_Name");
        lastNameCol = cursor.getColumnIndexOrThrow("Last_Name");
        desCol = cursor.getColumnIndexOrThrow("Designation");

        TextView adminName, adminDesignation;

        String townshipId, userId;
        townshipId = cursor.getString(cursor.getColumnIndexOrThrow("TownshipId"));
        userId = cursor.getString(cursor.getColumnIndexOrThrow("User_Id"));

        ImageView profilePic = ((ImageView) headerView.findViewById(R.id.admin_home_nav_header_profile_image));
        final String url = "https://township-manager.s3.ap-south-1.amazonaws.com/townships/" + townshipId + "/user_profile_pics/" + userId + ".png";
        Picasso.get()
                .load(url)
                .noFade()
                .placeholder(R.drawable.ic_man)
                .error(R.drawable.ic_man)
                .networkPolicy(NetworkPolicy.NO_CACHE)
                .memoryPolicy(MemoryPolicy.NO_CACHE, MemoryPolicy.NO_STORE)
                .into(profilePic);

        adminDesignation = headerView.findViewById(R.id.navheader_admin_home_screen_designation_textview);
        adminName = headerView.findViewById(R.id.navheader_admin_home_screen_name_textview);

        adminName.setText(cursor.getString(firstNameCol) + " " + cursor.getString(lastNameCol));
        adminDesignation.setText(cursor.getString(desCol));
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
            Intent intent=new Intent(AdminHomeScreenActivity.this, IntercomActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_maintenance_admin) {
            Intent intent = new Intent(AdminHomeScreenActivity.this, MaintenanceAdminContainerActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_admin_info_admin) {
            Intent intent = new Intent(AdminHomeScreenActivity.this, AdminInfoActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_security_list_admin) {
            Intent intent = new Intent(AdminHomeScreenActivity.this, SecurityActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_vendors_admin) {
            Intent intent = new Intent(AdminHomeScreenActivity.this, ServiceVendorActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_wing_details_admin) {

        } else if (id == R.id.nav_amenities_admin) {
            Intent intent = new Intent(AdminHomeScreenActivity.this, AmenitiesAdminContainerActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_logout_admin) {
            LogOutDialog logOutDialog = new LogOutDialog();
            logOutDialog.show(getSupportFragmentManager(), "Logout");
        }

        drawerLayout.closeDrawers();
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

                NoticesAsyncTask noticesAsyncTask = new NoticesAsyncTask();
                noticesAsyncTask.execute();
            }
        }.start();

    }

    public void getComplaintsFromServer() {

        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();

        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);

        Call<JsonArray> call = retrofitServerAPI.getComplaints(
                username,
                password,
                null,
                null
        );

        call.enqueue(new Callback<JsonArray>() {
            @Override
            public void onResponse(Call<JsonArray> call, Response<JsonArray> response) {
                assert response.body() != null;
                String responseString = response.body().getAsJsonArray().toString();
                Log.d("printres", responseString);
                try {
                    JSONArray jsonArray = new JSONArray(responseString);
                    JSONObject loginResponse = jsonArray.getJSONObject(0);
                    if (loginResponse.getInt("login_status") == 1) {

                        JSONArray jsonArrayComplaint;
                        JSONObject jsonObjectComplaint;
                        ArrayList<Complaint> complaints = new ArrayList<>();
                        Complaint complaint;
                        Gson gson = new Gson();
                        jsonArrayComplaint = jsonArray.getJSONArray(1);
                        for (int i = 0; i < jsonArrayComplaint.length(); i++) {
                            jsonObjectComplaint = jsonArrayComplaint.getJSONObject(i);
                            complaint = gson.fromJson(jsonObjectComplaint.toString(), Complaint.class);
                            complaints.add(complaint);
                        }
                        jsonArrayComplaint = jsonArray.getJSONArray(2);
                        for (int i = 0; i < jsonArrayComplaint.length(); i++) {
                            jsonObjectComplaint = jsonArrayComplaint.getJSONObject(i);
                            complaint = gson.fromJson(jsonObjectComplaint.toString(), Complaint.class);
                            complaints.add(complaint);
                        }
                        addComplaintsToDatabase(complaints);
                    }

                } catch (JSONException jsonexcpetion) {
                    Toast.makeText(AdminHomeScreenActivity.this, jsonexcpetion.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<JsonArray> call, Throwable t) {

            }
        });

    }

    public void addComplaintsToDatabase(final ArrayList<Complaint> complaints) {
        new Thread() {
            public void run() {
                complaintDao = appDatabase.complaintDao();
                complaintsArray = new Complaint[complaints.size()];
                complaints.toArray(complaintsArray);
                ComplaintsAsyncTask complaintsAsyncTask = new ComplaintsAsyncTask();
                complaintsAsyncTask.execute();

            }
        }.start();

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

    private class ComplaintsAsyncTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            appDatabase.complaintDao().deleteAll();
            complaintDao.insert(complaintsArray);
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            if (complaintsFragment.getContext() != null) {
                complaintsFragment.updateRecyclerView();
            }
            super.onPostExecute(aVoid);
        }
    }


}

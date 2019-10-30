package com.township.manager;

import android.animation.LayoutTransition;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ComplaintsAdapter extends RecyclerView.Adapter {

    ArrayList<Complaint> dataset;
    String townshipId;
    Boolean resolved;
    String username, password;
    Context context;
    Boolean isAdmin;

    public ComplaintsAdapter(ArrayList<Complaint> dataset, Context context, Boolean resolved, String townshipId, Boolean isAdmin, String username, String password) {
        this.dataset = dataset;
        this.context = context;
        this.resolved = resolved;
        this.townshipId = townshipId;
        this.isAdmin = isAdmin;
        this.username = username;
        this.password = password;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_complaints, parent, false);

        final MyViewHolder viewHolder = new MyViewHolder(view);

        viewHolder.clickArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Boolean complaintExpanded = viewHolder.complaintExpanded;
                if (complaintExpanded) {
                    viewHolder.expandButton.setImageResource(R.drawable.ic_keyboard_arrow_down_black_24dp);
                    viewHolder.complaintResolveButton.setVisibility(View.GONE);
                    viewHolder.complaintDescriptionTextView.setVisibility(View.GONE);
                    viewHolder.complaintImageButton.setVisibility(View.GONE);
                    viewHolder.complaintExpanded = false;
                } else {
                    viewHolder.expandButton.setImageResource(R.drawable.ic_keyboard_arrow_up_black_24dp);
                    viewHolder.complaintResolveButton.setVisibility(View.VISIBLE);
                    viewHolder.complaintDescriptionTextView.setVisibility(View.VISIBLE);
                    viewHolder.complaintImageButton.setVisibility(View.VISIBLE);
                    viewHolder.complaintExpanded = true;
                }
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, int position) {
        MyViewHolder viewHolder = (MyViewHolder) holder;
        final Complaint complaint = dataset.get(position);
        final String url = "https://township-manager.s3.ap-south-1.amazonaws.com/townships/" + townshipId + "/complaints/" + complaint.getComplaint_id() + ".png";
        Picasso.get()
                .load(url)
                .into(viewHolder.complaintImageButton);
        viewHolder.complaintImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullScreenImageViewActivity.class);
                intent.putExtra("url", url);
                context.startActivity(intent);
            }
        });
        if (!resolved) {
            viewHolder.complaintResolveButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // query to resolve complaint goes here
                    // update local database and change UI accordingly
                    resolveComplaint(complaint);
                }
            });
        } else {
            viewHolder.complaintResolveButton.setText("Resolved");
        }
        viewHolder.complaintTitle.setText(complaint.getTitle());
        viewHolder.residentNameTextView.setText(complaint.getResident_first_name() + " " + complaint.getResident_last_name());
        viewHolder.residentApartmentTextView.setText(complaint.getResident_wing() + "/" + complaint.getResident_apartment());
        viewHolder.complaintDescriptionTextView.setText(complaint.getDescription());

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    private class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView expandButton;
        View clickArea;
        ImageView complaintImageButton;
        Boolean complaintExpanded = false;
        TextView residentNameTextView, residentApartmentTextView, complaintTitle;
        MaterialTextView complaintDescriptionTextView;
        MaterialButton complaintResolveButton;
        ConstraintLayout constraintLayout;

        public MyViewHolder(View view) {
            super(view);
            expandButton = view.findViewById(R.id.complaint_expand_button);
            clickArea = view.findViewById(R.id.complaint_card_click_area);
            complaintImageButton = view.findViewById(R.id.complaint_image_button);
            residentNameTextView = view.findViewById(R.id.complaint_resident_name);
            residentApartmentTextView = view.findViewById(R.id.complaint_apartment);
            complaintTitle = view.findViewById(R.id.complaint_title);
            complaintDescriptionTextView = view.findViewById(R.id.complaint_description_textview);
            complaintResolveButton = view.findViewById(R.id.resolve_complaint_button);
            constraintLayout = view.findViewById(R.id.complaint_card_constraint_layout);

        }

    }

    public void resolveComplaint(final Complaint complaint) {
        Retrofit.Builder builder = new Retrofit.Builder()
                .baseUrl(context.getString(R.string.server_addr))
                .addConverterFactory(GsonConverterFactory.create());
        Retrofit retrofit = builder.build();
        RetrofitServerAPI retrofitServerAPI = retrofit.create(RetrofitServerAPI.class);
        Call<JsonArray> call = retrofitServerAPI.resolveComplaints(
                username,
                password,
                complaint.getComplaint_id()
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
                            Toast.makeText(context, "Complaint resolved", Toast.LENGTH_SHORT).show();
                            int position = dataset.indexOf(complaint);
                            dataset.remove(complaint);
                            notifyItemRemoved(position);
                            new Thread() {
                                public void run() {
                                    AppDatabase appDatabase = Room.databaseBuilder(context.getApplicationContext(),
                                            AppDatabase.class, "app-database")
                                            .fallbackToDestructiveMigration()
                                            .build();
                                    ComplaintDao complaintDao = appDatabase.complaintDao();
                                    complaintDao.markAsResolved(complaint.getComplaint_id());
                                }
                            }.start();
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


}

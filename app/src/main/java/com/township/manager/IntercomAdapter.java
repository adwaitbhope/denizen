package com.township.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;


public class IntercomAdapter extends RecyclerView.Adapter {

    ArrayList<Intercom> dataset;
    Context context;
    WingDao wingDao;
    AppDatabase appDatabase;
    String wing;

    public IntercomAdapter(ArrayList<Intercom> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;

        appDatabase = Room.databaseBuilder(context,
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();
        wingDao = appDatabase.wingDao();
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_intercom, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final Intercom intercom = dataset.get(position);
        final ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.phone.setText(intercom.getPhone());

        if (intercom.getType().equals("security")) {
            viewHolder.name.setText(intercom.getDesignation());
            viewHolder.designation.setVisibility(View.GONE);
        } else if (intercom.getType().equals("admin")) {
            viewHolder.name.setText(intercom.getFirst_name() + " " + intercom.getLast_name());
            viewHolder.designation.setText(intercom.getDesignation());
        } else {
            viewHolder.name.setText(intercom.getFirst_name() + " " + intercom.getLast_name());
            viewHolder.designation.setText(intercom.getWing() + "-" + intercom.getApartment());
        }

        viewHolder.callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("tel:" + intercom.getPhone());
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                context.startActivity(intent);
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialButton callButton;
        TextView name, phone, designation;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            callButton = itemView.findViewById(R.id.recycler_intercom_call_button);
            name = itemView.findViewById(R.id.recycler_intercom_name_text_view);
            phone = itemView.findViewById(R.id.recycler_intercom_phone_text_view);
            designation = itemView.findViewById(R.id.recycler_intercom_designation_text_view);
        }
    }


}

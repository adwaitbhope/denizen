package com.township.manager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MaintenanceAdapter extends RecyclerView.Adapter {

    ArrayList<Maintenance> dataset;
    Context context;

    String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public MaintenanceAdapter(ArrayList<Maintenance> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_maintenance, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        Maintenance maintenance = dataset.get(position);
        viewHolder.name.setText(maintenance.getFirst_name() + " " + maintenance.getLast_name());
        viewHolder.apartment.setText(maintenance.getWing() + "-" + maintenance.getApartment());
        viewHolder.amount.setText("₹ " + maintenance.getAmount() + "/-");
        viewHolder.paymentMode.setText(maintenance.getMode());
        viewHolder.timestamp.setText(getFormattedDate(maintenance.getTimestamp()));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, apartment, amount, paymentMode, timestamp;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recycler_maintenance_name);
            apartment = itemView.findViewById(R.id.recycler_maintenance_apartment);
            amount = itemView.findViewById(R.id.recycler_maintenance_amount);
            paymentMode = itemView.findViewById(R.id.recycler_maintenance_payment_mode);
            timestamp = itemView.findViewById(R.id.recycler_maintenance_date);
        }
    }

    public String getFormattedDate(String timestamp) {
        String day = timestamp.substring(8, 10);
        String digit = day.substring(1);

        if (digit.equals("1")) {
            day =  day + "st";
        } else if (digit.equals("2")) {
             day = day + "nd";
        } else if (digit.equals("3")) {
            day = day + "rd";
        } else {
            day = day + "th";
        }

        String month = months[Integer.valueOf(timestamp.substring(5, 7)) - 1];

        String year = timestamp.substring(2, 4);

        return month + " " + day + ", '" + year;
    }

}

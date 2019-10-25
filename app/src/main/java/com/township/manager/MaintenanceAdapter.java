package com.township.manager;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MaintenanceAdapter extends RecyclerView.Adapter {

    ArrayList<Maintenance> dataset;
    Context context;

    public MaintenanceAdapter(ArrayList<Maintenance> dataset, Context context){
        this.dataset = dataset;
        this.context = context;
        Log.d("inView","holdercoons");

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_maintenance, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        Maintenance maintenance=dataset.get(position);
        viewHolder.flatNo.setText(maintenance.getApartment());
        viewHolder.amount.setText(maintenance.getAmount());
        viewHolder.modeofPayment.setText(maintenance.getMode());
        viewHolder.monthYear.setText(maintenance.getTimestamp());


    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView maintenanceCardView;
        TextView flatNo, amount, modeofPayment,paidOn,monthYear;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            maintenanceCardView=itemView.findViewById(R.id.maintenance_cardview);
            flatNo=itemView.findViewById(R.id.flat_name);
            amount=itemView.findViewById(R.id.maintenance_rs);
            modeofPayment=itemView.findViewById(R.id.mode_of_payment);
            paidOn=itemView.findViewById(R.id.paid_on_tv);
            monthYear=itemView.findViewById(R.id.month_year);
        }
    }
}

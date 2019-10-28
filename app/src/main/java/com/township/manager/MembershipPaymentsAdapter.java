package com.township.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MembershipPaymentsAdapter extends RecyclerView.Adapter {

    ArrayList<MembershipPayment> dataset;
    Context context;

    public MembershipPaymentsAdapter(ArrayList<MembershipPayment> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_membership_payments, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        MembershipPayment payment = dataset.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.name.setText(payment.getFirst_name() + " " + payment.getLast_name());
        viewHolder.amount.setText(payment.getAmount());
        viewHolder.apartment.setText(payment.getWing() + "-" + payment.getApartment());
        viewHolder.paidOn.setText(payment.getTimestamp());
        viewHolder.validFrom.setText(payment.getTimestamp());
        viewHolder.validThru.setText(payment.getValid_thru_timestamp());
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, apartment, amount, paidOn, validFrom, validThru;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recycler_membership_payments_name_text_view);
            apartment = itemView.findViewById(R.id.recycler_membership_payments_apartment_text_view);
            amount = itemView.findViewById(R.id.recycler_membership_payments_amount_text_view);
            paidOn = itemView.findViewById(R.id.recycler_membership_payments_paid_on_text_view);
            validFrom = itemView.findViewById(R.id.recycler_membership_payments_valid_from_text_view);
            validThru = itemView.findViewById(R.id.recycler_membership_payments_valid_thru_text_view);
        }
    }
}

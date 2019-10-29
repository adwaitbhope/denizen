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

    String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

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

        viewHolder.paidOn.setText(getFormattedDate(payment.getTimestamp()) + " " + getFormattedTime(payment.getTimestamp()));
        viewHolder.validFrom.setText(getFormattedDate(payment.getTimestamp()));
        viewHolder.validThru.setText(getFormattedDate(payment.getValid_thru_timestamp()));
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

    public String getFormattedDate(String timestamp) {
        String day = timestamp.substring(8, 10);
        String digit = day.substring(1);

        if (digit.equals("1")) {
            day = day + "st";
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

    public String getFormattedTime(String timestamp) {
        int hourInt = Integer.valueOf(timestamp.substring(11, 13));
        String period = "am";
        if (hourInt > 12) {
            hourInt -= 12;
            period = "pm";
        }
        String hour = String.valueOf(hourInt);
        if (hourInt == 0) {
            hour = "00";
        }
        int minute = Integer.valueOf(timestamp.substring(14, 16));
        return hour + ":" + minute + " " + period;
    }
}

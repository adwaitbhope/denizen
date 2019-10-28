package com.township.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AmenitiesBookingHistoryAdapter extends RecyclerView.Adapter {

    ArrayList<AmenityBooking> dataset;
    Context context;

    public AmenitiesBookingHistoryAdapter (ArrayList<AmenityBooking> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_amenities_booking_history, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AmenityBooking booking = dataset.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.amenityName.setText(booking.getAmenity_id());
        viewHolder.name.setText(booking.getFirst_name() + " " + booking.getLast_name());
        viewHolder.apartment.setText(booking.getWing_id() + "-" + booking.getApartment());
        if (booking.getPayment()) {
            viewHolder.amount.setText(booking.getPayment_amount());
        } else {
            viewHolder.amount.setText(" - ");
        }
        viewHolder.slotDate.setText(booking.getBooking_from().substring(11, 13) + " to " + booking.getBooking_to().substring(11, 13));
        viewHolder.slotDate.setText(booking.getBooking_from().substring(0, 10));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView amenityName, name, apartment, amount, slotTime, slotDate;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            amenityName = itemView.findViewById(R.id.recycler_amenity_booking_history_amenity_name_text_view);
            name = itemView.findViewById(R.id.recycler_amenity_booking_history_name_text_view);
            apartment = itemView.findViewById(R.id.recycler_amenity_booking_history_apartment_text_view);
            amount = itemView.findViewById(R.id.recycler_amenity_booking_history_amount_text_view);
            slotTime = itemView.findViewById(R.id.recycler_amenity_booking_history_slot_time_text_view);
            slotDate = itemView.findViewById(R.id.recycler_amenity_booking_history_slot_date_text_view);

        }
    }

}

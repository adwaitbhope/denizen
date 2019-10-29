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

    String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};


    public AmenitiesBookingHistoryAdapter(ArrayList<AmenityBooking> dataset, Context context) {
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

        viewHolder.amenityName.setText(booking.getAmenity_name());
        viewHolder.name.setText(booking.getFirst_name() + " " + booking.getLast_name());
        viewHolder.apartment.setText("(" + booking.getWing() + "-" + booking.getApartment() + ")");
        if (booking.getPayment()) {
            viewHolder.amount.setText("₹" + booking.getPayment_amount());
        } else {
            viewHolder.amount.setText(" - ");
        }
        int startTime = Integer.valueOf(booking.getBooking_from().substring(11, 13));
        String startPeriod = "am";
        if (startTime > 12) {
            startTime -= 12;
            startPeriod = "pm";
        }
        int endTime = Integer.valueOf(booking.getBooking_to().substring(11, 13));
        String endPeriod = "am";
        if (endTime > 12) {
            endTime -= 12;
            endPeriod = "pm";
        }

        String slotTime = startTime + "" + startPeriod + " to " + endTime + "" + endPeriod;
        viewHolder.slotTime.setText(slotTime);
        viewHolder.slotDate.setText(getFormattedDate(booking.getBooking_from().substring(0, 10)));
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

}

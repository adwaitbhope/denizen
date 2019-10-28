package com.township.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AmenitySlotsAdapter extends RecyclerView.Adapter {

    ArrayList<AmenitySlot> dataset;
    Context context;

    public AmenitySlotsAdapter(ArrayList<AmenitySlot> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_amenity_slots, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        AmenitySlot slot = dataset.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;

        int startTime = Integer.valueOf(slot.getBilling_from().substring(11, 13));
        String startPeriod = "am";
        if (startTime > 12) {
            startTime -= 12;
            startPeriod = "pm";
        }
        int endTime = Integer.valueOf(slot.getBilling_to().substring(11, 13));
        String endPeriod = "am";
        if (endTime > 12) {
            endTime -= 12;
            endPeriod = "pm";
        }

        String slotTime = startTime + "" + startPeriod + " to " + endTime + "" + endPeriod;
        viewHolder.slotTime.setText(slotTime);
        viewHolder.bookSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(context, "Booked", Toast.LENGTH_SHORT).show();
            }
        });
     }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MaterialTextView slotTime;
        MaterialButton bookSlot;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            slotTime = itemView.findViewById(R.id.recycler_amenity_slot_time_text_view);
            bookSlot = itemView.findViewById(R.id.recycler_amenity_slot_book_slot_button);
        }
    }
}

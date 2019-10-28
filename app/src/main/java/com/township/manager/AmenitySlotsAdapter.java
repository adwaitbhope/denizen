package com.township.manager;

import android.content.Context;
import android.content.Intent;
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
    String amenityId;
    int amount;
    Boolean freeForMembers;

    public AmenitySlotsAdapter(ArrayList<AmenitySlot> dataset, Context context, Boolean freeForMembers, String amenityId, int amount) {
        this.dataset = dataset;
        this.context = context;
        this.amenityId = amenityId;
        this.freeForMembers = freeForMembers;
        this.amount = amount;
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
        final AmenitySlot slot = dataset.get(position);
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

        final String slotTime = startTime + "" + startPeriod + " to " + endTime + "" + endPeriod;
        viewHolder.slotTime.setText(slotTime);
        viewHolder.bookSlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, AmenityBookSlotActivity.class);
                intent.putExtra("slot_start", slot.getBilling_from());
                intent.putExtra("free_for_members", freeForMembers);
                intent.putExtra("amount", amount);
                intent.putExtra("amenity_id", amenityId);
                context.startActivity(intent);
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

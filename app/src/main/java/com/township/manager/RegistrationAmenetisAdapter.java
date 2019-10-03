package com.township.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class RegistrationAmenetisAdapter extends RecyclerView.Adapter<RegistrationAmenetisAdapter.MyViewHolder> {

    ArrayList<Amenity> dataset;
    Context context;


    public RegistrationAmenetisAdapter(ArrayList<Amenity> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public RegistrationAmenetisAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_registration_ameneties, parent, false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RegistrationAmenetisAdapter.MyViewHolder holder, int position) {

        String[] BILLING_PERIOD = new String[]{"Hourly", "Daily"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        context,
                        R.layout.dropdown_menu_popup_item,
                        BILLING_PERIOD);

        holder.billingPeriod.setAdapter(adapter);


        String[] MEMBERS_FREE = new String[]{"Yes", "No"};

        ArrayAdapter<String> adapter1 =
                new ArrayAdapter<>(
                        context,
                        R.layout.dropdown_menu_popup_item,
                        MEMBERS_FREE);

        holder.freeOrNot.setAdapter(adapter1);

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextInputEditText amenityName, rateAmenity;
        AutoCompleteTextView billingPeriod, freeOrNot;
        TextInputLayout amenityNameIIL, rateAmenityTIL;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            amenityName = itemView.findViewById(R.id.amenity_edit_text);
            amenityNameIIL = itemView.findViewById(R.id.amenity_name_til);
            rateAmenity = itemView.findViewById(R.id.amenities_rate_edit_text);
            rateAmenityTIL = itemView.findViewById(R.id.amenities_rate_til);
            billingPeriod = itemView.findViewById(R.id.billing_period_details_filled_exposed_dropdown);
            freeOrNot = itemView.findViewById(R.id.free_for_members_filled_exposed_dropdown);
        }
    }
}

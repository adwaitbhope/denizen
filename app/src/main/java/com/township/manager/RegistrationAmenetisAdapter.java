package com.township.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class RegistrationAmenetisAdapter extends RecyclerView.Adapter<RegistrationAmenetisAdapter.MyViewHolder> {

    ArrayList<Amenity> dataset;
    Context context;
    ArrayList<RegistrationAmenetisAdapter.MyViewHolder> viewHolders;
    Boolean checkAmenitiesError=false;


    public RegistrationAmenetisAdapter(ArrayList<Amenity> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
        viewHolders = new ArrayList<>();
    }

    @NonNull
    @Override
    public RegistrationAmenetisAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_registration_ameneties, parent, false);
        MyViewHolder myViewHolder = new MyViewHolder(view);
        viewHolders.add(myViewHolder);
        error(myViewHolder.amenityNameIIL);
        error(myViewHolder.billingPeriodTIL);
        error(myViewHolder.freeOrNotTIL);
        error(myViewHolder.rateAmenityTIL);
        return myViewHolder;
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
        Amenity amenity = dataset.get(position);

    }

    @SuppressLint({"ResourceType", "Assert"})
    ArrayList<Amenity> getAmenityData() {
        ArrayList<Amenity> amenities = new ArrayList<>();
        Amenity amenity = null;
        MyViewHolder myViewHolder;
        for (int i = 0; i < viewHolders.size(); i++) {
            boolean bool = false;
            myViewHolder = viewHolders.get(i);
            amenity=new Amenity();
            if(checkError(myViewHolder)) {
                checkAmenitiesError=true;
                break;
            }
            amenity.setAmenityrate(Integer.valueOf(myViewHolder.rateAmenity.getText().toString()));
            amenity.setBillingperiod(myViewHolder.billingPeriod.getId() + 1);
            if (myViewHolder.freeOrNot.getId() == 1) bool = true;
            amenity.setFreeornot(bool);
            amenity.setName(myViewHolder.amenityName.getText().toString());

            amenities.add(amenity);
        }
        return amenities;

    }


    @Override
    public int getItemCount() {
        return dataset.size();
    }
    private boolean checkError(RegistrationAmenetisAdapter.MyViewHolder viewHolder) {
        boolean error=false;

        if(TextUtils.isEmpty(viewHolder.amenityName.getText().toString())){
            viewHolder.amenityNameIIL.setError("Please enter your username");
            viewHolder.amenityNameIIL.setErrorEnabled(true);
            viewHolder.amenityNameIIL.requestFocus();
            viewHolder.amenityNameIIL.setErrorIconDrawable(null);
            Log.d("val5",String.valueOf(error));
            error=true;
            return error;
        }
        if(TextUtils.isEmpty(String.valueOf(viewHolder.billingPeriod.getId()))){
            viewHolder.billingPeriodTIL.setError("Please enter your username");
            viewHolder.billingPeriodTIL.setErrorEnabled(true);
            viewHolder.billingPeriodTIL.requestFocus();
            viewHolder.billingPeriodTIL.setErrorIconDrawable(null);
            Log.d("val6",String.valueOf(error));
            error=true;
            return error;
        }
        if(TextUtils.isEmpty(viewHolder.rateAmenity.getText().toString())){
            viewHolder.rateAmenityTIL.setError("Please enter your username");
            viewHolder.rateAmenityTIL.setErrorEnabled(true);
            viewHolder.rateAmenityTIL.requestFocus();
            viewHolder.rateAmenityTIL.setErrorIconDrawable(null);
            Log.d("val7",String.valueOf(error));
            error=true;
            return error;
        }
        if(TextUtils.isEmpty(String.valueOf(viewHolder.freeOrNot.getId()))){
            viewHolder.freeOrNotTIL.setError("Please enter your username");
            viewHolder.freeOrNotTIL.setErrorEnabled(true);
            viewHolder.freeOrNotTIL.requestFocus();
            viewHolder.freeOrNotTIL.setErrorIconDrawable(null);
            Log.d("val8",String.valueOf(error));
            error=true;
            return error;
        }
        return  error;
    }
    public Boolean getAmenitiesError(){
        Log.d("amenitieserror",String.valueOf(checkAmenitiesError));
        return  checkAmenitiesError;
    }
    private void error(final TextInputLayout textInputLayout) {
        Objects.requireNonNull(textInputLayout.getEditText()).addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                textInputLayout.setError(null);
                textInputLayout.setErrorEnabled(false);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
    }
    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextInputEditText amenityName, rateAmenity;
        AutoCompleteTextView billingPeriod, freeOrNot;
        TextInputLayout amenityNameIIL, rateAmenityTIL,billingPeriodTIL,freeOrNotTIL;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            amenityName = itemView.findViewById(R.id.amenity_edit_text);
            amenityNameIIL = itemView.findViewById(R.id.amenity_name_til);
            rateAmenity = itemView.findViewById(R.id.amenities_rate_edit_text);
            rateAmenityTIL = itemView.findViewById(R.id.amenities_rate_til);
            billingPeriod = itemView.findViewById(R.id.billing_period_details_filled_exposed_dropdown);
            freeOrNot = itemView.findViewById(R.id.free_for_members_filled_exposed_dropdown);
            billingPeriodTIL=itemView.findViewById(R.id.amenities_registration_billing_period_TIL);
            freeOrNotTIL=itemView.findViewById(R.id.amenities_registration_free_or_not_til);
        }
    }

}

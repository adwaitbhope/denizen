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

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class RegistrationAmenitiesAdapter extends RecyclerView.Adapter<RegistrationAmenitiesAdapter.MyViewHolder> {

    ArrayList<Amenity> dataset;
    Context context;
    ArrayList<RegistrationAmenitiesAdapter.MyViewHolder> viewHolders;
    Boolean checkAmenitiesError=false;
    Integer billingperiod;


    public RegistrationAmenitiesAdapter(ArrayList<Amenity> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
        viewHolders = new ArrayList<>();
    }

    @NonNull
    @Override
    public RegistrationAmenitiesAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
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
    public void onBindViewHolder(@NonNull RegistrationAmenitiesAdapter.MyViewHolder holder, int position) {

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
            checkAmenitiesError=false;
            boolean bool = false;
            myViewHolder = viewHolders.get(i);
            amenity=new Amenity();
            if(checkErrorAmenities(myViewHolder)) {
                checkAmenitiesError=true;
                break;
            }
            if(myViewHolder.billingPeriod.getText().toString().equals("Hourly"))
                billingperiod=1;
            else
                billingperiod=2;

            if (myViewHolder.freeOrNot.getText().toString().equals("Yes"))
                bool=true;
            amenity.setAmenityrate(Integer.valueOf(myViewHolder.rateAmenity.getText().toString()));
            amenity.setBillingperiod(billingperiod);
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
    private boolean checkErrorAmenities(RegistrationAmenitiesAdapter.MyViewHolder viewHolder) {
        boolean error=false;

        if(TextUtils.isEmpty(viewHolder.amenityName.getText().toString())){
            viewHolder.amenityNameIIL.setError("Required");
            viewHolder.amenityNameIIL.setErrorEnabled(true);
            viewHolder.amenityNameIIL.requestFocus();
            viewHolder.amenityNameIIL.setErrorIconDrawable(null);
            Log.d("val5",String.valueOf(error));
            error=true;
            return error;
        }
        if(TextUtils.isEmpty(viewHolder.rateAmenity.getText().toString())){
            viewHolder.rateAmenityTIL.setError("Required");
            viewHolder.rateAmenityTIL.setErrorEnabled(true);
            viewHolder.rateAmenityTIL.requestFocus();
            viewHolder.rateAmenityTIL.setErrorIconDrawable(null);
            Log.d("val7",String.valueOf(error));
            error=true;
            return error;
        }
        if(TextUtils.isEmpty(viewHolder.billingPeriod.getText().toString())){
            viewHolder.billingPeriodTIL.setError("Required");
            viewHolder.billingPeriodTIL.setErrorEnabled(true);
            viewHolder.billingPeriodTIL.requestFocus();
            viewHolder.billingPeriodTIL.setErrorIconDrawable(null);
            Log.d("val6",String.valueOf(error));
            error=true;
            return error;
        }
        if(TextUtils.isEmpty(viewHolder.freeOrNot.getText().toString())){
            viewHolder.freeOrNotTIL.setError("Required");
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

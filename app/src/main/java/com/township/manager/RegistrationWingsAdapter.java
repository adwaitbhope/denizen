package com.township.manager;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
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
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

class RegistrationWingsAdapter extends RecyclerView.Adapter<RegistrationWingsAdapter.MyViewHolder> {

    ArrayList<Wing> dataset;
    ArrayList<RegistrationWingsAdapter.MyViewHolder> viewsHolder;
    Context context;
    Boolean checkWingsError=false;

    public RegistrationWingsAdapter(ArrayList<Wing> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
        viewsHolder = new ArrayList<>();
    }


    @NonNull
    @Override
    public RegistrationWingsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_registration_wings, parent, false);
        MyViewHolder viewHolder = new MyViewHolder(view);
        viewsHolder.add(viewHolder);
        error(viewHolder.wingNameTIL);
        error(viewHolder.namingConvetionTIL);
        error(viewHolder.numberOfApartmentsPerFloorTIL);
        error(viewHolder.numberOfFloorsTIL);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RegistrationWingsAdapter.MyViewHolder holder, int position) {
        String[] NAMING_CONVENTION = new String[] {"A-1 to A-36", "A-101 to A-904"};

        ArrayAdapter<String> adapter =
                new ArrayAdapter<>(
                        context,
                        R.layout.dropdown_menu_popup_item,
                        NAMING_CONVENTION);

        holder.namingConvention.setAdapter(adapter);


    }

    @SuppressLint("ResourceType")
    public ArrayList<Wing> getWingsData() {
        ArrayList<Wing> wings = new ArrayList<>();
        Wing wing = null;
        MyViewHolder viewHolder;
        for (int i = 0; i < viewsHolder.size(); i++) {
            viewHolder = viewsHolder.get(i);
            wing = new Wing();
            if(checkError(viewHolder)){
                checkWingsError=true;
                break;
            }
            wing.setName(viewHolder.wingName.getText().toString());
            wing.setNamingConvention(viewHolder.namingConvention.getId()+1);
            wing.setNumberOfFloors(Integer.valueOf(viewHolder.numberOfFloors.getText().toString()));
            wing.setNumberOfApartmentsPerFloor(Integer.valueOf(viewHolder.numberOfApartmentsPerFloor.getText().toString()));
            // TODO
            wings.add(wing);
        }
        return wings;
    }
    public Boolean getWingsError(){
        return checkWingsError;
    }
    private boolean checkError(MyViewHolder viewHolder) {
        boolean error=false;

        if(TextUtils.isEmpty(viewHolder.wingName.getText().toString())){
            viewHolder.wingNameTIL.setError("Please enter your username");
            viewHolder.wingNameTIL.setErrorEnabled(true);
            viewHolder.wingNameTIL.requestFocus();
            viewHolder.wingNameTIL.setErrorIconDrawable(null);
            Log.d("val1",String.valueOf(error));
            error=true;
            return error;
        }
        if(TextUtils.isEmpty(String.valueOf(viewHolder.namingConvention.getId()))){
            Log.d("naming",String.valueOf(viewHolder.namingConvention.getId()));
            viewHolder.namingConvetionTIL.setError("Please enter your username");
            viewHolder.namingConvetionTIL.setErrorEnabled(true);
            viewHolder.namingConvetionTIL.requestFocus();
            viewHolder.namingConvetionTIL.setErrorIconDrawable(null);
            Log.d("val2",String.valueOf(error));
            error=true;
            return error;
        }
        if(TextUtils.isEmpty(viewHolder.numberOfFloors.getText().toString())){
            viewHolder.numberOfFloorsTIL.setError("Please enter your username");
            viewHolder.numberOfFloorsTIL.setErrorEnabled(true);
            viewHolder.numberOfFloorsTIL.requestFocus();
            viewHolder.numberOfFloorsTIL.setErrorIconDrawable(null);
            Log.d("val3",String.valueOf(error));
            error=true;
            return error;
        }
        if(TextUtils.isEmpty(viewHolder.numberOfApartmentsPerFloor.getText().toString())){
            viewHolder.numberOfApartmentsPerFloorTIL.setError("Please enter your username");
            viewHolder.numberOfApartmentsPerFloorTIL.setErrorEnabled(true);
            viewHolder.numberOfApartmentsPerFloorTIL.requestFocus();
            viewHolder.numberOfApartmentsPerFloorTIL.setErrorIconDrawable(null);
            Log.d("val4",String.valueOf(error));
            error=true;
            return error;
        }


        return  error;
    }



    @Override
    public int getItemCount() {
        return dataset.size();
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

        TextInputEditText wingName, numberOfFloors, numberOfApartmentsPerFloor;
        AutoCompleteTextView namingConvention;
        TextInputLayout wingNameTIL, numberOfFloorsTIL, numberOfApartmentsPerFloorTIL, namingConvetionTIL;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            wingName = itemView.findViewById(R.id.wing_name_textinput_child);
            numberOfFloors = itemView.findViewById(R.id.no_of_floors_textinput_child);
            numberOfApartmentsPerFloor = itemView.findViewById(R.id.flats_per_floor_textinput_child);
            namingConvention = itemView.findViewById(R.id.naming_convention_filled_exposed_dropdown);

            wingNameTIL = itemView.findViewById(R.id.wing_name_textinput);
            numberOfFloorsTIL = itemView.findViewById(R.id.no_of_floors_textinput);
            numberOfApartmentsPerFloorTIL = itemView.findViewById(R.id.flats_per_floor_textinput);
            namingConvetionTIL = itemView.findViewById(R.id.naming_convention_textinput);
        }
    }
}

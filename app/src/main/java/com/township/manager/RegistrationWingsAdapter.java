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

class RegistrationWingsAdapter extends RecyclerView.Adapter<RegistrationWingsAdapter.MyViewHolder> {

    ArrayList<Wing> dataset;
    Context context;

    public RegistrationWingsAdapter(ArrayList<Wing> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public RegistrationWingsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_registration_wings, parent, false);
        return new MyViewHolder(view);
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

    @Override
    public int getItemCount() {
        return dataset.size();
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

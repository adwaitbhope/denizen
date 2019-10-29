package com.township.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import androidx.room.Room;


public class IntercomAdapter extends RecyclerView.Adapter {

    ArrayList<Intercom> dataset;
    Context context;
    WingDao wingDao;
    AppDatabase appDatabase;
    String wing;
    int wingfilter;

    public IntercomAdapter(ArrayList<Intercom> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;

        appDatabase = Room.databaseBuilder(context,
                AppDatabase.class, "app-database")
                .fallbackToDestructiveMigration()
                .build();
        wingDao=appDatabase.wingDao();
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if(viewType==1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_intercom_admins, parent, false);
            final ViewHolderAdmin viewHolder = new ViewHolderAdmin(view);
            return viewHolder;
        }
        else if(viewType==2){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_intercom_security, parent, false);
            final ViewHolderSecurity viewHolder = new ViewHolderSecurity(view);
            return viewHolder;
        }
        else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_intercom_apartments, parent, false);
            final ViewHolderApartments viewHolder = new ViewHolderApartments(view);
            return viewHolder;
        }


    }

    @Override
    public int getItemViewType(int position) {
        if(dataset.get(position).getType().equals("admin"))
            return 1;
        else if(dataset.get(position).getType().equals("security"))
            return 2;
        else
            return 3;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final Intercom intercom=dataset.get(position);
        MaterialButton button;
        if(dataset.get(position).getType().equals("admin")){
            final ViewHolderAdmin viewHolderAdmin=(ViewHolderAdmin)holder;
            viewHolderAdmin.adminPhone.setText(intercom.getPhone());
            viewHolderAdmin.adminName.setText(intercom.getFirst_name()+" "+intercom.getLast_name());
            viewHolderAdmin.adminDesignation.setText(intercom.getDesignation());
            button=viewHolderAdmin.phoneAdmin;
        }
        else if(dataset.get(position).getType().equals("security")){
            final ViewHolderSecurity viewHolderSecurity=(ViewHolderSecurity)holder;
            viewHolderSecurity.securityPhone.setText(intercom.getPhone());
            viewHolderSecurity.securityGateNumer.setText(intercom.getDesignation());
            button=viewHolderSecurity.phoneSecurity;
        }
        else{

            new Thread(){
                @Override
                public void run() {
                    super.run();
                    wing=wingDao.getWingName(intercom.getWing_id());
                }
            }.start();
            final ViewHolderApartments viewHolderApartments=(ViewHolderApartments)holder;
            viewHolderApartments.residentPhone.setText(intercom.getPhone());
            viewHolderApartments.residentName.setText(intercom.getFirst_name()+" "+intercom.getLast_name());
            viewHolderApartments.flatNo.setText(wing+" "+intercom.getApartment());
            button=viewHolderApartments.phoneApartment;
        }
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri u = Uri.parse("tel:" + intercom.getPhone());
                Intent i = new Intent(Intent.ACTION_DIAL, u);


                try
                {
                    context.startActivity(i);
                }
                catch (SecurityException s)
                {

                }
            }
        });


    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolderAdmin extends RecyclerView.ViewHolder {

        MaterialButton phoneAdmin;
        TextView adminName,adminPhone,adminDesignation;


        public ViewHolderAdmin(@NonNull View itemView) {
            super(itemView);
            phoneAdmin=itemView.findViewById(R.id.intercom_admin_call_button);
            adminName=itemView.findViewById(R.id.intercom_admin_name_text_view);
            adminPhone=itemView.findViewById(R.id.intercom_admin_phone_number_text_view);
            adminDesignation=itemView.findViewById(R.id.intercom_admin_designation_text_view);

        }
    }


    public static class ViewHolderSecurity extends RecyclerView.ViewHolder {

       MaterialButton phoneSecurity;
       TextView securityGateNumer,securityPhone;

        public ViewHolderSecurity(@NonNull View itemView) {
            super(itemView);
            phoneSecurity=itemView.findViewById(R.id.intercom_security_call_button);
            securityGateNumer=itemView.findViewById(R.id.intercom_security_gate_number_text_view);
            securityPhone=itemView.findViewById(R.id.intercom_security_phone_number_text_view);

        }
    }

    public static class ViewHolderApartments extends RecyclerView.ViewHolder {

        MaterialButton phoneApartment;
        TextView flatNo,residentName,residentPhone;

        public ViewHolderApartments(@NonNull View itemView) {
            super(itemView);
            phoneApartment=itemView.findViewById(R.id.intercom_wing_resident_call_button);
            flatNo=itemView.findViewById(R.id.intercom_wing_resident_flat_number_text_view);
            residentName=itemView.findViewById(R.id.intercom_wing_resident_name_text_view);
            residentPhone=itemView.findViewById(R.id.intercom_wing_resident_phone_number_text_view);
        }
    }
}

package com.township.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SecurityDesksAdapter extends RecyclerView.Adapter {

    ArrayList<SecurityDesks> dataset;
    Context context;

    public SecurityDesksAdapter(ArrayList<SecurityDesks> dataset, Context context) {
        this.dataset = dataset;
        this.context=context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_security_desks, parent, false);
        final ViewHolder viewHolder=new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder=(ViewHolder)holder;
        final SecurityDesks securityDesks=dataset.get(position);

        viewHolder.securityDeskNumber.setText(securityDesks.getSecurity_desk_name());
        viewHolder.securityDeskPhoneNumber.setText(securityDesks.getSecurity_desk_phone());
    }

    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{

        TextView securityDeskNumber,securityDeskPhoneNumber;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);


            securityDeskNumber=itemView.findViewById(R.id.security_desk_number);
            securityDeskPhoneNumber=itemView.findViewById(R.id.security_desk_phone_number);
        }

    }
}

package com.township.manager;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class AdminInfoAdapter extends RecyclerView.Adapter {

    ArrayList<AdminInfo> dataset;
    Context context;
    String TOWNSHIP_ID;

    public AdminInfoAdapter(ArrayList<AdminInfo> dataset,Context context) {
        this.dataset = dataset;
        this.context=context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_admin_info, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ViewHolder viewHolder = (ViewHolder) holder;
        final AdminInfo adminInfo = dataset.get(position);
        final String url = "https://township-manager.s3.ap-south-1.amazonaws.com/townships/" + TOWNSHIP_ID + "/user_profile_pics/" + adminInfo.getAdmin_id() + ".png";
        Picasso.get()
                .load(url)
                .into(viewHolder.adminProfilePicure);

        viewHolder.adminName.setText(adminInfo.getFirst_name()+" "+adminInfo.getLast_name());
        viewHolder.adminPhoneNumber.setText(adminInfo.getPhone());
        viewHolder.adminDesignation.setText(adminInfo.getDesignation());
        viewHolder.adminEmail.setText(adminInfo.getEmail());

        viewHolder.adminPhoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Uri uri = Uri.parse("tel:" + adminInfo.getPhone());
                Intent intent = new Intent(Intent.ACTION_DIAL, uri);
                context.startActivity(intent);
            }
        });

        final String[] sendTo;
        sendTo=new String[1];
        sendTo[0]=adminInfo.getEmail();
        viewHolder.adminEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SEND);
                emailIntent.setData(Uri.parse("mailto:"));
                emailIntent.setType("text/plain");
                emailIntent.putExtra(Intent.EXTRA_EMAIL,sendTo);
                context.startActivity(Intent.createChooser(emailIntent, "Send mail..."));
            }
        });
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView adminName,adminPhoneNumber,adminDesignation,adminEmail;
        ImageView adminProfilePicure;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            adminName=itemView.findViewById(R.id.admin_info_name_textView);
            adminPhoneNumber=itemView.findViewById(R.id.admin_info_phone_number_textView);
            adminDesignation=itemView.findViewById(R.id.admin_info_designation_textView);
            adminEmail=itemView.findViewById(R.id.admin_info_email_text_view);
            adminProfilePicure=itemView.findViewById(R.id.admin_info_profile_picture);

        }
    }
}

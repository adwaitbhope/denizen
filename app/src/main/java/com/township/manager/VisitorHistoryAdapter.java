package com.township.manager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VisitorHistoryAdapter extends RecyclerView.Adapter {

    ArrayList<Visitor> dataset;
    Context context;
    String townshipId;

    String[] months = new String[]{"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

    public VisitorHistoryAdapter (ArrayList<Visitor> dataset, Context context, String townshipId) {
        this.dataset = dataset;
        this.context = context;
        this.townshipId = townshipId;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_visitor_history, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;
        Visitor visitor = dataset.get(position);

        final String url = "https://township-manager.s3.ap-south-1.amazonaws.com/townships/" + townshipId + "/visitors/" + visitor.getId() + ".png";
        Picasso.get()
                .load(url)
                .into(viewHolder.image);

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, FullScreenImageViewActivity.class);
                intent.putExtra("url", url);
                context.startActivity(intent);
            }
        });

        viewHolder.name.setText(visitor.getFirst_name() + " " + visitor.getLast_name());
//        viewHolder.visitee.setText(visitor.getWing() + "-" + visitor.getApartment());
        viewHolder.dateIn.setText(getFormattedDate(visitor.getIn_timestamp()));
        viewHolder.timeIn.setText(getFormattedTime(visitor.getIn_timestamp()));
        if (visitor.getOut_timestamp() == null) {
            viewHolder.dateOut.setVisibility(View.GONE);
            viewHolder.timeOut.setVisibility(View.GONE);
            viewHolder.outImage.setVisibility(View.GONE);
        } else {
            viewHolder.dateOut.setText(getFormattedDate(visitor.getOut_timestamp()));
            viewHolder.timeOut.setText(getFormattedTime(visitor.getOut_timestamp()));
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, dateIn, timeIn, dateOut, timeOut;
        ImageView outImage;
        ImageView image;
        TextView visitee;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recycler_view_visitor_name);
            dateIn = itemView.findViewById(R.id.recycler_view_visitor_date_in_text_view);
            timeIn = itemView.findViewById(R.id.recycler_view_visitor_time_in_text_view);
            dateOut = itemView.findViewById(R.id.recycler_view_visitor_date_out_text_view);
            timeOut = itemView.findViewById(R.id.recycler_view_visitor_time_out_text_view);
            outImage = itemView.findViewById(R.id.visitor_out_image_view);
            image = itemView.findViewById(R.id.recycler_view_visitor_image_view);
            visitee = itemView.findViewById(R.id.recycler_view_visitee_wing_flat_text_view);
        }
    }

    public String getFormattedDate(String timestamp) {
        String day = timestamp.substring(8, 10);
        String digit = day.substring(1);

        if (digit.equals("1")) {
            day = day + "st";
        } else if (digit.equals("2")) {
            day = day + "nd";
        } else if (digit.equals("3")) {
            day = day + "rd";
        } else {
            day = day + "th";
        }

        String month = months[Integer.valueOf(timestamp.substring(5, 7)) - 1];

        String year = timestamp.substring(2, 4);

        return month + " " + day + ", '" + year;
    }

    public String getFormattedTime(String timestamp) {
        int hourInt = Integer.valueOf(timestamp.substring(11, 13));
        String period = "am";
        if (hourInt > 12) {
            hourInt -= 12;
            period = "pm";
        }
        String hour = String.valueOf(hourInt);
        if (hourInt == 0) {
            hour = "00";
        }
        int minute = Integer.valueOf(timestamp.substring(14, 16));
        return hour + ":" + minute + " " + period;
    }
}

package com.township.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.lang.reflect.Array;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class VisitorHistoryAdapter extends RecyclerView.Adapter {

    ArrayList<Visitor> dataset;
    Context context;

    public VisitorHistoryAdapter (ArrayList<Visitor> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
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
        viewHolder.name.setText(visitor.getFirst_name() + " " + visitor.getLast_name());
        viewHolder.date.setText(visitor.getInTimestamp().substring(0, 13));
        viewHolder.time.setText(visitor.getInTimestamp().substring(14));
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, date, time;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.recycler_view_visitor_name);
            date = itemView.findViewById(R.id.recycler_view_visitor_date);
            time = itemView.findViewById(R.id.recycler_view_visitor_time);
        }
    }
}

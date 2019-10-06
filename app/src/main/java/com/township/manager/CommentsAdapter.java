package com.township.manager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class CommentsAdapter extends RecyclerView.Adapter {

    ArrayList<Notice.Comment> dataset;
    Context context;

    public CommentsAdapter(ArrayList<Notice.Comment> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_notice_board_comments, parent, false);
        final CommentsAdapter.ViewHolder viewHolder = new CommentsAdapter.ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolder viewHolder = (ViewHolder) holder;

        Notice.Comment comment = dataset.get(position);
        viewHolder.name.setText(comment.getPosted_by_first_name() + " " + comment.getPosted_by_last_name());

        if (comment.getPosted_by_apartment() == null) {
            viewHolder.apartment.setText("(" + comment.getPosted_by_designation() + ")");
        } else {
            viewHolder.apartment.setText("(" + comment.getPosted_by_wing() + "-" + comment.getPosted_by_apartment() + ")");
        }
        viewHolder.content.setText(comment.getContent());

    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, apartment, content;
        ImageView image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.comment_name);
            apartment = itemView.findViewById(R.id.comment_apartment);
            content = itemView.findViewById(R.id.comment_content);
            image = itemView.findViewById(R.id.comment_image);
        }
    }

}

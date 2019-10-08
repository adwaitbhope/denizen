package com.township.manager;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class NoticesAdapter extends RecyclerView.Adapter {

    ArrayList<Notice> dataset;
    Context context;

    public NoticesAdapter(ArrayList<Notice> dataset, Context context) {
        this.dataset = dataset;
        this.context = context;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_notices, parent, false);
        final ViewHolder viewHolder = new ViewHolder(view);

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        final ViewHolder viewHolder = (ViewHolder) holder;
        final Notice notice = dataset.get(position);

        viewHolder.image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, FullScreenImageViewActivity.class));
            }
        });

        viewHolder.viewAllComments.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, NoticeBoardCommentActivity.class);
                intent.putExtra("notice_id", notice.getNotice_id());
                context.startActivity(intent);
            }
        });

        viewHolder.cardView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                viewHolder.cardView.setChecked(!viewHolder.cardView.isChecked());
                return true;
            }
        });

        String title = notice.getTitle();
        if (notice.getWings().size() != 0) {
            title += " ( ";
            for (Wing wing : notice.getWings()) {
                title += wing.getName() + " ";
            }
            title += ")";
        }

        viewHolder.title.setText(title);
        viewHolder.description.setText(notice.getDescription());
        viewHolder.latestComment.setText("No comments yet.");

        ArrayList<Notice.Comment> comments = notice.getComments();
        if (comments.size() == 0) {
//            viewHolder.latestComment.setVisibility(View.GONE);
        } else {
            viewHolder.latestComment.setText(comments.get(comments.size() -1).getContent());
        }
    }

    @Override
    public int getItemCount() {
        return dataset.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        MaterialCardView cardView;
        TextView title, description, latestComment;
        ImageView image;
        MaterialButton viewAllComments;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.notice_title);
            image = itemView.findViewById(R.id.notice_image);
            description = itemView.findViewById(R.id.notice_description);
            latestComment = itemView.findViewById(R.id.notice_latest_comment);
            viewAllComments = itemView.findViewById(R.id.notice_view_all_comments_button);
            cardView = itemView.findViewById(R.id.notice_board_card_view);
        }
    }

}

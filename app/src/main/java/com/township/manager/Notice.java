package com.township.manager;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
class Notice {

    @NonNull
    @PrimaryKey
    String notice_id;

    @ColumnInfo (name = "title")
    String title;

    @ColumnInfo (name = "description")
    String description;

    @ColumnInfo (name = "timestamp")
    String timestamp;

    @ColumnInfo (name = "posted_by_first_name")
    String posted_by_first_name;

    @ColumnInfo (name = "posted_by_last_name")
    String posted_by_last_name;

    @ColumnInfo (name = "posted_by_designation")
    String posted_by_designation;

    @Ignore
    ArrayList<Wing> wings;

    @Ignore
    ArrayList<Comment> comments;

    Notice (String title, String description) {
        this.title = title;
        this.description = description;
    }

    @NonNull
    public String getNotice_id() {
        return notice_id;
    }

    public void setNotice_id(@NonNull String notice_id) {
        this.notice_id = notice_id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public void setWings(ArrayList<Wing> wings) {
        this.wings = wings;
    }

    public ArrayList<Wing> getWings() {
        return wings;
    }

    public void setComments(ArrayList<Comment> comments) {
        this.comments = comments;
    }

    public ArrayList<Comment> getComments() {
        return comments;
    }

    public static class Comment {
        String posted_by_first_name, posted_by_last_name, posted_by_user_id, posted_by_wing, posted_by_apartment;
        String content, timestamp;

        public void setPosted_by_first_name(String posted_by_first_name) {
            this.posted_by_first_name = posted_by_first_name;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public void setContent(String content) {
            this.content = content;
        }

        public void setPosted_by_apartment(String posted_by_apartment) {
            this.posted_by_apartment = posted_by_apartment;
        }

        public void setPosted_by_last_name(String posted_by_last_name) {
            this.posted_by_last_name = posted_by_last_name;
        }

        public void setPosted_by_user_id(String posted_by_user_id) {
            this.posted_by_user_id = posted_by_user_id;
        }

        public void setPosted_by_wing(String posted_by_wing) {
            this.posted_by_wing = posted_by_wing;
        }

        public String getPosted_by_first_name() {
            return posted_by_first_name;
        }

        public String getPosted_by_apartment() {
            return posted_by_apartment;
        }

        public String getContent() {
            return content;
        }

        public String getPosted_by_last_name() {
            return posted_by_last_name;
        }

        public String getPosted_by_user_id() {
            return posted_by_user_id;
        }

        public String getPosted_by_wing() {
            return posted_by_wing;
        }

        public String getTimestamp() {
            return timestamp;
        }
    }

}


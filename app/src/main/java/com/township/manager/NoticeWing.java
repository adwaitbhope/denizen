package com.township.manager;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Notice.class,
                parentColumns = "notice_id",
                childColumns = "notice_id"),
        @ForeignKey(entity = Wing.class,
                parentColumns = "wing_id",
                childColumns = "wing_id")
})
public class NoticeWing {

    @PrimaryKey
    @NonNull
    int id;

    String notice_id;
    String wing_id;

    public void setWing_id(String wing_id) {
        this.wing_id = wing_id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setNotice_id(String notice_id) {
        this.notice_id = notice_id;
    }

    public int getId() {
        return id;
    }

    public String getNotice_id() {
        return notice_id;
    }

    public String getWing_id() {
        return wing_id;
    }
}

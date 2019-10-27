package com.township.manager;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
class Complaint {

    @NonNull
    @PrimaryKey
    String complaint_id;

    @ColumnInfo(name="resident_first_name")
    String resident_first_name;

    @ColumnInfo(name="resident_last_name")
    String resident_last_name;

    @ColumnInfo(name="resident_wing")
    String resident_wing;

    @ColumnInfo(name="resident_apartment")
    String resident_apartment;

    @ColumnInfo(name="resolved")
    Boolean resolved;

    @ColumnInfo(name="description")
    String description;

    @ColumnInfo(name="timestamp")
    String timestamp;

    @ColumnInfo(name="title")
    String title;

    @NonNull
    public String getComplaint_id() {
        return complaint_id;
    }

    public String getResident_first_name() {
        return resident_first_name;
    }

    public String getResident_last_name() {
        return resident_last_name;
    }

    public String getResident_wing() {
        return resident_wing;
    }

    public String getResident_apartment() {
        return resident_apartment;
    }

    public Boolean getResolved() {
        return resolved;
    }

    public String getDescription() {
        return description;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getTitle() {
        return title;
    }

    public void setComplaint_id(@NonNull String complaint_id) {
        this.complaint_id = complaint_id;
    }

    public void setResident_first_name(String resident_first_name) {
        this.resident_first_name = resident_first_name;
    }

    public void setResident_last_name(String resident_last_name) {
        this.resident_last_name = resident_last_name;
    }

    public void setResident_wing(String resident_wing) {
        this.resident_wing = resident_wing;
    }

    public void setResident_apartment(String resident_apartment) {
        this.resident_apartment = resident_apartment;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}

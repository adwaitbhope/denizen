package com.township.manager;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Wing.class,
                parentColumns = "wing_id",
                childColumns ="wing_id")
})
public class Intercom {
    @NonNull
    @PrimaryKey
    String user_id;

    @ColumnInfo(name="type")
    String type;

    @ColumnInfo(name="first_name")
    String first_name;

    @ColumnInfo(name="last_name")
    String last_name;

    @ColumnInfo(name="phone")
    String phone;

    @Ignore
    String wing;

    String wing_id;

    @ColumnInfo(name="apartment")
    String apartment;

    @ColumnInfo(name="designation")
    String designation;

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getWing_id() {
        return wing_id;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }

    public String getWing() {
        return wing;
    }

    public void setWing_id(String wing_id) {
        this.wing_id = wing_id;
    }

    public String getApartment() {
        return apartment;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    @NonNull
    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(@NonNull String user_id) {
        this.user_id = user_id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getFirst_name() {
        return first_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

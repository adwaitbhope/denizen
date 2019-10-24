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
                childColumns = "wing_id")
})
public class Resident {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "username")
    String username;

    @ColumnInfo(name = "first_name")
    String first_name;

    @ColumnInfo(name = "last_name")
    String last_name;

    @ColumnInfo(name = "phone")
    String phone;

    @Ignore
    String wing;

    String wing_id;

    @ColumnInfo(name = "apartment")
    String apartment;

    public String getPhone() {
        return phone;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getFirst_name() {
        return first_name;
    }

    public int getId() {
        return id;
    }

    public String getWing_id() {
        return wing_id;
    }

    public String getWing() {
        return wing;
    }

    public String getUsername() {
        return username;
    }

    public String getApartment() {
        return apartment;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setWing_id(String wing_id) {
        this.wing_id = wing_id;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}


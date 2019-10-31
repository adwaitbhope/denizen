package com.township.manager;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
class Visitor {

    @NonNull
    @PrimaryKey(autoGenerate = true)
    int id;

    @ColumnInfo(name = "first_name")
    String first_name;

    @ColumnInfo(name = "last_name")
    String last_name;

    @ColumnInfo(name = "phone")
    String phone;

    @ColumnInfo(name = "in_timestamp")
    String in_timestamp;

    @ColumnInfo(name = "out_timestamp")
    String out_timestamp;

    @Ignore
    String wing;

    @ColumnInfo(name = "wing_id")
    String wing_id;

    @ColumnInfo(name = "apartment")
    String apartment;

    public String getFirst_name() {
        return first_name;
    }

    public int getId() {
        return id;
    }

    public String getIn_timestamp() {
        return in_timestamp;
    }

    public String getOut_timestamp() {
        return out_timestamp;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPhone() {
        return phone;
    }

    public String getWing_id() {
        return wing_id;
    }

    public String getApartment() {
        return apartment;
    }

    public String getWing() {
        return wing;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setIn_timestamp(String in_timestamp) {
        this.in_timestamp = in_timestamp;
    }

    public void setOut_timestamp(String out_timestamp) {
        this.out_timestamp = out_timestamp;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public void setWing_id(String wing_id) {
        this.wing_id = wing_id;
    }
}

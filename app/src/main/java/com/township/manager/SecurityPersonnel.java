package com.township.manager;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SecurityPersonnel {

    @NonNull
    @PrimaryKey
    String personnel_id;

    @ColumnInfo(name="first_name")
    String first_name;

    @ColumnInfo(name = "last_name")
    String last_name;

    @ColumnInfo(name="phone")
    String phone;

    @ColumnInfo(name = "shift_days")
    int days;

    @ColumnInfo(name="shift_start")
    String shift_start;

    @ColumnInfo(name="shift_end")
    String shift_end;

    @NonNull
    public String getPersonnel_id() {
        return personnel_id;
    }

    public void setPersonnel_id(@NonNull String personnel_id) {
        this.personnel_id = personnel_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getPhone() {
        return phone;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getShift_start() {
        return shift_start;
    }

    public String getShift_end() {
        return shift_end;
    }

    public int getDays() {
        return days;
    }

    public void setShift_start(String shift_start) {
        this.shift_start = shift_start;
    }

    public void setShift_end(String shift_end) {
        this.shift_end = shift_end;
    }

    public void setDays(int days) {
        this.days = days;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }
}

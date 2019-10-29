package com.township.manager;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class AdminInfo {
    @NonNull
    @PrimaryKey
    String admin_id;

    @ColumnInfo(name="first_name")
    String first_name;


    @ColumnInfo(name="last_name")
    String last_name;


    @ColumnInfo(name="designation")
    String designation;


    @ColumnInfo(name="phone")
    String phone;


    @ColumnInfo(name="email")
    String email;

    @NonNull
    public String getAdmin_id() {
        return admin_id;
    }

    public void setAdmin_id(@NonNull String admin_id) {
        this.admin_id = admin_id;
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

    public String getDesignation() {
        return designation;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}

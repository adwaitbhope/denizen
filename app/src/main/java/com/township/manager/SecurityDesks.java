package com.township.manager;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class SecurityDesks {

    @NonNull
    @PrimaryKey
    String desk_id;

    @ColumnInfo(name="designation")
    String designation;

    @ColumnInfo(name="phone")
    String phone;

    @NonNull
    public String getDesk_id() {
        return desk_id;
    }

    public void setDesk_id(@NonNull String desk_id) {
        this.desk_id = desk_id;
    }

    public String getDesignation() {
        return designation;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public void setDesignation(String designation) {
        this.designation = designation;
    }

}

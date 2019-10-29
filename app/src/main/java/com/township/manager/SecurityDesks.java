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

    @ColumnInfo(name="security_desk_name")
    String security_desk_name;

    @NonNull
    public String getDesk_id() {
        return desk_id;
    }

    public void setDesk_id(@NonNull String desk_id) {
        this.desk_id = desk_id;
    }

    public String getSecurity_desk_name() {
        return security_desk_name;
    }

    public void setSecurity_desk_name(String security_desk_name) {
        this.security_desk_name = security_desk_name;
    }

    public String getSecurity_desk_phone() {
        return security_desk_phone;
    }

    public void setSecurity_desk_phone(String security_desk_phone) {
        this.security_desk_phone = security_desk_phone;
    }

    @ColumnInfo(name="security_desk_phone")
    String security_desk_phone;

}

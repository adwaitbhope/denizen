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

    @ColumnInfo(name="security_personnel_name")
    String security_personnel_name;

    @ColumnInfo(name="security_personnel_phone")
    String security_personnel_phone;

    @ColumnInfo(name="security_personnel_timings_from")
    String security_personnel_timings_from;

    @ColumnInfo(name="security_personnel_timings_till")
    String security_personnel_timings_till;

    @NonNull
    public String getPersonnel_id() {
        return personnel_id;
    }

    public void setPersonnel_id(@NonNull String personnel_id) {
        this.personnel_id = personnel_id;
    }

    public String getSecurity_personnel_name() {
        return security_personnel_name;
    }

    public void setSecurity_personnel_name(String security_personnel_name) {
        this.security_personnel_name = security_personnel_name;
    }

    public String getSecurity_personnel_phone() {
        return security_personnel_phone;
    }

    public void setSecurity_personnel_phone(String security_personnel_phone) {
        this.security_personnel_phone = security_personnel_phone;
    }

    public String getSecurity_personnel_timings_from() {
        return security_personnel_timings_from;
    }

    public void setSecurity_personnel_timings_from(String security_personnel_timings_from) {
        this.security_personnel_timings_from = security_personnel_timings_from;
    }

    public String getSecurity_personnel_timings_till() {
        return security_personnel_timings_till;
    }

    public void setSecurity_personnel_timings_till(String security_personnel_timings_till) {
        this.security_personnel_timings_till = security_personnel_timings_till;
    }
}

package com.township.manager;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
class Amenity {

    @NonNull
    @PrimaryKey
    String amenity_id;

    @ColumnInfo(name = "name")
    String name;

    @ColumnInfo(name="time_period")
    int time_period;

    @ColumnInfo(name = "billing_rate")
    int billing_rate;

    @ColumnInfo(name = "free_for_members")
    Boolean free_for_members;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getTime_period() {
        return time_period;
    }

    public void setTime_period(Integer time_period) {
        this.time_period = time_period;
    }

    public Boolean getFree_for_members() {
        return free_for_members;
    }

    public void setFree_for_members(Boolean free_for_members) {
        this.free_for_members = free_for_members;
    }

    public int getBilling_rate() {
        return billing_rate;
    }

    public void setBilling_rate(int billing_rate) {
        this.billing_rate = billing_rate;
    }


}

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
public class MembershipPayment {

    @NonNull
    @PrimaryKey
    String payment_id;

    @ColumnInfo(name = "first_name")
    String first_name;

    @ColumnInfo(name = "last_name")
    String last_name;

    @Ignore
    String wing;

    String wing_id;

    @ColumnInfo(name = "apartment")
    String apartment;

    @ColumnInfo(name = "amount")
    String amount;

    @ColumnInfo(name = "timestamp")
    String timestamp;

    @ColumnInfo(name = "valid_thru_timestamp")
    String valid_thru_timestamp;

    @ColumnInfo(name = "mode")
    String mode;

    public String getLast_name() {
        return last_name;
    }

    public String getWing() {
        return wing;
    }

    public String getWing_id() {
        return wing_id;
    }

    public String getApartment() {
        return apartment;
    }

    public String getFirst_name() {
        return first_name;
    }

    @NonNull
    public String getPayment_id() {
        return payment_id;
    }

    public String getAmount() {
        return amount;
    }

    public String getMode() {
        return mode;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getValid_thru_timestamp() {
        return valid_thru_timestamp;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }

    public void setWing_id(String wing_id) {
        this.wing_id = wing_id;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setPayment_id(@NonNull String payment_id) {
        this.payment_id = payment_id;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setValid_thru_timestamp(String valid_thru_timestamp) {
        this.valid_thru_timestamp = valid_thru_timestamp;
    }
}

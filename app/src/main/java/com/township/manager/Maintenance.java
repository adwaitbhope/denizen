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
public class Maintenance {

    @NonNull
    @PrimaryKey
    String payment_id;

    @ColumnInfo(name="amount")
    String amount;

    @ColumnInfo(name="apartment")
    String apartment;

    @ColumnInfo(name="mode")
    String mode;

    @ColumnInfo(name="cheque_no")
    String cheque_no;

    @ColumnInfo(name="first_name")
    String first_name;

    @ColumnInfo(name="last_name")
    String last_name;

    @ColumnInfo(name="timestamp")
    String timestamp;

    String wing_id;

    @Ignore
    String wing;

    public String getWing() {
        return wing;
    }

    public String getPayment_id() {
        return payment_id;
    }

    public String getAmount() {
        return amount;
    }

    public String getApartment() {
        return apartment;
    }

    public String getMode() {
        return mode;
    }

    public String getCheque_no() {
        return cheque_no;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getWing_id() {
        return wing_id;
    }

    public void setPayment_id(@NonNull String payment_id) {
        this.payment_id = payment_id;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public void setCheque_no(String cheque_no) {
        this.cheque_no = cheque_no;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public void setWing_id(String wing_id) {
        this.wing_id = wing_id;
    }
}

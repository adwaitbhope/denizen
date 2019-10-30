package com.township.manager;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FinancesCredit {
    @NonNull
    @PrimaryKey
    String credit_id;

    @ColumnInfo(name="credit_timestamp")
    String credit_timestamp;

    @ColumnInfo(name="credit_title")
    String credit_title;

    @ColumnInfo(name="credit_amount")
    String credit_amount;

    @ColumnInfo(name="credit_mode_of_payment")
    String credit_mode_of_payment;

    @NonNull
    public String getCredit_id() {
        return credit_id;
    }

    public void setCredit_id(@NonNull String credit_id) {
        this.credit_id = credit_id;
    }

    public String getCredit_timestamp() {
        return credit_timestamp;
    }

    public void setCredit_timestamp(String credit_timestamp) {
        this.credit_timestamp = credit_timestamp;
    }

    public String getCredit_title() {
        return credit_title;
    }

    public void setCredit_title(String credit_title) {
        this.credit_title = credit_title;
    }

    public String getCredit_amount() {
        return credit_amount;
    }

    public void setCredit_amount(String credit_amount) {
        this.credit_amount = credit_amount;
    }

    public String getCredit_mode_of_payment() {
        return credit_mode_of_payment;
    }

    public void setCredit_mode_of_payment(String credit_mode_of_payment) {
        this.credit_mode_of_payment = credit_mode_of_payment;
    }
}
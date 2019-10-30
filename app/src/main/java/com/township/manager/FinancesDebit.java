package com.township.manager;


import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class FinancesDebit {
    @NonNull
    @PrimaryKey
    String debit_id;

    @ColumnInfo(name="debit_timestamp")
    String debit_timestamp;

    @ColumnInfo(name="debit_title")
    String debit_title;

    @ColumnInfo(name="debit_amount")
    String debit_amount;

    @ColumnInfo(name="debit_mode_of_payment")
    String debit_mode_of_payment;

    @NonNull
    public String getDebit_id() {
        return debit_id;
    }

    public void setDebit_id(@NonNull String debit_id) {
        this.debit_id = debit_id;
    }

    public String getDebit_timestamp() {
        return debit_timestamp;
    }

    public void setDebit_timestamp(String debit_timestamp) {
        this.debit_timestamp = debit_timestamp;
    }

    public String getDebit_title() {
        return debit_title;
    }

    public void setDebit_title(String debit_title) {
        this.debit_title = debit_title;
    }

    public String getDebit_amount() {
        return debit_amount;
    }

    public void setDebit_amount(String debit_amount) {
        this.debit_amount = debit_amount;
    }

    public String getDebit_mode_of_payment() {
        return debit_mode_of_payment;
    }

    public void setDebit_mode_of_payment(String debit_mode_of_payment) {
        this.debit_mode_of_payment = debit_mode_of_payment;
    }
}

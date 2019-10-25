package com.township.manager;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Wing.class,
        parentColumns = "wing_id",
        childColumns ="wing_id")
})
public class Maintenance {

    @NonNull
    @PrimaryKey
    String maintenance_id;

    @ColumnInfo(name="amount")
    String amount;

    @ColumnInfo(name="apartment")
    String apartment;

    @ColumnInfo(name="mode")
    String mode;

    @ColumnInfo(name="cheque_no")
    String cheque_no;

    @ColumnInfo(name="first_name")
    String last_name;

    @ColumnInfo(name="timestamp")
    String timestamp;

    String wing_id;

    @NonNull
    public String getMaintenance_id() {
        return maintenance_id;
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

    public String getLast_name() {
        return last_name;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getWing_id() {
        return wing_id;
    }
}

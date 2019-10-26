package com.township.manager;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class ServiceVendors {
    @NonNull
    @PrimaryKey
    String vendor_id;

    @ColumnInfo(name = "first_name")
    String first_name;

    @ColumnInfo(name = "last_name")
    String last_name;

    @ColumnInfo(name = "phone")
    String phone;

    @ColumnInfo(name = "work")
    String work;
}

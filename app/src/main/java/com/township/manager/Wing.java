package com.township.manager;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

@Entity
class Wing {

    @NonNull
    @PrimaryKey
    String wing_id;

    @ColumnInfo (name = "name")
    String wing_name;

    @ColumnInfo (name = "naming_convention")
    int namingConvention;

    @Ignore
    int numberOfFloors;

    @Ignore
    int numberOfApartmentsPerFloor;

    public void setWing_id(@NonNull String wing_id) {
        this.wing_id = wing_id;
    }

    @NonNull
    public String getWing_id() {
        return wing_id;
    }

    public void setName(String name) {
        this.wing_name = name;
    }

    public void setNamingConvention(int namingConvention) {
        this.namingConvention = namingConvention;
    }

    public void setNumberOfApartmentsPerFloor(int numberOfApartmentsPerFloor) {
        this.numberOfApartmentsPerFloor = numberOfApartmentsPerFloor;
    }

    public void setNumberOfFloors(int numberOfFloors) {
        this.numberOfFloors = numberOfFloors;
    }

    public int getNamingConvention() {
        return namingConvention;
    }

    public int getNumberOfApartmentsPerFloor() {
        return numberOfApartmentsPerFloor;
    }

    public int getNumberOfFloors() {
        return numberOfFloors;
    }

    public String getName() {
        return wing_name;
    }
}

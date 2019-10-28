package com.township.manager;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

@Entity(foreignKeys = {
        @ForeignKey(entity = Wing.class,
                parentColumns = "wing_id",
                childColumns = "wing_id")
})
public class AmenityBooking {

    @NonNull
    @PrimaryKey
    String booking_id;

    String amenity_id;

    @ColumnInfo(name = "first_name")
    String first_name;

    @ColumnInfo(name = "last_name")
    String last_name;

    String wing_id;

    @ColumnInfo(name = "apartment")
    String apartment;

    @ColumnInfo(name = "booking_from")
    String booking_from;

    @ColumnInfo(name = "booking_to")
    String booking_to;

    @ColumnInfo(name = "payment")
    Boolean payment;

    @ColumnInfo(name = "payment_mode")
    String payment_mode;

    @ColumnInfo(name = "payment_amount")
    String payment_amount;

    @NonNull
    public String getBooking_id() {
        return booking_id;
    }

    public String getAmenity_id() {
        return amenity_id;
    }

    public String getFirst_name() {
        return first_name;
    }

    public String getApartment() {
        return apartment;
    }

    public String getWing_id() {
        return wing_id;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getBooking_from() {
        return booking_from;
    }

    public String getBooking_to() {
        return booking_to;
    }

    public Boolean getPayment() {
        return payment;
    }

    public String getPayment_amount() {
        return payment_amount;
    }

    public String getPayment_mode() {
        return payment_mode;
    }

    public void setBooking_id(@NonNull String booking_id) {
        this.booking_id = booking_id;
    }

    public void setAmenity_id(String amenity_id) {
        this.amenity_id = amenity_id;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setWing_id(String wing_id) {
        this.wing_id = wing_id;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setBooking_from(String booking_from) {
        this.booking_from = booking_from;
    }

    public void setBooking_to(String booking_to) {
        this.booking_to = booking_to;
    }

    public void setPayment(Boolean payment) {
        this.payment = payment;
    }

    public void setPayment_amount(String payment_amount) {
        this.payment_amount = payment_amount;
    }

    public void setPayment_mode(String payment_mode) {
        this.payment_mode = payment_mode;
    }

}


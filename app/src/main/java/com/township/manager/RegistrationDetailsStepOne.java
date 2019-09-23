package com.township.manager;

import com.google.gson.annotations.SerializedName;

import java.io.File;

public class RegistrationDetailsStepOne {
    @SerializedName("body")
    String applicant_name, applicant_phone, applicant_email, applicant_designation, name, address, phone, geo_address, lat, lng;


    public String getApplicant_name() {
        return applicant_name;
    }

    public RegistrationDetailsStepOne(String applicant_name, String applicant_phone, String applicant_email, String applicant_designation, String name, String address, String phone, String geo_address, String lat, String lng) {
        this.applicant_name = applicant_name;
        this.applicant_phone = applicant_phone;
        this.applicant_email = applicant_email;
        this.applicant_designation = applicant_designation;
        this.name = name;
        this.address = address;
        this.phone = phone;
        this.geo_address = geo_address;
        this.lat = lat;
        this.lng = lng;
    }

    public void setApplicant_name(String applicant_name) {
        this.applicant_name = applicant_name;
    }

    public String getApplicant_phone() {
        return applicant_phone;
    }

    public void setApplicant_phone(String applicant_phone) {
        this.applicant_phone = applicant_phone;
    }

    public String getApplicant_email() {
        return applicant_email;
    }

    public void setApplicant_email(String applicant_email) {
        this.applicant_email = applicant_email;
    }

    public String getApplicant_designation() {
        return applicant_designation;
    }

    public void setApplicant_designation(String applicant_designation) {
        this.applicant_designation = applicant_designation;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGeo_address() {
        return geo_address;
    }

    public void setGeo_address(String geo_address) {
        this.geo_address = geo_address;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLng() {
        return lng;
    }

    public void setLng(String lng) {
        this.lng = lng;
    }
}


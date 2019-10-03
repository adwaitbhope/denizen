package com.township.manager;

 class Amenity {
    String name,billingperiod,freeornot;




    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBillingperiod() {
        return billingperiod;
    }

    public void setBillingperiod(String billingperiod) {
        this.billingperiod = billingperiod;
    }

    public String getFreeornot() {
        return freeornot;
    }

    public void setFreeornot(String freeornot) {
        this.freeornot = freeornot;
    }

    public int getAmenityrate() {
        return amenityrate;
    }

    public void setAmenityrate(int amenityrate) {
        this.amenityrate = amenityrate;
    }

    int amenityrate;
}

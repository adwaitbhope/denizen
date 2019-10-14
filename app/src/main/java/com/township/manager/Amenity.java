package com.township.manager;

 class Amenity {
    int billingperiod,amenityrate;
    String name;
    Boolean freeornot;



    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getBillingperiod() {
        return billingperiod;
    }

    public void setBillingperiod(Integer billingperiod) {
        this.billingperiod = billingperiod;
    }

    public Boolean getFreeornot() {
        return freeornot;
    }

    public void setFreeornot(Boolean freeornot) {
        this.freeornot = freeornot;
    }

    public int getAmenityrate() {
        return amenityrate;
    }

    public void setAmenityrate(int amenityrate) {
        this.amenityrate = amenityrate;
    }


}

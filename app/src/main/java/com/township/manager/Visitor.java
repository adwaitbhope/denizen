package com.township.manager;

class Visitor {

    String id;

    String first_name;

    String last_name;

    String phone;

    String inTimestamp;

    public String getFirst_name() {
        return first_name;
    }

    public String getId() {
        return id;
    }

    public String getInTimestamp() {
        return inTimestamp;
    }

    public String getLast_name() {
        return last_name;
    }

    public String getPhone() {
        return phone;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setFirst_name(String first_name) {
        this.first_name = first_name;
    }

    public void setInTimestamp(String inTimestamp) {
        this.inTimestamp = inTimestamp;
    }

    public void setLast_name(String last_name) {
        this.last_name = last_name;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}

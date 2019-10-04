package com.township.manager;

class Complaint {
    private String complaintId, title, description;
    private String firstName, lastName, wing, apartment;
    private Boolean resolved;
    private String timestamp;

    public void setComplaintId(String complaintId) {
        this.complaintId = complaintId;
    }

    public void setApartment(String apartment) {
        this.apartment = apartment;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setResolved(Boolean resolved) {
        this.resolved = resolved;
    }

    public void setWing(String wing) {
        this.wing = wing;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getComplaintId() {
        return complaintId;
    }

    public Boolean isResolved() {
        return resolved;
    }

    public String getDescription() {
        return description;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getApartment() {
        return apartment;
    }

    public String getLastName() {
        return lastName;
    }

    public String getTitle() {
        return title;
    }

    public String getWing() {
        return wing;
    }

    public String getTimestamp() {
        return timestamp;
    }
}

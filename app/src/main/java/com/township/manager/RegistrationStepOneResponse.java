package com.township.manager;

public class RegistrationStepOneResponse {

    int registration_status;
    String application_id;

    public RegistrationStepOneResponse(int registration_status, String application_id) {
        this.registration_status = registration_status;
        this.application_id = application_id;
    }

    public int getRegistration_status() {
        return registration_status;
    }

    public void setRegistration_status(int registration_status) {
        this.registration_status = registration_status;
    }

    public String getApplication_id() {
        return application_id;
    }

    public void setApplication_id(String application_id) {
        this.application_id = application_id;
    }
}

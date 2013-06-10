package org.motechproject.ivr.domain;

public final class Call {

    private String phoneNumber;
    private String provider;


    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getProvider() {
        return provider;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }
}

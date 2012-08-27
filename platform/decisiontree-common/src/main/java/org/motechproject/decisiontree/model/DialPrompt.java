package org.motechproject.decisiontree.model;

/**
 * Represents the IVR Dial out verb. one can make out-going call in the middle of ivr tree.
 */
public class DialPrompt extends Prompt {
    private String phoneNumber;
    private String callerId;

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public DialPrompt() {
    }

    public DialPrompt(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCallerId() {
        return callerId;
    }

    public DialPrompt setCallerId(String callerId) {
        this.callerId = callerId;
        return this;
    }
}

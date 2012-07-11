package org.motechproject.decisiontree.model;

/**
 * Represents the IVR Dial out verb. one can make out-going call in the middle of ivr tree.
 */
public class DialPrompt extends Prompt {
    private String phoneNumber;
    private String callerId;

    public String getCallerId() {
        return callerId;
    }

    public void setCallerId(String callerId) {
        this.callerId = callerId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        DialPrompt that = (DialPrompt) o;

        if (callerId != null ? !callerId.equals(that.callerId) : that.callerId != null) return false;
        if (phoneNumber != null ? !phoneNumber.equals(that.phoneNumber) : that.phoneNumber != null) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + (phoneNumber != null ? phoneNumber.hashCode() : 0);
        result = 31 * result + (callerId != null ? callerId.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "DialPrompt{" +
                "phoneNumber='" + phoneNumber + '\'' +
                ", callerId='" + callerId + '\'' +
                '}';
    }
}

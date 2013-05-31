package org.motechproject.ivr.domain;

/**
 * Call status - answered or busy/not reachable etc
 */
public enum IVRStatus {
    Answered("answered"),
    NotAnswered("not_answered");

    private String value;

    IVRStatus(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public static boolean isAnswered(String status) {
        return Answered.getValue().equals(status);
    }

    @Override
    public String toString() {
        return value;
    }
}


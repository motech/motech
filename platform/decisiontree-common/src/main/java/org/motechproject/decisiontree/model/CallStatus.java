package org.motechproject.decisiontree.model;

public enum CallStatus {
    hangup;

    public static boolean isValid(String key) {
        for (CallStatus callStatus : values()) {
            if (callStatus.name().equals(key)) {
                return true;
            }
        }
        return false;
    }
}

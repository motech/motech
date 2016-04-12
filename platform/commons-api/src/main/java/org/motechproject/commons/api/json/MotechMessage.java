package org.motechproject.commons.api.json;

/**
 * Represents a message returned from MOTECH
 */
public class MotechMessage {
    private String message;

    public MotechMessage(String text){
        message = text;
    }

    public void setMessage(String text) {
        message = text;
    }

    public String getMessage() {
        return message;
    }
}

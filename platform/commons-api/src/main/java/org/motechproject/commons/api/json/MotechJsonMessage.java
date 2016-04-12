package org.motechproject.commons.api.json;

import com.google.gson.Gson;

/**
 * Handles the creation of Json messages
 * for the MotechMessage class
 */
public class MotechJsonMessage {
    private MotechMessage message;

    public MotechJsonMessage (String text){
        message = new MotechMessage(text);
    }

    public void setMessage (String text) {
        message = new MotechMessage(text);
    }

    public String getMessage () {
        return message.getMessage();
    }

    public String toJson () {
        Gson gson = new Gson();
        return gson.toJson(message);
    }
}

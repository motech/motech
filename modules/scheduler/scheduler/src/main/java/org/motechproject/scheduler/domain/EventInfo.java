package org.motechproject.scheduler.domain;

import java.util.HashMap;
import java.util.Map;

/**
 * EventInfo is the class which contains information about event associated with scheduled job.
 */

public class EventInfo {
    private String subject;
    private Map<String, Object> parameters;

    public EventInfo() {
        parameters = new HashMap<>();
    }

    public String getSubject() {
        return subject;
    }

    public void setSubject(String subject) {
        this.subject = subject;
    }

    public Map<String, Object> getParameters() {
        return parameters;
    }

    public void setParameters(Map<String, Object> parameters) {
        this.parameters = parameters;
    }
};

package org.motechproject.scheduletracking.api.domain;

public class InvalidScheduleDefinition extends RuntimeException {

    public InvalidScheduleDefinition(String s, String ... args) {
        super(String.format(s, args));
    }
}

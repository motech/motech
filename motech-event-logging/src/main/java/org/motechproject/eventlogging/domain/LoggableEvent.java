package org.motechproject.eventlogging.domain;

import java.util.Collections;
import java.util.List;

import org.motechproject.scheduler.domain.MotechEvent;

public class LoggableEvent {

    private List<String> eventSubjects;
    private List<? extends EventFlag> flags;

    public List<String> getEventSubjects() {
        return eventSubjects;
    }

    public List<? extends EventFlag> getFlags() {
        return flags;
    }

    public void setFlags(List<? extends EventFlag> flags) {
        this.flags = flags;
    }

    public void setEventSubjects(List<String> eventSubjects) {
        this.eventSubjects = eventSubjects;
    }

    public LoggableEvent(List<String> eventSubjects, List<? extends EventFlag> flags) {
        if (eventSubjects == null) {
            this.eventSubjects = Collections.<String> emptyList();
        } else {
            this.eventSubjects = eventSubjects;
        }
        if (flags == null) {
            this.flags = Collections.<EventFlag> emptyList();
        } else {
            this.flags = flags;
        }
    }

    public boolean isLoggableEvent(MotechEvent eventToLog) {
        for (String eventSubject : eventSubjects) {
            if (eventToLog.getSubject().equals(eventSubject)) {
                return checkFlags(eventToLog);
            }
            if (eventSubject.endsWith("*")) {
                if (checkWildCardMatch(eventToLog, eventSubject)) {
                    return true;
                }
            }
        }

        return false;
    }

    private boolean checkWildCardMatch(MotechEvent eventToLog, String eventSubject) {
        String[] eventPath = eventToLog.getSubject().split("\\.");
        String[] eventLogPath = eventSubject.split("\\.");

        if (eventLogPath.length <= 1) {
            return checkFlags(eventToLog);
        }

        if (eventLogPath.length <= eventPath.length) {

            for (int i = 0; i < eventLogPath.length - 1; i++) {
                if (!eventLogPath[i].equals(eventPath[i])) {
                    break;
                } else if (i == eventLogPath.length - 2) {
                    return checkFlags(eventToLog);
                }
            }
        } else if (eventLogPath.length - 1 == eventPath.length) {
            for (int i = 0; i < eventLogPath.length - 1; i++) {
                if (!eventLogPath[i].equals(eventPath[i])) {
                    break;
                } else if (i == eventLogPath.length - 2) {
                    return checkFlags(eventToLog);
                }
            }
        }

        return false;
    }

    private boolean checkFlags(MotechEvent eventToLog) {
        for (EventFlag eventFlag : flags) {
            if (!eventFlag.passesFlags(eventToLog)) {
                return false;
            }
        }
        return true;
    }

}

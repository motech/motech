package org.motechproject.scheduler.domain;

import org.motechproject.scheduler.MotechSchedulerService;

import java.io.Serializable;

public abstract class JobId implements Serializable {
    private static final long serialVersionUID = 1L;

    private String subject;
    private String id;
    private String suffix;
    private boolean repeatingJob;

    public JobId(String subject, String id, String suffix) {
        this.subject = subject;
        this.id = id;
        this.suffix = suffix;
    }

    public JobId(MotechEvent motechEvent, String suffix) {
        this(motechEvent.getSubject(), (String) motechEvent.getParameters().get(MotechSchedulerService.JOB_ID_KEY), suffix);
    }

    public String value() {
        return String.format("%s-%s%s", subject, id, suffix);
    }

    @Override
    public String toString() {
        return value();
    }
}

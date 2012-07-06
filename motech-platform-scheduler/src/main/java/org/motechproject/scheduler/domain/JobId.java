package org.motechproject.scheduler.domain;

import org.motechproject.scheduler.MotechSchedulerService;

import java.io.Serializable;

public class JobId implements Serializable {
    public static final String REPEAT_JOB_SUFFIX = "-repeat";
    private static final long serialVersionUID = 1L;

    private String subject;
    private String id;
    private boolean repeatingJob;

    public JobId(String subject, String id, boolean repeatingJob) {
        this.subject = subject;
        this.id = id;
        this.repeatingJob = repeatingJob;
    }

    public JobId(MotechEvent motechEvent, boolean repeatingJob) {
        this(motechEvent.getSubject(), (String) motechEvent.getParameters().get(MotechSchedulerService.JOB_ID_KEY), repeatingJob);
    }

    public String value() {
        return String.format("%s-%s", subject, id);
    }

    @Override
    public String toString() {
        return value();
    }

    public String repeatingId() {
        return value() + REPEAT_JOB_SUFFIX;
    }

    public boolean isRepeatingJob() {
        return repeatingJob;
    }
}

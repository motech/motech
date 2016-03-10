package org.motechproject.scheduler.contract;

import org.motechproject.event.MotechEvent;

import java.io.Serializable;

/**
 * ID used to distinguish one job from others.
 */
public abstract class JobId implements Serializable {

    public static final String JOB_ID_KEY = "JobID";

    private static final long serialVersionUID = 1L;

    private String subject;
    private String id;
    private String suffix;

    /**
     * Constructor.
     *
     * @param subject  the subject of {@code MotechEvent} fired, when job is triggered, not null
     * @param id  the "JobID" parameter for {@code MotechEvent} fired, when job is triggered, not null
     * @param suffix  the type of job
     */
    public JobId(String subject, String id, String suffix) {
        this.subject = subject;
        this.id = id;
        this.suffix = suffix;
    }

    /**
     * Constructor.
     *
     * @param motechEvent  the {@code MotechEvent} fired, when job is triggered, not null
     * @param suffix  the type of job, not null
     */
    public JobId(MotechEvent motechEvent, String suffix) {
        this(motechEvent.getSubject(), (String) motechEvent.getParameters().get(JOB_ID_KEY), suffix);
    }

    /**
     * Returns jobs ID representation as String.
     *
     * @return String representation of jobs ID
     */
    public String value() {
        return String.format("%s-%s-%s", subject, id, suffix);
    }

    @Override
    public String toString() {
        return value();
    }
}

package org.motechproject.scheduler.contract;

import org.motechproject.event.MotechEvent;

/**
 * Represents Job that can be scheduled
 */
public interface SchedulableJob {

    MotechEvent getMotechEvent();

    boolean isIgnorePastFiresAtStart();
}

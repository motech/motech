package org.motechproject.tasks.service;

import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.ex.TriggerNotFoundException;

/**
 * Standard interface for tasks which executes tasks.
 */
public interface TriggerHandler {

    void registerHandlerFor(String subject);

    void handle(MotechEvent event) throws TriggerNotFoundException;

}

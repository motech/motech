package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;

import java.util.List;

/**
 * Responsible for providing dynamic triggers and actions for existing task channels.
 */
public interface DynamicChannelProvider {

    /**
     * Returns a list of triggers based on the given {@code page} and {@code pageSize}.
     *
     * @param page  the number of the page
     * @param pageSize  the size of the page
     * @return the list of triggers
     */
    List<TriggerEvent> getTriggers(int page, int pageSize);

    /**
     * Returns a trigger based on the given {@code info}.
     *
     * @param info  the information about the trigger
     * @return the trigger with the given subject if it exists, null otherwise
     */
    TriggerEvent getTrigger(TaskTriggerInformation info);

    /**
     * Checks whether this provider provides trigger with the given subject.
     *
     * @param subject  the subject of the trigger
     * @return true if this provider returns a trigger with the given subject, false otherwise
     */
    boolean validateSubject(String subject);

    /**
     * Returns the amount of the provided triggers.
     *
     * @return the number of provided triggers
     */
    long countTriggers();
}

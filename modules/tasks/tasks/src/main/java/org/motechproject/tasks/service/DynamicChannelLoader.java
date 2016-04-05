package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;

import java.util.List;

/**
 * Service responsible for loading dynamic triggers and action from modules which provides an instance of the
 * {@link DynamicChannelProvider}.
 */
public interface DynamicChannelLoader {

    /**
     * Returns a list of triggers based on the given {@code moduleName}, {@code page} and {@code pageSize}.
     *
     * @param moduleName  the name of the module
     * @param page  the number of the page
     * @param pageSize  the size of the page
     * @return list of triggers
     */
    List<TriggerEvent> getDynamicTriggers(String moduleName, int page, int pageSize);

    /**
     * Checks whether the module with the given {@code moduleName} provides dynamic triggers.
     *
     * @param moduleName  the name of the module
     * @return true if module provides triggers, false otherwise
     */
    boolean providesDynamicTriggers(String moduleName);

    /**
     * Returns the amount of the triggers provided by the module with the given {@code moduleName}.
     *
     * @param moduleName  the name of the module
     * @return the amount of trigger provided by the module
     */
    Long countByChannelModuleName(String moduleName);

    /**
     * Returns a trigger matching the information provided with the {@code triggerInformation}.
     *
     * @param triggerInformation  the information about the trigger
     * @return the matching trigger
     */
    TriggerEvent getTrigger(TaskTriggerInformation triggerInformation);

    /**
     * Checks whether module with the given {@code moduleName} provides an implementation of the
     * {@link DynamicChannelProvider}.
     *
     * @param moduleName  the name of the module
     * @return true if module provides the implementation, false otherwise
     */
    boolean channelExists(String moduleName);

    /**
     * Checks whether the given {@code subject} is a valid trigger for module with the given {@code moduleName}.
     *
     * @param moduleName  the name of the module
     * @param subject  the subject of the trigger
     * @return true if the subject is valid, false otherwise
     */
    boolean validateTrigger(String moduleName, String subject);
}

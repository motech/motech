package org.motechproject.tasks.service;

import org.motechproject.tasks.domain.mds.task.TaskError;
import org.motechproject.tasks.domain.mds.task.TaskTriggerInformation;
import org.motechproject.tasks.domain.mds.channel.TriggerEvent;

import java.util.List;
import java.util.Set;

/**
 * Service responsible for retrieving, counting and validating triggers.
 */
public interface TriggerEventService {

    /**
     * Returns a list of dynamic triggers currently provided by the channel with the given {@code moduleName} based on
     * the given page and page size.
     *
     * @param moduleName  the name of the channel module
     * @param page  the number of the page
     * @param pageSize  the size of the page
     * @return list of dynamic triggers
     */
    List<TriggerEvent> getDynamicTriggers(String moduleName, int page, int pageSize);

    /**
     * Returns a list of static triggers currently provided by the channel with the given {@code moduleName} based on
     * the given page and page size.
     *
     * @param moduleName  the name of the channel module
     * @param page  the number of the page
     * @param pageSize  the size of the page
     * @return list of static triggers
     */
    List<TriggerEvent> getStaticTriggers(String moduleName, int page, int pageSize);

    /**
     * Returns a trigger based on the information given in the {@code triggerInformation}.
     *
     * @param triggerInformation  the information about a trigger
     * @return the trigger it exists, null otherwise
     */
    TriggerEvent getTrigger(TaskTriggerInformation triggerInformation);

    /**
     * Checks whether trigger matching the given information exists.
     *
     * @param triggerInformation  the information about a trigger
     * @return true if matching trigger exists, false otherwise
     */
    boolean triggerExists(TaskTriggerInformation triggerInformation);

    /**
     * Checks whether channel with the given {@code moduleName} provides dynamic triggers.
     *
     * @param moduleName  the name of the module
     * @return true if the channel provides dynamic triggers, false otherwise
     */
    boolean providesDynamicTriggers(String moduleName);

    /**
     * Returns the amount of static triggers provided by the channel with the given {@code moduleName}.
     *
     * @param moduleName  the name of the module
     * @return the amount of static triggers
     */
    long countStaticTriggers(String moduleName);

    /**
     * Returns the amount of dynamic triggers provided by the channel with the given {@code moduleName}.
     *
     * @param moduleName  the name of the module
     * @return the amount of dynamic triggers
     */
    long countDynamicTriggers(String moduleName);

    /**
     * Checks if the given trigger is valid by checking if related channel exists and if it provides a trigger matching
     * the given {@code triggerInformation}.
     *
     * @param triggerInformation  the information about a trigger
     * @return list of error related with the trigger validation
     */
    Set<TaskError> validateTrigger(TaskTriggerInformation triggerInformation);
}

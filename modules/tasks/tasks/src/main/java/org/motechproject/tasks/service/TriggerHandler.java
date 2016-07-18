package org.motechproject.tasks.service;

import org.motechproject.commons.api.DataProvider;
import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.exception.TriggerNotFoundException;

/**
 * Service responsible for handling triggers. When registered for an event with a specific subject, it will act as
 * MotechEvent listener for it. That means, when the event with the subject handled by this handler is fired, the
 * handler will retrieve all active tasks with triggers corresponding to this event and execute them.
 */
public interface TriggerHandler {

    /**
     * Registers this handler to listen for events with the given subject. This handler will now act as a MotechEvent
     * listener for the given subject and will get called by the event system when an event with the given subject is
     * fired.
     *
     * @param subject  the event subject, not null
     */
    void registerHandlerFor(String subject);

    /**
     * Registers this handler to listen for events with the given subject. This handler will now act as a MotechEvent
     * listener for the given subject and will get called by the event system when an event with the given subject is
     * fired. If a task is using retry system, the additional retry handler should be registered. The flag isRetryHandler
     * should be use to choose what kind of handler will be registered.
     *
     * @param subject  the event subject, not null
     * @param isRetryHandler true if handler is for task retry system; false otherwise
     */
    void registerHandlerFor(String subject, boolean isRetryHandler);

    /**
     * Handles the given event. This method is responsible for retrieving active tasks with triggers corresponding to
     * this event and then executing them. It is called by the event system.
     *
     * @param event  the event, not null
     * @throws TriggerNotFoundException if the trigger for the given event wasn't found
     */
    void handle(MotechEvent event) throws TriggerNotFoundException;

    /**
     * Handles the given event. This method is responsible for handling task retry.
     *
     * @param event  the event, not null
     */
    void handleRetry(MotechEvent event);

    /**
     * Retries task execution for activity with the given ID.
     *
     * @param activityId the ID of activity for which task should be retried
     */
    void retryTask(Long activityId);

    /**
     * Adds {@link org.motechproject.commons.api.DataProvider} for this handler.
     *
     * @param provider DataProvider to be added
     */
    void addDataProvider(DataProvider provider);

    /**
     * Removes {@link org.motechproject.commons.api.DataProvider} from this handler.
     *
     * @param taskDataProviderId ID of the DataProvider to be removed, passed as a String
     */
    void removeDataProvider(String taskDataProviderId);
}

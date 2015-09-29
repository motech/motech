package org.motechproject.tasks.service;

import org.motechproject.event.MotechEvent;
import org.motechproject.tasks.domain.Task;
import org.motechproject.tasks.ex.TriggerNotFoundException;

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
     * Handles the given event. This method is responsible for retrieving active tasks with triggers corresponding to
     * this event and then executing them. It is called by the event system.
     *
     * @param event  the event, not null
     * @throws TriggerNotFoundException if the trigger for the given event wasn't found
     */
    void handle(MotechEvent event) throws TriggerNotFoundException;

    // todo
    void unscheduleTaskTriggerFor(Task task);

}

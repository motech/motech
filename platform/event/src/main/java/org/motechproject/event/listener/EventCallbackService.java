package org.motechproject.event.listener;

import org.motechproject.event.MotechEvent;

/**
 * Implementing {@link EventCallbackService} and exposing it as OSGi service allows to receive callbacks, after
 * the events have been handled by their respective listener. In order to invoke a callback, the name of the callback service must be set in
 * the {@link MotechEvent} and must match the name returned by the {@link #getName()} method.
 */
public interface EventCallbackService {

    /**
     * Callback method, invoked when the event handler method has thrown an exception.
     *
     * @param event the event that was being handled
     * @param throwable the throwable that has caused the failure
     * @return true, if the event system should proceed with retries; false otherwise
     */
    boolean failureCallback(MotechEvent event, Throwable throwable);

    /**
     * Callback method, invoked when the event handler method has executed successfully.
     *
     * @param event the event that has been handled
     */
    void successCallback(MotechEvent event);

    /**
     * The name of this callback. It must match the name set in the {@link MotechEvent#callbackName} in order to have
     * the callbacks invoked.
     *
     * @return callback name
     */
    String getName();
}

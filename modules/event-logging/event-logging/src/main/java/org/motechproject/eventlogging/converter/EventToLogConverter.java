package org.motechproject.eventlogging.converter;

import org.motechproject.scheduler.domain.MotechEvent;

/**
 * Interface to implement for custom converters. The converter should return an
 * appropriate return type that a logger can use to log the information.
 * @param <T> The return type that will be used to log.
 */
public interface EventToLogConverter<T> {

    /**
     * Converts an event to a generic type that should be used to log the event.
     * @param event The incoming motech event to be converted to a log.
     * @return An appropriate object type that can be used by a logger to log the event.
     */
    T convertToLog(MotechEvent event);
}

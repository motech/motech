package org.motechproject.eventlogging.service;

import java.util.List;

/**
 * Interface to query for stored events.
 */
public interface EventQueryService<T> {

    /**
     * Queries the database for all events that match a given subject.
     *
     * @param subject  the subject of the event
     * @return a list of informational objects that match the event subject
     */
    List<T> getAllEventsBySubject(String subject);

    /**
     * Queries the database for all events that match a given parameter and key-
     * value pair.
     *
     * @param parameter  the parameter key from the event
     * @param value  the parameter value from the event
     * @return a list of informational objects that match the key-value pair
     */
    List<T> getAllEventsByParameter(String parameter, String value);

    /**
     * Queries the database for all events that match a given subject and
     * parameter key-value pair.
     *
     * @param subject  the subject of the event.
     * @param parameter  the parameter key from the event
     * @param value  the parameter value from the event
     * @return a list of informational objects that match the subject as well as the key-value pair
     */
    List<T> getAllEventsBySubjectAndParameter(String subject, String parameter, String value);

}

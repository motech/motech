package org.motechproject.eventlogging.service;

import java.util.List;

/**
 * Class to query Couch for stored events.
 */
public interface EventQueryService<T> {

    /**
     * Queries the database for all events that match a given subject.
     * @param subject The subject of the event.
     * @return A list of informational objects that match the event subject.
     */
    List<T> getAllEventsBySubject(String subject);

    /**
     * Queries the database for all events that match a given parameter and key
     * value pair.
     * @param parameter The parameter from the event.
     * @param value The value the event should have.
     * @return A list of informational objects that match the key value pair.
     */
    List<T> getAllEventsByParameter(String parameter, String value);

    /**
     * Queries the database for all events that match a given subject and
     * parameter and key value pair.
     * @param subject The subject of the event.
     * @param parameter The parameter from the event.
     * @param value The value the event should have.
     * @return A list of informational objects that match the subject as well as the key value pair.
     */
    List<T> getAllEventsBySubjectAndParameter(String subject, String parameter, String value);

}

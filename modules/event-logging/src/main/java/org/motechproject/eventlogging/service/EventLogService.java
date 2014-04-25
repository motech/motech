package org.motechproject.eventlogging.service;

import org.motechproject.eventlogging.domain.EventLog;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

/**
 * Motech Data Service interface for {@link EventLog}s. The implementation is generated
 * and injected by the MDS module.
 */
public interface EventLogService extends MotechDataService<EventLog> {

    /**
     * Finds all recorded events with the given subject
     * @param name A subject to filter by
     * @return A collection of all recorded events with the given subject.
     */
    @Lookup
    List<EventLog> findBySubject(String name);

}

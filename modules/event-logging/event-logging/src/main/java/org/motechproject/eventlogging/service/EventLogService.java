package org.motechproject.eventlogging.service;

import org.motechproject.eventlogging.domain.EventLog;
import org.motechproject.mds.annotations.Lookup;
import org.motechproject.mds.service.MotechDataService;

import java.util.List;

public interface EventLogService extends MotechDataService<EventLog> {

    @Lookup
    List<EventLog> findBySubject(String subject);

}

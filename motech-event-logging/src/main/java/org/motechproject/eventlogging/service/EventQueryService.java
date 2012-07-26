package org.motechproject.eventlogging.service;

import java.util.List;
import org.motechproject.eventlogging.domain.CouchEventLog;

public interface EventQueryService {

    List<CouchEventLog> getAllEventsBySubject(String subject);

    List<CouchEventLog> getAllEventsByParameter(String parameter, String value);

    List<CouchEventLog> getAllEventsBySubjectAndParameter(String subject,
            String parameter, String value);

}

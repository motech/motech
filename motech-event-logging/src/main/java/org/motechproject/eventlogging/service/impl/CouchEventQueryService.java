package org.motechproject.eventlogging.service.impl;

import java.util.List;
import org.motechproject.eventlogging.domain.CouchEventLog;
import org.motechproject.eventlogging.repository.AllCouchLogs;
import org.motechproject.eventlogging.service.EventQueryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouchEventQueryService implements EventQueryService<CouchEventLog> {

    @Autowired
    private AllCouchLogs allCouchLogs;

    @Override
    public List<CouchEventLog> getAllEventsBySubject(String subject) {
        return allCouchLogs.findAllBySubject(subject);
    }

    @Override
    public List<CouchEventLog> getAllEventsByParameter(String parameter, String value) {
        return allCouchLogs.findAllByParameter(parameter, value);
    }

    @Override
    public List<CouchEventLog> getAllEventsBySubjectAndParameter(String subject, String parameter, String value) {
        return allCouchLogs.findAllBySubjectAndParameter(subject, parameter, value);
    }

}

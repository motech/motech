package org.motechproject.server.logging.dao;

import org.ektorp.CouchDbConnector;
import org.motechproject.dao.MotechBaseRepository;
import org.motechproject.server.logging.domain.EventLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;

public class AllEventLogs extends MotechBaseRepository<EventLog> {
    @Autowired
    public AllEventLogs(@Qualifier("loggingDbConnector") CouchDbConnector db) {
        super(EventLog.class, db);
        initStandardDesignDocument();
    }
}
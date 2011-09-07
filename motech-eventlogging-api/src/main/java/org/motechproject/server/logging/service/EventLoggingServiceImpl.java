package org.motechproject.server.logging.service;

import org.joda.time.DateTime;
import org.motechproject.server.logging.dao.AllEventLogs;
import org.motechproject.server.logging.domain.EventLog;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

public class EventLoggingServiceImpl implements EventLoggingService {
    private AllEventLogs allEventLogs;

    @Autowired
    public EventLoggingServiceImpl(AllEventLogs allEventLogs) {
        this.allEventLogs = allEventLogs;
    }

    @Override
    public void create(String externalId, String logType, String name, String description, DateTime dateTime, Map<String, String> data) {
        EventLog eventLog = new EventLog(externalId, logType, name, description, dateTime, data);
        allEventLogs.add(eventLog);
    }
}
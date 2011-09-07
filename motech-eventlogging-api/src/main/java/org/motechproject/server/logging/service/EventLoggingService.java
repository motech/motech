package org.motechproject.server.logging.service;

import org.joda.time.DateTime;

import java.util.Map;

public interface EventLoggingService {
    void create (String externalId, String logType, String name, String description, DateTime dateTime, Map<String, String> data);
}
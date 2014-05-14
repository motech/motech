package org.motechproject.eventlogging.service.impl;

import org.motechproject.event.MotechEvent;
import org.motechproject.eventlogging.converter.impl.DefaultDbToLogConverter;
import org.motechproject.eventlogging.matchers.DbLoggableEvent;
import org.motechproject.eventlogging.matchers.KeyValue;
import org.motechproject.eventlogging.matchers.LogMappings;
import org.motechproject.eventlogging.matchers.LoggableEvent;
import org.motechproject.eventlogging.matchers.MappingsJson;
import org.motechproject.eventlogging.matchers.ParametersPresentEventFlag;
import org.motechproject.eventlogging.loggers.impl.DbEventLogger;
import org.motechproject.eventlogging.repository.AllEventMappings;
import org.motechproject.eventlogging.service.EventLogService;
import org.motechproject.eventlogging.service.EventLoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class DbEventLoggingService implements EventLoggingService {

    private List<DbEventLogger> dbEventLoggers;

    private DbEventLogger defaultDbEventLogger;

    @Autowired
    private AllEventMappings allEventMappings;

    @Autowired
    private EventLogService eventLogService;

    @Autowired
    private DefaultDbToLogConverter defaultDbToLogConverter;

    public DbEventLoggingService() {
        this.dbEventLoggers = Collections.<DbEventLogger> emptyList();
    }

    public DbEventLoggingService(AllEventMappings allEventMappings) {
        this.allEventMappings = allEventMappings;
        this.dbEventLoggers = Collections.<DbEventLogger> emptyList();
        this.defaultDbEventLogger = createDefaultEventLogger();
    }

    @PostConstruct
    private DbEventLogger createDefaultEventLogger() {
        List<MappingsJson> allMappings = allEventMappings.getAllMappings();
        defaultDbEventLogger = new DbEventLogger(eventLogService, defaultDbToLogConverter);
        List<LoggableEvent> loggableEvents = new ArrayList<>();
        for (MappingsJson mapping : allMappings) {
            if (mapping.getMappings() == null && mapping.getIncludes() == null && mapping.getExcludes() == null) {
                LoggableEvent event = new LoggableEvent(mapping.getSubjects(), mapping.getFlags());
                loggableEvents.add(event);
            } else {
                List<KeyValue> mappings = null;

                DbLoggableEvent dbLoggableEvent = new DbLoggableEvent(mapping.getSubjects(), null, null);

                if (mapping.getMappings() != null) {
                    List<Map<String, String>> mappingList = mapping.getMappings();
                    mappings = new ArrayList<>();

                    List<String> subjects = mapping.getSubjects();

                    if (subjects != null) {
                        for (Map<String, String> map : mappingList) {
                            KeyValue keyValue = constructKeyValue(map);
                            mappings.add(keyValue);
                        }
                    }
                }

                List<String> inclusions = mapping.getIncludes();
                List<String> exclusions = mapping.getExcludes();

                LogMappings logMappings = new LogMappings(mappings, exclusions, inclusions);

                dbLoggableEvent.setMappings(logMappings);

                List<ParametersPresentEventFlag> eventFlags = mapping.getFlags();

                if (eventFlags != null) {
                    dbLoggableEvent.setFlags(eventFlags);
                }

                loggableEvents.add(dbLoggableEvent);
            }
        }
        defaultDbEventLogger.addLoggableEvents(loggableEvents);

        return defaultDbEventLogger;
    }

    public DbEventLoggingService(List<DbEventLogger> dbEventLoggers) {
        this.dbEventLoggers = new ArrayList<>();
        this.dbEventLoggers.addAll(dbEventLoggers);
    }

    @Override
    public void logEvent(MotechEvent event) {
        for (DbEventLogger dbEventLogger : dbEventLoggers) {
            dbEventLogger.log(event);
        }

        defaultDbEventLogger.log(event);
    }

    @Override
    public Set<String> getLoggedEventSubjects() {
        Set<String> eventSubjectsSet = new HashSet<String>();
        for (DbEventLogger eventLogger : dbEventLoggers) {
            List<LoggableEvent> events = eventLogger.getLoggableEvents();
            for (LoggableEvent event : events) {
                List<String> eventSubjects = event.getEventSubjects();
                eventSubjectsSet.addAll(eventSubjects);
            }
        }

        List<LoggableEvent> events = defaultDbEventLogger.getLoggableEvents();
        for (LoggableEvent event : events) {
            List<String> eventSubjects = event.getEventSubjects();
            eventSubjectsSet.addAll(eventSubjects);
        }

        return eventSubjectsSet;
    }

    public List<DbEventLogger> getDbEventLoggers() {
        return dbEventLoggers;
    }

    public void setDbEventLoggers(List<DbEventLogger> dbEventLoggers) {
        this.dbEventLoggers = dbEventLoggers;
    }

    public DbEventLogger getDefaultDbEventLogger() {
        return defaultDbEventLogger;
    }

    public void setDefaultDbEventLogger(DbEventLogger defaultDbEventLogger) {
        this.defaultDbEventLogger = defaultDbEventLogger;
    }

    public AllEventMappings getAllEventMappings() {
        return allEventMappings;
    }

    public void setAllEventMappings(AllEventMappings allEventMappings) {
        this.allEventMappings = allEventMappings;
    }

    public DefaultDbToLogConverter getDefaultDbToLogConverter() {
        return defaultDbToLogConverter;
    }

    public void setDefaultDbToLogConverter(DefaultDbToLogConverter defaultDbToLogConverter) {
        this.defaultDbToLogConverter = defaultDbToLogConverter;
    }

    private KeyValue constructKeyValue(Map<String, String> map) {
        String startKey = null;
        String startValue = null;
        String endKey = null;
        String endValue = null;

        for (Map.Entry<String, String> entry : map.entrySet()) {
            if (startKey == null) {
                startKey = entry.getKey();
                startValue = entry.getValue();
            } else {
                endKey = entry.getKey();
                endValue = entry.getValue();
            }
        }

        return new KeyValue(startKey, startValue, endKey, endValue, true);
    }
}

package org.motechproject.eventlogging.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.motechproject.eventlogging.converter.impl.DefaultCouchToLogConverter;
import org.motechproject.eventlogging.domain.CouchLogMappings;
import org.motechproject.eventlogging.domain.CouchLoggableEvent;
import org.motechproject.eventlogging.domain.KeyValue;
import org.motechproject.eventlogging.domain.LoggableEvent;
import org.motechproject.eventlogging.domain.MappingsJson;
import org.motechproject.eventlogging.domain.ParametersPresentEventFlag;
import org.motechproject.eventlogging.loggers.impl.CouchEventLogger;
import org.motechproject.eventlogging.repository.AllCouchLogs;
import org.motechproject.eventlogging.repository.AllEventMappings;
import org.motechproject.eventlogging.service.EventLoggingService;
import org.motechproject.scheduler.domain.MotechEvent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CouchEventLoggingService implements EventLoggingService {

    private List<CouchEventLogger> couchEventLoggers;

    private CouchEventLogger defaultCouchEventLogger;

    @Autowired
    private AllEventMappings allEventMappings;

    @Autowired
    private AllCouchLogs allCouchLogs;

    @Autowired
    private DefaultCouchToLogConverter defaultCouchToLogConverter;

    public CouchEventLoggingService() {
        this.couchEventLoggers = Collections.<CouchEventLogger> emptyList();
    }

    public CouchEventLoggingService(AllEventMappings allEventMappings) {
        this.allEventMappings = allEventMappings;
        this.couchEventLoggers = Collections.<CouchEventLogger> emptyList();
        this.defaultCouchEventLogger = createDefaultCouchEventLogger();
    }

    @PostConstruct
    private CouchEventLogger createDefaultCouchEventLogger() {
        List<MappingsJson> allMappings = allEventMappings.getAllMappings();
        defaultCouchEventLogger = new CouchEventLogger(allCouchLogs, defaultCouchToLogConverter);
        List<LoggableEvent> loggableEvents = new ArrayList<LoggableEvent>();
        for (MappingsJson mapping : allMappings) {
            if (mapping.getMappings() == null && mapping.getIncludes() == null && mapping.getExcludes() == null) {
                LoggableEvent event = new LoggableEvent(mapping.getSubjects(), mapping.getFlags());
                loggableEvents.add(event);
            } else {
                List<KeyValue> mappings = null;

                CouchLoggableEvent couchEvent = new CouchLoggableEvent(mapping.getSubjects(), null, null);

                if (mapping.getMappings() != null) {
                    List<Map<String, String>> mappingList = mapping.getMappings();
                    mappings = new ArrayList<KeyValue>();

                    List<String> subjects = mapping.getSubjects();

                    if (subjects != null) {
                        for (Map<String, String> map : mappingList) {
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
                            KeyValue keyValue = new KeyValue(startKey, startValue, endKey, endValue, true);
                            mappings.add(keyValue);
                        }
                    }
                }

                List<String> inclusions = mapping.getIncludes();
                List<String> exclusions = mapping.getExcludes();

                CouchLogMappings logMappings = new CouchLogMappings(mappings, exclusions, inclusions);

                couchEvent.setMappings(logMappings);

                List<ParametersPresentEventFlag> eventFlags = mapping.getFlags();

                if (eventFlags != null) {
                    couchEvent.setFlags(eventFlags);
                }

                loggableEvents.add(couchEvent);
            }
        }
        defaultCouchEventLogger.addLoggableEvents(loggableEvents);

        return defaultCouchEventLogger;
    }

    public CouchEventLoggingService(List<CouchEventLogger> couchEventLoggers) {
        this.couchEventLoggers = new ArrayList<CouchEventLogger>();
        this.couchEventLoggers.addAll(couchEventLoggers);
    }

    @Override
    public void logEvent(MotechEvent event) {
        for (CouchEventLogger couchEventLogger : couchEventLoggers) {
            couchEventLogger.log(event);
        }

        defaultCouchEventLogger.log(event);
    }

    @Override
    public Set<String> getLoggedEventSubjects() {
        Set<String> eventSubjectsSet = new HashSet<String>();
        for (CouchEventLogger eventLogger : couchEventLoggers) {
            List<LoggableEvent> events = eventLogger.getLoggableEvents();
            for (LoggableEvent event : events) {
                List<String> eventSubjects = event.getEventSubjects();
                eventSubjectsSet.addAll(eventSubjects);
            }
        }

        List<LoggableEvent> events = defaultCouchEventLogger.getLoggableEvents();
        for (LoggableEvent event : events) {
            List<String> eventSubjects = event.getEventSubjects();
            eventSubjectsSet.addAll(eventSubjects);
        }

        return eventSubjectsSet;
    }

    public List<CouchEventLogger> getCouchEventLoggers() {
        return couchEventLoggers;
    }

    public void setCouchEventLoggers(List<CouchEventLogger> couchEventLoggers) {
        this.couchEventLoggers = couchEventLoggers;
    }

    public CouchEventLogger getDefaultCouchEventLogger() {
        return defaultCouchEventLogger;
    }

    public void setDefaultCouchEventLogger(CouchEventLogger defaultCouchEventLogger) {
        this.defaultCouchEventLogger = defaultCouchEventLogger;
    }

    public AllEventMappings getAllEventMappings() {
        return allEventMappings;
    }

    public void setAllEventMappings(AllEventMappings allEventMappings) {
        this.allEventMappings = allEventMappings;
    }

    public AllCouchLogs getAllCouchLogs() {
        return allCouchLogs;
    }

    public void setAllCouchLogs(AllCouchLogs allCouchLogs) {
        this.allCouchLogs = allCouchLogs;
    }

    public DefaultCouchToLogConverter getDefaultCouchToLogConverter() {
        return defaultCouchToLogConverter;
    }

    public void setDefaultCouchToLogConverter(DefaultCouchToLogConverter defaultCouchToLogConverter) {
        this.defaultCouchToLogConverter = defaultCouchToLogConverter;
    }

}

package org.motechproject.eventlogging.web;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.motechproject.eventlogging.converter.impl.DefaultCouchToLogConverter;
import org.motechproject.eventlogging.converter.impl.DefaultFileToLogConverter;
import org.motechproject.eventlogging.domain.CouchEventLog;
import org.motechproject.eventlogging.domain.EventFlag;
import org.motechproject.eventlogging.domain.LoggableEvent;
import org.motechproject.eventlogging.domain.ParametersPresentEventFlag;
import org.motechproject.eventlogging.loggers.impl.CouchEventLogger;
import org.motechproject.eventlogging.loggers.impl.FileEventLogger;
import org.motechproject.eventlogging.service.EventLoggingServiceManager;
import org.motechproject.eventlogging.service.EventQueryService;
import org.motechproject.eventlogging.service.impl.CouchEventLoggingService;
import org.motechproject.eventlogging.service.impl.FileEventLoggingService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class EventTestController {

    @Autowired
    private CouchEventLogger couchEventLogger;

    @Autowired
    EventLoggingServiceManager eventLoggingServiceManager;
    
    @Autowired
    CouchEventLoggingService couchEventLoggingService;
    
    @Autowired
    EventQueryService eventQueryService;
    
    @RequestMapping({ "/query" })
    public ModelAndView query(HttpServletRequest request,
            HttpServletResponse response) {
        List<CouchEventLog> log1 = eventQueryService.getAllEventsBySubjectAndParameter("org.test", "key1", "value1");
        List<CouchEventLog> log2 = eventQueryService.getAllEventsBySubjectAndParameter("org.test", "key2", "value1");
        List<CouchEventLog> log3 = eventQueryService.getAllEventsBySubjectAndParameter("org.test.test", "key1", "value1");
        
        System.out.println(log1.size());
        System.out.println(log2.size());
        System.out.println(log3.size());
        
        List<CouchEventLog> log4 = eventQueryService.getAllEventsByParameter("key1", "value1");
        
        System.out.println(log4.size());
        
        List<CouchEventLog> log5 = eventQueryService.getAllEventsBySubject("org.test");
        
        System.out.println(log5.size());
        return null;
    }
    
    
    
    @RequestMapping({ "/register" })
    public ModelAndView register(HttpServletRequest request,
            HttpServletResponse response) {
        eventLoggingServiceManager.registerEventLoggingService(couchEventLoggingService);
        return null;
    }

    @RequestMapping({ "/test" })
    public ModelAndView testSubmission(HttpServletRequest request,
            HttpServletResponse response) {

        List<FileEventLogger> fileEventLoggers = new ArrayList<FileEventLogger>();
        
        List<LoggableEvent> events = new ArrayList<LoggableEvent>();
        
        List<String> eventSubjects = new ArrayList<String>();
        
        eventSubjects.add("org.motechproject.commcare.api.exception");
        
        LoggableEvent event = new LoggableEvent(eventSubjects, null);
        
        events.add(event);
        
        List<File> filesToLogTo = new ArrayList<File>();
        
        filesToLogTo.add(new File("testLogging.txt"));
        
        filesToLogTo.add(new File("testLogging2.txt"));
        
        DefaultFileToLogConverter converter = new DefaultFileToLogConverter();
        
        FileEventLogger fileLogger = new FileEventLogger(events, filesToLogTo, converter);
        
        fileEventLoggers.add(fileLogger);
        
        FileEventLoggingService fileService = new FileEventLoggingService(fileEventLoggers);
        
        eventLoggingServiceManager.registerEventLoggingService(fileService);
        
        return null;
    }
    
    @RequestMapping({ "/test2" })
    public ModelAndView testSubmission2(HttpServletRequest request,
            HttpServletResponse response) {

        List<CouchEventLogger> fileEventLoggers = new ArrayList<CouchEventLogger>();
        
        List<LoggableEvent> events = new ArrayList<LoggableEvent>();
        
        List<String> eventSubjects = new ArrayList<String>();
        
        eventSubjects.add("org.test.*");
        
        List<EventFlag> flags = new ArrayList<EventFlag>();
        
        
        Map<String, String> keyValuePairsPresent = new HashMap<String, String>();
        
        keyValuePairsPresent.put("result", "SUCCESS");
        
        EventFlag flag = new ParametersPresentEventFlag(keyValuePairsPresent);
        
        
        flags.add(flag);
        
        LoggableEvent event = new LoggableEvent(eventSubjects, flags);
        
        events.add(event);
        
        List<CouchEventLogger> loggers = new ArrayList<CouchEventLogger>();
        
        couchEventLogger.addLoggableEvents(events);
        
        loggers.add(couchEventLogger);
        
        System.out.println("LOGGER SIZE: " + loggers.size());
        
        CouchEventLoggingService couchService = new CouchEventLoggingService(loggers);
        
        System.out.println("Registering logger...");
        
        System.out.println("Before registering: " + couchService.getLoggedEventSubjects());
        
        eventLoggingServiceManager.registerEventLoggingService(couchService);
        
        return null;
    }
}

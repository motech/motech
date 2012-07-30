package org.motechproject.eventlogging.service.impl;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.motechproject.eventlogging.service.EventLoggingService;
import org.motechproject.eventlogging.service.EventLoggingServiceManager;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.server.event.EventListener;
import org.motechproject.server.event.EventListenerRegistryService;
import org.motechproject.server.event.annotations.MotechListenerEventProxy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class EventLoggingServiceManagerImpl implements EventLoggingServiceManager {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    private List<EventLoggingService> eventLoggingServices = new ArrayList<EventLoggingService>();

    @Autowired
    private EventListenerRegistryService eventListenerRegistryService;

    @Autowired
    private CouchEventLoggingService couchEventLoggingService;

    @PostConstruct
    private void registerDefaultService() {
        registerEventLoggingService(couchEventLoggingService);
    }

    @Override
    public void registerEventLoggingService(EventLoggingService eventLoggingService) {
        registerListeners(eventLoggingService);
        eventLoggingServices.add(eventLoggingService);
    }

    private void registerListeners(EventLoggingService eventLoggingService) {

        Set<String> subjectsToListenOn = eventLoggingService.getLoggedEventSubjects();

        EventListener eventListener = null;

        try {
            Method method = eventLoggingService.getClass().getMethod("logEvent", MotechEvent.class);
            String className = eventLoggingService.getClass().getSimpleName();
            String beanName = className.replace(className.charAt(0), Character.toLowerCase(className.charAt(0)));
            eventListener = new MotechListenerEventProxy(beanName, eventLoggingService, method);
        } catch (NoSuchMethodException e) {
            logger.warn("Unable to write to: add listener for " + eventLoggingService.getClass().getName());
        } catch (SecurityException e) {
            logger.warn("Unable to write to: add listener for " + eventLoggingService.getClass().getName());
        }

        List<String> subjectList = new ArrayList<String>(subjectsToListenOn);

        if (eventListener != null) {
            eventListenerRegistryService.registerListener(eventListener, subjectList);
        }
    }

    @Override
    public void updateEventLoggingService(EventLoggingService eventLoggingService) {
        // TODO Auto-generated method stub
    }
}

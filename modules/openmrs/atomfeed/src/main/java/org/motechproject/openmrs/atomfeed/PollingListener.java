package org.motechproject.openmrs.atomfeed;

import org.apache.log4j.Logger;
import org.motechproject.MotechException;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.event.listener.annotations.MotechListener;
import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.motechproject.openmrs.atomfeed.service.AtomFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

/**
 * Listens for the {@link EventSubjects#POLLING_SUBJECT}, then invokes the
 * {@link AtomFeedService#fetchNewOpenMrsEvents()} to retrieve latest events
 * from the OpenMRS
 */
@Component
public class PollingListener {
    private static final Logger LOGGER = Logger.getLogger(PollingListener.class);

    private final AtomFeedService atomFeedService;
    private final EventRelay eventRelay;

    @Autowired
    public PollingListener(AtomFeedService atomFeedService, EventRelay eventRelay) {
        this.atomFeedService = atomFeedService;
        this.eventRelay = eventRelay;
    }

    @MotechListener(subjects = { EventSubjects.POLLING_SUBJECT })
    public void onPollingEvent(MotechEvent event) {
        LOGGER.debug("Handling OpenMRS Atom Feed Polling event");
        try {
            atomFeedService.fetchOpenMrsChangesSinceLastUpdate();
        } catch (MotechException e) {
            LOGGER.error("There was an error fetching the atom feed from the OpenMRS");
            MotechEvent exceptionEvent = new MotechEvent(EventSubjects.POLLING_EXCEPTION);
            eventRelay.sendEventMessage(exceptionEvent);
        }
    }
}

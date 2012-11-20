package org.motechproject.openmrs.atomfeed.service.impl;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.Xpp3Driver;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.motechproject.MotechException;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.openmrs.atomfeed.OpenMrsHttpClient;
import org.motechproject.openmrs.atomfeed.builder.ConceptEvent;
import org.motechproject.openmrs.atomfeed.builder.EncounterEvent;
import org.motechproject.openmrs.atomfeed.builder.ObservationEvent;
import org.motechproject.openmrs.atomfeed.builder.PatientEvent;
import org.motechproject.openmrs.atomfeed.model.Entry;
import org.motechproject.openmrs.atomfeed.model.Feed;
import org.motechproject.openmrs.atomfeed.model.Link;
import org.motechproject.openmrs.atomfeed.repository.AtomFeedDao;
import org.motechproject.openmrs.atomfeed.service.AtomFeedService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;

@Component("atomFeedService")
public class AtomFeedServiceImpl implements AtomFeedService {
    private static final Logger LOGGER = Logger.getLogger(AtomFeedServiceImpl.class);

    private final OpenMrsHttpClient client;
    private final XStream xstream;
    private final EventRelay eventRelay;
    private final AtomFeedDao atomFeedDao;

    @Autowired
    public AtomFeedServiceImpl(OpenMrsHttpClient client, EventRelay eventRelay, AtomFeedDao atomFeedDao) {
        this.client = client;
        this.eventRelay = eventRelay;
        this.atomFeedDao = atomFeedDao;

        xstream = new XStream(new Xpp3Driver());
        xstream.setClassLoader(getClass().getClassLoader());

        xstream.processAnnotations(Feed.class);
        xstream.processAnnotations(Entry.class);
        xstream.processAnnotations(Entry.Author.class);
        xstream.processAnnotations(Link.class);

        xstream.omitField(Entry.class, "summary");
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.motechproject.openmrs.atomfeed.service.AtomFeedService#
     * fetchAllOpenMrsChanges()
     */
    @Override
    public void fetchAllOpenMrsChanges() {
        String feed = client.getOpenMrsAtomFeed();
        parseFeed(feed, null, null);
    }

    private void parseFeed(String feed, String lastTimeUpdate, String lastId) {
        if (StringUtils.isEmpty(feed)) {
            LOGGER.debug("No XML found from OpenMRS Atom Feed");
            return;
        }

        parseChanges(feed, lastTimeUpdate, lastId);
    }

    private void parseChanges(String feedXml, String lastTimeUpdate, String lastId) {
        Feed feed = (Feed) xstream.fromXML(feedXml);
        List<Entry> entries = feed.getEntry();

        if (entries == null || entries.isEmpty()) {
            LOGGER.debug("No entries present");
            return;
        }

        // entries from the atom feed come in descending order
        // reversing puts them in ascending order so if there is
        // an exception that occurs during the processing of entries
        // the update time can be recovered during a later request
        String lastProcessedEntryUpdateTime = null;
        String lastProcessedId = null;
        Collections.reverse(entries);

        try {
            for (Entry entry : entries) {
                if (matchesLastUpdatedEntry(entry, lastTimeUpdate, lastId)) {
                    continue;
                }
                MotechEvent event = null;
                if ("org.openmrs.Patient".equals(entry.getClassname())) {
                    LOGGER.debug("Found a patient change");
                    event = handlePatientEntry(entry);
                } else if ("org.openmrs.Concept".equals(entry.getClassname())) {
                    LOGGER.debug("Found a concept change");
                    event = handleConceptEntry(entry);
                } else if ("org.openmrs.Encounter".equals(entry.getClassname())) {
                    LOGGER.debug("Found an encounter change");
                    event = handleEncounterEntry(entry);
                } else if ("org.openmrs.Obs".equals(entry.getClassname())) {
                    LOGGER.debug("Found an observation change");
                    event = handleObservationEntry(entry);
                }

                eventRelay.sendEventMessage(event);
                lastProcessedEntryUpdateTime = entry.getUpdated();
                lastProcessedId = entry.getId();
            }
        } catch (Exception e) {
            LOGGER.error("There was a problem processing an OpenMRS Atom Feed entry: " + e.getMessage());
            throw new MotechException("Problem processing an OpenMRS Atom Feed entry", e);
        } finally {
            if (StringUtils.isNotBlank(lastProcessedEntryUpdateTime)) {
                atomFeedDao.setLastUpdateTime(lastProcessedId, lastProcessedEntryUpdateTime);
            }
        }
    }

    private boolean matchesLastUpdatedEntry(Entry entry, String lastTimeUpdate, String lastId) {
        if (StringUtils.isBlank(lastTimeUpdate) || StringUtils.isBlank(lastId)) {
            return false;
        }

        return lastTimeUpdate.equals(entry.getUpdated()) && lastId.equals(entry.getId());
    }

    private MotechEvent handlePatientEntry(Entry entry) {
        return new PatientEvent(entry).toEvent();
    }

    private MotechEvent handleConceptEntry(Entry entry) {
        return new ConceptEvent(entry).toEvent();
    }

    private MotechEvent handleEncounterEntry(Entry entry) {
        return new EncounterEvent(entry).toEvent();
    }

    private MotechEvent handleObservationEntry(Entry entry) {
        return new ObservationEvent(entry).toEvent();
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.motechproject.openmrs.atomfeed.service.AtomFeedService#
     * fetchOpenMrsChangesSinceLastUpdate()
     */
    @Override
    public void fetchOpenMrsChangesSinceLastUpdate() {
        String lastUpdateTime = atomFeedDao.getLastUpdateTime();
        String lastId = atomFeedDao.getLastId();
        fetchOpenMrsChangesSince(lastUpdateTime, lastId);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.motechproject.openmrs.atomfeed.service.AtomFeedService#
     * fetchOpenMrsChangesSince(java.lang.String, java.lang.String)
     */
    @Override
    public void fetchOpenMrsChangesSince(String sinceDateTime, String lastId) {
        LOGGER.debug("Fetching OpenMRS Atom Feed since: " + sinceDateTime);

        String feed = null;

        if (StringUtils.isNotBlank(sinceDateTime)) {
            feed = client.getOpenMrsAtomFeedSinceDate(sinceDateTime);
        } else {
            feed = client.getOpenMrsAtomFeed();
        }

        parseFeed(feed, sinceDateTime, lastId);
    }
}

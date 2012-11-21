package org.motechproject.openmrs.atomfeed.service.impl;

import org.apache.commons.io.IOUtils;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.commons.api.MotechException;
import org.motechproject.event.MotechEvent;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.openmrs.atomfeed.OpenMrsHttpClient;
import org.motechproject.openmrs.atomfeed.events.EventDataKeys;
import org.motechproject.openmrs.atomfeed.events.EventSubjects;
import org.motechproject.openmrs.atomfeed.repository.AtomFeedDao;
import org.motechproject.openmrs.atomfeed.service.AtomFeedService;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyZeroInteractions;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class AtomFeedServiceImplTest {

    @Mock
    private EventRelay eventRelay;

    @Mock
    private OpenMrsHttpClient client;

    @Mock
    private AtomFeedDao atomFeedDao;

    @Before
    public void setUp() {
        initMocks(this);
        atomFeedClient = new AtomFeedServiceImpl(client, eventRelay, atomFeedDao);
    }

    private static final String PATIENT_LINK = "http://localhost:8092/openmrs/ws/rest/v1/person/64f6f0e2-1acc-4a00-8a54-6adcd8cbfdfc";
    private static final String PATIENT_UUID = "64f6f0e2-1acc-4a00-8a54-6adcd8cbfdfc";

    private static final String CONCEPT_UUID = "a8f54bd7-429a-43d1-b47a-5ee56f18f0f2";
    private static final String CONCEPT_LINK = "http://localhost:8092/openmrs/ws/rest/v1/concept/a8f54bd7-429a-43d1-b47a-5ee56f18f0f2";

    private static final String ENCOUNTER_LINK = "http://localhost:8092/openmrs/ws/rest/v1/encounter/a8f54bd7-429a-43d1-b47a-5ee56f18f0f2";
    private static final String ENCOUNTER_UUID = "a8f54bd7-429a-43d1-b47a-5ee56f18f0f2";

    private static final String OBSERVATION_LINK = "http://localhost:8092/openmrs/ws/rest/v1/obs/a8f54bd7-429a-43d1-b47a-5ee56f18f0f2";
    private static final String OBSERVATION_UUID = "a8f54bd7-429a-43d1-b47a-5ee56f18f0f2";

    private AtomFeedService atomFeedClient;

    @Test
    public void shouldNotRaiseAnyEventsOnEmptyXml() throws IOException {
        when(client.getOpenMrsAtomFeed()).thenReturn("");
        atomFeedClient.fetchAllOpenMrsChanges();

        verify(eventRelay, times(0)).sendEventMessage(any(MotechEvent.class));
    }

    @Test
    public void shouldRaisePatientCreateEvent() throws IOException {
        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("patient-create.xml"));

        atomFeedClient.fetchAllOpenMrsChanges();

        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(1)).sendEventMessage(event.capture());

        assertEquals(expectedMotechEventForPatient(EventSubjects.PATIENT_CREATE, "create", PATIENT_LINK, PATIENT_UUID),
                event.getValue());
    }

    private Object expectedMotechEventForPatient(String subject, String action, String link, String uuid) {
        MotechEvent event = new MotechEvent(subject);
        Map<String, Object> params = event.getParameters();
        params.put(EventDataKeys.UUID, uuid);
        params.put(EventDataKeys.AUTHOR, "Super User (admin)");
        params.put(EventDataKeys.ACTION, action);
        params.put(EventDataKeys.UPDATED, "2012-07-02T15:00:00-04:00");
        params.put(EventDataKeys.LINK, link);
        return event;
    }

    private String readXmlFile(String string) throws IOException {
        ClassPathResource resource = new ClassPathResource(string);
        String read = IOUtils.toString(resource.getInputStream());
        resource.getInputStream().close();
        return read;
    }

    @Test
    public void shouldRaisePatientUpdateEvent() throws IOException {
        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("patient-update.xml"));

        atomFeedClient.fetchAllOpenMrsChanges();

        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(1)).sendEventMessage(event.capture());

        assertEquals(expectedMotechEventForPatient(EventSubjects.PATIENT_UPDATE, "update", PATIENT_LINK, PATIENT_UUID),
                event.getValue());
    }

    @Test
    public void shouldRaisePatientVoidEvent() throws IOException {
        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("patient-void.xml"));

        atomFeedClient.fetchAllOpenMrsChanges();

        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(1)).sendEventMessage(event.capture());

        assertEquals(expectedMotechEventForPatient(EventSubjects.PATIENT_VOIDED, "void", PATIENT_LINK, PATIENT_UUID),
                event.getValue());
    }

    @Test
    public void shouldRaisePatientDeleteEvent() throws IOException {
        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("patient-delete.xml"));

        atomFeedClient.fetchAllOpenMrsChanges();

        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(1)).sendEventMessage(event.capture());

        assertEquals(
                expectedMotechEventForPatient(EventSubjects.PATIENT_DELETED, "delete", PATIENT_LINK, PATIENT_UUID),
                event.getValue());
    }

    @Test
    public void shouldRaiseConceptEvent() throws IOException {
        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("concept.xml"));

        atomFeedClient.fetchAllOpenMrsChanges();

        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(1)).sendEventMessage(event.capture());

        assertEquals(expectedMotechEventForPatient(EventSubjects.CONCEPT_CREATE, "create", CONCEPT_LINK, CONCEPT_UUID),
                event.getValue());
    }

    @Test
    public void shouldRaiseEncounterEvent() throws IOException {
        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("encounter.xml"));

        atomFeedClient.fetchAllOpenMrsChanges();

        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(1)).sendEventMessage(event.capture());

        assertEquals(
                expectedMotechEventForPatient(EventSubjects.ENCOUNTER_CREATE, "create", ENCOUNTER_LINK, ENCOUNTER_UUID),
                event.getValue());
    }

    @Test
    public void shouldRaiseObservationEvent() throws IOException {
        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("observation.xml"));

        atomFeedClient.fetchAllOpenMrsChanges();

        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(eventRelay, times(1)).sendEventMessage(event.capture());

        assertEquals(
                expectedMotechEventForPatient(EventSubjects.OBSERVATION_CREATE, "create", OBSERVATION_LINK,
                        OBSERVATION_UUID), event.getValue());
    }

    @Test
    public void shouldSetLastUpdateTimeAndIdToLastEntry() throws IOException {
        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("single-patient-with-multiple-updates.xml"));

        atomFeedClient.fetchAllOpenMrsChanges();

        verify(atomFeedDao).setLastUpdateTime("urn:uuid:64f6f0e2-1acc-4a00-8a54-6adcd8cbfdfc",
                "2012-07-02T17:00:00-04:00");
    }

    @Test(expected = MotechException.class)
    public void shouldNotSetLastUpdateTimeWhenFirstEntryFails() throws IOException {
        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("single-patient-with-multiple-updates.xml"));
        doThrow(new RuntimeException()).when(eventRelay).sendEventMessage(any(MotechEvent.class));
        try {
            atomFeedClient.fetchAllOpenMrsChanges();
        } finally {
            verifyZeroInteractions(atomFeedDao);
        }
    }

    @Test(expected = MotechException.class)
    public void shouldSetLastUpdateTimeToFirstEntry() throws IOException {
        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("single-patient-with-multiple-updates.xml"));
        doNothing().doThrow(new RuntimeException()).when(eventRelay).sendEventMessage(any(MotechEvent.class));
        try {
            atomFeedClient.fetchAllOpenMrsChanges();
        } finally {
            verify(atomFeedDao).setLastUpdateTime("urn:uuid:64f6f0e2-1acc-4a00-8a54-6adcd8cbfdfc",
                    "2012-07-02T15:00:00-04:00");
        }
    }

    @Test
    public void shouldOnlySkipFirstEntryOnTimeAndIdMatch() throws IOException {
        when(client.getOpenMrsAtomFeedSinceDate(anyString())).thenReturn(
                readXmlFile("single-patient-with-multiple-updates.xml"));
        when(atomFeedDao.getLastUpdateTime()).thenReturn("2012-07-02T15:00:00-04:00");
        when(atomFeedDao.getLastId()).thenReturn("urn:uuid:64f6f0e2-1acc-4a00-8a54-6adcd8cbfdfc");

        atomFeedClient.fetchOpenMrsChangesSinceLastUpdate();

        verify(eventRelay, times(1)).sendEventMessage(any(MotechEvent.class));
    }

    @Test
    public void shouldFetchAllOnEmptyLastUpdateTime() {
        when(client.getOpenMrsAtomFeed()).thenReturn("");

        atomFeedClient.fetchOpenMrsChangesSinceLastUpdate();

        verify(client).getOpenMrsAtomFeed();
    }

    @Test
    public void shouldIgnoreAlreadyProcessedEntry() throws IOException {
        when(client.getOpenMrsAtomFeedSinceDate(anyString())).thenReturn(
                readXmlFile("multiple-patients-updates-with-same-time.xml"));
        when(atomFeedDao.getLastUpdateTime()).thenReturn("2012-07-02T15:00:00-04:00");
        when(atomFeedDao.getLastId()).thenReturn("urn:uuid:64f6f0e2-1acc-4a00-8a54-6adcd8cbfdfc");

        atomFeedClient.fetchOpenMrsChangesSinceLastUpdate();

        verify(eventRelay, times(1)).sendEventMessage(any(MotechEvent.class));
    }

    @Test
    public void shouldNotProcessAnyEntriesOnEmptyFeed() throws IOException {
        when(client.getOpenMrsAtomFeed()).thenReturn(readXmlFile("empty-feed.xml"));

        atomFeedClient.fetchAllOpenMrsChanges();

        verifyZeroInteractions(eventRelay);
    }
}

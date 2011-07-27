package org.motechproject.server.pillreminder;


import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.motechproject.server.pillreminder.util.PillReminderTime;

import java.util.Date;
import java.util.HashMap;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class ReminderEventHandlerTest {

    @Mock
    private OutboundEventGateway outboundEventGateway;

    @Mock
    private AllPillRegimens allPillRegimens;

    @Mock
    private PillReminderTime pillRegimenTime;

    private ReminderEventHandler pillReminderEventHandler;


    @Before
    public void setUp() {
        initMocks(this);
        pillReminderEventHandler = new ReminderEventHandler(outboundEventGateway, allPillRegimens, pillRegimenTime);
    }

    @Test
    public void shouldRaiseEventsForEachPillWindow() {
        int pillWindow = 0;
        String externalId = "externalId";
        String dosageId = "dosageId";
        int retryInterval = 0;

        Dosage dosage = buildDosage(dosageId);
        PillRegimen pillRegimen = buildPillRegimen(externalId, pillWindow, dosage, retryInterval);

        when(allPillRegimens.findByExternalId(externalId)).thenReturn(pillRegimen);

        MotechEvent motechEvent = buildMotechEvent(externalId, dosageId);
        pillReminderEventHandler.handleEvent(motechEvent);

        verify(allPillRegimens, atLeastOnce()).findByExternalId(externalId);
        verify(outboundEventGateway, only()).sendEventMessage(Matchers.<MotechEvent>any());
    }

    @Test
    public void shouldRaiseEventWithInformationAboutTheNumberOfTimesItHasBeenRaisedIncludingCurrentEvent() {
        int pillWindow = 0;
        int retryInterval = 1;
        String externalId = "externalId";
        String dosageId = "dosageId";
        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        int timesPillReminderSent = 2;

        Dosage dosage = buildDosage(dosageId);
        PillRegimen pillRegimen = buildPillRegimen(externalId, pillWindow, dosage, retryInterval);

        when(pillRegimenTime.timesPillRemindersSent(dosage, pillWindow, retryInterval)).thenReturn(timesPillReminderSent);
        when(allPillRegimens.findByExternalId(externalId)).thenReturn(pillRegimen);

        MotechEvent motechEvent = buildMotechEvent(externalId, dosageId);
        pillReminderEventHandler.handleEvent(motechEvent);

        verify(allPillRegimens, atLeastOnce()).findByExternalId(externalId);
        verify(outboundEventGateway, times(1)).sendEventMessage(event.capture());

        assertNotNull(event.getValue().getParameters());
        assertEquals(timesPillReminderSent, event.getValue().getParameters().get(EventKeys.PILLREMINDER_TIMES_SENT));
    }

    @Test
    public void shouldRaiseEventWithInformationAboutTheNumberOfTimesItWillBeRaisedForEveryPillWindow() {
        int pillWindow = 0;
        String externalId = "externalId";
        String dosageId = "dosageId";
        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        int timesToBeSent = 2;
        int retryInterval = 4;

        Dosage dosage = buildDosage(dosageId);
        PillRegimen pillRegimen = buildPillRegimen(externalId, pillWindow, dosage, retryInterval);

        when(pillRegimenTime.timesPillRemainderWillBeSent(pillWindow, retryInterval)).thenReturn(timesToBeSent);
        when(allPillRegimens.findByExternalId(externalId)).thenReturn(pillRegimen);

        MotechEvent motechEvent = buildMotechEvent(externalId, dosageId);
        pillReminderEventHandler.handleEvent(motechEvent);

        verify(allPillRegimens, atLeastOnce()).findByExternalId(externalId);
        verify(pillRegimenTime, atLeastOnce()).timesPillRemainderWillBeSent(pillWindow, retryInterval);
        verify(outboundEventGateway, times(1)).sendEventMessage(event.capture());

        assertNotNull(event.getValue().getParameters());
        assertEquals(timesToBeSent, event.getValue().getParameters().get(EventKeys.PILLREMINDER_TIMES_TO_BE_SENT));
    }


    @Test
    @Ignore
    public void shoulNotRaiseEventWhenDosageIsAlreadyTakenForCurrentPillWindow() {
    }

    private MotechEvent buildMotechEvent(String externalId, String dosageId) {
        HashMap eventParams = new SchedulerPayloadBuilder().withDosageId(dosageId).withExternalId(externalId).withJobId(dosageId).payload();
        return new MotechEvent(EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT, eventParams);
    }

    private Dosage buildDosage(String dosageId) {
        return org.motechproject.server.pillreminder.builder.test.DosageBuilder.newDosage()
                .withCurrentDosageDate(new Date())
                .withId(dosageId)
                .build();
    }

    private PillRegimen buildPillRegimen(String externalId, int pillWindow, Dosage dosage, int retryInterval) {
        return org.motechproject.server.pillreminder.builder.test.PillRegimenBuilder.newPillRegimen()
                .withExternalId(externalId)
                .withReminderRepeatWindowInHours(pillWindow)
                .withReminderRepeatIntervalInMinutes(retryInterval)
                .withSingleDosage(dosage)
                .build();
    }
}

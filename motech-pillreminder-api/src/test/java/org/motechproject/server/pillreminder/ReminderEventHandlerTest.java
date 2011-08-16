package org.motechproject.server.pillreminder;


import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.HashMap;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatcher;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.RepeatingSchedulableJob;
import org.motechproject.model.Time;
import org.motechproject.scheduler.MotechSchedulerService;
import org.motechproject.server.pillreminder.builder.SchedulerPayloadBuilder;
import org.motechproject.server.pillreminder.builder.testbuilder.DosageBuilder;
import org.motechproject.server.pillreminder.builder.testbuilder.PillRegimenBuilder;
import org.motechproject.server.pillreminder.dao.AllPillRegimens;
import org.motechproject.server.pillreminder.domain.Dosage;
import org.motechproject.server.pillreminder.domain.PillRegimen;
import org.motechproject.server.pillreminder.util.PillReminderTimeUtils;
import org.motechproject.util.DateUtil;

public class ReminderEventHandlerTest {

    @Mock
    private OutboundEventGateway outboundEventGateway;

    @Mock
    private AllPillRegimens allPillRegimens;

    @Mock
    private PillReminderTimeUtils pillRegimenTimeUtils;

    @Mock
    private MotechSchedulerService schedulerService;

    private ReminderEventHandler pillReminderEventHandler;


    @Before
    public void setUp() {
        initMocks(this);
        pillReminderEventHandler = new ReminderEventHandler(outboundEventGateway, allPillRegimens, pillRegimenTimeUtils, schedulerService);
    }

    @Test
    public void shouldRaiseEventsForEachPillWindow() {
        int pillWindow = 0;
        String externalId = "externalId";
        String dosageId = "dosageId";
        int retryInterval = 0;

        Dosage dosage = buildDosageNotYetTaken(dosageId);
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

        Dosage dosage = buildDosageNotYetTaken(dosageId);
        PillRegimen pillRegimen = buildPillRegimen(externalId, pillWindow, dosage, retryInterval);

        when(pillRegimenTimeUtils.timesPillRemindersSent(dosage, pillWindow, retryInterval)).thenReturn(timesPillReminderSent);
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

        Dosage dosage = buildDosageNotYetTaken(dosageId);
        PillRegimen pillRegimen = buildPillRegimen(externalId, pillWindow, dosage, retryInterval);

        when(pillRegimenTimeUtils.timesPillRemainderWillBeSent(pillWindow, retryInterval)).thenReturn(timesToBeSent);
        when(allPillRegimens.findByExternalId(externalId)).thenReturn(pillRegimen);

        MotechEvent motechEvent = buildMotechEvent(externalId, dosageId);
        pillReminderEventHandler.handleEvent(motechEvent);

        verify(allPillRegimens, atLeastOnce()).findByExternalId(externalId);
        verify(pillRegimenTimeUtils, atLeastOnce()).timesPillRemainderWillBeSent(pillWindow, retryInterval);
        verify(outboundEventGateway, times(1)).sendEventMessage(event.capture());

        assertNotNull(event.getValue().getParameters());
        assertEquals(timesToBeSent, event.getValue().getParameters().get(EventKeys.PILLREMINDER_TOTAL_TIMES_TO_SEND));
    }

    @Test
    public void shoulNotRaiseEventWhenDosageIsAlreadyTakenForCurrentPillWindow() {
        int pillWindow = 0;
        String externalId = "externalId";
        String dosageId = "dosageId";
        int retryInterval = 4;

        Dosage dosage = buildDosageTaken(dosageId);

        PillRegimen pillRegimen = buildPillRegimen(externalId, pillWindow, dosage, retryInterval);

        when(allPillRegimens.findByExternalId(externalId)).thenReturn(pillRegimen);

        MotechEvent motechEvent = buildMotechEvent(externalId, dosageId);
        pillReminderEventHandler.handleEvent(motechEvent);

        verify(allPillRegimens, atLeastOnce()).findByExternalId(externalId);
        verify(outboundEventGateway, never()).sendEventMessage(Matchers.<MotechEvent>any());
    }
    
    @Test
    public void shouldScheduleRepeatRemindersForFirstCall() {
        int pillWindow = 0;
        String externalId = "externalId";
        String dosageId = "dosageId";
        int retryInterval = 4;

        Dosage dosage = buildDosageNotYetTaken(dosageId);

        PillRegimen pillRegimen = buildPillRegimen(externalId, pillWindow, dosage, retryInterval);

        when(allPillRegimens.findByExternalId(externalId)).thenReturn(pillRegimen);

        MotechEvent motechEvent = buildMotechEvent(externalId, dosageId);
        pillReminderEventHandler.handleEvent(motechEvent);
        
        verify(schedulerService).scheduleRepeatingJob(argThat(new RepeatingSchedulableJobArgumentMatcher(new Time(10, 25+  retryInterval))));
    }

    @Test
    public void shouldNotScheduleRepeatRemindersForReminderCalls() {
        int pillWindow = 1;
        String externalId = "externalId";
        String dosageId = "dosageId";
        int retryInterval = 4;

        Dosage dosage = buildDosageNotYetTaken(dosageId);

        PillRegimen pillRegimen = buildPillRegimen(externalId, pillWindow, dosage, retryInterval);

        when(allPillRegimens.findByExternalId(externalId)).thenReturn(pillRegimen);

        MotechEvent motechEvent = buildMotechEvent(externalId, dosageId);

        when(pillRegimenTimeUtils.timesPillRemindersSent(dosage, pillWindow, retryInterval)).thenReturn(1);
        
        pillReminderEventHandler.handleEvent(motechEvent);
        
        verify(schedulerService, never()).scheduleRepeatingJob(Matchers.<RepeatingSchedulableJob>any());
    }

    private class RepeatingSchedulableJobArgumentMatcher extends ArgumentMatcher<RepeatingSchedulableJob> {
   	
    	Time time;
    	
        public RepeatingSchedulableJobArgumentMatcher(Time dosageTime) {
        	this.time = dosageTime;
		}

		@Override
        public boolean matches(Object o) {
        	RepeatingSchedulableJob job = (RepeatingSchedulableJob)o;
        	return job.getStartTime().getHours() == time.getHour() 
        			&& job.getStartTime().getMinutes() == time.getMinute();
        }
    }
    
    private MotechEvent buildMotechEvent(String externalId, String dosageId) {
        HashMap eventParams = new SchedulerPayloadBuilder().withDosageId(dosageId).withExternalId(externalId).withJobId(dosageId).payload();
        return new MotechEvent(EventKeys.PILLREMINDER_REMINDER_EVENT_SUBJECT, eventParams);
    }

    private Dosage buildDosageNotYetTaken(String dosageId) {
        return DosageBuilder.newDosage()
        		.withDosageTime(new Time(10, 25))
                .withResponseLastCapturedDate(DateUtil.today().minusDays(1))
                .withId(dosageId)
                .build();
    }

    private Dosage buildDosageTaken(String dosageId) {
        return DosageBuilder.newDosage()
        		.withDosageTime(new Time(10, 25))
                .withResponseLastCapturedDate(DateUtil.today())
                .withId(dosageId)
                .build();
    }

    private PillRegimen buildPillRegimen(String externalId, int pillWindow, Dosage dosage, int retryInterval) {
        return PillRegimenBuilder.newPillRegimen()
                .withExternalId(externalId)
                .withReminderRepeatWindowInHours(pillWindow)
                .withReminderRepeatIntervalInMinutes(retryInterval)
                .withSingleDosage(dosage)
                .build();
    }
}

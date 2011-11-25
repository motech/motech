package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.messagecampaign.BaseUnitTest;
import org.motechproject.server.messagecampaign.builder.CampaignMessageBuilder;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.messagecampaign.EventKeys.*;
import static org.motechproject.server.messagecampaign.scheduler.RepeatingProgramScheduleHandler.OFFSET;
import static org.motechproject.server.messagecampaign.scheduler.RepeatingProgramScheduleHandler.WEEK_DAY;
import static org.motechproject.server.messagecampaign.scheduler.RepeatingProgramScheduler.INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT;

public class RepeatingProgramScheduleHandlerTest extends BaseUnitTest {

    @Mock
    AllMessageCampaigns allMessageCampaigns;
    @Mock
    OutboundEventGateway outboundEventGateway;
    RepeatingProgramScheduleHandler handler;

    String campaignName = "campaign-name";
    private final String jobId = "job-id";
    private final String externalId = "external-id";

    @Before
    public void setUp() {
        initMocks(this);
        handler = new RepeatingProgramScheduleHandler(outboundEventGateway, allMessageCampaigns);
    }

    @Test
    public void shouldHandleEventForRepeatCampaignScheduleAndUpdateMessageKey() {

        String jobMessageKey = "message-key-" + OFFSET;
        CampaignMessage campaignMessage = new CampaignMessageBuilder().repeatingCampaignMessageForInterval(campaignName, "2 Weeks", jobMessageKey);
        int repeatIntervalInDays = ((RepeatingCampaignMessage) campaignMessage).repeatIntervalInDaysForOffset();
        when(allMessageCampaigns.get(campaignName, jobMessageKey)).thenReturn(campaignMessage);

        DateTime today = date(2011, 11, 14);
        Date nov112011 = today.toDate();
        Date may102012 = date(2012, 5, 10).toDate();

        callHandleEvent(today, internalMotechEvent(nov112011, may102012, jobMessageKey, 1));
        assertEventSentToOutboundGateway(campaignMotechEvent(nov112011, may102012, "message-key-1", 1));

        callHandleEvent(today.plusDays(repeatIntervalInDays - 1), internalMotechEvent(nov112011, may102012, jobMessageKey, 3));
        assertEventSentToOutboundGateway(campaignMotechEvent(nov112011, may102012, "message-key-1", 3));

        callHandleEvent(today.plusDays(repeatIntervalInDays * 2), internalMotechEvent(nov112011, may102012, jobMessageKey, 1));
        assertEventSentToOutboundGateway(campaignMotechEvent(nov112011, may102012, "message-key-3", 1));

        callHandleEvent(today.plusDays(repeatIntervalInDays * 2 + 1), internalMotechEvent(nov112011, may102012, jobMessageKey, 4));
        assertEventSentToOutboundGateway(campaignMotechEvent(nov112011, may102012, "message-key-3", 4));

        callHandleEvent(today.plusDays(repeatIntervalInDays * 3), internalMotechEvent(nov112011, may102012, jobMessageKey, 1));
        assertEventSentToOutboundGateway(campaignMotechEvent(nov112011, may102012, "message-key-4", 1));
    }

    @Test
    public void shouldHandleEventForRepeatCampaignScheduleBasedOnWeeksDaysApplicableAndUpdateMessageKey() {

        String jobMessageKey = "message-key-" + OFFSET + "-" + WEEK_DAY;
        CampaignMessage campaignMessage = new CampaignMessageBuilder().repeatingCampaignMessageForDaysApplicable(campaignName,  
                asList("Monday", "Wednesday", "Friday", "Saturday"), jobMessageKey);
        int repeatIntervalAs7 = ((RepeatingCampaignMessage) campaignMessage).repeatIntervalInDaysForOffset();
        when(allMessageCampaigns.get(campaignName, jobMessageKey)).thenReturn(campaignMessage);

        DateTime today = date(2011, 11, 16);
        Date nov162011 = today.toDate();
        Date may102012 = date(2012, 5, 10).toDate();
        assertEquals(7, repeatIntervalAs7);

        callHandleEvent(today, internalMotechEvent(nov162011, may102012, jobMessageKey, 1));
        assertEventSentToOutboundGateway(campaignMotechEvent(nov162011, may102012, "message-key-1-Wednesday", 1));

        callHandleEvent(today.plusDays(1), internalMotechEvent(nov162011, may102012, jobMessageKey, 2));
        assertEventNotSentToOutboundGateway();

        callHandleEvent(today.plusDays(2), internalMotechEvent(nov162011, may102012, jobMessageKey, 2));
        assertEventSentToOutboundGateway(campaignMotechEvent(nov162011, may102012, "message-key-2-Friday", 2));

        callHandleEvent(today.plusDays(3), internalMotechEvent(nov162011, may102012, jobMessageKey, 2));
        assertEventSentToOutboundGateway(campaignMotechEvent(nov162011, may102012, "message-key-2-Saturday", 2));

        callHandleEvent(today.plusDays(11), internalMotechEvent(nov162011, may102012, jobMessageKey, 5));
        assertEventNotSentToOutboundGateway();

        callHandleEvent(today.plusDays(repeatIntervalAs7 * 3), internalMotechEvent(nov162011, may102012, jobMessageKey, 5));
        assertEventSentToOutboundGateway(campaignMotechEvent(nov162011, may102012, "message-key-8-Wednesday", 5));
    }

    @Test
    public void shouldHandleEventForRepeatCampaignScheduleBasedOnCalendarWeeksDaysApplicableAndUpdateMessageKey() {

        String jobMessageKey = "message-key-" + OFFSET + "-" + WEEK_DAY;
        CampaignMessage campaignMessage = new CampaignMessageBuilder().repeatingCampaignMessageForCalendarWeek(campaignName,
                "Monday", asList("Monday", "Friday", "Sunday"), jobMessageKey);
        int repeatIntervalAs7 = ((RepeatingCampaignMessage) campaignMessage).repeatIntervalInDaysForOffset();
        when(allMessageCampaigns.get(campaignName, jobMessageKey)).thenReturn(campaignMessage);

        DateTime today = date(2011, 11, 18);
        Date nov182011 = today.toDate();
        Date may102012 = date(2012, 5, 10).toDate();
        assertEquals(7, repeatIntervalAs7);

        callHandleEvent(today, internalMotechEvent(nov182011, may102012, jobMessageKey, 1));
        assertEventSentToOutboundGateway(campaignMotechEvent(nov182011, may102012, "message-key-1-Friday", 1));

        callHandleEvent(today.plusDays(1), internalMotechEvent(nov182011, may102012, jobMessageKey, 1));
        assertEventNotSentToOutboundGateway();

        callHandleEvent(today.plusDays(2), internalMotechEvent(nov182011, may102012, jobMessageKey, 3));
        assertEventSentToOutboundGateway(campaignMotechEvent(nov182011, may102012, "message-key-3-Sunday", 3));

        callHandleEvent(today.plusDays(3), internalMotechEvent(nov182011, may102012, jobMessageKey, 4));
        assertEventSentToOutboundGateway(campaignMotechEvent(nov182011, may102012, "message-key-5-Monday", 4));

        callHandleEvent(today.plusDays(repeatIntervalAs7 * 4), internalMotechEvent(nov182011, may102012, jobMessageKey, 1));
        assertEventSentToOutboundGateway(campaignMotechEvent(nov182011, may102012, "message-key-5-Friday", 1));

        DateTime todayMockAs29Nov = today.plusDays(11);
        callHandleEvent(todayMockAs29Nov, internalMotechEvent(nov182011, may102012, jobMessageKey, 1));
        assertEventNotSentToOutboundGateway();

        callHandleEvent(todayMockAs29Nov.plusDays(3), internalMotechEvent(nov182011, may102012, jobMessageKey, 2));
        assertEventSentToOutboundGateway(campaignMotechEvent(nov182011, may102012, "message-key-4-Friday", 2));
    }

    @Test
    public void shouldReturnTrueIfTheEventIsTheLastEvent() {

        String jobMessageKey = "message-key-" + OFFSET + "-" + WEEK_DAY;
        CampaignMessage campaignMessage = new CampaignMessageBuilder().repeatingCampaignMessageForCalendarWeek(campaignName, "Monday", asList("Monday", "Friday", "Sunday"), jobMessageKey);
        when(allMessageCampaigns.get(campaignName, jobMessageKey)).thenReturn(campaignMessage);

        DateTime today = date(2011, 11, 18);
        Date endDate = today.plus(34).toDate();
        callHandleEvent(today, internalMotechEvent(today.toDate(), endDate, jobMessageKey, 1).setLastEvent(true));
        assertEventSentToOutboundGateway(campaignMotechEvent(today.toDate(), endDate, "message-key-1-Friday", 1).setLastEvent(true));
    }

    private void callHandleEvent(DateTime todayMockTime, MotechEvent inputEvent) {
        reset(outboundEventGateway);
        mockCurrentDate(todayMockTime);
        handler.handleEvent(inputEvent);
    }

    private void assertEventSentToOutboundGateway(MotechEvent expectedEvent) {
        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(outboundEventGateway, times(1)).sendEventMessage(eventCaptor.capture());
        assertMotechEvent(expectedEvent, eventCaptor.getValue());
    }

    private void assertEventNotSentToOutboundGateway() {
        verify(outboundEventGateway, never()).sendEventMessage(Matchers.<MotechEvent>any());
    }

    private void assertMotechEvent(MotechEvent expectedEvent, MotechEvent actual) {

        assertEquals(expectedEvent.getSubject(), actual.getSubject());
        assertEquals(expectedEvent.getEndTime(), actual.getEndTime());

        Map<String,Object> actualParams = actual.getParameters();
        Map<String,Object> expectedParams = expectedEvent.getParameters();
        assertEquals(expectedParams.get(START_DATE), actualParams.get(START_DATE));
        assertEquals(expectedParams.get(REPEATING_START_OFFSET), actualParams.get(REPEATING_START_OFFSET));
        assertEquals(expectedParams.get(MESSAGE_KEY), actualParams.get(MESSAGE_KEY));

        assertEquals(campaignName, actualParams.get(CAMPAIGN_NAME_KEY));
        assertEquals(jobId, actualParams.get(SCHEDULE_JOB_ID_KEY));
        assertEquals(externalId, actualParams.get(EXTERNAL_ID_KEY));
        assertEquals(expectedParams, actualParams);
    }

    private MotechEvent internalMotechEvent(Date startTime, Date endTime, String messageKey, int startOffset) {
        return event(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, startTime, endTime, messageKey, startOffset);
    }

    private MotechEvent campaignMotechEvent(Date startTime, Date endTime, String messageKey, int startOffset) {
        return event(MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, startTime, endTime, messageKey, startOffset);
    }

    private MotechEvent event(String subject, Date startTime, Date endTime, String messageKey, int startOffset) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(MESSAGE_KEY, messageKey);
        parameters.put(REPEATING_START_OFFSET, startOffset);
        parameters.put(START_DATE, startTime);

        parameters.put(CAMPAIGN_NAME_KEY, campaignName);
        parameters.put(SCHEDULE_JOB_ID_KEY, jobId);
        parameters.put(EXTERNAL_ID_KEY, externalId);
        return new MotechEvent(subject, parameters).setEndTime(endTime);
    }
}

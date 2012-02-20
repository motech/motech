package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.Constants;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.CampaignMessageBuilder;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentService;
import org.motechproject.testing.utils.BaseUnitTest;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.motechproject.server.messagecampaign.scheduler.RepeatingProgramScheduleHandler.OFFSET;
import static org.motechproject.server.messagecampaign.scheduler.RepeatingProgramScheduleHandler.WEEK_DAY;
import static org.motechproject.server.messagecampaign.scheduler.RepeatingProgramScheduler.INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT;

public class RepeatingProgramScheduleHandlerTest extends BaseUnitTest {
    @Mock
    AllMessageCampaigns allMessageCampaigns;
    @Mock
    CampaignEnrollmentService mockCampaignEnrollmentService;
    @Mock
    OutboundEventGateway outboundEventGateway;
    RepeatingProgramScheduleHandler handler;

    String campaignName = "campaign-name";
    private String externalId = "external-id";

    @Before
    public void setUp() {
        initMocks(this);
        handler = new RepeatingProgramScheduleHandler(outboundEventGateway, allMessageCampaigns, mockCampaignEnrollmentService);
    }

    @Test
    public void shouldHandleEventForRepeatCampaignScheduleAndUpdateMessageKey() {

        String jobMessageKey = "message-key-" + OFFSET;
        CampaignMessage campaignMessage = new CampaignMessageBuilder().repeatingCampaignMessageForInterval(campaignName, "2 Weeks", jobMessageKey).deliverTime(new Time(10, 30));
        int repeatIntervalInDays = ((RepeatingCampaignMessage) campaignMessage).repeatIntervalInDaysForOffset();
        when(allMessageCampaigns.get(campaignName, jobMessageKey)).thenReturn(campaignMessage);

        DateTime today = date(2011, 11, 14);
        Date startDateNov112011 = today.toDate();
        Date may102012 = date(2012, 5, 10).toDate();

        mockCampaignEnrollment(startDateNov112011, 1);
        callHandleEvent(today, motechEvent(may102012, jobMessageKey, true));
        assertHandleEvent("message-key-1");

        mockCampaignEnrollment(startDateNov112011, 3);
        callHandleEvent(today.plusDays(repeatIntervalInDays - 1), motechEvent(may102012, jobMessageKey, true));
        assertHandleEvent("message-key-1");

        mockCampaignEnrollment(startDateNov112011, 1);
        callHandleEvent(today.plusDays(repeatIntervalInDays * 2), motechEvent(may102012, jobMessageKey, true));
        assertHandleEvent("message-key-3");
    }

    private CampaignEnrollment mockCampaignEnrollment(Date startDate, int startOffset) {
        reset(mockCampaignEnrollmentService);
        CampaignEnrollment enrollment = new CampaignEnrollment(externalId, campaignName).setStartDate(new LocalDate(startDate));
        when(mockCampaignEnrollmentService.findByExternalIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment
                .setStartOffset(startOffset));
        return enrollment;
    }

    @Test
    public void shouldHandleEventForRepeatCampaignScheduleBasedOnWeeksDaysApplicableAndUpdateMessageKey() {

        String jobMessageKey = "message-key-" + OFFSET + "-" + WEEK_DAY;
        CampaignMessage campaignMessage = new CampaignMessageBuilder().repeatingCampaignMessageForDaysApplicable(campaignName,
                asList("Monday", "Wednesday", "Friday", "Saturday"), jobMessageKey);
        int repeatIntervalAs7 = ((RepeatingCampaignMessage) campaignMessage).repeatIntervalInDaysForOffset();
        when(allMessageCampaigns.get(campaignName, jobMessageKey)).thenReturn(campaignMessage);

        DateTime today = date(2011, 11, 16);
        Date startDateNov162011 = today.toDate();
        Date may102012 = date(2012, 5, 10).toDate();
        assertEquals(7, repeatIntervalAs7);

        mockCampaignEnrollment(startDateNov162011, 1);
        callHandleEvent(today, motechEvent(may102012, jobMessageKey, true));
        assertHandleEvent("message-key-1-Wednesday");

        mockCampaignEnrollment(startDateNov162011, 2);
        callHandleEvent(today.plusDays(1), motechEvent(may102012, jobMessageKey, true));
        assertHandleEvent_ThatItDoesntProceed();

        mockCampaignEnrollment(startDateNov162011, 2);
        callHandleEvent(today.plusDays(2), motechEvent(may102012, jobMessageKey, true));
        assertHandleEvent("message-key-2-Friday");

        mockCampaignEnrollment(startDateNov162011, 5);
        callHandleEvent(today.plusDays(11), motechEvent(may102012, jobMessageKey, true));
        assertHandleEvent_ThatItDoesntProceed();

        mockCampaignEnrollment(startDateNov162011, 5);
        callHandleEvent(today.plusDays(repeatIntervalAs7 * 3), motechEvent(may102012, jobMessageKey, true));
        assertHandleEvent("message-key-8-Wednesday");
    }

    @Test
    public void shouldHandleEventForRepeatCampaignScheduleIfTheMessageDispatchStrategyIsNot24Hours() {
        DateTime today = date(2011, 11, 16);
        Date currentDate = date(2012, 5, 10).toDate();
        String jobMessageKey = "message-key-" + OFFSET + "-" + WEEK_DAY;
        CampaignMessage campaignMessage = new CampaignMessageBuilder().repeatingCampaignMessageForDaysApplicable(campaignName,
                asList("Monday", "Wednesday", "Friday", "Saturday"), jobMessageKey);

        when(allMessageCampaigns.get(campaignName, jobMessageKey)).thenReturn(campaignMessage);
        mockCampaignEnrollment(today.toDate(), 1);

        callHandleEvent(today, motechEvent(currentDate, jobMessageKey, false));
        assertHandleEvent("message-key-1-Wednesday");
    }

    @Test
    public void shouldHandleEventForRepeatCampaignScheduleBasedOnCalendarWeeksDaysApplicableAndUpdateMessageKey() {

        String jobMessageKey = "message-key-" + OFFSET + "-" + WEEK_DAY;
        CampaignMessage campaignMessage = new CampaignMessageBuilder().repeatingCampaignMessageForCalendarWeek(campaignName,
                "Monday", asList("Monday", "Friday", "Sunday"), jobMessageKey);
        int repeatIntervalAs7 = ((RepeatingCampaignMessage) campaignMessage).repeatIntervalInDaysForOffset();
        when(allMessageCampaigns.get(campaignName, jobMessageKey)).thenReturn(campaignMessage);

        DateTime today = date(2011, 11, 18);
        Date startDateNov182011 = today.toDate();
        Date may102012 = date(2012, 5, 10).toDate();
        assertEquals(7, repeatIntervalAs7);

        mockCampaignEnrollment(startDateNov182011, 1);
        callHandleEvent(today, motechEvent(may102012, jobMessageKey, true));
        assertHandleEvent("message-key-1-Friday");

        mockCampaignEnrollment(startDateNov182011, 1);
        callHandleEvent(today.plusDays(1), motechEvent(may102012, jobMessageKey, true));
        assertHandleEvent_ThatItDoesntProceed();

        mockCampaignEnrollment(startDateNov182011, 3);
        callHandleEvent(today.plusDays(2), motechEvent(may102012, jobMessageKey, true));
        assertHandleEvent("message-key-3-Sunday");

        mockCampaignEnrollment(startDateNov182011, 4);
        callHandleEvent(today.plusDays(3), motechEvent(may102012, jobMessageKey, true));
        assertHandleEvent("message-key-5-Monday");

        mockCampaignEnrollment(startDateNov182011, 1);
        callHandleEvent(today.plusDays(11), motechEvent(may102012, jobMessageKey, true));
        assertHandleEvent_ThatItDoesntProceed();

        mockCampaignEnrollment(startDateNov182011, 2);
        callHandleEvent(today.plusDays(14), motechEvent(may102012, jobMessageKey, true));
        assertHandleEvent("message-key-4-Friday");
    }

    @Test
    public void shouldNotSetLastEventIfAlreadySet() {

        DateTime today = date(2011, 11, 18);
        Date may102012 = date(2012, 5, 10).toDate();

        String jobMessageKey = "msgKey";
        RepeatingCampaignMessage campaignMessage = new CampaignMessageBuilder().repeatingCampaignMessageForCalendarWeek(campaignName,
                "Monday", asList("Monday", "Friday"), jobMessageKey);
        campaignMessage = spy(campaignMessage);
        doReturn(false).when(campaignMessage).hasEnded(Matchers.<Date>any());
        doReturn("Monday").when(campaignMessage).applicableWeekDayInNext24Hours();

        CampaignEnrollment enrollment = new CampaignEnrollment("", campaignName).setStartDate(today.toLocalDate());
        when(mockCampaignEnrollmentService.findByExternalIdAndCampaignName(Matchers.<String>any(), Matchers.<String>any())).thenReturn(enrollment);
        when(allMessageCampaigns.get(campaignName, jobMessageKey)).thenReturn(campaignMessage);

        callHandleEvent(today, motechEvent(may102012, jobMessageKey, true).setLastEvent(true));
        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(outboundEventGateway, times(1)).sendEventMessage(event.capture());
        assertTrue(event.getValue().isLastEvent());
    }

    @Test
    public void shouldSetLastEventIfNotAlreadySet() {

        DateTime today = date(2011, 11, 18);
        Date may102012 = date(2012, 5, 10).toDate();

        String jobMessageKey = "msgKey";
        RepeatingCampaignMessage campaignMessage = new CampaignMessageBuilder().repeatingCampaignMessageForInterval(campaignName, "2 Weeks", jobMessageKey);
        campaignMessage = spy(campaignMessage);
        doReturn(true).when(campaignMessage).hasEnded(Matchers.<Date>any());
        doReturn("Monday").when(campaignMessage).applicableWeekDayInNext24Hours();

        CampaignEnrollment enrollment = new CampaignEnrollment("", campaignName).setStartDate(today.toLocalDate());
        when(mockCampaignEnrollmentService.findByExternalIdAndCampaignName(Matchers.<String>any(), Matchers.<String>any())).thenReturn(enrollment);
        when(allMessageCampaigns.get(campaignName, jobMessageKey)).thenReturn(campaignMessage);

        callHandleEvent(today, motechEvent(may102012, jobMessageKey, true).setLastEvent(false));
        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        verify(outboundEventGateway, times(1)).sendEventMessage(event.capture());
        assertTrue(event.getValue().isLastEvent());
    }

    private void callHandleEvent(DateTime todayMockTime, MotechEvent inputEvent) {
        reset(outboundEventGateway);
        mockCurrentDate(todayMockTime);
        handler.handleEvent(inputEvent);
    }

    private void assertHandleEvent(String expectedMsgKey) {
        assertHandleEvent(expectedMsgKey, true);
    }

    private void assertHandleEvent_ThatItDoesntProceed() {
        assertHandleEvent(null, false);
    }

    private void assertHandleEvent(String expectedMsgKey, boolean shouldFireToEventGateway) {
        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);
        if (shouldFireToEventGateway) {
            verify(outboundEventGateway, times(1)).sendEventMessage(event.capture());
            assertOffsetForMessage(event, expectedMsgKey);
        } else
            verify(outboundEventGateway, never()).sendEventMessage(event.capture());
    }

    private void assertOffsetForMessage(ArgumentCaptor<MotechEvent> event, String messageKey) {
        Map<String, Object> params = event.getValue().getParameters();
        assertNotNull(params);
        assertEquals(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, event.getValue().getSubject());
        assertEquals(campaignName, params.get(EventKeys.CAMPAIGN_NAME_KEY));
        assertEquals("job-id", params.get(EventKeys.SCHEDULE_JOB_ID_KEY));
        assertEquals("external-id", params.get(EventKeys.EXTERNAL_ID_KEY));
        assertEquals(messageKey, params.get(EventKeys.MESSAGE_KEY));
    }

    private MotechEvent motechEvent(Date endTime, String messageKey, Boolean strategy) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.CAMPAIGN_NAME_KEY, campaignName);
        parameters.put(EventKeys.MESSAGE_KEY, messageKey);
        parameters.put(EventKeys.SCHEDULE_JOB_ID_KEY, "job-id");
        parameters.put(EventKeys.EXTERNAL_ID_KEY, externalId);
        parameters.put(Constants.REPEATING_PROGRAM_24HRS_MESSAGE_DISPATCH_STRATEGY, strategy);
        return new MotechEvent(INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, parameters).setEndTime(endTime);
    }
}

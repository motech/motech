package org.motechproject.server.messagecampaign.scheduler;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.gateway.OutboundEventGateway;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.builder.CampaignMessageBuilder;
import org.motechproject.server.messagecampaign.dao.AllMessageCampaigns;
import org.motechproject.server.messagecampaign.domain.message.CampaignMessage;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;
import org.motechproject.util.DateTimeSourceUtil;
import org.motechproject.util.datetime.DateTimeSource;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class RepeatingProgramScheduleHandlerTest {

    @Mock
    AllMessageCampaigns allMessageCampaigns;
    @Mock
    OutboundEventGateway outboundEventGateway;
    RepeatingProgramScheduleHandler handler;

    String campaignName = "campaign-name";
    String messageKey = "message-key-" + RepeatingProgramScheduleHandler.OFFSET;

    @Before
    public void setUp() {
        initMocks(this);
        handler = new RepeatingProgramScheduleHandler(outboundEventGateway, allMessageCampaigns);
    }

    @Test
    public void shouldHandleEventForRepeatCampaignScheduleAndUpdateMessageKey() {

        CampaignMessage campaignMessage = new CampaignMessageBuilder().repeatingCampaignMessage(campaignName, "2 Weeks", messageKey);
        int repeatIntervalInDays = ((RepeatingCampaignMessage) campaignMessage).repeatIntervalInDays();
        when(allMessageCampaigns.get(campaignName, messageKey)).thenReturn(campaignMessage);

        DateTime today = date(2011, 11, 14);
        DateTime nov112011 = today;
        DateTime may102012 = date(2012, 5, 10);

        handleEventAndAssert(today, nov112011, may102012, "message-key-1");
        handleEventAndAssert(today.plusDays(repeatIntervalInDays - 1), nov112011, may102012, "message-key-1");
        handleEventAndAssert(today.plusDays(repeatIntervalInDays * 2), nov112011, may102012, "message-key-3");
        handleEventAndAssert(today.plusDays(repeatIntervalInDays * 2 + 1),nov112011, may102012, "message-key-3");
        handleEventAndAssert(today.plusDays(repeatIntervalInDays * 3), nov112011, may102012, "message-key-4");
    }

    private void handleEventAndAssert(DateTime todayMockTime, DateTime startTime, DateTime endTime, String expectedMsgKey) {

        reset(outboundEventGateway);

        mockCurrentDate(todayMockTime);
        ArgumentCaptor<MotechEvent> event = ArgumentCaptor.forClass(MotechEvent.class);

        handler.handleEvent(getMotechEvent(startTime.toDate(), endTime.toDate(), messageKey));
        verify(outboundEventGateway, times(1)).sendEventMessage(event.capture());
        assertOffsetForMessage(event, expectedMsgKey);
    }

    private void assertOffsetForMessage(ArgumentCaptor<MotechEvent> event, String messageKey) {
        Map<String,Object> params = event.getValue().getParameters();
        assertNotNull(params);
        assertEquals(EventKeys.MESSAGE_CAMPAIGN_SEND_EVENT_SUBJECT, event.getValue().getSubject());
        assertEquals(campaignName, params.get(EventKeys.CAMPAIGN_NAME_KEY));
        assertEquals("job-id", params.get(EventKeys.SCHEDULE_JOB_ID_KEY));
        assertEquals("external-id", params.get(EventKeys.EXTERNAL_ID_KEY));
        assertEquals(messageKey, params.get(EventKeys.MESSAGE_KEY));
    }

    private MotechEvent getMotechEvent(Date startTime, Date endTime, String messageKey) {
        HashMap<String, Object> parameters = new HashMap<String, Object>();
        parameters.put(EventKeys.CAMPAIGN_NAME_KEY, campaignName);
        parameters.put(EventKeys.MESSAGE_KEY, messageKey);
        parameters.put(EventKeys.SCHEDULE_JOB_ID_KEY, "job-id");
        parameters.put(EventKeys.EXTERNAL_ID_KEY, "external-id");
        return new MotechEvent(RepeatingProgramScheduler.INTERNAL_REPEATING_MESSAGE_CAMPAIGN_SUBJECT, parameters)
                .setStartTime(startTime).setEndTime(endTime);
    }

    private void mockCurrentDate(final DateTime currentDate) {
        DateTimeSourceUtil.SourceInstance = new DateTimeSource() {

            @Override
            public DateTimeZone timeZone() {
                return currentDate.getZone();
            }

            @Override
            public DateTime now() {
                return currentDate;
            }

            @Override
            public LocalDate today() {
                return currentDate.toLocalDate();
            }
        };
    }

    private DateTime date(int year, int monthOfYear, int dayOfMonth) {
           return new DateTime(year, monthOfYear, dayOfMonth, 0, 0);
    }
}

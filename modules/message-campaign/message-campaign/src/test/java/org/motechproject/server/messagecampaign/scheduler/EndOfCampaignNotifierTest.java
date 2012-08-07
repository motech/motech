package org.motechproject.server.messagecampaign.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.scheduler.domain.MotechEvent;
import org.motechproject.scheduler.gateway.OutboundEventGateway;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.quartz.SchedulerException;
import org.quartz.Trigger;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EndOfCampaignNotifierTest {

    EndOfCampaignNotifier endOfCampaignNotifier;

    @Mock
    private OutboundEventGateway outboundEventGateway;
    @Mock
    private AllCampaignEnrollments allCampaignEnrollments;

    @Before
    public void setup() {
        initMocks(this);
        endOfCampaignNotifier = new EndOfCampaignNotifier(allCampaignEnrollments, outboundEventGateway);
    }

    @Test
    public void shouldRaiseEndOfCampaignEvent_WhenThisIsTheLastTrigger() throws SchedulerException {
        Trigger trigger = mock(Trigger.class);
        when(trigger.getNextFireTime()).thenReturn(null);

        when(allCampaignEnrollments.findByExternalIdAndCampaignName("123abc", "campaign")).thenReturn(new CampaignEnrollment("123abc", "campaign"));

        Map<String, Object> params = new HashMap<>();
        params.put(EventKeys.EXTERNAL_ID_KEY, "123abc");
        params.put(EventKeys.CAMPAIGN_NAME_KEY, "campaign");
        params.put(EventKeys.MESSAGE_KEY, "message");
        MotechEvent event = new MotechEvent(EventKeys.SEND_MESSAGE, params).setLastEvent(true);
        endOfCampaignNotifier.handle(event);

        ArgumentCaptor<MotechEvent> eventCaptor = ArgumentCaptor.forClass(MotechEvent.class);
        verify(outboundEventGateway).sendEventMessage(eventCaptor.capture());
        MotechEvent raisedEvent = eventCaptor.getValue();

        assertEquals(EventKeys.CAMPAIGN_COMPLETED, raisedEvent.getSubject());
        assertEquals("123abc", raisedEvent.getParameters().get(EventKeys.EXTERNAL_ID_KEY));
        assertEquals("campaign", raisedEvent.getParameters().get(EventKeys.CAMPAIGN_NAME_KEY));
    }

    @Test
    public void shouldMarkEnrollmentAsComplete() throws SchedulerException {
        Trigger trigger = mock(Trigger.class);
        when(trigger.getNextFireTime()).thenReturn(null);

        when(allCampaignEnrollments.findByExternalIdAndCampaignName("123abc", "campaign")).thenReturn(new CampaignEnrollment("123abc", "campaign"));

        Map<String, Object> params = new HashMap<>();
        params.put(EventKeys.EXTERNAL_ID_KEY, "123abc");
        params.put(EventKeys.CAMPAIGN_NAME_KEY, "campaign");
        params.put(EventKeys.MESSAGE_KEY, "message");
        MotechEvent event = new MotechEvent(EventKeys.SEND_MESSAGE, params).setLastEvent(true);
        endOfCampaignNotifier.handle(event);

        ArgumentCaptor<CampaignEnrollment> enrollmentCaptor = ArgumentCaptor.forClass(CampaignEnrollment.class);
        verify(allCampaignEnrollments).update(enrollmentCaptor.capture());
        CampaignEnrollment enrollment = enrollmentCaptor.getValue();
        assertEquals("123abc", enrollment.getExternalId());
        assertEquals("campaign", enrollment.getCampaignName());
        assertEquals(CampaignEnrollmentStatus.COMPLETED, enrollment.getStatus());
    }

    @Test
    public void shouldNotRaiseEndOfCampaignEvent_WhenThisIsNotTheLastTrigger() throws SchedulerException {
        Trigger trigger = mock(Trigger.class);
        when(trigger.getNextFireTime()).thenReturn(new Date());

        Map<String, Object> params = new HashMap<>();
        params.put(EventKeys.EXTERNAL_ID_KEY, "123abc");
        params.put(EventKeys.CAMPAIGN_NAME_KEY, "campaign");
        params.put(EventKeys.MESSAGE_KEY, "message");
        MotechEvent event = new MotechEvent(EventKeys.SEND_MESSAGE, params);
        endOfCampaignNotifier.handle(event);

        verify(outboundEventGateway, never()).sendEventMessage(any(MotechEvent.class));
    }
}

package org.motechproject.messagecampaign.handler;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.motechproject.commons.date.model.Time;
import org.motechproject.event.MotechEvent;
import org.motechproject.messagecampaign.EventKeys;
import org.motechproject.messagecampaign.contract.CampaignRequest;
import org.motechproject.messagecampaign.service.MessageCampaignService;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class MessageCampaignEventHandlerTest {

    public static final String CAMPAIGN_NAME = "Campaign Name";
    public static final String EXTERNAL_ID = "12345";
    public static final LocalDate REFERENCE_DATE = LocalDate.now();
    public static final String REFERENCE_TIME = "5:10";
    public static final String START_TIME = "10:40";

    @InjectMocks
    private MessageCampaignEventHandler messageCampaignEventHandler = new MessageCampaignEventHandler();

    @Mock
    private MessageCampaignService messageCampaignService;

    @Before
    public void setup() {
        initMocks(this);
    }

    @Test
    public void shouldEnrollFromEvent() {
        MotechEvent event = new MotechEvent(EventKeys.ENROLL_USER_SUBJECT, createParam());

        messageCampaignEventHandler.enrollOrUnenroll(event);

        ArgumentCaptor<CampaignRequest> requestArgumentCaptor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(messageCampaignService).startFor(requestArgumentCaptor.capture());
        CampaignRequest request = requestArgumentCaptor.getValue();
        assertEquals(EXTERNAL_ID, request.externalId());
        assertEquals(CAMPAIGN_NAME, request.campaignName());
        assertEquals(REFERENCE_DATE, request.referenceDate());
        assertEquals(new Time(REFERENCE_TIME), request.referenceTime());
    }

    @Test
    public void shouldUnenrollFromEvent() {
        MotechEvent event = new MotechEvent(EventKeys.UNENROLL_USER_SUBJECT, createParam());

        messageCampaignEventHandler.enrollOrUnenroll(event);

        ArgumentCaptor<CampaignRequest> requestArgumentCaptor = ArgumentCaptor.forClass(CampaignRequest.class);
        verify(messageCampaignService).stopAll(requestArgumentCaptor.capture());
        CampaignRequest request = requestArgumentCaptor.getValue();
        assertEquals(EXTERNAL_ID, request.externalId());
        assertEquals(CAMPAIGN_NAME, request.campaignName());
        assertEquals(REFERENCE_DATE, request.referenceDate());
        assertEquals(new Time(REFERENCE_TIME), request.referenceTime());
    }

    private Map<String, Object> createParam() {
        Map<String, Object> param = new HashMap<>();
        param.put(EventKeys.EXTERNAL_ID_KEY, EXTERNAL_ID);
        param.put(EventKeys.CAMPAIGN_NAME_KEY, CAMPAIGN_NAME);
        param.put(EventKeys.REFERENCE_DATE, REFERENCE_DATE);
        param.put(EventKeys.REFERENCE_TIME, REFERENCE_TIME);
        param.put(EventKeys.START_TIME, START_TIME);
        return param;
    }
}

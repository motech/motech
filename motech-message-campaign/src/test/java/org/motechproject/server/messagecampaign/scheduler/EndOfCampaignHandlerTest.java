package org.motechproject.server.messagecampaign.scheduler;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.model.MotechEvent;
import org.motechproject.server.messagecampaign.EventKeys;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;

import java.util.HashMap;
import java.util.Map;

import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;

public class EndOfCampaignHandlerTest {

    @Mock
    AllCampaignEnrollments allCampaignEnrollments;

    @Before
    public void setup() {
       initMocks(this);
    }

    @Test
    public void shouldMarkEnrollmentAsComplete() {
        EndOfCampaignHandler handler = new EndOfCampaignHandler(allCampaignEnrollments);

        Map<String, Object> params = new HashMap<String, Object>();
        CampaignEnrollment enrollment = new CampaignEnrollment("entity_1", "foobar");
        params.put(EventKeys.ENROLLMENT_KEY, enrollment);
        MotechEvent event = new MotechEvent(EventKeys.MESSAGE_CAMPAIGN_COMPLETED_EVENT_SUBJECT, params);
        handler.handle(event);

        ArgumentCaptor<CampaignEnrollment> enrollmentCaptor = ArgumentCaptor.forClass(CampaignEnrollment.class);
        verify(allCampaignEnrollments).update(enrollmentCaptor.capture());
        CampaignEnrollment savedEnrollment = enrollmentCaptor.getValue();
        assertEquals(CampaignEnrollmentStatus.COMPLETED, savedEnrollment.getStatus());
    }
}

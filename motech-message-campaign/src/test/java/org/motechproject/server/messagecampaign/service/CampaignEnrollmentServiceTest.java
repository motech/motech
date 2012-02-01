package org.motechproject.server.messagecampaign.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.util.ReflectionTestUtils.setField;

public class CampaignEnrollmentServiceTest {

    @Mock
    AllCampaignEnrollments mockAllCampaignEnrollments;
    CampaignEnrollmentService service;

    @Before
    public void setUp() {
        initMocks(this);
        service = new CampaignEnrollmentService();
        setField(service, "allCampaignEnrollments", mockAllCampaignEnrollments);
    }

    @Test
    public void shouldSaveOrUpdateEnrollment() {
        CampaignEnrollment enrollment = mock(CampaignEnrollment.class);
        service.saveOrUpdate(enrollment);
        verify(mockAllCampaignEnrollments).saveOrUpdate(enrollment);
    }
}

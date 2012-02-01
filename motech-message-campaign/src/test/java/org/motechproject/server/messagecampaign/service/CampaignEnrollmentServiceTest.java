package org.motechproject.server.messagecampaign.service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;
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
    public void shouldSaveOrUpdateEnrollmentOnRegister() {
        CampaignEnrollment enrollment = mock(CampaignEnrollment.class);
        service.register(enrollment);
        verify(mockAllCampaignEnrollments).saveOrUpdate(enrollment);
    }

    @Test
    public void shouldDeleteEnrollmentOnUnregister() {
        CampaignEnrollment enrollment = new CampaignEnrollment("externalId", "cccaaName");
        service.unregister(enrollment);
        verify(mockAllCampaignEnrollments).remove(enrollment);
    }

    @Test
    public void shouldDeleteEnrollmentByUnregisterBasedOnCampaignNameAndExternalId() {

        String externalId = "newExternalId";
        String campaignName = "NewCampaignName";
        CampaignEnrollment enrollment = new CampaignEnrollment(externalId, campaignName);
        when(mockAllCampaignEnrollments.findByExternalIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        service.unregister(enrollment.getExternalId(), enrollment.getCampaignName());

        ArgumentCaptor<CampaignEnrollment> campaignEnrollmentCaptor = ArgumentCaptor.forClass(CampaignEnrollment.class);
        verify(mockAllCampaignEnrollments).remove(campaignEnrollmentCaptor.capture());
        assertThat(campaignEnrollmentCaptor.getValue().getCampaignName(), is(enrollment.getCampaignName()));
        assertThat(campaignEnrollmentCaptor.getValue().getExternalId(), is(enrollment.getExternalId()));
    }
    
    @Test
    public void shouldFindByExternalIdAndCampaignName() {
        String externalId = "externalId";
        String campaignName = "cccaaName";
        service.findByExternalIdAndCampaignName(externalId, campaignName);
        verify(mockAllCampaignEnrollments).findByExternalIdAndCampaignName(externalId, campaignName);
    }

}

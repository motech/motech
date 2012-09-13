package org.motechproject.server.messagecampaign.service;

import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.server.messagecampaign.search.Criterion;

import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
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
    public void shouldSetInactiveStatusForEnrollmentOnUnregister() {
        CampaignEnrollment enrollment = new CampaignEnrollment("externalId", "cccaaName");
        service.unregister(enrollment);
        assertEquals(CampaignEnrollmentStatus.INACTIVE,enrollment.getStatus());
        ArgumentCaptor<CampaignEnrollment> campaignEnrollmentCaptor = ArgumentCaptor.forClass(CampaignEnrollment.class);
        verify(mockAllCampaignEnrollments).saveOrUpdate(campaignEnrollmentCaptor.capture());
        assertThat(campaignEnrollmentCaptor.getValue().getCampaignName(), is(enrollment.getCampaignName()));
        assertThat(campaignEnrollmentCaptor.getValue().getExternalId(), is(enrollment.getExternalId()));
        assertThat(campaignEnrollmentCaptor.getValue().getStatus(), is(CampaignEnrollmentStatus.INACTIVE));
    }

    @Test
    public void shouldSetInactiveStatusForEnrollmentByUnregisterBasedOnCampaignNameAndExternalId() {

        String externalId = "newExternalId";
        String campaignName = "NewCampaignName";
        CampaignEnrollment enrollment = new CampaignEnrollment(externalId, campaignName);
        when(mockAllCampaignEnrollments.findByExternalIdAndCampaignName(externalId, campaignName)).thenReturn(enrollment);

        service.unregister(enrollment.getExternalId(), enrollment.getCampaignName());

        ArgumentCaptor<CampaignEnrollment> campaignEnrollmentCaptor = ArgumentCaptor.forClass(CampaignEnrollment.class);
        verify(mockAllCampaignEnrollments).saveOrUpdate(campaignEnrollmentCaptor.capture());
        assertThat(campaignEnrollmentCaptor.getValue().getCampaignName(), is(enrollment.getCampaignName()));
        assertThat(campaignEnrollmentCaptor.getValue().getExternalId(), is(enrollment.getExternalId()));
        assertThat(campaignEnrollmentCaptor.getValue().getStatus(), is(CampaignEnrollmentStatus.INACTIVE));
    }
    
    @Test
    public void shouldSearchTheCampaignEnrollmentsBasedOnTheGivenQuery() {
        Criterion primaryCriterion = mock(Criterion.class);
        List<CampaignEnrollment> primaryCriterionFilteredEnrollments = mock(List.class);
        when(primaryCriterion.fetch(mockAllCampaignEnrollments)).thenReturn(primaryCriterionFilteredEnrollments);

        Criterion secondaryCriterion1 = mock(Criterion.class);
        List<CampaignEnrollment> secondaryCriterion1FilteredEnrollments = mock(List.class);
        when(secondaryCriterion1.filter(primaryCriterionFilteredEnrollments)).thenReturn(secondaryCriterion1FilteredEnrollments);

        Criterion secondaryCriterion2 = mock(Criterion.class);
        List<CampaignEnrollment> expectedFilteredEnrollments = mock(List.class);
        when(secondaryCriterion2.filter(secondaryCriterion1FilteredEnrollments)).thenReturn(expectedFilteredEnrollments);

        CampaignEnrollmentsQuery enrollmentQuery = mock(CampaignEnrollmentsQuery.class);
        when(enrollmentQuery.getPrimaryCriterion()).thenReturn(primaryCriterion);
        when(enrollmentQuery.getSecondaryCriteria()).thenReturn(asList(secondaryCriterion1, secondaryCriterion2));

        Assert.assertEquals(expectedFilteredEnrollments, service.search(enrollmentQuery));
    }
}

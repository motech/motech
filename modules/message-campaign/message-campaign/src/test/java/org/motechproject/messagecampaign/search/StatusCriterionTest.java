package org.motechproject.messagecampaign.search;

import org.junit.Test;
import org.motechproject.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.messagecampaign.search.StatusCriterion;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class StatusCriterionTest {

    @Test
    public void shouldFilterByStatus() {
        List<CampaignEnrollment> allCampaignEnrollments = new ArrayList<CampaignEnrollment>();
        allCampaignEnrollments.add(new CampaignEnrollment(null, null));
        allCampaignEnrollments.add(new CampaignEnrollment(null, null));

        CampaignEnrollment inactiveCampaignEnrollment = new CampaignEnrollment(null, null);
        inactiveCampaignEnrollment.setStatus(CampaignEnrollmentStatus.INACTIVE);
        allCampaignEnrollments.add(inactiveCampaignEnrollment);

        CampaignEnrollment completedCampaignEnrollment = new CampaignEnrollment(null, null);
        completedCampaignEnrollment.setStatus(CampaignEnrollmentStatus.COMPLETED);
        allCampaignEnrollments.add(completedCampaignEnrollment);

        List<CampaignEnrollment> filteredCampaignEnrollments = new StatusCriterion(CampaignEnrollmentStatus.ACTIVE).filter(allCampaignEnrollments);
        assertEquals(asList(new CampaignEnrollmentStatus[]{ CampaignEnrollmentStatus.ACTIVE, CampaignEnrollmentStatus.ACTIVE}), extract(filteredCampaignEnrollments, on(CampaignEnrollment.class).getStatus()));
    }

    @Test
    public void shouldFetchByCampaignEnrollmentStatusFromDb() {
        List<CampaignEnrollment> campaignEnrollments = mock(List.class);
        AllCampaignEnrollments allCampaignEnrollments = mock(AllCampaignEnrollments.class);

        when(allCampaignEnrollments.findByStatus(CampaignEnrollmentStatus.ACTIVE)).thenReturn(campaignEnrollments);

        assertEquals(campaignEnrollments, new StatusCriterion(CampaignEnrollmentStatus.ACTIVE).fetch(allCampaignEnrollments));
    }

}

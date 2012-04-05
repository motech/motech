package org.motechproject.server.messagecampaign.search;

import org.junit.Test;
import org.motechproject.server.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ExternalIdCriterionTest {

    @Test
    public void shouldFilterByExternalId() {
        List<CampaignEnrollment> allCampaignEnrollments = new ArrayList<CampaignEnrollment>();
        allCampaignEnrollments.add(new CampaignEnrollment("externalId1", null));
        allCampaignEnrollments.add(new CampaignEnrollment("externalId2", null));
        allCampaignEnrollments.add(new CampaignEnrollment("externalId3", null));

        List<CampaignEnrollment> filteredCampaignEnrollments = new ExternalIdCriterion("externalId1").filter(allCampaignEnrollments);
        assertEquals(asList(new String[]{ "externalId1"}), extract(filteredCampaignEnrollments, on(CampaignEnrollment.class).getExternalId()));
    }

    @Test
    public void shouldFetchByExternalIdFromDb() {
        List<CampaignEnrollment> campaignEnrollments = mock(List.class);
        AllCampaignEnrollments allCampaignEnrollments = mock(AllCampaignEnrollments.class);

        when(allCampaignEnrollments.findByExternalId("externalId")).thenReturn(campaignEnrollments);

        assertEquals(campaignEnrollments, new ExternalIdCriterion("externalId").fetch(allCampaignEnrollments));
    }

}

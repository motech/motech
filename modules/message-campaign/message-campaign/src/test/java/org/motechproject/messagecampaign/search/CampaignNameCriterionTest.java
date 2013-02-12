package org.motechproject.messagecampaign.search;

import org.junit.Test;
import org.motechproject.messagecampaign.dao.AllCampaignEnrollments;
import org.motechproject.messagecampaign.search.CampaignNameCriterion;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;

import java.util.ArrayList;
import java.util.List;

import static ch.lambdaj.Lambda.extract;
import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CampaignNameCriterionTest {

    @Test
    public void shouldFilterByCampaignName() {
        List<CampaignEnrollment> allCampaignEnrollments = new ArrayList<CampaignEnrollment>();
        allCampaignEnrollments.add(new CampaignEnrollment("externalId1", "campaign1"));
        allCampaignEnrollments.add(new CampaignEnrollment("externalId1", "campaign2"));
        allCampaignEnrollments.add(new CampaignEnrollment("externalId1", "campaign1"));

        List<CampaignEnrollment> filteredCampaignEnrollments = new CampaignNameCriterion("campaign1").filter(allCampaignEnrollments);
        assertEquals(asList(new String[]{ "campaign1", "campaign1"}), extract(filteredCampaignEnrollments, on(CampaignEnrollment.class).getCampaignName()));
    }

    @Test
    public void shouldFetchByCampaignNameFromDb() {
        List<CampaignEnrollment> campaignEnrollments = mock(List.class);
        AllCampaignEnrollments allCampaignEnrollments = mock(AllCampaignEnrollments.class);

        when(allCampaignEnrollments.findByCampaignName("campaign1")).thenReturn(campaignEnrollments);

        assertEquals(campaignEnrollments, new CampaignNameCriterion("campaign1").fetch(allCampaignEnrollments));
    }

}

package org.motechproject.messagecampaign.service;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.messagecampaign.search.CampaignNameCriterion;
import org.motechproject.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.messagecampaign.search.Criterion;
import org.motechproject.messagecampaign.search.ExternalIdCriterion;
import org.motechproject.messagecampaign.search.StatusCriterion;

import java.util.List;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertTrue;

public class CampaignEnrollmentsQueryTest {
    private CampaignEnrollmentsQuery enrollmentsQuery;

    @Before
    public void setUp() {
        enrollmentsQuery = new CampaignEnrollmentsQuery();
    }

    @Test
    public void shouldVerifyHavingStateQuery() {
        CampaignEnrollmentsQuery query = enrollmentsQuery.havingState(CampaignEnrollmentStatus.ACTIVE);
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof StatusCriterion);
    }

    @Test
    public void shouldVerifyWithExternalIdQuery() {
        CampaignEnrollmentsQuery query = enrollmentsQuery.withExternalId("externalId");
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof ExternalIdCriterion);
    }

    @Test
    public void shouldVerifyWithCampaignNameQuery() {
        CampaignEnrollmentsQuery query = enrollmentsQuery.withCampaignName("campaign1");
        List<Criterion> criteria = query.getCriteria();
        assertEquals(criteria.size(), 1);
        assertTrue(criteria.get(0) instanceof CampaignNameCriterion);
    }
}

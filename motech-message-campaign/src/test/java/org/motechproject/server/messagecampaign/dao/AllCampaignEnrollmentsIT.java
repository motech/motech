package org.motechproject.server.messagecampaign.dao;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertEquals;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:testMessageCampaignApplicationContext.xml")
public class AllCampaignEnrollmentsIT {
    @Autowired
    private AllCampaignEnrollments allCampaignEnrollments;

    String externalId = "9500012332";
    String campaignName = "REPEATING_CHILDCARE";

    @Before
    public void setUp() {
    }

    @Test
    public void shouldFindByExternalIdAndCampaignName() {
        CampaignEnrollment enrollment = new CampaignEnrollment(externalId, campaignName).setStartDate(DateUtil.newDate(2011, 11, 1)).setStartOffset(1);
        allCampaignEnrollments.add(enrollment);

        CampaignEnrollment actualEnrollment = allCampaignEnrollments.findByExternalIdAndCampaignName(externalId, campaignName);
        assertThat(enrollment.getId(), is(actualEnrollment.getId()));
        assertThat(enrollment.getStartDate(), is(actualEnrollment.getStartDate()));
    }
    
    @Test
    public void shouldCreateNewEnrollmentIfSavedForFirstTime() {
        allCampaignEnrollments = spy(allCampaignEnrollments);

        CampaignEnrollment enrollment = new CampaignEnrollment(externalId, campaignName).setStartDate(DateUtil.newDate(2011, 11, 1)).setStartOffset(1);
        allCampaignEnrollments.saveOrUpdate(enrollment);
        verify(allCampaignEnrollments).add(any(CampaignEnrollment.class));

        enrollment = allCampaignEnrollments.findByExternalIdAndCampaignName(externalId, campaignName);
        allCampaignEnrollments.saveOrUpdate(new CampaignEnrollment(externalId, campaignName).setStartDate(DateUtil.newDate(2011, 11, 1)).setStartOffset(1));
        verify(allCampaignEnrollments).update(any(CampaignEnrollment.class));

        assertEquals(1, allCampaignEnrollments.getAll().size());
    }

	@After
	public void tearDown() {
		allCampaignEnrollments.removeAll();
	}

}

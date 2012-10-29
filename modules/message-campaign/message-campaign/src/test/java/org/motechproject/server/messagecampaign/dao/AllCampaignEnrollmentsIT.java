package org.motechproject.server.messagecampaign.dao;

import ch.lambdaj.Lambda;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static ch.lambdaj.Lambda.on;
import static java.util.Arrays.asList;
import static junit.framework.Assert.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
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
        allCampaignEnrollments.removeAll();
    }

    @Test
    public void shouldFindByExternalIdAndCampaignName() {
        CampaignEnrollment enrollment = new CampaignEnrollment(externalId, campaignName).setReferenceDate(DateUtil.newDate(2011, 11, 1));
        allCampaignEnrollments.add(enrollment);

        CampaignEnrollment actualEnrollment = allCampaignEnrollments.findByExternalIdAndCampaignName(externalId, campaignName);
        assertThat(enrollment.getId(), is(actualEnrollment.getId()));
        assertThat(enrollment.getReferenceDate(), is(actualEnrollment.getReferenceDate()));
    }
    
    @Test
    public void shouldCreateNewEnrollmentIfSavedForFirstTime() {
        allCampaignEnrollments = spy(allCampaignEnrollments);

        CampaignEnrollment enrollment = new CampaignEnrollment(externalId, campaignName).setReferenceDate(DateUtil.newDate(2011, 11, 1));
        allCampaignEnrollments.saveOrUpdate(enrollment);
        verify(allCampaignEnrollments).add(any(CampaignEnrollment.class));

        enrollment = allCampaignEnrollments.findByExternalIdAndCampaignName(externalId, campaignName);
        allCampaignEnrollments.saveOrUpdate(new CampaignEnrollment(externalId, campaignName).setReferenceDate(DateUtil.newDate(2011, 11, 1)));
        verify(allCampaignEnrollments).update(any(CampaignEnrollment.class));

        assertEquals(1, allCampaignEnrollments.getAll().size());
    }

    @Test
    public void shouldReturnEnrollmentsThatMatchGivenStatus() {
        CampaignEnrollment activeEnrollment1 = new CampaignEnrollment("active_external_id_1", campaignName);
        allCampaignEnrollments.add(activeEnrollment1);

        CampaignEnrollment activeEnrollment2 = new CampaignEnrollment("active_external_id_2", campaignName);
        allCampaignEnrollments.add(activeEnrollment2);

        CampaignEnrollment inActiveEnrollment = new CampaignEnrollment("some_external_id", campaignName);
        inActiveEnrollment.setStatus(CampaignEnrollmentStatus.INACTIVE);
        allCampaignEnrollments.add(inActiveEnrollment);

        CampaignEnrollment completedEnrollment = new CampaignEnrollment("some_other_external_id", campaignName);
        completedEnrollment.setStatus(CampaignEnrollmentStatus.COMPLETED);
        allCampaignEnrollments.add(completedEnrollment);

        List<CampaignEnrollment> filteredEnrollments = allCampaignEnrollments.findByStatus(CampaignEnrollmentStatus.ACTIVE);
        assertEquals(asList(new String[]{"active_external_id_1", "active_external_id_2"}), Lambda.extract(filteredEnrollments, on(CampaignEnrollment.class).getExternalId()));

        filteredEnrollments = allCampaignEnrollments.findByStatus(CampaignEnrollmentStatus.INACTIVE);
        assertEquals(asList(new String[] { "some_external_id"}), Lambda.extract(filteredEnrollments, on(CampaignEnrollment.class).getExternalId()));
    }

    @Test
    public void shouldFindByExternalId() {
        allCampaignEnrollments.add(new CampaignEnrollment(externalId, campaignName));
        allCampaignEnrollments.add(new CampaignEnrollment(externalId, "different_campaign"));
        allCampaignEnrollments.add(new CampaignEnrollment("some_other_external_id", campaignName));

        List<CampaignEnrollment> filteredEnrollments = allCampaignEnrollments.findByExternalId(externalId);
        assertEquals(asList(new String[]{externalId, externalId}), Lambda.extract(filteredEnrollments, on(CampaignEnrollment.class).getExternalId()));
    }

    @Test
    public void shouldFindByCampaignName() {
        allCampaignEnrollments.add(new CampaignEnrollment(externalId, campaignName));
        allCampaignEnrollments.add(new CampaignEnrollment(externalId, "different_campaign"));
        allCampaignEnrollments.add(new CampaignEnrollment("some_other_external_id", campaignName));

        List<CampaignEnrollment> filteredEnrollments = allCampaignEnrollments.findByCampaignName(campaignName);
        assertEquals(asList(new String[]{campaignName, campaignName}), Lambda.extract(filteredEnrollments, on(CampaignEnrollment.class).getCampaignName()));
    }

    @After
    public void tearDown() {
        allCampaignEnrollments.removeAll();
    }
}

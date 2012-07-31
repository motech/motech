package org.motechproject.server.messagecampaign.domain.campaign;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class CampaignEnrollmentTest {

    @Test
    public void shouldSetStatusAsActiveOnCreatingEnrollment() {
        CampaignEnrollment enrollment = new CampaignEnrollment("123", "Campaign name");
        assertEquals(CampaignEnrollmentStatus.ACTIVE, enrollment.getStatus());
    }
}

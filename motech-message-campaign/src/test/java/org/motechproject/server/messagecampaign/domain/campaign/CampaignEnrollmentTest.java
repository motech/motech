package org.motechproject.server.messagecampaign.domain.campaign;

import org.junit.Test;
import org.motechproject.server.messagecampaign.domain.message.RepeatingCampaignMessage;

import static junit.framework.Assert.assertEquals;

public class CampaignEnrollmentTest {

    @Test
    public void shouldReturnStartOffsetAs1_ForRepeatCampaignInternal() {
        CampaignEnrollment enrollment = new CampaignEnrollment("123", "Campaign name");
        RepeatingCampaignMessage message = new RepeatingCampaignMessage("9 weeks", "10:30");
        int startOffset = enrollment.startOffset(message);
        assertEquals(1, startOffset);
    }
}

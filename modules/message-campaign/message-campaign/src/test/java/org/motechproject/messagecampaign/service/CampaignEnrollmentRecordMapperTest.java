package org.motechproject.messagecampaign.service;

import org.joda.time.LocalDate;
import org.junit.Before;
import org.junit.Test;
import org.motechproject.messagecampaign.service.CampaignEnrollmentRecord;
import org.motechproject.messagecampaign.domain.campaign.CampaignEnrollment;
import org.motechproject.messagecampaign.service.CampaignEnrollmentRecordMapper;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

public class CampaignEnrollmentRecordMapperTest {

    private CampaignEnrollmentRecordMapper campaignEnrollmentRecordMapper;

    @Before
    public void setup() {
        campaignEnrollmentRecordMapper = new CampaignEnrollmentRecordMapper();
    }

    @Test
    public void shouldMapEnrollmentToEnrollmentResponse(){
        CampaignEnrollment enrollment = new CampaignEnrollment("externalId", "campaign1");
        enrollment.setReferenceDate(new LocalDate(2012, 12, 24));
        CampaignEnrollmentRecord record = campaignEnrollmentRecordMapper.map(enrollment);

        assertRecordMatchesEnrollment(record, enrollment);

        assertThat(new CampaignEnrollmentRecordMapper().map(null), is(equalTo(null)));
    }

    private void assertRecordMatchesEnrollment(CampaignEnrollmentRecord actualRecord, CampaignEnrollment expectedEnrollment) {
        assertThat(actualRecord.getExternalId(), is(equalTo(expectedEnrollment.getExternalId())));
        assertThat(actualRecord.getCampaignName(), is(equalTo(expectedEnrollment.getCampaignName())));
        assertThat(actualRecord.getReferenceDate(), is(equalTo(expectedEnrollment.getReferenceDate())));
        assertThat(actualRecord.getStatus(), is(equalTo(expectedEnrollment.getStatus())));
    }
}

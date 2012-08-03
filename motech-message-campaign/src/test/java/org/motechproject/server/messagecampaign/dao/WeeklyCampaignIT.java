package org.motechproject.server.messagecampaign.dao;

import org.ektorp.CouchDbConnector;
import org.junit.Test;
import org.motechproject.model.Time;
import org.motechproject.server.messagecampaign.builder.CampaignRecordBuilder;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignEnrollmentStatus;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentRecord;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.testing.utils.SpringIntegrationTest;
import org.motechproject.util.DateUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;

import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNotNull;

@ContextConfiguration("/testMessageCampaignApplicationContext.xml")
public class WeeklyCampaignIT extends SpringIntegrationTest {
    @Autowired
    @Qualifier("messageCampaignDBConnector")
    private CouchDbConnector connector;

    @Autowired
    MessageCampaignService messageCampaignService;

    @Test
    public void shouldEnrollToWeeklyMessageCampaign() throws Exception {
        assertNotNull(messageCampaignService);
        final CampaignRequest enrollRequest = new CampaignRequest();
        enrollRequest.setCampaignName("Simple Repeating Campaign");
        enrollRequest.setExternalId("extId");
        enrollRequest.setReferenceDate(DateUtil.today());
        enrollRequest.setStartTime(new Time(10,10));
        messageCampaignService.startFor(enrollRequest);

        messageCampaignService.stopAll(enrollRequest);

        Date end = DateUtil.now().plusDays(30).toDate();
        final Map<String,List<Date>> timings = messageCampaignService.getCampaignTimings("extId", "Simple Repeating Campaign", new Date(), end);
        for(String key:timings.keySet()){
            System.out.println(key + " " + timings.get(key));
        }
        final List<CampaignEnrollmentRecord> search = messageCampaignService.search(new CampaignEnrollmentsQuery().havingState(CampaignEnrollmentStatus.ACTIVE));
        for (CampaignEnrollmentRecord enrollmentRecord : search) {
            System.out.println(enrollmentRecord.getCampaignName() + " " + enrollmentRecord.getReferenceDate() + " " + enrollmentRecord.getStatus());
        }
    }

    @Override
    public CouchDbConnector getDBConnector() {
        return connector;
    }
}

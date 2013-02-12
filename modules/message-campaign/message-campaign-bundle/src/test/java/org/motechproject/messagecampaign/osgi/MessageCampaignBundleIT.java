package org.motechproject.messagecampaign.osgi;

import org.joda.time.LocalDate;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentRecord;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.List;
import java.util.UUID;

public class MessageCampaignBundleIT extends BaseOsgiIT {

    public void testMessageCampaignService() {
        ServiceReference serviceReference = bundleContext.getServiceReference(MessageCampaignService.class.getName());
        assertNotNull(serviceReference);

        MessageCampaignService messageCampaignService = (MessageCampaignService) getApplicationContext().getBean("messageCampaignServiceRef");
        assertNotNull(messageCampaignService);

        messageCampaignService = (MessageCampaignService) bundleContext.getService(serviceReference);
        assertNotNull(messageCampaignService);

        String externalId = "MessageCampaignBundleIT-" + UUID.randomUUID();
        CampaignRequest campaignRequest = new CampaignRequest(externalId, "PREGNANCY", new LocalDate(2020, 7, 10), null, null);

        try {
            messageCampaignService.startFor(campaignRequest);
            List<CampaignEnrollmentRecord> campaignEnrollmentRecords = messageCampaignService.search(new CampaignEnrollmentsQuery().withExternalId(externalId));
            assertTrue(campaignEnrollmentRecords.size() == 1);
        } finally {
            messageCampaignService.stopAll(campaignRequest); // Doesn't delete the doc
        }
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testMessageCampaignApiBundleContext.xml"};
    }
}

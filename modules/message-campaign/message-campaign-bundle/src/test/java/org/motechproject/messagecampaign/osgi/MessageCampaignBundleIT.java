package org.motechproject.messagecampaign.osgi;

import org.apache.commons.httpclient.HttpStatus;
import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.joda.time.LocalDate;
import org.motechproject.security.service.MotechUserService;
import org.motechproject.server.messagecampaign.contract.CampaignRequest;
import org.motechproject.server.messagecampaign.domain.campaign.CampaignType;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentRecord;
import org.motechproject.server.messagecampaign.service.CampaignEnrollmentsQuery;
import org.motechproject.server.messagecampaign.service.MessageCampaignService;
import org.motechproject.server.messagecampaign.userspecified.CampaignRecord;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static java.util.Arrays.asList;
import static org.apache.commons.codec.binary.Base64.encodeBase64String;

public class MessageCampaignBundleIT extends BaseOsgiIT {

    public void testMessageCampaignService() {
        ServiceReference serviceReference = bundleContext.getServiceReference(MessageCampaignService.class.getName());
        assertNotNull(serviceReference);

        MessageCampaignService messageCampaignService = (MessageCampaignService) getApplicationContext().getBean("messageCampaignServiceRef");
        assertNotNull(messageCampaignService);

        messageCampaignService = (MessageCampaignService) bundleContext.getService(serviceReference);
        assertNotNull(messageCampaignService);

        CampaignRecord campaign = new CampaignRecord();
        campaign.setName("PREGNANCY");
        campaign.setCampaignType(CampaignType.ABSOLUTE);

        messageCampaignService.saveCampaign(campaign);

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

    public void testControllersAnonymous() throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpGet request = new HttpGet("http://localhost:8080/messagecampaign/web-api/campaigns");
        HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());

        EntityUtils.consume(response.getEntity());

        request = new HttpGet("http://localhost:8080/messagecampaign/web-api/enrollments/users");
        response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    public void testControllersUnauthenticated() throws IOException {
        DefaultHttpClient httpClient = new DefaultHttpClient();
        httpClient.getCredentialsProvider()
                .setCredentials(AuthScope.ANY, new UsernamePasswordCredentials("mal", "icious"));

        HttpGet request = new HttpGet("http://localhost:8080/messagecampaign/web-api/campaigns");
        HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());

        EntityUtils.consume(response.getEntity());

        request = new HttpGet("http://localhost:8080/messagecampaign/web-api/enrollments/users");
        response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_UNAUTHORIZED, response.getStatusLine().getStatusCode());
    }

    public void testControllersAsUnathorizedUser() throws IOException {
        ServiceReference motechUserServiceRef = bundleContext.getServiceReference(MotechUserService.class.getName());
        MotechUserService motechUserService = (MotechUserService) bundleContext.getService(motechUserServiceRef);
        motechUserService.register("user-mc-noauth", "pass", "testmcnoauth@test.com", null, asList("Admin User"));

        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpGet request = new HttpGet("http://localhost:8080/messagecampaign/web-api/enrollments/users");
        request.setHeader("Authorization", "Basic " + encodeBase64String("user-mc-noauth:pass".getBytes("UTF-8")).trim());
        HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatusLine().getStatusCode());

        EntityUtils.consume(response.getEntity());

        request = new HttpGet("http://localhost:8080/messagecampaign/web-api/campaigns");
        request.setHeader("Authorization", "Basic " + encodeBase64String("user-mc-noauth:pass".getBytes("UTF-8")).trim());
        response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_FORBIDDEN, response.getStatusLine().getStatusCode());
    }

    public void testControllersAsAuthorizedUser() throws InterruptedException, IOException {
        ServiceReference motechUserServiceRef = bundleContext.getServiceReference(MotechUserService.class.getName());
        MotechUserService motechUserService = (MotechUserService) bundleContext.getService(motechUserServiceRef);
        motechUserService.register("user-mc-auth", "pass", "testmcauth@test.com", "test", asList("Message-campaign Bundle"));

        DefaultHttpClient httpClient = new DefaultHttpClient();

        HttpGet request = new HttpGet("http://localhost:8080/messagecampaign/web-api/campaigns");
        request.addHeader("Authorization", "Basic " + encodeBase64String("user-mc-auth:pass".getBytes("UTF-8")).trim());
        HttpResponse response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());

        EntityUtils.consume(response.getEntity());

        request = new HttpGet("http://localhost:8080/messagecampaign/web-api/enrollments/users");
        request.addHeader("Authorization", "Basic " + encodeBase64String("user-mc-auth:pass".getBytes("UTF-8")).trim());
        response = httpClient.execute(request);
        assertEquals(HttpStatus.SC_OK, response.getStatusLine().getStatusCode());
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testMessageCampaignApiBundleContext.xml"};
    }
}

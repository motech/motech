package org.motechproject.cmslite.api.osgi;


import org.apache.http.impl.client.BasicResponseHandler;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.PollingHttpClient;

import java.io.IOException;

public class CMSLiteBundleIT extends BaseOsgiIT {


    public void testCMSLiteApiBundle() throws CMSLiteException, ContentNotFoundException, IOException, InterruptedException {
        assertNotNull(bundleContext.getServiceReference("org.motechproject.event.listener.EventListenerRegistryService"));
        assertNotNull(bundleContext.getServiceReference("org.motechproject.server.config.SettingsFacade"));
        assertNotNull(bundleContext.getServiceReference("org.motechproject.server.config.service.PlatformSettingsService"));
        assertNotNull(bundleContext.getServiceReference("org.motechproject.cmslite.api.service.CMSLiteService"));
        final CMSLiteService cmsLiteService = (CMSLiteService) getApplicationContext().getBean("cmsLiteServiceRef");
        assertNotNull(cmsLiteService);

        cmsLiteService.addContent(new StringContent("en", "title", "Test content"));

        final StringContent content = cmsLiteService.getStringContent("en", "title");
        assertEquals("Test content", content.getValue());

        PollingHttpClient httpClient = new PollingHttpClient();
        String response = httpClient.get("http://localhost:8080/cmsliteapi/string/en/title", new BasicResponseHandler());

        assertEquals("Test content", response);
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testCmsliteApiBundleContext.xml"};
    }
}
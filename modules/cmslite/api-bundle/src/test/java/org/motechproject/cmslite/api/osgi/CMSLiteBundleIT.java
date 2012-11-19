package org.motechproject.cmslite.api.osgi;


import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.eclipse.gemini.blueprint.test.platform.Platforms;
import org.motechproject.cmslite.api.model.CMSLiteException;
import org.motechproject.cmslite.api.model.ContentNotFoundException;
import org.motechproject.cmslite.api.model.StringContent;
import org.motechproject.cmslite.api.service.CMSLiteService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.io.IOException;

public class CMSLiteBundleIT extends BaseOsgiIT{

    @Override
    protected String getPlatformName() {
        return Platforms.FELIX;
    }


    public void testCMSLiteService() throws InterruptedException, CMSLiteException, ContentNotFoundException, IOException {
        assertNotNull(bundleContext.getServiceReference("org.motechproject.event.listener.EventListenerRegistryService"));
        assertNotNull(bundleContext.getServiceReference("org.motechproject.server.config.SettingsFacade"));
        assertNotNull(bundleContext.getServiceReference("org.motechproject.server.config.service.PlatformSettingsService"));
        assertNotNull(bundleContext.getServiceReference("org.motechproject.cmslite.api.service.CMSLiteService"));
        final CMSLiteService cmsLiteService = (CMSLiteService)getApplicationContext().getBean("cmsLiteServiceRef");
        assertNotNull(cmsLiteService);

        cmsLiteService.addContent(new StringContent("en", "title", "Test content"));

        final StringContent content = cmsLiteService.getStringContent("en", "title");
        assertEquals("Test content", content.getValue());

        HttpClient client = new DefaultHttpClient();
        final String response = client.execute(new HttpGet("http://localhost:8080/cmsliteapi/string/en/title"), new BasicResponseHandler());
        assertEquals("Test content", response);
    }

    @Override
    protected String[] getConfigLocations() {
        return new String[] { "/META-INF/spring/testCmsliteApiBundleContext.xml" };
    }
}
package org.motechproject.sms.http.osgi;

import org.motechproject.event.listener.EventListenerRegistryService;
import org.motechproject.event.listener.EventRelay;
import org.motechproject.server.config.service.PlatformSettingsService;
import org.motechproject.sms.api.service.SmsAuditService;
import org.motechproject.sms.api.service.SmsService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.motechproject.testing.utils.Wait;
import org.motechproject.testing.utils.WaitCondition;
import org.motechproject.testing.utils.server.RequestInfo;
import org.motechproject.testing.utils.server.StubServer;
import org.osgi.framework.ServiceReference;
import org.springframework.core.io.ClassPathResource;

import java.io.InputStream;


public class SmsHttpServiceBundleIT extends BaseOsgiIT {

    public static final int PORT = 8282;
    public static final int MAX_WAIT_TIME = 2000;
    public static final int WAIT_DURATION_BETWEEN_CHECKS = 10;
    private StubServer stubServer;


    @Override
    protected void onSetUp() throws Exception {
        stubServer = new StubServer(PORT, "/sms").start();
    }

    public void testThatSMSShouldBeSentToSMSGateway() throws Exception {

        assertNotNull(bundleContext.getServiceReference(SmsAuditService.class.getName()));
        assertNotNull(bundleContext.getServiceReference(EventRelay.class.getName()));
        assertNotNull(bundleContext.getServiceReference(EventListenerRegistryService.class.getName()));

        ServiceReference platformSettingsRef = bundleContext.getServiceReference(PlatformSettingsService.class.getName());
        assertNotNull(platformSettingsRef);

        PlatformSettingsService platformSettingsService = (PlatformSettingsService) bundleContext.getService(platformSettingsRef);

        InputStream inputStream = new ClassPathResource("sms-http-template.json").getInputStream();
        platformSettingsService.saveRawConfig("org.motechproject.motech-sms-http-bundle","sms-http-template.json", inputStream);


        ServiceReference smsServiceRef = bundleContext.getServiceReference(SmsService.class.getName());
        assertNotNull(smsServiceRef);

        SmsService smsService = (SmsService) bundleContext.getService(smsServiceRef);
        assertNotNull(smsService);

        smsService.sendSMS("9999", "Hello");

        new Wait(stubServer, new WaitCondition() {
            @Override
            public boolean needsToWait() {
                return stubServer.waitingForRequests();
            }
        }, WAIT_DURATION_BETWEEN_CHECKS, MAX_WAIT_TIME).start();

        RequestInfo requestInfo = stubServer.detailForRequest("/sms");

        assertNotNull(requestInfo);
        assertEquals("/sms", requestInfo.getContextPath());
        assertEquals("Hello", requestInfo.getQueryParam("message"));
        assertEquals("9999", requestInfo.getQueryParam("recipients"));
    }


    @Override
    protected void onTearDown() throws Exception {
        stubServer.stop();
    }


}



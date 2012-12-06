package org.motechproject.sms.http.osgi;

import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.osgi.web.MotechOsgiWebApplicationContext;
import org.motechproject.server.ui.ModuleRegistrationData;
import org.motechproject.server.ui.UIFrameworkService;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.service.http.HttpService;
import org.osgi.util.tracker.ServiceTracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;

public class Activator implements BundleActivator {
    private static final String CONTEXT_CONFIG_LOCATION = "applicationSmsHttpBundle.xml";
    private static final String SERVLET_URL_MAPPING = "/smshttp/api";
    private static final String MODULE_NAME = "smshttp";
    private static final String RESOURCE_URL_MAPPING = "/smshttp";
    private ServiceTracker httpServiceTracker;

    private static BundleContext bundleContext;
    private UIServiceTracker uiServiceTracker;

    @Override
    public void start(BundleContext context) {
        bundleContext = context;

        this.httpServiceTracker = new HttpServiceTracker(context, HttpService.class.getName(), null,
                new ServletDefinition(CONTEXT_CONFIG_LOCATION, SERVLET_URL_MAPPING, SmsHttpApplicationContext.class, RESOURCE_URL_MAPPING));
        this.uiServiceTracker = new UIServiceTracker(context, UIFrameworkService.class.getName(), null,moduleRegistrationData());

        this.httpServiceTracker.open();
        this.uiServiceTracker.open();
    }

    public void stop(BundleContext context) {
        this.httpServiceTracker.close();
        this.uiServiceTracker.close();
    }

    public static class SmsHttpApplicationContext extends MotechOsgiWebApplicationContext {

        public SmsHttpApplicationContext() {
            super();
            setBundleContext(Activator.bundleContext);
        }

    }

    private ModuleRegistrationData moduleRegistrationData() {
        ModuleRegistrationData regData = new ModuleRegistrationData();
        regData.setModuleName(MODULE_NAME);
        regData.setUrl("../smshttp/");
        regData.addAngularModule("motech-smshttp");

        regData.addI18N("messages", "../smshttp/bundles/");

        InputStream is = null;
        StringWriter writer = new StringWriter();
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("header.html");
            IOUtils.copy(is, writer);

            regData.setHeader(writer.toString());
        } catch (IOException e) {
            throw new MotechException("Cant read header.html", e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(writer);
        }
        return regData;
    }


}


package org.motechproject.event.aggregation.osgi;

import org.apache.commons.io.IOUtils;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIFrameworkService;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class Activator extends org.motechproject.osgi.web.Activator {

    private static Logger logger = LoggerFactory.getLogger(Activator.class);

    private ServiceTracker uiServiceTracker;

    private static final String MODULE_NAME = "event-aggregation";
    private static final String RESOURCE_URL_MAPPING = "/event-aggregation";

    @Override
    public void start(BundleContext context) {
        super.start(context);

        this.uiServiceTracker = new ServiceTracker(context,
            UIFrameworkService.class.getName(), null) {

            @Override
            public Object addingService(ServiceReference ref) {
                Object service = super.addingService(ref);
                serviceAdded((UIFrameworkService) service);
                return service;
            }

            @Override
            public void removedService(ServiceReference ref, Object service) {
                serviceRemoved((UIFrameworkService) service);
                super.removedService(ref, service);
            }
        };
        this.uiServiceTracker.open();

    }

    @Override
    protected Map<String, String> resourceMappings() {
        final HashMap<String, String> mapping = new HashMap<>();
        mapping.put(RESOURCE_URL_MAPPING, "/webapp");
        return mapping;
    }

    private void serviceAdded(UIFrameworkService service) {
        ModuleRegistrationData regData = new ModuleRegistrationData();
        regData.setModuleName(MODULE_NAME);
        regData.setUrl("../event-aggregation/index.html");
        regData.addAngularModule("motech-event-aggregation");

        regData.addI18N("messages", "../event-aggregation/bundles/");

        InputStream is = null;
        StringWriter writer = new StringWriter();
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("header.html");
            IOUtils.copy(is, writer);

            regData.setHeader(writer.toString());
        } catch (IOException e) {
            logger.error("Cant read header.html", e);
            throw new BundleStartException(e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(writer);
        }
        service.registerModule(regData);
        logger.debug("Event Aggregation registered in UI framework");
    }

    private void serviceRemoved(UIFrameworkService service) {
        service.unregisterModule(MODULE_NAME);
        logger.debug("Event Aggregation unregistered from ui framework");
    }
}

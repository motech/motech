package org.motechproject.osgi.web;

import org.apache.commons.io.IOUtils;
import org.motechproject.osgi.web.util.WebBundleUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

public class WebUIBundleActivator extends org.motechproject.osgi.web.Activator {

    private static final String STATIC_RESOURCE_MAPPING = "/resources";
    private static final String DEFAULT_RESOURCE_FOLDER = "/webapp";
    private static final String DEFAULT_MESSAGES_FOLDER = "/messages/";
    private static final String DEFAULT_HOME_PAGE_FOR_MODULE = "/index.html";

    private static Logger logger = LoggerFactory.getLogger(WebUIBundleActivator.class);

    private BundleContext bundleContext;
    private ServiceTracker uiServiceTracker;

    protected String moduleId() {
       return WebBundleUtil.getModuleId(bundleContext.getBundle());
    }

    protected String resourceFolder() {
       return "/" + WebBundleUtil.getModuleId(bundleContext.getBundle()) + STATIC_RESOURCE_MAPPING;
    }


    @Override
    protected Map<String, String> resourceMappings() {
        final HashMap<String, String> map = new HashMap<>();
        map.put(resourceFolder(), DEFAULT_RESOURCE_FOLDER);
        return map;
    }

    @Override
    public void start(BundleContext context) {
        bundleContext = context;
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

    public void stop(BundleContext context) {
        this.uiServiceTracker.close();
        super.stop(context);
    }

    private void serviceAdded(UIFrameworkService service) {
        String resourceRoot = ".." + resourceFolder();
        ModuleRegistrationData regData = new ModuleRegistrationData();
        regData.setModuleName(moduleId());
        regData.setUrl(resourceRoot + DEFAULT_HOME_PAGE_FOR_MODULE);
        regData.addAngularModule(moduleId());
        regData.addI18N("messages", resourceRoot + DEFAULT_MESSAGES_FOLDER);

        InputStream is = null;
        StringWriter writer = new StringWriter();
        try {
            final URL resource = bundleContext.getBundle().getResource("header.html");
            IOUtils.copy(resource.openStream(), writer);

            regData.setHeader(writer.toString());
        } catch (IOException e) {
            logger.warn("Cant read header.html for " + moduleId());
            throw new HeaderFileMissingException("Cant read header.html for ", e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(writer);
        }

        service.registerModule(regData);
        logger.info("Registered " + moduleId() + " in UI framework");
    }

    private void serviceRemoved(UIFrameworkService service) {
        service.unregisterModule(moduleId());
        logger.info("Unregistered " + moduleId() + " from ui framework");
    }
}


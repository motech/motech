package org.motechproject.admin.osgi;

import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.osgi.web.ModuleRegistrationData;
import org.motechproject.osgi.web.UIServiceTracker;
import org.osgi.framework.BundleContext;
import org.osgi.util.tracker.ServiceTracker;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class Activator extends org.motechproject.osgi.web.Activator {

    private static final String MODULE_NAME = "admin";

    private ServiceTracker uiServiceTracker;

    @Override
    public void start(BundleContext context) {
        super.start(context);
        uiServiceTracker = new UIServiceTracker(context, moduleRegistrationData());
        uiServiceTracker.open();
    }

    @Override
    public void stop(BundleContext context) {
        super.stop(context);
        this.uiServiceTracker.close();
    }

    @Override
    protected Map<String, String> resourceMappings() {
        Map<String, String> mappings = new HashMap<>();
        mappings.put("/admin", "/webapp");
        return mappings;
    }

    private ModuleRegistrationData moduleRegistrationData() {
        ModuleRegistrationData regData = new ModuleRegistrationData();
        regData.setModuleName(MODULE_NAME);
        regData.setUrl("../admin/index.html");
        regData.addAngularModule("motech-admin");

        regData.addSubMenu("#/bundles", "manageModules");
        regData.addSubMenu("#/messages", "messages");
        regData.addSubMenu("#/settings", "settings");
        regData.addSubMenu("#/operations", "operations");
        regData.addSubMenu("#/log", "log");

        regData.addI18N("messages", "../admin/bundles/");

        InputStream is = null;
        StringWriter writer = new StringWriter();
        try {
            is = this.getClass().getClassLoader().getResourceAsStream("header.html");
            IOUtils.copy(is, writer);

            regData.setHeader(writer.toString());

            return regData;
        } catch (IOException e) {
            throw new MotechException("Can't read header", e);
        } finally {
            IOUtils.closeQuietly(is);
            IOUtils.closeQuietly(writer);
        }
    }
}

package org.motechproject.sms.api.osgi;

import org.apache.commons.io.IOUtils;
import org.motechproject.commons.api.MotechException;
import org.motechproject.server.ui.ModuleRegistrationData;
import org.osgi.framework.BundleContext;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

public class Activator extends org.motechproject.osgi.web.Activator {

    private static final String RESOURCE_URL_MAPPING = "/sms/resource";


    private UIServiceTracker uiServiceTracker;

    @Override
    public void start(BundleContext context) {
        super.start(context);
        this.uiServiceTracker = new UIServiceTracker(context, moduleRegistrationData());
        this.uiServiceTracker.open();
    }

    public void stop(BundleContext context) {
        super.stop(context);
        this.uiServiceTracker.close();
    }


    protected Map<String, String> resourceMappings() {
        HashMap<String, String> map = new HashMap<>();
        map.put(RESOURCE_URL_MAPPING, "/webapp");
        return map;
    }

    private ModuleRegistrationData moduleRegistrationData() {
        ModuleRegistrationData regData = new ModuleRegistrationData();
        regData.setModuleName("SMS");
        regData.setUrl("../sms/resource/index.html");
        regData.addAngularModule("motech-sms");

        regData.addI18N("messages", "../sms/resource/bundles/");

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

package org.motechproject.server.osgi.it;

import org.motechproject.server.ui.UIFrameworkService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.List;

public class ServerBundleIT extends BaseOsgiIT {

    public void testStartServer() {
        ServiceReference serviceReference = bundleContext.getServiceReference(UIFrameworkService.class.getName());
        assertNotNull(serviceReference);
        UIFrameworkService service = (UIFrameworkService) bundleContext.getService(serviceReference);
        assertNotNull(service);
    }

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.server.ui");
    }
}

package org.motechproject.config.core.service.osgi;

import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.ServiceReference;

import java.util.Arrays;
import java.util.List;

public class CoreConfigurationBundleIT extends BaseOsgiIT {

    public void testBootstrapConfigBundleIT() {
        ServiceReference registryReference = bundleContext.getServiceReference(CoreConfigurationService.class.getName());
        assertNotNull(registryReference);
        CoreConfigurationService service = (CoreConfigurationService) bundleContext.getService(registryReference);
        assertNotNull(service);

        BootstrapConfig bootstrapConfig = service.loadBootstrapConfig();
        assertNotNull(bootstrapConfig);
        assertNotNull(bootstrapConfig.getDbConfig());
        assertNotNull(bootstrapConfig.getConfigSource());
    }

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.config.core.domain");
    }
}

package org.motechproject.config.core.service.osgi;

import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.service.CoreConfigurationService;
import org.motechproject.testing.osgi.BaseOsgiIT;

import java.util.Arrays;
import java.util.List;

public class CoreConfigurationBundleIT extends BaseOsgiIT {

    public void testBootstrapConfigBundleIT() {
        CoreConfigurationService service = getService(CoreConfigurationService.class);

        BootstrapConfig bootstrapConfig = service.loadBootstrapConfig();
        assertNotNull(bootstrapConfig);
        assertNotNull(bootstrapConfig.getCouchDbConfig());
        assertNotNull(bootstrapConfig.getSqlConfig());
        assertNotNull(bootstrapConfig.getConfigSource());
    }

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.config.core.domain");
    }
}

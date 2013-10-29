package org.motechproject.config.core.service.osgi;

import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
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

        final String url = "http://www.testurl.com";
        final String username = "test_usr";
        final String password = "test_pwd";
        final String tenantId = "test_tenentid";
        final ConfigSource configSource = ConfigSource.FILE;
        final DBConfig dbConfig = new DBConfig(url, username, password);
        BootstrapConfig bootstrapConfig = new BootstrapConfig(dbConfig, tenantId, configSource);
        service.saveBootstrapConfig(bootstrapConfig);

        final BootstrapConfig loadedBootstrapConfig = service.loadBootstrapConfig();
        assertNotNull(loadedBootstrapConfig);
        assertEquals(dbConfig, loadedBootstrapConfig.getDbConfig());
        assertEquals(configSource, loadedBootstrapConfig.getConfigSource());
    }

    @Override
    protected List<String> getImports() {
        return Arrays.asList("org.motechproject.config.core.domain");
    }
}

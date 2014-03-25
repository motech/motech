package org.motechproject.testing.osgi.test;

import org.motechproject.testing.osgi.BaseOsgiIT;

public class BaseOsgiFrameworkIT extends BaseOsgiIT {
    public void testRunOsgiTest() throws Exception {
        assertNotNull(bundleContext.getBundles());
    }
}

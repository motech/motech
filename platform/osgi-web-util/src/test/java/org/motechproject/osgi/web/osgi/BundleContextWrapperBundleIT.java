package org.motechproject.osgi.web.osgi;

import org.motechproject.osgi.web.BundleContextWrapper;
import org.motechproject.testing.osgi.BaseOsgiIT;
import org.osgi.framework.Bundle;
import org.springframework.context.ApplicationContext;

public class BundleContextWrapperBundleIT extends BaseOsgiIT {

    public void testThatBundleContextWrapperReturnsCorrectApplicationContext() {
        BundleContextWrapper bundleContextWrapper = new BundleContextWrapper();
        bundleContextWrapper.setBundleContext(bundleContext);

        Bundle bundle = bundleContext.getBundle();
        assertEquals(bundle.getSymbolicName(),bundleContextWrapper.getCurrentBundleSymbolicName());

        ApplicationContext applicationContextForCurrentBundle = bundleContextWrapper.getBundleApplicationContext();
        assertNotNull(applicationContextForCurrentBundle);

        Object testBundleContextWrapper = applicationContextForCurrentBundle.getBean("testBundleContextWrapper");
        assertNotNull(testBundleContextWrapper);
    }



    @Override
    protected String[] getConfigLocations() {
        return new String[]{"/META-INF/spring/testWebUtilApplicationContext.xml"};
    }
}

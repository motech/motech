package org.motechproject.server.osgi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.osgi.framework.Bundle;
import org.osgi.framework.launch.Framework;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.mock.web.MockServletContext;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.support.GenericWebApplicationContext;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"/testPlatformServerApplicationContext.xml"})
public class OsgiFrameworkServiceTestIT {

    @Autowired
    private OsgiFrameworkService service;

    @Autowired
    private Framework framework;

    @Autowired
    private ApplicationContext applicationContext;

    private List<String> expectedBundles = Arrays.asList("org.apache.felix.framework", "org.apache.felix.http.bridge");

    @Test
    public void startStopTest() throws Exception {
        service.setApplicationContext(getWebApplicationContext());

        assertEquals(Bundle.INSTALLED, framework.getState());

        service.start();
        assertEquals(Bundle.ACTIVE, framework.getState());

        Bundle[] bundles = framework.getBundleContext().getBundles();
        assertEquals(expectedBundles.size(), bundles.length);
        checkBundleState(bundles[0], Bundle.ACTIVE);
        checkBundleState(bundles[1], Bundle.INSTALLED);

        service.launchBundles();
        assertEquals(Bundle.ACTIVE, framework.getState());

        bundles = framework.getBundleContext().getBundles();
        assertEquals(expectedBundles.size(), bundles.length);
        checkBundleState(bundles[0], Bundle.ACTIVE);
        checkBundleState(bundles[1], Bundle.ACTIVE);

        assertNotNull(service.getClassLoaderBySymbolicName("org.apache.felix.http.bridge"));

        service.stop();
        assertEquals(Bundle.STOPPING, framework.getState());
    }

    private void checkBundleState(final Bundle bundle, final int expectedState) {
        assertTrue(String.format("Bundle '%s' was not expected to be found!", bundle.getSymbolicName()), expectedBundles.contains(bundle.getSymbolicName()));
        assertEquals(expectedState, bundle.getState());
    }

    private WebApplicationContext getWebApplicationContext() {
        GenericWebApplicationContext wac = BeanUtils.instantiateClass(GenericWebApplicationContext.class);
        wac.setParent(applicationContext);
        wac.setServletContext(new MockServletContext());
        return wac;
    }
}

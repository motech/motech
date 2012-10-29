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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

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
        for (Bundle bundle : bundles) {
            assertTrue("Bundle '" + bundle.getSymbolicName() + "' was not expected to be found!",
                    expectedBundles.contains(bundle.getSymbolicName()));
            assertEquals(Bundle.ACTIVE, bundle.getState());
        }

        assertNotNull(service.getClassLoaderBySymbolicName(expectedBundles.get(1)));

        service.stop();
        framework.waitForStop(500);
        assertTrue(Bundle.STOPPING == framework.getState() || Bundle.RESOLVED == framework.getState());
    }

    private WebApplicationContext getWebApplicationContext() {
        GenericWebApplicationContext wac = BeanUtils.instantiateClass(GenericWebApplicationContext.class);
        wac.setParent(applicationContext);
        wac.setServletContext(new MockServletContext());
        return wac;
    }
}

package org.motechproject.server.impl;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.motechproject.server.osgi.status.PlatformStatus;
import org.motechproject.server.osgi.status.PlatformStatusManager;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;

import java.util.HashMap;
import java.util.Map;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PlatformStatusProxyTest {

    @Mock
    private PlatformStatusManager statusManager;

    @Mock
    private BundleContext bundleContext;

    @Mock
    private ServiceReference ref;

    private PlatformStatusProxy platformStatusProxy;

    @Before
    public void setUp() {
        platformStatusProxy = new PlatformStatusProxy(bundleContext);
    }

    @Test
    public void shouldReturnEmptyStatusIfManagerUnavailable() {
        PlatformStatus status = platformStatusProxy.getCurrentStatus();

        assertNotNull(status);
        assertNotNull(status.getStartedBundles());
        assertTrue(status.getStartedBundles().isEmpty());
        assertNotNull(status.getBundleErrorsByBundle());
        assertTrue(status.getBundleErrorsByBundle().isEmpty());
        assertNotNull(status.getContextErrorsByBundle());
        assertTrue(status.getContextErrorsByBundle().isEmpty());

    }

    @Test
    public void shouldRetrieveTheStatus() {
        when(bundleContext.getServiceReference(PlatformStatusManager.class.getName())).thenReturn(ref);
        when(bundleContext.getService(ref)).thenReturn(statusManager);
        PlatformStatus statusFromMgr = buildStatus();
        when(statusManager.getCurrentStatus()).thenReturn(statusFromMgr);

        PlatformStatus returnedStatus = platformStatusProxy.getCurrentStatus();

        // a new object should have been created
        assertTrue(statusFromMgr != returnedStatus);
        assertStatus(returnedStatus);
    }

    private PlatformStatus buildStatus() {
        PlatformStatus platformStatus = new PlatformStatus();

        platformStatus.setStartedBundles(asList("b1", "b2"));

        Map<String, String> bundleErrors = new HashMap<>();
        bundleErrors.put("b3", "bundle error");
        platformStatus.setBundleErrorsByBundle(bundleErrors);

        Map<String, String> contextErrors = new HashMap<>();
        contextErrors.put("b4", "ctx error 1");
        contextErrors.put("b5", "ctx error 2");
        platformStatus.setContextErrorsByBundle(contextErrors);

        return platformStatus;
    }

    public void assertStatus(PlatformStatus status) {
        assertNotNull(status);
        assertNotNull(status.getStartedBundles());
        assertEquals(asList("b1", "b2"), status.getStartedBundles());

        Map<String, String> bundleErrors = status.getBundleErrorsByBundle();
        assertNotNull(bundleErrors);
        assertEquals(1, bundleErrors.size());
        assertEquals("bundle error", bundleErrors.get("b3"));

        Map<String, String> ctxErrors = status.getContextErrorsByBundle();
        assertNotNull(ctxErrors);
        assertEquals(2, ctxErrors.size());
        assertEquals("ctx error 1", ctxErrors.get("b4"));
        assertEquals("ctx error 2", ctxErrors.get("b5"));

        // make sure this gets updated during copy
        assertEquals(20, status.getStartupProgressPercentage());
    }
}

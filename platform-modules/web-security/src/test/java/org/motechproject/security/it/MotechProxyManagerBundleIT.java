package org.motechproject.security.it;

import org.junit.Before;
import org.junit.Test;
import org.motechproject.security.service.MotechProxyManager;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

import static junit.framework.Assert.assertTrue;

public class MotechProxyManagerBundleIT extends BaseIT {

    private MotechProxyManager proxyManager;

    @Before
    @Override
    public void setUp() throws Exception {
        super.setUp();

        proxyManager = getFromContext(MotechProxyManager.class);
    }

    @Test
    public void testProxyHasDefaultSecurityChains() {
        FilterChainProxy filterChainProxy = proxyManager.getFilterChainProxy();
        List<SecurityFilterChain> filterChains = filterChainProxy.getFilterChains();

        assertTrue(filterChains.size() > 0);
    }
}

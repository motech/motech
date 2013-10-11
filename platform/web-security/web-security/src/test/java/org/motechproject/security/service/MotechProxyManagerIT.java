package org.motechproject.security.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

import static junit.framework.Assert.assertTrue;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath*:META-INF/motech/*.xml")
public class MotechProxyManagerIT {

    @Autowired
    private MotechProxyManager proxyManager;

    @Test
    public void testProxyHasDefaultSecurityChains() {
        FilterChainProxy filterChainProxy = proxyManager.getFilterChainProxy();
        List<SecurityFilterChain> filterChains = filterChainProxy.getFilterChains();

        assertTrue(filterChains.size() > 0);
    }
}

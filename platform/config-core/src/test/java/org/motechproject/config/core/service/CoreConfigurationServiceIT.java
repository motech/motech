package org.motechproject.config.core.service;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.core.domain.ConfigLocation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static junit.framework.Assert.assertSame;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:/META-INF/motech/*.xml"})
public class CoreConfigurationServiceIT {
    @Autowired
    private CoreConfigurationService coreConfigurationService;

    @Test
    public void shouldCacheConfigLocation() {
        ConfigLocation configLocation = coreConfigurationService.getConfigLocation();

        ConfigLocation cachedConfigLocation = coreConfigurationService.getConfigLocation();

        assertSame(configLocation, cachedConfigLocation);
    }
}

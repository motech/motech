package org.motechproject.config.bootstrap.impl;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertNotNull;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/configContext.xml"})
public class BootstrapconfigManagerIT {

    @Autowired
    private BootstrapConfigManagerImpl bootstrapConfigManager;

    @Test
    public void shouldLoadBootstrapConfigLocationFromPropertiesFile() {
//        assertThat(bootstrapConfigManager.getDefaultBootstrapConfigDir(), IsEqual.equalTo("${user.home}/.motech/config"));
        assertNotNull(bootstrapConfigManager.getDefaultBootstrapConfigDir());
    }
}

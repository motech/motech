package org.motechproject.config.bootstrap.impl;

import org.hamcrest.core.IsEqual;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:META-INF/motech/*.xml"})
public class BootstrapconfigLoaderIT {

    @Autowired
    private BootstrapConfigLoaderImpl bootstrapConfigLoader;

    @Test
    public void shouldLoadBootstrapConfigLocationFromPropertiesFile() {
        assertThat(bootstrapConfigLoader.getDefaultBootstrapConfigDir(), IsEqual.equalTo("/tmp"));
    }
}

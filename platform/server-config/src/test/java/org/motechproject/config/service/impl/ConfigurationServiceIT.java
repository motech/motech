package org.motechproject.config.service.impl;


import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.FileUtils;
import org.hamcrest.core.IsEqual;
import org.junit.After;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.motechproject.config.core.domain.BootstrapConfig;
import org.motechproject.config.core.domain.ConfigSource;
import org.motechproject.config.core.domain.DBConfig;
import org.motechproject.config.service.ConfigurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.File;

import static org.junit.Assert.assertThat;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath*:testApplicationPlatformConfig.xml"})
public class ConfigurationServiceIT {

    private String url = "http://www.testurl.com";
    private String username = "test_usr";
    private String password = "test_pwd";
    private String tenantId = "test_tenentid";
    private ConfigSource configSource = ConfigSource.FILE;

    @Autowired
    private ConfigurationService configurationService;

    @Autowired
    private PropertiesConfiguration propertiesConfiguration;

    @After
    public void tearDown() {
        FileUtils.deleteQuietly(new File(propertiesConfiguration.getString("config.location")));
    }

    @Test
    public void shouldSaveBootstrapConfigToDefaultLocationAndLoadFromTheSameLocation() {
        BootstrapConfig bootstrapConfig = new BootstrapConfig(new DBConfig(url, username, password), tenantId, configSource);
        configurationService.save(bootstrapConfig);

        assertThat(configurationService.loadBootstrapConfig(), IsEqual.equalTo(bootstrapConfig));
    }
}
